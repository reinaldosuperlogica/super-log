package com.superlogica.logs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class SuperLogHttp {

	private static final String HEADER_X_PERSON_ID = "x-person-id";
	private static final String HEADER_X_COMPANY_ID = "x-company-id";

	private SuperLogBrokerPublisher superLogBrokerPublisher;
	
	@Autowired
	public SuperLogHttp(SuperLogBrokerPublisher superLogBrokerPublisher) {
		this.superLogBrokerPublisher = superLogBrokerPublisher;
	}	
	
    @Value("#{new Boolean('${superlog.all.requests}')}")
    private boolean logAllRequests;

    @Value("#{new Boolean('${superlog.requests.avoid.stdout}')}")
    private boolean avoidSendToStdout;

    public void logRequestAndResponse(HttpServletRequest request, HttpServletResponse response, Object body) {

		String fullURL = this.getFullURL(request);
    	
    	if (Boolean.TRUE.equals(logAllRequests) && !StringUtils.containsIgnoreCase(fullURL, "/actuator/")) {
	    	SuperLogHttpMessage superLogHttpMessage = SuperLogHttpMessage.builder()
	    		.request(
	    			SuperLogHttpRequest.builder()
						.method(HttpMethod.resolve(request.getMethod()))
						.url(this.getFullURL(request))
						.headers(this.getRequestHeaders(request))
						.body(
							StringUtils.containsIgnoreCase(fullURL, "/external-logs") ? "***" : this.getRequestBody(request)
						)
						.build()
	    		)
	    		.response(
					SuperLogHttpResponse.builder()
						.httpCode(response.getStatus())
						.headers(this.getResponseHeaders(response))
						.body(
							this.getResponseBody(body)
						)
						.build()
	    		)
	    		.build();
	    		
	    	this.resolveLog(superLogHttpMessage);
    	}
    }
    
    private String getFullURL(HttpServletRequest request) {
        StringBuilder requestURL = new StringBuilder(request.getRequestURL().toString());
        String queryString = request.getQueryString();

        if (queryString == null) {
            return requestURL.toString();
        } else {
            return requestURL.append('?').append(queryString).toString();
        }
    }
    
	private Map<String, String> getRequestHeaders(HttpServletRequest request) {

		Map<String, String> map = new HashMap<>();

		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String key = headerNames.nextElement();
			if (StringUtils.compareIgnoreCase(key, "Authorization") == 0 || 
				StringUtils.containsIgnoreCase(key, "token") ||
				StringUtils.containsIgnoreCase(key, "secret")) {
				map.put(key, "***");
				continue;
			}
			String value = request.getHeader(key);
			map.put(key.toLowerCase(), value);
		}

		return map;
	}
	
	private String getRequestBody(HttpServletRequest request) {
		try {
			StringBuilder sb = new StringBuilder();
			BufferedReader reader = request.getReader();
			String line;
			while ((line = reader.readLine()) != null) {
			  sb.append(line);
			}
			return sb.toString();
		} catch(Exception e) {
			try {
				return request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
			} catch(Exception e2) {
				try {
					return IOUtils.toString(request.getReader());
				} catch(Exception e3) {
					return "";
				}
			}
		}
	}
	
    private String getResponseBody(Object body){
        ObjectMapper objectMapper = new ObjectMapper();
        String responseBody = "";
        try {
        	responseBody = objectMapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {       
        	e.printStackTrace();
            return "";
        }
        return responseBody;
    }
	
	private Map<String, String> getResponseHeaders(HttpServletResponse response) {
        Map<String, String> map = new HashMap<>();
        
        Collection<String> headerNames = response.getHeaderNames();
        headerNames.forEach( header -> map.put(header, response.getHeader(header)));
        
        return map;
	}
	
	private void resolveLog(SuperLogHttpMessage superLogHttpMessage) {
        if (superLogHttpMessage.getResponse().getHttpCode() >= 200 && superLogHttpMessage.getResponse().getHttpCode() < 400) {
        	SuperLog.info(
        		superLogHttpMessage, 
        		this.mountDefaultTags(superLogHttpMessage),
        		superLogBrokerPublisher,
				avoidSendToStdout
        	);
        	return;
        }
        
        if (superLogHttpMessage.getResponse().getHttpCode() >= 400 && superLogHttpMessage.getResponse().getHttpCode() < 500) {
        	SuperLog.warning(
        		superLogHttpMessage, 
        		this.mountDefaultTags(superLogHttpMessage),
        		superLogBrokerPublisher,
				avoidSendToStdout
        	);
        	return;
        }
        
        if (superLogHttpMessage.getResponse().getHttpCode() >= 500) {
        	SuperLog.critical(
        		superLogHttpMessage, 
        		this.mountDefaultTags(superLogHttpMessage),
        		superLogBrokerPublisher,
				avoidSendToStdout
        	);
        }
	}

	private List<String> mountDefaultTags(SuperLogHttpMessage superLogHttpMessage) {
		List<String> tags = new ArrayList<>(Arrays.asList(
			"HTTP_REQUEST",
			Integer.toString(superLogHttpMessage.getResponse().getHttpCode())
		));

		if (superLogHttpMessage.getRequest().getHeaders().containsKey(SuperLogHttp.HEADER_X_PERSON_ID)) {
			tags.add("person_id:" + superLogHttpMessage.getRequest().getHeaders().get(SuperLogHttp.HEADER_X_PERSON_ID));
		}

		if (superLogHttpMessage.getRequest().getHeaders().containsKey(SuperLogHttp.HEADER_X_COMPANY_ID)) {
			tags.add("company_id:" + superLogHttpMessage.getRequest().getHeaders().get(SuperLogHttp.HEADER_X_COMPANY_ID));
		}

		return tags;
	}
}

package com.superlogica.logs;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpMethod;

import java.util.Map;

@Getter
@Setter
@Builder
public class SuperLogHttpRequest {
    private HttpMethod method;
    private String url;
    private Map<String, String> headers;
    private String body;
}

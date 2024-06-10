package com.superlogica.logs;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Builder
public class SuperLogHttpResponse {	
	private Integer httpCode;
	private String body;	
	private Map<String, String> headers;
}

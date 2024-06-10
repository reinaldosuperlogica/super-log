package com.superlogica.logs;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SuperLogHttpMessage implements SuperLogMessage<SuperLogHttpMessage> {
	private SuperLogHttpRequest request;
	private SuperLogHttpResponse response;
	
	public SuperLogHttpMessage(
		SuperLogHttpRequest request,
		SuperLogHttpResponse response
	) {
		this.request = request;
		this.response = response;
	}
	
	@JsonIgnore
	public SuperLogHttpMessage getMessage() {
		return this;
	}
}

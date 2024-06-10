package com.superlogica.logs;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SuperLogEventMessage implements SuperLogMessage<SuperLogEventMessage> {
    private String id;
    private String key;
    private String topic;
    private String event;
    private Object details;
	
	@JsonIgnore
	public SuperLogEventMessage getMessage() {
		return this;
	}
}

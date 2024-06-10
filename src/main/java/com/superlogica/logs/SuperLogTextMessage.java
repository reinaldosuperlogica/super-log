package com.superlogica.logs;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@Builder
public class SuperLogTextMessage implements SuperLogMessage<String> {
	private String text;
	
	public SuperLogTextMessage(
		String text,
		Object... args
	) {
		if (Objects.isNull(args) || args.length == 0) {
			this.text = text;
			return;	
		}
		this.text = String.format(text, args);
	}

	public SuperLogTextMessage(
		String text
	) {
		this.text = text;
	}
	
	@JsonIgnore
	public String getMessage() {
		return this.text;
	}
}

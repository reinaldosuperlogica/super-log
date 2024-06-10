package com.superlogica.logs;

import com.amazonaws.util.json.Jackson;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
public class SuperLogObject {

	private SuperLogBrokerPublisher superLogBrokerPublisher;
	
	@Autowired
	public SuperLogObject(SuperLogBrokerPublisher superLogBrokerPublisher) {
		this.superLogBrokerPublisher = superLogBrokerPublisher;
	}	
	
	public void info(List<String> tags, Object object) {
		if (object instanceof String) {
			SuperLog.info(
				new SuperLogTextMessage((String) object),
				tags,
				superLogBrokerPublisher,
				false
			);
			return;
		}

		SuperLog.info(
			this.toMessage(object),
			tags,
			superLogBrokerPublisher,
			false
		);
	}

	public void info(Object object) {
		if (object instanceof String) {
			SuperLog.info(
				this.toMessage((String) object),
				Collections.emptyList(),
				superLogBrokerPublisher,
				false
			);
			return;
		}

		SuperLog.info(
			this.toMessage(object),
			Collections.emptyList(),
			superLogBrokerPublisher,
			false
		);
	}

	public void warning(List<String> tags, Object object) {
		if (object instanceof String) {
			SuperLog.warning(
				this.toMessage((String) object),
				tags,
				superLogBrokerPublisher,
				false
			);
			return;
		}

		SuperLog.warning(
			this.toMessage(object),
			tags,
			superLogBrokerPublisher,
			false
		);
	}

	public void warning(Object object) {
		if (object instanceof String) {
			SuperLog.warning(
				this.toMessage((String) object),
				Collections.emptyList(),
				superLogBrokerPublisher,
				false
			);
			return;
		}

		SuperLog.warning(
			this.toMessage(object),
			Collections.emptyList(),
			superLogBrokerPublisher,
			false
		);
	}

	public void critical(List<String> tags, Object object) {
		if (object instanceof String) {
			SuperLog.critical(
				this.toMessage((String) object),
				tags,
				superLogBrokerPublisher,
				false
			);
			return;
		}

		SuperLog.critical(
			this.toMessage(object),
			tags,
			superLogBrokerPublisher,
			false
		);
	}

	public void critical(Object object) {
		if (object instanceof String) {
			SuperLog.critical(
				this.toMessage((String) object),
				Collections.emptyList(),
				superLogBrokerPublisher,
				false
			);
			return;
		}

		SuperLog.critical(
			this.toMessage(object),
			Collections.emptyList(),
			superLogBrokerPublisher,
			false
		);		
	}

	private SuperLogMessage<JsonNode> toMessage(String object) {

		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return (SuperLogMessage<JsonNode>) objectMapper.readTree(object);
		} catch (Exception e) {
			return null;
		}

	}

	private SuperLogTextMessage toMessage(Object object) {
		if (Objects.isNull(object)) {
			return new SuperLogTextMessage("");
		}

		try {
			return new SuperLogTextMessage(Jackson.toJsonString(object));
		} catch (Exception e) {
			return new SuperLogTextMessage(object.toString());
		}
	}
             
}

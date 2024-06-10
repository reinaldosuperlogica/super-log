package com.superlogica.logs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class SuperLogText {
	
	@Autowired
	public SuperLogText(
		SuperLogBrokerPublisher superLogBrokerPublisher
	) {
		this.superLogBrokerPublisher = superLogBrokerPublisher;
	}
	private SuperLogBrokerPublisher superLogBrokerPublisher;
	
	public void info(List<String> tags, String text, Object... args) {
		SuperLog.info(
			new SuperLogTextMessage(text, args),
			tags,
			superLogBrokerPublisher,
			false
		);
	}

	public void info(String text, Object... args) {
		SuperLog.info(
			new SuperLogTextMessage(text, args),
			Collections.emptyList(),
			superLogBrokerPublisher,
			false
		);
	}

	public void info(String text) {
		SuperLog.info(
			new SuperLogTextMessage(text),
			Collections.emptyList(),
			superLogBrokerPublisher,
			false
		);
	}

	public void warning(List<String> tags, String text, Object... args) {
		SuperLog.warning(
			new SuperLogTextMessage(text, args),
			tags,
			superLogBrokerPublisher,
			false
		);
	}

	public void warning(String text, Object... args) {
		SuperLog.warning(
			new SuperLogTextMessage(text, args),
			Collections.emptyList(),
			superLogBrokerPublisher,
			false
		);
	}

	public void warning(String text) {
		SuperLog.warning(
			new SuperLogTextMessage(text),
			Collections.emptyList(),
			superLogBrokerPublisher,
			false
		);
	}

	public void critical(List<String> tags, String text, Object... args) {
		SuperLog.critical(
			new SuperLogTextMessage(text, args),
			tags,
			superLogBrokerPublisher,
			false
		);
	}

	public void critical(String text, Object... args) {
		SuperLog.critical(
			new SuperLogTextMessage(text, args),
			Collections.emptyList(),
			superLogBrokerPublisher,
			false
		);
	}

	public void critical(String text) {
		SuperLog.critical(
			new SuperLogTextMessage(text),
			Collections.emptyList(),
			superLogBrokerPublisher,
			false
		);
	}
             
}

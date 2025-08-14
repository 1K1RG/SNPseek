package org.irri.iric.portal.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:keys.properties")
public class KeysPropertyConfig {

	@Value("${smtp.port}")
	private String port;

	@Value("${smtp.from}")
	private String from;

	@Value("${smtp.fromName}")
	private String fromName;

	@Value("${smtp.username}")
	private String username;

	@Value("${smtp.password}")
	private String password;

	@Value("${smtp.host}")
	private String host;
	
	@Value("${passkey}")
	private String key;

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getFromName() {
		return fromName;
	}

	public void setFromName(String fromName) {
		this.fromName = fromName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@PostConstruct
    public void init() {
        System.out.println("Property value from keys.properties: " + key);
    }
	
}

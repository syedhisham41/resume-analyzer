package com.resumeanalyzer.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

	@Bean
	public WebClient mlWebClient(WebClient.Builder builder, @Value("${ml.service.url}") String baseServiceUrl) {
		return builder.baseUrl(baseServiceUrl).defaultHeader("Content-Type", "application/json").build();
	}
}

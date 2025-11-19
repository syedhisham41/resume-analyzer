package com.resumeanalyzer.analyzer.client;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.resumeanalyzer.analyzer.dto.AnalyzeResponseDTO;
import com.resumeanalyzer.analyzer.dto.ParseAllResponse;
import com.resumeanalyzer.analyzer.dto.ParseAnalyzeText;
import com.resumeanalyzer.analyzer.dto.ParseText;
import com.resumeanalyzer.analyzer.exceptions.AnalyzeEngineException;
import com.resumeanalyzer.analyzer.exceptions.AnalyzeEngineTimeOutException;

import reactor.core.publisher.Mono;

@Service
public class MlClientService {

	private final WebClient mlClient;

	public MlClientService(WebClient mlWebClient) {
		this.mlClient = mlWebClient;
	}

	public Mono<ResponseEntity<ParseAllResponse>> parseRawText(ParseText content) {
		return mlClient.post().uri("/nlp/parse_all").bodyValue(content).retrieve().toEntity(ParseAllResponse.class)
				.timeout(Duration.ofSeconds(30)).onErrorMap(throwable -> {
					if (throwable instanceof TimeoutException) {
						return new AnalyzeEngineTimeOutException("Response from Analyze Engine timed-out", throwable);
					} else if (throwable instanceof WebClientResponseException) {
						return new AnalyzeEngineException("Analyze Engine returned error", throwable);
					}
					return throwable;
				});

	}

	public Mono<ResponseEntity<AnalyzeResponseDTO>> runAnalyzeEngine(ParseAnalyzeText content) {

		return mlClient.post().uri("/analyze").bodyValue(content).retrieve().toEntity(AnalyzeResponseDTO.class)
				.timeout(Duration.ofSeconds(30)).onErrorMap(throwable -> {
					if (throwable instanceof TimeoutException) {
						return new AnalyzeEngineTimeOutException("Response from Analyze Engine timed-out", throwable);
					} else if (throwable instanceof WebClientResponseException) {
						return new AnalyzeEngineException("Analyze Engine returned error", throwable);
					}
					return throwable;
				});
	}

}

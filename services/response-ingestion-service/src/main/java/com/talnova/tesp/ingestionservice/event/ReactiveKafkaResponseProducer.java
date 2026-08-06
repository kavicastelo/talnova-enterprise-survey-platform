package com.talnova.tesp.ingestionservice.event;

import reactor.core.publisher.Mono;

public interface ReactiveKafkaResponseProducer {

    Mono<Void> publishResponseEvent(SurveyResponseSubmittedEvent event);
}

package com.moqucu.akka.actor;

import akka.actor.AbstractActor;
import com.moqucu.service.GreetingService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GreetingActor extends AbstractActor {

    private final GreetingService greetingService;

    @Autowired
    public GreetingActor(GreetingService greetingService) {

        this.greetingService = greetingService;
    }
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(GreetingActor.Greet.class,
                        r -> {
                            log.info("Received Greet message from " + getSender());
                            String name = r.name;
                            log.info("The sender's name is {} ", name);
                            getSender().tell(greetingService.greet(name), getSelf());
                        })
                .build();
    }

    @Data
    @RequiredArgsConstructor
    public static class Greet {

        private final String name;
    }
}

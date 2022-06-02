package com.moqucu.akka.actor;

import akka.actor.AbstractActor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class MyActor extends AbstractActor {

    @Override
    public void postStop() {
        log.info("Stopping actor {}", this);
    }

    public Receive createReceive() {
        return receiveBuilder()
                .matchEquals("printit", p -> {
                    log.info("The address of this actor is: {}", getSelf());
                    getSender().tell("Got Message", getSelf());
                })
                .build();
    }
}

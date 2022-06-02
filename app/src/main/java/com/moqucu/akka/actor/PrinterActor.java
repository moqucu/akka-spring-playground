package com.moqucu.akka.actor;

import akka.actor.AbstractActor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PrinterActor extends AbstractActor {

    public static final class PrintFinalResult {
        Integer totalNumberOfWords;

        public PrintFinalResult(Integer totalNumberOfWords) {
            this.totalNumberOfWords = totalNumberOfWords;
        }
    }

    @Override
    public void preStart() {
        log.info("Starting PrinterActor {}", this);
    }

    @Override
    public void postStop() {
        log.info("Stopping PrinterActor {}", this);
    }


    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(PrinterActor.PrintFinalResult.class,
                        r -> {
                            log.info("Received PrintFinalResult message from " + getSender());
                            log.info("The text has a total number of {} words", r.totalNumberOfWords);
                        })
                .build();
    }

}

package com.moqucu.akka.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.pattern.Patterns;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static scala.compat.java8.FutureConverters.*;

@Slf4j
public class ReadingActor extends AbstractActor {

    private final String text;

    public ReadingActor(String text) {
        this.text = text;
    }

    public static Props props(String text) {

        return Props.create(ReadingActor.class, text);
    }

    public static final class ReadLines {
    }

    @Override
    public void preStart() {

        log.info("Starting ReadingActor {}", this);
    }

    @Override
    public void postStop() {

        log.info("Stopping ReadingActor {}", this);
    }

    @Override
    public Receive createReceive() {

        return receiveBuilder()
                .match(ReadLines.class, r -> {

                    log.info("Received ReadLines message from " + getSender());

                    String[] lines = text.split("\n");
                    List<CompletableFuture<Object>> futures = new ArrayList<>();

                    for (int i = 0; i < lines.length; i++) {
                        String line = lines[i];
                        ActorRef wordCounterActorRef
                                = getContext().actorOf(Props.create(WordCounterActor.class), "word-counter-" + i);

                        CompletionStage<Object> future = toJava(Patterns.ask(
                                wordCounterActorRef,
                                new WordCounterActor.CountWords(line),
                                1000
                        ));
                        futures.add(future.toCompletableFuture());
                    }

                    Integer totalNumberOfWords = futures.stream()
                            .map(CompletableFuture::join)
                            .mapToInt(n -> (Integer) n)
                            .sum();

                    ActorRef printerActorRef = getContext().actorOf(Props.create(PrinterActor.class), "Printer-Actor");
                    printerActorRef.forward(new PrinterActor.PrintFinalResult(totalNumberOfWords), getContext());

                })
                .build();
    }
}

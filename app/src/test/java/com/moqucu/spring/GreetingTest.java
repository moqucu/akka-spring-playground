package com.moqucu.spring;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import akka.testkit.javadsl.TestKit;
import akka.util.Timeout;

import com.moqucu.akka.actor.GreetingActor;
import com.moqucu.service.GreetingService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;

import static com.moqucu.spring.SpringExtension.SPRING_EXTENSION_PROVIDER;
import static scala.compat.java8.FutureConverters.toJava;

@SpringBootTest
@ContextConfiguration(classes = {AppConfiguration.class, GreetingActor.class, GreetingService.class})
public class GreetingTest {

    @Autowired
    private ActorSystem system;

    @Test
    public void whenCallingGreetingActor_thenActorGreetsTheCaller() {

        ActorRef greeterRef = system
                .actorOf(
                        SPRING_EXTENSION_PROVIDER
                                .get(system)
                                .props("greetingActor"),
                        "greeter"
                );

        FiniteDuration duration = FiniteDuration.create(1, TimeUnit.SECONDS);
        Timeout timeout = Timeout.durationToTimeout(duration);

        CompletableFuture<Object> future = toJava(Patterns.ask(
                greeterRef,
                new GreetingActor.Greet("John"),
                timeout
        )).toCompletableFuture();

        try {
            assertEquals("Hello, John", future.get(1, TimeUnit.SECONDS));
        } catch (ExecutionException e) {
            assertTrue(e.getMessage().contains("The text to process can't be null!"), "Invalid error message");
        } catch (InterruptedException | TimeoutException e) {
            fail("Actor should respond with an exception instead of timing out !");
        }
    }

    @AfterEach
    public void teardown() {

        TestKit.shutdownActorSystem(system, Duration.apply(1000, TimeUnit.MILLISECONDS), true);
        system = null;
    }
}

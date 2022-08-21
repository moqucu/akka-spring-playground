package com.moqucu.spring;

import akka.actor.ActorSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import static com.moqucu.spring.SpringExtension.SPRING_EXTENSION_PROVIDER;

@Configuration
public class AppConfiguration {

    private final ApplicationContext applicationContext;

    @Autowired
    public AppConfiguration(ApplicationContext applicationContext) {

        this.applicationContext = applicationContext;
    }

    @Bean
    public ActorSystem actorSystem() {

        ActorSystem system = ActorSystem.create("akka-spring-demo");
        SPRING_EXTENSION_PROVIDER.get(system).initialize(applicationContext);

        return system;
    }
}

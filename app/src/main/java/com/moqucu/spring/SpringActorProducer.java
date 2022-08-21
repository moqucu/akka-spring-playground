package com.moqucu.spring;

import akka.actor.Actor;
import akka.actor.IndirectActorProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

@Slf4j
public class SpringActorProducer implements IndirectActorProducer {

    private final ApplicationContext applicationContext;

    private final String beanActorName;

    public SpringActorProducer(ApplicationContext applicationContext, String beanActorName) {

        this.applicationContext = applicationContext;
        this.beanActorName = beanActorName;
    }

    @Override
    public Actor produce() {

        return (Actor) applicationContext.getBean(beanActorName);
    }

    @Override
    public Class<? extends Actor> actorClass() {

        Class<?> classReference = applicationContext.getType(beanActorName);

        assert classReference != null;
        return classReference.asSubclass(Actor.class);
    }
}

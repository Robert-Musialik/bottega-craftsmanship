class: center, middle

# Spring

by Jakub Nabrdalik

---

# whoami

jakub nabrdalik

solution architect & dev @bottega

details: solidcraft.eu

linkedin: https://pl.linkedin.com/in/jnabrdalik

18 years in software dev as a Solution Architect, Software Architect, Team Leader, Head of IT, Developer, Analyst

banking, fintech, ecommerce, telco, energy, startups

From 3 man-month systems to 200+ dev monoliths to 700+ microservices

ex WJUG, WGUG leader, in programme committee 4 DevoxxPL, etc.

Developing with Spring since 2005

---

# who are you?

What's your name?

What do you do?

What's your experience with Spring Framework?

What's your experience with Spring Boot?

What's your experience with Spring Data?

What's your experience with Spring MVC?

What's your experience with Spring AOP?

What's your experience with Reactive Programming?

What's your experience with Microservices?

What do you expect from this training?

---

## Our plan

Exercises: just a normal app, from zero to hero

---

## Boot & Core

What is Spring Boot, how it works

Boot events and bootstrap

Setting up and autoconfiguration

Tuning autoconfiguration

Component scan gotchas

Ways Dependency Injection works in Spring: JavaConfig vs @Autwired

KoFu/JaFu: Functional Configuration

Ports&Adapters / Hexagonal Architecture

Properties, Profiles, Conditions

Spring Actuator: endpoints, security

---

## Spring MVC

How it works: Dispatcher, Request Mapping

Defining controllers: arguments & return values

Building URIs, RESTafaranism

Controller Advices, Handling Exceptions

Serving errors & static content

Locales, Session & Request scopes

Spring MVC testing

HATEOAS

Spring MVC vs Spring WebFlux

---

## Spring Data

What it is, how to use it

Repositories, fine tuning

Nested properties

Query cration anatomy & Native Query

Page, Slice, Sort

Custom logic in repository implementation

Default methods & additional logic in repository

Populating the database & bootstraping

Auditing

Criteria/Specification

---

## Spring Data JPA & transactions

Transaction management & attributes

Isolation & Propagation

Declarative Transactions

Manual Transactions

Patterns: where to start transaction

Aggregates and JPA misconceptions

NoSQL + SQL, best of both worlds

---

## Spring AOP

Aspect Oriented Programming nomenclature

LoadTimeWeaving vs CompileTimeWeaving vs Proxy

SPEL

---

# Part 1: getting started

Spring Boot app runner and Application Context

---

## Let's see some magic

I need a prototype fast!

How much do I need to write?

--

```Groovy
@RestController
class ThisWillActuallyRun {
	@RequestMapping("/") 
	String home() {
		"Hello World!"
	}
}
```

spring run app.groovy

---
class: center, middle

![](img/phillwebonboot.png)

---

## How do we start a real project

get a starter from http://start.spring.io/

create the controller

```Java
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class SampleController {
    @GetMapping("/")
    String home() {
        return "Hello World!";
    }
}

```

run

```bash
gradle bootRun
```

---

## Fat Jar

```
gradle build

java -jar build/libs/....jar
```

ls -lh build/libs/demo-0.0.1-SNAPSHOT.jar

unpack and see BOOT-INF

---

## What is spring-boot?

Kind of twitter-bootstrap for your Spring projects.

> Spring Boot makes it easy to create stand-alone, production-grade Spring based Applications that you can “just run”.

But how does it work?

---

### Gradle or Maven plugin

The Spring Boot Gradle Plugin provides Spring Boot support in Gradle, allowing you to package executable jar or war archives, run Spring Boot applications and omit version information from your build.gradle file for “blessed” dependencies.

> A typical Spring Boot project will apply the groovy, java, or org.jetbrains.kotlin.jvm plugin and the io.spring.dependency-management plugin as a minimum.

---

### Fat jar

This jar is handy because it includes all the other dependencies and things like your web server inside the archive. You can give anybody this one .jar and they can run your entire Spring application with no fuss: no build tool required, no setup, no web server configuration, etc: just

```bash
java -jar ...your.jar.

```

### Embedded server

```bash
gradle dependencyInsight  --dependency tomcat
```

---

## Dependencies

Both in Maven and Gradle you don't want to fight with incompatible versions

### Gradle 

```Groovy
buildscript {
	ext {
		springBootVersion = '2.0.2.RELEASE'
	}
	dependencies {
		classpath("org.springframework.boot:spring-boot-gradle-plugin:${springBootVersion}")
	}
}```

---

## Dependencies

### Maven

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.0.2.RELEASE</version>
</parent>
```

---

## Dependenies gone

> To manage the portfolio, a BOM (Bill of Materials - see this example) is published with a curated set of dependencies on the individual project. The release trains have names, not versions, to avoid confusion with the sub-projects.

No versions anymore

```Groovy

dependencies {
	compile('org.springframework.boot:spring-boot-starter-security')
	compile('org.springframework.boot:spring-boot-starter-actuator')
	compile('org.springframework.boot:spring-boot-actuator-docs')
	compile('org.springframework.boot:spring-boot-starter-aop')
	compile('org.springframework.boot:spring-boot-starter-data-jpa')
	compile('org.springframework.boot:spring-boot-starter-data-rest')
	compile('org.springframework.boot:spring-boot-starter-hateoas')
	...
```

---

## The main method

```java
@SpringBootApplication
public class MyApplicationConfiguration {
	public static void main(String[] args) throws Exception {
		SpringApplication.run(MyApplicationConfiguration.class, args)
	}
}
```

---

## @SpringBootApplication

```java
@SpringBootApplication 
	= @SpringBootConfiguration + @EnableAutoConfiguration + @ComponentScan
```

```java
@SpringBootConfiguration = @Configuration on top level (only 1)
```

---

## The main method

```java
@SpringBootApplication
public class MyApplicationConfiguration {
	public static void main(String[] args) throws Exception {
		SpringApplication.run(MyApplicationConfiguration.class, args)
	}
}
```

--

```java
public static void main(String[] args) throws Exception {
	SpringApplication application = new SpringApplication(SampleController.class); //I haz an app
	application.setLogStartupInfo(false); //I configure smth
	//Also I do whatever I like, hell yeah!
	application.run(args); //I run the app
}
```


---

## The main method: builder

```java
public static void main(String[] args) throws Exception {
	new SpringApplicationBuilder()
		.bannerMode(Mode.OFF)
		.sources(Parent.class)
		.child(Application.class)
		.run(args);
}
```

---

## No magic included

maven/gradle plugin != spring-boot

You don’t need fat jars or plugins for spring-boot.

You can add dependencies manually, and use only the parts you like.

---

##  How is it configured?

```java
@EnableAutoConfiguration
```

Enable auto-configuration of the Spring Application Context, attempting to guess and configure beans that you are likely to need [...] based on your classpath and what beans you have defined.

For example, If you have tomat-embedded.jar on your classpath you are likely to want a TomcatEmbeddedServletContainerFactory (unless you have defined your own EmbeddedServletContainerFactory bean).

Auto-configuration tries to be as intelligent as possible and will back-away as you define more of your own configuration. You can always manually exclude() any configuration that you never want to apply.

It is generally recommended that you place @EnableAutoConfiguration in a root package so that all sub-packages and classes can be searched (or you can specify @ComponentScan(basePackages = {"..."})).

---

# Condition out of the box

Double tap shift, enter @Conditional, show more

```Java
@ConditionalOnResource
@ConditionalOnExperssion(SpEL)
@ConditionalOnProperty
@ConditionalOnJava
@ConditionalOnClass
@ConditionalOnMissingBean
...
```

---

## Starters

Setting up dependencies for you

Because: http://en.wikipedia.org/wiki/Dependency_hell

Goto: https://github.com/spring-projects/spring-boot/tree/master/spring-boot-project/spring-boot-starters

---

## back away as you define

Add HSQLDB or H2, Spring-boot will configure in-memory db

--

Define your own DataSource -> No more in-memory db

--

You can also exclude manually

--

Add spring-data-jpa

```groovy
compile("org.springframework.boot:spring-boot-starter-data-jpa")
```

--

gradle run - will not work (no datasource, and no db lib on classpath)

--

Exclude all autoconfig requiring datasource

```java
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class,
	JpaRepositoriesAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
```

--

gradle run - will work

---

## Subscribing to events

```java
new SpringApplicationBuilder().listeners(/*here be listeners*/).run(args);
```

- ApplicationStartingEvent
- ApplicationEnvironmentPreparedEvent
- ApplicationPreparedEvent
- ApplicationReadyEvent
- ApplicationStartedEvent
- ApplicationReadyEvent
- ApplicationFailedEvent

--

Core context: ContextClosedEvent, ContextRefreshedEvent, ContextStartedEvent, ContextStoppedEvent,

Servlet: ServletRequestHandledEvent, BrokerAvailabilityEvent

Websockets: SessionConnectedEvent, SessionConnectEvent, SessionDisconnectEvent, SessionSubscribeEvent,
SessionUnsubscribeEvent

---

## When JVM says bye bye

Last chance to react

- implement DisposableBean interface

- add @PreDestroy to a bean method

- implement ExitCodeGenerator interface

---

## Logging

Commons Logging for all internal logging

Default configurations are provided for Java Util Logging, Log4J2, Log4J and Logback.

By default Logback

To configure

- Logback - logback.xml
- Log4j - log4j.properties or log4j.xml
- JDK (Java Util Logging) - logging.properties

---

## Application context

Notice what the run method returns

```Java
public static ConfigurableApplicationContext run(....)
```

If Spring MVC is present, an AnnotationConfigServletWebServerApplicationContext is used

If Spring MVC is not present and Spring WebFlux is present, an AnnotationConfigReactiveWebServerApplicationContext is used

Otherwise, AnnotationConfigApplicationContext is used
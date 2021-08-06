class: center, middle

# Spring Core

by Jakub Nabrdalik

---

## Plan

Ways Dependency Injection works in Spring: JavaConfig vs @Autwired

Functional configuration in different languages

Ports&Adapters / Hexagonal Architecture

Properties, Profiles, Conditions

Spring Actuator: endpoints, security

---


## What do we keep in a container?

Services, controllers, all other objects

… except what we keep in database (stateful objects: entities, a.k.a. model)

---

## Dependency Injection vs Inversion of control

Do you know the difference?

---

## No inversion

```java
class SomeController {
	private SomeEntityRepository someEntityRepository = new SomeEntityRepository()

	public Map mine() {
		List<SomeEntity> entities = someEntityRepository.findByUsername("some")
		return ["entities": entities]
 	}
```

--

Problem?

You cannot change someEntityRepository to anything else without recompilation.

You cannot test on a mock or on a different repository implementation.

---

## SOLID (object-oriented programming)

Dependency Inversion Principle

> one should “depend upon abstractions, [not] concretions"

https://en.wikipedia.org/wiki/SOLID_(object-oriented_design)

---

## Solution

> a software design pattern that allows the removal of hard-coded dependencies and makes it possible to change them, whether at run-time or compile-time.

---

## Dependency Injection

```groovy
class SomeController {
 private final SomeEntityRepository someEntityRepository
 
 public SomeController(SomeEntityRepository someEntityRepository) {
	 this.someEntityRepository = someEntityRepository
 }

 public Map mine() {
 	List<SomeEntity> entities = someEntityRepository.findByUsername("some");
 	return ["entities": entities]
 }
}
```

---

## Dependency Injection

- IoC: Inversion of Control
- DI: Dependency Injection

> As a result I think we need a more specific name for this pattern. Inversion of Control is too generic a term, and thus people find it confusing. As a result with a lot of discussion with various IoC advocates we settled on the name Dependency Injection (http://martinfowler.com/articles/injection.html)

---

## IoC container

The thing that does the wiring

```java
org.springframework.context.ApplicationContext
```

Represents the Spring IoC container and is responsible for instantiating, configuring, and assembling the aforementioned beans

---

## Injection techniques

What types of injection do you know?

---

## Injection via constructor

```java
public class WelcomeController {
 private final WorkshopRepository workshopRepository;
 private final LoggedUserRepository loggedUserRepository;
 private final UserRepository userRepository;

 public WelcomeController(
		 WorkshopRepository workshopRepository,
		 LoggedUserRepository loggedUserRepository,
		 UserRepository userRepository) {
	 this.workshopRepository = workshopRepository;
	 this.loggedUserRepository = loggedUserRepository;
	 this.userRepository = userRepository;
 }
```

---

## Injection via setters

```Java
public class WelcomeController {
 private WorkshopRepository workshopRepository;
 private LoggedUserRepository loggedUserRepository;
 private UserRepository userRepository;

public void setWorkshopRepository(WorkshopRepository workshopRepository) {
 this.workshopRepository = workshopRepository;
}

public void setLoggedUserRepository(LoggedUserRepository loggedUserRepository) {
 this.loggedUserRepository = loggedUserRepository;
}

public void setUserRepository(UserRepository userRepository) {
 this.userRepository = userRepository;
}
```

---

## Injection by magic

```Java
public class WelcomeController {
 @Autowired private WorkshopRepository workshopRepository;
 @Autowired private LoggedUserRepository loggedUserRepository;
 @Autowired private UserRepository userRepository;

 //BADUM! Tssssss...
```

---

# Configuring container metadata

or how to register beans in the container

---

## @Annotations

```groovy
@ComponentScan(basePackages = {"eu.solidcraft.forf"})
public class Application {...}

@Component
class SomeService {
 private final SomeEntityRepository someEntityRepository

 @Autowired
 SomeService(SomeEntityRepository someEntityRepository) {
 	this.someEntityRepository = someEntityRepository
 }
```

---

## @Annotations

If you register a bean with single constructor, @Autowired is not needed (Spring will use that constructor).

```groovy
@Component
class SomeService {
 private final SomeEntityRepository someEntityRepository

 SomeService(SomeEntityRepository someEntityRepository) {
 	this.someEntityRepository = someEntityRepository
 }
```


---

## Autowiring by

name - using id, name or alias

type - assuming you have only one object with that type

---

## Types of annotations to autowire

@Autowired - applies to methods, constructors and fields

@Required - applies to bean property setter methods

@Resource - from JSR 250 (setters and fields)

@Inject and @Named - from JSR 330

---

## Which one to use? 

If in doubt, use @Autowired

If you want to be able to get rid of Spring, use @Inject

But beware, because @Inject != @Autowired (prototype vs singleton)

---

## @Autowired and pointing to id

```java
public class MovieRecommender {
 @Autowired
 @Qualifier("main")
 private MovieCatalog movieCatalog;
…
}


@Component(“main”)
class MovieCatalog {...}
```

---

## @Autowired: required by default

```java
@Autowired(required=false)
public void setMovieFinder(MovieFinder movieFinder) {
 this.movieFinder = movieFinder;
}
```

---

## @Autowired & many constructors 

> Only one annotated constructor per-class can be marked as required, but multiple non-required constructors can be annotated. In that case, each is considered among the candidates and Spring uses the greediest constructor whose dependencies can be satisfied, that is the constructor that has the largest number of arguments.

---

# Registering a bean with annotations

```java
@Component(“someName”)

@Service(“someName”)

@Repository(“someName”)

@Controller(“someName”)

@RestController(“someName”)
```

---

## JSR 330 annotations

```java
@Named("movieListener")
public class SimpleMovieLister {
 private MovieFinder movieFinder;
 
 @Inject
 public void setMovieFinder(
 		@Named("main") MovieFinder movieFinder) {
 	this.movieFinder = movieFinder;
 }
 // ...
}
```

---

## Spring vs JEE annotations

> The JSR-330 default scope is like Spring’s prototype. However, in order to keep it consistent with Spring’s general defaults, a JSR-330 bean declared in the Spring container is a singleton by default.

https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html#beans-named

---

## @PostConstruct & @PreDestroy

```java
public class CachingMovieLister {

 @PostConstruct
 public void populateMovieCache() {
 	// populates the movie cache upon initialization...
 }

 @PreDestroy
 public void clearMovieCache() {
 	// clears the movie cache upon destruction...
 }
}
```

---

## @Lazy

```java
@Component
@Lazy
public class SomeLazyBastard {
}
```

Indicates whether a bean is to be lazily initialized. 

If not present: initialization on startup

If present: when referenced

Has a value (true|false) but why would you?

---

## @Lazy

```java
@Lazy
@Autowired
SomeLazyBastard(@Lazy UserService userService) {
 this.userService = userService;
}
```

In addition to its role for component initialization, this annotation may also be placed on injection points marked with @Autowired or @Inject.

In that context, it leads to the creation of a lazy-resolution proxy for all affected dependencies

---

## Is @Annotation config good 4 everything?

Register two beans of the same class, under dirrefent id, with different configuration.

--

Annotation config is limited. Use it only for:

- hello worlds
- tests (no control over constructor)
- @Controller and @RestController (not injected anywhere)

The JVM world kind of drifted into the direction of native language (Java) config (Dagger1, Dagger2, Guice)

---

# Java config

Power for real projects

---

## @Configuration & @Bean

```java
@Configuration
public class AppConfig {
 
 @Bean
 public MyService myService() {
 	return new MyServiceImpl();
 }
}
```

> The @Bean annotation is used to indicate that a method instantiates, configures and initializes a new object to be managed by the Spring IoC container.

> Annotating a class with @Configuration indicates that its primary purpose is as a source of bean definitions. Furthermore, @Configuration classes allow inter-bean dependencies to be defined by simply calling other @Bean methods in the same class. 

---

## Changing id

```java
@Configuration
public class AppConfig {
 
 @Bean(name=”someService”)
 public MyService myService() {
 	return new MyServiceImpl();
 }
}
```

---

## Aliasing

```java
@Configuration
public class AppConfig {

 @Bean(name={”someService”, “some2”})
 public MyService myService() {
 	return new MyServiceImpl();
 }
}
```

---

## Importing, or keeping it simple

```java
@Configuration
public class ConfigA {
 @Bean
 public A a() {
 	return new A();
 }
}

@Configuration
@Import(ConfigA.class)
public class ConfigB {
 @Bean
 public B b() {
 	return new B();
 }
}
```

---

## Dependencies

```java
@Configuration
public class AppConfig {
 @Bean
 public Foo foo() {
	 return new Foo(bar());
 }

 @Bean
 public Bar bar() {
 	return new Bar();
 } 
}
```

How many times will bar() be called?

---

## Dependencies

```java
@Configuration
public class AppConfig {
 @Bean
 public Foo foo(Bar bar) {
 	return new Foo(bar);
 }

 @Bean
 public Bar bar() { 
 	return new Bar();
 }
}
```

How many times will bar() be called?

---

> The @Bean methods in a Spring component are processed differently than their counterparts inside a Spring @Configuration class. 

> The difference is that @Component classes are not enhanced with CGLIB to intercept the invocation of methods and fields. 

> CGLIB proxying is the means by which invoking methods or fields within @Configuration classes @Bean methods create bean metadata references to collaborating objects. Methods are not invoked with normal Java semantics. In contrast, calling a method or field within a @Component classes @Bean method has standard Java semantics.

---

## Dependencies from other files

```java
@Configuration
class ServiceConfig {
 
 private AccountRepository accountRepository;

 ServiceConfig(AccountRepository accountRepository) {
 	this.accountRepository = accountRepository
 }

 @Bean
 TransferService transferService() {
	return new TransferServiceImpl(accountRepository);
 }
}
```

---

## @Lazy on @Bean

```java
@Lazy @Bean
public SomeLazyBastard someLazyBastard() {
 return new SomeLazyBastard();
}
```

Works on @Bean just the same.

If Lazy is present on a Configuration, all @Bean methods within that @Configuration should be lazily initialized.

Can be overridden on a method level

---

# Scopes

Singleton (default) - one per application

Prototype - created new for each reference

Request

Session

(you can have your own: instance per user, for example?)

---

## Prototype Scope

```java
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
class MySpecialBean {
}
...
@Autowired
private MySpecialBean myVeryOwnBean;
```

---

## Session Scope

```java
@Component
@Scope(WebApplicationContext.SCOPE_SESSION)
class LoggedUserInSessionRepository {
 
 private User loggedInUser;
 
 void login(User user) {
 	this.loggedInUser = user;
 }
 
 User getLoggedUser() {
 	verifyUserExists();
 	return loggedInUser;
 }

 private void verifyUserExists() {
 	if(loggedInUser == null) {
 	throw new RuntimeException("No user logged in");
 }
}
```

---

## Injecting smaller scope into a larger

> If you want to inject (for example) an HTTP request scoped bean into another bean, you must inject an AOP proxy in place of the scoped bean.

---

## Types of proxies

CGLIB-based Class proxy

- proxy-target-class="true"

Interface-based proxy

- proxy-target-class="false"

---

## Define proxy per bean

```java
@Component
@Scope(value= WebApplicationContext.SCOPE_SESSION,
	proxyMode= ScopedProxyMode.TARGET_CLASS)
public class LoggedUserInSessionRepository
		implements LoggedUserRepository {
	private User loggedInUser;
}
```

Remember JEE vs Spring differences?

---

## Profiles with annotations

```java
@Configuration
@Profile("dev")
public class StandaloneDataConfig {
 
 @Bean
 public DataSource dataSource() {
	 return new EmbeddedDatabaseBuilder()
		 .setType(EmbeddedDatabaseType.HSQL)
		 .addScript("classpath:com/bank/config/sql/schema.sql")
		 .addScript("classpath:com/bank/config/sql/test-data.sql")
		 .build();
 }
}
```

---

## Profiles work with tests

```groovy
@Transactional
@TransactionConfiguration(defaultRollback = true)
@ActiveProfiles(profiles = ['pricing.test'])
abstract class IntegrationSpec extends Specification {
```

---

## @Conditional inside

The @Conditional annotation indicates specific
org.springframework.context.annotation.Condition implementations that should
be consulted before a @Bean is registered.

```java
@Conditional(ProfileCondition.class)
public @interface Profile {
```

---

## Profile condition

```java
class ProfileCondition implements Condition {
@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		MultiValueMap<String, Object> attrs = metadata.getAllAnnotationAttributes(Profile.class.getName());
		if (attrs != null) {
			for (Object value : attrs.get("value")) {
				if (context.getEnvironment().acceptsProfiles((String[]) value)) {
					return true;
				}
			}
			return false;
		}
		return true;
	}
}
```

---

## Profiles are NOT exclusive


```java
@Configuration
@EnableWebSecurity
@Profile([Profiles.DEVELOPMENT, Profiles.TEST])
class HttpBasicSecurityConfig {
```

---

## How to turn on profiles

JVM param or system environment variable

```bash
-Dspring.profiles.active="pricing.development"
```

Servlet context parameter

```xml
<servlet>
 <servlet-name>dispatcher</servlet-name>
 <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
 <init-param>
 <param-name>spring.profiles.active</param-name>
 <param-value>production</param-value>
 </init-param>
 </servlet>
```

In code

```java
ctx.getEnvironment().setActiveProfiles("profile1", "profile2");
```

---

## Special profile

If no profile is specified, it is equal to “default” profile. You can set it up yourself.

Command line

```bash
-Dspring.profiles.default=production
```

or call setDefaultProfiles() on Environment (which you will get from context)

---

# Properties

Set up defaults (for example in application.properties)

Give other on production via:
- /config/application.properties subdir in current dir
- /application.properties in current dir
- application-{profile}.properties or YAML files
- OS environment variables.
- Java System properties
- Command line arguments (--server.port=9000)
- ...more

Don’t forget about Profiles.

You can often keep properties for staging/testing/dev in the repo, under
different profiles. Production properties, on the other hand, not really

---

## Property priorities

Higher override lower. These are popular, the full list is much longer.

- Command line arguments.
- Java System properties (System.getProperties())
- OS environment variables.
- application-{profile}.properties outside of your packaged jar
- application-{profile}.properties inside your jar
- application.properties outside of your packaged jar
- application.properties inside your jar
- @PropertySource annotations on your @Configuration classes.

As of Spring Boot 2.4, external file always override packaged files (profile specific or not)

---

## Loading properties

```java
@PropertySource("classpath:com/foo/foo.properties")
```

Resource syntax


classpath -  classpath:com/myapp/config.xml - Loaded from the classpath

file - file:/data/config.xml Loaded as a URL - from the filesystem

http - http://myserver/logo.png - Loaded as a URL

(none) - /data/config.xml - Depends on the underlying ApplicationContext

---

## Accessing properties

@Value and default after semicolon

```java
@Value( "${jdbc.url:localhost}" ) private String jdbcUrl;
```

```
${...} the property placeholder syntax, only to dereference properties
#{...} SpEL syntax, which is far more complex, also handle property
placeholders
```

Via Environment

```java
@Autowired private Environment env;

dataSource.setUrl(env.getProperty("jdbc.url"));
```

---

## You can use YAML instead of


```yaml
connection:
 username: admin
 remoteAddress: 192.168.1.1
```

---

## Typesafe properties

```java
@ConfigurationProperties(prefix="connection")
public class ConnectionSettings {
	private String username;
	private InetAddress remoteAddress;
	... //setters and getters
}

@Configuration
@EnableConfigurationProperties(ConnectionSettings.class)
class PropertiesConfiguration {
	
	@Bean 
	ConnectionSettings connectionSettings() {
		return new ConnectionSettings()
	}
}
```

---

## Generate random values

```
my.secret=${random.value}
my.number=${random.int}
my.bignumber=${random.long}
my.number.less.than.ten=${random.int(10)}
my.number.in.range=${random.int[1024,65536]}
```

---

## Properties in tests

To have default properties in tests, in JUnit use @ContextConfiguration

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringExercisesApplication.class)
@WebAppConfiguration
public abstract class IntegrationTest {
```

Or setup config file initializer

```java
initializers = ConfigFileApplicationContextInitializer.class
```

```java
@WebAppConfiguration
@ContextConfiguration(
	classes = SpringExercisesApplication, 
	initializers = ConfigFileApplicationContextInitializer.class)
class IntegrationSpec extends Specification {
```

---

## Properties in tests

requires: SmartContextLoader

```java
@SpringBootTest
@TestPropertySource("classpath:test.properties")
public class MyIntegrationTests {...}

@SpringBootTest
//relative to the package in which the test class is defined
@TestPropertySource("/test.properties")
public class MyIntegrationTests {...}
```

---

## Properties in tests

Also can be inlined


```java
@ContextConfiguration
@TestPropertySource(properties={"timezone = GMT", "port: 4242"})
public class MyIntegrationTests {...}
```

we can inline them in many ways

- "key=value"
- "key:value"
- "key value"

---

## Properties in tests: default

If @TestPropertySource is declared as an empty annotation (i.e., without explicit values for the locations or properties attributes), an attempt will be made to detect a default properties file relative to the class that declared the annotation.

```java
@ContextConfiguration
@TestPropertySource
public class MyTest {...}
```

will use "classpath:com/example/MyTest.properties"

---

## Properties in tests: precedence

Test property sources have higher precedence than those loaded from the operating system’s
environment or Java system properties as well as property sources added by the application declaratively via @PropertySource or programmatically.

Inlined properties override other.

---

## Grouping profiles in properties

A profile group allows you to define a logical name for a related group of profiles.

```
spring.profiles.group.production[0]=proddb
spring.profiles.group.production[1]=prodmq
```

Our application can now be started using 
```
--spring.profiles.active=production
```
to active the production, proddb and prodmq profiles in one hit.

---

## Importing properties

You can import properties from properties file (on filesystem or over http). If the file ends in properties, yaml or is a config tree, you jut do:

```
spring.config.import=optional:file:./dev.properties
```

otherwise you need to point out what kind of file to expect

```
spring.config.import=file:/etc/config/myconfig[.yaml]
```

---

## ConfigMaps and Secrets

On k8s prod properties and secrets are defined using k8s ConfigMaps and Secrets (mounted data volumes)

You can import them by using spring.config.import property

```
spring.config.import=optional:configtree:/etc/config/
```

---

## Functional configuration

Apart from @Bean/@Configuration java config, there is a push for pure java configuration without package scanning

This was always possible in java config, but now it also gets into the MVC config, and there are several projects for languages where annotations are less natural

---

### Bean definition DSL

For java and kotlin see docs [here](https://docs.spring.io/spring-framework/docs/current/reference/html/languages.html#kotlin)
```java
GenericApplicationContext context = new GenericApplicationContext();
context.registerBean(Foo.class);
context.registerBean(Bar.class, () -> new Bar(context.getBean(Foo.class)));
```

```kotlin
val context = GenericApplicationContext().apply {
    registerBean<Foo>()
    registerBean { Bar(it.getBean()) }
}
```

or with single constructor bar will be autowired by type

```kotlin
val context = GenericApplicationContext().apply {
    registerBean<Foo>()
    registerBean<Bar>()
}
```

Works with profiles, properties, and routes

---

### Kotlin support in Bean Definition

Spring docs example

```kotlin
val myBeans = beans {
    bean<Foo>()
    bean<Bar>()
    bean("bazBean") {
        Baz().apply {
            message = "Hello world"
        }
    }
    profile("foobar") {
        bean { FooBar(ref("bazBean")) }
    }
    bean(::myRouter)
}

fun myRouter(foo: Foo, bar: Bar, baz: Baz) = router {
    // ...
}
```

---

### My example

```kotlin
internal fun commandBeans() = beans {
    profile(Profiles.NOT_INTEGRATION_TEST) {
        bean {
            { Instant.now() } as CurrentClock
        }
    }

    profile(Profiles.INTEGRATION_TEST) {
        bean {
            ProgressingClock()
        }
    }

    environment(condition = { getProperty("kafka.enabled") == "true" }) {
        bean {
            SessionStartRequestedKafkaEventPublisher(
                    ref<String>("kafkaBrokers"),
                    ref<KafkaSerdeFactory>()
            )
        }
    }

    bean {
        PartnerWebClient(ref<WebClient.Builder>())
    }
```

---

### My example

```kotlin
    bean {
        router {
            (accept(MediaType.APPLICATION_JSON) and "/commands/").nest {
                POST("/START_SESSION") {
                    startSession(it)
                }
                POST("/STOP_SESSION") {
                    stopSession(it)
                }
            }
            GET("/$SESSIONS_URI_PATH", (accept(MediaType.APPLICATION_JSON))) {
                getSessions(it, ref<Environment>())
            }
        }
    }
}

...

internal fun BeanSupplierContext.startSession(it: ServerRequest): ServerResponse {
    return ref<CommandFacade>()
            .startSession(
                    it.body(OcpiStartSessionCommand::class.java),
                    OcpiAuthenticationParams.forHttpRequest(it.servletRequest()))
            .toServerResponse()
}

```

---

### Kotlin support in Bean Definition

All current features of the DSL are available [here](https://docs.spring.io/spring-framework/docs/current/kdoc-api/spring-framework/org.springframework.context.support/-bean-definition-dsl/)

---

### Functional Java support

A full Jafu app definition (experimental)

```java
import org.springframework.fu.jafu.JafuApplication;
import static org.springframework.fu.jafu.Jafu.webApplication;
import static org.springframework.fu.jafu.webmvc.WebMvcServerDsl.webMvc;

public class Application {

	public static JafuApplication app = webApplication(a -> a.beans(b -> b
			.bean(SampleHandler.class)
			.bean(SampleService.class))
			.enable(webMvc(s -> s
					.port(s.profiles().contains("test") ? 8181 : 8080)
					.router(router -> {
						SampleHandler handler = s.ref(SampleHandler.class);
						router
								.GET("/", handler::hello)
								.GET("/api", handler::json);
					}).converters(c -> c
							.string()
							.jackson()))));

	public static void main (String[] args) {
		app.run(args);
	}
}
```

---

### Groovy Bean Definition DSL

Docs are [here](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#groovy-bean-definition-dsl)

```groovy
beans {
    dataSource(BasicDataSource) {
        driverClassName = "org.hsqldb.jdbcDriver"
        url = "jdbc:hsqldb:mem:grailsDB"
        username = "sa"
        password = ""
        settings = [mynew:"setting"]
    }
    sessionFactory(SessionFactory) {
        dataSource = dataSource
    }
    myService(MyService) {
        nestedBean = { AnotherBean bean ->
            dataSource = dataSource
        }
    }
}
```

```groovy
val context = GenericApplicationContext()
GroovyBeanDefinitionReader(context)
	.loadBeanDefinitions("services.groovy", "daos.groovy")
context.refresh()
```

---

### Kofu - functional kotlin example

Reactive kotlin web spring boot example

https://github.com/mixitconf/mixit/

---

## Event bus

To publish a custom ApplicationEvent, inject ApplicationEventPublisher and call the publishEvent() method

```Java
void publishEvent(ApplicationEvent event)
```

If the specified event is not an ApplicationEvent, it is wrapped in a PayloadApplicationEvent

```Java
void publishEvent(Object event)
```

---

## Listening to an event

Just annotate your class with @EventListener

```Java
public class BlackListNotifier {
    @EventListener
    public ListUpdateEvent handleBlackListEvent(BlackListEvent event) {
        // notify appropriate parties via notificationAddress...
        // emits ListUpdateEvent 
    }
}
```

---

## Listening to an event asynchronously

In main class add @EnableAsync, then add @Async to your listening methods

```Java
@EventListener
@Async("blackListTaskExecutor")
public void processBlackListEvent(BlackListEvent event) {
    // BlackListEvent is processed in a separate thread
}
```

If the event listener throws an Exception it will not be propagated to the caller

Such event listener cannot send replies

--

Now register the executor

```Java
@Bean
TaskExecutor blackListTaskExecutor() {
    ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
    threadPoolTaskScheduler.setThreadGroupName("blacklistThread");
    threadPoolTaskScheduler.setPoolSize(20);
    return threadPoolTaskScheduler;
}
```
---

## Remember to set target executor!

If you don't, all @Async methods will run on SimpleAsyncTaskExecutor

> implementation that fires up a new Thread for each task, executing it asynchronously.

What is the problem with this implementation?

--

> By default, the number of concurrent threads is unlimited.

You will run out of memory!
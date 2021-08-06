class: center, middle

Spring Security 4.2.2.RELEASE

by Jakub Nabrdalik

---

# Warning

Spring 5.0 going reactive requires a lot of changes

Thus Spring Security 5 is going to be a bit different

You have to be aware of those problems even with Spring 4, if you want to use reactive style (or thread pools in general).

---

## Modules

Spring security has a long history (acegi security anyone), and is composed from a lot of modules supporting different use cases.

Some of those modules are

- Core - shared abstracts and interfaces
- Web - servlet filters, web authentication etc.
- Config - java and xml config
- LDAP - guess what
- ACL - for ACL on db entities security model
- CAS - authentication with CAS single sign-on server
- OpenID - authentication via OpenID
- Test - support for tests
- Remoting - securing JMS, RMI, AMQP...
- Crypto - better cryptography

---

## SecurityContextHolder

Stores details of the present security context of the application (why is logged in). By default uses ThreadLocal to store it.

--

Which threads are being used in a web application?

--

What Spring Security has to do when a request comes in?

--

What Spring Security has to do when a response is sent?

--

What about asynchronous behaviour (Completable future, Observable, Flux)?

--

> applications might want to have threads spawned by the secure thread also assume the same security identity. This is achieved by using SecurityContextHolder.MODE_INHERITABLETHREADLOCAL

Standalone app (single user): 

```
SecurityContextHolder.MODE_GLOBAL
```

Good old non-reactive web app: 

```
SecurityContextHolder.MODE_THREADLOCAL
```

Servlet 3.1 async is supported out of the box (do not worry about it)

---

## Getting the logged in user

```java
Object principal = SecurityContextHolder
	.getContext() //SecurityContext
	.getAuthentication() //Authentication
	.getPrincipal(); //Principal
```

--

Authentication

- principal (who is the logged in user)
- authorities (GrantedAuthority; usually "roles")

--

Principal

- ussually UserDetails or a String
- can be anything - Object

--

Why would they return an Object on this lib?

---

## Logged in user name

Most often all I need in my code is to get the logged in user name

```java
public class CurrentUserGetter {

    public String getSignedInUserName() {
        String loggedUser = null;
        SecurityContext context = SecurityContextHolder.getContext();
        if(context != null) {
            Authentication authentication = context.getAuthentication();
            if (authentication != null) {
                loggedUser = authentication.getName();
            }
        }
        return loggedUser;
    }

    public String getSignedInUserNameOrAnonymous() {
        String signedInUserName = getSignedInUserName();
        return (hasText(signedInUserName)) ? getSignedInUserName() : "anonymous";
    }
}
```

There are other options (Principal in controller for example), but I'd recommend not using them

---

## Where does user come from 

UserDetailsService - DAO for user data (InMemoryDaoImpl, JdbcDaoImpl, etc.)

```java
public interface UserDetailsService {
	UserDetails loadUserByUsername(String username) 
		throws UsernameNotFoundException;
}
```

--

> On successful authentication, UserDetails is used to build the Authentication object that is stored in the SecurityContextHolder

--

```java
public interface UserDetails extends Serializable {
	Collection < ? extends GrantedAuthority > getAuthorities();
	String getPassword();
	String getUsername();
	boolean isAccountNonExpired();
	boolean isAccountNonLocked();
	boolean isCredentialsNonExpired();
	boolean isEnabled();
}
```

--

```java
public class User implements UserDetails, CredentialsContainer {...}
```

---

## Authentication in web

DelegatingFilterProxy registered as a servlet filter and delegates to FilterChainProxy

--

FilterChainProxy - now we are in a Spring Security world. This one fires up a list of SecurityFilterChains

--

SecurityFilterChain decides whether this request should fire its filters (for example by URL path)

--

```java
public interface SecurityFilterChain {
	boolean matches(HttpServletRequest request);
	List<Filter> getFilters();
}
```

```java
public interface Filter {
	...
	public void doFilter(
		ServletRequest request, 
		ServletResponse response,
        FilterChain chain) {
    }
}
```

---

## Filters

Filters vary depending on your configuration. Their order is important. You can add you own

--

Example for simple usernam/password

- ChannelProcessingFilter - check if needs to redirect to a different protocol
- SecurityContextPersistenceFilter - stores SecurityContext in SecurityContextHolder
- ConcurrentSessionFilter - hmmm... what if user has several sessions? Need to update SecurityContext 
- Authentication processing mechanisms - (for example UsernamePasswordAuthenticationFilter), adds valid Authentication
- RememberMeAuthenticationFilter - adds valid Authentication
- AnonymousAuthenticationFilter - if all fails, adds anonymous Authentication
- ExceptionTranslationFilter, to catch any Spring Security exceptions and redirect etc.

http://docs.spring.io/spring-security/site/docs/4.2.2.RELEASE/reference/htmlsingle/#filter-stack

---

## Authentication processing mechanisms

```java

public interface AuthenticationManager {
	Authentication authenticate(Authentication authentication)
		throws AuthenticationException;
}
```

--

The default implementation - ProviderManager - delegates to a list of configured AuthenticationProvider

--

Each provider will either throw an exception or return a fully populated Authentication object

--

Why would they want to have a list of AuthenticationProviders? Why several methods of authentication?

---

## DaoAuthenticationProvider

The most common approach to verifying an authentication request is to load the corresponding UserDetails and check the loaded password against the one that has been entered by the user

--

```java
public class DaoAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {
	public void setPasswordEncoder(Object passwordEncoder) {...}
	public void setSaltSource(SaltSource saltSource) {...}
	public void setUserDetailsService(UserDetailsService userDetailsService) {...}
}
```

---

## Password encoder

```java
//org.springframework.security.authentication.encoding - deprecated
package org.springframework.security.crypto.password

public interface PasswordEncoder {
	String encode(CharSequence rawPassword);
	boolean matches(CharSequence rawPassword, String encodedPassword);
}
```

What password encoding algorithm would you suggest?

--

- BCryptPasswordEncoder - default & recommended
- SCryptPasswordEncoder
- StandardPasswordEncoder - SHA256
- Pbkdf2PasswordEncoder

Neither of those takes salt. Salt is better random. This is why org.springframework.security.authentication.encoding.PasswordEncoder was deprecated 

---

## Web configuration


```java
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Bean
	public UserDetailsService userDetailsService() throws Exception {
		InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
		manager.createUser(User.withUsername("user").password("password").roles("USER").build());
		return manager;
	}
}
```

Require authentication to every URL in your application

- Generate a login form for you
- Registers UserDetailsService
- Allow the user to logout
- CSRF attack prevention
- Session Fixation protection
- Security Header integration

Not yet ON.

---

## Web servlet container without Spring

WebApplicationInitializer - fires up Spring on container start 

Now we register our security configuration

```java
import org.springframework.security.web.context.*;

public class SecurityWebApplicationInitializer
	extends AbstractSecurityWebApplicationInitializer {

	public SecurityWebApplicationInitializer() {
		super(WebSecurityConfig.class);
	}
}
```

---

## Web servlet container with Spring Core

Yeah, sure, but let's just skip to Spring Boot, shall we?


---

## Spring Boot & Security

> If Spring Security is on the classpath then web applications will be secure by default with ‘basic’ authentication on all HTTP endpoints. 

Default username: "user"

Check out logs on start for password

---

## Configuring security in boot

```java
@EnableWebSecurity
class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http); //security per resource      
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web); //filters, interceptors etc
    }

    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(
                User.withUsername("user")
                    .password("password")
                    .roles("USER")
                    .build());
        return manager;
    }
}
```

---

Do not forget to set permitAll on a login form

```java
protected void configure(HttpSecurity http) throws Exception {
	http
		.authorizeRequests()                                                                
			.antMatchers("/resources/**", "/signup", "/about").permitAll()                  
			.antMatchers("/admin/**").hasRole("ADMIN")                                      
			.antMatchers("/db/**").access("hasRole('ADMIN') and hasRole('DBA')")            
		.anyRequest().authenticated() //order is important
			.and()
		.formLogin()
			.loginPage("/login")
			.permitAll();
}
```

What would happen if we have started with

```java
.anyRequest().authenticated()
```

---

## Custom DSL

If you are making a lib with custom filters etc. (quite popular, for example to connect to your SSO), you can create your own Domain Specific Language

```java
@EnableWebSecurity
class Config extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.apply(customDsl())
				.flag(true)
				.and()
			...;
	}
}
```

---

```java
public class MyCustomDsl 
		extends AbstractHttpConfigurer < CorsConfigurerMyCustomDsl, HttpSecurity > {
	
	private boolean flag;
	
	@Override
	public void configure(H http) throws Exception {
		MyFilter myFilter = new MyFilter(flag);
		http.addFilterBefore(myFilter, UsernamePasswordAuthenticationFilter.class);
	}

	public MyCustomDsl flag(boolean value) {
		this.flag = value;
		return this;
	}

	public static MyCustomDsl customDsl() {
		return new MyCustomDsl();
	}
}
```

---

## Method security

```java
@EnableGlobalMethodSecurity(
        prePostEnabled = true, //@PreFilter, @PreAuthorize, 
        					   //@PostFilter, @PostAuthorize
        jsr250Enabled = true,
        securedEnabled = true, //@Secured
        proxyTargetClass = true)
```

---

@Secured gives you a few predetermined options

```java
@Secured("IS_AUTHENTICATED_ANONYMOUSLY")
public Account readAccount(Long id);

@Secured("IS_AUTHENTICATED_ANONYMOUSLY")
public Account[] findAccounts();

@Secured("ROLE_TELLER") //any role name
public Account post(Account account, double amount);
```

--

We will use prePost security as it is the most advanced (SpEL)

```java
@PreAuthorize("isAnonymous()")
public Account readAccount(Long id)

@PreAuthorize("hasAuthority('ROLE_TELLER')")
public Account post(Account account, double amount);

@PreAuthorize("hasPermission(#contact, 'admin')")
public void deletePermission(Contact contact, Sid recipient, Permission permission);

@PreAuthorize(@articleRules.isDraftArticleAuthor(#articleId, authentication.name)")
public ArticleQueryDto deleteArticle(String articleId)
```

---

## Expression-Based Access Control (SpEL)

```
hasRole([role])
hasAnyRole([role1,role2])
hasAuthority([authority])
hasAnyAuthority([authority1,authority2])
principal
authentication
permitAll
denyAll
isAnonymous()
isRememberMe()
isAuthenticated()
isFullyAuthenticated()

and or etc. 
"hasRole('role1') and isRememberMe()" 
```

---

## Decision making

AccessDecisionManager - makes the decision if to allow or not. Has a list of AccessDecisionVoters

Each voter can vote: ACCESS_ABSTAIN, ACCESS_DENIED, ACCESS_GRANTED

3 types of managers

- Affirmative - will grant access if one or more ACCESS_GRANTED votes were received
- Consensus - consensus of non-abstain votes
- Unanimous - expects unanimous ACCESS_GRANTED votes in order to grant access

RoleVoter - will vote to grant access if there is a GrantedAuthority which returns a String starting with ROLE_

AuthenticatedVoter - used to differentiate between anonymous, fully-authenticated and remember-me authenticated users

---

## Security models

- Roles/Groups
- ACL
- Business Rules

---

### Roles

```java
.antMatchers("/db/**").access("hasRole('ADMIN') and hasRole('DBA')")

@Secured("ROLE_TELLER")

@PreAuthorize("hasAuthority('ROLE_TELLER')")
```

role = "ROLE_" + authority

Very popular, you will probably use it anyway

Nicely integrated with LDAP or AD groups

Primitve. Limited to static has-role: yes/no

---

## ACLs

Access-Control on domain objects

```
hasPermission(Object target, Object permission)
```

Returns true if the user has access to the provided target for the given permission. For example, hasPermission(article, 'read')

--

```
hasPermission(Object targetId, String targetType, Object permission)
```

Returns true if the user has access to the provided target for the given permission. For example, hasPermission(1, 'com.example.domain.Message', 'read')

--

Not very usable because of the nightmare of GUI that defines it

In my 12 years of Spring history, I've been asked to implement it TWICE. Both times customer asked for it to be removed, after realizing how much work on his side it requires in administration

The cost of usable GUI was huge

---

## Business rules

```java
@PreAuthorize("#n == authentication.name")
Contact findContactByName(@Param("n") String name);
```

--

If JDK 8 was used to compile the source with the -parameters argument and Spring 4+ is being used, then the standard JDK reflection API is used to discover the parameter names

```javs
@PreAuthorize("#contact.name == authentication.name")
public void doSomething(Contact contact);
```

--

We can also filter in-and-out data

```java
@PreAuthorize("hasRole('USER')")
@PostFilter("hasPermission(filterObject, 'read') 
			 or hasPermission(filterObject, 'admin')")
public List<Contact> getAll();
```

filterObject refers to the current object in the collection

---

## Freedom in business rules

```java
@PreAuthorize(IS_EDITOR_OR_ADMIN + 
	" or @articleRules.isDraftArticleAuthor(#articleId, authentication.name)")
public ArticleQueryDto deleteArticle(String articleId) {...}

class ArticleRules {
    private ArticleRepository repository;

    ArticleRules(ArticleRepository repository) {
        this.repository = repository;
    }

    public boolean isDraftArticleAuthor(String articleId, String createdBy) {
        Article article = repository.findOne(checkNotNull(articleId));
        return article != null 
        	&& createdBy != null 
        	&& article.isCreatedBy(createdBy) 
        	&& article.isDraft();
    }

```


---

## Freedom in business rules

```java
@ArticleRules.CanDelete
public ArticleQueryDto deleteArticle(String articleId) {...}

class ArticleRules {
    @Target({ElementType.METHOD}) @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @ArticleRules.DraftArticleAuthorOrEditorOrAdmin
    @interface CanDelete {}

    @Target({ElementType.ANNOTATION_TYPE}) @Retention(RetentionPolicy.RUNTIME) 
    @Inherited
    @PreAuthorize(IS_EDITOR_OR_ADMIN + " 
    	or @articleRules.isDraftArticleAuthor(#articleId, authentication.name)")
    @interface DraftArticleAuthorOrEditorOrAdmin {}

    private ArticleRepository repository;

    ArticleRules(ArticleRepository repository) {
        this.repository = repository;
    }

    public boolean isDraftArticleAuthor(String articleId, String createdBy) {
        Article article = repository.findOne(checkNotNull(articleId));
        return article != null 
        		&& createdBy != null 
        		&& article.isCreatedBy(createdBy) 
        		&& article.isDraft();
    }
```

---

## Security headers

HTTP Strict Transport Security for secure requests

X-Content-Type-Options integration

Cache Control (can be overridden later by your application to allow caching of your static resources)

X-XSS-Protection integration

X-Frame-Options integration to help prevent Clickjacking

---

## Tests

```java
@WithMockUser //above test method
```

- The Authentication that is populated in the SecurityContext is of type UsernamePasswordAuthenticationToken
- The principal on the Authentication is Spring Security’s User object
- The User will have the username of "user", the password "password", and a single GrantedAuthority named "ROLE_USER" is used.

--

```java
@WithMockUser("customUsername")

@WithMockUser(username="admin",roles={"USER","ADMIN"})

@WithMockUser(username = "admin", authorities = { "ADMIN", "USER" })

@WithAnonymousUser

@WithUserDetails("customUsername") //get user from your UserDetailsService

@WithSecurityContext //if you want custom Authentication object
```

--

You can also do all of that on mockMvc, but do not bother

```java
mvc.perform(get("/").with(user("user")))
```

---

## CSRF Token

Cross Site Request Forgery (CSRF)

> each request requires a randomly generated token as an HTTP parameter. When a request is submitted, the server must look up the expected value for the parameter and compare it against the actual value in the request. If the values do not match, the request should fail.

You incude a token on each get, and you expect that token from each POST/PUT/DELETE

--

On by default

--

In tests

```java
mvc
	.perform(
		post("/").with(csrf())
	)
```

---

## CORS

Cross-origin resource sharing (CORS) is a W3C specification implemented by most browsers that allows you to specify in a flexible way what kind of cross domain requests are authorized, instead of using some less secured and less powerful hacks like IFRAME or JSONP.

```java
@CrossOrigin(origins = "http://domain2.com", maxAge = 3600)
@RestController
public class AccountController {
	...
}
```

Or globally

```java
@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter {
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/api/**")
			.allowedOrigins("http://domain2.com")
			.allowedMethods("PUT", "DELETE")
			.allowedHeaders("header1", "header2", "header3")
			.exposedHeaders("header1", "header2")
			.allowCredentials(false).maxAge(3600);
	}
}
```

---
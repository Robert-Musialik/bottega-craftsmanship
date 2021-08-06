class: center, middle

# Spring Data

by Jakub Nabrdalik

---

##  Support?

JDBC JPA R2DBC MongoDB NEO4J Redis Hadoop Gemfire Rest Solr...
 
## For tests

spring-boot-starter-data-jpa + HSQL/H2/Derby on classpath = embedded in-memory database

## For production with single DB

```properties
spring.datasource.url=jdbc:mysql://localhost/test 
spring.datasource.username=dbuser 
spring.datasource.password=dbpass 
spring.datasource.driver-class-name=com.mysql.jdbc.Driver 
spring.jpa.hibernate.ddl-auto=create-drop
```

Or just register your own DataSource as a bean

Don’t forget to have the driver on the classpath!

---

## Custom hibernate params

To pass params to hibernate entity manager, just start it with

```properties
spring.jpa.properties.hibernate:
```

application.properties

```properties
spring.jpa.properties.hibernate.globally_quoted_identifiers=true
```

hibernate sees:

```properties
hibernate.globally_quoted_identifiers=true
```

---

## Common CRUD needs

Save an entity

Return the entity identified by the given id

Return all entities

Return the number of entities

Delete the given entity

Indicate whether an entity with the given id exists

---

## Repository

```java
public interface Repository<T, ID extends Serializable> {}

public interface CrudRepository<T, ID> extends Repository<T, ID> {
	<S extends T> S save(S entity);
	<S extends T> Iterable<S> saveAll(Iterable<S> entities);
	Optional<T> findById(ID id);
	boolean existsById(ID id);
	Iterable<T> findAll();
	Iterable<T> findAllById(Iterable<ID> ids);
	long count();
	void deleteById(ID id);
	void delete(T entity);
	void deleteAll(Iterable<? extends T> entities);
	void deleteAll();
}
```

---

## Pageable Repository

```java
public interface PagingAndSortingRepository<T, ID extends Serializable>
		extends CrudRepository<T, ID> {
	Iterable<T> findAll(Sort sort);
	Page<T> findAll(Pageable pageable);
}

public interface Pageable {
	boolean isPaged()
	boolean isUnpaged();
	int getPageNumber();
	int getPageSize();
	long getOffset();
	Sort getSort();
	Sort getSortOr(Sort sort);
	Pageable next();
	Pageable previousOrFirst();
	Pageable first();
	boolean hasPrevious();
	Optional<Pageable> toOptional();
}

public class PageRequest extends AbstractPageRequest {
	public PageRequest(int page, int size) {...}
}

public abstract class AbstractPageRequest implements Pageable, Serializable ...

Page<User> users = repository.findAll(new PageRequest(1, 20));
```

---

## And finally - JPA Repository

```java
public interface JpaRepository<T, ID extends Serializable> extends
		PagingAndSortingRepository<T, ID>, QueryByExampleExecutor<T> {
	List<T> findAll();
	List<T> findAll(Sort sort);
	List<T> findAllById(Iterable<ID> ids);
	<S extends T> List<S> saveAll(Iterable<S> entities);
	void flush();
	<S extends T> S saveAndFlush(S entity);
	void deleteInBatch(Iterable<T> entities);
	void deleteAllInBatch();
	T getOne(ID id);
	<S extends T> List<S> findAll(Example<S> example);
	<S extends T> List<S> findAll(Example<S> example, Sort sort);
}
```

---

## How to set it up?

Create your interface

```java
interface SomeEntityRepository extends JpaRepository<SomeEntity, Long> {
}
```

Register your repositories in Spring

assuming you have transactionManager and entityManagerFactory (which you get from boot by default)

```java
@EnableJpaRepositories("eu.solidcraft.starter.domain")
```

Inject and start using your new repository

```java
List<SomeEntity> entities = someEntityRepository.findAll();
```

---

## But all I have is an interface...

Spring will create the implementation for you.

Because it’s boring.

---

## Which repository to extend?

You can extend any repository, if you need CRUD and Paging, use JpaRepository.

Do not always extend JpaRepository blindly.

It's easier to add a method when you need it, than to remove one

---

## Not extending an interface?

Instead of extending Repository, you can annotate your interface with @RepositoryDefinition

Or you can annotate your interface, that extends Repository, with
@NoRepositoryBean. This way you can create abstracts (base for your repos).

---

## How to fine-tune your repository

```java
@Entity
class SomeEntity {
	@NotNull private String username;
	…
}

interface SomeEntityRepository extends JpaRepository<SomeEntity, Long> {
	List<SomeEntity> findByUsername(String username)
}
```

---

## Query method examples

```java
List<Person> findByEmailAddressAndLastname(EmailAddress emailAddress, String lastname);

// Enables the distinct flag for the query
List<Person> findDistinctPeopleByLastnameOrFirstname(String lastname, String firstname);
List<Person> findPeopleDistinctByLastnameOrFirstname(String lastname, String firstname);

// Enabling ignoring case for an individual property
List<Person> findByLastnameIgnoreCase(String lastname);

// Enabling ignoring case for all suitable properties
List<Person> findByLastnameAndFirstnameAllIgnoreCase(String lastname, String firstname);

// Enabling static ORDER BY for a query
List<Person> findByLastnameOrderByFirstnameAsc(String lastname);
List<Person> findByLastnameOrderByFirstnameDesc(String lastname);
```

---

## What does it understand?

property traversals

AND | OR

operators (Between, LessThan, Like, In)

IgnoreCase (for each property)

OrderBy (Asc | Desc)

FirstX, TopY

more...

---

## Nested properties

```Java
findByAddressZipCode(ZipCode zipCode);

//check if property exists
person.addressZipCode

//split camel case and find
person.addressZip.Code
person.address.zipCode
person.address.zip.code
```

---

## Nested properties

what if we have both?

```Java
person.address.zipCode
person.address.zip.code
```

--

tell it directly

```Java
findByAddress_ZipCode(ZipCode zipCode);
```

---

## Query creation anatomy

find…By, read…By, query…By, count…By, and get…By

first “By” marks the moment of parsing

---

## Returning nothing

As of Spring Data 2.0, repository CRUD methods that return an individual aggregate instance use Java 8’s Optional to indicate the potential absence of a value. Besides that, Spring Data supports returning the following wrapper types on query methods

com.google.common.base.Optional

scala.Option

io.vavr.control.Option

You can also use no wrapper, and get null in the effect

---

## Returning a stream

```Java
@Query("select u from User u")
Stream<User> findAllByCustomQueryAndStream();

Stream<User> readAllByFirstnameNotNull();

@Query("select u from User u")
Stream<User> streamAllPaged(Pageable pageable);
```

A Stream potentially wraps underlying data store-specific resources and must, therefore, be closed after usage. 

You can either manually close the Stream by using the close() method or by using a Java 7 try-with-resources block, as shown in the following example:

```Java
try (Stream<User> stream = repository.findAllByCustomQueryAndStream()) {
  stream.forEach(…);
}
```

---

## Async queries

```Java
@Async
Future<User> findByFirstname(String firstname);               

@Async
CompletableFuture<User> findOneByFirstname(String firstname); 

@Async
ListenableFuture<User> findOneByLastname(String lastname);
```


---

## Custom JPA queries

```java
public interface UserRepository extends JpaRepository<User, Long> {
	@Query("select u from User u where u.emailAddress = ?1")
	User findByEmailAddress(String emailAddress);

	@Query("select u from User u where u.firstname like %?1")
	List<User> findByFirstnameEndsWith(String firstname);

	@Query(
		"select u from User u where u.firstname = :firstname or " +
		"u.lastname = :lastname")
	User findByLastnameOrFirstname(
		@Param("lastname") String lastname,
		@Param("firstname") String firstname);
}
```

--

You can load data directly into DTOs (no Entity required):

```java
@Query("SELECT NEW com.company.PublisherInfo(pub.id, pub.revenue, mag.price)" +
		" FROM Publisher pub JOIN pub.magazines mag " +
		" WHERE mag.price > ?1")
PublisherInfo findByEmailAddress(BigDecimal price);
```

---

## Custom Native queries

```java
public interface UserRepository extends JpaRepository<User, Long> {
	@Query(
		value = "SELECT * FROM USERS WHERE EMAIL_ADDRESS = ?1",
		nativeQuery = true)
	User findByEmailAddress(String emailAddress);
}
```

---

## Special parameters

```java
Page<User> findByLastname(String lastname, Pageable pageable);

Slice<User> findByLastname(String lastname, Pageable pageable);

List<User> findByLastname(String lastname, Sort sort);

List<User> findByLastname(String lastname, Pageable pageable);
```

---

## Slice vs Page

```java
public interface Slice<T> extends Iterable<T> {
	int getNumber();
	int getSize();
	int getNumberOfElements();
	List<T> getContent();
	boolean hasContent();
	Sort getSort();
	boolean isFirst();
	boolean isLast();
	boolean hasNext();
	boolean hasPrevious();
	Pageable getPageable();
	Pageable nextPageable();
	Pageable previousPageable();
	<U> Slice<U> map(Function<? super T, ? extends U> converter);}
```

A Page knows about the total number of elements and pages available. It does
so by the infrastructure triggering a count query to calculate the overall
number.

As this might be expensive depending on the store used, Slice can be used as
return instead. A Slice only knows about whether there’s a next Slice
available which might be just sufficient when walking though a larger result set.

---

## Sort

Build in Pageable & Slice

You can have only sorting, if you wish.

```java
public static Sort by(String... properties) 

public static Sort by(List<Order> orders)

public static Sort by(Direction direction, String... properties)

public Sort and(Sort sort)

public Order(Direction direction, String property)

public static enum Direction { ASC, DESC; ....

```

---

## Custom logic in repository

Step 1: define your custom method in an interface


```java
interface UserRepositoryCustom {
	public void someCustomMethod(User user);
}
```

Step 2: implement it

```java
class UserRepositoryCustomImpl implements UserRepositoryCustom {
	public void someCustomMethod(User user) {
		// Your custom implementation
	}
}
```

Step 3: declare your interface extending both your custom, and Spring Data repository

```java
public interface UserRepository
	extends CrudRepository<User, Long>, UserRepositoryCustom {
	// Declare query methods here
}
```

---

## Custom logic in repository

If you use namespace configuration, the repository infrastructure tries to autodetect
custom implementations by scanning for classes below the package we found a
repository in. 

These classes need to follow the naming convention of appending the
namespace element’s attribute repository-impl-postfix to the found repository
interface name. 

This postfix defaults to Impl.

But you can change it:

```xml
<repositories base-package="com.acme.repository" repository-impl-postfix="Customization" />
```

--

If your custom implementation bean needs special wiring, you simply declare
the bean and name it after the conventions just described.

The infrastructure will then refer to the manually defined bean definition by
name instead of creating one itself.

---

## What about Java 8 default methods?

Only if all you need is access to other repository methods.

Because you have no access to any state of the instance, you just have an interface.

```java
default Owner getSafeCopy(Long id) {
	Owner owner = findById(id);
	return new Owner(owner).withAccountNumber(“stripped”);
}
```

---

## Querydsl

http://www.querydsl.com/

com.querydsl:querydsl-jpa + com.querydsl:querydsl-apt + apt-maven-plugin

```Java
public interface QuerydslPredicateExecutor<T> {
	Optional<T> findOne(Predicate predicate);
	Iterable<T> findAll(Predicate predicate);
	Iterable<T> findAll(Predicate predicate, Sort sort);
	Iterable<T> findAll(Predicate predicate, OrderSpecifier<?>... orders);
	Iterable<T> findAll(OrderSpecifier<?>... orders);
	Page<T> findAll(Predicate predicate, Pageable pageable);
	long count(Predicate predicate);
	boolean exists(Predicate predicate);
}

interface UserRepository extends 
	CrudRepository<User, Long>, QuerydslPredicateExecutor<User> {}
```

Now you can do type safe queries

```Java
Predicate predicate = QUser.user.firstname.equalsIgnoreCase("dave")
	.and(user.lastname.startsWithIgnoreCase("mathews"));

userRepository.findAll(predicate);
```
---

## Prepopulating the database

```java
@Entity
class SomeEntity {
	@Id
	@SequenceGenerator(name = "SomeSequence",
	sequenceName = "SEQ_SOME_PK", initialValue=10000)
	
	@GeneratedValue(generator = "SomeSequence")
	private Long id;
	
	@NotNull
	private String username;
	
	@NotNull
	private BigDecimal someAmount;
	
	@NotNull
	private Date someDate;
	…
}
```

---

## Prepopulating the database


```json
[
	{
		"_class" : "eu.solidcraft.starter.domain.some.SomeEntity",
		"id" : 10000,
		"username" : "test",
		"someAmount": 100,
		"someDate": "2009-04-12T20:44:55"
	},
	{
		"_class" : "eu.solidcraft.starter.domain.some.SomeEntity",
		"id" : 10001,
		"username" : "test",
		"someAmount": 50,
		"someDate": "2009-04-12T20:44:55"
	},
	{
		"_class" : "eu.solidcraft.starter.domain.some.SomeEntity",
		"id" : 10002,
		"username" : "test",
		"someAmount": 30,
		"someDate": "2009-04-12T20:44:55"
	}
]
```

---

## Prepopulating the database

```java
@Configuration
class ApplicationConfig {
	@Bean
	public JacksonRepositoryPopulatorFactoryBean repositoryPopulator() {
		Resource sourceData = new ClassPathResource("test-data.json");
		JacksonRepositoryPopulatorFactoryBean factory = 
			new JacksonRepositoryPopulatorFactoryBean();
		factory.setObjectMapper(…); //custom ObjectMapper if needed
		factory.setResources(new Resource[] { sourceData });
		return factory;
	}
}
```

--

Seriously... just use liquibase of flyway

---

## Auditing

@CreatedBy, @LastModifiedBy, @CreatedDate, @LastModifiedDate


```java
class Customer {
	@CreatedBy
	private User user;

	@CreatedDate
	private DateTime createdDate;
}
```

or implement Auditable

or extend AbstractAuditable

---

## Auditing

```java
class SpringSecurityAuditorAware implements AuditorAware<User> {
	
	public User getCurrentAuditor() {
		Authentication authentication = 
			SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			return null;
		}
		return ((MyUserDetails) authentication.getPrincipal()).getUser();
	}
}
```

---

## JPA 2.1 support

@Modyfing - the query will change DB state

@QueryHints

@EntityGraph & @NamedEntityGraph

@NamedStoredProcedureQuery &

@Procedure

---

## Criteria… errr… Specification

Criteria - Hibernate, JPA 2

Specification - Spring

```java
public interface CustomerRepository 
	extends CrudRepository<Customer, Long>, JpaSpecificationExecutor { 
	… 
}
```

and you get List<T> findAll(Specification<T> spec) in your repo

```java
public interface Specification<T> {
	Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder);
}
```

---

## Criteria… errr… Specification

Pays back when you have to combine them

```java
List<Customer> customers = customerRepository.findAll(
	where(isLongTermCustomer()).or(hasSalesOfMoreThan(amount)));
```

And combine them you can

```java	
public class Specifications<T> implements Specification<T> {
	private final Specification<T> spec;
	private Specifications(Specification<T> spec)
	public static <T> Specifications<T> where(Specification<T> spec)
	public Specifications<T> and(final Specification<T> other)
	public Specifications<T> or(final Specification<T> other)
	public static <T> Specifications<T> not(final Specification<T> spec)
	public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder)
}
```

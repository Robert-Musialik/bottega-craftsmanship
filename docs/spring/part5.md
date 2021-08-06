class: center, middle

# Transaction management

---

## Declarative transaction management

```java
@Transactional
public class RentFacade {

    public OperationsOutcomeDto rent(@NonNull RentRequestDto rentRequestDto) ...

	@Transactional(readOnly = true)
    public Page<RentedFilmDto> getMyRents(@NonNull Pageable pageable) ...
}
```

---

## Transaction attributes: Isolation

What the transaction can see

```java
public enum Isolation {
	ISOLATION_DEFAULT 
	ISOLATION_READ_UNCOMMITTED 
	ISOLATION_READ_COMMITTED 
	ISOLATION_REPEATABLE_READ 
	ISOLATION_SERIALIZABLE
}
```

Default = Use the default isolation level of the underlying datastore

---

## Transaction attributes: Propagation

What if transaction already exists

```
REQUIRED - Support a current transaction, create a new one if none exists

SUPPORTS - Support a current transaction, execute non-transactionally if none exists

MANDATORY - Support a current transaction, throw an exception if none exists

REQUIRES_NEW - Create a new transaction, suspend the current transaction if one exists

NOT_SUPPORTED - Execute non-transactionally, suspend the current transaction if one exists

NEVER - Execute non-transactionally, throw an exception if a transaction exists

NESTED - Execute within a nested transaction if a current transaction exists
```

---

## Other transaction attributes

Timeout

Read-only status

rollbackFor + rollbackForClassName 

noRollbackFor + noRollbackForClassName

---

## Default @Transactional attributes

Propagation = PROPAGATION_REQUIRED. 

Isolation = ISOLATION_DEFAULT. 

Transaction = read/write.

Transaction timeout = default timeout of the underlying transaction system, or to none if timeouts are not supported.

Any RuntimeException triggers rollback, and any checked Exception does not.

---

## Where do we start a transaction

All repository methods are transactional by default (queries with readOnly flag).
 
*Open session in view* - Transaction per Request (OpenSessionInViewInterceptor and OpenSessionInViewFilter)

*Transaction on Controllers*

*Transaction on Domain Services*

*Transaction on Facades*

Which one is better?

---

### Open session in view

Advantages
- you can do lazy loading in view layer you can be lazy

Disadvantages
- lazy loading is very bad for you (performance) coupling view with sesion
- if you get an exception, you are out of luck many more, depending who you ask
 
---

### Transaction on Controllers

Advantages
- popular in Anemic model makes you eager load more

Disadvantages
- anemic model is often bad for you (complexity, maintainability)
- why is your presentation layer handling database?

---

###  Transaction on Domain Services

Advantages
- popular in both Rich and Anemic model 
- makes you eager load even more 
- looks like it is the logical thing to do for many business algorithms (partial rollback etc.)

Disadvantages
- good luck analysing where/what/when
- cannot rollback in integration tests if you commit in domain code
- you can end up with services just for transactions

---

### Transaction on a module Facade

Advantages
- easy to grasp and reason about (a facade method is usually a use case)
- easy to test if you Support a running transaction

Disadvantages
- might be sometimes too simple for the flow

---

### Where do we start a transaction

By default I recommend the facade

If you need to (business requirement) you can do it on domain services: just remember if you do not support existing transaction, it will be harder to run tests in parallel

Never use it on Controllers or Open Session in View

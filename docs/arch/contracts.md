class: center, middle

# Contracts

by Jakub Nabrdalik

---

## Biodegradation of architecture

Entropy increases with time, unless you work on it

--

There are 3 approaches to keeping the code/architecture clean

- hire only senior devs with high self-discipline
- teach architecture
- create automatically tested contracts

---

## Contracts for a single app

www.archunit.org

---

```java
@AnalyzeClasses(packages = "com.tngtech.archunit.example")
public class LayerDependencyRulesTest {

    @ArchTest
    static final ArchRule services_should_not_access_controllers =
            noClasses().that().resideInAPackage("..service..")
                    .should().accessClassesThat()
                    .resideInAPackage("..controller..");

    @ArchTest
    static final ArchRule persistence_should_not_access_services =
            noClasses().that().resideInAPackage("..persistence..")
                    .should().accessClassesThat()
                    .resideInAPackage("..service..");

    @ArchTest
    static final ArchRule services_should_only_be_accessed_by_controllers_or_other_services =
            classes().that().resideInAPackage("..service..")
                    .should().onlyBeAccessed()
                    .byAnyPackage("..controller..", "..service..");


    @ArchTest
    static final ArchRule services_should_not_depend_on_controllers =
            noClasses().that().resideInAPackage("..service..")
                    .should().dependOnClassesThat()
                    .resideInAPackage("..controller..");

    @ArchTest
    static final ArchRule persistence_should_not_depend_on_services =
            noClasses().that().resideInAPackage("..persistence..")
                    .should().dependOnClassesThat()
                    .resideInAPackage("..service..");
```

---

```java
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.GeneralCodingRules.ACCESS_STANDARD_STREAMS;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING;

@AnalyzeClasses(packages = "com.tngtech.archunit.example")
public class CodingRulesTest {

    @ArchTest
    private final ArchRule NO_ACCESS_TO_STANDARD_STREAMS = 
      NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;

    @ArchTest
    private void no_access_to_standard_streams_as_method(JavaClasses classes) {
        noClasses().should(ACCESS_STANDARD_STREAMS).check(classes);
    }

    @ArchTest
    private final ArchRule NO_GENERIC_EXCEPTIONS = 
      NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS;

    @ArchTest
    private final ArchRule NO_JAVA_UTIL_LOGGING = 
      NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING;
}
```

You can check 
Package Dependency, Class Dependency, Class and Package Containment, Inheritance Checks, Annotation, Layer, Cycle...

---

## Consumer driven contracts (between apps)

[Pact](http://pact.io)

[Spring cloud contract](https://cloud.spring.io/spring-cloud-contract/)

--

Contract testing is a way to ensure that services (such as an API provider and a client) can communicate with each other.

--

> Assume that Loan Issuance is a client to the Fraud Detection server. In the current sprint, we must develop a new feature: if a client wants to borrow too much money, then we mark the client as a fraud.

---

## Contract in producer's repo

Team Loan Issuance
- Start doing TDD by writing a test for your feature.
- Write the missing implementation.
- Clone the Fraud Detection service repository locally.
- Define the contract locally in the repo of Fraud Detection service.
- Publish to local maven cache (mvn install)
- Add the Spring Cloud Contract Verifier plugin.
- Run the integration tests.
- File a pull request.

--

Team Fraud Detection
- Create an initial implementation.
- Take over the pull request.
- Write the missing implementation.
- Deploy your app & publish contract to maven (public)

--

Team Loan Issuance
- Point your verifier to the public contract 
- Merge to master

---

## Contract in shared contract's repo

Team Loan Issuance + Team Fraud Detection
- Discuss the contract, 
- Pull shared contract repo
- Add the contract to shared repo
- Publish to shared repo (new contract jar version)

--

Team Loan Issuance
- Start doing TDD by writing a test for your feature.
- Write the missing implementation.
- Add the Spring Cloud Contract Verifier plugin
- Run the integration tests

--

Team Fraud Detection
- Update your contracts jar to latest version
- Write the missing implementation.

---

## How does a contract DSL look like

```groovy
package contracts

org.springframework.cloud.contract.spec.Contract.make {
   request { 
      method 'PUT' 
      url '/fraudcheck' 
      body([ 
            "client.id": $(regex('[0-9]{10}')),
            loanAmount: 99999
      ])
      headers { 
         contentType('application/json')
      }
   }
   response { 
      status OK() 
      body([ 
            fraudCheckStatus: "FRAUD",
            "rejection.reason": "Amount too high"
      ])
      headers { 
         contentType('application/json')
      }
   }
}
```

---

## You can use YAML

If you don't like life

```yaml
description: Some description
name: some name
priority: 8
ignored: true
request:
  url: /foo
  queryParameters:
    a: b
    b: c
  method: PUT
  headers:
    foo: bar
    fooReq: baz
  body:
    foo: bar
  matchers:
    body:
      - path: $.foo
        type: by_regex
        value: bar
    headers:
      - key: foo
        regex: bar
```
---

```yaml
response:
  status: 200
  headers:
    foo2: bar
    foo3: foo33
    fooRes: baz
  body:
    foo2: bar
    foo3: baz
    nullValue: null
  matchers:
    body:
      - path: $.foo2
        type: by_regex
        value: bar
      - path: $.foo3
        type: by_command
        value: executeMe($it)
      - path: $.nullValue
        type: by_null
        value: null
    headers:
      - key: foo2
        regex: bar
      - key: foo3
        command: andMeToo($it)

```

---

## Consumer driven contracts caveats

It makes sense only if everyone follow the method

It checks only semantic contract (if we can talk understand each other), not whether what we say makes sense

The tooling and docs is a bit... confusing

There is support for Spring Cloud Streams (but hey, maybe use avro?)

You can do it manually... sort of.. kind of...

The tool helps a lot working with teams you don't trust
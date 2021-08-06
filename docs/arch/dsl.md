class: center, middle

# Domain specific language & API design

by Jakub Nabrdalik

---

## Fluent API a.k.a. Interface driven interactions

> implementation of an object oriented API that aims to provide more readable code

> normally implemented by using method chaining to relay the instruction context of a subsequent call

---

## Fluent API example 1

```Java
Author author = AUTHOR.as("author");
create.selectFrom(author)
      .where(exists(selectOne()
                   .from(BOOK)
                   .where(BOOK.STATUS.eq(BOOK_STATUS.SOLD_OUT))
                   .and(BOOK.AUTHOR_ID.eq(author.ID))));
```

---

## Fluent API example 2

```Java
Vacation vacation = vacation().starting("10/09/2007")
                               .ending("10/17/2007")
                               .city("Paris")
                               .hotel("Hilton")
                               .airline("United")
                               .flight("UA-6886"); 
```

---

## Fluent API example 3

```Java
return osOfferItems.stream()
        .filter(item -> variantToOffers.getOfferIds().contains(item.getId()))
        .map(osOfferItem -> createOfferWithSeller(sellerIdToSeller, osOfferItem))
        .sorted(offerSorter::compareOffers)
        .collect(toList());
```

This is a good API for streams, not a good API for domain logic

---

## See if you can tell what this does

```java
interface ReadableArticle {
    ArticleQueryDto dto();
}
```

--

```java
interface UpdatableArticle {
    void update(UpdateArticleDto updateArticleDto);
    void updateCategories(List<CategoryPathDto> categories);
}
```

--

```java
interface DraftArticle extends ReadableArticle, UpdatableArticle {
    DeletedArticle delete();
    SubmittedArticle submit();
}
```

--

```java
interface SubmittedArticle extends ReadableArticle, UpdatableArticle {
    RejectedArticle reject(ApplicationEventPublisher eventPublisher);
    PublishedArticle publish(CanonicalUrlGenerator canonicalUrlGenerator);
}
```

--

```java
interface DeletedArticle extends ReadableArticle {
    DraftArticle restore();
}
```

---

```java
interface RejectedArticle extends ReadableArticle {
    SubmittedArticle submit();
}
```

--

```java
interface PublishedArticle extends ReadableArticle, UpdatableArticle {
    SubmittedArticle unpublish();
    PublishedArticle republish(CanonicalUrlGenerator canonicalUrlGenerator);
}
```

--

```java
class Article implements DraftArticle, SubmittedArticle,
    DeletedArticle, RejectedArticle, PublishedArticle {
   ...
}
```

--

Driving state machine changes through interfaces (also a fluent API)


---

## Rules of API

--

1. be fluent, be natural

--

2. one parameter per method

--

3. return yourself except for finishing methods

--

4. you can give more options by using interfaces

---

## Finite type algebra

> In mathematics, logic, and computer science, a type theory is any of a class of formal systems, some of which can serve as alternatives to set theory as a foundation for all mathematics. In type theory, every "term" has a "type" and operations are restricted to terms of a certain type.

---

> Type theory is closely related to (and in some cases overlaps with) type systems, which are a programming language feature used to reduce bugs. 

---

## Algebraic Data Types

> ADT is a type which is represented by several other subtypes
> [Dmitry Zaytsev](https://medium.com/car2godevs/kotlin-adt-74472319962a)

```java
enum DeliveryStatus {
  PREPARING,
  DISPATCHED,
  WAITING_FOR_YOU_TO_LEAVE_HOME_SO_YOU_WILL_MISS_IT
}
```

--

Now we want to add a tracking number.

---

## Wrong approach

```java
@AllArgsConstructor
class DeliveryStatus {
  final Stage stage;
  final String trackingNumber; //only dispatched has this
  
  enum Stage {
    PREPARING,
    DISPATCHED,
    WAITING_FOR_YOU_TO_LEAVE_HOME_SO_YOU_WILL_MISS_IT
  }
}
```

---

## Java limitations

```java
interface Trackable {
   String getTrackingNumber();
}

interface DeliveryStatus() {
}

class Preparing implements DeliveryStatus {}
class Dispatched implements DeliveryStatus, Trackable {...}
class WaitingForYou implements DeliveryStatus {}
```

---

## Kotlin and Scala handle this better

```kotlin
sealed class DeliveryStatus {
  
  object Preparing : DeliveryStatus()
  
  data class Dispatching(
    val trackingNumber: String
  ) : DeliveryStatus()
  
  object Delivered : DeliveryStatus()
}
```

--

Usage with pattern matching

```kotlin
fun showDeliveryStatus(status: DeliveryStatus) {
  return when (status) {
    is Preparing -> showPreparing()
    is Dispatched -> showDispatched(it.trackingNumber) // note that no cast needed!
    is Delivered -> showDelivered()
  }
}
```

All possibilities checked by compiler



---

## DSL

> A domain-specific language (DSL) is commonly described as a computer language targeted at a particular kind of problem and it is not planned to solve problems outside of its domain.

> An internal DSL is created with the main language of an application without requiring the creation (and maintenance) of custom compilers and interpreters.

List of patterns for DSLs: http://martinfowler.com/dslCatalog/

---

## Task: DSL

You are writing a lib for working with excel files via Apache POI.

Create a DSL, that will allow the user to:

- create an xlsx
- create a worksheet
- fill a row with properties from an object
- fill rows with a collection of objects and add header on top of first row
- set cell value
- get cell value
- save xlsx to file
- save xlsx to output stream
- save xlsx to http response (including proper headers)

Make sure that you only allow the user to do things that make sense.

---

## Task: DSL 2

You are writing a microservice that allow to manipulate categories in a eCommerce application.

Create a DSL, that will allow the user to:
- add a category to a tree of categories
- move a category in a tree of categories, into another category

---

To test adding a new category, let's start with declaring a tree

```Groovy
CategoryNode root =
    A (
        B,
        C (E, F),
        D
    )
```

--

```Groovy
def "should add a new category"() {
    given: "user wants to add under C a new category G at position 1 
        AddNewCategoryDto addNewCategory = C + G.at(1)
```

--

```Groovy
    when: "new category is added"
        TreeDto updatedTree = modifyTree(addNewCategory)
```

--

```Groovy    
    then: "updated tree contains new category"
        TreeDto expected = expectedTree(
            A (
                B,
                C (E, G, F),
                D
            )
        )
        updatedTree == expected
}
```

---

WTF is this?

```Groovy
CategoryNode root =
    A (
        B,
        C (E, F),
        D
    )
```

--

Oh, it's a simple method call in groovy

```Groovy
class CategoryNode {
    static final CategoryNode A = new CategoryNode("A") //B,C, etc...
    
    final String id
    final List<CategoryNode> children

    //in groovy, a method without a name on an object is 'call'
    CategoryNode call(CategoryNode... children) {
        return new CategoryNode(id, name, alias, children.toList())
    }
    ...
}
```

---

How to achieve this?

```Groovy
AddNewCategoryDto addNewCategory = C + G.at(1)
```

--

Operator '+' is also a method named 'plus'

```Groovy
class CategoryNode {
    ...

    //overriding + operator in groovy
    AddNewCategoryDto plus(CategoryNode node) {
        return new AddNewCategoryDto(this.alias, node.name, node.alias)
    }

    //and now let's handle the position
    static class CategoryNodeAtPosition {
        CategoryNode node
        int position
    }

    CategoryNodeAtPosition at(int position) {
        return new CategoryNodeAtPosition(this, position)
    }
}    
```


---

How to describe moving a category?

```Groovy
    CategoryNode root =
            A (
                B,
                C (
                    E,
                    F
                ),
                D
            )
```

--

```Groovy
    def "should move category under different category"() {
        when:
            TreeDto updatedTree = modifyTree(B >> F)
```


--

```Groovy
        then:
            TreeDto expected = expectedTree(
                A (
                    C (
                        E,
                        F (
                            B
                        )
                    ),
                    D
                )
            )
            updatedTree == expected
    }

    //The '>>' operator in groovy is just a method called 'rightShift'
```

---

## Summary of DSLs

Domain specifc languages SIMPLIFY development, by limiting ambiguity and improving communication (even with a client!)

Use them for domain, infrastructure, tests, everything that is complex

The more complex things get, the more you need a DSL

Be prepared to refactor DSLs. You never get the domain right the first time

DSLs are easy!
class: center, middle

# Tooling of an architect

by Jakub Nabrdalik

---

## What tools do you use?

---

## Task 1

Take a sheet of paper, and in groups create an architecture for a system described below:

Build a system for car-sharing. Cars have GPS'es that send location every 5 sec. Users search for nearby cars via a mobile app. While rented, a user sees his cost so far (live changes).

- The cost is 0,80 PLN for every km started
- The cost is 0,50 PLN for every minute of travel
- The cost is 0,10 PLN for every minute of parking the car for later use

System sends a summary invoice to the user via email. This is done at the end of the day, from all the costs of that day, for given user.

Expected number of cars: 1k

Expected number of users: 100k

---

## C4 model

Diagrams are maps of your system

Maps do not represent reality, but maps help navigate the reality

Maps are also used for communication

You can zoom in on a map

Maps bring value because they abstract away the detail, and because 1/3 of your brain is dedicated to processing visual input

--

C4 - Context, Container, Component, Class

---

## C1: Context

> your system as a box in the centre, surrounded by its users and the other systems that it interacts with

<img style="width: 100%" src="/img/bigbankplc-SystemContext.png">

---

## C2: Container

> a container is a separately runnable/deployable unit (e.g. a separate process space) that executes code or stores data.

A container could be
- Runnable JAR
- Old fashioned WAR/EAR
- Docker image
- Kubernetes pod/service
- mobile app

---

<img style="width: 100%" src="/img/bigbankplc-Containers.png">

---

## C3: Component

> The Component diagram shows how a container is made up of a number of "components", what each of those components are, their responsibilities and the technology/implementation details

I'd suggest you treat Components as Modules

--

What are properties of a Component/Module?

--

Component/Module
- has a responsibility (for a process, not data)
- has (almost) all layers (vertical slicing): UI-API / Data Access / Logic etc.
- has clerly defined collaborators and APIs
- encapsulates its data (access only via API), no access to module DB
- very much like a microservice candidate
- most likely a Bounded Context (words have their own definitions/classes)
- on JVM often implemented as a separate package, build module or jar

---

## Bad component diagram example

<img style="width: 100%" src="/img/bigbankplc-Components.png">

---

## Better component diagram example (primitive case)

<img style="width: 100%" src="/img/hentai-rental-components.png">

---

class: center, middle

<img style="width: 60%" src="/img/techtribesje-updater-components.png">

---

class: center, middle

<img style="width: 100%" src="/img/techtribesje-core-components.png">

---

## C4: Class Diagram

Just generate them from code if you need to

---

## How to keep diagrams up to date?

Seriously, how do you do it?

--

My suggestions

Use a tool that is close to developers (code), that tracks all the changes (git) and allows for review (pull requests)

Give access to everyone for pull requests, and all to review and discuss. Make people used to working with diagrams

[Code examples are here](https://github.com/structurizr/java/tree/master/structurizr-examples/src/com/structurizr/example)

--

Use architecture diagrams for discussing new features, sprint planning, etc.

--

Make people used to RFC (Request For Comments) by providing them yourself, whenever you want to change or introduce something

---

## Validate the model

How do we find out all the good things and all the bad things about a model?

--

Architecture diagrams in C4 are a static map

To validate a static map it helps to go through all the main flows and see what bad can happen

--

To describe dynamic flows, use sequence diagrams

www.websequencediagrams.com

Again, save code (source of diagrams) in git so that everyone can contribute

---

```
title CDR Export

Pricing -> Roaming CDR out: CDR was created

Roaming CDR out -> Roaming CDR out: Is it roaming CDR?

opt it is roaming CDR
    Roaming CDR out -> Roaming CDR out: Save CDR
    Roaming CDR out -> Roaming Partner: Import CDR
    alt valid credentials
        Roaming Partner --> Roaming CDR out: ok
    else invalid credentials
        Roaming Partner --> Roaming CDR out: not ok
        Roaming CDR out -> Roaming Credentials: Authorization Credentials Expired

        Roaming Credentials -> Roaming Partner: Handshake
        Roaming Partner --> Roaming Credentials: Authorization Credentials

        Roaming Credentials -> Roaming CDR out: New Authorization Credentials
        Roaming CDR out -> Roaming Partner: Import CDR
```

---

.center[![CDR](/img/CDR-Export.png)]


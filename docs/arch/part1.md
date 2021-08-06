class: center, middle

# Designing architecture for modular, distributed, and event driven systems

a pragmatic approach

by Jakub Nabrdalik

---

# whoami

jakub nabrdalik

solution architect & dev @bottega

details: https://nabrdalik.dev/

linkedin: https://pl.linkedin.com/in/jnabrdalik

18 years in software dev as a Solution Architect, Software Architect, Team Leader, Head of IT, Developer, Analyst

banking, fintech, ecommerce, telco, energy, startups

From 3 man-month systems to 200+ dev monoliths to 700+ microservices

100+ talks, 100+ commercial workshops

ex WJUG, WGUG leader, in programme committee 4 DevoxxPL, etc.

---

# who are you?

What's your name?

What do you do?

What's your experience with 
- Architecture
- C4 model
- UML
- Distributed systems
- Microservices
- Hexagonal architecture / Ports&Adapters
- Multitenancy
- Domain Driven Design
- CQRS
- Event Sourcing
- DSLs

What do you expect from this training?

---

## Our plan

### Part 1

1. [Tooling of an architect](tooling.html)
   1. C4 model and notation
   1. Websequencediagrams
1. [System of systems](systemofsystems.html)
   1. Modular monolith
   1. Distributed synchronous system
   1. Event driven monolith
   1. Event driven microservices
   1. Eventual consistency

---

### Part 2

1. [DDD](ddd.html)
   1. Lanugage
   1. Finding Contexts
   1. Building blocks
   1. CQRS
1. [Hexagonal Architecture and testability](hexagonal.html)
1. [DSLs](dsl.html)
   1. Finite type algebra
   1. Interface driven interactions
1. [Contracts](contracts.html)
   1. ArchUnit
   1. Consumer Driven Contracts

---

## Sources

Software Architecture for Developers - Simon Brown

Fundamentals of Software Architecture: An Engineering Apporach - Mark Richards, Neal Ford

Designing Event-Driven Systems - Ben Stopford

Building Microservices: Designing Fine-Grained Systems - Sam Newman

Domain-Driven Design: Tackling Complexity in the Heart of Software (Eric Evans)

Reactive Design Patterns - Roland Kuhn Dr.

Refactoring to a System of Systems - Oliver Gierke

[Self-Contained Systems](http://scs-architecture.org)

Life Beyond Distributed Transactions (Pat Helland)

Clean Architecture: A Craftsman's Guide to Software Structure and Design (Robert C. Martin)

Test Driven Development: By Example (Kent Beck)

---

## Who is an Enterprise Architect?

--

> responsible for performing the analysis of business structure and processes and are often called upon to draw conclusions from the information collected to address the goals of enterprise architecture: effectiveness, efficiency, agility, and durability.

--

> organizing logic for business processes and IT infrastructure reflecting the integration and standardization requirements of the company's operating model. The operating model is the desired state of business process integration and business process standardization for delivering goods and services to customers

--

> analyzes areas of common activity within or between organizations, where information and other resources are exchanged to guide future states from an integrated viewpoint of strategy, business, and technology

--

> proactively and holistically leading enterprise responses to disruptive forces by identifying and analyzing the execution of change toward desired business vision and outcomes

--

> delivers value by presenting business and IT leaders with signature-ready recommendations for adjusting policies and projects to achieve target business outcomes that capitalize on relevant business disruptions

---

## Who is an Enterprise Architect?

It's about organization as a whole

IT knowledge & experience is required

It's not about software architecture

Often it's the person, business (CEO/CTO) trusts to understand the high level goal

--

Sometimes it's the only person who has a picture of how the IT works

--

Sometimes it's the only person who doesn't understand how the IT works

--

## What are the problems with an Enterprise Architect?

> A 2008 study performed by Erasmus University Rotterdam and software company IDS Scheer concluded that two-thirds of enterprise architecture projects failed to improve business and IT alignment.

--

Can we escape the "organization" part when we create software architecture?

---

## Who is a Solution Architect?

--

> A Solution Architecture typically applies to a single project or project release, assisting in the translation of requirements into a solution vision, high-level business and/or IT system specifications, and a portfolio of implementation tasks

--

> Solution architecture includes business architecture, information architecture, application architecture, and technology architecture operating at a tactical level and focusing on the scope and span of a selected business problem. 

> In contrast, enterprise architecture operates at the strategic level and its scope and span is the enterprise rather than a specific business problem.

> Consequently, enterprise architecture provides strategic direction and guidance to solution architecture.

--

Quite often it's a Software Architect that sees more than just software

---

## Who is a Software Architect?

--

> is a software developer expert who makes high-level design choices and dictates technical standards, including software coding standards, tools, and platforms.

--

> makes high-level design choices based on their experience on making low-level coding

--

> has thought through all the aspects of a software, just like a designer that builds a house

---

## Architect

Enterprise
- highly abstracted interactions
- thinking and communication across organization
- minimal, high level design

Solution
- very detailed interactions
- communication with multiple teams
- detailed design

Software
- very detailed interactions
- communication with a single team
- very detailed design

--

We will focus on Solution & Software (detailed design & code), but we have to take enterprise into architecture considerations due to Conway's Law

---

## Conway's Law

--

Managers realize the system will be large, so they throw too many people at the design

--

many people = too many communication paths = zero productivity

--

so organizations limit communication by creating design subgroups

--

large organization can understand only tree structure with single superior + 7 subordinates, 
so design subgroups are organized this way

--

this limits communication channels to this structure, hence: miscommunication

--

*relationship between the graph structure of a design organization and the graph structure of the system it designs is 1:1*

--

so the final design also has the wrong structure, and is build on miscommunication

---

## Conway's Law conclusions

Look at  your architecture from communication perspective

Will communication be efficient while building the system?

Can your organization handle this architecture?

--

Even big systems should be designed only in a small group

What a small group cannot handle, a big one will fuck up even more

--

Design architecture AND organization

World has no boundaries, boundaries are in our mind, all systems are connected, all systems interact

--

For productivity make teams as independent as possible

---

## Role of an architect

It depends on the level of the team(s)

> 1. Survival model (chaos): requires a more direct, command and control leadership style.

> 2. Learning: requires a coaching leadership style.

> 3. Self-organising: requires facilitation to ensure the balance remains intact.

---

## Infrastructure Architecture

Stuff we are not going to cover, but we need to take into account anyway...

Some lectures for a high level overview

[K8s infra in PL](https://www.youtube.com/watch?v=o51N4c90jVw)

[Cloud Security in PL](https://www.youtube.com/watch?v=_sdt1yc5LPM)

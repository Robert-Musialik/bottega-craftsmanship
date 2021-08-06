class: center, middle

# Problems of highly distributed systems

---

## Understanding the picture

- architecture
- communication
- monitoring
- operations
- organization
- security
- legacy

---

class: center, middle

# Architecture

---

## What are microservices

--

> Microservices are an approach to distributed systems that promote the use of finely grained services with their own lifecycles, which collaborate together. 

--

> Because microservices are primarily modeled around business domains, they avoid the problems of traditional tiered architectures. 

--

> Domain-driven design. Continuous delivery. On-demand virtualization. Infrastructure automation. Small autonomous teams. Systems at scale. Microservices have emerged from this world.

--

> Cohesion — the drive to have related code grouped together — is an important concept when we think about microservices. Single Responsibility Principle: “Gather together those things that change for the same reason, and separate those things that change for different reasons.”

---

## What is a "distributed monolith"

--

A system composed of several applications that have to be deployed together (release trains, etc) or at least coordinated

---

## What size a microservice should be

--

> When speaking at conferences, I nearly always ask the question who has a system that is too big and that you’d like to break down? Nearly everyone raises their hands. We seem to have a very good sense of what is too big, and so it could be argued that **once a piece of code no longer feels too big, it’s probably small enough**

--

Sizes I see in practice

- the size of a bounded context
- the size of command part (CQRS) of bounded context
- the size of a function (lambda/serverless)
- the size of a failure scope
- the size of a tool (scheduler etc.) 

--

Be pragmatic!

---

## Autonomous microservices

--

> These services need to be able to change independently of each other, and be deployed by themselves without requiring consumers to change. 

--

> All communication between the services themselves are via network calls, to enforce separation between the services and avoid the perils of tight coupling.

Do you remember the leaked letter Besos sent to all IT in Amazon?

--

> We also need to think about what technology (for API) is appropriate to ensure that this itself doesn’t couple consumers. This may mean picking technology-agnostic APIs to ensure that we don’t constrain technology choices.

--

> The golden rule: can you make a change to a service and deploy it by itself without changing anything else?

---

## Steve Yegge @Google about Amazon

> So one day Jeff Bezos issued a mandate.  He's doing that all the time, of course, and people scramble like ants being pounded with a rubber mallet whenever it happens. But on one occasion -- back around 2002 I think, plus or minus a year -- he issued a mandate that was so out there, so huge and eye-bulgingly ponderous, that it made all of his other mandates look like unsolicited peer bonuses.

---

> His Big Mandate went something along these lines:

> All teams will henceforth expose their data and functionality through service interfaces.

--

> Teams must communicate with each other through these interfaces.

--

> There will be no other form of interprocess communication allowed:  no direct linking, no direct reads of another team's data store, no shared-memory model, no back-doors whatsoever.  The only communication allowed is via service interface calls over the network.

--

> It doesn't matter what technology they use.  HTTP, Corba, Pubsub, custom protocols -- doesn't matter.  Bezos doesn't care.

--

> All service interfaces, without exception, must be designed from the ground up to be externalizable.  That is to say, the team must plan and design to be able to expose the interface to developers in the outside world.  No exceptions.

--

> Anyone who doesn't do this will be fired.


---

## Technology Heterogenity

--

> One of the biggest barriers to trying out and adopting new technology is the risks associated with it

--

Two approaches

--

1. Every technology is ok, as long as you can adhere to the contract

--

2. One technology per problem solved (one OOP language, one FP language, one Data digging language, one RDBMS, one document store, one key-value store, one ...) 

--

What are benefits and risks of both approaches?

---

## Resilience

--

If one service can bring the whole system down, you have no resilience

--

> If one component of a system fails, but that failure doesn’t cascade, you can isolate the problem and the rest of the system can carry on working (degrade functionality accordingly).

--

> Networks can and will fail, as will machines. 

--

In practice, there are more and less important parts of the system.

You want to be able to degrade system gracefully.

How do we do that?

--

Can you DDOS yourself?

---

## Architectural Safety

> We cannot afford for one badly behaved service to ruin the party for everyone. We have to ensure that our services shield themselves accordingly from unhealthy, downstream calls.

--

> This means you will probably want to mandate as a minimum that each downstream service gets its own connection pool, and you may even go as far as to say that each also uses a circuit breaker.

--

How many thread pools do you have in your services?

```
pstree | grep boot
jcmd 04199 Thread.print
```

---

## Circuit Breaker

--

Circuit is closed - data flows

--

One of the services fails. What now?

--

Let's try again, perhaps it was temporary (retry)

--

Hmmmm... still doesn't work. What now?

--

Perhaps there is something else we can do (fallback)

--

Should we try again? Perhaps not for a while (open circuit)

---

## Horizontal auto scaling

--

Stateless services

--

No shared lock

--

Monitoring bottlenecks

---

## Ease of Deployment

How often do you deploy a microservice?

--

How many services do you have to deploy together?

--

> In practice, large-impact, high-risk deployments end up happening infrequently due to understandable fear

--

Deployemnt == time to market == money

--

What do you need to do to deploy?

--

- run a scipt?
- click a button on CI?
- push to git?

---

## Optimizing for Replaceability

> Why hasn’t it been replaced? You know why: it’s too big and risky a job. [...] Teams using microservice approaches are comfortable with completely rewriting services when required, and just killing a service when it is no longer needed. 

--

Wendigo & JRuby

--

My rule for 15 years: I do not support things I did not write. I can rewrite anything though.

--

We'll talk how to deal with legacy

---

## SOA

What is SOA?

--

> SOA emerged as an approach to combat the challenges of the large monolithic applications.

--

> SOA at its heart is a very sensible idea. However, despite many efforts, there is a lack of good consensus on how to do SOA well.

--

> Many of the problems laid at the door of SOA are actually problems with things like communication protocols (e.g., SOAP), vendor middleware, a lack of guidance about service granularity, or the wrong guidance on picking places to split your system.

--

> The microservice approach has emerged from real-world use, taking our better understanding of systems and architecture to do SOA well. So you should instead think of microservices as a specific approach for SOA in the same way that XP or Scrum are specific approaches for Agile software development.

---

## Other Decomposition Techniques

What other decomposition techniques are you aware of?

--

### Shared libraries 

Why not?

--

- no technology heterogeneity
- no scaling
- no deployment of changes in isolation

--

### Modules 

- gradle/maven modules
- Separate spring contexts
- WARs in an EAR
- packaging like it was intended to

---

## Modules that have lifecycle 

--

> such that they can be deployed into a running process, allowing you to make changes without taking the whole process down

--

Example OSGI

--

> My own experience with OSGI, which is matched by that of colleagues in the industry, is that even with good teams it is easy for OSGI to become a much bigger source of complexity than its benefits warrant.

--

Why not modular monolith?

--

Modular monolith is a good start, but how often have you seen one?

> Technically, it should be possible to create well-factored, independent modules within a single monolithic process. And yet we rarely see this happen. [...] Having a process bound ary separation does enforce clean hygiene in this respect

--

If you can build modular monolith instead of microservices, do it!

--

At some number of devs, you will not be able to.

---

## What Makes a Good Service?

--

### Loose Coupling 

> When services are loosely coupled, a change to one service should not require a change to another. 

> A classic mistake is to pick an integration style that tightly binds one service to another, causing changes inside the service to require a change to consumers

--

### High Cohesion 

> We want related behavior to sit together, and unrelated behavior to sit elsewhere. 

--

Why? 

> Well, if we want to change behavior, we want to be able to change it in one place, and release that change as soon as possible. 

---

## Bounded Context

> Each bounded context has an explicit interface, where it decides what models to share with other contexts

--

> once you have found your bounded contexts in your domain, make sure they are modeled within your codebase as modules, with shared and hidden models. These modular boundaries then become excellent candidates for microservices. In general, microservices should cleanly align to bounded contexts

--

> When you start to think about the bounded contexts that exist in your organization, you should be thinking not in terms of data that is shared, but about the capabilities those contexts provide the rest of the domain. 

--

> too often that thinking about data leads to anemic, CRUD-based services. 

--

You never split your services via layers (web, datastore, etc.)

---


## Orchestration Versus Choreography

Which is which?

--

> With orchestration, we rely on a central brain to guide and drive the process, much like the conductor in an orchestra. 

--

> With choreography, we inform each part of the system of its job, and let it work out the details, like dancers all finding their way and reacting to others around them in a ballet.

---

### Which one is better?

--

> The downside to this orchestration approach is that the customer service can become too much of a central governing authority.

--

> With a choreographed approach, we could instead just have the customer service emit an event in an asynchronous manner, saying Customer created. 

> This approach is significantly more decoupled 

> The downside is that the explicit view of the business process is now only implicitly reflected in our system. 

--

Can we do something with it?

--

> One approach I like for dealing with this is to build a monitoring system that explicitly matches the view of the business process.

---

## Saga

In a single system, you may have transaction on the whole proces. In a distributed system, that is no longer possible. What can you do with it?

--

Every process behaves as if a long running process

--

> Design the process as a composite task, which is tracked by an executive component that records the steps and completeness of the task using a persistent object. [Implementing Domain-Driven Design: Vaughn Vernon]

--

That's orchestration

--

You can find a lot more about it in DDD papers (https://bottega.com.pl/pdf/materialy/ddd/ddd2.pdf)

Just be aware that your "single transaction" turns to a long running process with state, compensation and monitoring

---


## UI

How do we deal with UI in microservices?

--

How about one service for UI?

--

Mobile has different needs than thin/fat web client (throughput etc.)


Backend For Frontend on mobile

Takes API of all services and combines an answer to GUI

--

Drawback: every change requires two microservice changes, and those tend to grow 

--

> The problem that can occur is that normally we’ll have one giant layer for all our services. This leads to everything being thrown in together, and suddenly we start to lose isolation of our various user interfaces, limiting our ability to release them independently.

--

> restrict the use of these backends to one specific user interface or application

---

### UI Fragment Composition 

Every service returns their "box"

One service combines them all

service team can change their UI fragment

Non-tech people can create new sites by choosing data sources and UI components

---

### UI Fragment Composition drawbacks

Possible problem with consistency of the user experience

Native mobile or thick client still prefers to call the API than include HTML components

---

## Principles of microservices

Model Around Business Concepts

Adopt a Culture of Automation

Hide Internal Implementation Details

Decentralize All the Things

---

## Amazon/Google lessons 

>  pager escalation gets way harder, because a ticket might bounce through 20 service calls before the real owner is identified.  If each bounce goes through a team with a 15-minute response time, it can be hours before the right team finally finds out, unless you build a lot of scaffolding and metrics and reporting.

--

> every single one of your peer teams suddenly becomes a potential DOS attacker.  Nobody can make any real forward progress until very serious quotas and throttling are put in place in every single service.

--

> monitoring and QA are the same thing.  You'd never think so until you try doing a big SOA.  But when your service says "oh yes, I'm fine", it may well be the case that the only thing still functioning in the server is the little component that knows how to say "I'm fine, roger roger, over and out" in a cheery droid voice.

> In order to tell whether the service is actually responding, you have to make individual calls.  The problem continues recursively until your monitoring is doing comprehensive semantics checking of your entire range of services and data, at which point it's indistinguishable from automated QA.  So they're a continuum.

---

## Amazon/Google lessons 

> if you have hundreds of services, and your code MUST communicate with other groups' code via these services, then you won't be able to find any of them without a service-discovery mechanism

---

## Microsoft lessons on product design at scale

[On Designing and Deploying Internet-Scale Services](JamesRH_Lisa.pdf) 

James Hamilton; Windows Live Services Platform

---

## Microsoft lessons: Design for failure

> This is a core concept when
developing large services that comprise many
cooperating components. Those components will
fail and they will fail frequently

> Once the service has scaled beyond
10,000 servers and 50,000 disks, failures will occur
multiple times a day. 

> If a hardware failure requires
any immediate administrative action, the
service simply won’t scale cost-effectively and
reliably. The entire service must be capable of
surviving failure without human administrative
interaction. 

---

## Microsoft lessons: Redundancy and fault recovery

> For each failure,
ensure that the service can continue to operate
without unacceptable loss in service quality,
or determine that this failure risk is acceptable
for this particular service (e.g., loss of an
entire data center in a non-geo-redundant service).

> We’ve been surprised at how frequently
‘‘unusual’’ combinations of events take
place when running thousands of servers that
produce millions of opportunities for component
failures each day. Rare combinations can
become commonplace.

---

## Microsoft lessons: Commodity hardware slice

> large clusters of commodity servers are
much less expensive than the small number
of large servers they replace,

--

> server performance continues to increase
much faster than I/O performance, making
a small server a more balanced system for
a given amount of disk,

--

> power consumption scales linearly with
servers but cubically with clock frequency,
making higher performance servers more
expensive to operate, and

--

> a small server affects a smaller proportion
of the overall service workload when failing
over.

---

## Microsoft lessons: Single-version software

> Enterprises are used to having
significant influence over their software providers
and to having complete control over when they
deploy new versions (typically slowly). 

> The most economic services don’t give customers
control over the version they run, and
only host one version.

---

## Something as a service

https://mesosphere.com/blog/iaas-vs-caas-vs-paas-vs-faas/

It's all about things we don't have to do

--

Building a platform?

--

> A platform is what emerges after years of extracting common functionality out of applications into lower level abstractions. If done with deliberate intent and design, you get a platform out of it. If not, you probably end up with an organic mess on your hands and find yourself looking into platforms other people have built for a way out, a ray of hope.

---

## As a service

IaaS: infrastructure (OpenStack/AWS EC2)

CaaS: container (Kubernetes)

PaaS: application (CloudFoundry, Heroku, Google App Engine)

FaaS: function (Amazon Lambda)

SaaS: software (Google Docs)

--

Never trust vendors!

---

class: center, middle

# Communication

---

## Interfaces

You are designing a new distributed system. What protocols/interface technologies, should your microservices use?

--

> Picking a small number of defined interface technologies helps integrate new consumers. Having one standard is a good number. Two isn’t too bad, either. Having 20 different styles of integration is bad. 

--

Example

- Kafka for events (async)
- HTTPS for sync
- SSH/JMX for manual operations 

---

## Synchronous or Asynchronous?

Which one is better?

--

> These two different modes of communication can enable two different idiomatic styles of collaboration: request/response or event-based

--

> With an event-based collaboration, we invert things. Instead of a client initiating requests asking for things to be done, it instead says this thing happened and expects other parties to know what to do. We never tell anyone else what to do

--

> Event-driven architectures seem to lead to significantly more decoupled, scalable systems. And they can. But these programming styles do lead to an increase in complexity. 

> Ensure you have good monitoring in place, and strongly consider the use of correlation IDs, which allow you to trace requests across process boundaries.

--

Rethink the default!

https://vimeo.com/221255968

http://nurkiewicz.github.io/talks/2017/async/

---

## Integration APIs

Avoid Breaking Changes 

If a microservice adds new fields to a piece of data it sends out, existing consumers shouldn’t be impacted.

Keep Your APIs Technology-Agnostic

Make Your Service Simple for Consumers

Hide Internal Implementation Detail

NO shared databases

---

## Service Discovery and Registration

What service discovery techs do you know?

---

### DNS (GUI, Kubernetes sidecar)

Problem with TTL too long

> One way to work around this problem is to have the domain name entry for your service point to a load balancer, which in turn points to the instances of your service,

---

### Zookeeper

> At its heart, Zookeeper provides a hierarchical namespace for storing information. Clients can insert new nodes in this hierarchy, change them, or query them. Further‐ more, they can add watches to nodes to be told when they change. 

> expect to be running at least three Zookeeper nodes. Most of the smarts in Zookeeper are around ensuring that data is replicated safely between these nodes, and that things remain consistent when nodes fail.

---

### Consul

> supports both configuration management and service discovery. But it goes further than Zookeeper in providing more support for these key use cases. For example, it exposes an HTTP interface for service discovery, and one of Consul’s killer features is that it actually provides a DNS server out of the box

> This means if part of your system uses DNS already and can support SRV records, you can just drop in Consul and start using it without any changes to your existing system.

> the ability to perform health checks on nodes

> Consul uses a RESTful HTTP interface for everything from registering a service, querying the key/value store, or inserting health checks.

---

### Eureka

> provides basic load-balancing capabilities in that it can support basic round-robin lookup of service instances. It provides a REST-based endpoint so you can write your own clients, or you can use its own Java client. The Java client provides additional capabilities, such as health checking of instances.

---

### How to kill your service discovery

Service discovery becomes the single point of failure

I've seen a company DDOSing its own service discovery

How would you mitigate that?

--

[An example](Consul_ATM_2017_public.pdf)

[Other suggestions](https://stackoverflow.com/questions/37811262/run-redis-in-marathon-mesos-under-one-url/37822870)

[Meta service discovery](http://www.cs.stir.ac.uk/~mko/MP2P-2_Brown_A.pdf)

---


## HATEOAS 

Hypermedia As the Engine of Application State

--

> Hypermedia is a concept whereby a piece of content contains links to various other pieces of content in a variety of formats (e.g., text, images, sounds). 

--

> HATEOAS - clients should perform interactions (potentially leading to state transitions) with the server via these links to other resources

[An example](https://spring.io/understanding/HATEOAS)

--

Any disadvantages?

--

> One of the downsides is that this navigation of controls can be quite chatty, as the client needs to follow links to find the operation it wants to perform. Ultimately, this is a trade-off. I would suggest you start with having your clients navigate these con‐ trols first, then optimize later if necessary.

---

## Versioning

- avoid making breaking changes

--

- as a client bind only variable you use

--

- Postel’s Law - “Be conservative in what you do, be liberal in what you accept from others.”

--

- Semantic Versioning: MAJOR.MINOR.PATCH.

--

- remove old version only when you have no more consumers

--

- let everyone know (it-all) that you will deprecate/remove an API

--

Should you keep old API in old services/branches, or in master of the new service?

--

It's Easier to support old API in new master, then to keep both services alive for long. So after canary release, we usually remove the old stuff

---

## Version numbers in HTTP

How do you do it?

--

- in headers (additional header)

--

- in url /v1/ customer/ or /v2/customer/

---

## Correlation/Trace IDs

> When the first call is made, you generate a GUID for the call. This is then passed along to all subsequent calls (Zipkin)

For that you need to wrap your inter-service calls (or even inter-thread calls) and pass correlationId automatically.

---

class: center, middle

# Monitoring

---

## Monitoring & Logs

How do you organize monitoring & logs for 700+ microservices?

--

All the logs need to be written in the same format. No exceptions. We need to gather them to a central point. 

--

Most popular approach: ELK - elasticsearch + logstash + kibana.

--

> Track inbound response time at a bare minimum. Once you’ve done that, follow with error rates and then start working on application-level metrics.

--

> Track the health of all downstream responses, at a bare minimum including the response time of downstream calls, and at best tracking error rates. Libraries like Hystrix can help here.

--

Aggregate host-level metrics like CPU together with application-level metrics.

--

Add business metrics

--

Buy large TVs per team (or even several)

---

<img style="position: absolute; height: 100%; top: 0; bottom: 0;" src="img/metricjnb.jpg"/> 

---

class: center, middle


<img style="height: 600px" src="img/large_Code1.jpg"/> 

---

## Zipkin

> Zipkin is a distributed tracing system. It helps gather timing data needed to troubleshoot latency problems in microservice architectures. It manages both the collection and lookup of this data. Zipkin’s design is based on the Google Dapper paper.

![How it works](img/zipkinarchitecture.png)

---

## Zipkin

[Website](http://zipkin.io/pages/architecture.html)

---

## Open tracing

> OpenTracing is a new, open distributed tracing standard for applications and OSS packages. 

> OpenTracing is that “single, standard mechanism.” OpenTracing allows developers of application code, OSS packages, and OSS services to instrument their own code without binding to any particular tracing vendor. 

http://opentracing.io/

--

*That's not enough though*

You might need a Linux daemon reporting connnections

---

## Communication Visualization 

Also called "architecture"

--

<iframe width="560" height="315" src="https://www.youtube.com/embed/MYHf_BXWuOc?rel=0&amp;controls=0" frameborder="0" allowfullscreen></iframe>

[vizceral](https://github.com/Netflix/vizceral)

There are many others like this

---


## Graphite

> It exposes a very simple API and allows you to send metrics in real time. It then allows you to query those metrics to produce charts and other displays to see what is happening. You configure it so that you reduce the resolution of older metrics to ensure the volumes don’t get too large.

---

## Data mining

Haadop + Spark (batch)

OLAP (real time)

---

## Semantic Monitoring

> what if our monitoring systems were programmed to act a bit like our users, and could report back if something goes wrong

--

> Every minute or so, we had Nagios run a command-line job that inserted a fake event into one of our queues. Our system picked it up and ran all the various calculations just like any other job, except the results appeared in the junk book, which was used only for testing. If a re-pricing wasn’t seen within a given time, Nagios reported this as an issue.

We've set up an external user.

---

## Do you understand your microservice?

Set of questions before any microservice goes to prod

--

Response time/latency - How long should various operations take?

--

Availability - Can you expect a service to be down? Is this considered a 24/7 service?

--

Durability of data - How much data loss is acceptable? How long should data be kept for?

--

What load can your service handle?

--

What will be the first bottleneck your service hits under high load?

--

How will your service behave under load too high?

--

What are the thread pools in your service? How much are they used? How much usage will fire an alarm?

--

How does your service behave when services you depend from fail (500)? What if they start responding very slow? (cascading failure, timeouts)

---

> Put timeouts on all out-of-process calls, and pick a default timeout for everything. Log when timeouts occur, look at what happens, and change them accordingly.

--

Stop polluting your logs!

--

*DEBUG* (inTestDev)

information that is useful during development. Usually very chatty, and will not show in production.

--

*INFO* (inProdDev)

information you will need to debug production issues.

--

*WARN* (toInvestigateTomorrow)

someone in the team will have to investigate what happened, but it can wait until tomorrow.

--

*ERROR* (wakeMeInTheMiddleOfTheNight)

Oh-oh, call the fireman! This needs to be investigated now!

[source](https://labs.ig.com/logging-level-wrong-abstraction)

---

class: center, middle

# Operations

Anything specjal about microservices and operations?

You've got to get good at it.

---

## Blue/Green deployment

> With blue/green, we have two copies of our software deployed at a time, but only one version of it is receiving real requests.

---

## Canary Releasing

> With canary releasing, we are verifying our newly deployed software by directing amounts of production traffic against the system to see if it performs as expected.

---

## Degrading Functionality

We can reduce some functionality but provide main value even under extreme circumstances

Those are architecture decisions

Reader vs Editor, slashdot outage by regexp in comment

Use saparate pools per service, use circuit breakers

---

## Getting strong

Chaos monkey - from Netflix: during certain hours of the day will turn off random machines

The Chaos Gorilla - takes down entire data center

Latency Monkey - simulates slow latency on the network

Importance of learning from the failure when it occurs, and adopting a blameless culture when mistakes do happen - post mortems

> The fact that your system is now spread across multiple machines (which can and will fail) across a network (which will be unreliable) can actually make your system more vulnerable, not less

http://principlesofchaos.org/

---

## Getting efficient

Mesos: A distributed systems kernel

Mesos uses Linux cgroups to provide isolation for CPU, memory, I/O and file system

Mesos is built using the same principles as the Linux kernel, only at a different level of abstraction. The Mesos
kernel runs on every machine and provides applications (e.g., Hadoop, Spark, Kafka, Elasticsearch) with API’s for
resource management and scheduling across entire datacenter and cloud environments.

---

## Marahton

Marathon is a production-grade container orchestration platform for Mesosphere’s Datacenter Operating System (DC/OS) and Apache Mesos.

- High Availability 
- Multiple container runtimes (cgroups, Docker)
- Stateful apps. Marathon can bind persistent storage volumes to your application. You can run databases like MySQL and Postgres, and have storage accounted for by Mesos.
- Constraints. e.g. Only one instance of an application per rack, node, etc.
- Service Discovery & Load Balancing. Several methods available.
- Health Checks. Evaluate your application’s health using HTTP or TCP checks.
- Event Subscription. Supply an HTTP endpoint to receive notifications.
- Metrics. Query them at /metrics in JSON format or push them to systems like graphite, statsd and Datadog.
- Complete REST API for easy integration and scriptability.

https://mesosphere.github.io/marathon/


---

class: center, middle

# Organisation

---

## Organizational Alignment

What kind of organisation do you need to have to be sucessful with microservices?

--

> smaller teams working on smaller codebases tend to be more productive. Microservices allow us to better align our architecture to our organization, helping us minimize the number of people working on any one codebase to hit the sweet spot of team size and productivity.

Conway's law

---

## Microsoft

> Microsoft carried out an empirical study where it looked at how its own organizational structure impacted the software quality of a specific product, Windows Vista. Specifically, the researchers looked at multiple factors to determine how error-prone a component in the system would be. 

--

> After looking at multiple metrics, including commonly used software quality metrics like code complexity, they found that the metrics associated with organizational structures proved to be the most statistically relevant measures.

---

## Harvard Business School

> they matched similar product pairs from each type of organization, the authors found that the more loosely coupled organizations actually created more modular, less coupled systems, whereas the more tightly focused organization’s software was less modularized.

---

## Amazon

> It wanted teams to own and operate the systems they looked after, managing the entire lifecycle. Small teams can work faster than large teams. Two-pizza teams

---

## Netflix

> Netflix designed the organizational structure for the system architecture it wanted.

---

## Service ownership

> the team owning a service is responsible for making changes to that service. The team should feel free to restructure the code however it wants, as long as that change doesn’t break consuming services.

> Conway’s law highlights the perils of trying to enforce a system design that doesn’t match the organization. This leads us to trying to align service ownership to colocated teams, which themselves are aligned around the same bounded contexts of the organization. When the two are not in alignment, we get tension points

---

## Internal Open Source

> With normal open source, a small group of people are considered core committers. They are the custodians of the code. If you want a change to an open source project, you either ask one of the committers to make the change for you, or else you make the change yourself and send them a pull request. The core committers are still in charge of the codebase; they are the owners.

---

## The Orphaned Service?

> if you’ve adopted a truly polyglot approach, making use of multiple technology stacks, then the challenges of making changes to an orphaned service could be compounded if your team doesn’t know the tech stack any longer.

---

## Handling hundreds of microservices

[Fallacies of distributed computing](https://en.wikipedia.org/wiki/Fallacies_of_distributed_computing)

> failure becomes a statistical certainty at scale

> organizations put processes and controls in place to try to stop failure from occurring, but put little to no thought into actually making it easier to recover from failure in the first place

> Baking in the assumption that everything can and will fail leads you to think differently about how you solve problems.

--

Tooling team(s)

---

## Architects


Does a microservice company have Architects?

--

What is a job of an architect?

> With larger, monolithic systems, there are fewer opportunities for people to step up and own something. With microservices, on the other hand, we have multiple autonomous codebases that will have their own independent lifecycles. Helping people step up by having them take ownership of individual services before accepting more responsibility can be a great way to help them achieve their own career goals, and at the same time lightens the load on whoever is in charge!

---

## Architects in practice

Everyone is an architect

Seniors take care of complex stuff (inter-service rules, communication, tooling)

Managers/leads are responsible for building teams that can handle architecture

Not everyone is capable of doing it right

Good architecture takes experience!

Years worked != experience

---

## Examplars

Assuming you are about to use a new framework/lib, would you rather like a book about it or an example of working solution?

--

> Written documentation is good, and useful. [...] But developers also like code, and code they can run and explore.

> Ideally, these should be real-world services you have that get things right, rather than isolated services that are just implemented to be perfect examples. 

---

## Tailored Service Template

--

star.spring.io?

--

> out of the box, you have a service complete with an embedded servlet container that can be launched from the command line

--

> Of course, if you embraced multiple disparate technology stacks, you’d need a matching service template for each.

--

Who writes those templates?

--

> You do have to be careful that creating the service template doesn’t become the job of a central tools or architecture team who dictates how things should be done, albeit via code. Defining the practices you use should be a collective activity, so ideally your team(s) should take joint responsibility for updating this template (an internal open source approach works well here).

> Ideally, its use should be purely optional

In real life: open repo with PRs from people

---

class: center, middle

# Security

---

## Sensitive data

RODO - controlling and removing of sensitive data

--

Do we delete data when user asks us to?

--

[Data anonymization](https://en.wikipedia.org/wiki/Data_anonymization)

--

Fake production data (real distribution)


---

## SSO

> These systems allow you to store information about principals, such as what roles they play in the organization.

What implementations are you aware of?

--

> Security Assertion Markup Language 2.0 (SAML 2.0) - standard for exchanging authentication and authorization data between security domains. SAML 2.0 is an XML-based protocol (SOAP) that uses security tokens containing assertions to pass information about a principal (usually an end user) between a SAML authority, named an Identity Provider, and a SAML consumer, named a Service Provider.

--

> OpenID Connect (OIDC) is an authentication layer on top of OAuth 2.0, an authorization framework.

> OpenID Connect is a simple identity layer on top of the OAuth 2.0 protocol, which allows computing clients to verify the identity of an end-user based on the authentication performed by an authorization server, as well as to obtain basic profile information about the end-user in an interoperable and REST-like manner. In technical terms, OpenID Connect specifies a RESTful HTTP API, using JSON as a data format.

--

> Lightweight Directory Access Protocol (LDAP) and/or Active Directory

---

## SSO Gateway

> Rather than having each service manage handshaking with your identity provider, you can use a gateway to act as a proxy, sitting between your services and the outside world. The idea is that we can centralize the behavior for redirecting the user and perform the handshake in only one place.

---

## Service-to-Service Authentication and Authorization

Assume that any calls to a service made from inside our perimeter are implicitly trusted (weak)

Transport Layer Security Client Certificates

JSON Web Token (asymetric key)

HMAC (hash-based messaging code) Over HTTP. With HMAC the body request along with a private key is hashed, and the resulting hash is sent along with the request. The server then uses its own copy of the private key and the request body to re-create the hash. If it matches, it allows the request. 

API Keys. All public APIs from services like Twitter, Google, Flickr, and AWS make use of API keys. API keys allow a service to identify who is making a call, and place limits on what they can do.

---

## How do we secure hundreds of microservices?

--

Security team for exploring vulnerabilities and teaching

Mandatory security lectures

Cracking weekend

---

class: center, middle

# Escaping legacy

---

## How to move from monolith to microservices?

> not every monolith is bad. You can build modular monoliths, with highly cohesive and loosely coupled modules. But most of the time, we get the opposite due to Conway's Law, too many teams on a single codebase, and too litle self-discipline

How would you do it?

---

## The Strangler Pattern

finding seams/events, embracing and strangling

> you capture and intercept calls to the old system. This allows you to decide if you route these calls to existing, legacy code, or direct them to new code you may have written. This allows you to replace functionality over time without requiring a big bang rewrite.

> if you have sources, expose events, take over functions with microservices

---

## The Cutter Pattern

finding seams, refactoring (modularization) and splitting (look at Structure 101, SchemaSpy)

Requires stopping development for a while

But who is going to let you do that?

--

Whatever you do, do it bit by bit, focus on getting something outside, instead of getting everything right. Choose the part that changes the most (you'll get more benefits for taking it out)

In fact, when strangling, there is usually a leftover body, hanging for years to come, either not worth replacing, or too cumbersome


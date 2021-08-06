## How to fire it up

If you want to see generated diagram, first go to https://structurizr.com/, 
setup a free account (corpo accounts will follow) and you will receive a workspace id, key and secret.


Create a file /src/main/resources/secret.properties 
and add there

```
workspace.id=...
api.key=...
api.secret=...
```

Now just import this project into Intellij Idea, and run the class Structurizr (RMB -> Run, or shift+ctrl+F10)

Or you can run  with gradle  ```gradle clean run```

You diagrams will be available online (only for you though)

It would be really great if you could export the generated diagrams to png (there is a button on web for it), 
and save it in /images folder. Thank you :)

For more detailed information, please see [Structurizr for Java - Getting Started](https://github.com/structurizr/java/blob/master/docs/getting-started.md).

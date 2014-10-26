halite
======

A Java implementation of the Hypertext Application Language for XML and JSon.
Halite allows to extend a basic model formed by a Resource and a Link object using XSD Schemas or JAXB and
operate on this model to add links or embedd resources. The model object can easily be transformed to xml+hal or 
json+hal.

### Add a self link to a resource
```java
Resource res = ...; 
HAL.newLink(res, HAL.SELF, "http://...").title("Self-Link");
```

### Create a new Resource with a link
```java
HAL.newResource().addLink("next", "page=3");
``` 

### Adding a link to many Resources
```java
Resource r1 = ...;
Resource r2 = ...;
Resource r3 = ...;
HAL.newLink("home", "http://...").title("Home").addTo(r1).addTo(r2).addTo(r3);
```

### Outlook
Support for JSon generation
Support for XML generation
Support for HTML generation
Support for JCR-HAL mapping

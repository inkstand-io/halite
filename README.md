halite
======

A Java implementation of the Hypertext Application Language for XML and JSon.
Halite allows to extend a basic model formed by a Resource and a Link object using XSD Schemas or JAXB and
operate on this model to add links or embedd resources. The model object can easily be transformed to xml+hal or 
json+hal.

### Create a resource
```java
Resource resource = HAL.newResource("example");
```
The resource has an URI "example" and a link of relation (rel) "self" pointing to that URI. To extend that
link, call:
```java
resource.getLink(HAL.SELF).title("Link to self");
```

### Create a link to other URIs
```java
Resource res = ...; 
HAL.newLink(res, HAL.NEXT, "http://...").title("Next Page");
//or
res.addLink(HAL.NEXT, "http://...").title("Next Page");
```
This will create a link on resource 'res' pointing to the next page.

### Adding a link to many Resources
Assume you have a set of resources and want to add the same link to each of them
```java
Resource r1 = ...;
Resource r2 = ...;
Resource r3 = ...;
HAL.newLink("home", "http://...").title("Home").addTo(r1, r2, r3);
```

### Embedding Resources
If you have a resource and instead of linking it to another Resource, you want to embedd the
resource.
```java
Resource parent = ...;
Resource child = ...;
parent.embed("child", child);
```
This will embed the child resource under the relation "child".  

### Writing to JSon
For writing the structure to an OutputStream the JsonHalWriter can be used. The writer writes correct
_embedded and _link fields to the Json.
```java
Resource res = ...;
OutputStream os = ...;
JsonHalWriter writer = new JsonHalWriter(os);
writer.write(res);
```
A better integration into Jackson Json Processor is planned.

### Writing to XML
Writing to XML kind of inherently supported by JAXB as the base classes Resource and Link are JAXB
annotated. So to create an XML Representation of your structure, call for instance:
```java
Resource resource = ...;
OutputStream os = ...;
javax.xml.bind.JAXB.marshall(resource, os);
```
Have a look at the JAXBTest for a running example.

### Outlook
Support for HTML generation
Support for JCR-HAL mapping

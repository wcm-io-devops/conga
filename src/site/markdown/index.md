## CONGA - CONfiguration GenerAtor

Configuration generator for DevOps Teams.


### Maven Dependency

```xml
<plugin>
  <groupId>io.wcm.devops.conga</groupId>
  <artifactId>conga-maven-plugin</artifactId>
  <version>1.0.0</version>
  <extensions>true</extensions>
</plugin>
```

### Documentation

* [Usage][usage]
* [General concepts][general-concepts]
* [Extensibility model][extensibility]
* [YAML definitions][yaml-definitions]
* [Handlebars quickstart][handlebars-quickstart]
* [API documentation][apidocs]
* [Changelog][changelog]


### Overview

The configuration generator helps generating configuration based on templates for a set of environments, nodes, roles and tenants.


It supports any file type that can be generated using a text-based template. Additionally it supports validators and post processors with a flexible Java ServiceLoader-based extensibility model.

It targets DevOps teams where developers are responsible for defining the roles and templates, and operations defines the environments with the nodes and tenants.

The generator can be executed from the command line or via a maven plugin. The definition files are written in [YAML 1.1](http://yaml.org/) format. The templates use [Handlebars](http://handlebarsjs.com/) as scripting language.

See [general concepts][general-concepts] for a more in-depth explanation or jump into the [usage description][usage] and the [examples][examples].


## Plugins

Based on the [extensibility model][extensibility] the wcm.io DevOps project hosts CONGA plugins:

* [CONGA Plugin for Apache Sling](plugins/sling)
* [CONGA Plugin for Adobe Experience Manager (AEM)](plugins/aem)


## Definitions

The wcm.io DevOps project provides some generic templates implementing best practice configurations for certain environments:

Definitions for CONGA:

* [CONGA Definitions for Adobe Experience Manager (AEM)](definitions/aem)


[apidocs]: generator/apidocs/
[changelog]: changes-report.html
[usage]: usage.html
[general-concepts]: general-concepts.html
[extensibility]: extensibility.html
[examples]: examples.html
[yaml-definitions]: yaml-definitions.html
[handlebars-quickstart]: handlebars-quickstart.html

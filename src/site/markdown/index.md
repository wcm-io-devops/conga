## CONGA - CONfiguration GenerAtor

Configuration generator for DevOps Teams.

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.wcm.devops.conga/conga-maven-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.wcm.devops.conga/conga-maven-plugin)


### Documentation

* [Usage][usage]
* [General concepts][general-concepts]
* [YAML definitions][yaml-definitions]
* [Handlebars quickstart][handlebars-quickstart]
* [Custom Handlebars expressions][handlebars-helpers]
* [Extensibility model][extensibility]
* [API documentation][apidocs]
* [CONGA Maven Plugin Documentation][plugindocs]
* [Changelog][changelog]


### Overview

The configuration generator helps generating configuration based on templates for a set of environments, nodes, roles and tenants.


It supports any file type that can be generated using a text-based template. Additionally it supports validators and post processors with a flexible Java ServiceLoader-based extensibility model.

It targets DevOps teams where developers are responsible for defining the roles and templates, and operations defines the environments with the nodes and tenants.

The generator can be executed from the command line or via a maven plugin. The definition files are written in [YAML 1.1](http://yaml.org/) format. The templates use [Handlebars](http://handlebarsjs.com/) as scripting language.

See [general concepts][general-concepts] for a more in-depth explanation or jump into the [usage description][usage].


### Plugins

Based on the [extensibility model][extensibility] the wcm.io DevOps project hosts CONGA plugins:

* [CONGA Plugin for Apache Sling](plugins/sling)
* [CONGA Plugin for Adobe Experience Manager (AEM)](plugins/aem)
* [CONGA Plugin for Ansible](plugins/ansible)


### Definitions

The wcm.io DevOps project provides some generic templates implementing best practice configurations for certain environments:

Definitions for CONGA:

* [CONGA Definitions for Adobe Experience Manager (AEM)](definitions/aem)


### Further Resources

* [wcm.io CONGA training material with exercises](http://training.wcm.io/conga/)
* [adaptTo() 2015 Talk: CONGA - Configuration generation for Sling and AEM][adaptto-talk-2015-conga]
* [adaptTo() 2017 Talk: Automate AEM Deployment with Ansible and wcm.io CONGA][adaptto-talk-2017-aem-ansible]
* [adaptTo() 2018 Talk: Maven Archetypes for AEM][adaptto-talk-2018-aem-archetypes] - about AEM project archetypes including Ansible/CONGA-based cloud deployment



[apidocs]: generator/apidocs/
[plugindocs]: tooling/conga-maven-plugin/plugin-info.html
[changelog]: changes-report.html
[usage]: usage.html
[general-concepts]: general-concepts.html
[extensibility]: extensibility.html
[examples]: examples.html
[yaml-definitions]: yaml-definitions.html
[handlebars-quickstart]: handlebars-quickstart.html
[handlebars-helpers]: handlebars-helpers.html
[adaptto-talk-2015-conga]: https://adapt.to/2018/en/schedule/maven-archetypes-for-aem.html
[adaptto-talk-2017-aem-ansible]: https://adapt.to/2017/en/schedule/automate-aem-deployment-with-ansible-and-wcm-io-conga.html
[adaptto-talk-2018-aem-archetypes]: https://adapt.to/2018/en/schedule/maven-archetypes-for-aem.html

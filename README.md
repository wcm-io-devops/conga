<img src="http://wcm.io/images/favicon-16@2x.png"/> CONGA - CONfiguration GenerAtor
======
[![Build Status](https://travis-ci.org/wcm-io-devops/wcm-io-devops-conga.png?branch=master)](https://travis-ci.org/wcm-io-devops/wcm-io-devops-conga)


## Overview

The configuration generator helps generating configuration based on templates for a set of environments, nodes, roles and tenants.

It supports any file type that can be generated using a text-based template. Additionally it supports validators and post processors with a flexible Java ServiceLoader-based extensibility model.

It targets DevOps teams where developers are responsible for defining the roles and templates, and operations defines the environments with the nodes and tenants.

The generator can be executed from the command line or via a maven plugin. The defintion files are writen in [YAML 1.1](http://yaml.org/) format. The templates use [Handlebars](http://handlebarsjs.com/) as scripting language.


## Examples

See [example/](example/) for a commented example with role, environment definntions and template.


## Issue Tracking

Issue tracking in wcm.io JIRA: https://wcm-io.atlassian.net/projects/WDCONGA

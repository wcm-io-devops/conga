## CONGA - Usage


### Preparations

Make sure you have installed:

* Java 8 or higher
* Apache Maven 3.3 or higher

To start with the example project by cloning this GIT repository:
https://github.com/wcm-io-devops/conga/

The example project is in a subfolder `example'.


### Definitions

CONGA differentiates between role/template definitions and environment definitions. Although it is possible to put them all together into one single Maven project it is recommended to separate them, because they are usually maintained by separated people, see [general concepts][general-concepts].

In this example they are separated, start with building the definitions:

```
cd definitions
mvn clean install
```

This produces an maven artifact `io.wcm.devops.conga.example.definitions-1-SNAPSHOT.jar` which is installed in the local maven repository as well. It contains role definitions and file templates. It is recommended to deploy it to a central maven repository within your organization.

The role files can be found in `src/main/roles`, you can also see there what configuration parameters are possible and what are their default values.

The file templates are located at `src/main/templates` in folders per role. They are written using the [Handlebars][handlebars] scripting language.


### Environments

To build the real configuration files switch go to the environments folder:


```
cd environments
mvn clean package
```

This builds all configuration files for all nodes defined in the environment files. The definitions from the previous step are referenced as maven dependency.

You find the generated files at `target/configuration`, they are packed as well in a ZIP file located at `target'. Those files can be uploaded to the destination system (ideally in an automated fashion by using a deployment automation tool).


### Next steps

* Read the [general concepts][general-concepts]
* Learn about the definition YAML files syntax in [YAML definitions][yaml-definitions]
* Get a quickstart introduction to Handlebars: [Handlebars quickstart][handlebars-quickstart]



[general-concepts]: general-concepts.html
[yaml-definitions]: yaml-definitions.html
[handlebars-quickstart]: handlebars-quickstart.html
[handlebars]: http://handlebarsjs.com/


### Using from command line

It is possible to use CONGA outside Maven from the command line using this prepackaged JAR with all dependencies:
http://search.maven.org/#search|ga|1|a%3A%22io.wcm.devops.conga.tooling.cli%22

This mode lacks some features like building definition artifacts, versioning etc. So it is recommended to use the Maven plugin whenever possible.

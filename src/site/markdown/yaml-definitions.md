## CONGA - YAML definitions

CONGA uses [YAML][yaml] to describe roles and environments. 

[SnakeYAML][snakeyaml] is used to parse the definition files and map them to a Java object model. Because of this the detailed YAML file documentation are the JavaDocs of the model classes.


### Role defintions

Example role definitions:<br/>
https://github.com/wcm-io-devops/conga/tree/master/example/definitions/src/main/roles

The filename of the role YAML file is the role name.

The documentation of all role and file configuration options can be found in the<br/>
[**Role Model API documentation**][role-model].


### Environment definitions:

Example environment definitions:<br/>
https://github.com/wcm-io-devops/conga/tree/master/example/environments/src/main/environments

The filename of the environment YAML file is the environment name.

The documentation of all environment and node configuration options can be found in the<br/>
[**Environment Model API documentation**][environment-model].



[role-model]: generator/apidocs/io/wcm/devops/conga/model/role/Role.html
[environment-model]: generator/apidocs/io/wcm/devops/conga/model/environment/Environment.html
[yaml]: http://yaml.org/
[snakeyaml]: http://www.snakeyaml.org/

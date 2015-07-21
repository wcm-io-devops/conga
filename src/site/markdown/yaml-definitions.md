## CONGA - YAML definitions

CONGA uses [YAML][yaml] to describe roles and environments. 

[SnakeYAML][snakeyaml] is used to parse the definition files and map them to a Java object model. Because of this the detailed YAML file documentation are the JavaDocs of the model classes. 

Please note that the Java Bean naming conventions are applied: For the property names in the YAML files the "get" prefix from the model class is omitted, and the property is written in headless camel case. Example: `getRoleConfig()` method name results in `roleConfig` property name.


### Role defintions

Example role definitions:<br/>
https://github.com/wcm-io-devops/conga/tree/master/example/definitions/src/main/roles

The filename of the role YAML file is the role name.

The documentation of all role and file configuration options can be found in the<br/>
[Role Model API documentation][role-model].


### Environment definitions

Example environment definitions:<br/>
https://github.com/wcm-io-devops/conga/tree/master/example/environments/src/main/environments

The filename of the environment YAML file is the environment name.

The documentation of all environment and node configuration options can be found in the<br/>
[Environment Model API documentation][environment-model].



[role-model]: generator/apidocs/io/wcm/devops/conga/model/role/Role.html
[environment-model]: generator/apidocs/io/wcm/devops/conga/model/environment/Environment.html
[yaml]: http://yaml.org/
[snakeyaml]: http://www.snakeyaml.org/


### Configuration parameter maps

On nearly every element within the YAML structure configuration parameter maps can be applied. Example:

```yaml
config:
  param1: value1
  group1:
    param11: 5
    param12: true
  list1:
  - listValue1
  - listValue2
```

Parameter names can be nested in object in any depth. Lists are supported as well.

For nested groups a short notation is supported using a "." notation.

This example is equivalent to the previous one:

```yaml
config:
  param1: value1
  group1.param11: 5
  group1.param12: true
  list1:
  - listValue1
  - listValue2
```

Within the configuration parameters maps other parameters can be references using a variable notation. Example: `${group1.param1}`. Nested variable references are supported, just make sure you define no cyclic dependencies.

Configuration parameter maps are inherited to "deeper levels" within the YAML structure, and the maps are merged on each level. The configuration parameters on the "deeper levels" overwrite the parameters from the higher level.

Inheritance order (higher number has higher precedence):

1. Global configuration parameters from role definition
2. Configuration from role variant definition
3. Global configuration from environment
4. Node configuration from environment
5. Global role configuration from environment
6. Role configuration from node
7. Variant configuration from node

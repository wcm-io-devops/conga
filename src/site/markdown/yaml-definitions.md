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
8. Configuration from multiply plugins, e.g. the tenant-specific configuration

There is a special support when merging list parameter. By default a list value on a deeper lever overwrites a list inherited from a parameter map on a higher level completely. If you insert the keyword `_merge_` as list item on either of the list values, they are merged and the special keyword entry is removed.

### Default context properties

Additionally to the variables defined in the configuration parameter maps a set of default context properties are defined automatically by CONGA and merged with the parameter maps:

| Property             | Description
|----------------------|-------------
| `version`            | Environment version
| `nodeRole`           | Current node role name
| `nodeRoleVariant`    | Current node role variant name
| `environment`        | Environment name
| `node`               | Current node name
| `nodes`              | List of all nodes. Each node has properties as defined in the [Node model][node-model].
| `nodesByRole`        | Map with node roles with each entry containing a list of all nodes with this role. Each node has properties as defined in the [Node model][node-model].
| `nodesByRoleVariant` | Map with node roles with each entry containing a map with node role variants each entry containing a list of all nodes with this role and variant. Each node has properties as defined in the [Node model][node-model].
| `tenants`            | List of all tenants. Each tenant has properties as defined in the [Tenant model][tenant-model].
| `tenantsByRole`      | Map with tenant roles with each entry containing a list of all tenants with this role. Each tenant has properties as defined in the [Tenant model][tenant-model].
| `tenant`             | Current tenant name. This is only set if the tenant multiple plugin is used.
| `tenantRoles`        | List of current tenant's role names This is only set if the tenant multiply plugin is used.


[role-model]: generator/apidocs/io/wcm/devops/conga/model/role/Role.html
[environment-model]: generator/apidocs/io/wcm/devops/conga/model/environment/Environment.html
[node-model]: generator/apidocs/io/wcm/devops/conga/model/environment/Node.html
[tenant-model]: generator/apidocs/io/wcm/devops/conga/model/environment/Tenant.html
[yaml]: http://yaml.org/
[snakeyaml]: http://www.snakeyaml.org/


### Exporting model data

You can export the "model data" (expanded configuration, list of generated files and tenants) from the current CONGA run to an external file per node to pick this up in infrastructure automation tools like Ansible for executing the further development steps.

Example environment definitions:<br/>
https://github.com/wcm-io-devops/conga/tree/master/example/environments/src/main/environments

Example export definition within an environment definition - dumps model data for each node using the plugin `yaml`:

```yaml
exportModel:
- node: yaml
```

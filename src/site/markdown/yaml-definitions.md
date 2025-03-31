## CONGA - YAML definitions

CONGA uses [YAML][yaml] to describe roles and environments.

[SnakeYAML][snakeyaml] is used to parse the definition files and map them to a Java object model. Because of this the detailed YAML file documentation are the JavaDocs of the model classes.

Please note that the Java Bean naming conventions are applied: For the property names in the YAML files the "get" prefix from the model class is omitted, and the property is written in headless camel case. Example: `getRoleConfig()` method name results in `roleConfig` property name.


### Role definitions

Example role definitions:<br/>
https://github.com/wcm-io-devops/conga/tree/develop/tooling/conga-maven-plugin/src/it/example/definitions/src/main/roles

The filename of the role YAML file is the role name.

The documentation of all role and file configuration options can be found in the<br/>
[Role Model API documentation][role-model].

#### Files

Files are generated using a Handlebars template. After generation a file header is added automatically, and the file syntax is checked for well-formedness. Optionally additional post-processors can be configured.

Alternatively it is possible to specify an URL instead of a template. In this case the file is copied/downloaded from an external source. The following URL prefixes are supported out of the box:

- `file:` - Absolute filesystem path
- `file-node:` - Filesystem path relative to the node root in the target directory of generated files
- `classpath:` - Classpath resource reference
- `http://` or `https://` - External URL
- `mvn:` - Maven Artifact coordinates (only supported when CONGA runs inside Maven)
    - Maven Coordinates Syntax 1 ([Maven-style][artifact-coords-maven]): `groupId:artifactId[:packaging][:classifier]:version`
    - Maven Coordinates Syntax 2 ([Pax URL-style][artifact-coords-paxurl]): `groupId/artifactId/version[/type][/classifier]`
    - `classifier` and  `type` are optional
    - if the version is empty in the role file it is resolved from the Maven project

If no prefix is specified the URL is interpreted as path relative to the POM root directory in the local filesystem.

#### Role Inheritance

A role can inherit from one or multiple other roles:

```yaml
inherits:
- role: superRole
```

In this case the current role inherits all configuration and files from the super role(s). Configuration maps are merged, the config of the current role has higher precedence. If the super role defines variants, the current has to define the same variants as well. Files in the current role with the same target file name as a file in a super role have higher precedence than the files from the super role.


### Environment definitions

Example environment definitions:<br/>
https://github.com/wcm-io-devops/conga/tree/develop/tooling/conga-maven-plugin/src/it/example/environments/src/main/environments

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

Within the configuration parameters maps other parameters can be referenced using a variable notation. Example: `${group1.param1}`. Nested variable references are supported, just make sure you define no cyclic dependencies.

Configuration parameter maps are inherited to "deeper levels" within the YAML structure, and the maps are merged on each level. The configuration parameters on the "deeper levels" overwrite the parameters from the higher level.

Inheritance order (higher number has higher precedence):

1. Global configuration parameters from role definition
2. Configuration from role variant definition
    * If multiple variants are assigned to a node/role their configs are merged, first variants have higher precedence
3. Global configuration from environment
4. Node configuration from environment
5. Global role configuration from environment
6. Role configuration from node
7. Variant configuration from node
8. Configuration from multiply plugins, e.g. the tenant-specific configuration

There is a special support when merging list parameter. By default a list value on a deeper lever overwrites a list inherited from a parameter map on a higher level completely. If you insert the keyword `_merge_` as list item on either of the list values, they are merged and the special keyword entry is removed.


### Variable references

You can reference configuration parameter values defined in the environment, or as default values in the roles using this variable syntax:

```
${myvariable}
${mygroup.myvariable}
```

If no value is defined for this variable, the configuration generation fails. It is recommended to define a default value in the role definition for each variable it uses. An alternative is to define a default value within the variable reference like this:

```
${myvariable:defaultValue}
${myvariable:defaultListItem1,item2,item3}
```

You can reference values from external source via value providers (see [Extensibility model][extensibility]), e.g. from the "System Parameter Value Provider":

```
${system::my.system.parameter}
${system::my.system.parameter:defaultValue}
```

Instead of single variable expression you can also use Java Expression Language ([JEXL][jexl]). This cannot be combined with value provider expressions or default values. Examples:

```
${myvariable1 + '/' + myvariable2}
${mygroup.myvariable == 'expected_value'}
${mynumber + 1}
${new('java.text.DecimalFormat','000').format(multiplyIndex)}
${stringUtils:join(listParam,'|')}
```

When you escape the dollar sign with a backslash the variable is untouched by CONGA:
```
\${my-static-string}
```


### Iterate over variable list values

When reading a variable with list of values from external source via a value provider, it may be required to generate a list of configuration statements within an environment definition. In this case the `_iterate_` keyword can be used.

Example from an environment definition file:

```
- node: author1
  roles:
  - role: aem-cms
    variant: aem-author
  config:
    replication.author.publishTargets:
      _iterate_: ${system::publishTransportUrls}
      name: publish${_itemIndex_}
      host: ${_item_}
      transportUser: ...
      transportPassword: ...
```

In this example a list of hostnames/transport URLs is read from system parameter `publishTransportUrls`. For each item a configuration block is generated. The following implicit variables can be references inside the 'iterate' block:

* `_item_`: The list item value
* `_itemIndex_` The list item index (starting with 0)

The example above results in an environment configuration like this when the configuration is generated:

```
- node: author1
  roles:
  - role: aem-cms
    variant: aem-author
  config:
    replication.author.publishTargets:
    - name: publish0
      host: http://publish1:4503
      transportUser: ...
      transportPassword: ...
    - name: publish1
      host: http://publish2:4503
      transportUser: ...
      transportPassword: ...
```


### Default context properties

Additionally to the variables defined in the configuration parameter maps a set of default context properties are defined automatically by CONGA and merged with the parameter maps:

| Property             | Description
|----------------------|-------------
| `version`            | Environment version
| `nodeRole`           | Current node role name
| `nodeRoleVariant`    | Current node role variant name (only set if the role has exactly one variant)
| `nodeRoleVariants`   | List of current node role variant names
| `environment`        | Environment name
| `node`               | Current node name
| `nodes`              | List of all nodes. Each node has properties as defined in the [Node model][node-model].
| `nodesByRole`        | Map with node roles with each entry containing a list of all nodes with this role. Each node has properties as defined in the [Node model][node-model].
| `nodesByRoleVariant` | Map with node roles with each entry containing a map with node role variants each entry containing a list of all nodes with this role and variant. Each node has properties as defined in the [Node model][node-model].
| `tenants`            | List of all tenants. Each tenant has properties as defined in the [Tenant model][tenant-model].
| `tenantsByRole`      | Map with tenant roles with each entry containing a list of all tenants with this role. Each tenant has properties as defined in the [Tenant model][tenant-model].
| `tenant`             | Current tenant name. This is only set if the tenant multiple plugin is used.
| `tenantRoles`        | List of current tenant's role names This is only set if the tenant multiply plugin is used.
| `multiplyIndex`      | File index (starting with 0) if a multiply plugin is used.


[role-model]: generator/apidocs/io/wcm/devops/conga/model/role/Role.html
[environment-model]: generator/apidocs/io/wcm/devops/conga/model/environment/Environment.html
[node-model]: generator/apidocs/io/wcm/devops/conga/model/environment/Node.html
[tenant-model]: generator/apidocs/io/wcm/devops/conga/model/environment/Tenant.html
[yaml]: http://yaml.org/
[snakeyaml]: https://github.com/snakeyaml/snakeyaml
[extensibility]: extensibility.html
[artifact-coords-maven]: https://maven.apache.org/pom.html#Maven_Coordinates
[artifact-coords-paxurl]: https://ops4j1.jira.com/wiki/x/CoA6
[jexl]: http://commons.apache.org/proper/commons-jexl/

## CONGA - Handlebars quickstart

For a full documentation of handlebars syntax see [Handlebars][handlebars] and [Handlebars Java][handlebars-java] websites. This page lists a collection of handlebars expressions that are most useful when writing CONGA templates.

See also [Custom Handlebars expressions][handlebars-helpers].

### Variable references

To insert a variable from configuration parameter maps with escaping (escaping strategy depending on file type):

```
{{group1.param1}}
```

By default you should always use this syntax with escaping applied.

To insert a variable without escaping (you have to take care of generating a valid file yourself):

```
{{{group1.param1}}}
```

See [YAML definition][yaml-definitions] for more information about configuration parameter maps.


### Conditions

To conditionally generate a block:

```
{{#if group1.flag1}}
  condition met block...
{{/if}}
```

Optionally you can define an else block:

```
{{#if group1.flag1}}
  condition met block...
{{else}}
  condition not met block...
{{/if}}
```


### For each loop

To loop about a list of values:

```
{{#each group1.list}}
  {{this.param1}}
{{/each}}
```

If you want to add a separator between each item but not after the last:

```
{{#each group1.list}}
  "{{this.param1}}"{{#unless @last}},{{/unless}}
{{/each}}
```

To insert the list index from the for each loop:

```
{{#each group1.list}}
  "prop{{@index}}": "{{this.param1}}",
{{/each}}
```


### Whitespace handling

You can control whitespace handling around handlebar expressions by inserting `~` at the beginning or end of the handlebars expression. On the side of this expression all whitespaces are removed up to the next handlebars expression or non-white space content.

Example: Remove all whitespaces inside the expression:

```
{{#if group1.flag1 ~}}
  conditional block...
{{~/if}}
```

Example: Remove all whitespaces around the expression:

```
{{~#if group1.flag1}}
  conditional block...
{{/if ~}}
```


### Partials and blocks

If you want to modularize your templates and reused a shared set of content or expressions in multiple templates you can use partials and blocks.

Example of a file with shared content/expressions using blocks:

```
{{#block "serverName"}}
  ServerName {{group1.serverName}}
{{/block}}

{{#block "documentRoot"}}
  DocumentRoot "{{group1.rootPath}}"
{{/block}}

{{#block "logSettings"}}
  LogLevel warn
  ErrorLog ${APACHE_LOG_DIR}/error.log
  CustomLog ${APACHE_LOG_DIR}/access.log combined
{{/block}}
```

To include this file in a main template:

```
... main template start

{{> role1/mypartialtemplate.conf.hbs}}

... main template end
```

You can overwrite parts from the shared file by overwriting single blocks with a partial:

```
... main template start

{{#partial "serverName"}}
  ServerName {{group1.otherServerName}}
  ServerAlias {{group1.aliasName}}
{{/partial}}

{{> role1/mypartialtemplate.conf.hbs}}

... main template end
```


### Comments

To include a comment that is stripped from the generated file:

```
{{!-- my comment --}}
```



[handlebars]: http://handlebarsjs.com/
[handlebars-java]: https://github.com/jknack/handlebars.java
[handlebars-helpers]: handlebars-helpers.html
[yaml-definitions]: yaml-definitions.html

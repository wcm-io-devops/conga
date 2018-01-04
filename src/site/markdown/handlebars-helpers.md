## CONGA - Custom Handlebars expressions

By using CONGA handlebars helper plugins it is possible to extend handlebars by registering custom expressions. Out of the box CONGA ships with a set of built-in custom expressions documented in this chapter. See [Extensibility model][extensibility] how to register you own helpers.

The basic handlebars expressions are documented in the [Handlebars quickstart][handlebars-quickstart].


### regexQuote

To insert a variable expression and applying regex quoting on it:

```
{{regexQuote group1.param1}}
```


### join

To join a list of values with a separator character:

```
{{join group1.list ","}}
```


### ifEquals

Conditional if statement - block is rendered if expression equals to an argument:

```
{{#ifEquals group1.param1 "myValue"}}
  condition met block...
{{/ifEquals}}
```


### defaultIfEmpty

To insert a default value if the given variable expression is not set:

```
{{defaultIfEmpty group1.param1 "defaultValue"}}
```


### eachIf

Conditional for each loop - loop is generated if condition is true:

```
{{#eachIf group1.list "group1.flag1"}},
  conditional loop block
{{/eachIf}}
```


### eachIfEquals

Conditional for each loop - loop is generated if expression equals to an argument:

```
{{#eachIfEquals group1.list "group1.param1" "myValue"}},
  conditional loop block
{{/eachIfEquals}}
```


### contains

Checks for presence of a given value in a list:

```
{{#contains group1.list1 "myValue"}}
  condition met block...
{{/contains}}
```


### ensureProperties

Ensure that all properties with the given names are set. Build fails if this is not the case, the exception message contains the missing properties.

```
{{ensureProperties "group1.prop1" "group1.prop2"}}
```



[handlebars-quickstart]: handlebars-quickstart.html
[extensibility]: extensibility.html

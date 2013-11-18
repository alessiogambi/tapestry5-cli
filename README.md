tapestry5-cli
=============

Tapestry5-cli is a library for managing command line inputs (CLI) that builds upon [tapestry-ioc](http://tapestry.apache.org/ioc.html), the inversion of control framework underlying the [Tapestry5 Web framework](http://tapestry.apache.org/).

The goal of the tapestry5-cli library is to implement a service to parse the command line, validate the options and inputs provided by the user, and provide the resulting values to Tapestry5 enabled applications.

Tapestry5-cli targets mainly Non-Web applications; nevertheless, it can be used also in 'standard' Tapestry5 Web applications.

This README file describes the high level view and the main functionalities provided by tapestry5-cli.
The docs folder contains additional documentations.

# How does it work?

Tapestry5-cli implements a service called `CLIParser` that offers the following method:
```java
public void parse(String[] args) throws ParseException, ValidationException;
```
The parser receives the `String[] args`, which is the command line input of the program, and processes it according to the following logic:  
1. Parse the input according to the specifications contributed by the user.  
2. Validate the value(s) of single options and inputs.  
3. Validate the combinations of values from multiple options and inputs at once.  

If the entire process works fine the values of the options and the inputs can be accessed through a second service called `CLIOptionSource`.
Options ans input values can also be injected in Tapestry5 managed objects using the following annotations: `@CLIOption` and `@CLIInput`.
This mechanism is inspired by the `SymbolSource` and related services.

If the parsing fails, a `ParsingException` is raised, while if the validation fails a `ValidationException` is raised;
in both cases, the console will print out the usage message and a message that explains to the user why the exception was raised.

# Main Configurations

Users can configure this library by exploiting the facilities provided by tapestry-ioc and the other frameworks used.

## Specify the Main Command

At the moment, the main command symbol (`CLISymbolConstants.COMMAND_NAME`) is the only mandatory configuration that must be provided.
This symbol specifies the name of your application/command and must be contributed as tapestry symbols.
At the moment, the comman name is used only to display the help message.
  
For example, we can specify the command name inside an application module as an `ApplicationDefaults` contribution:  

```java
public void contributeApplicationDefaults(MappedConfiguration<String, String> configuration) {
		configuration.add(CLISymbolConstants.COMMAND_NAME, "test");
}
```

## Specify the Options
The options are used to organize the string provided on the command line.
Each option is identified by a letter (its short name), and a human-friendly name (its long notation).  

In tapestry5-cli, options are defined by contributing instances of the `CLIOption` class to the
`CLIParser` service, as shown below:  

```java
public void contributeCLIParser(Configuration<CLIOption> contributions) {

	contributions.add(new CLIOption("a", "alfa", 1, true, "Alfa option is very important"));
	contributions.add(new CLIOption("b", "beta-option", 0, false, "Beta is a flag as it does not require any input"));
	
	CLIOption charlie = new CLIOption("c", "charlie", 1, true, "Charlie is also important.");
	charlie.setDefaultValue("foo-bar");
	contributions.add( charlie );
	
	contributions.add(new CLIOption("g", "the-gamma", 2, false, "Gamma requires 2 inputs but it is not a mandatory option"));
}
```
Each contribution must specify the short notation for the option (e.g., `h`), the long notation (e.g., `help`),
the number of inputs to be provided (0, 1, or more), if the option is mandatory/required,
and a brief description of the option that will used inside the automatically generated usage message.
Options can have default values (see CLIOption charlie above).

The configurations can be distributed across modules, and users might end up (re-)defining options.
During the instantiation of `CLIParser`, the tapestry5-cli checks for *conflicting* definitions:
Conflicting definitions are identified by the same short notation but different long notation, or *vice-versa*;
conflicting definitions are also identified when their notations match but the expected number of parameters differs,
or if two definitions set different default values.  

If definitions provide different descriptions, or different *required* attributed, or a null and a non-null default value,
they are merged according to the following rules:  
- Descriptions are appended one after the other without a specific order;  
- Stronger requirements are maintained. In other words, if two options with same notations but different *required* parameter are contributed, the library will merge them into a new options with strictest *required* attribute, i.e., `required=true`;
- Non-null default values are maintained.  

*Note*: this rules are hard-coded, therefore cannot be configured by the user.

## Specify the Application Configuration Objects

To validate the values of options the tapestry5-cli resorts to a simplified version of another project, tapestry-beanvalidator[[1],[2]], that brings the power of JSR303 bean-validation into Tapestry's world.
JSR-303 requires that fields/methods of classes are annotated with specific annotations to describe how to validate them;
furthermore, the libraries that provide the implementation of JSR-303 standard (e.g., hibernate-validator[4]) may offer additional validators (and annotations).

To specify how options must be validated users provide contribution to the `ApplicationConfigurationSource` service.
The service uses `MappedConfiguration`s and accepts Objects that adhere to the JavaBean standard;
these objects carry the JSR-303 annotations on their field, and at runtime, tapestry5-cli (with the help of tapestrybean-utils by Apache[5]), will provide the input values (if any) right before checking their validity.
Once the values are in place, the `ApplicationConfiguration` object will be created and validated.

To match Bean Properties to Options, tapestry5-cli relies on:
- `@CLIOption(...)` annotations placed on bean fields/properties;
- a naming convention that matches properties' name to corresponding options' long notations.
In particular, dash characters are removed and long options are transformed in bean property names in Camel notation.
Tapestry5-cli uses `TypeCoercer` to instantiate the "right" object.

For example, we can define a Java Bean `MyApplicationConf` and contribute it to the `ApplicationConfigurationSource`:  

```java
	public class MyApplicationConf {

	@CLIOption(shortName = "a")
	private Integer foo;

	@CLIOption(longName = "the-gamma")
	private String[] bar;

	// This will match the --charlie option
	private Long charlie;

	public Integer getFoo() {
		return foo;
	}

	public String[] getBar() {
		return bar;
	}

	public Long getCharlie() {
		return charlie;
	}

}
```
*Note*: setters are not showed in the example but must be specified. Moreover, the example shows a plain bean, but in general there may be some logic inside the class  (as long as it respects the Java Bean rule), and beans can also be nested.

```java

public void contributeApplicationConfigurationSource(
			MappedConfiguration<String, Object> configuration) {
		configuration.addInstance("MyApplication", MyApplicationConf.class);
	}
```

Users can contribute different bean objects, and organized the validation constraints in a distributed fashion.
In fact, the same option can be assigned multiple times to object of different types that have different validation constraints.

*Note*: in general it is more preferrable to use the annotations over naming conventions because annotations are in general refactoring-safe.

### Basic validators

Basic validators are provided by JSR-303 or by its implementation.
Custom validators can be provided according to the JSR-303 specification.
In this case, tapestry5-cli does not an explicit contribution inside the code of the user application because custom
validators are directly managed by the underlying JSR-303 implementation.

### Complex validators

Users can define custom validators that check conditions over properties of nested beans, in this way,
complex conditions that combines multiple values from multiple beans can be easily defined.

### Tapestry5-based validators

Custom validators can also exploit services that are provided via tapestry-ioc (see [[3]]).
For example, if one of the input values must be chosen among a set of contributions to be valid,
we can inject the contributed object and use the actual values during the validation (see the docs for more details).
In this case, users need to explicitly contribute the services according to the tapestry rules.


## Add CLIValidators

JSR-303 validators cover all the cases, but the situations where users need to combine the values from multiple inputs
that belong to different - independent - beans (Nested beans belong to the former cases!), or if they want to express conditions
to the options/inputs without the need of contributing bean objects.
A typical example is to check if two options (`a` and `b`) are specified at the same time, or if an option is specified while the another is not (`a` XOR `b`).
In this case, users can contribute `CLIValidator` objects and implement a different form of validation.
CLIValidator objects can access the values of the options parsed and partially validated, and they can provide additional logic to check the validity of their combination.

# Accessing the Options/Inputs

Parsed options and additional inputs can be accessed by means of the `CLIOptionSource` service iff both the parsing and validation succeed.
Options can be accessed by "name" while inputs are accessed by position.

# Usage example

There are test cases the show how to use these services.

[1]: http://tapestry.apache.org/bean-validation.html
[2]: http://blog.tapestry5.de/index.php/2010/01/04/tapestry-and-jsr-303-bean-validation-api/
[3]: http://tawus.wordpress.com/2011/05/12/tapestry-magic-12-tapestry-ioc-aware-jsr-303-custom-validators/
[4]: http://www.hibernate.org/subprojects/validator.html
[5]: http://commons.apache.org/proper/commons-beanutils/

tapestry5-cli
=============

A library to manage command line inputs (CLI) that builds upon tapestry-ioc, the inversion of control framework underlying the Tapestry5 Web framework.

The goal of the tapestry5-cli library is to implement a service to parse the command line, validate the options and inputs provided by the user, and provide the resulting values to tapestry5 enabled applications.

Tapestry5-cli targets mainly Non-Web applications; nevertheless, it can be used also in 'standard' Tapestry5 Web applications.

This README file describes the high level view and the main functionalities provided by the framework. The docs folder contains additional documentations.

# How does it work?

Tapestry5-cli provides a service, called `CLIParser`, that offers the following method:
```java
public void parse(String[] args) throws ParseException, ValidationException;
```
The parser receives the `String[] args`, which is the command line input of the program, and process the inputs with the following logic:
1. Parse the input according to the specifications contributed by the user. 
2. Validate the value(s) of single options and inputs. 
3. Validate the combinations of values from multiple options and inputs at once.

If the entire process works fine the values of the options and the inputs can be accessed through a service called `CLIOptionSource`, or they can be injected in the code via the following annotations: `@CLIOption` and `@CLIInput`.
The mechanism provided in this way resembles closely the one provided by the tapestry-ioc libraries under the name of `SymbolSource`.

If the parsing fails, a `ParsingException` is raised, while if the validation fails a `ValidationException` is raised; in both cases, the console will print out the help message and a message to explain the user why the exception was raised.

# Contributions

Users can configure the library in different places by exploiting the facilities provided by the tapestry-ioc framework or by the other frameworks employed in the library.

## Specify the Main Command
At the moment, the main command symbol (`CLISymbolConstants.COMMAND_NAME`) is the only mandatory configuration that must be provided.
This symbol specifies the name of your application/command and it is used to diplay the help message and can be contributed as any other tapestry symbols.
For example, inside the application defaults:  
```java
public void contributeApplicationDefaults(MappedConfiguration<String, String> configuration) {
		configuration.add(CLISymbolConstants.COMMAND_NAME, "test");
}
```

*Note*: this feature may be removed in the future releases of the library.

## Specify the Options
Options are defined by contributing `Option` instances to the `CLIParser` service, as shown in the following.
Each contribution must specify the short notation for the option (for example, `-h`), its long notation (e.g., `--help`), the number of inputs required (0 or more), if the option is mandatory, and a brief description of the option.

```java
public void contributeCLIParser(Configuration<Option> contributions) {

	contributions.add(new Option("a", "alfa", 1, true, "Alfa option is very important"));
	contributions.add(new Option("b", "beta-option", 0, false, "Beta is a flag as it does not require any input"));
	contributions.add(new Option("c", "charlie", 1, true, "Charlie is also important."));
	contributions.add(new Option("g", "the-gamma", 2, false, "Gamma requires 2 inputs but it is not a mandatory option"));
}
```

As the configurations might be distributed across several modules and contributions, the library checks for duplicate or conflicting definitions.
Conflicting definitions are identified by the same short notation but different long notation, or viceversa; conflicting definitions are also identified when the notations match but the expected number of parameters differs.
Definitions that have different descriptions, or different *required* attributed are merge according to the following rule: Descriptions are appended one after the other, while stronger requirements are maintained. In other words, if two options with same notations and expected parameter have different required attribute, the library force the strictes one, i.e., required.

*Note*: this rules are encoded and cannot be configured. They might possibly change in future releases.

## Specify the Application Configuration Objects
To validate the values of options the tapestry5-cli resort to another project, called tapestry-beanvalidator[1][2], that brings the power of JSR303 bean-validation into Tapestry's world.
JSR303 requires that fields/methods of classes are annotated with specific annotations to specify how to validate them;
furthermore, the libraries that provide the implementation of the JSR 303 standard (e.g., hibernate-validator[4]) may offer additional annotations, therefore validators, to be used.

To specify how options must be validated users provide contribution to the `ApplicationConfigurationSource` service.
The service uses `MappedConfiguration`s and accepts Objects that adhere to the Java Bean standard;
these objects carry the JSR-303 annotations inside, and at runtime, tapestry5-cli (with the help of tapestrybean-utils by Apache[5]), will provide the input values (if any) right before checking their validity.

To match Bean Properties to Options, tapestry5-cli relies on:
- `@Option(...)` annotations placed on bean fields/properties;
- a naming convention that matches properties' name to corresponding options' long notations.  

For example, we can define a Java Bean `MyApplicationConf` and contribute it to the `ApplicationConfigurationSource`:  

```java
	public class MyApplicationConf {

	@CLIOption("a")
	private Integer foo;

	@CLIOption("the-gamma")
	private String[] bar;

	private Long charlie;

	public Integer getFoo() {
		return foo;
	}

	public String[] getBar() {
		return bar;
	}

	// We'll use the naming convention here
	public Long getCharlie() {
		return charlie;
	}

}
```
*Note*: setters are not showed in the example but must be specified. Moreover, the exampls shows a very simple bean, but in general there may be some logic inside the class  (as long as it respects the Java Bean rule), and beans can also be nested.

```java

public void contributeApplicationConfigurationSource(
			MappedConfiguration<String, Object> configuration) {
		configuration.addInstance("MyApplication", MyApplicationConf.class);
	}
```

Users can specify as many contributions as the like, meaning that the can contribute different bean objects.
Moreover, leveraging the power of distributed configurations, they can also contribute different objects in different modules.

*Note*: in general it is more preferrable to use the annotations over naming conventions.

### Basic validators
Basic validators are provided by JSR303 or by its implementation. And custom validators can be provided according to the JSR303 specification.
In this case, tapestry5-cli does not an explicit contribution inside the code of the user application because custom validators are directly managed by the JSR303 implementation.

### Advanced validators
Custom validators can also exploit services that are provided via tapestry-ioc[3]. For example, if one of the input values must be chosen among a set of contributions to be valid, we can inject the contributed object and use the actual values during the validation (see the docs for more details).
In this case, users need to explicitly contribute the services according to the tapestry rules.

## Add CLIValidators
JSR303 validators cover all the cases, but the situations where users need to combine the values from multiple inputs that belong to different - independent - beans (Nested beans belong to the former cases!)
For example, if users  need to check if two options are specified at the same time, or if an option is specified while another is not, while each of the options must be valid by itself, they must resort to a different form of Validation.
In this case, users can contribute `CLIValidators` that can access the values of the options parsed, and partially validated, so far (but that are not yet available inside CLIOptionSource), and can provide additional logic to check any combination of them.

# Null and Optional values
[TODO]

# A Simple Example


# References
[1]: http://tapestry.apache.org/bean-validation.html
[2]: http://blog.tapestry5.de/index.php/2010/01/04/tapestry-and-jsr-303-bean-validation-api/
[3]: http://tawus.wordpress.com/2011/05/12/tapestry-magic-12-tapestry-ioc-aware-jsr-303-custom-validators/
[4]: http://www.hibernate.org/subprojects/validator.html
[5]: http://commons.apache.org/proper/commons-beanutils/

tapestry5-cli
=============

A library to manage command line inputs (CLI) that builds upon tapestry-ioc, the inversion of control framework underlying the Tapestry5 Web framework.

The goal of the tapestry5-cli library is to implement a service to parse the command line, validate the options and inputs provided by the user, and provide the resulting values to tapestry5 enabled applications.

Tapestry5-cli targets mainly Non-Web applications; nevertheless, it can be used also in 'standard' Tapestry5 Web applications.

# How does it work?

Tapestry5-cli provides a service, called `CLIParser`, that offers the following method:
```java
public void parse(String[] args) throws ParseException, ValidationException;
```
The parser receives the `String[] args`, which is the command line input of the program, and process the inputs with the following logic:
1. Parse the input according to the specifications contributed by the user.
2. Validate the value(s) of single options and inputs.
3. Validate the combinations of values from multiple options and inputs at once.

If the entire process works fine the values of the options and the inputs can be accessed through a service called `CLIOptionSource`, or they can be injected in the code via the following annotations: `CLIOption` and `CLIInput`.
The mechanism provided in this way resemble closely the one provided by the tapestry-ioc libraries under the name of `SymbolSource`.

If the parsing fails, a `ParsingException` is raised, while if the validation fails a `ValidationException` is raised; in both, the console will print out the help message and a message to explain the user why the exception was raised.

# User Contributions


## Specify the Main Command
This is mandatory

## Specify the Options

## Specify the Application Configuration Objects
This is used to enable the JSR303 bean-validation.

### Basic validators
Validators already provided by the JSR303 implementation or custom validators provided according to the JSR303.
Note this do not require explicit contribution inside the code of the application because is directly managed by the JSR303 implementation.

### Advanced validators
Validators that use Tapestry5 provided services to validate the input.
Note this may require an explicit contribution to provide the services used inside the validator, if not build/bind so far.

## Add CLIValidators
JSR303 validators cover all the cases, but the situations where we need to combine the values form multiple sources (options/inputs) together.
For example, we need to check if two options are specified at the same time, or if an option is specified while another is not.
In this case, we can provide CLIValidators that receive the values of the options elaborated so far (but NOT yet available inside CLIOptionSource), and can provide additional logic to check any combination of them.

# Null and Optional values
[TODO]
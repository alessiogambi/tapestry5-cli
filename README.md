tapestry5-cli
=============

A library to manage Command Line Input the "Tapestry5 way"

The goal of the library is to provide services to parse and validate the CLI; tapestry5-cli targets mainly Tapestry 5 Non-Web applications but can be used also for regular Tapestry 5 projects.

It works by receiving the ``code String[] args`` of the input command line, parse them according to user provided options, validate inputs and options (again, according to user specified options). If the entire process works fine, options, their values, and all the inputs will be exported a symbols to be used inside the application. If the parsing fails, a ``ParsingException`` will be generated, and if the validation fails, a ``Validation Exception`` will be generated; in such cases, the console will print out the help message.

This library is more a proof of concept prototype than a product, and I work on it to acquire more experience in using Tapestry-IoC. In particular, I am trying to investigate (and evaluate) the different options/design-alternatives to implement it.

* Solution 1.3

Study the following feature: Define an OptionSource or CLIOptionSource service that mimics SymbolSource and that gives direct access to the CLI options (by name.ref) and additional CLI inputs (by position)

Values can be accessed either by injecting the service or by using Parameter/Field annotations like @Symbol(string="key")

The solution uses a CLIOptionSource to access the parsed value of the options/inputs. So we need a way to put/inject those values inside it.
The values are generated inside the CLIParser by means of (direct code/injected service?), before we used a RuntimeSymbol Provider. Therefore, we can exploit the same mechanism
and define a particular CLIOptionProvider that we can inject in two places, and that receives the values from the CLIParser.

This is implemented by having the CLIOptionProvider and the CLIParser receving the same RuntimeSymbolProvider at construction time.
It looks like an hack, but it should work. In this way we also remove the ugly RuntimeSymbols from SymbolSource because they violated (not tested) the meaning of symbols.

Otherwise we need to setup somekind of Environmental service that maintans the shared values and is injected in all the places. 

Maybe thinking the CLI as user request may be beneficial for the future:
- Interactive CLI
 Client/Server architecture for the registry, i.e., an application that may receive commands via CLI that hits the same registry (server)

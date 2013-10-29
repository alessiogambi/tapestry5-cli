tapestry5-cli
=============

A library to manage Command Line Input the "Tapestry5 way"

The goal of the library is to provide services to parse and validate the CLI; tapestry5-cli targets mainly Tapestry 5 Non-Web applications but can be used also for regular Tapestry 5 projects.

It works by receiving the ``code String[] args`` of the input command line, parse them according to user provided options, validate inputs and options (again, according to user specified options). If the entire process works fine, options, their values, and all the inputs will be exported a symbols to be used inside the application. If the parsing fails, a ``ParsingException`` will be generated, and if the validation fails, a ``Validation Exception`` will be generated; in such cases, the console will print out the help message.

This library is more a proof of concept prototype than a product, and I work on it to acquire more experience in using Tapestry-IoC. In particular, I am trying to investigate (and evaluate) the different options/design-alternatives to implement it.

* Solution 1.3

Study the following feature: Define an OptionSource or CLIOptionSource service that mimics SymbolSource and that gives direct access to the CLI options (by name.ref) and additional CLI inputs (by position)

Values can be accessed either by injecting the service or by using Parameter/Field annotations like @Symbol(string="key")

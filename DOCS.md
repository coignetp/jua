## Architecture

Let's describe the architecture of the project. We'll start by the first parts which have been built. It explains how a given string, representing some *lua* code, is processed and interpreted.

![architecture](./docs/jua.png)

There is also a class diagram available [here](./docs/jua_classes.png).

### Lexer

[`/src/main/java/jua/lexer`](./src/main/java/jua/lexer)


![lexer](./docs/lexer.png)


The job of the *Lexer* is to transform the input (a stream of strings) into a stream of *Tokens*.

#### Tokens

*Tokens* are the first abstraction around the text input. Basically, it separates words and punctuation, and try to give them some meaning.
We have several types of Tokens :

- **Operator**: these are all the symbols which operate on arguments: *+*, *-*, */*,etc...
- **Keyword**: Reserved names for the language like *if*, *for*, *end*, etc...
- **Separator**: Punctuation characters, delimiters like : *(*, *}*, *,*, etc...
- **Literal**: explicit number, textual, data: *3.4*, *0xff*, *"hello"*
- **Identifier**: name chosen by the programmer for a variable, function: *x*, *myFunction*
- **Special**: invalid token, end of line

The tokens for the *jua* language are defined in [src/main/java/jua/token](./src/main/java/jua/token). There is a main **abstract** class [Token](./src/main/java/jua/token/Token.java) from which all the the other tokens ([*TokenOperator*](./src/main/java/jua/token/TokenOperator.java), [*TokenKeyword*](./src/main/java/jua/token/TokenKeyword.java), etc...) inherit. The base attributes a Token has are : line, position on this line and literal, which is the explicit *string* from the code.

*Enumerations* are used to list all the explicit tokens we expect to encounter inside a given token type.

A [*TokenFactory*](./src/main/java/jua/token/TokenFactory.java), which follows the **Factory Pattern**, is used to instantiate all the Tokens.

#### How does the Lexer works ?

By looking *character by character*, the **Lexer** returns a stream of Tokens. With a simple *switch statement*, it can determinate the correct token. If we found a `(`, it returns a `TokenDelimiter("(")` .

Sometimes, for example for `*<=*`, it needs to look ahead for the next characters. Therefore, when it finds a `<`, it *peeks* the next characters, to distinguish `<<` , `<=` or just `<`.

Then, it handles others tokens like literals and identifiers.

### Parser

![parser](./docs/parser.png)

The job of **Parser** transforms the stream of Tokens from the **Lexer** into an **Abstract Syntax Tree** (AST).

From [Wikipedia](https://en.wikipedia.org/wiki/Abstract_syntax_tree):
> In computer science, an abstract syntax tree (AST), or just syntax tree, is a tree representation of the abstract syntactic structure of source code written in a programming language. Each node of the tree denotes a construct occurring in the source code. 

For example for a simple statement like : 

```lua
x = 1
y = 2
3 * (x + y)
```

We'll have this AST: 

![ast](./docs/ast.png)

The AST is composed of two types of elements : *Expressions* and *Statements*.

A *statement* is the base of the program, something to execute. A lua program is list of *statements*: variable definition of assignements, function definition or procedure execution. A statement is composed of several others statements and expressions.
An *expression* is an arithmetic combination of operator and function calls that returns a result, we parse them with the [Pratt algorithm](https://en.wikipedia.org/wiki/Recursive_descent_parser).

We have a StatementParser interface implemented for each type of statement that operates on the token stream to look up the type of the next statement and parse it.
Similarly we have expression parsers interfaces, InfixParser and PrefixParser implemented for each type of expression and used in the Pratt algorithm.

Let's take an if statement as example:
- to know if the next *statement* is an if we simply compare the next token with the keyword 'if'.
- an if statement is composed of condition *expression*, and two substatements, the consequence and the alternative, with the alternative being facultative.
When parsing an if *statement* we have to parse recursively the *expression* and the *statements*, taking care of the separators: `if expression then consequence else alternative end`.

### Evaluator

The jua evaluator is a tree walking evaluator, it simply visits each node of the AST and evaluate them.

This is possible because each AST node implement the [Evaluable](./src/main/java/jua/evaluator/Evaluable.java) interface.

This works for both Expressions and Statements.

An example of AST evaluations can be found [here for the multiplication](./src/main/java/jua/ast/ExpressionMultiplication.java)

### Testing

[`src/test/java/jua/{evaluator, lexer, parser}`](./src/test/java/jua/)

We have done a lot of tests trough the development of the application. Since we first developed the Lexer, then the Parser followed by the Evaluator, we wrote tests for each steps of this process which can assure that each part of the application does its jobs correctly.

After the first working version, we had to add some new features like a new *keyword* or *bitwise operators*, for which a change in the three services were necessary. In this case, all these tests permit to develop without worrying about what have been done before.

To implement more tests in a simple way, we also use a directory with some [lua scripts](./src/test/java/jua/evaluator/testdata/). A test function in `EvaluatorTest.java` compares all the `myTestFile.lua` to the corresponding expected results in `myTestFile.expected`. Add new test is therefore very easy, just write a new `script.lua` and generate the output `lua script.lua > script.expected`.
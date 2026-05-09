# LOGO LSP Server

A lightweight Language Server Protocol implementation for the LOGO programming language.

LOGO is a high-level educational programming language known for its simple syntax and turtle graphics. Since LOGO does not have one strictly defined semantics, this project follows a consistent interpretation of common LOGO constructs and documents the assumptions made during implementation.

## Features

This language server provides editor support for a documented subset of common LOGO constructs:

- Semantic-token based syntax highlighting for LOGO keywords, turtle commands, procedure names, variables, numbers, comments, operators, and brackets
- Go-to-declaration for procedure references
- Go-to-declaration for variable references declared as procedure parameters
- Diagnostics for simple syntax and reference errors, such as unknown procedures, unknown variables, invalid procedure declarations, and missing `end` statements

## Supported LOGO Constructs

The server recognizes common LOGO constructs such as:

- turtle commands, for example `forward`, `back`, `left`, `right`
- procedure definitions using `to ... end`
- procedure calls
- variables referenced with `:`
- comments
- numeric literals
- identifiers
- brackets and simple operators

Example:

```logo
to square :size
  repeat 4 [
    forward :size
    right 90
  ]
end

square 100
```

## Example Interaction

The following example shows how the language server reacts to a simple LOGO file.

Example file `example.logo`:

```logo
to square :size
  repeat 4 [
    forward :size
    right 90
  ]
end

square 100
```

When this file is opened in an LSP-compatible editor, the server provides the following behavior.

### 1. Syntax Highlighting

The server recognizes and highlights LOGO elements such as:

- `to` and `end` as procedure definition keywords
- `repeat` as a control keyword
- `forward` and `right` as turtle commands
- `square` as a procedure name
- `:size` as a variable reference
- `100` and `90` as numeric literals
- `[` and `]` as brackets

### 2. Go-to-Declaration for Procedures

If the cursor is placed on the procedure call:

```logo
square 100
```

and **Go to Declaration** is triggered, the editor jumps to the procedure definition:

```logo
to square :size
```

### 3. Go-to-Declaration for Variables

If the cursor is placed on the variable reference:

```logo
forward :size
```

and **Go to Declaration** is triggered, the editor jumps to the parameter declaration in the procedure header:

```logo
to square :size
```

### 4. Diagnostics for Unknown Variables

For example:

```logo
to square :size
  repeat 4 [
    forward :missingSize
    right 90
  ]
end
```

The server reports a diagnostic such as:

```text
Unknown variable: :missingSize
```

### 5. Diagnostics for Unknown Procedures

For example:

```logo
triangle 100
```

The server reports a diagnostic such as:

```text
Unknown procedure: triangle
```

### 6. Diagnostics for Incomplete Procedure Definitions

For example:

```logo
to square :size
  repeat 4 [
    forward :size
    right 90
  ]
```

The server reports a diagnostic such as:

```text
Missing 'end' for procedure definition.
```

## Build & Run

To build the project and run the tests, use:

```sh
./gradlew build
```

To run the tests separately, use:

```sh
./gradlew test
```

To start the language server during development, use:

```sh
./gradlew run
```

The language server communicates via standard input and standard output, as expected by common LSP clients.

When started from the terminal, the server keeps running and waits for messages from an LSP client. This is normal behavior for an LSP server.

To stop the server manually, press:

```text
Ctrl + C
```

If the run task is stopped manually, Gradle or the IDE may show the task as canceled. This is expected because the language server is a long-running process.

## Testing

The project contains unit tests for the lexer, analyzer, diagnostics, semantic tokens, and go-to-declaration logic.

To run the tests, use:

```sh
./gradlew test
```

The tests cover the following behavior:

- token recognition for LOGO keywords, commands, variables, numbers, comments, brackets, and operators
- procedure declaration detection
- variable declaration detection
- go-to-declaration for procedure calls
- go-to-declaration for variable references
- diagnostics for unknown procedures
- diagnostics for unknown variables
- diagnostics for incomplete procedure definitions

The language server can also be tested manually by connecting it to an LSP-compatible editor and opening a `.logo` file.

## Scope and Limitations

This project does not implement a full LOGO interpreter. It focuses on static source-code analysis for editor features.

Because LOGO does not have one strictly defined standard, this server supports a documented subset of common LOGO constructs and turtle commands. The built-in command list can be extended in `LogoLexer` if support for additional LOGO dialects or commands is needed.

Variable declarations are handled based on procedure parameters. A production-ready implementation could introduce a more complete AST-based analysis model for advanced scoping rules and larger LOGO programs.

## Connecting to a Client

This project implements a language server only. It does not include a custom editor or IDE plugin.

To use the server, connect it to an LSP-compatible client, for example:

- LSP4IJ for IntelliJ-based IDEs
- VS Code with a generic LSP client extension
- any editor that supports custom language servers

For development, the client can start the server with:

```sh
./gradlew run --quiet
```

Alternatively, the project can be installed as a Gradle application distribution:

```sh
./gradlew installDist
```

This creates a start script under:

```text
build/install/LOGO_lsp_server/bin/LOGO_lsp_server
```

The LSP client can then be configured to run that script.

The language ID should be configured as:

```text
logo
```

A typical file extension for LOGO files is:

```text
.logo
```

After the client starts the server, opening a `.logo` file should enable the supported language features:

- syntax highlighting
- go-to-declaration for procedure references
- go-to-declaration for variable references
- diagnostics for simple syntax and reference errors

## Architecture & Project Layout

The project is organized into small components with clear responsibilities.

```text
LOGO_lsp_server/
├── README.md
├── build.gradle.kts
├── settings.gradle
├── examples/
│   ├── README.md
│   ├── square.logo
│   └── errors.logo
│
├── src/
│   ├── main/
│   │   └── java/
│   │       └── logo/
│   │           └── lsp/
│   │               ├── analysis/
│   │               ├── features/
│   │               ├── model/
│   │               ├── parser/
│   │               ├── server/
│   │               └── Main.java
│   └── test/
│       ├── README.md
│       └── java/
│           └── logo/
│               └── lsp/
└── gradle/
```

### Main Components

The server is divided into the following conceptual parts.

#### Language Server Entry Point

The entry point starts the LOGO server and connects it to the LSP client via standard input and standard output.

Its responsibility is to initialize the server and keep the communication channel with the client open.

#### LOGO Server

The language server implements the main LSP lifecycle methods, such as initialization and shutdown.

It registers the supported language features and delegates document-specific work to the text document service.

#### Text Document Service

The text document service reacts to opened, changed, and saved LOGO files.

It is responsible for analyzing the current document content and providing language features such as:

- diagnostics
- go-to-declaration
- semantic token information for syntax highlighting

#### Parser / Analyzer

The parser or analyzer reads the LOGO source code and extracts relevant language elements, for example:

- procedure definitions
- procedure calls
- variable declarations
- variable references
- turtle commands
- numeric literals
- brackets and operators

The extracted information is then used by the LSP features.

#### Symbol Table

The symbol table stores discovered declarations inside a LOGO file.

It maps names to their source code positions, for example:

- procedure name to procedure definition location
- procedure-local variable key to parameter declaration location


This allows the server to implement go-to-declaration.

#### Diagnostics

The diagnostics component checks the source code for simple errors, such as:

- unknown procedure calls
- unknown variable references
- missing `end` statements
- invalid or incomplete procedure definitions

Detected problems are sent back to the client as LSP diagnostics.

### Design Idea

The project intentionally keeps the architecture lightweight.

Instead of implementing a full LOGO interpreter, the server focuses on static source code analysis. This is sufficient for editor features such as syntax highlighting, go-to-declaration, and diagnostics.

Because LOGO does not have one universally fixed standard, this project uses a documented and consistent interpretation of common LOGO constructs.
# LOGO LSP Server

A lightweight Language Server Protocol implementation for the LOGO programming language.

LOGO is a high-level educational programming language known for its simple syntax and turtle graphics. Since LOGO does not have one strictly defined semantics, this project follows a consistent interpretation of common LOGO constructs and documents the assumptions made during implementation.

## Features

This language server supports:

- Syntax highlighting for LOGO language elements
- Go-to-declaration for procedure references
- Go-to-declaration for variable references
- Additional LSP feature: diagnostics for simple syntax and reference errors

## Supported LOGO Constructs

The server recognizes common LOGO constructs such as:

- turtle commands, for example `forward`, `back`, `left`, `right`
- procedure definitions using `to ... end`
- procedure calls
- variables referenced with `:`
- comments
- numeric literals
- identifiers

Example:

```logo
to square :size
  repeat 4 [
    forward :size
    right 90
  ]
end

square 100
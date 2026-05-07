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

### Syntax Highlighting

The server recognizes and highlights LOGO language elements such as:

- `to` and `end` as procedure definition keywords
- `repeat` as a control keyword
- `forward` and `right` as turtle commands
- `square` as a procedure name
- `:size` as a variable reference
- `100` and `90` as numeric literals

### Go-to-Declaration for Procedures

If the cursor is placed on the procedure call:

```logo
square 100
```

and **Go to Declaration** is triggered, the editor jumps to the procedure definition:

```logo
to square :size
```

### Go-to-Declaration for Variables

If the cursor is placed on the variable reference:

```logo
forward :size
```

and **Go to Declaration** is triggered, the editor jumps to the variable declaration in the procedure header:

```logo
to square :size
```

### Diagnostics

The server also reports simple syntax and reference errors.

For example:

```logo
to square :size
  repeat 4 [
    forward :missingSize
    right 90
  ]
end

triangle 100
```

This produces diagnostics such as:

- `Unknown variable: :missingSize`
- `Unknown procedure: triangle`

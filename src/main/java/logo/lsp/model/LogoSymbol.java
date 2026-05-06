package logo.lsp.model;

/**
 * Represents a declaration discovered in a LOGO document.
 *
 * @param name the symbol name without prefixes such as ':'
 * @param type the symbol type
 * @param line zero-based declaration line
 * @param startColumn zero-based start column
 * @param endColumn zero-based exclusive end column
 */
public record LogoSymbol(String name,
                         LogoSymbolType type,
                         int line,
                         int startColumn,
                         int endColumn) {

}

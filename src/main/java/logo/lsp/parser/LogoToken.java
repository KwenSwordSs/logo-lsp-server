package logo.lsp.parser;

/**
 * Represents one lexical token in LOGO source document.
 *
 * @param type the token category
 * @param text the original token text
 * @param line zero-based line number
 * @param startColumn zero-based start column
 * @param endColumn zero-based exclusive end column
 */
public record LogoToken(LogoTokenType type,
                        String text,
                        int line,
                        int startColumn,
                        int endColumn) {

    public boolean contains(int targetLine, int targetColumn) {
        return line == targetLine
            && targetColumn >= startColumn
            && targetColumn < endColumn;
    }


}

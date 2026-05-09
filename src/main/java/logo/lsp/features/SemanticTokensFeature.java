package logo.lsp.features;

import java.util.ArrayList;
import java.util.List;

import logo.lsp.parser.LogoLexer;
import logo.lsp.parser.LogoToken;
import logo.lsp.parser.LogoTokenType;
import org.eclipse.lsp4j.SemanticTokens;

/**
 * Creates semantic tokens for syntax highlighting.
 */
public final class SemanticTokensFeature {

    private final LogoLexer lexer = new LogoLexer();

    public SemanticTokens createSemanticTokens(String text) {
        List<LogoToken> tokens = lexer.tokenize(text);
        List<Integer> data = new ArrayList<>();

        int previousLine = 0;
        int previousColumn = 0;

        for (LogoToken token : tokens) {
            int tokenType = toSemanticTokenType(token.type());

            if (tokenType < 0) {
                continue;
            }

            int deltaLine = token.line() - previousLine;
            int deltaStart = deltaLine == 0
                ? token.startColumn() - previousColumn
                : token.startColumn();

            data.add(deltaLine);
            data.add(deltaStart);
            data.add(token.endColumn() - token.startColumn());
            data.add(tokenType);
            data.add(0);

            previousLine = token.line();
            previousColumn = token.startColumn();
        }

        return new SemanticTokens(data);
    }

    private int toSemanticTokenType(LogoTokenType type) {
        return switch (type) {
            case KEYWORD -> 0;
            case COMMAND, IDENTIFIER -> 1;
            case VARIABLE -> 2;
            case NUMBER -> 3;
            case COMMENT -> 4;
            case OPERATOR -> 5;
            case BRACKET -> 6;
            default -> -1;
        };
    }
}
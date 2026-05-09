package logo.lsp.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Splits LOGO source code into lexical tokens.
 */
public final class LogoLexer {

    private static final Set<String> KEYWORDS = Set.of(
        "to",
        "end",
        "repeat",
        "if",
        "else"
    );

    private static final Set<String> COMMANDS = Set.of(
        "forward",
        "fd",
        "back",
        "bk",
        "left",
        "lt",
        "right",
        "rt",
        "penup",
        "pu",
        "pendown",
        "pd",
        "home",
        "clearscreen",
        "cs"
    );

    public List<LogoToken> tokenize(String text) {
        List<LogoToken> tokens = new ArrayList<>();
        String[] lines = text.split("\\R", -1);

        for (int lineIndex = 0; lineIndex < lines.length; lineIndex++) {
            tokenizeLine(lines[lineIndex], lineIndex, tokens);
        }

        return tokens;
    }

    private void tokenizeLine(String line, int lineIndex, List<LogoToken> tokens) {
        int column = 0;

        while (column < line.length()) {
            char current = line.charAt(column);

            if (Character.isWhitespace(current)) {
                column++;
                continue;
            }

            if (current == ';') {
                tokens.add(new LogoToken(
                    LogoTokenType.COMMENT,
                    line.substring(column),
                    lineIndex,
                    column,
                    line.length()
                ));
                break;
            }

            if (current == '[' || current == ']') {
                tokens.add(new LogoToken(
                    LogoTokenType.BRACKET,
                    String.valueOf(current),
                    lineIndex,
                    column,
                    column + 1
                ));
                column++;
                continue;
            }

            if (isOperator(current)) {
                tokens.add(new LogoToken(
                    LogoTokenType.OPERATOR,
                    String.valueOf(current),
                    lineIndex,
                    column,
                    column + 1
                ));
                column++;
                continue;
            }

            int startColumn = column;

            while (column < line.length()
                && !Character.isWhitespace(line.charAt(column))
                && line.charAt(column) != '['
                && line.charAt(column) != ']'
                && !isOperator(line.charAt(column))) {
                column++;
            }

            String word = line.substring(startColumn, column);
            LogoTokenType type = classify(word);

            tokens.add(new LogoToken(
                type,
                word,
                lineIndex,
                startColumn,
                column
            ));
        }
    }

    private LogoTokenType classify(String word) {
        String normalized = word.toLowerCase();

        if (KEYWORDS.contains(normalized)) {
            return LogoTokenType.KEYWORD;
        }

        if (COMMANDS.contains(normalized)) {
            return LogoTokenType.COMMAND;
        }

        if (word.startsWith(":") && word.length() > 1) {
            return LogoTokenType.VARIABLE;
        }

        if (word.matches("-?\\d+(\\.\\d+)?")) {
            return LogoTokenType.NUMBER;
        }

        if (word.matches("[A-Za-z_][A-Za-z0-9_\\-]*")) {
            return LogoTokenType.IDENTIFIER;
        }

        return LogoTokenType.UNKNOWN;
    }

    private boolean isOperator(char character) {
        return character == '+'
            || character == '-'
            || character == '*'
            || character == '/'
            || character == '='
            || character == '<'
            || character == '>';
    }

}
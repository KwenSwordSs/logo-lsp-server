package logo.lsp.features;

import java.util.List;
import java.util.Optional;

import logo.lsp.analysis.LogoAnalysisResult;
import logo.lsp.analysis.LogoAnalyzer;
import logo.lsp.model.LogoSymbol;
import logo.lsp.parser.LogoToken;
import logo.lsp.parser.LogoTokenType;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

/**
 * Implements go-to-declaration for procedure and variable references.
 */
public final class DefinitionFeature {

    private static final String TO_KEYWORD = "to";
    private static final String END_KEYWORD = "end";

    private final LogoAnalyzer analyzer = new LogoAnalyzer();

    public List<Location> findDefinition(String uri, String text, Position position) {
        LogoAnalysisResult result = analyzer.analyze(text);

        Optional<LogoToken> tokenAtCursor = result.tokens()
            .stream()
            .filter(token -> token.contains(position.getLine(), position.getCharacter()))
            .findFirst();

        if (tokenAtCursor.isEmpty()) {
            return List.of();
        }

        LogoToken token = tokenAtCursor.get();

        if (token.type() == LogoTokenType.VARIABLE) {
            String currentProcedure = findCurrentProcedure(result.tokens(), position);

            if (currentProcedure == null) {
                return List.of();
            }

            String variableName = normalizeVariableName(token.text()).toLowerCase();
            String variableKey = createVariableKey(currentProcedure, variableName);

            LogoSymbol symbol = result.variables().get(variableKey);
            return toLocationList(uri, symbol);
        }

        if (token.type() == LogoTokenType.IDENTIFIER) {
            LogoSymbol symbol = result.procedures().get(token.text().toLowerCase());
            return toLocationList(uri, symbol);
        }

        return List.of();
    }

    private String findCurrentProcedure(List<LogoToken> tokens, Position position) {
        String currentProcedure = null;

        for (int i = 0; i < tokens.size(); i++) {
            LogoToken token = tokens.get(i);

            if (isAfterPosition(token, position)) {
                break;
            }

            if (token.type() == LogoTokenType.KEYWORD && TO_KEYWORD.equalsIgnoreCase(token.text())) {
                if (i + 1 < tokens.size() && tokens.get(i + 1).type() == LogoTokenType.IDENTIFIER) {
                    currentProcedure = tokens.get(i + 1).text().toLowerCase();
                }
            }

            if (token.type() == LogoTokenType.KEYWORD && END_KEYWORD.equalsIgnoreCase(token.text())) {
                currentProcedure = null;
            }
        }

        return currentProcedure;
    }

    private boolean isAfterPosition(LogoToken token, Position position) {
        return token.line() > position.getLine()
            || token.line() == position.getLine()
            && token.startColumn() > position.getCharacter();
    }

    private List<Location> toLocationList(String uri, LogoSymbol symbol) {
        if (symbol == null) {
            return List.of();
        }

        Location location = new Location(
            uri,
            new Range(
                new Position(symbol.line(), symbol.startColumn()),
                new Position(symbol.line(), symbol.endColumn())
            )
        );

        return List.of(location);
    }

    private String normalizeVariableName(String variable) {
        if (variable.startsWith(":")) {
            return variable.substring(1);
        }
        return variable;
    }

    private String createVariableKey(String procedureName, String variableName) {
        return procedureName.toLowerCase() + ":" + variableName.toLowerCase();
    }
}
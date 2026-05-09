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
            String variableName = normalizeVariableName(token.text());
            LogoSymbol symbol = result.variables().get(variableName);
            return toLocationList(uri, symbol);
        }

        if (token.type() == LogoTokenType.IDENTIFIER) {
            LogoSymbol symbol = result.procedures().get(token.text().toLowerCase());
            return toLocationList(uri, symbol);
        }

        return List.of();
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
}
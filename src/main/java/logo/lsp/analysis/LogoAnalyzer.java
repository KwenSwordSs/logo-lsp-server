package logo.lsp.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import logo.lsp.model.LogoSymbol;
import logo.lsp.model.LogoSymbolType;
import logo.lsp.parser.LogoLexer;
import logo.lsp.parser.LogoToken;
import logo.lsp.parser.LogoTokenType;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

/**
 * Performs simple semantic analysis for a LOGO document.
 */
public final class LogoAnalyzer {

    private static final String TO_KEYWORD = "to";
    private static final String END_KEYWORD = "end";

    private final LogoLexer lexer = new LogoLexer();

    public LogoAnalysisResult analyze(String text) {
        List<LogoToken> tokens = lexer.tokenize(text);
        Map<String, LogoSymbol> procedures = new HashMap<>();
        Map<String, LogoSymbol> variables = new HashMap<>();
        List<Diagnostic> diagnostics = new ArrayList<>();

        collectDeclarations(tokens, procedures, variables, diagnostics);
        collectDiagnostics(tokens, procedures, variables , diagnostics);

        return new LogoAnalysisResult(tokens, procedures, variables, diagnostics);
    }

    private void collectDeclarations(
        List<LogoToken> tokens,
        Map<String, LogoSymbol> procedures,
        Map<String, LogoSymbol> variables,
        List<Diagnostic> diagnostics
    ) {
        boolean insideProcedure = false;
        boolean hasOpenProcedure = false;

        for (int i = 0; i < tokens.size(); i++) {
            LogoToken token = tokens.get(i);
            String text = token.text().toLowerCase();

            if (token.type() == LogoTokenType.KEYWORD && TO_KEYWORD.equals(text)) {
                if (i + 1 >= tokens.size() || tokens.get(i + 1).type() != LogoTokenType.IDENTIFIER) {
                    diagnostics.add(createDiagnostic(
                        token,
                        "Invalid procedure declaration. Expected procedure name after 'to'."
                    ));
                    continue;
                }

                LogoToken procedureName = tokens.get(i + 1);
                procedures.put(
                    procedureName.text(),
                    new LogoSymbol(
                        procedureName.text(),
                        LogoSymbolType.PROCEDURE,
                        procedureName.line(),
                        procedureName.startColumn(),
                        procedureName.endColumn()
                    )
                );

                insideProcedure = true;
                hasOpenProcedure = true;

                int parameterIndex = i + 2;
                while (parameterIndex < tokens.size()
                    && tokens.get(parameterIndex).line() == token.line()
                    && tokens.get(parameterIndex).type() == LogoTokenType.VARIABLE) {
                    LogoToken parameter = tokens.get(parameterIndex);
                    variables.put(
                        normalizeVariableName(parameter.text()),
                        new LogoSymbol(
                            normalizeVariableName(parameter.text()),
                            LogoSymbolType.VARIABLE,
                            parameter.line(),
                            parameter.startColumn(),
                            parameter.endColumn()
                        )
                    );
                    parameterIndex++;
                }
            }

            if (token.type() == LogoTokenType.KEYWORD && END_KEYWORD.equals(text)) {
                insideProcedure = false;
                hasOpenProcedure = false;
            }
        }

        if (insideProcedure && hasOpenProcedure) {
            LogoToken lastToken = tokens.isEmpty() ? null : tokens.get(tokens.size() - 1);
            if (lastToken != null) {
                diagnostics.add(createDiagnostic(lastToken, "Missing 'end' for procedure definition."));
            }
        }
    }

    private void collectDiagnostics(
        List<LogoToken> tokens,
        Map<String, LogoSymbol> procedures,
        Map<String, LogoSymbol> variables,
        List<Diagnostic> diagnostics
    ) {
        for (int i = 0; i < tokens.size(); i++) {
            LogoToken token = tokens.get(i);

            if (token.type() == LogoTokenType.IDENTIFIER) {
                if (isProcedureDeclarationName(tokens, i)) {
                    continue;
                }

                if (!procedures.containsKey(token.text())) {
                    diagnostics.add(createDiagnostic(
                        token,
                        "Unknown procedure: " + token.text()
                    ));
                }
            }

            if (token.type() == LogoTokenType.VARIABLE) {
                String variableName = normalizeVariableName(token.text());

                if (!variables.containsKey(variableName)) {
                    diagnostics.add(createDiagnostic(
                        token,
                        "Unknown variable: " + token.text()
                    ));
                }
            }
        }
    }

    private boolean isProcedureDeclarationName(List<LogoToken> tokens, int index) {
        if (index == 0) {
            return false;
        }

        LogoToken previous = tokens.get(index - 1);
        return previous.type() == LogoTokenType.KEYWORD
            && TO_KEYWORD.equalsIgnoreCase(previous.text())
            && previous.line() == tokens.get(index).line();
    }

    private Diagnostic createDiagnostic(LogoToken token, String message) {
        Diagnostic diagnostic = new Diagnostic();
        diagnostic.setSeverity(DiagnosticSeverity.Warning);
        diagnostic.setMessage(message);
        diagnostic.setRange(new Range(
            new Position(token.line(), token.startColumn()),
            new Position(token.line(), token.endColumn())
        ));
        return diagnostic;
    }

    private String normalizeVariableName(String variable) {
        if (variable.startsWith(":")) {
            return variable.substring(1);
        }
        return variable;
    }
}
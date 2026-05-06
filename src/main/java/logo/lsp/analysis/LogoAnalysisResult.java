package logo.lsp.analysis;

import java.util.List;
import java.util.Map;

import logo.lsp.model.LogoSymbol;
import logo.lsp.parser.LogoToken;
import org.eclipse.lsp4j.Diagnostic;

/**
 * Stores the reusable analysis result of a LOGO document.
 *
 * @param tokens all lexical tokens found in the document
 * @param procedures procedure declarations by name
 * @param variables variable declarations by name
 * @param diagnostics diagnostics found during analysis
 */
public record LogoAnalysisResult(
    List<LogoToken> tokens,
    Map<String, LogoSymbol> procedures,
    Map<String, LogoSymbol> variables,
    List<Diagnostic> diagnostics
) {
}
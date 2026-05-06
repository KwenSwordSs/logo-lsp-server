package logo.lsp.features;

import java.util.List;

import logo.lsp.analysis.LogoAnalysisResult;
import logo.lsp.analysis.LogoAnalyzer;
import org.eclipse.lsp4j.Diagnostic;

/**
 * Creates diagnostics for LOGO source documents.
 */
public final class DiagnosticsFeature {

    private final LogoAnalyzer analyzer = new LogoAnalyzer();

    public List<Diagnostic> createDiagnostics(String text) {
        LogoAnalysisResult result = analyzer.analyze(text);
        return result.diagnostics();
    }
}

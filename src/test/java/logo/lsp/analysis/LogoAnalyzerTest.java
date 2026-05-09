package logo.lsp.analysis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class LogoAnalyzerTest {

    @Test
    void validProgramHasNoDiagnostics() {
        LogoAnalyzer analyzer = new LogoAnalyzer();

        String text = """
            to square :size
              repeat 4 [
                forward :size
                right 90
              ]
            end

            square 100
            """;

        LogoAnalysisResult result = analyzer.analyze(text);

        assertTrue(result.diagnostics().isEmpty());
    }

    @Test
    void detectsUnknownProcedure() {
        LogoAnalyzer analyzer = new LogoAnalyzer();

        String text = "triangle 100";

        LogoAnalysisResult result = analyzer.analyze(text);

        assertEquals(1, result.diagnostics().size());
        assertEquals("Unknown procedure: triangle", result.diagnostics().get(0).getMessage());
    }

    @Test
    void detectsUnknownVariable() {
        LogoAnalyzer analyzer = new LogoAnalyzer();

        String text = """
            to square :size
              forward :missingSize
            end
            """;

        LogoAnalysisResult result = analyzer.analyze(text);

        assertEquals(1, result.diagnostics().size());
        assertEquals("Unknown variable: :missingSize", result.diagnostics().get(0).getMessage());
    }

    @Test
    void detectsMissingEnd() {
        LogoAnalyzer analyzer = new LogoAnalyzer();

        String text = """
            to square :size
              forward :size
            """;

        LogoAnalysisResult result = analyzer.analyze(text);

        assertEquals(1, result.diagnostics().size());
        assertEquals("Missing 'end' for procedure definition.", result.diagnostics().get(0).getMessage());
    }

    @Test
    void collectsProcedureAndVariableDeclarations() {
        LogoAnalyzer analyzer = new LogoAnalyzer();

        String text = """
            to square :size
              forward :size
            end
            """;

        LogoAnalysisResult result = analyzer.analyze(text);

        assertTrue(result.procedures().containsKey("square"));
        assertTrue(result.variables().containsKey("square:size"));    }
}
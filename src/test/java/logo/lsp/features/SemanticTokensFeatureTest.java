package logo.lsp.features;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.eclipse.lsp4j.SemanticTokens;
import org.junit.jupiter.api.Test;

class SemanticTokensFeatureTest {

    @Test
    void createsSemanticTokensForLogoProgram() {
        SemanticTokensFeature feature = new SemanticTokensFeature();

        String text = """
            to square :size
              forward :size
              right 90
            end
            """;

        SemanticTokens tokens = feature.createSemanticTokens(text);

        assertFalse(tokens.getData().isEmpty());
    }

    @Test
    void createsSemanticTokensForLogoElements() {
        SemanticTokensFeature feature = new SemanticTokensFeature();

        String text = """
        to square :size
          repeat 4 [
            forward :size
            right 90
          ]
        end
        """;

        SemanticTokens tokens = feature.createSemanticTokens(text);

        assertFalse(tokens.getData().isEmpty());

        int valuesPerToken = 5;
        int tokenCount = tokens.getData().size() / valuesPerToken;

        assertEquals(12, tokenCount);

    }

}
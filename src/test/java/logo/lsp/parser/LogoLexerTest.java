package logo.lsp.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class LogoLexerTest {

    @Test
    void tokenizesLogoProgram() {
        LogoLexer lexer = new LogoLexer();

        String text = """
            to square :size
              repeat 4 [
                forward :size
                right 90
              ]
            end

            square 100
            """;

        List<LogoToken> tokens = lexer.tokenize(text);

        assertTrue(tokens.stream().anyMatch(token ->
            token.type() == LogoTokenType.KEYWORD && token.text().equals("to")
        ));

        assertTrue(tokens.stream().anyMatch(token ->
            token.type() == LogoTokenType.IDENTIFIER && token.text().equals("square")
        ));

        assertTrue(tokens.stream().anyMatch(token ->
            token.type() == LogoTokenType.VARIABLE && token.text().equals(":size")
        ));

        assertTrue(tokens.stream().anyMatch(token ->
            token.type() == LogoTokenType.COMMAND && token.text().equals("forward")
        ));

        assertTrue(tokens.stream().anyMatch(token ->
            token.type() == LogoTokenType.NUMBER && token.text().equals("100")
        ));
    }

    @Test
    void tokenizesComments() {
        LogoLexer lexer = new LogoLexer();

        List<LogoToken> tokens = lexer.tokenize("forward 100 ; move forward");

        assertEquals(LogoTokenType.COMMENT, tokens.get(2).type());
        assertEquals("; move forward", tokens.get(2).text());
    }
}
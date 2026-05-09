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

    @Test
    void recognizesLogoCommands() {
        LogoLexer lexer = new LogoLexer();

        List<LogoToken> tokens = lexer.tokenize("forward 100 right 90 back 50 left 45");

        assertEquals(LogoTokenType.COMMAND, tokens.get(0).type());
        assertEquals("forward", tokens.get(0).text());

        assertEquals(LogoTokenType.NUMBER, tokens.get(1).type());
        assertEquals("100", tokens.get(1).text());

        assertEquals(LogoTokenType.COMMAND, tokens.get(2).type());
        assertEquals("right", tokens.get(2).text());

        assertEquals(LogoTokenType.NUMBER, tokens.get(3).type());
        assertEquals("90", tokens.get(3).text());

        assertEquals(LogoTokenType.COMMAND, tokens.get(4).type());
        assertEquals("back", tokens.get(4).text());

        assertEquals(LogoTokenType.COMMAND, tokens.get(6).type());
        assertEquals("left", tokens.get(6).text());
    }

}
package logo.lsp.features;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.Position;
import org.junit.jupiter.api.Test;

class DefinitionFeatureTest {

    @Test
    void findsProcedureDefinition() {
        DefinitionFeature feature = new DefinitionFeature();

        String text = """
        to square :size
          repeat 4 [
            forward :size
            right 90
          ]
        end

        square 100
        """;

        List<Location> locations = feature.findDefinition(
            "file:///example.logo",
            text,
            new Position(7, 1)
        );

        assertEquals(1, locations.size());
        assertEquals(0, locations.get(0).getRange().getStart().getLine());
        assertEquals(3, locations.get(0).getRange().getStart().getCharacter());
    }

    @Test
    void findsVariableDefinition() {
        DefinitionFeature feature = new DefinitionFeature();

        String text = """
            to square :size
              repeat 4 [
                forward :size
                right 90
              ]
            end
            """;

        List<Location> locations = feature.findDefinition(
            "file:///example.logo",
            text,
            new Position(2, 13)
        );

        assertEquals(1, locations.size());
        assertEquals(0, locations.get(0).getRange().getStart().getLine());
        assertEquals(10, locations.get(0).getRange().getStart().getCharacter());
    }

    @Test
    void returnsEmptyListForUnknownDefinition() {
        DefinitionFeature feature = new DefinitionFeature();

        String text = "triangle 100";

        List<Location> locations = feature.findDefinition(
            "file:///example.logo",
            text,
            new Position(0, 1)
        );

        assertTrue(locations.isEmpty());
    }
}
package org.example;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.swing.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Set;

class queryBridegWords_case6 {
    private Code1 code;
    private JTextArea outputArea;

    @BeforeEach
    void setUp() {
        code = new Code1();
        outputArea = new JTextArea();
        code.outputArea = outputArea;

        String text = "The scientist carefully analyzed the data, wrote a detailed report, " +
                "and shared the report with the team, but the team requested more " +
                "requested data, so the scientist analyzed it again.";
        buildDirectedGraphFromText(text);
    }

    @Test
    void queryBridgeWords() {
        String start = "wrote";
        String end = "report";

        Set<String> bridgeWords = code.queryBridgeWords(start, end, true);

        assertNotNull(bridgeWords, "Should return empty set, not null");
        assertTrue(bridgeWords.isEmpty(), "Bridge words set should be empty");

        String output = outputArea.getText();
        assertTrue(output.contains("No bridge words from wrote to report!"),
                "Output should indicate no bridge words found");

        assertFalse(output.contains("No wrote in the graph!"),
                "Output should not indicate 'wrote' is missing");
        assertFalse(output.contains("No report in the graph!"),
                "Output should not indicate 'report' is missing");
    }

    private void buildDirectedGraphFromText(String text) {
        String[] words = text.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
        String previousWord = null;
        for (String word : words) {
            if (!word.isEmpty()) {
                code.addNode(word);
                if (previousWord != null) {
                    code.addEdge(previousWord, word);
                }
                previousWord = word;
            }
        }
    }
}
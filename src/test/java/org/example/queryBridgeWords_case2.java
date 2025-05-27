package org.example;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.swing.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Set;

class queryBridgeWords_case2 {
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
        buildDirectedGraphTest(text);
    }

    @Test
    void queryBridgeWords() {
        String start = "requested";
        String end = "requested";

        String expectedBridgeWord = "more";

        Set<String> bridgeWords = code.queryBridgeWords(start, end, true);

        assertNotNull(bridgeWords, "Bridge words set should not be null");
        assertTrue(bridgeWords.contains(expectedBridgeWord),
                "Bridge word should be '" + expectedBridgeWord + "'");

        String output = outputArea.getText();
        assertTrue(output.contains("The bridge words from requested to requested are: more"),
                "Output should contain the expected bridge word");
    }

    private void buildDirectedGraphTest(String text) {
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

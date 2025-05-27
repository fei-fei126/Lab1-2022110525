package org.example;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.swing.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;


class queryBridgeWords_case1 {
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
        try {
            buildDirectedGraphTest(text);
        } catch (Exception e) {
            fail("Failed to build directed graph: " + e.getMessage());
        }
    }

    @org.junit.jupiter.api.Test
    void queryBridgeWords() {
        String start = "analyzed";
        String end = "data";

        String expectedBridgeWord = "the";

        Set<String> bridgeWords = code.queryBridgeWords(start, end, true);

        assertNotNull(bridgeWords, "Bridge words set should not be null");
        //assertEquals(1, bridgeWords.size(), "Should find exactly one bridge word");
        assertTrue(bridgeWords.contains(expectedBridgeWord),
                "Bridge word should be '" + expectedBridgeWord + "'");

        String output = code.outputArea.getText();
        assertTrue(output.contains("The bridge words from analyzed to data are: the"),
                "Output should contain the expected bridge word");
    }

    private void buildDirectedGraphTest(String text) throws Exception {
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
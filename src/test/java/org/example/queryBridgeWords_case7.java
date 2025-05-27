package org.example;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.swing.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Set;

class queryBridgeWords_case7 {
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
        String start = "123";
        String end = "report";

        Set<String> bridgeWords = code.queryBridgeWords(start, end, true);

        assertNull(bridgeWords, "Should return null for invalid input");

        String output = outputArea.getText();
        assertTrue(output.contains("The input must consist of English alphabetic characters only."),
                "Output should indicate invalid input format");

        assertFalse(output.contains("No 123 in the graph!"),
                "Output should not check graph for invalid input");
        assertFalse(output.contains("No report in the graph!"),
                "Output should not check graph for valid word when other is invalid");
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
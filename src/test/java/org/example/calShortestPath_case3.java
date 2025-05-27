package org.example;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.swing.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.List;

class calShortestPath_case3 {
    private Code1 code;
    private JTextArea outputArea;
    private String[] allWords;

    @BeforeEach
    void setUp() {
        code = new Code1();
        outputArea = new JTextArea();
        code.outputArea = outputArea;
        String text = "The scientist carefully analyzed the data, wrote a detailed report, " +
                "and shared the report with the team, but the team requested more " +
                "requested data, so the scientist analyzed it again.";
        allWords = buildDirectedGraphFromText(text);
    }

    @Test
    void calcShortestPath() {
        String word1 = "carefully";
        String word2 = "so";
        int i = 1;
        String root = allWords[0];
        List<List<Object>> result = code.calcShortestPath(word1, word2, i, root);

        assertNotNull(result, "Should not return null when path exists");
        assertEquals(1, result.size(), "Should find exactly one shortest path");

        List<String> expectedPath = Arrays.asList("carefully", "analyzed", "the", "data", "so");
        int expectedLength = 4; // 4 edges between 5 nodes

        List<String> actualPath = (List<String>) result.get(0).get(0);
        int actualLength = (int) result.get(0).get(1);

        assertEquals(expectedPath, actualPath, "Path nodes don't match expected sequence");
        assertEquals(expectedLength, actualLength, "Path length doesn't match expected value");

        String output = outputArea.getText();
        assertTrue(output.contains("计算从 carefully 到 so 的最短路径"),
                "Should show calculation start message");
        assertTrue(output.contains("carefully 和 so 之间的路径:"),
                "Should list all found paths");
        assertTrue(output.contains("最短路径图已保存到"),
                "Should confirm graph image generation");
        assertTrue(output.contains("[carefully, analyzed, the, data, so]"),
                "Should display the correct path in output");
    }

    private String[] buildDirectedGraphFromText(String text) {
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
        return words;
    }
}
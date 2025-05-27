package org.example;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import javax.swing.*;
import java.util.*;

class calcShortestPath_case4 {
    private Code1 code;
    private JTextArea outputArea;
    private String[] allWords;

    @BeforeEach
    void setUp() {
        code = new Code1();
        outputArea = new JTextArea();
        code.outputArea = outputArea;
        String text = "The scientist carefully analyzed the data, wrote a detailed report, " +
                "and shared the report with the team, but the team requested more requested data, so the scientist analyzed it again.";
        allWords = buildDirectedGraphFromText(text);
    }

    @Test
    void calcShortestPath() {
        String word1 = "team";
        String word2 = "it";
        int i = 1;
        String root = allWords[0];
        List<List<Object>> result = code.calcShortestPath(word1, word2, i, root);

        assertNotNull(result, "Should not return null when path exists");
        assertEquals(1, result.size(), "Should find exactly one shortest path");
        List<String> expectedPath = Arrays.asList("team", "but", "the", "scientist", "analyzed", "it");
        int expectedLength = 6; // 6 edges between 7 nodes

        List<String> actualPath = (List<String>) result.get(0).get(0);
        int actualLength = (int) result.get(0).get(1);

        assertEquals(expectedPath, actualPath, "Path nodes don't match expected sequence");
        assertEquals(expectedLength, actualLength, "Path length doesn't match expected value");

        String output = outputArea.getText();
        assertTrue(output.contains("计算从 team 到 it 的最短路径"),
                "Should show calculation start message");
        assertTrue(output.contains("team 和 it 之间的路径:"),
                "Should list all found paths");
        assertTrue(output.contains("最短路径图已保存到"),
                "Should confirm image generation");
        assertTrue(output.contains("[team, but, the, scientist, analyzed, it]"),
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
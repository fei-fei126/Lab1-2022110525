package org.example;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.swing.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.List;

class calShortestPath_case2 {
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
        String word1 = "wrote";
        String word2 = "report";
        int i = 1;
        String root = allWords[0]; // "the"

        List<List<Object>> result = code.calcShortestPath(word1, word2, i, root);
        assertNotNull(result, "Should not return null when path exists");
        assertEquals(1, result.size(), "Should find exactly one shortest path");
        List<String> expectedPath = Arrays.asList("wrote", "a", "detailed", "report");
        int expectedLength = 3;
        List<String> actualPath = (List<String>) result.get(0).get(0);
        int actualLength = (int) result.get(0).get(1);
        assertEquals(expectedPath, actualPath, "Path nodes don't match");
        assertEquals(expectedLength, actualLength, "Path length doesn't match");
        String output = outputArea.getText();
        assertTrue(output.contains("计算从 wrote 到 report 的最短路径"),
                "Should show calculation start");
        assertTrue(output.contains("wrote 和 report 之间的路径:"),
                "Should list all paths");
        assertTrue(output.contains("最短路径图已保存到"),
                "Should confirm graph image generation");
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
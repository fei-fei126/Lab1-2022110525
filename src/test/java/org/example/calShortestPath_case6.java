package org.example;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.swing.*;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class CalcShortestPathCase6 {
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
    void testCalcShortestPath_AToMore() {
        String word1 = "a";
        String word2 = "more";
        int i = 1;
        String root = allWords[0];

        List<List<Object>> result = code.calcShortestPath(word1, word2, i, root);

        assertNotNull(result, "当路径存在时不应返回null");
        assertEquals(1, result.size(), "应该只找到一条最短路径");

        List<String> expectedPath = Arrays.asList("a", "detailed", "report", "with", "the", "team", "requested", "more");
        int expectedLength = 8;
        List<String> actualPath = (List<String>) result.get(0).get(0);
        int actualLength = (int) result.get(0).get(1);
        assertEquals(expectedPath, actualPath, "路径节点与预期不符");
        assertEquals(expectedLength, actualLength, "路径长度与预期不符");
        String output = outputArea.getText();
        assertTrue(output.contains("计算从 a 到 more 的最短路径"),
                "应显示计算开始信息");
        assertTrue(output.contains("a 和 more 之间的路径:"),
                "应列出所有找到的路径");
        assertTrue(output.contains("最短路径图已保存到"),
                "应确认生成了图像文件");
        assertTrue(output.contains("[a, detailed, report, with, the, team, requested, more]"),
                "应在输出中显示正确路径");
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
package org.example;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.swing.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class calShortestPath_case1 {
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
        String word1 = "again";
        String word2 = "the";
        int i = 1;
        String root = allWords[0]; // "the"
        List<List<Object>> result = code.calcShortestPath(word1, word2, i, root);

        assertNull(result, "当不存在路径时应返回null");

        String output = outputArea.getText();
        assertTrue(output.contains("计算从 again 到 the 的最短路径"),
                "应输出计算开始信息");
        assertTrue(output.contains("again 和 the 之间没有路径"),
                "应输出无路径信息");
        assertFalse(output.contains("最短路径图已保存到"),
                "不应生成图像文件");
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
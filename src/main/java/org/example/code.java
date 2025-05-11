package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class code {
    private final Map<String, Map<String, Integer>> directedGraph;
    private volatile boolean stopWalk = false;
    private final CountDownLatch latch = new CountDownLatch(1);
    private JTextArea outputArea;
    private JFrame frame;
    private String[] allWords;
    private String root;

    public code() {
        directedGraph = new HashMap<>();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new code().createAndShowGUI();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void createAndShowGUI() {
        //创建一个主窗口，窗口标题为“有向图处理程序”
        frame = new JFrame("有向图处理程序");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);
        frame.setLayout(new BorderLayout());

        // 顶部面板 - 文件选择和操作按钮
        JPanel topPanel = new JPanel(new GridLayout(1, 8, 5, 5)); // 1行8列，水平和垂直间隙为5

        // 文件选择按钮
        JButton openButton = new JButton("选择文件");
        JTextField filePathField = new JTextField(30);
        //设置文本框为不可编辑
        filePathField.setEditable(false);

        // 操作按钮
        JButton bridgeWordsButton = new JButton("查询桥接词");
        JButton generateTextButton = new JButton("生成新文本");
        JButton shortestPathButton = new JButton("最短路径");
        JButton randomWalkButton = new JButton("随机游走");
        JButton stopWalkButton = new JButton("停止游走");
        JButton pageRankButton = new JButton("PageRank");
        JButton improvedPRButton = new JButton("改进PR");

        // 将按钮添加到面板
        topPanel.add(new JLabel("文件路径:"));
        topPanel.add(filePathField);
        topPanel.add(openButton);
        topPanel.add(bridgeWordsButton);
        topPanel.add(generateTextButton);
        topPanel.add(shortestPathButton);
        topPanel.add(randomWalkButton);
        topPanel.add(stopWalkButton);
        topPanel.add(pageRankButton);
        topPanel.add(improvedPRButton);

        // 按钮事件绑定
        openButton.addActionListener(e -> selectFile(filePathField));
        bridgeWordsButton.addActionListener(e -> showBridgeWordsDialog());
        generateTextButton.addActionListener(e -> showGenerateTextDialog());
        shortestPathButton.addActionListener(e -> showShortestPathDialog());
        randomWalkButton.addActionListener(e -> startRandomWalk());
        stopWalkButton.addActionListener(e -> stopWalk = true);
        pageRankButton.addActionListener(e -> showPageRankDialog());
        improvedPRButton.addActionListener(e -> showImprovedPRDialog());

        // 中间面板 - 输出区域和图形显示
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        // 底部面板 - 状态信息
        JLabel statusLabel = new JLabel("就绪");

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(statusLabel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void selectFile(JTextField filePathField) {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(frame);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String filePath = selectedFile.getAbsolutePath();
            filePathField.setText(filePath);

            File dir = new File("./output/");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String dotFilePath = "./output/directed_graph.dot";
            String imageFilePath = "./output/directed_graph.png";

            try {
                allWords = showDirectedGraph(filePath, dotFilePath, imageFilePath);
                //将数组的第一个元素赋值给变量 root，表示有向图的根节点
                root = allWords[0];
                outputArea.append("有向图生成成功: " + imageFilePath + "\n");
            } catch (IOException e) {
                outputArea.append("有向图生成失败: " + e.getMessage() + "\n");
                e.printStackTrace();
            }
        }
    }

    private void showBridgeWordsDialog() {
        JTextField word1Field = new JTextField(10);
        JTextField word2Field = new JTextField(10);

        JPanel panel = new JPanel();
        panel.add(new JLabel("第一个单词:"));
        panel.add(word1Field);
        panel.add(new JLabel("第二个单词:"));
        panel.add(word2Field);
        //使用 JOptionPane.showConfirmDialog 方法显示一个确认对话框
        int result = JOptionPane.showConfirmDialog(frame, panel,
                "查询桥接词", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String word1 = word1Field.getText().toLowerCase();
            String word2 = word2Field.getText().toLowerCase();

            outputArea.append("查询桥接词输入: " + word1 + " 和 " + word2 + "\n");

            queryBridgeWords(word1, word2, true);
        }
    }

    private void showGenerateTextDialog() {
        String inputText = JOptionPane.showInputDialog(frame,
                "输入文本:", "生成新文本", JOptionPane.PLAIN_MESSAGE);

        if (inputText != null && !inputText.trim().isEmpty()) {
            outputArea.append("生成新文本输入: " + inputText + "\n");

            String newText = generateNewText(inputText);
            outputArea.append("生成的新文本: " + newText + "\n");
        }
    }

    private void showShortestPathDialog() {
        String input = JOptionPane.showInputDialog(frame,
                "输入两个单词(空格分隔):", "计算最短路径", JOptionPane.PLAIN_MESSAGE);

        if (input != null && !input.trim().isEmpty()) {
            outputArea.append("计算最短路径输入: " + input + "\n");

            String[] words = input.split(" ");
            if (words.length == 1) {
                // 计算到所有节点的最短路径
                String word1 = words[0].toLowerCase();
                int index = 0;
                for (String word2 : allWords) {
                    calcShortestPath(word1, word2, ++index, root);
                }
            } else if (words.length == 2) {
                String word1 = words[0].toLowerCase();
                String word2 = words[1].toLowerCase();
                calcShortestPath(word1, word2, 1, root);
            } else {
                outputArea.append("请输入1个或2个单词!\n");
            }
        }
    }

    private void startRandomWalk() {
        new Thread(() -> {
            outputArea.append("开始随机游走...\n");
            String result = randomWalk();
            outputArea.append("随机游走结果已保存到: " + result + "\n");
            latch.countDown();
        }).start();
    }

    public void addNode(String node) {
        directedGraph.putIfAbsent(node, new HashMap<>());
    }

    public void addEdge(String source, String destination) {
        directedGraph.get(source).merge(destination, 1, Integer::sum);
    }

    public String[] buildDirectedGraph(String filePath) throws IOException {
        //wordList：用于存储文件中处理过的单词。
        //previousWord：用于记录上一个处理过的单词，初始值为 null。
        //batchSize：设置每批处理的单词数量为 10000。这是为了在处理大量数据时定期清理内存。
        //currentBatch：用于记录当前处理的单词数量。
        List<String> wordList = new ArrayList<>();
        String previousWord = null;
        int batchSize = 10000; // 每批处理的单词数量
        int currentBatch = 0;
        //使用 BufferedReader 和 FileReader 打开文件，逐行读取内容
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                //将字符串中所有非字母字符替换为空格
                String[] words = line.replaceAll("[^a-zA-Z]", " ")
                        .toLowerCase()
                        //按空格分割字符串，得到单词数组
                        .split("\\s+");

                for (String word : words) {
                    if (!word.isEmpty()) {
                        wordList.add(word);
                        if (previousWord != null) {
                            addNode(previousWord);
                            addNode(word);
                            addEdge(previousWord, word);

                            // 定期清理内存
                            if (++currentBatch % batchSize == 0) {
                                System.gc();
                            }
                        }
                        previousWord = word;
                    }
                }
            }
        }
        return wordList.toArray(new String[0]);
    }

    public void createDotFile(String dotFilePath, List<List<Object>> shortestPaths, String root) {
        try (PrintWriter writer = new PrintWriter(dotFilePath)) {
            writer.println("digraph G {");
            writer.println("\tgraph [nodesep=0.5, ranksep=1.5, overlap=false, splines=true];");
            writer.println("\tnode [shape=circle, width=0.4, height=0.4, fontsize=8];");
            writer.println("\tedge [fontsize=8];");

            // 如果提供了最短路径（shortestPaths），则只显示与这些路径相关的节点
            if (shortestPaths != null && !shortestPaths.isEmpty()) {
                Set<String> nodesToShow = new HashSet<>();
                for (List<Object> pathWithLength : shortestPaths) {
                    List<String> path = (List<String>) pathWithLength.get(0);//表示路径上的节点
                    nodesToShow.addAll(path);
                }

                // 只写入需要显示的节点和边
                for (String node : nodesToShow) {
                    writer.println("\t" + node + " [label=\"" + node + "\"];");
                }

                // 添加路径边
                int colorIndex = 0;
                String[] colors = {"red", "green", "blue", "yellow", "orange", "purple"};
                for (List<Object> pathWithLength : shortestPaths) {
                    String color = colors[colorIndex % colors.length];
                    List<String> path = (List<String>) pathWithLength.get(0);
                    for (int i = 0; i < path.size() - 1; i++) {
                        //遍历路径上的每对相邻节点（from 和 to）
                        String from = path.get(i);
                        String to = path.get(i + 1);
                        Integer weight = directedGraph.get(from).get(to);
                        writer.println("\t" + from + " -> " + to +
                                " [label=\"" + weight + "\", color=\"" + color +
                                "\", penwidth=2];");
                    }
                    colorIndex++;
                }
            } else {
                // 对于大图，只显示部分节点和边
                int nodeLimit = 500; // 限制显示的节点数量
                int edgeLimit = 1000; // 限制显示的边数量

                int nodeCount = 0;
                int edgeCount = 0;
                //directedGraph 是一个 Map<String, Map<String, Integer>> 类型的变量，表示有向图的邻接表。键是节点，值是该节点的出边及其权重。
                for (Map.Entry<String, Map<String, Integer>> entry : directedGraph.entrySet()) {
                    if (nodeCount++ >= nodeLimit) break;

                    String vertex = entry.getKey();
                    writer.println("\t" + vertex + " [label=\"" + vertex + "\"];");

                    for (Map.Entry<String, Integer> edge : entry.getValue().entrySet()) {
                        if (edgeCount++ >= edgeLimit) break;

                        String destination = edge.getKey();
                        int weight = edge.getValue();
                        writer.println("\t" + vertex + " -> " + destination +
                                " [label=\"" + weight + "\"];");
                    }
                }
            }

            writer.println("}");
        } catch (IOException e) {
            outputArea.append("创建DOT文件失败: " + e.getMessage() + "\n");
        }
    }

    public String[] showDirectedGraph(String filePath, String dotFilePath, String imageFilePath) throws IOException {
        outputArea.append("开始生成有向图...\n");
        String[] words = buildDirectedGraph(filePath);

        int batchSize = 1000; // 每批处理的单词数量
        int totalBatches = (words.length + batchSize - 1) / batchSize;

        for (int i = 0; i < totalBatches; i++) {
            int start = i * batchSize;
            int end = Math.min(start + batchSize, words.length);
            String batchDotFilePath = dotFilePath + "_batch" + i + ".dot";
            String batchImageFilePath = imageFilePath + "_batch" + i + ".png";
            createDotFile(batchDotFilePath, null, words[start]);
            convertDotToImage(batchDotFilePath, batchImageFilePath);
            outputArea.append("生成有向图批次 " + (i + 1) + "/" + totalBatches + ": " + batchImageFilePath + "\non");
        }

        return words;
    }

    public static void convertDotToImage(String dotFilePath, String imageFilePath) {
        try {
            //使用 ProcessBuilder 调用 Graphviz 的 fdp 布局引擎，将 DOT 文件转换为 PNG 图像
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "fdp",
                    "-Gmemory_limit=2G",  // 增加内存限制到2GB
                    "-Tpng",
                    dotFilePath,
                    "-o",
                    imageFilePath
            );
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IOException("Graphviz转换失败，退出码: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("转换DOT文件为图像失败", e);
        }
    }

    public Set<String> queryBridgeWords(String start, String end, Boolean print) {
        List<String> path = new ArrayList<>();
        //检查 start 和 end 是否存在于图中（directedGraph）
        if (!directedGraph.containsKey(start) && print) {
            outputArea.append("在图中没有“" + start + "”\n");
            return null;
        }
        if (!directedGraph.containsKey(end) && print) {
            outputArea.append("在图中没有“" + end + "”\n");
            return null;
        }
        //bridgeWords：用于存储找到的桥接词。
        //visited：用于记录已访问过的节点，避免重复访问。
        //queue：用于实现广度优先搜索（BFS）。
        //将起始单词 start 添加到队列中，并标记为已访问。
        //从队列中取出第一个单词作为当前节点current
        Set<String> bridgeWords = new HashSet<>();
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        queue.offer(start);
        visited.add(start);
        String current = queue.poll();
        //遍历当前节点的所有邻居节点（neighbor），如果邻居节点未被访问过，将其标记为已访问，将其添加到队列中，检查该邻居节点是否可以直接到达目标节点 end
        //如果可以到达，将其添加到 bridgeWords 中，并打印到输出区域
        for (Map.Entry<String, Integer> entry : directedGraph.getOrDefault(current, Collections.emptyMap()).entrySet()) {
            String neighbor = entry.getKey();
            if (!visited.contains(neighbor)) {
                visited.add(neighbor);
                queue.offer(neighbor);
                if (directedGraph.containsKey(neighbor) && directedGraph.get(neighbor).containsKey(end)) {
                    bridgeWords.add(neighbor);
                    if (print) {
                        outputArea.append("桥接词为：" + neighbor + "\n");
                    }
                }
            }
        }

        if (bridgeWords.isEmpty() && print) {
            outputArea.append(start + "和" + end + "之间没有桥接词\n");
        }
        return bridgeWords;
    }

    private List<List<Object>> findAllPaths(String current, String end, Set<String> visited, List<String> path, int currentLength) {
        //如果当前节点 current 等于目标节点 end，说明找到了一条完整的路径
        if (current.equals(end)) {
            List<String> newPath = new ArrayList<>(path);
            newPath.add(current);
            List<Object> resultPath = new ArrayList<>();
            resultPath.add(newPath);
            resultPath.add(currentLength);
            List<List<Object>> result = new ArrayList<>();
            result.add(resultPath);
            return result;
        }

        visited.add(current);
        List<List<Object>> allPaths = new ArrayList<>();
        //遍历当前节点的邻接节点
        for (Map.Entry<String, Integer> entry : directedGraph.getOrDefault(current, Collections.emptyMap()).entrySet()) {
            //entry.getKey()：获取邻接节点的名称。
            //entry.getValue()：获取从当前节点到邻接节点的边的权重（路径长度）。
            String neighbor = entry.getKey();
            if (!visited.contains(neighbor)) {
                path.add(current);
                //传入的路径长度为 currentLength + entry.getValue()，即当前路径长度加上从当前节点到邻接节点的边的权重
                List<List<Object>> neighborPaths = findAllPaths(neighbor, end, visited, path, currentLength + entry.getValue());
                allPaths.addAll(neighborPaths);
                //从路径 path 中移除最后一个节点（即当前节点 current），以便回溯
                path.remove(path.size() - 1);
            }
        }
        //在递归返回之前，从 visited 集合中移除当前节点 current，以便其他路径可以再次访问该节点
        visited.remove(current);
        return allPaths;
    }

    public String generateNewText(String inputText) {
        outputArea.append("原始文本: " + inputText + "\n");

        StringBuilder newText = new StringBuilder();
        //将输入文本按空格分割成单词数组
        String[] words = inputText.split(" ");
        //随机选择桥接词
        Random random = new Random();

        for (int i = 0; i < words.length - 1; i++) {
            //遍历单词数组，每次处理两个相邻的单词：currentWord 和 nextWord。
            //将当前单词添加到 newText 中，并追加一个空格
            String currentWord = words[i];
            String nextWord = words[i + 1];
            newText.append(currentWord).append(" ");

            Set<String> bridgeWords = queryBridgeWords(currentWord, nextWord, false);
            if (bridgeWords != null && !bridgeWords.isEmpty()) {
                List<String> bridgeWordsList = new ArrayList<>(bridgeWords);
                String randomBridge = bridgeWordsList.get(random.nextInt(bridgeWordsList.size()));
                newText.append(randomBridge).append(" ");
            }
        }
        //添加最后一个单词
        newText.append(words[words.length - 1]);
        return newText.toString();
    }

    public List<List<Object>> calcShortestPath(String word1, String word2, int i, String root) {
        outputArea.append("计算从 " + word1 + " 到 " + word2 + " 的最短路径\n");

        //path：用于存储当前路径的节点。
        //visited：用于记录已经访问过的节点，防止重复访问。
        List<String> path = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        List<List<Object>> allPaths = findAllPaths(word1, word2, visited, path, 0);

        if (allPaths.isEmpty()) {
            outputArea.append(word1 + " 和 " + word2 + " 之间没有路径\n");
            return null;
        }

        outputArea.append(word1 + " 和 " + word2 + " 之间的路径:\n");
        int minLength = Integer.MAX_VALUE;
        for (List<Object> p : allPaths) {
            //遍历 allPaths，提取每条路径的长度
            int length = (int) p.get(1);
            if (length < minLength) {
                minLength = length;
            }
            outputArea.append(p + "\n");
        }

        List<List<Object>> shortestPaths = new ArrayList<>();
        for (List<Object> p : allPaths) {
            int length = (int) p.get(1);
            if (length == minLength) {
                shortestPaths.add(p);
            }
        }

        String dotFilePath = "./output/shortest/directed_graph_shortest" + i + ".dot";
        String imageFilePath = "./output/shortest/directed_graph_shortest" + i + ".png";
        createDotFile(dotFilePath, shortestPaths, root);
        convertDotToImage(dotFilePath, imageFilePath);
        outputArea.append("最短路径图已保存到: " + imageFilePath + "\n");

        return shortestPaths;
    }

    public String randomWalk() {
        //Random random：创建一个随机数生成器。
        //vertices：将图的所有节点（directedGraph.keySet()）存储到一个列表中。
        //currentVertex：从节点列表中随机选择一个节点作为起始节点
        Random random = new Random();
        List<String> vertices = new ArrayList<>(directedGraph.keySet());
        String currentVertex = vertices.get(random.nextInt(vertices.size()));

        StringBuilder randomWalkText = new StringBuilder();
        List<String> visitedVertices = new ArrayList<>();

        while (!stopWalk) {
            //将当前节点添加到访问记录中。
            //将当前节点追加到随机游走路径的文本中
            visitedVertices.add(currentVertex);
            randomWalkText.append(currentVertex).append(" ");

            outputArea.append(currentVertex + " -> ");
            //获取当前节点的所有出边
            Map<String, Integer> edges = directedGraph.get(currentVertex);
            if (edges != null && !edges.isEmpty()) {
                List<String> nextVertices = new ArrayList<>(edges.keySet());
                String nextVertex = nextVertices.get(random.nextInt(nextVertices.size()));
                currentVertex = nextVertex;

                try {
                    Thread.sleep(500); // 暂停一段时间以便观察
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            } else {
                break;
            }
        }

        // 添加最后一个节点
        randomWalkText.append(currentVertex);
        outputArea.append(currentVertex + "\n");

        String outputPath = "./output/random_walk.txt";
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputPath))) {
            writer.println(randomWalkText.toString());
        } catch (IOException e) {
            outputArea.append("保存随机游走结果失败: " + e.getMessage() + "\n");
        }

        stopWalk = false;
        return outputPath;
    }

    public Map<String, Double> calculatePageRank(double dampingFactor, int iterations) {
        if (directedGraph.isEmpty() || iterations <= 0) {
            return Collections.emptyMap();
        }
        // 初始化PR值
        Map<String, Double> pageRank = new HashMap<>();
        int N = directedGraph.size();
        double initialValue = 1.0 / N;
        // 所有节点初始PR值为1/N
        for (String node : directedGraph.keySet()) {
            pageRank.put(node, initialValue);
        }

        // 迭代计算
        for (int i = 0; i < iterations; i++) {
            Map<String, Double> newPageRank = new HashMap<>();
            double danglingPR = 0.0; // 处理出度为0的节点

            // 计算出度为0的节点的总PR值
            for (String node : directedGraph.keySet()) {
                if (directedGraph.get(node).isEmpty()) {
                    danglingPR += pageRank.get(node);
                }
            }

            // 将danglingPR均分给所有节点
            double danglingContribution = dampingFactor * danglingPR / N;

            // 计算每个节点的新PR值
            for (String u : directedGraph.keySet()) {
                double sum = 0.0;

                // 找出所有指向u的节点(B_u)
                for (String v : directedGraph.keySet()) {
                    if (directedGraph.get(v).containsKey(u)) {
                        int L_v = directedGraph.get(v).size(); // v的出度
                        sum += pageRank.get(v) / L_v;
                    }
                }

                // 应用PageRank公式
                double newPR = (1 - dampingFactor) / N + dampingFactor * sum + danglingContribution;
                newPageRank.put(u, newPR);
            }

            pageRank = newPageRank;
        }

        return pageRank;
    }

    private void showPageRankDialog() {
        JTextField dampingField = new JTextField("0.85", 5);
        JTextField iterationsField = new JTextField("20", 5);

        JPanel panel = new JPanel();
        panel.add(new JLabel("阻尼因子(0-1):"));
        panel.add(dampingField);
        panel.add(new JLabel("迭代次数:"));
        panel.add(iterationsField);

        int result = JOptionPane.showConfirmDialog(frame, panel,
                "PageRank参数设置", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                double dampingFactor = Double.parseDouble(dampingField.getText());
                int iterations = Integer.parseInt(iterationsField.getText());

                if (dampingFactor < 0 || dampingFactor > 1) {
                    outputArea.append("阻尼因子必须在0到1之间\n");
                    return;
                }

                outputArea.append("开始计算PageRank(d=" + dampingFactor +
                        ", 迭代" + iterations + "次)...\n");

                Map<String, Double> pageRank = calculatePageRank(dampingFactor, iterations);

                // 按PR值排序
                List<Map.Entry<String, Double>> sortedPR = new ArrayList<>(pageRank.entrySet());
                sortedPR.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

                outputArea.append("PageRank结果(从高到低):\n");
                for (Map.Entry<String, Double> entry : sortedPR) {
                    outputArea.append(String.format("%s: %.6f\n", entry.getKey(), entry.getValue()));
                }

                savePageRankResults(pageRank, "./output/pagerank_results.txt");
                outputArea.append("PageRank结果已保存到文件\n");

            } catch (NumberFormatException e) {
                outputArea.append("请输入有效的数字\n");
            }
        }
    }

    private void savePageRankResults(Map<String, Double> pageRank, String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // 按PR值排序
            //将 pageRank 的条目（Map.Entry<String, Double>）转换为一个列表。
            //使用 sort 方法对列表进行排序，按 PageRank 值从高到低排列
            List<Map.Entry<String, Double>> sorted = new ArrayList<>(pageRank.entrySet());
            sorted.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

            writer.println("PageRank结果(从高到低):");
            for (Map.Entry<String, Double> entry : sorted) {
                writer.printf("%s: %.6f%n", entry.getKey(), entry.getValue());
            }
        } catch (IOException e) {
            outputArea.append("保存PageRank结果失败: " + e.getMessage() + "\n");
        }
    }

    private Map<String, Double> calculateTFIDF(String[] document) {
        // 1. 计算TF (词频)
        Map<String, Integer> termFrequency = new HashMap<>();
        int totalTerms = document.length;

        for (String word : document) {
            //使用 getOrDefault 方法获取单词的当前词频
            termFrequency.put(word, termFrequency.getOrDefault(word, 0) + 1);
        }

        // 2. 计算IDF (逆文档频率) - 整个文档是一个"文档集"
        Map<String, Integer> documentFrequency = new HashMap<>();
        //从 termFrequency 中提取的唯一单词集合
        Set<String> uniqueTerms = termFrequency.keySet();

        for (String term : uniqueTerms) {
            int count = 0;
            for (String word : document) {
                if (word.equals(term)) {
                    count++;
                }
            }
            //遍历文档中的每个单词，统计当前单词是否出现（count）。
            //如果单词出现过（count > 0），则将文档频率设为 1，否则设为 0
            documentFrequency.put(term, count > 0 ? 1 : 0); // 简化处理
        }

        int totalDocuments = 1; // 我们只有一个文档
        Map<String, Double> tfidf = new HashMap<>();

        for (String term : uniqueTerms) {
            double tf = (double) termFrequency.get(term) / totalTerms;
            double idf = Math.log((double) totalDocuments / (documentFrequency.get(term) + 1));
            tfidf.put(term, tf * idf);
        }

        return tfidf;
    }


    public Map<String, Double> calculateImprovedPageRank(double dampingFactor, int iterations) {
        // 计算TF-IDF值
        Map<String, Double> tfidf = calculateTFIDF(allWords);

        // 初始化PR值 - 使用TF-IDF归一化值
        Map<String, Double> pageRank = new HashMap<>();
        double sumTFIDF = tfidf.values().stream().mapToDouble(Double::doubleValue).sum();

        //遍历 TF-IDF 值的条目：
        //将每个单词的 TF-IDF 值除以总和 sumTFIDF，得到归一化的初始 PageRank 值。
        //将归一化的初始 PageRank 值存储到 pageRank 中
        for (Map.Entry<String, Double> entry : tfidf.entrySet()) {
            pageRank.put(entry.getKey(), entry.getValue() / sumTFIDF);
        }

        // 迭代计算
        for (int i = 0; i < iterations; i++) {
            Map<String, Double> newPageRank = new HashMap<>();
            double danglingPR = 0.0;

            // 处理出度为0的节点
            for (String node : directedGraph.keySet()) {
                if (directedGraph.get(node).isEmpty()) {
                    danglingPR += pageRank.get(node);
                }
            }

            double danglingContribution = dampingFactor * danglingPR / directedGraph.size();

            // 计算每个节点的新PR值
            for (String u : directedGraph.keySet()) {
                double sum = 0.0;

                // 找出所有指向u的节点
                for (String v : directedGraph.keySet()) {
                    if (directedGraph.get(v).containsKey(u)) {
                        int L_v = directedGraph.get(v).size();
                        sum += pageRank.get(v) / L_v;
                    }
                }

                double newPR = (1 - dampingFactor) * (tfidf.getOrDefault(u, 0.0) / sumTFIDF)
                        + dampingFactor * sum
                        + danglingContribution;
                newPageRank.put(u, newPR);
            }

            pageRank = newPageRank;
        }

        return pageRank;
    }

    private void showImprovedPRDialog() {
        JTextField dampingField = new JTextField("0.85", 5);
        JTextField iterationsField = new JTextField("20", 5);

        JPanel panel = new JPanel();
        panel.add(new JLabel("阻尼因子(0-1):"));
        panel.add(dampingField);
        panel.add(new JLabel("迭代次数:"));
        panel.add(iterationsField);

        int result = JOptionPane.showConfirmDialog(frame, panel,
                "改进PageRank参数设置", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                double dampingFactor = Double.parseDouble(dampingField.getText());
                int iterations = Integer.parseInt(iterationsField.getText());

                if (dampingFactor < 0 || dampingFactor > 1) {
                    outputArea.append("阻尼因子必须在0到1之间\n");
                    return;
                }

                outputArea.append("开始计算改进的PageRank(基于TF-IDF)...\n");
                outputArea.append("参数: d=" + dampingFactor + ", 迭代" + iterations + "次\n");

                Map<String, Double> improvedPR = calculateImprovedPageRank(dampingFactor, iterations);

                // 按PR值排序
                List<Map.Entry<String, Double>> sortedPR = new ArrayList<>(improvedPR.entrySet());
                sortedPR.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

                outputArea.append("改进的PageRank结果(从高到低):\n");
                for (Map.Entry<String, Double> entry : sortedPR) {
                    outputArea.append(String.format("%s: %.6f\n", entry.getKey(), entry.getValue()));
                }

                savePageRankResults(improvedPR, "./output/improved_pagerank_results.txt");
                outputArea.append("改进的PageRank结果已保存到文件\n");

            } catch (NumberFormatException e) {
                outputArea.append("请输入有效的数字\n");
            }
        }
    }
}
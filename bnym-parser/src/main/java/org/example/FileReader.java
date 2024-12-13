package org.example;

import java.util.*;

public class FileReader {

    HashMap<String, List<Node>> generalTree = new HashMap<>();
    int indexCounter = 0;
    Stack<Node> stack = new Stack<>();


    public void readFile(String filePath) {

        try (Scanner fileReader = new Scanner(Objects.requireNonNull(FileReader.class.getClassLoader().getResourceAsStream(filePath)))) {

            while (fileReader.hasNextLine()) {
                String line = fileReader.nextLine().trim();
                String key = line.substring(0, 2);
                String value = line.substring(2);

                if (key.equals("01"))
                    cleanStack();

                if (!generalTree.containsKey(key)) {
                    generalTree.put(key, new ArrayList<>());
                }

                if (!stack.isEmpty() && ((Integer.parseInt(key) <= Integer.parseInt(stack.peek().getType())) || ((stack.peek().getType().equals("04")) && key.equals("07")))) {
                    updateNodeInStack();
                }

                Node node = createNode(key, value);

                if (key.equals("01") || key.equals("03") || key.equals("04")) stack.push(node);
                else generalTree.get(key).add(node);

            }
            cleanStack();
        }
    }

    private void updateNodeInStack() {
        Node poppedNode = stack.pop();
        poppedNode.setRightID(indexCounter + 1);
        indexCounter++;
        generalTree.get(poppedNode.getType()).add(poppedNode);

        if (poppedNode.getType().equals("01")) publishMapToDB();
    }

    private void cleanStack() {
        while (!stack.isEmpty()) {
            updateNodeInStack();
        }
    }

    private void publishMapToDB() {
        System.out.println("Publishing the Tree to DB. After Checking that the data in the Tree is not null " + generalTree.size());
        insertIntoDB();
        stack.clear();
        generalTree.clear();
    }

    private void insertIntoDB() {
        /**
         * Establish DB Connection
         * Create Batches for the data in the map
         * Perform a batch insert in the DB
         */
    }

    private Node createNode(String key, String value) {
        Node node;
        if (key.equals("02") || key.equals("05") || key.equals("06") || key.equals("07")) {
            node = Node.builder()
                    .type(key)
                    .value(value)
                    .leftID(indexCounter + 1)
                    .rightID(indexCounter + 2)
                    .build();
            indexCounter = indexCounter + 2;
        } else {
            node = Node.builder()
                    .type(key)
                    .value(value)
                    .leftID(indexCounter + 1)
                    .build();
            indexCounter++;
        }
        return node;
    }
}

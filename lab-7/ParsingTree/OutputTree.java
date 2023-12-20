import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OutputTree {

    private ParsingTreeRow root;

    private final Grammar grammar;

    private int currentIndex = 1;

    private int indexInInput = 1;

    private int maxLevel = 0;

    private List<ParsingTreeRow> treeList;

    public OutputTree(Grammar grammar){
        this.grammar = grammar;
    }

    public ParsingTreeRow getRoot(){
        return this.root;
    }

    public ParsingTreeRow generateTreeFromSequence(List<Integer> inputSequence){
        int productionIndex = inputSequence.get(0);

        Pair<String, List<String>> productionString = this.grammar.getOrderedProductions().get(productionIndex);

        this.root = new ParsingTreeRow(productionString.getFirst());
        this.root.setIndex(0);
        this.root.setLevel(0);

        this.root.setLeftChild(buildRecursive(1, this.root, productionString.getSecond(), inputSequence));

        return this.root;
    }

    public ParsingTreeRow buildRecursive(int level, ParsingTreeRow parent, List<String> currentContent, List<Integer> inputSequence){
        if(currentContent.isEmpty() || this.indexInInput >= inputSequence.size() + 1){
            return null;
        }
        String currentSymbol = currentContent.get(0);

        if(this.grammar.getTerminals().contains(currentSymbol)){

            ParsingTreeRow node = new ParsingTreeRow(currentSymbol);

            node.setIndex(this.currentIndex);
            this.currentIndex++;

            node.setLevel(level);
            node.setParent(parent);

            List<String> newList = new ArrayList<>(currentContent);
            newList.remove(0);

            node.setRightSibling(buildRecursive(level, parent, newList, inputSequence));

            return node;

        } else if(this.grammar.getNonTerminals().contains(currentSymbol)){

            int productionIndex = inputSequence.get(this.indexInInput);

            Pair<String, List<String>> productionString = this.grammar.getOrderedProductions().get(productionIndex);

            ParsingTreeRow node = new ParsingTreeRow(currentSymbol);

             node.setIndex(this.currentIndex);
             node.setLevel(level);
             node.setParent(parent);

             int newLevel = level + 1;
             if(newLevel > this.maxLevel)
                 this.maxLevel = newLevel;

             this.currentIndex++;
             this.indexInInput++;

             node.setLeftChild(buildRecursive(newLevel, node, productionString.getSecond(), inputSequence));

             List<String> newList = new ArrayList<>(currentContent);
             newList.remove(0);

             node.setRightSibling(buildRecursive(level, parent, newList, inputSequence));

             return node;
        }
        else {
            System.out.println("ERROR");
            return null;
        }
    }

    public void printTree(ParsingTreeRow node, String filePath) throws IOException {
        this.treeList = new ArrayList<>();
        createList(node);

        for(int i = 0; i <= this.maxLevel; i++){
            for(ParsingTreeRow n: this.treeList){
                if(n.getLevel() == i){
                    System.out.println(n);
                    writeToFile(filePath, n.toString());
                }
            }
        }
    }

    public void writeToFile(String file, String line) throws IOException {
        FileWriter fw = new FileWriter(file, true);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(line);
        bw.newLine();
        bw.close();
    }

    public void createList(ParsingTreeRow node){
        if(node == null)
            return;

        while(node != null){

            this.treeList.add(node);
            if(node.getLeftChild() != null){
                createList(node.getLeftChild());
            }

            node = node.getRightSibling();
        }

    }



}

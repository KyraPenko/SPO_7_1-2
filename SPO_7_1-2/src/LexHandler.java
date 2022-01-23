import java.util.ArrayList;

public class LexHandler {
    Pair<String, String> label;
    ArrayList<LexHandler> children;

    public LexHandler(Pair<String, String> label) {
        this.label = label;
        this.children = new ArrayList<>();
    }

    public void addChild(LexHandler child)
    {
        children.add(child);
    }

    public Pair<String, String> getLabel() {
        return label;
    }

    public ArrayList<LexHandler> getChildren() {
        return children;
    }

    public void showTree()
    {
        System.out.println("PARENT");
        System.out.println(label.toString());
        System.out.println("CHILDREN");
        for (LexHandler node:
                children) {
            System.out.println(node.getLabel().toString());
        }
        for (LexHandler node:
                children) {
            node.showTree();
        }
    }

    @Override
    public String toString() {
        return "PARENT\n" +
                label +
                "\nCHILDREN\n" +
                children;
    }
}

public class LexTree {
    LexHandler root;

    public LexTree(Pair<String, String> label) {
        this.root = new LexHandler(label);
    }

    public LexTree(LexHandler root) {
        this.root = root;
    }

    public void addExpr(LexHandler node)
    {
        root.addChild(node);
    }

    public LexHandler getRoot() {
        return root;
    }

    public void showTree()
    {
        root.showTree();
    }

}

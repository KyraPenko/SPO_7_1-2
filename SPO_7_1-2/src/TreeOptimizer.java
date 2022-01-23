

public class TreeOptimizer {
    public static LexTree optimizeTree(LexTree tree){
        tree = new LexTree(treeTraversal(tree.getRoot()));
        return tree;
    }

    public static LexHandler treeTraversal(LexHandler node){
        if(node.getLabel().getFirst().equals("VAR") || node.getLabel().getFirst().equals("VAR_TYPE") ||
                node.getLabel().getFirst().equals("IF_LK") || node.getLabel().getFirst().equals("ELSE_LK") ||
                node.getLabel().getFirst().equals("WHILE_LK") || node.getLabel().getFirst().equals("DO_LK") ||
                node.getLabel().getFirst().equals("OP") || node.getLabel().getFirst().equals("LOGICAL_OP") ||
                node.getLabel().getFirst().equals("ASSIGN_OP") || node.getLabel().getFirst().equals("NUMBER") ||
                node.getLabel().getFirst().equals("L_BR") || node.getLabel().getFirst().equals("R_BR") ||
                node.getLabel().getFirst().equals("L_S_BR") || node.getLabel().getFirst().equals("R_S_BR") ||
                node.getLabel().getFirst().equals("SEP") || node.getLabel().getFirst().equals("PRINT_LK")) {
            return node;
        }
        else {
            for(int i = 0; i < node.getChildren().size(); i++){
                if(node.getChildren().get(i).getLabel().getFirst().equals("if_expr")){
                    node.getChildren().set(i, optimizeIf(node.getChildren().get(i)));
                    if(node.getChildren().get(i) == null) {
                        node.getChildren().remove(i);
                        i--;
                        System.out.println("Delete\n" + node.getChildren().toString());
                        return null;
                    }
                }
                else if(node.getChildren().get(i).getLabel().getFirst().equals("while_expr") ||
                        node.getChildren().get(i).getLabel().getFirst().equals("do_while_expr")){
                    node.getChildren().set(i, optimizeWhile(node.getChildren().get(i)));
                    if(node.getChildren().get(i) == null) {
                        node.getChildren().remove(i);
                        i--;
                        System.out.println("Delete\n" + node.getChildren().toString());
                        return null;
                    }
                }

                else{
                    LexHandler subNode = treeTraversal(node.getChildren().get(i));
                    if(subNode == null) {
                        node.getChildren().remove(i);
                        i--;
                    }
                }
            }
            /*for (LexHandler child :
                    node.getChildren()) {
                res.addAll(treeToList(child));
            }*/
        }
        return node;
    }

    public static LexHandler optimizeIf(LexHandler expr){
        if(expr.getChildren().get(1).getChildren().size() < 3){ // пустой if
            System.out.println("Find empty if");
            if(expr.getChildren().size() > 2){ // найден else
                System.out.println("\tFind else");
                if(expr.getChildren().get(3).getChildren().size() < 3){ // пустой else
                    System.out.println("\tElse is empty");
                    return null;
//                    expr = null;
//                    System.out.println("BEFORE\n" + expr.getChildren().toString());
                }
            }
            else { // нет else
                return null;
//                expr = null;
            }
        }
        else if(expr.getChildren().size() > 2 && expr.getChildren().get(3).getChildren().size() < 3){ // пустой else
            System.out.println("Empty while");
            expr.getChildren().remove(2);
            expr.getChildren().remove(2);
            return expr;
        }
        return expr;
    }

    public static LexHandler optimizeWhile(LexHandler expr){
        if(expr.getChildren().get(1).getChildren().size() < 3){ // пустой while
            System.out.println("Empty while");
            return null;
        }
        return expr;
    }


}

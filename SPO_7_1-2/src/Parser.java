import java.util.ArrayList;

public class Parser {
    static ArrayList<Pair<String, String>> tokens;

    /*public static ArrayList<Pair<String, String>> getRPN(ArrayList<Pair<String, String>> tokens_list) {
        tokens = new ArrayList<>(tokens_list);

        try {
            LexTree tree = lang();
            System.out.println("--------------------------------");
            System.out.println("TREE");
            System.out.println("--------------------------------");
            tree.showTree();
            return RPN.getRPN(tokens_list);
        }
        catch (Exception e)
        {
            return null;
        }

    }*/

    public static LexTree getSyntaxTree(ArrayList<Pair<String, String>> tokens_list){
        tokens = new ArrayList<>(tokens_list);

        try {
            LexTree tree = lang();
            return tree;
        }
        catch (Exception e)
        {
            System.out.println(e);
            return null;
        }
    }

    public static LexTree lang()
    {
        LexTree tree = new LexTree(new Pair("lang", ""));
        ArrayList<Pair<String, String>> buffer = new ArrayList<>();
        LexHandler expression;
        for (int i = 0; i < tokens.size(); i++)
        {
            buffer.add(tokens.get(i));
            if(buffer.get(0).getFirst().equals("IF_LK")) {
                i++;
                int bracketCount;
                while(true)
                {
                    while (i < tokens.size() && !tokens.get(i).getFirst().equals("R_S_BR")) {
                        buffer.add(tokens.get(i));
                        i++;
                    }
                    if(i < tokens.size() - 1 && tokens.get(i + 1).getFirst().equals("ELSE_LK"))
                    {
                        buffer.add(tokens.get(i));
                        i++;
                    }
                    else
                    {
                        buffer.add(tokens.get(i));
                        break;
                    }
                }
            }
            else if(buffer.get(0).getFirst().equals("WHILE_LK")) {
                i++;
                while (i < tokens.size() && !tokens.get(i).getFirst().equals("R_S_BR")) {
                    buffer.add(tokens.get(i));
                    i++;
                }
                buffer.add(tokens.get(i));
            }
            else if(buffer.get(0).getFirst().equals("DO_LK"))
            {
                i++;
                while (i < tokens.size() && !tokens.get(i).getFirst().equals("WHILE_LK")) {
                    buffer.add(tokens.get(i));
                    i++;
                }
                while (i < tokens.size() && !tokens.get(i).getFirst().equals("SEP")) {
                    buffer.add(tokens.get(i));
                    i++;
                }
                buffer.add(tokens.get(i));
            }
            expression = expr(buffer);
            try {
                if (expression != null)
                {
                    tree.addExpr(expression);
                    buffer.clear();
                }
            }
            catch (Exception e)
            {
                System.out.println(e.getMessage());
                System.exit(1);
            }
        }
        return tree;
    }

    public static LexHandler expr(ArrayList<Pair<String, String>> tokens_list)
    {
        System.out.println(tokens_list.toString());
        LexHandler expression, parent = new LexHandler(new Pair<>("expr", ""));
        expression = declaring_expr(tokens_list);
        if(expression != null)
        {
            parent.addChild(expression);
            return parent;
        }
        expression = assign_expr(tokens_list);
        if(expression != null)
        {
            parent.addChild(expression);
            return parent;
        }
        expression = if_expr(tokens_list);
        if(expression != null)
        {
            parent.addChild(expression);
            return parent;
        }
        expression = while_expr(tokens_list);
        if(expression != null)
        {
            parent.addChild(expression);
            return parent;
        }
        expression = do_while_expr(tokens_list);
        if(expression != null)
        {
            parent.addChild(expression);
            return parent;
        }
        expression = print_expr(tokens_list);
        if(expression != null)
        {
            parent.addChild(expression);
            return parent;
        }
        return null;
    }

    public static LexHandler declaring_expr(ArrayList<Pair<String, String>> tokens_list)
    {
        if(tokens_list.size() < 3 || !tokens_list.get(0).getFirst().equals("VAR_TYPE") || !tokens_list.get(1).getFirst().equals("VAR") ||
                !tokens_list.get(tokens_list.size() - 1).getFirst().equals("SEP"))
            return null;
        LexHandler res = new LexHandler(new Pair<>("declaring_expr", ""));
        res.addChild(new LexHandler(tokens_list.get(0)));
        res.addChild(new LexHandler(tokens_list.get(1)));
        if(tokens_list.get(2).getFirst().equals("ASSIGN_OP"))
        {
            LexHandler expression = arithmetic_expr(new ArrayList<Pair<String, String>>(tokens_list.subList(3, tokens_list.size() - 1)));
            if(expression == null)
                return null;
            res.addChild(new LexHandler(tokens_list.get(2)));
            res.addChild(expression);
        }
        res.addChild(new LexHandler(tokens_list.get(tokens_list.size() - 1)));
        return res;
    }

    public static LexHandler assign_expr(ArrayList<Pair<String, String>> tokens_list)
    {
        if(tokens_list.size() < 4 || !tokens_list.get(0).getFirst().equals("VAR") || !tokens_list.get(1).getFirst().equals("ASSIGN_OP") ||
                !tokens_list.get(tokens_list.size() - 1).getFirst().equals("SEP"))
            return null;
        LexHandler res = new LexHandler(new Pair<>("assign_expr", ""));
        res.addChild(new LexHandler(tokens_list.get(0)));
        res.addChild(new LexHandler(tokens_list.get(1)));
        LexHandler expression = arithmetic_expr(new ArrayList<Pair<String, String>>(tokens_list.subList(2, tokens_list.size() - 1)));
        if(expression == null)
            return null;
        res.addChild(expression);
        res.addChild(new LexHandler(tokens_list.get(tokens_list.size() - 1)));
        return res;
    }

    public static LexHandler arithmetic_expr(ArrayList<Pair<String, String>> tokens_list)
    {
        LexHandler res = new LexHandler(new Pair<>("arithmetic_expr", ""));
        LexHandler expression = value(tokens_list.get(0));
        Integer rightBracket = 0;
        if(expression != null)
            res.addChild(expression);
        else if(tokens_list.get(0).getFirst().equals("L_BR") && (rightBracket = getPairBracket(tokens_list, 0, "L_BR", "R_BR")) != null)
        {
            res.addChild(new LexHandler(tokens_list.get(0)));
            expression = arithmetic_expr(new ArrayList<>(tokens_list.subList(1, rightBracket)));
            if(expression == null)
                return null;
            res.addChild(expression);
            res.addChild(new LexHandler((tokens_list.get(rightBracket))));
        }
        else
            return null;
        Integer i = rightBracket + 1, j;
        while(i < tokens_list.size() && tokens_list.get(i).getFirst().equals("OP"))
        {
            j = i;
            j++;
            while(j < tokens_list.size() && !tokens_list.get(j).getFirst().equals("OP"))
                j++;
            if(j == tokens_list.size())
                expression = arithmetic_expr(new ArrayList<>(tokens_list.subList(i + 1, j)));
            else
                expression = arithmetic_expr(new ArrayList<>(tokens_list.subList(i + 1, j - 1)));
            if(expression == null)
                return null;
            res.addChild(new LexHandler(tokens_list.get(i)));
            res.addChild(expression);
            i = j;
        }
        return res;
    }

    public static LexHandler if_expr(ArrayList<Pair<String, String>> tokens_list)
    {
        LexHandler res = new LexHandler(new Pair<>("if_expr", ""));
        Integer rightBracket = 0, rightCurlyBracket = 0;
        LexHandler expression;
        if(tokens_list.size() > 3 && tokens_list.get(0).getFirst().equals("IF_LK") && tokens_list.get(1).getFirst().equals("L_BR") &&
                (rightBracket = getPairBracket(tokens_list, 1, "L_BR", "R_BR")) != null &&
                (rightCurlyBracket = getPairBracket(tokens_list, rightBracket + 1, "L_S_BR", "R_S_BR")) != null)
        {
            expression = if_head(new ArrayList<Pair<String, String>>(tokens_list.subList(0, rightBracket + 1)));
            if(expression == null){
                return null;
            }
            res.addChild(expression);
            expression = if_body(new ArrayList<Pair<String, String>>(tokens_list.subList(rightBracket + 1, rightCurlyBracket + 1)));
            if(expression == null){
                return null;
            }
            res.addChild(expression);
            Integer i = rightCurlyBracket + 1, leftCurlyBracket;
            while(i < tokens_list.size() && tokens_list.get(i).getFirst().equals("ELSE_LK"))
            {
                leftCurlyBracket = i;
                while (leftCurlyBracket < tokens_list.size() && !tokens_list.get(leftCurlyBracket).getFirst().equals("L_S_BR"))
                    leftCurlyBracket++;
                expression = else_head(new ArrayList<Pair<String, String>>(tokens_list.subList(i, leftCurlyBracket)));
                if(expression != null)
                    res.addChild(expression);
                else
                    return null;
                if(tokens_list.get(leftCurlyBracket).getFirst().equals("L_S_BR") &&
                        (rightCurlyBracket = getPairBracket(tokens_list, leftCurlyBracket, "L_S_BR", "R_S_BR")) != null)
                    expression = else_body(new ArrayList<Pair<String, String>>(tokens_list.subList(leftCurlyBracket, rightCurlyBracket + 1)));
                if(expression != null)
                    res.addChild(expression);
                else
                    return null;
                i = rightCurlyBracket + 1;
            }
            return res;
        }
        return null;
    }

    public static LexHandler if_head(ArrayList<Pair<String, String>> tokens_list)
    {
        LexHandler res = new LexHandler(new Pair<>("if_head", ""));
        Integer rightBracket = 0;
        LexHandler expression;
        if(tokens_list.get(0).getFirst().equals("IF_LK") && tokens_list.get(1).getFirst().equals("L_BR") &&
                (rightBracket = getPairBracket(tokens_list, 1, "L_BR", "R_BR")) != null)
        {
            res.addChild(new LexHandler(tokens_list.get(0)));
            expression = condition(new ArrayList<Pair<String, String>>(tokens_list.subList(1, rightBracket + 1)));
            if(expression == null) {
                return null;
            }
            res.addChild(expression);
            return res;
        }
        return null;
    }

    public static LexHandler if_body(ArrayList<Pair<String, String>> tokens_list)
    {
        LexHandler res = new LexHandler(new Pair<>("if_body", ""));
        res.addChild(new LexHandler(tokens_list.get(0)));
        ArrayList<Pair<String, String>> buffer = new ArrayList<>();
        LexHandler expression;
        for (int i = 1; i < tokens_list.size() - 1; i++)
        {
            buffer.add(tokens_list.get(i));
            if(buffer.get(0).getFirst().equals("IF_LK")) {
                i++;
                while(true)
                {
                    while (i < tokens_list.size() && !tokens_list.get(i).getFirst().equals("R_S_BR")) {
                        buffer.add(tokens_list.get(i));
                        i++;
                    }
                    if(i < tokens_list.size() - 1 && tokens_list.get(i + 1).getFirst().equals("ELSE_LK"))
                    {
                        buffer.add(tokens_list.get(i));
                        i++;
                    }
                    else
                    {
                        buffer.add(tokens_list.get(i));
                        break;
                    }
                }
            }
            expression = expr(buffer);
            try {
                if (expression != null)
                {
                    res.addChild(expression);
                    buffer.clear();
                }
            }
            catch (Exception e)
            {
                System.out.println(e.getMessage());
                System.exit(1);
            }
        }
        res.addChild(new LexHandler(tokens_list.get(tokens_list.size() - 1)));
        return res;
    }

    public static LexHandler else_head(ArrayList<Pair<String, String>> tokens_list) {
        LexHandler res = new LexHandler(new Pair<>("else_head", "")), expression;
        Integer rightBracket;
        if (tokens_list.size() > 0 && tokens_list.get(0).getFirst().equals("ELSE_LK"))
        {
            res.addChild(new LexHandler(tokens_list.get(0)));
            if (tokens_list.size() > 2 && tokens_list.get(1).getFirst().equals("IF_LK") && tokens_list.get(2).getFirst().equals("L_BR") &&
                (rightBracket = getPairBracket(tokens_list, 2, "L_BR", "R_BR")) != null)
            {
                expression = if_head(new ArrayList<>(tokens_list.subList(1, rightBracket + 1)));
                if(expression == null)
                    return null;
                res.addChild(expression);
            }
            return res;
        }
        return null;
    }

    public static LexHandler else_body(ArrayList<Pair<String, String>> tokens_list)
    {
        LexHandler res = new LexHandler(new Pair<>("else_body", ""));
        res.addChild(new LexHandler(tokens_list.get(0)));
        ArrayList<Pair<String, String>> buffer = new ArrayList<>();
        LexHandler expression;
        for (int i = 1; i < tokens_list.size() - 1; i++)
        {
            buffer.add(tokens_list.get(i));
            if(buffer.get(0).getFirst().equals("IF_LK")) {
                i++;
                while(true)
                {
                    while (i < tokens_list.size() && !tokens_list.get(i).getFirst().equals("R_S_BR")) {
                        buffer.add(tokens_list.get(i));
                        i++;
                    }
                    if(i < tokens_list.size() - 1 && tokens_list.get(i + 1).getFirst().equals("ELSE_LK"))
                    {
                        buffer.add(tokens_list.get(i));
                        i++;
                    }
                    else
                    {
                        buffer.add(tokens_list.get(i));
                        break;
                    }
                }
            }
            expression = expr(buffer);
            try {
                if (expression != null)
                {
                    res.addChild(expression);
                    buffer.clear();
                }
            }
            catch (Exception e)
            {
                System.out.println(e.getMessage());
                System.exit(1);
            }
        }
        res.addChild(new LexHandler(tokens_list.get(tokens_list.size() - 1)));
        return res;
    }

    public static LexHandler while_expr(ArrayList<Pair<String, String>> tokens_list)
    {
        LexHandler res = new LexHandler(new Pair<>("while_expr", ""));
        Integer rightBracket = 0, rightCurlyBracket = 0;
        LexHandler expression;
        if(tokens_list.size() > 3 && tokens_list.get(0).getFirst().equals("WHILE_LK") && tokens_list.get(1).getFirst().equals("L_BR") &&
                (rightBracket = getPairBracket(tokens_list, 1, "L_BR", "R_BR")) != null &&
                (rightCurlyBracket = getPairBracket(tokens_list, rightBracket + 1, "L_S_BR", "R_S_BR")) != null)
        {
            expression = while_head(new ArrayList<Pair<String, String>>(tokens_list.subList(0, rightBracket + 1)));
            if (expression == null) {
                return null;
            }
            res.addChild(expression);
            expression = while_body(new ArrayList<Pair<String, String>>(tokens_list.subList(rightBracket + 1, rightCurlyBracket + 1)));
            if (expression == null) {
                return null;
            }
            res.addChild(expression);
            return res;
        }
        return null;
    }

    public static LexHandler while_head(ArrayList<Pair<String, String>> tokens_list)
    {
        LexHandler res = new LexHandler(new Pair<>("while_head", ""));
        Integer rightBracket = 0;
        LexHandler expression;
        if(tokens_list.get(0).getFirst().equals("WHILE_LK") && tokens_list.get(1).getFirst().equals("L_BR") &&
                (rightBracket = getPairBracket(tokens_list, 1, "L_BR", "R_BR")) != null)
        {
            res.addChild(new LexHandler(tokens_list.get(0)));
            expression = condition(new ArrayList<Pair<String, String>>(tokens_list.subList(1, rightBracket + 1)));
            if(expression == null) {
                return null;
            }
            res.addChild(expression);
            return res;
        }
        return null;
    }

    public static LexHandler while_body(ArrayList<Pair<String, String>> tokens_list)
    {
        LexHandler res = new LexHandler(new Pair<>("while_body", ""));
        res.addChild(new LexHandler(tokens_list.get(0)));
        ArrayList<Pair<String, String>> buffer = new ArrayList<>();
        LexHandler expression;
        for (int i = 1; i < tokens_list.size() - 1; i++)
        {
            buffer.add(tokens_list.get(i));
            if(buffer.get(0).getFirst().equals("IF_LK")) {
                i++;
                while(true)
                {
                    while (i < tokens_list.size() && !tokens_list.get(i).getFirst().equals("R_S_BR")) {
                        buffer.add(tokens_list.get(i));
                        i++;
                    }
                    if(i < tokens_list.size() - 1 && tokens_list.get(i + 1).getFirst().equals("ELSE_LK"))
                    {
                        buffer.add(tokens_list.get(i));
                        i++;
                    }
                    else
                    {
                        buffer.add(tokens_list.get(i));
                        break;
                    }
                }
            }
            expression = expr(buffer);
            try {
                if (expression != null)
                {
                    res.addChild(expression);
                    buffer.clear();
                }
            }
            catch (Exception e)
            {
                System.out.println(e.getMessage());
                System.exit(1);
            }
        }
        res.addChild(new LexHandler(tokens_list.get(tokens_list.size() - 1)));
        return res;
    }

    public static LexHandler do_while_expr(ArrayList<Pair<String, String>> tokens_list)
    {
        LexHandler res = new LexHandler(new Pair<>("do_while_expr", ""));
        Integer rightBracket = 0, rightCurlyBracket = 0;
        LexHandler expression;
        if(tokens_list.size() > 3 && tokens_list.get(0).getFirst().equals("DO_LK") && tokens_list.get(1).getFirst().equals("L_S_BR") &&
                (rightCurlyBracket = getPairBracket(tokens_list, 1, "L_S_BR", "R_S_BR")) != null &&
                tokens_list.get(rightCurlyBracket + 1).getFirst().equals("WHILE_LK") &&
                (rightBracket = getPairBracket(tokens_list, rightCurlyBracket + 2, "L_BR", "R_BR")) != null &&
                tokens_list.get(tokens_list.size() - 1).getFirst().equals("SEP"))
        {
            res.addChild(new LexHandler(tokens_list.get(0)));
            expression = while_body(new ArrayList<Pair<String, String>>(tokens_list.subList(1, rightCurlyBracket + 1)));
            if (expression == null) {
                return null;
            }
            res.addChild(expression);
            expression = while_head(new ArrayList<Pair<String, String>>(tokens_list.subList(rightCurlyBracket + 1, rightBracket + 1)));
            if (expression == null) {
                return null;
            }
            res.addChild(expression);
            res.addChild(new LexHandler(tokens_list.get(tokens_list.size() - 1)));
            return res;
        }
        return null;
    }

    public static LexHandler print_expr(ArrayList<Pair<String, String>> tokens_list){
        LexHandler res = new LexHandler(new Pair<>("print_expr", ""));
        if(tokens_list.size() == 3 && tokens_list.get(0).getFirst().equals("PRINT_LK") && tokens_list.get(2).getFirst().equals("SEP")){
            LexHandler val = value(tokens_list.get(1));
            if(val != null){
                res.addChild(new LexHandler(tokens_list.get(0)));
                res.addChild(val);
                res.addChild(new LexHandler(tokens_list.get(2)));
                return res;
            }
        }

        return null;
    }

    public static LexHandler condition(ArrayList<Pair<String, String>> tokens_list)
    {
        LexHandler res = new LexHandler(new Pair<>("condition", ""));
        LexHandler expression;
        if(tokens_list.get(0).getFirst().equals("L_BR") && tokens_list.get(tokens_list.size() - 1).getFirst().equals("R_BR"))
        {
            expression = logical_expression(new ArrayList<>(tokens_list.subList(1, tokens_list.size() - 1)));
            if(expression != null)
            {
                res.addChild(new LexHandler(tokens_list.get(0)));
                res.addChild(expression);
                res.addChild(new LexHandler(tokens_list.get(tokens_list.size() - 1)));
                return res;
            }
        }
        return null;
    }

    public static LexHandler logical_expression(ArrayList<Pair<String, String>> tokens_list)
    {
        LexHandler res = new LexHandler(new Pair<>("logical_expression", ""));
        LexHandler expression;
        expression = value(tokens_list.get(0));
        if(expression != null)
        {
            res.addChild(expression);
            Integer i = 1;
            while(i < tokens_list.size() && tokens_list.get(i).getFirst().equals("LOGICAL_OP"))
            {
                expression = value(tokens_list.get(i + 1));
                if(expression != null)
                {
                    res.addChild(new LexHandler(tokens_list.get(i)));
                    res.addChild(expression);
                }
                else
                    return null;
                i += 2;
            }
            return res;
        }
        return null;
    }

    public static Integer getPairBracket(ArrayList<Pair<String, String>> tokens_list, Integer left_bracket, String leftBracket, String rightBracket)
    {
        for (Integer i = left_bracket + 1; i < tokens_list.size(); i++)
        {
            if(tokens_list.get(i).getFirst().equals(rightBracket))
                return i;
            if(tokens_list.get(i).getFirst().equals(leftBracket))
                i = getPairBracket(tokens_list, i, leftBracket, rightBracket);
        }
        return null;
    }

    public static LexHandler value(Pair<String, String> token)
    {
        if(token.getFirst().equals("NUMBER") || token.getFirst().equals("VAR"))
            return new LexHandler(token);
        return null;
    }
}

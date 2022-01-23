import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        // Чтение файла с исходным кодом
        StringBuilder sb = new StringBuilder();
        try(FileReader reader = new FileReader("program.txt"))
        {
            int c;
            while((c = reader.read()) != -1) {
                sb.append((char) c);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String source = sb.toString();
        source = source.replaceAll("\\s+"," ");
        System.out.println("\033[1;31m \n\n\nLexer:\n\033[0m");
        ArrayList<Pair<String, String>> tokens = Lexer.getTokensList(source);
        System.out.println("\033[1;31m \n\n\nToken list:\n\033[0m");
        for (var token: tokens) {
            System.out.print(token.getFirst() + "|" + token.getSecond() + "\n");
        }
        System.out.println("\033[1;31m \n\n\nParser:\n\033[0m");
        LexTree tree = Parser.getSyntaxTree(tokens);
        if(tree == null)
        {
            System.out.println("\033[1;31m \n\n\nERROR - syntax\n\033[0m");
            System.exit(1);
        }
        System.out.println("\033[1;31m \n\n\nTree:\n\033[0m");
        tree.showTree();
        System.out.println("\033[1;31m \n\n\nTree Optimization:\n\033[0m");
        tree = TreeOptimizer.optimizeTree(tree);
        System.out.println("\033[1;31m \n\n\nOptimized tree:\n\033[0m");
        tree.showTree();
        System.out.println("\033[1;31m \n\n\nConverting to RPN:\n\033[0m");
         ArrayList<Pair<String, String>> rpn = RPN.getRPN(tree);
//        ArrayList<Pair<String, String>> rpn = RPN.getRPN(tokens);
        System.out.println("\033[1;31m \n\n\nRPN:\n\033[0m");
        System.out.println(rpn.toString());
        System.out.println(rpn.size());
        System.out.println("\033[1;31m \n\n\nRPN optimization using triads:\n\033[0m");
        rpn = TriadOptimizer.optimizeTriads(rpn);
        System.out.println("\033[1;31m \n\n\nOptimized RPN:\n\033[0m");
        System.out.println(rpn.toString());
        System.out.println(rpn.size());
        System.out.println("\033[1;31m \n\n\nStack Machine:\n\033[0m");
        SM.execute(rpn);

    }
}

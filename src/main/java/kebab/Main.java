package kebab;

import kebab.lang.EvalVisitor;
import kebab.lang.func.Func;
import kebab.lang.Scope;
import kebab.lang.func.SymbolVisitor;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String... args) {
        try {
            KebabLexer lexer = new KebabLexer(new ANTLRFileStream("src/main/resources/test2.kebab"));
            KebabParser parser = new KebabParser(new CommonTokenStream(lexer));
            parser.setBuildParseTree(true);
            ParseTree tree = parser.parse();

            Scope scope = new Scope();
            Map<String, Func> functions = new HashMap<>();
            SymbolVisitor symbolVisitor = new SymbolVisitor(functions);
            symbolVisitor.visit(tree);
            EvalVisitor visitor = new EvalVisitor(scope, functions);
            visitor.visit(tree);
        } catch (Exception e) {
            if (e.getMessage() != null) {
                System.err.println(e.getMessage());
            } else {
                e.printStackTrace();
            }
        }
    }
}
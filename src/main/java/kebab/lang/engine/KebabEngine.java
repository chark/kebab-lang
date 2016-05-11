package kebab.lang.engine;

import kebab.KebabLexer;
import kebab.KebabParser;
import kebab.lang.EvaluationVisitor;
import kebab.lang.Scope;
import kebab.lang.func.Func;
import kebab.lang.func.SymbolVisitor;
import kebab.lang.value.KebabValue;
import kebab.util.KebabException;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KebabEngine {

    private final EvaluationVisitor evaluationVisitor;
    private final SymbolVisitor symbolVisitor;
    private final KebabParser parser;
    private final KebabLexer lexer;
    private final ParseTree tree;
    private final Scope scope;

    /**
     * Main kebab engine constructor from char stream.
     *
     * @param charStream input stream of the code.
     */
    private KebabEngine(CharStream charStream, Object... args) {
        this.lexer = new KebabLexer(charStream);
        this.parser = new KebabParser(new CommonTokenStream(lexer));
        this.parser.setBuildParseTree(true);

        this.tree = parser.parse();
        this.scope = new Scope();
        this.symbolVisitor = new SymbolVisitor();
        this.symbolVisitor.visit(tree);
        this.evaluationVisitor = new EvaluationVisitor(scope, symbolVisitor.getFunctions());

        // Main function is needed.
        this.initMainFunc(args);
    }

    /**
     * Rune the kebab engine!
     *
     * @return value from the script.
     */
    public Object run() {
        KebabValue value = this.evaluationVisitor
                .visit(tree);

        // todo main func
        /*
        if (value == null) {
            throw new KebabException("Main function must return a value");
        }
        return value
                .get();
                */
        return null;
    }

    /**
     * Initialize the kebab engine by providing a snippet of code.
     *
     * @param code code snippet.
     */
    public static KebabEngine code(String code) throws Exception {
        return new KebabEngine(new ANTLRInputStream(code));
    }

    /**
     * Initialize the kebab engine by providing a file location.
     *
     * @param file file location.
     */
    public static KebabEngine file(String file, Object... args) throws Exception {
        return new KebabEngine(new ANTLRFileStream(file), args);
    }

    /**
     * Initialize main function with passed arguments.
     *
     * @param args arguments to be sent to main function.
     */
    private void initMainFunc(Object... args) {
        List<Object> argList = new ArrayList<>();

        // Collect passed arguments.
        if (args != null) {
            Collections.addAll(argList, args);
        }

        Func func = symbolVisitor.getFunctions().get(Func.MAIN_FUNC);
        if (func == null) {
            throw new KebabException("No main function found, please define a '_func main(args) {}' function");
        }
        func.setArgs(argList);
    }
}
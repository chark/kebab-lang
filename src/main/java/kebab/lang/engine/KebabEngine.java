package kebab.lang.engine;

import kebab.KebabLexer;
import kebab.KebabParser;
import kebab.lang.MainKebabVisitor;
import kebab.lang.Block;
import kebab.lang.func.KebabFunctionVisitor;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class KebabEngine {

    private final MainKebabVisitor evaluationVisitor;
    private final KebabFunctionVisitor symbolVisitor;
    private final KebabParser parser;
    private final KebabLexer lexer;
    private final ParseTree tree;
    private final Block scope;

    /**
     * Main kebab engine constructor from char stream.
     *
     * @param charStream input stream of the code.
     */
    private KebabEngine(CharStream charStream) {
        this.lexer = new KebabLexer(charStream);
        this.parser = new KebabParser(new CommonTokenStream(lexer));
        this.parser.setBuildParseTree(true);

        this.tree = parser.parse();
        this.scope = new Block();
        this.symbolVisitor = new KebabFunctionVisitor();
        this.symbolVisitor.visit(tree);
        this.evaluationVisitor = new MainKebabVisitor(scope, symbolVisitor.getFunctions());
    }

    /**
     * Rune the kebab engine!
     *
     * @return value from the script.
     */
    public Object run() {
        return this.evaluationVisitor
                .visit(tree);
    }

    /**
     * Initialize the kebab engine by providing a file location.
     *
     * @param file file location.
     */
    public static KebabEngine file(String file) throws Exception {
        return new KebabEngine(new ANTLRFileStream(file));
    }
}
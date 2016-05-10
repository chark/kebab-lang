package kebab.lang;

import kebab.KebabBaseVisitor;
import kebab.KebabParser;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SymbolVisitor extends KebabBaseVisitor<KebabValue> {

    private final Map<String, Func> functions;

    public SymbolVisitor(Map<String, Func> functions) {
        this.functions = functions;
    }

    @Override
    public KebabValue visitFunctionDecl(KebabParser.FunctionDeclContext context) {

        // Get func parameters and func name.
        List<TerminalNode> params = context.idList() != null ?
                context.idList().Identifier() :
                new ArrayList<TerminalNode>();

        // Func code block.
        ParseTree block = context.block();

        // Construct func identifier funcName + params.
        String identifier = context.Identifier().getText() + params.size();
        this.functions.put(identifier, new Func(identifier, params, block));
        return KebabValue.VOID;
    }
}
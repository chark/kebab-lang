package kebab.lang;

import kebab.KebabParser;
import kebab.util.KebabException;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;
import java.util.Map;

public class Func {

    private List<TerminalNode> parameters;
    private String identifier;
    private ParseTree block;

    public Func(String identifier, List<TerminalNode> parameters, ParseTree block) {
        this.identifier = identifier;
        this.parameters = parameters;
        this.block = block;
    }

    /**
     * Invoke a function.
     *
     * @param params    function parameters.
     * @param functions functions that this function refers to.
     * @param scope     scope of the function.
     * @return kebab value.
     */
    public KebabValue invoke(List<KebabParser.ExpressionContext> params,
                             Map<String, Func> functions,
                             Scope scope) {

        if (params.size() != this.parameters.size()) {
            throw new KebabException("Illegal func call of func: %s", identifier);
        }

        // Scope of the function.
        scope = new Scope(scope);

        EvalVisitor evalVisitor = new EvalVisitor(scope, functions);
        for (int i = 0; i < this.parameters.size(); i++) {
            KebabValue value = evalVisitor.visit(params.get(i));
            scope.assignParam(this.parameters.get(i).getText(), value);
        }

        KebabValue value = KebabValue.VOID;
        try {
            evalVisitor.visit(this.block);
        } catch (ReturnValue returnValue) {
            value = returnValue.value;
        }
        return value;
    }
}
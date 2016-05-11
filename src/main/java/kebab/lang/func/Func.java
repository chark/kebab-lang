package kebab.lang.func;

import kebab.KebabParser;
import kebab.lang.EvalVisitor;
import kebab.lang.KebabValue;
import kebab.lang.ReturnValue;
import kebab.lang.Scope;
import kebab.util.KebabException;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.List;
import java.util.Map;

public class Func {

    private final List<FuncParameter> parameters;
    private final String identifier;
    private final ParseTree block;

    private final int realParameterCount;

    public Func(List<FuncParameter> parameters,
                String identifier,
                ParseTree block,
                int realParameterCount) {

        this.parameters = parameters;
        this.identifier = identifier;
        this.block = block;
        this.realParameterCount = realParameterCount;
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

        if (params.size() > params.size()) {
            throw new KebabException("Invalid parameter count of on function: %s", identifier);
        }

        // Scope of the function.
        scope = new Scope(scope);

        EvalVisitor evalVisitor = new EvalVisitor(scope, functions);

        for (int i = 0; i < this.parameters.size(); i++) {

            FuncParameter virtual = this.parameters.get(i);

            KebabValue value;
            if (i < params.size()) {

                // Assign real parameters.
                value = evalVisitor.visit(params.get(i));
            } else {

                // Assign optional parameters.
                value = evalVisitor.visit(virtual.getContext());
            }
            scope.assignParam(virtual.getIdentifier(), value);
        }

        KebabValue value = KebabValue.VOID;
        try {
            evalVisitor.visit(this.block);
        } catch (ReturnValue returnValue) {
            value = returnValue.value;
        }
        return value;
    }

    /**
     * Get non-optional parameter count for this function.
     *
     * @return non-optional parameter count.
     */
    public int getRealParameterCount() {
        return realParameterCount;
    }

    /**
     * Check if function has only optional params.
     *
     * @return true if function has only optional params.
     */
    public boolean isPureleyOptional() {
        return realParameterCount == 0;
    }
}
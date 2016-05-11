package kebab.lang.func;

import kebab.KebabParser;
import kebab.lang.EvaluationVisitor;
import kebab.lang.value.KebabValue;
import kebab.lang.value.ReturnValue;
import kebab.lang.Scope;
import kebab.util.KebabException;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.List;
import java.util.Map;

public class Func {

    public static final String MAIN_FUNC = "main";

    private final List<FuncParameter> parameters;
    private final String identifier;
    private final ParseTree block;

    private final int realParameterCount;
    private List<Object> args;

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

        EvaluationVisitor evaluationVisitor = new EvaluationVisitor(scope, functions);

        for (int i = 0; i < this.parameters.size(); i++) {

            FuncParameter virtual = this.parameters.get(i);

            KebabValue value;
            if (i < params.size()) {

                // Assign real parameters.
                value = evaluationVisitor.visit(params.get(i));
            } else {

                // Assign optional parameters.
                value = evaluationVisitor.visit(virtual.getContext());
            }
            scope.assignParam(virtual.getIdentifier(), value);
        }

        KebabValue value = KebabValue.VOID;
        try {
            evaluationVisitor.visit(this.block);
        } catch (ReturnValue returnValue) {
            value = returnValue.value;
        }
        return value;
    }

    /**
     * Check if function has only optional params.
     *
     * @return true if function has only optional params.
     */
    public boolean isPurelyOptional() {
        return realParameterCount == 0;
    }

    /**
     * Check if this function is the main function.
     *
     * @return true if this function is the main function or false otherwise.
     */
    public boolean isMainFunction() {
        return MAIN_FUNC.equals(identifier);
    }

    /**
     * Set arguments.
     *
     * @param args function arguments.
     */
    public void setArgs(List<Object> args) {
        this.args = args;
    }

    /**
     * Get passed arguments to this function, note this only applies to the main func.
     *
     * @return function arguments.
     */
    public List<Object> getArgs() {
        return args;
    }
}
package kebab.lang.func;

import kebab.KebabBaseVisitor;
import kebab.KebabParser;
import kebab.lang.value.KebabValue;
import kebab.util.KebabException;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.*;

public class SymbolVisitor extends KebabBaseVisitor<KebabValue> {

    private final Map<String, Func> functions;

    public SymbolVisitor() {
        this.functions = new HashMap<>();
    }

    @Override
    public KebabValue visitFunctionDeclaration(KebabParser.FunctionDeclarationContext context) {

        // Get func parameters and func name.
        List<FuncParameter> params = new ArrayList<>();

        boolean optionalsStarted = false;

        int realParameterCount = 0;

        // Parse function arguments.
        if (context.argumentList() != null) {
            for (KebabParser.ArgumentContext argumentContext : context.argumentList().argument()) {

                KebabParser.ExpressionContext expression = argumentContext.expression();
                String id = argumentContext.Identifier().getText();

                if (expression != null) {
                    optionalsStarted = true;
                } else if (optionalsStarted) {
                    throw new KebabException(argumentContext.start,
                            "Optional arguments must be at the end of function args");
                }

                // Parameters must not duplicate (by id).
                for (FuncParameter parameter : params) {
                    if (parameter.getIdentifier().equals(id)) {
                        throw new KebabException(argumentContext.start,
                                "Function declaration must not contain duplicate ids");
                    }
                }

                FuncParameter parameter = new FuncParameter(id, expression);

                // Count non-optional parameters for func id.
                if (!parameter.isOptional()) {
                    realParameterCount++;
                }
                params.add(new FuncParameter(id, expression));
            }
        }

        // Function block.
        ParseTree block = context.block();

        // Function name identified.
        String identifier = context
                .Identifier()
                .getText();

        if (Func.MAIN_FUNC.equals(identifier)) {

            if (realParameterCount != 1) {
                throw new KebabException(context.start, "Main function must have only one param");
            }

        } else if (realParameterCount > 0) {

            // Only non-optional parameters count!
            identifier += realParameterCount;
        }

        // Add a new function to our list.
        this.functions.put(identifier, new Func(params,
                identifier,
                block,
                realParameterCount));

        return KebabValue.VOID;
    }

    /**
     * Get functions of symbol visitor.
     *
     * @return visible functions.
     */
    public Map<String, Func> getFunctions() {
        return functions;
    }
}
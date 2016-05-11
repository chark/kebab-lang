package kebab.lang.func;

import kebab.KebabParser;

/**
 * Holder class for function parameter.
 */
public class FuncParameter {

    private final KebabParser.ExpressionContext context;
    private final String identifier;
    private final boolean optional;

    public FuncParameter(String identifier,
                         KebabParser.ExpressionContext context) {

        this.identifier = identifier;
        this.context = context;
        this.optional = context != null;
    }

    public KebabParser.ExpressionContext getContext() {
        return context;
    }

    public String getIdentifier() {
        return identifier;
    }

    public boolean isOptional() {
        return optional;
    }
}
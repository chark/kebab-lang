package kebab.util;

import org.antlr.v4.runtime.ParserRuleContext;

/**
 * Generic exception for the Kebab language.
 */
public class KebabException extends RuntimeException {

    public KebabException(String message, Object... args) {
        super(String.format(message, args));
    }

    public KebabException(ParserRuleContext context) {
        this(String.format("Illegal expression: %s", context.getText()), context);
    }

    public KebabException(String message, ParserRuleContext context) {
        super(String.format("%s line: %s", message, context.start.getLine()));
    }
}
package kebab.util;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

/**
 * Generic exception for the Kebab language.
 */
public class KebabException extends RuntimeException {

    /**
     * Create a descriptive exception with line details where the exception occurred.
     *
     * @param token   token start which caused the exception.
     * @param message exception message.
     * @param args    message args.
     */
    public KebabException(Token token, String message, Object... args) {
        this("Error("
                + token.getLine()
                + ", "
                + token.getCharPositionInLine()
                + "): "
                + String.format(message, args));
    }

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
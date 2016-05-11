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

    /**
     * A generic exception with just a message.
     */
    public KebabException(String message, Object... args) {
        super(String.format(message, args));
    }

    /**
     * A generic exception with an attached context.
     *
     * @param context where the exception was thrown.
     */
    public KebabException(ParserRuleContext context) {
        this(context.start, String.format("Illegal expression: %s", context.getText()));
    }
}
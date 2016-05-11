package kebab.util;

public class Assert {

    public static final String NULL_MESSAGE = "Provided object value cannot be null";

    public static void notNull(Object object) {
        notNull(object, NULL_MESSAGE);
    }

    public static void notNull(Object object, String message, Object... args) {
        if (object == null) {
            throw new KebabException(message, args);
        }
    }
}
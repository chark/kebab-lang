package kebab.util;

public class Variables {

    public static final String BOOLEAN_TRUE = "yes";
    public static final String BOOLEAN_FALSE = "no";

    /**
     * Convert boolean value to string.
     *
     * @param value boolean object.
     * @return boolean object representation as string.
     */
    public static String booleanToString(Object value) {
        boolean actual = Boolean.valueOf(value.toString());

        if (actual) {
            return BOOLEAN_TRUE;
        } else {
            return BOOLEAN_FALSE;
        }
    }
}
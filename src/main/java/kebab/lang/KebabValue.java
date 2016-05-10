package kebab.lang;

import kebab.util.Assert;
import kebab.util.KebabException;

import java.util.List;

public class KebabValue implements Comparable<KebabValue> {

    public static final KebabValue EMPTY = new KebabValue();
    public static final KebabValue VOID = new KebabValue();

    private final Object value;

    private KebabValue() {
        this.value = new Object();
    }

    public KebabValue(Object value) {
        Assert.notNull(value);
        this.value = value;
        this.validate();
    }

    @Override
    public int compareTo(KebabValue that) {
        if (this.isNumber() && that.isNumber()) {
            if (this.equals(that)) {
                return 0;
            } else {
                return this.asDouble().compareTo(that.asDouble());
            }
        } else if (this.isString() && that.isString()) {
            return this.asString().compareTo(that.asString());
        } else {
            throw new KebabException("Cannot compare: '%s' to: '%s'", this, that);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (this == VOID || other == VOID) {
            throw new KebabException("Cannot use VOID: %s ==/!= %s", this, other);
        }
        if (this == other) {
            return true;
        }
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        KebabValue that = (KebabValue) other;
        if (this.isNumber() && that.isNumber()) {
            double diff = Math.abs(this.asDouble() - that.asDouble());
            return diff < 0.00000000001;
        } else {
            return this.value.equals(that.value);
        }
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    public Boolean asBoolean() {
        return (Boolean) value;
    }

    public Double asDouble() {
        return ((Number) value).doubleValue();
    }

    public Long asLong() {
        return ((Number) value).longValue();
    }

    @SuppressWarnings("unchecked")
    public List<KebabValue> asList() {
        return (List<KebabValue>) value;
    }

    public String asString() {
        return (String) value;
    }

    public boolean isBoolean() {
        return value instanceof Boolean;
    }

    public boolean isNumber() {
        return value instanceof Number;
    }

    public boolean isList() {
        return value instanceof List<?>;
    }

    public boolean isEmpty() {
        return this == EMPTY;
    }

    public boolean isVoid() {
        return this == VOID;
    }

    public boolean isString() {
        return value instanceof String;
    }

    /**
     * Throw an exception if invalid value is assigned.
     */
    private void validate() {
        if (!(isBoolean() || isList() || isNumber() || isString())) {
            throw new KebabException("Got invalid type: %s", value.getClass());
        }
    }

    @Override
    public String toString() {
        return isEmpty() ? "EMPTY" : isVoid() ? "VOID" : String.valueOf(value);
    }
}
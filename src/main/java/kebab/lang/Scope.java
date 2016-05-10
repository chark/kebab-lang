package kebab.lang;

import kebab.util.KebabException;
import org.antlr.v4.runtime.Token;

import java.util.HashMap;
import java.util.Map;

public class Scope {

    private final Map<String, KebabValue> variables;
    private final Scope parent;

    /**
     * Create a global scope.
     */
    public Scope() {

        // Global scope, parent is null.
        this(null);
    }

    /**
     * Create a scope with a parent.
     *
     * @param parent parent of the scope.
     */
    public Scope(Scope parent) {
        this.parent = parent;
        this.variables = new HashMap<>();
    }

    /**
     * Copy constructor.
     */
    private Scope(Map<String, KebabValue> variables, Scope parent) {
        this.variables = variables;
        this.parent = parent;
    }

    public void assignParam(String var, KebabValue value) {
        this.variables.put(var, value);
    }

    /**
     * Assign a new variable.
     *
     * @param token    token start where the assignment happens.
     * @param variable variable identifier.
     * @param value    value of the variable.
     */
    public void assign(Token token, String variable, KebabValue value) {
        if (resolve(variable) != null) {

            // Do not re-assign a variable by default.
            throw new KebabException(token, "Variable '%s' already declared in this scope",
                    variable);

        } else {

            // A newly declared variable.
            variables.put(variable, value);
        }
    }

    /**
     * Create a shallow copy of this scope. Used in case functions are are recursively called. If we wouldn't create
     * a copy in such cases, changing the variables would result in changes ro the Maps from  other "recursive scopes".
     */
    public Scope copy() {
        return new Scope(this.variables, this.parent);
    }

    public boolean isGlobalScope() {
        return parent == null;
    }

    public Scope parent() {
        return parent;
    }

    /**
     * Re-assign a variable.
     *
     * @param token    token start where the assignment happens.
     * @param variable variable identifier.
     * @param value    new variable value.
     */
    public void reAssign(Token token, String variable, KebabValue value) {
        if (variables.containsKey(variable)) {

            // The variable is declared in this scope.
            variables.put(variable, value);
        } else if (parent != null) {

            // The variable was not declared in this scope.
            throw new KebabException(token, "Variable '%s' is not declared in this scope",
                    variable);
        }
    }

    public KebabValue resolve(String var) {

        KebabValue value = variables.get(var);
        if (value != null) {

            // The variable resides in this scope.
            return value;
        } else if (!isGlobalScope()) {

            // Let the parent scope look for the variable.
            return parent.resolve(var);
        } else {

            // Unknown variable.
            return null;
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, KebabValue> variable : variables.entrySet()) {
            builder.append(variable.getKey())
                    .append("->")
                    .append(variable.getValue())
                    .append(",");
        }
        return builder
                .toString();
    }
}
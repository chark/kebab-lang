package kebab.lang;

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

    public void assign(String var, KebabValue value) {
        if (resolve(var) != null) {

            // There is already such a variable, re-assign it.
            this.reAssign(var, value);
        } else {

            // A newly declared variable.
            variables.put(var, value);
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

    private void reAssign(String identifier, KebabValue value) {
        if (variables.containsKey(identifier)) {

            // The variable is declared in this scope.
            variables.put(identifier, value);
        } else if (parent != null) {

            // The variable was not declared in this scope, so let
            // the parent scope re-assign it.
            parent.reAssign(identifier, value);
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
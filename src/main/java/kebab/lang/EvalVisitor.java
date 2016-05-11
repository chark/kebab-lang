package kebab.lang;

import kebab.KebabBaseVisitor;
import kebab.KebabParser;
import kebab.lang.func.Func;
import kebab.util.KebabException;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EvalVisitor extends KebabBaseVisitor<KebabValue> {

    private static final String BOOL_TRUE = "yes";

    private static ReturnValue returnValue = new ReturnValue();
    private Scope scope;
    private Map<String, Func> functions;

    public EvalVisitor(Scope scope, Map<String, Func> functions) {
        this.scope = scope;
        this.functions = functions;
    }

    /**
     * Function declaration.
     * functionDeclaration
     * : Func Identifier '(' identifierList? ')' block Close
     * ;
     */
    @Override
    public KebabValue visitFunctionDeclaration(KebabParser.FunctionDeclarationContext context) {
        return KebabValue.VOID;
    }

    // list: '[' exprList? ']'
    @Override
    public KebabValue visitList(KebabParser.ListContext context) {
        List<KebabValue> list = new ArrayList<>();
        if (context.expressionList() != null) {
            for (KebabParser.ExpressionContext ex : context.expressionList().expression()) {
                list.add(this.visit(ex));
            }
        }
        return new KebabValue(list);
    }


    // '-' expression                           #unaryMinusExpression
    @Override
    public KebabValue visitUnaryMinusExpression(KebabParser.UnaryMinusExpressionContext ctx) {
        KebabValue v = this.visit(ctx.expression());
        if (!v.isNumber()) {
            throw new KebabException(ctx);
        }
        return new KebabValue(-1 * v.asDouble());
    }

    /**
     * Visit no expression.
     * <pre>
     * | '!' expression
     * </pre>
     */
    @Override
    public KebabValue visitNotExpression(KebabParser.NotExpressionContext ctx) {
        KebabValue value = this.visit(ctx.expression());

        // Not expressions only allowed for booleans and nulls.
        if (!value.isBoolean() && !value.isEmpty()) {
            throw new KebabException(ctx);
        }
        return new KebabValue(!value.asBoolean());
    }

    // expression '^' expression                #powerExpression
    @Override
    public KebabValue visitPowerExpression(KebabParser.PowerExpressionContext ctx) {
        KebabValue lhs = this.visit(ctx.expression(0));
        KebabValue rhs = this.visit(ctx.expression(1));
        if (lhs.isNumber() && rhs.isNumber()) {
            return new KebabValue(Math.pow(lhs.asDouble(), rhs.asDouble()));
        }
        throw new KebabException(ctx);
    }

    // expression '*' expression                #multiplyExpression
    @Override
    public KebabValue visitMultiplyExpression(KebabParser.MultiplyExpressionContext ctx) {
        KebabValue lhs = this.visit(ctx.expression(0));
        KebabValue rhs = this.visit(ctx.expression(1));
        if (lhs == null || rhs == null) {
            System.err.println("lhs " + lhs + " rhs " + rhs);
            throw new KebabException(ctx);
        }

        // number * number
        if (lhs.isNumber() && rhs.isNumber()) {
            return new KebabValue(lhs.asDouble() * rhs.asDouble());
        }

        // string * number
        if (lhs.isString() && rhs.isNumber()) {
            StringBuilder str = new StringBuilder();
            int stop = rhs.asDouble().intValue();
            for (int i = 0; i < stop; i++) {
                str.append(lhs.asString());
            }
            return new KebabValue(str.toString());
        }

        // list * number
        if (lhs.isList() && rhs.isNumber()) {
            List<KebabValue> total = new ArrayList<>();
            int stop = rhs.asDouble().intValue();
            for (int i = 0; i < stop; i++) {
                total.addAll(lhs.asList());
            }
            return new KebabValue(total);
        }
        throw new KebabException(ctx);
    }

    // expression '/' expression                #divideExpression
    @Override
    public KebabValue visitDivideExpression(KebabParser.DivideExpressionContext ctx) {
        KebabValue lhs = this.visit(ctx.expression(0));
        KebabValue rhs = this.visit(ctx.expression(1));
        if (lhs.isNumber() && rhs.isNumber()) {
            return new KebabValue(lhs.asDouble() / rhs.asDouble());
        }
        throw new KebabException(ctx);
    }

    // expression '%' expression                #modulusExpression
    @Override
    public KebabValue visitModulusExpression(KebabParser.ModulusExpressionContext ctx) {
        KebabValue lhs = this.visit(ctx.expression(0));
        KebabValue rhs = this.visit(ctx.expression(1));
        if (lhs.isNumber() && rhs.isNumber()) {
            return new KebabValue(lhs.asDouble() % rhs.asDouble());
        }
        throw new KebabException(ctx);
    }

    // expression '+' expression                #addExpression
    @Override
    public KebabValue visitAddExpression(@NotNull KebabParser.AddExpressionContext ctx) {
        KebabValue lhs = this.visit(ctx.expression(0));
        KebabValue rhs = this.visit(ctx.expression(1));

        if (lhs == null || rhs == null) {
            throw new KebabException(ctx);
        }

        // number + number
        if (lhs.isNumber() && rhs.isNumber()) {
            return new KebabValue(lhs.asDouble() + rhs.asDouble());
        }

        // list + any
        if (lhs.isList()) {
            List<KebabValue> list = lhs.asList();
            list.add(rhs);
            return new KebabValue(list);
        }

        // string + any
        if (lhs.isString()) {
            return new KebabValue(lhs.asString() + "" + rhs.toString());
        }

        // any + string
        if (rhs.isString()) {
            return new KebabValue(lhs.toString() + "" + rhs.asString());
        }

        return new KebabValue(lhs.toString() + rhs.toString());
    }

    // expression '-' expression                #subtractExpression
    @Override
    public KebabValue visitSubtractExpression(KebabParser.SubtractExpressionContext ctx) {
        KebabValue lhs = this.visit(ctx.expression(0));
        KebabValue rhs = this.visit(ctx.expression(1));
        if (lhs.isNumber() && rhs.isNumber()) {
            return new KebabValue(lhs.asDouble() - rhs.asDouble());
        }
        if (lhs.isList()) {
            List<KebabValue> list = lhs.asList();
            list.remove(rhs);
            return new KebabValue(list);
        }
        throw new KebabException(ctx);
    }

    // expression '>=' expression               #gtEqExpression
    @Override
    public KebabValue visitGtEqExpression(KebabParser.GtEqExpressionContext ctx) {
        KebabValue lhs = this.visit(ctx.expression(0));
        KebabValue rhs = this.visit(ctx.expression(1));
        if (lhs.isNumber() && rhs.isNumber()) {
            return new KebabValue(lhs.asDouble() >= rhs.asDouble());
        }
        if (lhs.isString() && rhs.isString()) {
            return new KebabValue(lhs.asString().compareTo(rhs.asString()) >= 0);
        }
        throw new KebabException(ctx);
    }

    // expression '<=' expression               #ltEqExpression
    @Override
    public KebabValue visitLtEqExpression(KebabParser.LtEqExpressionContext ctx) {
        KebabValue lhs = this.visit(ctx.expression(0));
        KebabValue rhs = this.visit(ctx.expression(1));
        if (lhs.isNumber() && rhs.isNumber()) {
            return new KebabValue(lhs.asDouble() <= rhs.asDouble());
        }
        if (lhs.isString() && rhs.isString()) {
            return new KebabValue(lhs.asString().compareTo(rhs.asString()) <= 0);
        }
        throw new KebabException(ctx);
    }

    // expression '>' expression                #gtExpression
    @Override
    public KebabValue visitGtExpression(KebabParser.GtExpressionContext ctx) {
        KebabValue lhs = this.visit(ctx.expression(0));
        KebabValue rhs = this.visit(ctx.expression(1));
        if (lhs.isNumber() && rhs.isNumber()) {
            return new KebabValue(lhs.asDouble() > rhs.asDouble());
        }
        if (lhs.isString() && rhs.isString()) {
            return new KebabValue(lhs.asString().compareTo(rhs.asString()) > 0);
        }
        throw new KebabException(ctx);
    }

    // expression '<' expression                #ltExpression
    @Override
    public KebabValue visitLtExpression(KebabParser.LtExpressionContext ctx) {
        KebabValue lhs = this.visit(ctx.expression(0));
        KebabValue rhs = this.visit(ctx.expression(1));
        if (lhs.isNumber() && rhs.isNumber()) {
            return new KebabValue(lhs.asDouble() < rhs.asDouble());
        }
        if (lhs.isString() && rhs.isString()) {
            return new KebabValue(lhs.asString().compareTo(rhs.asString()) < 0);
        }
        throw new KebabException(ctx);
    }

    // expression '==' expression               #eqExpression
    @Override
    public KebabValue visitEqExpression(@NotNull KebabParser.EqExpressionContext ctx) {
        KebabValue lhs = this.visit(ctx.expression(0));
        KebabValue rhs = this.visit(ctx.expression(1));
        if (lhs == null) {
            throw new KebabException(ctx);
        }
        return new KebabValue(lhs.equals(rhs));
    }

    // expression '!=' expression               #notEqExpression
    @Override
    public KebabValue visitNotEqExpression(@NotNull KebabParser.NotEqExpressionContext ctx) {
        KebabValue lhs = this.visit(ctx.expression(0));
        KebabValue rhs = this.visit(ctx.expression(1));
        return new KebabValue(!lhs.equals(rhs));
    }

    // expression '&&' expression               #andExpression
    @Override
    public KebabValue visitAndExpression(KebabParser.AndExpressionContext ctx) {
        KebabValue lhs = this.visit(ctx.expression(0));
        KebabValue rhs = this.visit(ctx.expression(1));

        if (!lhs.isBoolean() || !rhs.isBoolean()) {
            throw new KebabException(ctx);
        }
        return new KebabValue(lhs.asBoolean() && rhs.asBoolean());
    }

    // expression '||' expression               #orExpression
    @Override
    public KebabValue visitOrExpression(KebabParser.OrExpressionContext ctx) {
        KebabValue lhs = this.visit(ctx.expression(0));
        KebabValue rhs = this.visit(ctx.expression(1));

        if (!lhs.isBoolean() || !rhs.isBoolean()) {
            throw new KebabException(ctx);
        }
        return new KebabValue(lhs.asBoolean() || rhs.asBoolean());
    }

    // expression '?' expression ':' expression #ternaryExpression
    @Override
    public KebabValue visitTernaryExpression(KebabParser.TernaryExpressionContext ctx) {
        KebabValue condition = this.visit(ctx.expression(0));
        if (condition.asBoolean()) {
            return new KebabValue(this.visit(ctx.expression(1)));
        } else {
            return new KebabValue(this.visit(ctx.expression(2)));
        }
    }

    // expression In expression                 #inExpression
    @Override
    public KebabValue visitInExpression(KebabParser.InExpressionContext ctx) {
        KebabValue lhs = this.visit(ctx.expression(0));
        KebabValue rhs = this.visit(ctx.expression(1));

        if (rhs.isList()) {
            for (KebabValue val : rhs.asList()) {
                if (val.equals(lhs)) {
                    return new KebabValue(true);
                }
            }
            return new KebabValue(false);
        }
        throw new KebabException(ctx);
    }

    // Number                                   #numberExpression
    @Override
    public KebabValue visitNumberExpression(@NotNull KebabParser.NumberExpressionContext ctx) {
        return new KebabValue(Double.valueOf(ctx.getText()));
    }

    // Bool                                     #boolExpression
    @Override
    public KebabValue visitBoolExpression(@NotNull KebabParser.BoolExpressionContext ctx) {
        return new KebabValue(BOOL_TRUE.equals(ctx.getText()));
    }

    /**
     * Null, empty expression.
     * <pre>
     * | Empty
     * </pre>
     */
    @Override
    public KebabValue visitEmptyExpression(@NotNull KebabParser.EmptyExpressionContext ctx) {
        return KebabValue.EMPTY;
    }

    private KebabValue resolveIndexes(ParserRuleContext ctx, KebabValue val, List<KebabParser.ExpressionContext> indexes) {
        for (KebabParser.ExpressionContext ec : indexes) {
            KebabValue idx = this.visit(ec);
            if (!idx.isNumber() || (!val.isList() && !val.isString())) {
                throw new KebabException(ec.start, "Could not resolve indexes on: '%s' at: %s", val, idx);
            }
            int i = idx.asDouble().intValue();
            if (val.isString()) {
                val = new KebabValue(val.asString().substring(i, i + 1));
            } else {
                val = val.asList().get(i);
            }
        }
        return val;
    }

    private void setAtIndex(ParserRuleContext ctx, List<KebabParser.ExpressionContext> indexes, KebabValue val, KebabValue newVal) {
        if (!val.isList()) {
            throw new KebabException(ctx);
        }
        // TODO some more list size checking in here
        for (int i = 0; i < indexes.size() - 1; i++) {
            KebabValue idx = this.visit(indexes.get(i));
            if (!idx.isNumber()) {
                throw new KebabException(ctx);
            }
            val = val.asList().get(idx.asDouble().intValue());
        }
        KebabValue idx = this.visit(indexes.get(indexes.size() - 1));
        if (!idx.isNumber()) {
            throw new KebabException(ctx);
        }
        val.asList().set(idx.asDouble().intValue(), newVal);
    }

    /**
     * Call a function, either a custom or pre-defined.
     */
    @Override
    public KebabValue visitFunctionCallExpression(KebabParser.FunctionCallExpressionContext ctx) {
        KebabValue val = this.visit(ctx.functionCall());
        if (ctx.indexes() != null) {
            List<KebabParser.ExpressionContext> exps = ctx.indexes().expression();
            val = resolveIndexes(ctx, val, exps);
        }
        return val;
    }

    // list indexes?                            #listExpression
    @Override
    public KebabValue visitListExpression(KebabParser.ListExpressionContext ctx) {
        KebabValue val = this.visit(ctx.list());
        if (ctx.indexes() != null) {
            List<KebabParser.ExpressionContext> exps = ctx.indexes().expression();
            val = resolveIndexes(ctx, val, exps);
        }
        return val;
    }

    // Identifier indexes?                      #identifierExpression
    @Override
    public KebabValue visitIdentifierExpression(@NotNull KebabParser.IdentifierExpressionContext ctx) {
        String id = ctx.Identifier().getText();
        KebabValue val = scope.resolve(id);

        if (ctx.indexes() != null) {
            List<KebabParser.ExpressionContext> exps = ctx.indexes().expression();
            val = resolveIndexes(ctx, val, exps);
        }
        return val;
    }

    // String indexes?                          #stringExpression
    // todo fix replacing
    @Override
    public KebabValue visitStringExpression(@NotNull KebabParser.StringExpressionContext ctx) {
        String text = ctx.getText();
        text = text.substring(1, text.length() - 1).replaceAll("\\\\(.)", "$1");
        KebabValue val = new KebabValue(text);
        if (ctx.indexes() != null) {
            List<KebabParser.ExpressionContext> exps = ctx.indexes().expression();
            val = resolveIndexes(ctx, val, exps);
        }
        return val;
    }

    // '(' expression ')' indexes?              #expressionExpression
    @Override
    public KebabValue visitExpressionExpression(KebabParser.ExpressionExpressionContext ctx) {
        KebabValue val = this.visit(ctx.expression());
        if (ctx.indexes() != null) {
            List<KebabParser.ExpressionContext> exps = ctx.indexes().expression();
            val = resolveIndexes(ctx, val, exps);
        }
        return val;
    }

    // Input '(' String? ')'                    #inputExpression
    @Override
    public KebabValue visitInputExpression(KebabParser.InputExpressionContext ctx) {
        TerminalNode inputString = ctx.String();
        try {
            if (inputString != null) {
                String text = inputString.getText();
                text = text.substring(1, text.length() - 1).replaceAll("\\\\(.)", "$1");
                return new KebabValue(new String(Files.readAllBytes(Paths.get(text))));
            } else {
                BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
                return new KebabValue(buffer.readLine());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reassignment to variable.
     * <pre>
     * reAssignment
     * : Identifier indexes? ':' expression
     * ;
     * </pre>
     */
    @Override
    public KebabValue visitReAssignment(@NotNull KebabParser.ReAssignmentContext ctx) {
        scope.reAssign(ctx.start,
                ctx.Identifier().getText(),
                this.visit(ctx.expression()));

        return KebabValue.VOID;
    }

    /**
     * Assignment/creation of a variable.
     * <pre>
     * assignment
     * : 'keb' Identifier indexes? ':' expression
     * ;
     * </pre>
     */
    @Override
    public KebabValue visitAssignment(@NotNull KebabParser.AssignmentContext ctx) {

        // Variable created without assignment.
        if (ctx.expression() == null) {
            scope.assign(ctx.start, ctx.Identifier().getText(), KebabValue.EMPTY);
            return KebabValue.VOID;
        }

        KebabValue newVal = this.visit(ctx.expression());
        if (ctx.indexes() != null) {
            KebabValue val = scope.resolve(ctx.Identifier().getText());
            List<KebabParser.ExpressionContext> expression = ctx.indexes().expression();
            setAtIndex(ctx, expression, val, newVal);
        } else {
            String id = ctx.Identifier().getText();
            scope.assign(ctx.start, id, newVal);
        }
        return KebabValue.VOID;
    }

    /**
     * Function call with args or no args.
     * <pre>
     * : Identifier (('(' expressionList? ')') | '()') #identifierFunctionCall
     * </pre>
     */
    @Override
    public KebabValue visitIdentifierFunctionCall(KebabParser.IdentifierFunctionCallContext ctx) {
        List<KebabParser.ExpressionContext> params = ctx.expressionList() != null ? ctx.expressionList().expression() : new ArrayList<>();
        String id = ctx.Identifier().getText();

        Func function;
        if ((function = functions.get(id + params.size())) != null) {

            // Try to get a function by real parameter count.
            return function.invoke(params, functions, scope);
        } else if ((function = functions.get(id)) != null && function.isPureleyOptional()) {

            // Try to get a purely optional function.
            return function.invoke(params, functions, scope);
        }
        throw new KebabException(ctx);
    }

    /**
     * Printing of variables.
     * <pre>
     * | Show '(' expression ')'
     * </pre>
     */
    @Override
    public KebabValue visitShowFunctionCall(KebabParser.ShowFunctionCallContext ctx) {
        System.out.print(this.visit(ctx.expression()));
        return KebabValue.VOID;
    }

    /**
     * Printing of stuff, multiline.
     * <pre>
     * | ShowL (('(' expression ')') | '()')
     * </pre>
     */
    @Override
    public KebabValue visitShowLineFunctionCall(KebabParser.ShowLineFunctionCallContext ctx) {
        if (ctx.expression() == null) {
            System.out.println();
        } else {
            System.out.println(this.visit(ctx.expression()));
        }
        return KebabValue.VOID;
    }

    // Assert '(' expression ')'    #assertFunctionCall
    @Override
    public KebabValue visitAssertFunctionCall(KebabParser.AssertFunctionCallContext ctx) {
        KebabValue value = this.visit(ctx.expression());

        if (!value.isBoolean()) {
            throw new KebabException(ctx);
        }

        if (!value.asBoolean()) {
            throw new AssertionError("Failed Assertion " + ctx.expression().getText() + " line:" + ctx.start.getLine());
        }

        return KebabValue.VOID;
    }

    // Size '(' expression ')'      #sizeFunctionCall
    @Override
    public KebabValue visitSizeFunctionCall(KebabParser.SizeFunctionCallContext ctx) {
        KebabValue value = this.visit(ctx.expression());

        if (value.isString()) {
            return new KebabValue(value.asString().length());
        }

        if (value.isList()) {
            return new KebabValue(value.asList().size());
        }

        throw new KebabException(ctx);
    }

    /**
     * Complete if statement.
     * <pre>
     * completeIfStatement
     *  : ifStatement elseIfStatement* elseStatement? Close
     *  ;
     *
     * ifStatement
     *  : If '(' expression ')' Open block
     *  ;
     *
     * elseIfStatement
     *  : Close ElseIf '(' expression ')' Open block
     *  ;
     *
     * elseStatement
     *  : Close Else Open block
     *  ;
     * </pre>
     */
    @Override
    public KebabValue visitCompleteIfStatement(@NotNull KebabParser.CompleteIfStatementContext ctx) {

        // _if(...)
        if (this.visit(ctx.ifStatement().expression()).asBoolean()) {
            return this.visit(ctx.ifStatement().block());
        }

        // _elif(...)
        for (int i = 0; i < ctx.elseIfStatement().size(); i++) {
            if (this.visit(ctx.elseIfStatement(i).expression()).asBoolean()) {
                return this.visit(ctx.elseIfStatement(i).block());
            }
        }

        // _el(...)
        if (ctx.elseStatement() != null) {
            return this.visit(ctx.elseStatement().block());
        }

        return KebabValue.VOID;
    }

    /**
     * Code block - scope.
     * <pre>
     * block
     *  : (statement | functionDeclaration)* (Return expression)?
     *  ;
     * </pre>
     */
    @Override
    public KebabValue visitBlock(KebabParser.BlockContext ctx) {

        scope = new Scope(scope); // create new local scope
        ctx.statement().forEach(this::visit);
        KebabParser.ExpressionContext ex;
        if ((ex = ctx.expression()) != null) {
            returnValue.value = this.visit(ex);
            scope = scope.parent();
            throw returnValue;
        }
        scope = scope.parent();
        return KebabValue.VOID;
    }

    /**
     * A for-each loop for strings and lists.
     * <pre>
     * eachLoopStatement
     * : EachLoop '(' Identifier Colon expression ')' Open block Close
     * ;
     * </pre>
     */
    @Override
    public KebabValue visitEachLoopStatement(KebabParser.EachLoopStatementContext ctx) {

        KebabValue iterate = this.visit(ctx.expression());
        if (!iterate.isString() && !iterate.isList()) {
            throw new KebabException(ctx.start, "Cannot iterate a non-string or a non-list in a _each");
        }

        // Loop inner scope identifier.
        String id = ctx.Identifier().getText();

        // Make sure scope doesn't have a variable like this already.
        scope.assign(ctx.start, id, KebabValue.EMPTY);
        if (iterate.isString()) {

            // Iterate a list of string.
            for (char c : iterate.asString().toCharArray()) {
                scope.reAssign(ctx.start, id, new KebabValue(String.valueOf(c)));

                KebabValue returnValue = this.visit(ctx.block());
                if (returnValue != KebabValue.VOID) {
                    return returnValue;
                }
            }

        } else if (iterate.isList()) {

            // Iterate a list.
            for (KebabValue value : iterate.asList()) {
                scope.reAssign(ctx.start, id, value);

                KebabValue returnValue = this.visit(ctx.block());
                if (returnValue != KebabValue.VOID) {
                    return returnValue;
                }
            }
        }

        // Clear the local for loop variable.
        scope.remove(ctx.start, id);
        return KebabValue.VOID;
    }

    /**
     * A simple while loop.
     * loopStatement
     * : Loop '(' expression ')' Open block Close
     * ;
     */
    @Override
    public KebabValue visitLoopStatement(KebabParser.LoopStatementContext ctx) {

        // Gotta check initial while loop condition.
        KebabValue expression = this.visit(ctx.expression());
        while (expression.asBoolean()) {

            KebabValue returnValue = this.visit(ctx.block());
            if (returnValue != KebabValue.VOID) {
                return returnValue;
            }

            // Check loop condition all the time.
            expression = this.visit(ctx.expression());
        }
        return KebabValue.VOID;
    }
}
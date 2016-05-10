package kebab.lang;

import kebab.KebabBaseVisitor;
import kebab.KebabParser;
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

    // functionDecl
    @Override
    public KebabValue visitFunctionDecl(KebabParser.FunctionDeclContext context) {
        return KebabValue.VOID;
    }

    // list: '[' exprList? ']'
    @Override
    public KebabValue visitList(KebabParser.ListContext context) {
        List<KebabValue> list = new ArrayList<>();
        if (context.exprList() != null) {
            for (KebabParser.ExpressionContext ex : context.exprList().expression()) {
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

    // '!' expression                           #notExpression
    @Override
    public KebabValue visitNotExpression(KebabParser.NotExpressionContext ctx) {
        KebabValue v = this.visit(ctx.expression());
        if (!v.isBoolean()) {
            throw new KebabException(ctx);
        }
        return new KebabValue(!v.asBoolean());
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

    // Null                                     #nullExpression
    @Override
    public KebabValue visitNullExpression(@NotNull KebabParser.NullExpressionContext ctx) {
        return KebabValue.EMPTY;
    }

    private KebabValue resolveIndexes(ParserRuleContext ctx, KebabValue val, List<KebabParser.ExpressionContext> indexes) {
        for (KebabParser.ExpressionContext ec : indexes) {
            KebabValue idx = this.visit(ec);
            if (!idx.isNumber() || (!val.isList() && !val.isString())) {
                throw new KebabException("Problem resolving indexes on " + val + " at " + idx, ec);
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

    // functionCall indexes?                    #functionCallExpression
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

    // Identifier '(' exprList? ')' #identifierFunctionCall
    @Override
    public KebabValue visitIdentifierFunctionCall(KebabParser.IdentifierFunctionCallContext ctx) {
        List<KebabParser.ExpressionContext> params = ctx.exprList() != null ? ctx.exprList().expression() : new ArrayList<>();
        String id = ctx.Identifier().getText() + params.size();
        Func function;
        if ((function = functions.get(id)) != null) {
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
        System.out.println(this.visit(ctx.expression()).asString());
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

    // ifStatement
    //  : ifStat elseIfStat* elseStat? End
    //  ;
    //
    // ifStat
    //  : If expression Do block
    //  ;
    //
    // elseIfStat
    //  : Else If expression Do block
    //  ;
    //
    // elseStat
    //  : Else Do block
    //  ;
    @Override
    public KebabValue visitCompleteIfStatement(@NotNull KebabParser.CompleteIfStatementContext ctx) {

        // if ...
        if (this.visit(ctx.ifStatement().expression()).asBoolean()) {
            return this.visit(ctx.ifStatement().block());
        }

        // else if ...
        for (int i = 0; i < ctx.elseIfStatement().size(); i++) {
            if (this.visit(ctx.elseIfStatement(i).expression()).asBoolean()) {
                return this.visit(ctx.elseIfStatement(i).block());
            }
        }

        // else ...
        if (ctx.elseStatement() != null) {
            return this.visit(ctx.elseStatement().block());
        }

        return KebabValue.VOID;
    }

    // block
    // : (statement | functionDecl)* (Return expression)?
    // ;
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

    // forStatement
    // : For Identifier '=' expression To expression OBrace block CBrace
    // ;
    @Override
    public KebabValue visitForStatement(KebabParser.ForStatementContext ctx) {
        int start = this.visit(ctx.expression(0)).asDouble().intValue();
        int stop = this.visit(ctx.expression(1)).asDouble().intValue();
        for (int i = start; i <= stop; i++) {
            scope.assign(ctx.start, ctx.Identifier().getText(), new KebabValue(i));
            KebabValue returnValue = this.visit(ctx.block());
            if (returnValue != KebabValue.VOID) {
                return returnValue;
            }
        }
        return KebabValue.VOID;
    }

    // whileStatement
    // : While expression OBrace block CBrace
    // ;
    @Override
    public KebabValue visitWhileStatement(KebabParser.WhileStatementContext ctx) {
        while (this.visit(ctx.expression()).asBoolean()) {
            KebabValue returnValue = this.visit(ctx.block());
            if (returnValue != KebabValue.VOID) {
                return returnValue;
            }
        }
        return KebabValue.VOID;
    }
}
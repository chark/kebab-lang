grammar Kebab;

@header {
   package kebab;
}

parse
 : block EOF
 ;

// A block, can be a variable or a function.
block
 : (statement | functionDecl)* (Return expression ';')?
 ;

// A generic statement, for example assignment keb a: 1
statement
 : assignment
 | reAssignment
 | functionCall
 | completeIfStatement
 | forStatement
 | whileStatement
 ;

/*
    Assign a value to a variable.

    keb a: 1
*/
assignment
 : 'keb' Identifier indexes? ':' expression
 | 'keb' Identifier indexes?
 ;

/*
    Re-assign an existing variable value.

    keb a: 1
    a: yes
*/
reAssignment
 : Identifier indexes? ':' expression
 ;

functionCall
 : Identifier '(' exprList? ')' #identifierFunctionCall
 | Show '(' expression ')'      #showFunctionCall
 | Assert '(' expression ')'    #assertFunctionCall
 | Size '(' expression ')'      #sizeFunctionCall
 ;

/*
    A complete if statement.

    _if(...) {

    } _elif(...) {

    } _el {

    }
*/
completeIfStatement
 : ifStatement elseIfStatement* elseStatement? Close
 ;

// _if
ifStatement
 : If '(' expression ')' Open block
 ;

// } _elif {
elseIfStatement
 : Close ElseIf '(' expression ')' Open block
 ;

// } _el {
elseStatement
 : Close Else Open block
 ;

functionDecl
 : Def Identifier '(' idList? ')' block Close
 ;

forStatement
 : For Identifier '=' expression To expression Open block Close
 ;

whileStatement
 : While expression Open block Close
 ;

idList
 : Identifier (',' Identifier)*
 ;

exprList
 : expression (',' expression)*
 ;

expression
 : '-' expression                           #unaryMinusExpression
 | '!' expression                           #notExpression
 | expression '^' expression                #powerExpression
 | expression '*' expression                #multiplyExpression
 | expression '/' expression                #divideExpression
 | expression '%' expression                #modulusExpression
 | expression '+' expression                #addExpression
 | expression '-' expression                #subtractExpression
 | expression '>=' expression               #gtEqExpression
 | expression '<=' expression               #ltEqExpression
 | expression '>' expression                #gtExpression
 | expression '<' expression                #ltExpression
 | expression '==' expression               #eqExpression
 | expression '!=' expression               #notEqExpression
 | expression '&&' expression               #andExpression
 | expression '||' expression               #orExpression
 | expression '?' expression ':' expression #ternaryExpression
 | expression In expression                 #inExpression
 | Number                                   #numberExpression
 | Bool                                     #boolExpression
 | Empty                                    #emptyExpression
 | functionCall indexes?                    #functionCallExpression
 | list indexes?                            #listExpression
 | Identifier indexes?                      #identifierExpression
 | String indexes?                          #stringExpression
 | '(' expression ')' indexes?              #expressionExpression
 | Input '(' String? ')'                    #inputExpression
 ;

list
 : '[' exprList? ']'
 ;

indexes
 : ('[' expression ']')+
 ;

// Block tokens.
Open      : '{';
Close     : '}';

// If statements.
If       : '_if';
Else     : '_el';
ElseIf   : '_elif';

// Printing function (new-line).
Show     : 'show';

Input    : 'input';
Assert   : 'assert';
Size     : 'size';
Def      : 'def';
Return   : 'return';
For      : 'for';
While    : 'while';
To       : 'to';
In       : 'in';
Empty    : 'empty';

Or       : '||';
And      : '&&';
Equals   : '==';
NEquals  : '!=';
GTEquals : '>=';
LTEquals : '<=';
Pow      : '^';
Excl     : '!';
GT       : '>';
LT       : '<';
Add      : '+';
Subtract : '-';
Multiply : '*';
Divide   : '/';
Modulus  : '%';
OBracket : '[';
CBracket : ']';
OParen   : '(';
CParen   : ')';
SColon   : ';';
Assign   : '=';
Comma    : ',';
QMark    : '?';
Colon    : ':';

// Yes = true, no = false.
Bool
 : 'yes'
 | 'no'
 ;

Number
 : Int ('.' Digit*)?
 ;

Identifier
 : [a-zA-Z_] [a-zA-Z_0-9]*
 ;

String
 : ["] (~["\r\n] | '\\\\' | '\\"')* ["]
 | ['] (~['\r\n] | '\\\\' | '\\\'')* [']
 ;

Comment
 : ('@' ~[\r\n]*) -> skip
 ;

Space
 : [ \t\r\n\u000C] -> skip
 ;
fragment Int
 : [1-9] Digit*
 | '0'
 ;

fragment Digit
 : [0-9]
 ;
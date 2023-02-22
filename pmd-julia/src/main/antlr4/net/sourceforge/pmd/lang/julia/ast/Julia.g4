grammar Julia;

// Parser

main
    : functionBody (functionDefinition functionBody)* END? EOF
    ;

functionDefinition
    : functionDefinition1
    | functionDefinition2
    ;

functionDefinition1
    : FUNCTION IDENTIFIER? anyToken*? ('(' anyToken*? ')'  whereClause*? functionBody)? END
    ;

functionDefinition2
    : functionIdentifier '(' anyToken*? ')' whereClause*? '=' functionBody
    ;

functionIdentifier
    : IDENTIFIER
    | '(' anyToken*? ')' // Operator
    ;

whereClause
    : WHERE anyToken*?
    ;

functionBody
    : anyToken*? (statement anyToken*?)*?
    ;

statement
    : beginStatement
    | doStatement
    | forStatement
    | functionDefinition1
    | ifStatement
    | letStatement
    | macroStatement
    | structStatement
    | tryCatchStatement
    | typeStatement
    | whileStatement
    ;

beginStatement
    : BEGIN functionBody END
    ;

doStatement
    : DO functionBody END
    ;

forStatement
    : FOR functionBody END
    ;

ifStatement
    : IF functionBody (ELSIF functionBody)* (ELSE functionBody)? END
    ;

letStatement
    : LET functionBody END
    ;

macroStatement
    : MACRO functionBody END
    ;

structStatement
    : STRUCT functionBody END
    ;

tryCatchStatement
    : TRY functionBody (CATCH functionBody)? (FINALLY functionBody)? END
    ;

typeStatement
    : TYPE functionBody END
    ;

whileStatement
    : WHILE functionBody END
    ;

anyToken
    : ANY
    | BEGIN
    | CATCH
    | CHAR
    | DO
    | ELSE
    | ELSIF
    | END
    | EXTERNALCOMMAND
    | FINALLY
    | FOR
    | FUNCTION
    | IDENTIFIER
    | IF
    | LET
    | MACRO
    | STRING
    | STRUCT
    | TRY
    | TYPE
    | WHERE
    | WHILE
    | '(' anyToken*? ')'
    | '[' anyToken*? ']'
    | '{' anyToken*? '}'
    | '='
    | '&&' // short-circuit
    | '||' // short-circuit
    | '==' // to disambiguate from "="
    ;

// Lexer

COMMENTS : '#' (~[=\r\n]~[\r\n]*)? -> skip; // skip #= because otherwise multiline comments are not recognized, see next line
MULTILINECOMMENTS1 : '#=' .*? '=#' -> skip;
MULTILINECOMMENTS2 : '```' .*? '```' -> skip;
MULTILINESTRING : '"""' ('\\"'|.)*? '"""' -> skip;
NL : '\r'? '\n' -> skip ;
WHITESPACE : [ \t]+ -> skip ;

BEGIN : 'begin' ;
CATCH : 'catch' ;
CHAR : '\'' '\\'? .? '\'' ;
DO : 'do' ;
ELSE : 'else' ;
ELSIF : 'elsif' ;
END : 'end' ;
EXTERNALCOMMAND : '`' .*? '`' ;
FINALLY : 'finally' ;
FOR : 'for' ;
FUNCTION : 'function' ;
IF : 'if' ;
LET : 'let' ;
MACRO : 'macro' ;
STRING : '"' ('\\\\'|'\\"'|'$(' ('$(' .*? ')'|'"' .*? '"'|.)*? ')'|.)*? '"';
STRUCT : 'struct' ;
TRY : 'try' ;
TYPE : 'type' ;
WHERE : 'where' ;
WHILE : 'while' ;

IDENTIFIER : [$a-zA-Z_] [a-zA-Z_0-9]* ;

ANY : . ;





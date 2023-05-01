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
    : ABSTRACT
    | ANY
    | ARROWOPERATOR
    | ASSIGNMENTOPERATOR
    | BAREMODULE
    | BEGIN
    | BITSHIFTOPERATOR
    | BITSTYPE
    | BREAK
    | CATCH
    | CCALL
    | CHAR
    | CONST
    | CONTINUE
    | DO
    | ELSE
    | ELSIF
    | END
    | EXPORT
    | EXTERNALCOMMAND
    | FINALLY
    | FOR
    | FUNCTION
    | GLOBAL
    | IDENTIFIER
    | IF
    | IMMUTABLE
    | IMPORT
    | IMPORTALL
    | INSTANCEOF
    | LET
    | LOCAL
    | MACRO
    | MODULE
    | NUMERICAL
    | PIPEOPERATOR
    | QUOTE
    | RETURN
    | STAGED_FUNCTION
    | STRING
    | STRUCT
    | TRY
    | TYPE
    | TYPEALIAS
    | USING
    | WHERE
    | WHILE
    | '(' anyToken*? ')'
    | '[' anyToken*? ']'
    | '{' anyToken*? '}'
    | '='
    | '=>'
    | '&&' // short-circuit
    | '||' // short-circuit
    | '==' // to disambiguate from "="
    | '>='
    | '<='
    | '<'
    | '<:'
    | '>'
    | '...'
    ;

// Lexer

COMMENTS : '#' (~[=\r\n]~[\r\n]*)? -> skip; // skip #= because otherwise multiline comments are not recognized, see next line
MULTILINECOMMENTS1 : '#=' .*? '=#' -> skip;
MULTILINECOMMENTS2 : '```' .*? '```' -> skip;
MULTILINESTRING : '"""' ('\\"'|.)*? '"""' -> skip;
NL : '\r'? '\n' -> skip ;
WHITESPACE : [ \t]+ -> skip ;

ABSTRACT : 'abstract' ;

ARROWOPERATOR
    : '--'
    | '-->'
    ;

ASSIGNMENTOPERATOR
    : ':=' | '+=' | '-=' | '*=' | '/=' | '//=' | './/='
    | '.*=' | './=' | '\\=' | '.\\=' | '^=' | '.^=' | '%=' | '.%='
    | '|=' | '&=' | '$=' | '=>' | '<<=' | '>>=' | '>>>='
    | '.+=' | '.-='
    ;

BAREMODULE : 'baremodule' ;
BEGIN : 'begin' ;
BITSHIFTOPERATOR : '<<' | '>>' | '>>>' | '.<<' | '.>>' | '.>>>';
BITSTYPE : 'bitstype' ;
BREAK : 'break' ;
CATCH : 'catch' ;
CCALL : 'ccall' ;
CHAR : '\'' '\\'? .? '\'' ;
CONST : 'const' ;
CONTINUE : 'continue' ;
DO : 'do' ;
ELSE : 'else' ;
ELSIF : 'elsif' ;
END : 'end' ;
EXPORT : 'export' ;
EXTERNALCOMMAND : '`' .*? '`' ;
FINALLY : 'finally' ;
FOR : 'for' ;
FUNCTION : 'function' ;
GLOBAL : 'global' ;
IF : 'if' ;
IMMUTABLE : 'immutable' ;
IMPORT : 'import' ;
IMPORTALL : 'importall' ;
INSTANCEOF : '::' ;
LET : 'let' ;
LOCAL : 'local' ;
MACRO : 'macro' ;
MODULE : 'module' ;
PIPEOPERATOR : '|>'|  '<|' ;
QUOTE : 'quote' ;
RETURN : 'return' ;
STAGEDFUNCTION : 'stagedfunction' ;
STRING : '"' ('\\\\'|'\\"'|'$(' ('$(' .*? ')'|'"' .*? '"'|.)*? ')'|.)*? '"' ;
STRUCT : 'struct' ;
TRY : 'try' ;
TYPE : 'type' ;
TYPEALIAS : 'typealias' ;
USING : 'using' ;
WHERE : 'where' ;
WHILE : 'while' ;

NUMERICAL
    : INT_LITERAL
    | BINARY
    | OCTAL
    | HEX
    | FLOAT32_LITERAL
    | FLOAT64_LITERAL
    | HEX_FLOAT
    ;

INT_LITERAL : DEC_DGT+;
BINARY : '0b'BIN_DGT+;
OCTAL : '0o'OCT_DGT+;
HEX : '0x'HEX_DGT+;

FLOAT32_LITERAL
    : DEC_DGT+ '.'? DEC_DGT* EXP32?
    | '.' DEC_DGT* EXP32?
    ;

FLOAT64_LITERAL
    : DEC_DGT+ '.'? DEC_DGT* EXP64?
    | '.' DEC_DGT* EXP64?
    ;

HEX_FLOAT : '0x' HEX_DGT? ('.' HEX_DGT*)? ( 'p' | 'P' ) ( '+' | '-' )? DEC_DGT+;

fragment EXP32 : [f] [+\-]? DEC_DGT+;
fragment EXP64 : [e] [+\-]? DEC_DGT+;

IDENTIFIER : ('_'|UNi) ('_'|UNi|DEC_DGT)* '!'?;
fragment DEC_DGT : [0-9_];
fragment BIN_DGT : [0-1_];
fragment OCT_DGT : [0-7_];
fragment HEX_DGT : [0-9A-Fa-f_];

fragment UNi
    : 'A'..'Z'
    | 'a'..'z'
    | '\u00C0'..'\u00D6'
    | '\u00D8'..'\u00F6'
    | '\u00F8'..'\u02FF'
    | '\u0370'..'\u037D'
    | '\u037F'..'\u1FFF'
    | '\u200C'..'\u200D'
    | '\u2070'..'\u218F'
    | '\u2C00'..'\u2FEF'
    | '\u3001'..'\uD7FF'
    | '\uF900'..'\uFDCF'
    | '\uFDF0'..'\uFFFD'
    ;

ANY : . ;
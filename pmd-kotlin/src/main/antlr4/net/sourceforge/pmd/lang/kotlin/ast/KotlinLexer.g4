/**
 * Kotlin lexical grammar in ANTLR4 notation
 */

lexer grammar KotlinLexer;

import UnicodeClasses;

// SECTION: lexicalGeneral

ShebangLine
    : '#!' ~[\r\n]*
    ;

DelimitedComment
    : '/*' ( DelimitedComment | . )*? '*/'
      -> channel(HIDDEN)
    ;

LineComment
    : '//' ~[\r\n]*
      -> channel(HIDDEN)
    ;

WS
    : [\u0020\u0009\u000C]
      -> channel(HIDDEN)
    ;

NL: '\n' | '\r' '\n'?;

fragment Hidden: DelimitedComment | LineComment | WS;

// SECTION: separatorsAndOperations

RESERVED: '...';
DOT: '.';
COMMA: ',';
LPAREN: '(' -> pushMode(Inside);
RPAREN: ')';
LSQUARE: '[' -> pushMode(Inside);
RSQUARE: ']';
LCURL: '{' -> pushMode(DEFAULT_MODE);
/*
 * When using another programming language (not Java) to generate a parser,
 * please replace this code with the corresponding code of a programming language you are using.
 */
RCURL: '}' { if (!_modeStack.isEmpty()) { popMode(); } };
MULT: '*';
MOD: '%';
DIV: '/';
ADD: '+';
SUB: '-';
INCR: '++';
DECR: '--';
CONJ: '&&';
DISJ: '||';
EXCL_WS: '!' Hidden;
EXCL_NO_WS: '!';
COLON: ':';
SEMICOLON: ';';
ASSIGNMENT: '=';
ADD_ASSIGNMENT: '+=';
SUB_ASSIGNMENT: '-=';
MULT_ASSIGNMENT: '*=';
DIV_ASSIGNMENT: '/=';
MOD_ASSIGNMENT: '%=';
ARROW: '->';
DOUBLE_ARROW: '=>';
RANGE: '..';
COLONCOLON: '::';
DOUBLE_SEMICOLON: ';;';
HASH: '#';
AT_NO_WS: '@';
AT_POST_WS: '@' (Hidden | NL);
AT_PRE_WS: (Hidden | NL) '@' ;
AT_BOTH_WS: (Hidden | NL) '@' (Hidden | NL);
QUEST_WS: '?' Hidden;
QUEST_NO_WS: '?';
LANGLE: '<';
RANGLE: '>';
LE: '<=';
GE: '>=';
EXCL_EQ: '!=';
EXCL_EQEQ: '!==';
AS_SAFE: 'as?';
EQEQ: '==';
EQEQEQ: '===';
SINGLE_QUOTE: '\'';

// SECTION: keywords

RETURN_AT: 'return@' Identifier;
CONTINUE_AT: 'continue@' Identifier;
BREAK_AT: 'break@' Identifier;

THIS_AT: 'this@' Identifier;
SUPER_AT: 'super@' Identifier;

FILE: 'file';
FIELD: 'field';
PROPERTY: 'property';
GET: 'get';
SET: 'set';
RECEIVER: 'receiver';
PARAM: 'param';
SETPARAM: 'setparam';
DELEGATE: 'delegate';

PACKAGE: 'package';
IMPORT: 'import';
CLASS: 'class';
INTERFACE: 'interface';
FUN: 'fun';
OBJECT: 'object';
VAL: 'val';
VAR: 'var';
TYPE_ALIAS: 'typealias';
CONSTRUCTOR: 'constructor';
BY: 'by';
COMPANION: 'companion';
INIT: 'init';
THIS: 'this';
SUPER: 'super';
TYPEOF: 'typeof';
WHERE: 'where';
IF: 'if';
ELSE: 'else';
WHEN: 'when';
TRY: 'try';
CATCH: 'catch';
FINALLY: 'finally';
FOR: 'for';
DO: 'do';
WHILE: 'while';
THROW: 'throw';
RETURN: 'return';
CONTINUE: 'continue';
BREAK: 'break';
AS: 'as';
IS: 'is';
IN: 'in';
NOT_IS: '!is' (Hidden | NL);
NOT_IN: '!in' (Hidden | NL);
OUT: 'out';
DYNAMIC: 'dynamic';

// SECTION: lexicalModifiers

PUBLIC: 'public';
PRIVATE: 'private';
PROTECTED: 'protected';
INTERNAL: 'internal';
ENUM: 'enum';
SEALED: 'sealed';
ANNOTATION: 'annotation';
DATA: 'data';
INNER: 'inner';
VALUE: 'value';
TAILREC: 'tailrec';
OPERATOR: 'operator';
INLINE: 'inline';
INFIX: 'infix';
EXTERNAL: 'external';
SUSPEND: 'suspend';
OVERRIDE: 'override';
ABSTRACT: 'abstract';
FINAL: 'final';
OPEN: 'open';
CONST: 'const';
LATEINIT: 'lateinit';
VARARG: 'vararg';
NOINLINE: 'noinline';
CROSSINLINE: 'crossinline';
REIFIED: 'reified';
EXPECT: 'expect';
ACTUAL: 'actual';

// SECTION: literals

fragment DecDigit: '0'..'9';
fragment DecDigitNoZero: '1'..'9';
fragment DecDigitOrSeparator: DecDigit | '_';

fragment DecDigits
    : DecDigit DecDigitOrSeparator* DecDigit
    | DecDigit
    ;

fragment DoubleExponent: [eE] [+-]? DecDigits;

RealLiteral
    : FloatLiteral
    | DoubleLiteral
    ;

FloatLiteral
    : DoubleLiteral [fF]
    | DecDigits [fF]
    ;

DoubleLiteral
    : DecDigits? '.' DecDigits DoubleExponent?
    | DecDigits DoubleExponent
    ;

IntegerLiteral
    : DecDigitNoZero DecDigitOrSeparator* DecDigit
    | DecDigit
    ;

fragment HexDigit: [0-9a-fA-F];
fragment HexDigitOrSeparator: HexDigit | '_';

HexLiteral
    : '0' [xX] HexDigit HexDigitOrSeparator* HexDigit
    | '0' [xX] HexDigit
    ;

fragment BinDigit: [01];
fragment BinDigitOrSeparator: BinDigit | '_';

BinLiteral
    : '0' [bB] BinDigit BinDigitOrSeparator* BinDigit
    | '0' [bB] BinDigit
    ;

UnsignedLiteral
    : (IntegerLiteral | HexLiteral | BinLiteral) [uU] [lL]?
    ;

LongLiteral
    : (IntegerLiteral | HexLiteral | BinLiteral) [lL]
    ;

BooleanLiteral: 'true'| 'false';

NullLiteral: 'null';

CharacterLiteral
    : '\'' (EscapeSeq | ~[\n\r'\\]) '\''
    ;

// SECTION: lexicalIdentifiers

fragment UnicodeDigit: UNICODE_CLASS_ND;

Identifier
    : (Letter | '_') (Letter | '_' | UnicodeDigit)*
    | '`' ~([\r\n] | '`')+ '`'
    ;

IdentifierOrSoftKey
    : Identifier
    /* Soft keywords */
    | ABSTRACT
    | ANNOTATION
    | BY
    | CATCH
    | COMPANION
    | CONSTRUCTOR
    | CROSSINLINE
    | DATA
    | DYNAMIC
    | ENUM
    | EXTERNAL
    | FINAL
    | FINALLY
    | IMPORT
    | INFIX
    | INIT
    | INLINE
    | INNER
    | INTERNAL
    | LATEINIT
    | NOINLINE
    | OPEN
    | OPERATOR
    | OUT
    | OVERRIDE
    | PRIVATE
    | PROTECTED
    | PUBLIC
    | REIFIED
    | SEALED
    | TAILREC
    | VARARG
    | WHERE
    | GET
    | SET
    | FIELD
    | PROPERTY
    | RECEIVER
    | PARAM
    | SETPARAM
    | DELEGATE
    | FILE
    | EXPECT
    | ACTUAL
    | VALUE
    /* Strong keywords */
    | CONST
    | SUSPEND
    ;

FieldIdentifier
    : '$' IdentifierOrSoftKey
    ;

fragment UniCharacterLiteral
    : '\\' 'u' HexDigit HexDigit HexDigit HexDigit
    ;

fragment EscapedIdentifier
    : '\\' ('t' | 'b' | 'r' | 'n' | '\'' | '"' | '\\' | '$')
    ;

fragment EscapeSeq
    : UniCharacterLiteral
    | EscapedIdentifier
    ;

// SECTION: characters

fragment Letter
    : UNICODE_CLASS_LU
    | UNICODE_CLASS_LL
    | UNICODE_CLASS_LT
    | UNICODE_CLASS_LM
    | UNICODE_CLASS_LO
    ;

// SECTION: strings

QUOTE_OPEN: '"' -> pushMode(LineString);

TRIPLE_QUOTE_OPEN: '"""' -> pushMode(MultiLineString);

mode LineString;

QUOTE_CLOSE
    : '"' -> popMode
    ;

LineStrRef
    : FieldIdentifier
    ;

LineStrText
    : ~('\\' | '"' | '$')+ | '$'
    ;

LineStrEscapedChar
    : EscapedIdentifier
    | UniCharacterLiteral
    ;

LineStrExprStart
    : '${' -> pushMode(DEFAULT_MODE)
    ;

mode MultiLineString;

TRIPLE_QUOTE_CLOSE
    : MultiLineStringQuote? '"""' -> popMode
    ;

MultiLineStringQuote
    : '"'+
    ;

MultiLineStrRef
    : FieldIdentifier
    ;

MultiLineStrText
    :  ~('"' | '$')+ | '$'
    ;

MultiLineStrExprStart
    : '${' -> pushMode(DEFAULT_MODE)
    ;

// SECTION: inside

mode Inside;

Inside_RPAREN: RPAREN -> popMode, type(RPAREN);
Inside_RSQUARE: RSQUARE -> popMode, type(RSQUARE);
Inside_LPAREN: LPAREN -> pushMode(Inside), type(LPAREN);
Inside_LSQUARE: LSQUARE -> pushMode(Inside), type(LSQUARE);
Inside_LCURL: LCURL -> pushMode(DEFAULT_MODE), type(LCURL);
Inside_RCURL: RCURL -> popMode, type(RCURL);

Inside_DOT: DOT -> type(DOT);
Inside_COMMA: COMMA  -> type(COMMA);
Inside_MULT: MULT -> type(MULT);
Inside_MOD: MOD  -> type(MOD);
Inside_DIV: DIV -> type(DIV);
Inside_ADD: ADD  -> type(ADD);
Inside_SUB: SUB  -> type(SUB);
Inside_INCR: INCR  -> type(INCR);
Inside_DECR: DECR  -> type(DECR);
Inside_CONJ: CONJ  -> type(CONJ);
Inside_DISJ: DISJ  -> type(DISJ);
Inside_EXCL_WS: '!' (Hidden|NL) -> type(EXCL_WS);
Inside_EXCL_NO_WS: EXCL_NO_WS  -> type(EXCL_NO_WS);
Inside_COLON: COLON  -> type(COLON);
Inside_SEMICOLON: SEMICOLON  -> type(SEMICOLON);
Inside_ASSIGNMENT: ASSIGNMENT  -> type(ASSIGNMENT);
Inside_ADD_ASSIGNMENT: ADD_ASSIGNMENT  -> type(ADD_ASSIGNMENT);
Inside_SUB_ASSIGNMENT: SUB_ASSIGNMENT  -> type(SUB_ASSIGNMENT);
Inside_MULT_ASSIGNMENT: MULT_ASSIGNMENT  -> type(MULT_ASSIGNMENT);
Inside_DIV_ASSIGNMENT: DIV_ASSIGNMENT  -> type(DIV_ASSIGNMENT);
Inside_MOD_ASSIGNMENT: MOD_ASSIGNMENT  -> type(MOD_ASSIGNMENT);
Inside_ARROW: ARROW  -> type(ARROW);
Inside_DOUBLE_ARROW: DOUBLE_ARROW  -> type(DOUBLE_ARROW);
Inside_RANGE: RANGE  -> type(RANGE);
Inside_RESERVED: RESERVED -> type(RESERVED);
Inside_COLONCOLON: COLONCOLON  -> type(COLONCOLON);
Inside_DOUBLE_SEMICOLON: DOUBLE_SEMICOLON  -> type(DOUBLE_SEMICOLON);
Inside_HASH: HASH  -> type(HASH);
Inside_AT_NO_WS: AT_NO_WS  -> type(AT_NO_WS);
Inside_AT_POST_WS: AT_POST_WS  -> type(AT_POST_WS);
Inside_AT_PRE_WS: AT_PRE_WS  -> type(AT_PRE_WS);
Inside_AT_BOTH_WS: AT_BOTH_WS  -> type(AT_BOTH_WS);
Inside_QUEST_WS: '?' (Hidden | NL) -> type(QUEST_WS);
Inside_QUEST_NO_WS: QUEST_NO_WS -> type(QUEST_NO_WS);
Inside_LANGLE: LANGLE  -> type(LANGLE);
Inside_RANGLE: RANGLE  -> type(RANGLE);
Inside_LE: LE  -> type(LE);
Inside_GE: GE  -> type(GE);
Inside_EXCL_EQ: EXCL_EQ  -> type(EXCL_EQ);
Inside_EXCL_EQEQ: EXCL_EQEQ  -> type(EXCL_EQEQ);
Inside_IS: IS -> type(IS);
Inside_NOT_IS: NOT_IS -> type(NOT_IS);
Inside_NOT_IN: NOT_IN -> type(NOT_IN);
Inside_AS: AS  -> type(AS);
Inside_AS_SAFE: AS_SAFE  -> type(AS_SAFE);
Inside_EQEQ: EQEQ  -> type(EQEQ);
Inside_EQEQEQ: EQEQEQ  -> type(EQEQEQ);
Inside_SINGLE_QUOTE: SINGLE_QUOTE  -> type(SINGLE_QUOTE);
Inside_QUOTE_OPEN: QUOTE_OPEN -> pushMode(LineString), type(QUOTE_OPEN);
Inside_TRIPLE_QUOTE_OPEN: TRIPLE_QUOTE_OPEN -> pushMode(MultiLineString), type(TRIPLE_QUOTE_OPEN);

Inside_VAL: VAL -> type(VAL);
Inside_VAR: VAR -> type(VAR);
Inside_FUN: FUN -> type(FUN);
Inside_OBJECT: OBJECT -> type(OBJECT);
Inside_SUPER: SUPER -> type(SUPER);
Inside_IN: IN -> type(IN);
Inside_OUT: OUT -> type(OUT);
Inside_FIELD: FIELD -> type(FIELD);
Inside_FILE: FILE -> type(FILE);
Inside_PROPERTY: PROPERTY -> type(PROPERTY);
Inside_GET: GET -> type(GET);
Inside_SET: SET -> type(SET);
Inside_RECEIVER: RECEIVER -> type(RECEIVER);
Inside_PARAM: PARAM -> type(PARAM);
Inside_SETPARAM: SETPARAM -> type(SETPARAM);
Inside_DELEGATE: DELEGATE -> type(DELEGATE);
Inside_THROW: THROW -> type(THROW);
Inside_RETURN: RETURN -> type(RETURN);
Inside_CONTINUE: CONTINUE -> type(CONTINUE);
Inside_BREAK: BREAK -> type(BREAK);
Inside_RETURN_AT: RETURN_AT -> type(RETURN_AT);
Inside_CONTINUE_AT: CONTINUE_AT -> type(CONTINUE_AT);
Inside_BREAK_AT: BREAK_AT -> type(BREAK_AT);
Inside_IF: IF -> type(IF);
Inside_ELSE: ELSE -> type(ELSE);
Inside_WHEN: WHEN -> type(WHEN);
Inside_TRY: TRY -> type(TRY);
Inside_CATCH: CATCH -> type(CATCH);
Inside_FINALLY: FINALLY -> type(FINALLY);
Inside_FOR: FOR -> type(FOR);
Inside_DO: DO -> type(DO);
Inside_WHILE: WHILE -> type(WHILE);

Inside_PUBLIC: PUBLIC -> type(PUBLIC);
Inside_PRIVATE: PRIVATE -> type(PRIVATE);
Inside_PROTECTED: PROTECTED -> type(PROTECTED);
Inside_INTERNAL: INTERNAL -> type(INTERNAL);
Inside_ENUM: ENUM -> type(ENUM);
Inside_SEALED: SEALED -> type(SEALED);
Inside_ANNOTATION: ANNOTATION -> type(ANNOTATION);
Inside_DATA: DATA -> type(DATA);
Inside_INNER: INNER -> type(INNER);
Inside_VALUE: VALUE -> type(VALUE);
Inside_TAILREC: TAILREC -> type(TAILREC);
Inside_OPERATOR: OPERATOR -> type(OPERATOR);
Inside_INLINE: INLINE -> type(INLINE);
Inside_INFIX: INFIX -> type(INFIX);
Inside_EXTERNAL: EXTERNAL -> type(EXTERNAL);
Inside_SUSPEND: SUSPEND -> type(SUSPEND);
Inside_OVERRIDE: OVERRIDE -> type(OVERRIDE);
Inside_ABSTRACT: ABSTRACT -> type(ABSTRACT);
Inside_FINAL: FINAL -> type(FINAL);
Inside_OPEN: OPEN -> type(OPEN);
Inside_CONST: CONST -> type(CONST);
Inside_LATEINIT: LATEINIT -> type(LATEINIT);
Inside_VARARG: VARARG -> type(VARARG);
Inside_NOINLINE: NOINLINE -> type(NOINLINE);
Inside_CROSSINLINE: CROSSINLINE -> type(CROSSINLINE);
Inside_REIFIED: REIFIED -> type(REIFIED);
Inside_EXPECT: EXPECT -> type(EXPECT);
Inside_ACTUAL: ACTUAL -> type(ACTUAL);

Inside_BooleanLiteral: BooleanLiteral -> type(BooleanLiteral);
Inside_IntegerLiteral: IntegerLiteral -> type(IntegerLiteral);
Inside_HexLiteral: HexLiteral -> type(HexLiteral);
Inside_BinLiteral: BinLiteral -> type(BinLiteral);
Inside_CharacterLiteral: CharacterLiteral -> type(CharacterLiteral);
Inside_RealLiteral: RealLiteral -> type(RealLiteral);
Inside_NullLiteral: NullLiteral -> type(NullLiteral);
Inside_LongLiteral: LongLiteral -> type(LongLiteral);
Inside_UnsignedLiteral: UnsignedLiteral -> type(UnsignedLiteral);

Inside_Identifier: Identifier -> type(Identifier);
Inside_Comment: (LineComment | DelimitedComment) -> channel(HIDDEN);
Inside_WS: WS -> channel(HIDDEN);
Inside_NL: NL -> channel(HIDDEN);

mode DEFAULT_MODE;

ErrorCharacter: .;

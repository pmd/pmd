/*
 [The "BSD licence"]
 Copyright (c) 2019 Wener
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

grammar Dart2;

compilationUnit: libraryDefinition | partDeclaration;

WHITESPACE
//  : ('\t' | ' ' | NEWLINE)+   -> skip
  :  [ \t\r\n\u000C]+ -> skip
  ;

SEMICOLON: ';' ;

// 8 Variables
variableDeclaration
  : declaredIdentifier (',' identifier)*
  ;

declaredIdentifier
  : metadata finalConstVarOrType identifier
  ;
finalConstVarOrType
  : 'final' type?
  | 'const' type?
  | varOrType
  ;
varOrType
  : 'var'
  | type
  ;

initializedVariableDeclaration
  : declaredIdentifier ('=' expression)? (','initializedIdentifier)*
  ;
initializedIdentifier
  : identifier ('=' expression)?
  ;
initializedIdentifierList
  : initializedIdentifier (',' initializedIdentifier)*
  ;




// 9 Functions
functionSignature
  : metadata returnType? identifier formalParameterPart
  ;
formalParameterPart
  : typeParameters? formalParameterList
  ;
returnType
  : 'void'
  | type
  ;

functionBody
  : 'async'? '=>' expression SEMICOLON
  | ('async' | 'async*' | 'sync*')? block
  ;
block
  : '{' statements '}'
  ;

// 9.2 Formal Parameters
formalParameterList
  : '(' ')'
  | '(' normalFormalParameters ')'
  | '(' normalFormalParameters (',' optionalFormalParameters)? ')'
  | '(' optionalFormalParameters ')'
  ;
normalFormalParameters
  : normalFormalParameter (',' normalFormalParameter)*
  ;
optionalFormalParameters
  : optionalPositionalFormalParameters
  | namedFormalParameters
  ;
optionalPositionalFormalParameters
  : '[' defaultFormalParameter (',' defaultFormalParameter)* ','? ']'
  ;
namedFormalParameters
  : '{' defaultNamedParameter (',' defaultNamedParameter)* ','? '}'
  ;

// 9.2.1 Required Formals
normalFormalParameter
  : functionFormalParameter
  | fieldFormalParameter
  | simpleFormalParameter
  ;
functionFormalParameter
  : metadata COVARIANT? returnType? identifier formalParameterPart
  ;
simpleFormalParameter
  : declaredIdentifier
  | metadata COVARIANT? identifier
  ;
fieldFormalParameter
  : metadata finalConstVarOrType? 'this' '.' identifier formalParameterPart?
  ;

// 9.2.2 Optional Formals
defaultFormalParameter
  : normalFormalParameter ('=' expression)?
  ;
defaultNamedParameter
  : normalFormalParameter ('=' expression)?
  | normalFormalParameter (':' expression)?
  ;

// 10 Classes
classDefinition
  : metadata ABSTRACT? 'class' identifier typeParameters?
    superclass? mixins? interfaces?
    '{' (metadata classMemberDefinition)* '}'
  | metadata ABSTRACT? 'class' mixinApplicationClass
;
mixins
  : 'with' typeList
  ;
classMemberDefinition
  : declaration SEMICOLON
  | methodSignature functionBody
  ;
methodSignature
  : constructorSignature initializers?
  | factoryConstructorSignature
  | STATIC? functionSignature
  | STATIC? getterSignature
  | STATIC? setterSignature
  | operatorSignature
  ;


declaration
  : constantConstructorSignature (redirection | initializers)?
  | constructorSignature (redirection | initializers)?
  | EXTERNAL constantConstructorSignature
  | EXTERNAL constructorSignature
  | (EXTERNAL STATIC?)? getterSignature
  | (EXTERNAL STATIC?)? setterSignature
  | EXTERNAL? operatorSignature
  | (EXTERNAL STATIC?)? functionSignature
  | STATIC ('final' | 'const') type? staticFinalDeclarationList
  | 'final' type? initializedIdentifierList
  | (STATIC | COVARIANT)? ('var' | type) initializedIdentifierList
  ;

staticFinalDeclarationList
  : staticFinalDeclaration (',' staticFinalDeclaration)*
  ;
staticFinalDeclaration
  : identifier '=' expression
  ;

// 10.1.1 Operators
operatorSignature
  : returnType? OPERATOR operator formalParameterList
  ;
operator
  : '~' | binaryOperator | '[]' | '[]='
  ;

binaryOperator
  : multiplicativeOperator
  | additiveOperator
  | shiftOperator
  | relationalOperator
  | '=='
  | bitwiseOperator
  ;
// 10.2 Getters
getterSignature
  : returnType? GET identifier
  ;
// 10.2 Setters
setterSignature
  : returnType? SET identifier formalParameterList
  ;

// 10.6 Constructors
constructorSignature
  : identifier ('.' identifier)? formalParameterList
  ;
redirection
  : ':' 'this' ('.' identifier)? arguments
  ;

initializers
  : ':' initializerListEntry (',' initializerListEntry)*
  ;
initializerListEntry
  : 'super' arguments
  | 'super' '.' identifier arguments
  | fieldInitializer
  | assertion
  ;
fieldInitializer
  : ('this' '.')? identifier '=' conditionalExpression cascadeSection*
  ;

// 10.6.2 Factories
factoryConstructorSignature
  : FACTORY identifier ('.' identifier)? formalParameterList
  ;
redirectingFactoryConstructorSignature
  : 'const'? FACTORY identifier ('.' identifier)? formalParameterList '='
    type ('.' identifier)?
  ;
// 10.6.3 Constant Constructors
constantConstructorSignature: 'const' qualified formalParameterList;

// 10.9 Supperclasses
superclass: 'extends' type;

// 10.10 SUperinterfaces
interfaces: IMPLEMENTS typeList;

// 12.1 Mixin Application
mixinApplicationClass
  : identifier typeParameters? '=' mixinApplication SEMICOLON;
mixinApplication
  : type mixins interfaces?
  ;

// 13 Enums
enumType
  : metadata 'enum' identifier
    '{' enumEntry (',' enumEntry)* ','? '}'
  ;

enumEntry
  : metadata identifier
  ;

// 14 Generics
typeParameter
  : metadata identifier ('extends' type)?
  ;
typeParameters
  : '<' typeParameter (',' typeParameter)* '>'
  ;

// 15 Metadata
metadata
  : ('@' qualified ('.' identifier)? arguments?)*
  ;

// 16 Expressions
expression
  : assignableExpression assignmentOperator expression
  | conditionalExpression cascadeSection*
  | throwExpression
  ;
expressionWithoutCascade
  : assignableExpression assignmentOperator expressionWithoutCascade
  | conditionalExpression
  | throwExpressionWithoutCascade
  ;
expressionList
  : expression (',' expression)*
  ;
primary
  : thisExpression
  | 'super' unconditionalAssignableSelector
  | functionExpression
  | literal
  | identifier
  | newExpression
  | constObjectExpression
  | '(' expression ')'
  ;

// 16.1 Constants

literal
  : nullLiteral
  | booleanLiteral
  | numericLiteral
  | stringLiteral
  | symbolLiteral
  | mapLiteral
  | listLiteral
  ;
nullLiteral: 'null';

numericLiteral
  : NUMBER
  | HEX_NUMBER
  ;

NUMBER
  : DIGIT+ ('.' DIGIT+)? EXPONENT?
  | '.' DIGIT+ EXPONENT?
  ;
fragment
EXPONENT
  : ('e' | 'E') ('+' | '-')? DIGIT+
  ;
HEX_NUMBER
  : '0x' HEX_DIGIT+
  | '0X' HEX_DIGIT+
  ;
fragment
HEX_DIGIT
  : [a-f]
  | [A-F]
  | DIGIT
  ;

booleanLiteral
  : 'true'
  | 'false'
  ;

stringLiteral: (MultiLineString | SingleLineString)+;

SingleLineString
  : '"' StringContentDQ* '"'
  | '\'' StringContentSQ* '\''
  | 'r\'' (~('\'' | '\n' | '\r'))* '\''
  | 'r"' (~('"' | '\n' | '\r'))* '"'
  ;

fragment
StringContentDQ
  : ~('\\' | '"' /*| '$'*/ | '\n' | '\r')
  | '\\' ~('\n' | '\r')
  //| stringInterpolation
  ;

fragment
StringContentSQ
  : ~('\\' | '\'' /*| '$'*/ | '\n' | '\r')
  | '\\' ~('\n' | '\r')
  //| stringInterpolation
  ;

MultiLineString
  : '"""' StringContentTDQ* '"""'
  | '\'\'\'' StringContentTSQ* '\'\'\''
  | 'r"""' (~'"' | '"' ~'"' | '""' ~'"')* '"""'
  | 'r\'\'\'' (~'\'' | '\'' ~'\'' | '\'\'' ~'\'')* '\'\'\''
  ;

fragment
StringContentTDQ
  : ~('\\' | '"' /*| '$'*/)
  | '"' ~'"' | '""' ~'"'
  //| stringInterpolation
  ;

fragment StringContentTSQ
  : ~('\\' | '\'' /*| '$'*/)
  | '\'' ~'\'' | '\'\'' ~'\''
  //| stringInterpolation
  ;

NEWLINE
  : '\n'
  | '\r'
  | '\r\n'
  ;

// 16.5.1 String Interpolation
stringInterpolation
//  : '$' IDENTIFIER_NO_DOLLAR
  : '$' identifier// FIXME
  | '${' expression '}'
  ;

// 16.6 Symbols
symbolLiteral
  : '#' (operator | (identifier (',' identifier)*))
  ;
// 16.7 Lists
listLiteral
  : 'const'? typeArguments? '[' (expressionList ','?)? ']'
  ;

// 16.8 Maps
mapLiteral
  : 'const'? typeArguments?
    '{' (mapLiteralEntry (',' mapLiteralEntry)* ','?)? '}'
;
mapLiteralEntry
  : expression ':' expression
  ;

// 16.9 Throw
throwExpression
  : 'throw' expression
  ;
throwExpressionWithoutCascade
  : 'throw' expressionWithoutCascade
  ;

// 16.10 Function Expressions
functionExpression
  : formalParameterPart functionBody
  ;

// 16.11 This
thisExpression: 'this';

// 16.12.1 New
newExpression
  : 'new' type ('.' identifier)? arguments
  ;

// 16.12.2 Const
constObjectExpression
  : 'const' type ('.' identifier)? arguments
  ;

// 16.14.1 Actual Argument List Evaluation
arguments
  : '(' (argumentList ','?)? ')'
  ;
argumentList
  : namedArgument (',' namedArgument)*
  | expressionList (',' namedArgument)*
  ;
namedArgument
  : label expression
  ;

// 16.18.2 Cascaded Invocations
cascadeSection
  : '..' (cascadeSelector argumentPart*)
         (assignableSelector argumentPart*)*
         (assignmentOperator expressionWithoutCascade)?
  ;
cascadeSelector
  : '[' expression ']'
  | identifier
  ;
argumentPart
  : typeArguments? arguments
  ;

// 16.20 Assignment
assignmentOperator
  : '='
  | compoundAssignmentOperator
  ;

// 16.20.1 Compound Assignment
compoundAssignmentOperator
  : '*='
  | '/='
  | '~/='
  | '%='
  | '+='
  | '<<='
  | '>>='
  | '>>>='
  | '&='
  | '^='
  | '|='
  | '??='
  ;

// 16.21 Conditional
conditionalExpression
  : ifNullExpression
    ('?' expressionWithoutCascade ':' expressionWithoutCascade)?
  ;
// 16.22 If-null Expression
ifNullExpression
  : logicalOrExpression ('??' logicalOrExpression)*
  ;

// 16.23 Logical Boolean Expressions
logicalOrExpression
  : logicalAndExpression ('||' logicalAndExpression)*
  ;
logicalAndExpression
  : equalityExpression ('&&' equalityExpression)*
  ;

// 16.24 Equality
equalityExpression
  : relationalExpression (equalityOperator relationalExpression)?
  | 'super' equalityOperator relationalExpression
  ;
equalityOperator
  : '=='
  | '!='
  ;

// 16.25 Relational Expressions
relationalExpression
  : bitwiseOrExpression
    (
      typeTest
      | typeCast
      | relationalOperator bitwiseOrExpression
    )?
  | 'super' relationalOperator bitwiseOrExpression
  ;
relationalOperator
  : '>='
  | '>'
  | '<='
  | '<'
  ;

// 16.26 Bitwize Expression
bitwiseOrExpression
  : bitwiseXorExpression ('|' bitwiseXorExpression)*
  | 'super' ('|' bitwiseOrExpression)+
  ;
bitwiseXorExpression
  : bitwiseAndExpression ('^' bitwiseAndExpression)*
  | 'super' ('^' bitwiseAndExpression)+
  ;
bitwiseAndExpression
  : shiftExpression ('&' shiftExpression)*
  | 'super' ('&' shiftExpression)+
  ;
bitwiseOperator
  : '&'
  | '^'
  | '|'
  ;

// 16.27 Shift
shiftExpression
  : additiveExpression (shiftOperator additiveExpression)*
  | 'super' (shiftOperator additiveExpression)+
  ;
shiftOperator
  : '<<'
  | '>>'
  | '>>>'
  ;

// 16.28 Additive Expression
additiveExpression
  : multiplicativeExpression (additiveOperator multiplicativeExpression)*
  | 'super' (additiveOperator multiplicativeExpression)+
  ;
additiveOperator
  : '+'
  | '-'
  ;
// 16.29 Multiplicative Expression
multiplicativeExpression
  : unaryExpression (multiplicativeOperator unaryExpression)*
  | 'super' (multiplicativeOperator unaryExpression)+
  ;
multiplicativeOperator
  : '*'
  | '/'
  | '%'
  | '~/'
  ;

// 16.30 Unary Expression
unaryExpression
  : prefixOperator unaryExpression
  | awaitExpression
  | postfixExpression
  | (minusOperator | tildeOperator) 'super'
  | incrementOperator assignableExpression
  ;
prefixOperator
  : minusOperator
  | negationOperator
  | tildeOperator
  ;
minusOperator: '-';
negationOperator: '!';
tildeOperator: '~';

// 16.31 Await Expressions
awaitExpression
  : 'await' unaryExpression
  ;

// 16.32 Postfix Expressions
postfixExpression
  : assignableExpression postfixOperator
  | primary selector*
  ;
postfixOperator
  : incrementOperator
  ;
selector
  : assignableSelector
  | argumentPart
  ;

incrementOperator
  : '++'
  | '--'
  ;
// 16.33 Assignable Expressions
// NOTE
// primary (argumentPart* assignableSelector)+ -> primary (argumentPart* assignableSelector)?
assignableExpression
  : primary (argumentPart* assignableSelector)?
  | 'super' unconditionalAssignableSelector identifier
  ;
unconditionalAssignableSelector
  : '[' expression ']'
  | '.' identifier
  ;
assignableSelector
  : unconditionalAssignableSelector
  | '?.' identifier
  ;

identifier
  : IDENTIFIER
  ;
qualified
  : identifier ('.' identifier)?
  ;
// 16.35 Type Test
typeTest
  : isOperator type
  ;
isOperator
  : 'is' '!'?
  ;

// 16.36 Type Cast
typeCast
  : asOperator type
  ;
asOperator
  : AS
  ;
// 17 Statements
statements
  : statement*
  ;
statement
  : label* nonLabledStatment
  ;
nonLabledStatment
  : block
  | localVariableDeclaration
  | forStatement
  | whileStatement
  | doStatement
  | switchStatement
  | ifStatement
  | rethrowStatment
  | tryStatement
  | breakStatement
  | continueStatement
  | returnStatement
  | yieldStatement
  | yieldEachStatement
  | expressionStatement
  | assertStatement
  | localFunctionDeclaration
  ;

// 17.2 Expression Statements
expressionStatement
  : expression? SEMICOLON
  ;

// 17.3 Local Variable Declaration
localVariableDeclaration
  : initializedVariableDeclaration SEMICOLON
  ;
// 17.4 Local Function Declaration
localFunctionDeclaration
  : functionSignature functionBody
  ;
// 17.5 If
ifStatement
  : 'if' '(' expression ')' statement ('else' statement)?
  ;

// 17.6 For for
forStatement
  : 'await'? 'for' '(' forLoopParts ')' statement
  ;
forLoopParts
  : forInitializerStatement expression? SEMICOLON expressionList?
  | declaredIdentifier 'in' expression
  | identifier 'in' expression
  ;
forInitializerStatement
  : localVariableDeclaration
  | expression? SEMICOLON
  ;

// 17.7 While

whileStatement
  : 'while' '(' expression ')' statement
  ;
// 17.8 Do
doStatement
  : 'do' statement 'while' '(' expression ')' SEMICOLON
  ;
// 17.9 Switch
switchStatement
  : 'switch'  '(' expression ')' '{' switchCase* defaultCase? '}'
  ;
switchCase
  : label* 'case' expression ':' statements
  ;
defaultCase
  : label* 'default' ':' statements
  ;

// 17.10 Rethrow
rethrowStatment
  : 'rethrow' SEMICOLON
  ;

// 17.11 Try
tryStatement
  : 'try' block (onPart+ finallyPart? | finallyPart)
  ;
onPart
  : catchPart block
  | 'on' type catchPart? block
  ;
catchPart
  : 'catch' '(' identifier (',' identifier)? ')'
  ;
finallyPart
  : 'finally' block
  ;

// 17.12 Return

returnStatement
  : 'return' expression? SEMICOLON
  ;

// 17.13 Labels
label
  : identifier ':'
  ;

// 17.13 Break
breakStatement
  : 'break' identifier? SEMICOLON
  ;

// 17.13 Continue
continueStatement
  : 'continue' identifier? SEMICOLON
  ;

// 17.16.1 Yield
yieldStatement
  : 'yield' expression SEMICOLON
  ;
// 17.16.1 Yield-Each
yieldEachStatement
  : 'yield*' expression SEMICOLON
  ;

// 17.17 Assert
assertStatement
  : assertion SEMICOLON
  ;
assertion
  : 'assert' '(' expression (',' expression )? ','? ')'
  ;

// 18 Libraries and Scripts
topLevelDefinition
  : classDefinition
  | enumType
  | typeAlias
  | EXTERNAL? functionSignature SEMICOLON
  | EXTERNAL? getterSignature SEMICOLON
  | EXTERNAL? setterSignature SEMICOLON
  | functionSignature functionBody
  | returnType? GET identifier functionBody
  | returnType? SET identifier formalParameterList functionBody
  | ('final' | 'const') type? staticFinalDeclarationList SEMICOLON
  | variableDeclaration SEMICOLON
  ;
getOrSet
  : GET
  | SET
  ;
libraryDefinition
  : scriptTag? libraryName? importOrExport* partDirective*
    topLevelDefinition*
  ;
scriptTag
  :  '#!' (~NEWLINE)* NEWLINE
  ;

libraryName
  : metadata LIBRARY dottedIdentifierList SEMICOLON
  ;
importOrExport
  : libraryimport
  | libraryExport
  ;
dottedIdentifierList
  : identifier (',' identifier)*
  ;

libraryimport
  : metadata importSpecification
  ;

importSpecification
  : IMPORT configurableUri (AS identifier)? combinator* SEMICOLON
//  | IMPORT uri DEFERRED AS identifier combinator* SEMICOLON
  ;

combinator
  : 'show' identifierList
  | 'hide' identifierList
  ;
identifierList
  : identifier (',' identifier)*
  ;

// 18.2 Exports
libraryExport
  : metadata EXPORT configurableUri combinator* SEMICOLON
  ;

// 18.3 Parts
partDirective
  : metadata PART uri SEMICOLON
  ;
partHeader
  : metadata PART 'of' identifier ('.' identifier)* SEMICOLON
  ;
partDeclaration
  : partHeader topLevelDefinition* EOF
  ;

// 18.5 URIs
uri
  : stringLiteral
  ;
configurableUri
  : uri configurationUri*
  ;
configurationUri
  : 'if' '(' uriTest ')' uri
  ;
uriTest
  : dottedIdentifierList ('==' stringLiteral)?
  ;

// 19.1 Static Types
type
  : typeName typeArguments?
  ;
typeName
  : qualified
  | 'void' // SyntaxFix
  ;
typeArguments
  : '<' typeList '>'
  ;
typeList
  : type (',' type)*
  ;

// 19.3.1 Typedef
typeAlias
  : metadata TYPEDEF typeAliasBody
  ;
typeAliasBody
  : functionTypeAlias
  ;
functionTypeAlias
  : functionPrefix typeParameters? formalParameterList SEMICOLON
  ;
functionPrefix
  : returnType? identifier
  ;

// 20.2 Lexical Rules
// 20.1.1 Reserved Words
//assert, break, case, catch, class, const, continue, default, do, else,
//enum, extends, false, final, finally, for, if, in, is, new, null, rethrow,
//return, super, switch, this, throw, true, try, var, void, while, with.
ABSTRACT: 'abstract' ;
AS: 'as' ;
COVARIANT: 'covariant' ;
DEFERRED: 'deferred' ;
DYNAMIC: 'dynamic' ;
EXPORT: 'export' ;
EXTERNAL: 'external' ;
FACTORY: 'factory' ;
FUNCTION: 'Function' ;
GET: 'get' ;
IMPLEMENTS: 'implements' ;
IMPORT: 'import' ;
INTERFACE: 'interface' ;
LIBRARY: 'library' ;
OPERATOR: 'operator' ;
MIXIN: 'mixin' ;
PART: 'part' ;
SET: 'set' ;
STATIC: 'static' ;
TYPEDEF: 'typedef' ;

//BUILT_IN_IDENTIFIER
//  : ABSTRACT
//  | AS
//  | COVARIANT
//  | DEFERRED
//  | DYNAMIC
//  | EXPORT
//  | EXTERNAL
//  | FACTORY
//  | FUNCTION
//  | GET
//  | IMPLEMENTS
//  | IMPORT
//  | INTERFACE
//  | LIBRARY
//  | OPERATOR
//  | MIXIN
//  | PART
//  | SET
//  | STATIC
//  | TYPEDEF
//  ;

fragment
IDENTIFIER_NO_DOLLAR
  : IDENTIFIER_START_NO_DOLLAR
    IDENTIFIER_PART_NO_DOLLAR*
  ;
IDENTIFIER
  : IDENTIFIER_START IDENTIFIER_PART*
  ;

fragment
IDENTIFIER_START
  : IDENTIFIER_START_NO_DOLLAR
  | '$'
  ;
fragment
IDENTIFIER_START_NO_DOLLAR
  : LETTER
  | '_'
  ;
fragment
IDENTIFIER_PART_NO_DOLLAR
  : IDENTIFIER_START_NO_DOLLAR
  | DIGIT
  ;
fragment
IDENTIFIER_PART
  : IDENTIFIER_START
  | DIGIT
  ;

// 20.1.1 Reserved Words
fragment
LETTER
  : [a-z]
  | [A-Z]
  ;
fragment
DIGIT
  : [0-9]
  ;
// 20.1.2 Comments
SINGLE_LINE_COMMENT
//  : '//' ~(NEWLINE)* (NEWLINE)? // Origin Syntax
  : '//' ~[\r\n]* -> skip
  ;
MULTI_LINE_COMMENT
//  : '/*' (MULTI_LINE_COMMENT | ~'*/')* '*/' // Origin Syntax
  : '/*' .*? '*/' -> skip
  ;


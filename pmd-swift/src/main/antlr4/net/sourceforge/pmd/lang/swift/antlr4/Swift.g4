// Downloaded on 2016/03/02 from https://github.com/sleekbyte/tailor/blob/master/src/main/antlr/com/sleekbyte/tailor/antlr/Swift.g4

/*
 * [The "BSD license"]
 *  Copyright (c) 2014 Terence Parr
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Converted from Apple's doc, http://tinyurl.com/n8rkoue, to ANTLR's
 * meta-language.
 */
grammar Swift;

topLevel : (statement | expression)* EOF ;

// Statements

// GRAMMAR OF A STATEMENT

statement
 : declaration ';'?
 | loopStatement ';'?
 | branchStatement ';'?
 | labeledStatement
 | controlTransferStatement ';'?
 | deferStatement ';' ?
 | doStatement ':'?
 | compilerControlStatement ';'?
 | expression ';'?  // Keep expression last to handle ambiguity
 ;

statements : statement+ ;

// GRAMMAR OF A LOOP STATEMENT

loopStatement : forStatement
 | forInStatement
 | whileStatement
 | repeatWhileStatement
 ;

// GRAMMAR OF A FOR STATEMENT

// Swift Language Reference has expression? instead of expressionList?
forStatement
 : 'for' forInit? ';' expression? ';' expressionList? codeBlock
 | 'for' '(' forInit?';' expression? ';' expressionList? ')' codeBlock
 ;

forInit : variableDeclaration | expressionList  ;

// GRAMMAR OF A FOR_IN STATEMENT

forInStatement : 'for' 'case'? pattern 'in' expression whereClause? codeBlock  ;

// GRAMMAR OF A WHILE STATEMENT

whileStatement : 'while' conditionClause codeBlock  ;

// GRAMMAR OF A REPEAT WHILE STATEMENT

repeatWhileStatement: 'repeat' codeBlock 'while' conditionClause ;

// GRAMMAR OF A BRANCH STATEMENT

branchStatement : ifStatement | guardStatement | switchStatement  ;

// GRAMMAR OF AN IF STATEMENT

ifStatement : 'if' conditionClause codeBlock elseClause? ;
elseClause : 'else' codeBlock | 'else' ifStatement  ;

// GRAMMAR OF A GUARD STATEMENT

guardStatement : 'guard' conditionClause 'else' codeBlock ;

// GRAMMAR OF A SWITCH STATEMENT

switchStatement : 'switch' expression '{' switchCases? '}'  ;
switchCases : switchCase switchCases? ;
switchCase : caseLabel statements | defaultLabel statements  | caseLabel ';' | defaultLabel ';'  ;
caseLabel : 'case' caseItemList ':' ;
caseItemList : caseItem (',' caseItem)* ;
caseItem: pattern whereClause? ;
defaultLabel : 'default' ':' ;

// GRAMMAR OF A LABELED STATEMENT

labeledStatement : statementLabel loopStatement | statementLabel switchStatement  ;
statementLabel : labelName ':' ;
labelName : identifier  ;

// GRAMMAR OF A CONTROL TRANSFER STATEMENT

controlTransferStatement : breakStatement
 | continueStatement
 | fallthroughStatement
 | returnStatement
 | throwStatement
 ;

// GRAMMAR OF A BREAK STATEMENT

breakStatement : 'break' labelName? ;

// GRAMMAR OF A CONTINUE STATEMENT

continueStatement : 'continue' labelName? ;

// GRAMMAR OF A FALLTHROUGH STATEMENT

fallthroughStatement : 'fallthrough'  ;

// GRAMMAR OF A RETURN STATEMENT

returnStatement : 'return' expression? ;

// GRAMMAR OF A THROW STATEMENT

throwStatement : 'throw' expression ;

// GRAMMAR OF A DEFER STATEMENT

deferStatement: 'defer' codeBlock ;

// GRAMMAR OF A DO STATEMENT

doStatement: 'do' codeBlock catchClauses? ;
catchClauses: catchClause catchClauses? ;
catchClause: 'catch' pattern? whereClause? codeBlock ;

// GRAMMAR FOR CONDITION CLAUSES

conditionClause : expression
 | expression ',' conditionList
 | conditionList
 | availabilityCondition ',' expression
 ;

conditionList : condition (',' condition)* ;
condition: availabilityCondition | caseCondition | optionalBindingCondition ;
caseCondition: 'case' pattern initializer whereClause? ;
// optionalBindingCondition is incorrect in the Swift Language Reference (missing a ',')
optionalBindingCondition: optionalBindingHead (',' optionalBindingContinuationList)? whereClause? ;
optionalBindingHead: 'let' pattern initializer | 'var' pattern initializer ;
optionalBindingContinuationList: optionalBindingContinuation (',' optionalBindingContinuation)* ;
optionalBindingContinuation: optionalBindingHead | pattern initializer ;

whereClause: 'where' whereExpression ;
whereExpression: expression ;

// GRAMMAR OF AN AVAILABILITY CONDITION

availabilityCondition: '#available' '(' availabilityArguments ')' ;
availabilityArguments: availabilityArgument (',' availabilityArguments)* ;
availabilityArgument: platformName platformVersion | '*' ;
platformName: 'iOS' | 'iOSApplicationExtension' | 'OSX' | 'OSXApplicationExtension' | 'watchOS'
 | 'watchOSApplicationExtension' | 'tvOS' | 'tvOSApplicationExtension' ;
platformVersion: VersionLiteral | DecimalLiteral | FloatingPointLiteral ; // TODO: Find a way to make this only VersionLiteral

// Generic Parameters and Arguments

// GRAMMAR OF A GENERIC PARAMETER CLAUSE

genericParameterClause : '<' genericParameterList requirementClause? '>'  ;
genericParameterList : genericParameter (',' genericParameter)*  ;
genericParameter : typeName | typeName ':' typeIdentifier | typeName ':' protocolCompositionType  ;
requirementClause : 'where' requirementList  ;
requirementList : requirement (',' requirement)*  ;
requirement : conformanceRequirement | sameTypeRequirement  ;
conformanceRequirement : typeIdentifier ':' typeIdentifier | typeIdentifier ':' protocolCompositionType  ;
sameTypeRequirement : typeIdentifier '==' sType  ;

// GRAMMAR OF A GENERIC ARGUMENT CLAUSE

genericArgumentClause : '<' genericArgumentList '>'  ;
genericArgumentList : genericArgument (',' genericArgument)* ;
genericArgument : sType  ;

// Declarations

// GRAMMAR OF A DECLARATION

declaration
 : importDeclaration ';'?
 | constantDeclaration ';'?
 | variableDeclaration ';'?
 | typealiasDeclaration ';'?
 | functionDeclaration ';'?
 | enumDeclaration ';'?
 | structDeclaration ';'?
 | classDeclaration ';'?
 | protocolDeclaration ';'?
 | initializerDeclaration ';'?
 | deinitializerDeclaration ';'?
 | extensionDeclaration ';'?
 | subscriptDeclaration ';'?
 | operatorDeclaration ';'?
 // compiler-control-statement not in Swift Language Reference
 | compilerControlStatement ';'?
 ;

declarations : declaration declarations? ;
declarationModifiers : declarationModifier declarationModifiers? ;
declarationModifier : 'class' | 'convenience' | 'dynamic' | 'final' | 'infix'
 | 'lazy' | 'mutating' | 'nonmutating' | 'optional' | 'override' | 'postfix'
 | 'prefix' | 'required' | 'static' | 'unowned' | 'unowned' '(' 'safe' ')'
 | 'unowned' '(' 'unsafe' ')' | 'weak'
 | accessLevelModifier ;

accessLevelModifier : 'internal' | 'internal' '(' 'set' ')'
 | 'private' | 'private' '(' 'set' ')'
 | 'public' | 'public' '(' 'set' ')' ;
accessLevelModifiers : accessLevelModifier accessLevelModifiers? ;

// GRAMMAR OF A CODE BLOCK

codeBlock : '{' statements? '}'  ;

// GRAMMAR OF AN IMPORT DECLARATION

importDeclaration : attributes? 'import' importKind? importPath  ;
importKind : 'typealias' | 'struct' | 'class' | 'enum' | 'protocol' | 'var' | 'func'  ;
importPath : importPathIdentifier | importPathIdentifier '.' importPath  ;
importPathIdentifier : identifier | operator  ;

// GRAMMAR OF A CONSTANT DECLARATION

constantDeclaration : attributes? declarationModifiers? 'let' patternInitializerList  ;
patternInitializerList : patternInitializer (',' patternInitializer)* ;
patternInitializer : pattern initializer? ;
initializer : '=' expression  ;

// GRAMMAR OF A VARIABLE DECLARATION

variableDeclaration
 : variableDeclarationHead variableName typeAnnotation getterSetterBlock
 | variableDeclarationHead variableName typeAnnotation getterSetterKeywordBlock
 | variableDeclarationHead variableName initializer willSetDidSetBlock
 | variableDeclarationHead variableName typeAnnotation initializer? willSetDidSetBlock
 // keep this below getter and setter rules for ambiguity reasons
 | variableDeclarationHead variableName typeAnnotation codeBlock
 | variableDeclarationHead patternInitializerList
 ;

variableDeclarationHead : attributes? declarationModifiers? 'var'  ;
variableName : identifier  ;
getterSetterBlock : '{' getterClause setterClause?'}'  | '{' setterClause getterClause '}'  ;
getterClause : attributes? 'get' codeBlock  ;
setterClause : attributes? 'set' setterName? codeBlock  ;
setterName : '(' identifier ')'  ;
getterSetterKeywordBlock : '{' getterKeywordClause setterKeywordClause?'}' | '{' setterKeywordClause getterKeywordClause '}'  ;
getterKeywordClause : attributes? 'get'  ;
setterKeywordClause : attributes? 'set'  ;
willSetDidSetBlock : '{' willSetClause didSetClause?'}' | '{' didSetClause willSetClause? '}'  ;
willSetClause : attributes? 'willSet' setterName? codeBlock  ;
didSetClause : attributes? 'didSet' setterName? codeBlock  ;

// GRAMMAR OF A TYPE ALIAS DECLARATION

typealiasDeclaration : typealiasHead typealiasAssignment  ;
typealiasHead : attributes? accessLevelModifier? 'typealias' typealiasName  ;
typealiasName : identifier  ;
typealiasAssignment : '=' sType  ;

// GRAMMAR OF A FUNCTION DECLARATION

/* HACK: functionBody? is intentionally not used to force the parser to try and match a functionBody first
 * This can be removed once we figure out how to enforce that statements are either separated by semi colons or new line characters
 */
functionDeclaration : functionHead functionName genericParameterClause? functionSignature functionBody
 | functionHead functionName genericParameterClause? functionSignature
 ;

functionHead : attributes? declarationModifiers? 'func'  ;
functionName : identifier |  operator  ;
// rethrows is not marked as optional in the Swift Language Reference
functionSignature : parameterClauses ('throws' | 'rethrows')? functionResult? ;
functionResult : '->' attributes? sType  ;
functionBody : codeBlock  ;
parameterClauses : parameterClause parameterClauses? ;
parameterClause : '(' ')' |  '(' parameterList '...'? ')'  ;
parameterList : parameter (',' parameter)*  ;
// Parameters don't have attributes in the Swift Language Reference
parameter : attributes? 'inout'? 'let'? '#'? externalParameterName? localParameterName typeAnnotation? defaultArgumentClause?
 | 'inout'? 'var' '#'? externalParameterName? localParameterName typeAnnotation? defaultArgumentClause?
 | attributes? sType
 | externalParameterName? localParameterName typeAnnotation '...'
 ;
externalParameterName : identifier | '_'  ;
localParameterName : identifier | '_'  ;
defaultArgumentClause : '=' expression  ;

// GRAMMAR OF AN ENUMERATION DECLARATION

enumDeclaration : attributes? accessLevelModifier? enumDef  ;
enumDef: unionStyleEnum | rawValueStyleEnum  ;
unionStyleEnum : 'indirect'? 'enum' enumName genericParameterClause? typeInheritanceClause? '{' unionStyleEnumMembers?'}'  ;
unionStyleEnumMembers : unionStyleEnumMember unionStyleEnumMembers? ;
unionStyleEnumMember : declaration | unionStyleEnumCaseClause ';'? ;
unionStyleEnumCaseClause : attributes? 'indirect'? 'case' unionStyleEnumCaseList  ;
unionStyleEnumCaseList : unionStyleEnumCase (',' unionStyleEnumCase)*  ;
unionStyleEnumCase : enumCaseName tupleType? ;
enumName : identifier  ;
enumCaseName : identifier  ;
// typeInheritanceClause is not optional in the Swift Language Reference
rawValueStyleEnum : 'enum' enumName genericParameterClause? typeInheritanceClause? '{' rawValueStyleEnumMembers?'}'  ;
rawValueStyleEnumMembers : rawValueStyleEnumMember rawValueStyleEnumMembers? ;
rawValueStyleEnumMember : declaration | rawValueStyleEnumCaseClause  ;
rawValueStyleEnumCaseClause : attributes? 'case' rawValueStyleEnumCaseList  ;
rawValueStyleEnumCaseList : rawValueStyleEnumCase (',' rawValueStyleEnumCase)*   ;
rawValueStyleEnumCase : enumCaseName rawValueAssignment? ;
rawValueAssignment : '=' literal  ;

// GRAMMAR OF A STRUCTURE DECLARATION

structDeclaration : attributes? accessLevelModifier? 'struct' structName genericParameterClause? typeInheritanceClause? structBody  ;
structName : identifier  ;
structBody : '{' declarations?'}'  ;

// GRAMMAR OF A CLASS DECLARATION

// declarationModifier missing in Swift Language Reference
classDeclaration : attributes? declarationModifier* 'class' className genericParameterClause? typeInheritanceClause? classBody  ;
className : identifier ;
classBody : '{' declarations? '}'  ;

// GRAMMAR OF A PROTOCOL DECLARATION

protocolDeclaration : attributes? accessLevelModifier? 'protocol' protocolName typeInheritanceClause? protocolBody  ;
protocolName : identifier  ;
protocolBody : '{' protocolMemberDeclarations?'}'  ;
protocolMemberDeclaration : protocolPropertyDeclaration ';'?
 | protocolMethodDeclaration ';'?
 | protocolInitializerDeclaration ';'?
 | protocolSubscriptDeclaration ';'?
 | protocolAssociatedTypeDeclaration ';'?
 ;
protocolMemberDeclarations : protocolMemberDeclaration protocolMemberDeclarations? ;

// GRAMMAR OF A PROTOCOL PROPERTY DECLARATION

protocolPropertyDeclaration : variableDeclarationHead variableName typeAnnotation getterSetterKeywordBlock  ;

// GRAMMAR OF A PROTOCOL METHOD DECLARATION

protocolMethodDeclaration : functionHead functionName genericParameterClause? functionSignature  ;

// GRAMMAR OF A PROTOCOL INITIALIZER DECLARATION

protocolInitializerDeclaration : initializerHead genericParameterClause? parameterClause ('throws' | 'rethrows')? ;

// GRAMMAR OF A PROTOCOL SUBSCRIPT DECLARATION

protocolSubscriptDeclaration : subscriptHead subscriptResult getterSetterKeywordBlock  ;

// GRAMMAR OF A PROTOCOL ASSOCIATED TYPE DECLARATION

protocolAssociatedTypeDeclaration : typealiasHead typeInheritanceClause? typealiasAssignment? ;

// GRAMMAR OF AN INITIALIZER DECLARATION

initializerDeclaration : initializerHead genericParameterClause? parameterClause ('throws' | 'rethrows')? initializerBody  ;
initializerHead : attributes? declarationModifiers? 'init' ('?' | '!')?  ;
initializerBody : codeBlock  ;

// GRAMMAR OF A DEINITIALIZER DECLARATION

deinitializerDeclaration : attributes? 'deinit' codeBlock  ;

// GRAMMAR OF AN EXTENSION DECLARATION

// attributes, requirementClause missing in the Swift Language Reference
extensionDeclaration : attributes? accessLevelModifier? 'extension' typeIdentifier requirementClause? typeInheritanceClause? extensionBody  ;
extensionBody : '{' declarations?'}'  ;

// GRAMMAR OF A SUBSCRIPT DECLARATION

subscriptDeclaration : subscriptHead subscriptResult getterSetterBlock
 | subscriptHead subscriptResult getterSetterKeywordBlock
 // most general form of subscript declaration; should be kept at the bottom.
 | subscriptHead subscriptResult codeBlock
 ;
subscriptHead : attributes? declarationModifiers? 'subscript' parameterClause  ;
subscriptResult : '->' attributes? sType  ;

// GRAMMAR OF AN OPERATOR DECLARATION

operatorDeclaration : prefixOperatorDeclaration | postfixOperatorDeclaration | infixOperatorDeclaration  ;
prefixOperatorDeclaration : 'prefix' 'operator' operator '{' '}'  ;
postfixOperatorDeclaration : 'postfix' 'operator' operator '{' '}'  ;
infixOperatorDeclaration : 'infix' 'operator' operator '{' infixOperatorAttributes '}'  ;
// Order of clauses can be reversed, not indicated in Swift Language Reference
infixOperatorAttributes : precedenceClause? associativityClause? | associativityClause? precedenceClause? ;
precedenceClause : 'precedence' precedenceLevel  ;
precedenceLevel : integerLiteral ;
associativityClause : 'associativity' associativity  ;
associativity : 'left' | 'right' | 'none'  ;

// Patterns


// GRAMMAR OF A PATTERN

pattern
 : wildcardPattern typeAnnotation?
 | identifierPattern typeAnnotation?
 | valueBindingPattern
 | tuplePattern typeAnnotation?
 | enumCasePattern
 | 'is' sType
 | pattern 'as' sType
 | expressionPattern
 ;

// GRAMMAR OF A WILDCARD PATTERN

wildcardPattern : '_'  ;

// GRAMMAR OF AN IDENTIFIER PATTERN

identifierPattern : identifier  ;

// GRAMMAR OF A VALUE_BINDING PATTERN

valueBindingPattern : 'var' pattern | 'let' pattern  ;

// GRAMMAR OF A TUPLE PATTERN

tuplePattern : '(' tuplePatternElementList? ')'  ;
tuplePatternElementList
	:	tuplePatternElement (',' tuplePatternElement)*
	;
tuplePatternElement : pattern  ;

// GRAMMAR OF AN ENUMERATION CASE PATTERN

// Swift Language Reference has '.' as mandatory
enumCasePattern : typeIdentifier? '.'? enumCaseName tuplePattern? ;

// GRAMMAR OF A TYPE CASTING PATTERN

typeCastingPattern : isPattern | asPattern  ;
isPattern : 'is' sType  ;
asPattern : pattern 'as' sType  ;

// GRAMMAR OF AN EXPRESSION PATTERN

expressionPattern : expression  ;

// Attributes

// GRAMMAR OF AN ATTRIBUTE

attribute : '@' attributeName attributeArgumentClause? ;
attributeName : identifier ;
attributeArgumentClause : '('  balancedTokens?  ')'  ;
attributes : attribute+ ;
// Swift Language Reference does not have ','
balancedTokens : balancedToken balancedTokens? ;
balancedToken
 : '('  balancedTokens? ')'
 | '[' balancedTokens? ']'
 | '{' balancedTokens? '}'
 | identifier | expression | contextSensitiveKeyword | literal | operator
 // | Any punctuation except ( ,  ')' , '[' , ']' , { , or }
 // Punctuation is very ambiguous, interpreting punctuation as defined in www.thepunctuationguide.com
 | ':' | ';' | ',' | '!' | '<' | '>' | '-' | '\'' | '/' | '...' | '"'
 ;

// Expressions

// GRAMMAR OF AN EXPRESSION

expressionList : expression (',' expression)* ;

expression : tryOperator? prefixExpression binaryExpression* ;

prefixExpression
  : prefixOperator? postfixExpression ';'?
  | inOutExpression
  ;

/*
expression
	:	prefixOperator expression
    |	inOutExpression
    |	primaryExpression
    |	expression binaryOperator expression
    |	expression assignmentOperator expression
    |	expression conditionalOperator expression
    |	expression typeCastingOperator
    |	expression postfixOperator
    |	expression parenthesizedExpression trailingClosure?
	|	expression '.' 'init'
 	|	expression '.' DecimalLiteral
	|	expression '.' identifier genericArgumentClause?
	|	expression '.' 'self'
	|	expression '.' 'dynamicType'
	|	expression '[' expressionList ']'
	|	expression '!'
	|	expression '?'
	;
*/

// GRAMMAR OF A PREFIX EXPRESSION

inOutExpression : '&' identifier ;

// GRAMMAR OF A TRY EXPRESSION

tryOperator : 'try' ('?' | '!')? ;

// GRAMMAR OF A BINARY EXPRESSION

binaryExpression
  : binaryOperator prefixExpression
  | assignmentOperator tryOperator? prefixExpression
  | conditionalOperator tryOperator? prefixExpression
  | typeCastingOperator
  ;

// GRAMMAR OF AN ASSIGNMENT OPERATOR

assignmentOperator : '='  ;

// GRAMMAR OF A CONDITIONAL OPERATOR

conditionalOperator : '?' tryOperator? expression ':' ;

// GRAMMAR OF A TYPE_CASTING OPERATOR

typeCastingOperator
  : 'is' sType
  | 'as' '?' sType
  | 'as' sType
  | 'as' '!' sType
  ;

// GRAMMAR OF A PRIMARY EXPRESSION

primaryExpression
 : (identifier | operator) genericArgumentClause? // operator not mentioned in the Swift Language Reference
 | literalExpression
 | selfExpression
 | superclassExpression
 | closureExpression
 | parenthesizedExpression
 | implicitMemberExpression
// | implicit_member_expression disallow as ambig with explicit member expr in postfix_expression
 | wildcardExpression
 ;

// GRAMMAR OF A LITERAL EXPRESSION

literalExpression
 : literal
 | arrayLiteral
 | dictionaryLiteral
 | '__FILE__' | '__LINE__' | '__COLUMN__' | '__FUNCTION__'
 ;

arrayLiteral : '[' arrayLiteralItems? ']'  ;
arrayLiteralItems : arrayLiteralItem (',' arrayLiteralItem)* ','?  ;
arrayLiteralItem : expression ;
dictionaryLiteral : '[' dictionaryLiteralItems ']' | '[' ':' ']'  ;
dictionaryLiteralItems : dictionaryLiteralItem (',' dictionaryLiteralItem)* ','? ;
dictionaryLiteralItem : expression ':' expression  ;

// GRAMMAR OF A SELF EXPRESSION

selfExpression
 : 'self'
 | 'self' '.' identifier
 | 'self' '[' expressionList ']'
 | 'self' '.' 'init'
 ;

// GRAMMAR OF A SUPERCLASS EXPRESSION

superclassExpression
  : superclassMethodExpression
  | superclassSubscriptExpression
  | superclassInitializerExpression
  ;

superclassMethodExpression : 'super' '.' identifier  ;
superclassSubscriptExpression : 'super' '[' expressionList ']'  ;
superclassInitializerExpression : 'super' '.' 'init'  ;

// GRAMMAR OF A CLOSURE EXPRESSION

// Statements are not optional in the Swift Language Reference
closureExpression : '{' closureSignature? statements? '}'  ;
closureSignature
 : parameterClause functionResult? 'in'
 | identifierList functionResult? 'in'
 | captureList parameterClause functionResult? 'in'
 | captureList identifierList functionResult? 'in'
 | captureList 'in'
 ;

captureList : '[' captureListItems ']' ;
captureListItems: captureListItem (',' captureListItem)? ;
captureListItem: captureSpecifier? expression ;
captureSpecifier : 'weak' | 'unowned' | 'unowned(safe)' | 'unowned(unsafe)'  ;

// GRAMMAR OF A IMPLICIT MEMBER EXPRESSION

implicitMemberExpression : '.' identifier  ;

// GRAMMAR OF A PARENTHESIZED EXPRESSION

parenthesizedExpression : '(' expressionElementList? ')'  ;
expressionElementList : expressionElement (',' expressionElement)*  ;
expressionElement : expression | identifier ':' expression  ;

// GRAMMAR OF A WILDCARD EXPRESSION

wildcardExpression : '_'  ;

// GRAMMAR OF A POSTFIX EXPRESSION

postfixExpression
 : primaryExpression                                             # primary
 | postfixExpression postfixOperator                             # postfixOperation
 // Function call with closure expression should always be above a lone parenthesized expression to reduce ambiguity
 | postfixExpression parenthesizedExpression? closureExpression  # functionCallWithClosureExpression
 | postfixExpression parenthesizedExpression                     # functionCallExpression
 | postfixExpression '.' 'init'                                  # initializerExpression
 // TODO: don't allow '_' here in DecimalLiteral:
 | postfixExpression '.' DecimalLiteral                         # explicitMemberExpression1
 | postfixExpression '.' identifier genericArgumentClause?     # explicitMemberExpression2
 | postfixExpression '.' 'self'                                  # postfixSelfExpression
 | postfixExpression '.' 'dynamicType'                           # dynamicTypeExpression
 | postfixExpression '[' expressionList ']'                     # subscriptExpression
 | postfixExpression '!'                                # forcedValueExpression
 | postfixExpression '?'                                         # optionalChainingExpression
 ;

// GRAMMAR OF A FUNCTION CALL EXPRESSION

/*
functionCallExpression
  : postfixExpression parenthesizedExpression
  : postfixExpression parenthesizedExpression? trailingClosure
  ;
  */

//trailing_closure : closure_expression  ;

//initializer_expression : postfix_expression '.' 'init' ;

/*explicitMemberExpression
  : postfixExpression '.' DecimalLiteral // TODO: don't allow '_' here in DecimalLiteral
  | postfixExpression '.' identifier genericArgumentClause?
  ;
  */

//postfix_self_expression : postfix_expression '.' 'self' ;

// GRAMMAR OF A DYNAMIC TYPE EXPRESSION

//dynamic_type_expression : postfix_expression '.' 'dynamicType' ;

// GRAMMAR OF A SUBSCRIPT EXPRESSION

//subscript_expression : postfix_expression '[' expression_list ']' ;

// GRAMMAR OF A FORCED_VALUE EXPRESSION

//forced_value_expression : postfix_expression '!' ;

// GRAMMAR OF AN OPTIONAL_CHAINING EXPRESSION

//optional_chaining_expression : postfix_expression '?' ;

// GRAMMAR OF OPERATORS

// split the operators out into the individual tokens as some of those tokens
// are also referenced individually. For example, type signatures use
// <...>.

operatorHead: '=' | '<' | '>' | '!' | '*' | '&' | '==' | '?' | '-' | '&&' | '||' | '/' | OperatorHead;
operatorCharacter: operatorHead | OperatorCharacter;

operator: operatorHead operatorCharacter*
  | '..' (operatorCharacter)*
  | '...'
  ;

// WHITESPACE scariness:

/* http://tinyurl.com/oalzfus
"If an operator has no whitespace on the left but is followed
immediately by a dot (.), it is treated as a postfix unary
operator. As an example, the ++ operator in a++.b is treated as a
postfix unary operator (a++ . b rather than a ++ .b).  For the
purposes of these rules, the characters (, [, and { before an
operator, the characters ), ], and } after an operator, and the
characters ,, ;, and : are also considered whitespace.

There is one caveat to the rules above. If the ! or ? operator has no
whitespace on the left, it is treated as a postfix operator,
regardless of whether it has whitespace on the right. To use the ?
operator as syntactic sugar for the Optional type, it must not have
whitespace on the left. To use it in the conditional (? :) operator,
it must have whitespace around both sides."
 */

/**
 "If an operator has whitespace around both sides or around neither side,
 it is treated as a binary operator. As an example, the + operator in a+b
  and a + b is treated as a binary operator."
*/
binaryOperator : operator ;

/**
 "If an operator has whitespace on the left side only, it is treated as a
 prefix unary operator. As an example, the ++ operator in a ++b is treated
 as a prefix unary operator."
*/
prefixOperator : operator  ; // only if space on left but not right

/**
 "If an operator has whitespace on the right side only, it is treated as a
 postfix unary operator. As an example, the ++ operator in a++ b is treated
 as a postfix unary operator."
 */
postfixOperator : operator  ;

// Types

// GRAMMAR OF A TYPE

sType
 : arrayType
 | dictionaryType
 | sType 'throws'? '->' sType  // function-type
 | sType 'rethrows' '->' sType // function-type
 | typeIdentifier
 | tupleType
 | sType '?'  // optional-type
 | sType '!'  // implicitly-unwrapped-optional-type
 | protocolCompositionType
 | sType '.' 'Type' | sType '.' 'Protocol' // metatype
 ;

arrayType: '[' sType ']' ;

dictionaryType: '[' sType ':' sType ']' ;

optionalType: sType '?' ;

implicitlyUnwrappedOptionalType: sType '!' ;

// GRAMMAR OF A TYPE ANNOTATION

typeAnnotation : ':' attributes? sType  ;

// GRAMMAR OF A TYPE IDENTIFIER

typeIdentifier
 : typeName genericArgumentClause?
 | typeName genericArgumentClause? '.' typeIdentifier
 | 'Self' // Swift Language Reference does not have this
 ;

typeName : identifier ;

// GRAMMAR OF A TUPLE TYPE

tupleType : '('  tupleTypeBody? ')'  ;
tupleTypeBody : tupleTypeElementList '...'? ;
tupleTypeElementList : tupleTypeElement | tupleTypeElement ',' tupleTypeElementList  ;
tupleTypeElement : attributes? 'inout'? sType | 'inout'? elementName typeAnnotation ;
elementName : identifier  ;

// GRAMMAR OF A PROTOCOL COMPOSITION TYPE

protocolCompositionType : 'protocol' '<' protocolIdentifierList? '>'  ;
protocolIdentifierList : protocolIdentifier | protocolIdentifier ',' protocolIdentifierList  ;
protocolIdentifier : typeIdentifier  ;

// GRAMMAR OF A METATYPE TYPE

metatypeType : sType '.' 'Type' | sType '.' 'Protocol';

// GRAMMAR OF A TYPE INHERITANCE CLAUSE

typeInheritanceClause : ':' classRequirement ',' typeInheritanceList
 | ':' classRequirement
 | ':' typeInheritanceList
 ;
typeInheritanceList : typeIdentifier (',' typeIdentifier)* ;
classRequirement: 'class' ;

// ------ Build Configurations (Macros) -------

compilerControlStatement: buildConfigurationStatement | lineControlStatement ;
buildConfigurationStatement: '#if' buildConfiguration statements? buildConfigurationElseIfClauses? buildConfigurationElseClause? '#endif' ;
buildConfigurationElseIfClauses: buildConfigurationElseIfClause+ ;
buildConfigurationElseIfClause: '#elseif' buildConfiguration statements? ;
buildConfigurationElseClause: '#else' statements? ;

buildConfiguration: platformTestingFunction | identifier | booleanLiteral
 | '(' buildConfiguration ')'
 | '!' buildConfiguration
 | buildConfiguration ('&&' | '||') buildConfiguration
 ;

platformTestingFunction: 'os' '(' operatingSystem ')' | 'arch' '(' architecture ')' ;
operatingSystem: 'OSX' | 'iOS' | 'watchOS' | 'tvOS' ;
architecture: 'i386' | 'x86_64' | 'arm' | 'arm64' ;

lineControlStatement: '#line' (lineNumber fileName)? ;
lineNumber: integerLiteral ;
fileName: StringLiteral ;

// ---------- Lexical Structure -----------

BooleanLiteral: 'true' | 'false' ;
NilLiteral: 'nil' ;

// GRAMMAR OF AN IDENTIFIER

identifier : Identifier | contextSensitiveKeyword ;

keyword : 'convenience' | 'class' | 'deinit' | 'enum' | 'extension' | 'func' | 'import' | 'init' | 'let' | 'protocol' | 'static' | 'struct' | 'subscript' | 'typealias' | 'var' | 'break' | 'case' | 'continue' | 'default' | 'do' | 'else' | 'fallthrough' | 'if' | 'in' | 'for' | 'return' | 'switch' | 'where' | 'while' | 'as' | 'dynamicType' | 'is' | 'super' | 'self' | 'Self' | 'Type' | 'repeat' ;

contextSensitiveKeyword :
 'associativity' | 'convenience' | 'dynamic' | 'didSet' | 'final' | 'get' | 'infix' | 'indirect' |
 'lazy' | 'left' | 'mutating' | 'none' | 'nonmutating' | 'optional' | 'operator' | 'override' | 'postfix' | 'precedence' |
 'prefix' | 'Protocol' | 'required' | 'right' | 'set' | 'Type' | 'unowned' | 'weak' | 'willSet' |
 'iOS' | 'iOSApplicationExtension' | 'OSX' | 'OSXApplicationExtension-' | 'watchOS' | 'x86_64' |
 'arm' | 'arm64' | 'i386' | 'os' | 'arch'
 ;

OperatorHead
  : '/' | '=' | '-' | '+' | '!' | '*' | '%' | '<' | '>' | '&' | '|' | '^' | '~' | '?'
  | [\u00A1-\u00A7]
  | [\u00A9\u00AB\u00AC\u00AE]
  | [\u00B0-\u00B1\u00B6\u00BB\u00BF\u00D7\u00F7]
  | [\u2016-\u2017\u2020-\u2027]
  | [\u2030-\u203E]
  | [\u2041-\u2053]
  | [\u2055-\u205E]
  | [\u2190-\u23FF]
  | [\u2500-\u2775]
  | [\u2794-\u2BFF]
  | [\u2E00-\u2E7F]
  | [\u3001-\u3003]
  | [\u3008-\u3030]
  ;

OperatorCharacter
  : OperatorHead
  | [\u0300–\u036F]
  | [\u1DC0–\u1DFF]
  | [\u20D0–\u20FF]
  | [\uFE00–\uFE0F]
  | [\uFE20–\uFE2F]
  //| [\uE0100–\uE01EF]  ANTLR can't do >16bit char
  ;

DotOperatorHead
  : '..'
  ;

Identifier : IdentifierHead IdentifierCharacters?
 | '`' IdentifierHead IdentifierCharacters? '`'
 | ImplicitParameterName
 ;

identifierList : (identifier | '_') (',' (identifier | '_'))*  ;

fragment IdentifierHead : [a-zA-Z] | '_'
 | '\u00A8' | '\u00AA' | '\u00AD' | '\u00AF' | [\u00B2-\u00B5] | [\u00B7-\u00BA]
 | [\u00BC-\u00BE] | [\u00C0-\u00D6] | [\u00D8-\u00F6] | [\u00F8-\u00FF]
 | [\u0100-\u02FF] | [\u0370-\u167F] | [\u1681-\u180D] | [\u180F-\u1DBF]
 | [\u1E00-\u1FFF]
 | [\u200B-\u200D] | [\u202A-\u202E] | [\u203F-\u2040] | '\u2054' | [\u2060-\u206F]
 | [\u2070-\u20CF] | [\u2100-\u218F] | [\u2460-\u24FF] | [\u2776-\u2793]
 | [\u2C00-\u2DFF] | [\u2E80-\u2FFF]
 | [\u3004-\u3007] | [\u3021-\u302F] | [\u3031-\u303F] | [\u3040-\uD7FF]
 | [\uF900-\uFD3D] | [\uFD40-\uFDCF] | [\uFDF0-\uFE1F] | [\uFE30-\uFE44]
 | [\uFE47-\uFFFD]
/*
 | U+10000–U+1FFFD | U+20000–U+2FFFD | U+30000–U+3FFFD | U+40000–U+4FFFD
 | U+50000–U+5FFFD | U+60000–U+6FFFD | U+70000–U+7FFFD | U+80000–U+8FFFD
 | U+90000–U+9FFFD | U+A0000–U+AFFFD | U+B0000–U+BFFFD | U+C0000–U+CFFFD
 | U+D0000–U+DFFFD or U+E0000–U+EFFFD
*/
 ;

fragment IdentifierCharacter : [0-9]
 | [\u0300-\u036F] | [\u1DC0-\u1DFF] | [\u20D0-\u20FF] | [\uFE20-\uFE2F]
 | IdentifierHead
 ;

fragment IdentifierCharacters : IdentifierCharacter+ ;

ImplicitParameterName : '$' DecimalLiteral ; // TODO: don't allow '_' here

// GRAMMAR OF A LITERAL

booleanLiteral: BooleanLiteral ;
literal : numericLiteral | StringLiteral | BooleanLiteral | NilLiteral ;

// GRAMMAR OF AN INTEGER LITERAL

numericLiteral: '-'? integerLiteral | '-'? FloatingPointLiteral ;

integerLiteral
 : BinaryLiteral
 | OctalLiteral
 | DecimalLiteral
 | HexadecimalLiteral
 ;

BinaryLiteral : '0b' BinaryDigit BinaryLiteralCharacters? ;
fragment BinaryDigit : [01] ;
fragment BinaryLiteralCharacter : BinaryDigit | '_'  ;
fragment BinaryLiteralCharacters : BinaryLiteralCharacter BinaryLiteralCharacters? ;

OctalLiteral : '0o' OctalDigit OctalLiteralCharacters? ;
fragment OctalDigit : [0-7] ;
fragment OctalLiteralCharacter : OctalDigit | '_'  ;
fragment OctalLiteralCharacters : OctalLiteralCharacter+ ;

DecimalLiteral : DecimalDigit DecimalLiteralCharacters? ;
fragment DecimalDigit : [0-9] ;
fragment DecimalDigits : DecimalDigit+ ;
fragment DecimalLiteralCharacter : DecimalDigit | '_'  ;
fragment DecimalLiteralCharacters : DecimalLiteralCharacter+ ;
HexadecimalLiteral : '0x' HexadecimalDigit HexadecimalLiteralCharacters? ;
fragment HexadecimalDigit : [0-9a-fA-F] ;
fragment HexadecimalLiteralCharacter : HexadecimalDigit | '_'  ;
fragment HexadecimalLiteralCharacters : HexadecimalLiteralCharacter+ ;

// GRAMMAR OF A FLOATING_POINT LITERAL

FloatingPointLiteral
 : DecimalLiteral DecimalFraction? DecimalExponent?
 | HexadecimalLiteral HexadecimalFraction? HexadecimalExponent
 ;
fragment DecimalFraction : '.' DecimalLiteral ;
fragment DecimalExponent : FloatingPointE Sign? DecimalLiteral ;
fragment HexadecimalFraction : '.' HexadecimalLiteral? ;
fragment HexadecimalExponent : FloatingPointP Sign? HexadecimalLiteral ;
fragment FloatingPointE : [eE] ;
fragment FloatingPointP : [pP] ;
fragment Sign : [+\-] ;

VersionLiteral: DecimalLiteral DecimalFraction DecimalFraction ;

// GRAMMAR OF A STRING LITERAL

StringLiteral : '"' QuotedText? '"' ;
fragment QuotedText : QuotedTextItem QuotedText? ;
fragment QuotedTextItem : EscapedCharacter
// | '\\(' expression ')'
 | ~["\\\u000A\u000D]
 ;
EscapedCharacter : '\\' [0\\(tnr"']
 | '\\x' HexadecimalDigit HexadecimalDigit
 | '\\u' '{' HexadecimalDigit HexadecimalDigit? HexadecimalDigit? HexadecimalDigit? HexadecimalDigit? HexadecimalDigit? HexadecimalDigit? HexadecimalDigit? '}'
;

WS : [ \n\r\t\u000B\u000C\u0000] -> channel(HIDDEN) ;

/* Added optional newline character to prevent the whitespace lexer rule from matching newline
 * at the end of the comment. This affects how blank lines are counted around functions.
 */
BlockComment : '/*' (BlockComment|.)*? '*/' '\n'? -> channel(HIDDEN) ; // nesting allow

LineComment : '//' .*? ('\n'|EOF) -> channel(HIDDEN) ;

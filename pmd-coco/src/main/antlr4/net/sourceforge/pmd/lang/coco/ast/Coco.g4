////////////////////////////////////////////////////////////////////////////////
//// Coco 1.2 ANTLR4 grammar definition file
//// Reversed engineered by Dimitri van Heesch
///////////////////////////////////////////////////////////////////////////////

grammar Coco;

module  : (declaration)* EOF
	;

declaration
	: attribute*
	( importDeclaration
	| variableDeclaration
	| enumDeclaration
	| structDeclaration
	| typeAliasDeclaration
	| functionDeclaration
	| instanceDeclaration              // COCOTEC: this is not documented. Also class declaration is mentioned and traits?
	| portDeclaration
	| componentDeclaration
	| externalConstantDeclaration
	| externalFunctionDeclaration
	| externalTypeDeclaration
	| attributeDeclaration             // COCOTEC: this is not documented.
	)
	;

attribute
	: AT dotIdentifierList ( LP expressions RP )?
	;

attributeDeclaration
	: ATTRIBUTE AT dotIdentifierList ( LP parameters? RP )?
	;

importDeclaration
	: IMPORT UNQUALIFIED? dotIdentifierList (AS IDENTIFIER)?
	;

variableDeclaration
	: PRIVATE? (VAR | VAL) IDENTIFIER genericTypeDeclaration? (COLON type)?  (ASSIGN expression)?
	;

enumDeclaration
	: ENUM IDENTIFIER genericTypeDeclaration? LC enumElement* RC
	;

structDeclaration
	: STRUCT IDENTIFIER genericTypeDeclaration? LC structElement* RC
	;

typeAliasDeclaration
	: TYPE IDENTIFIER genericTypeDeclaration? ASSIGN type
	;

functionDeclaration
	: FUNCTION IDENTIFIER genericTypeDeclaration? LP parameters? RP COLON type ASSIGN expression
	;

instanceDeclaration
	: INSTANCE IDENTIFIER genericTypeDeclaration? LP parameters RP blockExpression_
	;

portDeclaration
	: PORT IDENTIFIER (COLON types)? FINAL? LC portElement* RC
	;

componentDeclaration
	: EXTERNAL? COMPONENT IDENTIFIER (COLON type)? LC componentElement* RC
	;

externalConstantDeclaration
	: EXTERNAL VAL IDENTIFIER COLON type ASSIGN expression
	;

externalTypeDeclaration
	: EXTERNAL TYPE IDENTIFIER ( LC externalTypeElement* RC )?
	;

externalTypeElement
	: staticMemberDeclaration
	| variableDeclaration
	| (MUT | MUTATING)? externalFunctionDeclaration
	;

externalFunctionDeclaration
	: EXTERNAL FUNCTION IDENTIFIER LP parameters? RP COLON type ASSIGN expression
	;

	      // COCOTEC: this is not documented at the places it can be applied. The generic section mentions it
	      // can be used for Function/Enum/Struct/Type. Seems also applicable to Instances
	      // Is the WHERE part correctly placed here, so should it be at genericType?
genericTypeDeclaration
	: LT genericTypes (WHERE expressions)? GT
	;

genericTypes
	: genericType ( COMMA genericType )*
	;

genericType
	: VAL? IDENTIFIER (COLON type)?
	;

enumElement
	: enumCase
	| (MUT | MUTATING)? functionDeclaration
	| staticMemberDeclaration
	;

enumCase
	: CASE IDENTIFIER ( LP caseParameters? RP )? (ASSIGN expression)?
	;

caseParameters
	: caseParameter (COMMA caseParameter)*
	;

caseParameter
	: IDENTIFIER COLON type
	;

structElement
	:
	(fieldDeclaration
	| (MUT | MUTATING)? functionDeclaration
	| staticMemberDeclaration
	) ';'?                                     // COCOTEC: the semicolon seems to be allowed here
	;

fieldDeclaration                                   // COCOTEC: the VAL/VAR seems optional in case a ; is added
	: (VAL | VAR)? IDENTIFIER COLON type ( ASSIGN expression )?
	;

componentElement                                   // COCOTEC: optional attributes are not documented
	: attribute*
	( fieldDeclaration
	| variableDeclaration
	| constructorDeclaration
	| stateMachineDeclaration
	| staticMemberDeclaration
	) ';'?                                     // COCOTEC: semi colon seems to be allowed?
	;

staticMemberDeclaration
	: STATIC VAL IDENTIFIER COLON type ASSIGN expression
	;

constructorDeclaration
	: INIT LP parameters? RP ASSIGN blockExpression_
	;

expression                                          // COCOTEC: what are the precedence/associativity rules?
	: literalExpression_                                              # LiteralExpression
	| EXTERNAL BACKTICK_LITERAL                                       # ExternalLiteral   // COCOTEC: this is not documented
	| externalFunctionDeclaration                                     # ExternalFunction  // COCOTEC: this is not documented
	| dotIdentifierList (LT genericTypes GT)?                         # VariableReferenceExpression
	| expression DOT IDENTIFIER                                       # MemberReferenceExpression
        | stateInvariant                                                  # StateInvariantExpression    // COCOTEC: this is not documented
	| expression LP expressions? RP                                   # CallExpression
	| expression LB expression RB                                     # ArraySubscriptExpression
	| expression LC fieldAssignments RC                               # StructLiteralExpression
	| expression QM                                                   # TryOperatorExpression
	| (MINUS | EXCL | AMP) expression                                   # UnaryOperatorExpression
	| expression AS type                                              # CastExpression
	| expression (MUL | DIV | MOD) expression                         # ArithmicOrLogicalExpression
	| expression (PLUS | MINUS) expression                              # ArithmicOrLogicalExpression
	| expression (EQ | NE | OR | AND | LT | LE | GT | GE) expression  # ArithmicOrLogicalExpression
	| expression ASSIGN expression                                    # AssignmentExpression
	| ifExpression_                                                   # IfExpression
	| matchExpression_                                                # MatchExpression
	| DOT IDENTIFIER                                                  # ImplicitMemberExpression
	| LP expressions RP                                               # GroupedExpression  // COCOTEC: this is not documented
	| LB expression RB                                                # ArrayLiteralExpression
	| nondetExpression_                                               # NondetExpression
	| OPTIONAL expression                                             # OptionalExpression
	| blockExpression_                                                # BlockExpression
	;

blockExpression_
	: LC statement* ( expression )? RC
	;

ifExpression_
	: IF LP expression RP expression ( ELSE expression )?
	;

matchExpression_                // COCOTEC: the COLON is missing in the documentation
	: (IDENTIFIER COLON)? MATCH LP expression RP LC matchClauses RC
	;

nondetExpression_
	: NONDET LC nondetClauses ( OTHERWISE expression )? (COMMA|SEMI)? RC
	;

fieldAssignments
	: fieldAssignment (COMMA fieldAssignment )* COMMA?
	;

fieldAssignment
	: IDENTIFIER ASSIGN expression
	;

nondetClauses
	: nondetClause ((COMMA|SEMI) nondetClause )* (COMMA|SEMI)?
	;

nondetClause
	: ( IF LP expression RP )? expression
	;

matchClauses      // COCOTEC: both comma and semicolon seem to be allowed (trailing one can be omited), only comma is documented
	: matchClause ((COMMA|SEMI) matchClause )* (COMMA|SEMI)?
	;

matchClause
	: pattern IMPL expression
	;

pattern : enumCasePattern
	| literalExpression_
	| variableDeclarationPattern
	//| variablePattern                  // this is already handled by enumCasePattern
	//| wildcardPattern                  // this is already handled by enumCasePattern
	;

enumCasePattern
	: idParameterPatterns ( IF LP expression RP )?
	;

idParameterPatterns
	: idParameterPattern (DOT idParameterPattern)*
	| DOT idParameterPattern (DOT idParameterPattern)*
	;

idParameterPattern
	: IDENTIFIER ( LP parameterPatterns? RP )?
	;

variableDeclarationPattern
	: (VAL | DOT) IDENTIFIER ( COLON type )?
	;

parameterPatterns
	: parameterPattern ( COMMA parameterPattern )*
	;

parameterPattern
	: VAR? IDENTIFIER
	;

expressions
	: expression ( COMMA expression )*
	;

statement
	: attribute*                       // COCOTEC: optional attributes are not documented
	( expression SEMI?
	| stateInvariant SEMI?             // COCOTEC: this rule is not documented
	| declarationStatement
	| returnStatement
	| becomeStatement
	| whileStatement
	| forStatement
	| breakStatement
	| continueStatement
	)
	;

declarationStatement
	: variableDeclaration SEMI
	;

returnStatement
	: RETURN expression SEMI
	;

becomeStatement
	: BECOME expression SEMI
	;

whileStatement
	: ( IDENTIFIER COLON)? WHILE LP expression RP blockExpression_
	;

forStatement
	: ( IDENTIFIER COLON)? FOR IDENTIFIER (COLON type)? IN expression blockExpression_
	;

breakStatement
	: BREAK IDENTIFIER? SEMI
	;

continueStatement
	: CONTINUE IDENTIFIER? SEMI
	;

portElement
	: attribute*                           // COCOTEC: optional attributes are not documented
	( enumDeclaration
        | functionInterfaceDeclaration
	| signalDeclaration
	| fieldDeclaration
	| stateMachineDeclaration
	| portDeclaration
	| staticMemberDeclaration
	| structDeclaration
	| typeAliasDeclaration
	| externalTypeDeclaration
	)
	;

functionInterfaceDeclaration
	: FUNCTION IDENTIFIER LP parameters? RP COLON type
	;

signalDeclaration
	: OUTGOING SIGNAL IDENTIFIER LP parameters? RP
	;

stateMachineDeclaration
	: MACHINE IDENTIFIER? (COLON IDENTIFIER)? LC stateMachineElement* RC
	;

stateMachineElement
	: attribute*                   // COCOTEC: optional attributes are not documented
	( enumDeclaration              // COCOTEC: this is missing in the documentation
	| entryFunctionDeclaration     // COCOTEC: this is missing in the documentation
	| exitFunctionDeclaration      // COCOTEC: this is missing in the documentation
	| functionDeclaration
	| stateInvariant               // COCOTEC: this is missing in the documentation
	| stateDeclaration
	| staticMemberDeclaration
	| typeAliasDeclaration         // COCOTEC: this is listed in the documentation (but no clickable)
	| variableDeclaration
	| transitionDeclaration        // COCOTEC: this is missing in the documentation
	)
	;

stateDeclaration
	: eventStateDeclaration
	| executionStateDeclaration
	;

eventStateDeclaration
	: STATE IDENTIFIER ( LP parameters? RP )? LC eventStateElement* RC
	;

executionStateDeclaration              // COCOTEC: the documentation lists explicit { } around a blockExpression which is wrong
	: EXECUTION STATE IDENTIFIER ( LP parameters? RP )? blockExpression_
	;

eventStateElement
	: attribute*                   // COCOTEC: optional attributes are not documented
	( enumDeclaration
	| entryFunctionDeclaration
	| exitFunctionDeclaration
	| functionDeclaration
	| stateDeclaration
	| stateInvariant
	| staticMemberDeclaration
	| structDeclaration
	| transitionDeclaration
	| typeAliasDeclaration
	| variableDeclaration
	)
	;

entryFunctionDeclaration
	: ENTRY LP RP ASSIGN expression
	;

exitFunctionDeclaration
	: EXIT LP RP ASSIGN expression
	;

stateInvariant
	: ASSERT LP expression RP
	;

transitionDeclaration
	: eventTransition
	| spontaneousTransition
	| timerTransition
	// | unusedTransition  // special case of eventTransition
	;

eventTransition          // COCOTEC: the (DOT IDENTIFIER)* part is not documented
	: (IF LP expression RP )? ( eventSource DOT)* dotIdentifierList LP parameters? RP ASSIGN eventHandler
	;

eventSource
	: IDENTIFIER (LB pattern (PIPE expression)? RB)?
	;

spontaneousTransition
	: (IF LP expression RP )? SPONTANEOUS ASSIGN expression
	;

timerTransition
	: (AFTER | PERIODIC) LP expression RP ASSIGN expression
	;

eventHandler
	: expression
	| ILLEGAL
	| offer
	;
                  // COCOTEC: the comma seems to be optional
offer	: OFFER LC offerClauses ( OTHERWISE eventHandler COMMA? )? RC
	;

                  // COCOTEC: a trailing comma seems to be optional
offerClauses
	: offerClause (COMMA offerClause)* COMMA?
	;

offerClause       // COCOTEC: the optional attributes are not documented
	: attribute* ( IF LP expression RP )? eventHandler
	;

parameters
	: parameter (COMMA parameter)*
	;

parameter         // COCOTEC: the optional ellipsis is not documented
	: VAR? IDENTIFIER ELLIP? (COLON type)?
	| IDENTIFIER genericTypeDeclaration
	;

literalExpression_
	: INTEGER
	| CHAR_LITERAL
	| STRING_LITERAL
	;

type	: type (MUL | DIV | MOD) type                          # BinaryType
	| type (PLUS | MINUS) type                               # BinaryType
	| LP type RP                                           # GroupType      // COCOTEC: this is not documented
	| dotIdentifierList (LT types GT)? (DOT IDENTIFIER)?   # TypeReference // also InstantiatedType / DependentMemberType
	| LP types RP ARROW type			       # FunctionType
	| literalExpression_                                   # LiteralType
	| AMP ( MUT | OUT)? type			       # ReferenceType
	| MINUS type                                             # UnaryType
	;

types   : type (COMMA type)*
	;

dotIdentifierList
	: IDENTIFIER (DOT IDENTIFIER )*
	;

////////////////////////// LEXER PART (order of rules is important) ///////////////////////////////////

// keywords
AFTER       : 'after';
AS          : 'as';
ASSERT      : 'assert';
ATTRIBUTE   : 'attribute';
BECOME      : 'become';
BREAK       : 'break';
CASE        : 'case';
COMPONENT   : 'component';
CONTINUE    : 'continue';
ELSE        : 'else';
ENUM	    : 'enum';
ENTRY       : 'entry';
EXECUTION   : 'execution';
EXIT        : 'exit';
EXTERNAL    : 'external';
FINAL       : 'final';
FOR         : 'for';
FUNCTION    : 'function';
IF          : 'if';
ILLEGAL     : 'illegal';
IMPORT      : 'import';
IN          : 'in';
INIT        : 'init';
INSTANCE    : 'instance';
MACHINE     : 'machine';
MATCH       : 'match';
MUT         : 'mut';
MUTATING    : 'mutating';
NONDET      : 'nondet';
OFFER       : 'offer';
OPTIONAL    : 'optional';
OTHERWISE   : 'otherwise';
OUT         : 'out';
OUTGOING    : 'outgoing';
PERIODIC    : 'periodic';
PORT        : 'port';
PRIVATE	    : 'private';
RETURN      : 'return';
SIGNAL      : 'signal';
SPONTANEOUS : 'spontaneous';
STATE       : 'state';
STATIC      : 'static';
STRUCT      : 'struct';
TYPE        : 'type';
UNQUALIFIED : 'unqualified';
VAL         : 'val';
VAR         : 'var';
WHERE       : 'where';
WHILE       : 'while';

// general non-keyword identifier
IDENTIFIER  : Identifier;

// symbols
AT    : '@';   // AT symbol
ASSIGN: '=';   // ASSIGNment operator
COLON : ':';   // COLON
LP    : '(';   // Left Parentheses
RP    : ')';   // Right Parentheses
LC    : '{';   // Left Curly bracket
RC    : '}';   // Right Curly bracket
LB    : '[';   // Left square Bracket
RB    : ']';   // Right square Bracket
COMMA : ',';   // COMMA
SEMI  : ';';   // SEMIcolon
DOT   : '.';   // DOT
LT    : '<';   // Less Than
GT    : '>';   // Greater Than
MUL   : '*';   // MULtiplication
DIV   : '/';   // DIVision
MINUS : '-';   // MINUS
MOD   : '%';   // MODulo
PLUS  : '+';   // PLUS
IMPL  : '=>';  // IMPLication
ARROW : '->';  // right arrow
AMP   : '&';   // AMPersand
QM    : '?';   // Question Mark
PIPE  : '|';   // PIPE symbol
EXCL  : '!';   // EXCLamation mark
ELLIP : '...'; // Ellipsis
EQ    : '==';  // EQual
NE    : '!=';  // Not Equal
OR    : '||';  // logical OR
AND   : '&&';  // logical AND
LE    : '<=';  // Less or Equal
GE    : '>=';  // Greater or Equal

WHITESPACE
	: [ \t]+ -> channel(HIDDEN)
	;

NEWLINE : 
	(   '\r' '\n'?
	|   '\n'
	) -> channel(HIDDEN)
	;

LINE_COMMENT
	: '//' ~[\r\n]* -> channel(HIDDEN)
	;

BLOCK_COMMENT
	: '/*' .*? '*/' -> channel(HIDDEN)
	;

INTEGER : [0-9]+
	;

BACKTICK_LITERAL
	: '`'
	(
	   ~[`\n]
	)* '`'
	;

CHAR_LITERAL
	: '\''
	(
	   ~['\\\n\r\t]
	   | QUOTE_ESCAPE
	) '\''
	;

STRING_LITERAL
	: '"'
	(
	   ~["]
	   | QUOTE_ESCAPE
	)* '"'
	;

fragment QUOTE_ESCAPE
	: '\\' ['"nrt0\\]
	;

fragment Identifier
	: Letter LetterOrDigit*
	;

fragment LetterOrDigit
	: Letter
	| [0-9]
	;

fragment Letter
	: [a-zA-Z_] // these are the "letters" below 0x7F
	| ~[\u0000-\u007F\uD800-\uDBFF] // covers all characters above 0x7F which are not a surrogate
	| [\uD800-\uDBFF] [\uDC00-\uDFFF] // covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
	;


/*
BSD License

Copyright (c) 2013, Kazunori Sakamoto
Copyright (c) 2016, Alexander Alexeev
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:

1. Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in the
   documentation and/or other materials provided with the distribution.
3. Neither the NAME of Rainer Schuster nor the NAMEs of its contributors
   may be used to endorse or promote products derived from this software
   without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

This grammar file derived from:

    Luau 0.537 Grammar Documentation
    https://github.com/Roblox/luau/blob/0.537/docs/_pages/grammar.md

    Lua 5.4 Reference Manual
    http://www.lua.org/manual/5.4/manual.html

    Lua 5.3 Reference Manual
    http://www.lua.org/manual/5.3/manual.html

    Lua 5.2 Reference Manual
    http://www.lua.org/manual/5.2/manual.html

    Lua 5.1 grammar written by Nicolai Mainiero
    http://www.antlr3.org/grammar/1178608849736/Lua.g

Tested by Kazunori Sakamoto with Test suite for Lua 5.2 (http://www.lua.org/tests/5.2/)

Tested by Alexander Alexeev with Test suite for Lua 5.3 http://www.lua.org/tests/lua-5.3.2-tests.tar.gz

Tested by Matt Hargett with:
    - Test suite for Lua 5.4.4: http://www.lua.org/tests/lua-5.4.4-tests.tar.gz
    - Test suite for Selene Lua lint tool v0.20.0: https://github.com/Kampfkarren/selene/tree/0.20.0/selene-lib/tests
    - Test suite for full-moon Lua parsing library v0.15.1: https://github.com/Kampfkarren/full-moon/tree/main/full-moon/tests
    - Test suite for IntelliJ-Luanalysis IDE plug-in v1.3.0: https://github.com/Benjamin-Dobell/IntelliJ-Luanalysis/tree/v1.3.0/src/test
    - Test suite for StyLua formatting tool v.14.1: https://github.com/JohnnyMorganz/StyLua/tree/v0.14.1/tests
    - Entire codebase for luvit: https://github.com/luvit/luvit/
    - Entire codebase for lit: https://github.com/luvit/lit/
    - Entire codebase and test suite for neovim v0.7.2: https://github.com/neovim/neovim/tree/v0.7.2
    - Entire codebase for World of Warcraft Interface: https://github.com/tomrus88/BlizzardInterfaceCode
    - Benchmarks and conformance test suite for Luau 0.537: https://github.com/Roblox/luau/tree/0.537
    - Entire Lua codebase for nmap 7.92 : https://github.com/nmap/nmap
*/

grammar Lua;

chunk
    : block EOF
    ;

block
    : (stat ';'?)* (laststat ';'?)?
    ;

stat
    : ';'
    | varlist ASSIGNMENT explist
    | var compoundop exp
    | functioncall
    | label
    | 'break'
    | 'goto' NAME
    | 'do' block 'end'
    | 'while' exp 'do' block 'end'
    | 'repeat' block 'until' exp
    | 'if' exp 'then' block ('elseif' exp 'then' block)* ('else' block)? 'end'
    | 'for' binding ASSIGNMENT exp ',' exp (',' exp)? 'do' block 'end'
    | 'for' bindinglist 'in' explist 'do' block 'end'
    | 'function' funcname funcbody
    | LOCAL 'function' NAME funcbody
    | LOCAL bindinglist (ASSIGNMENT explist)?
    | ('export')? 'type' NAME ('<' genericTypeParameterList '>')? '=' type
    ;

attnamelist
    : NAME attrib (',' NAME attrib)*
    ;

attrib
    : ('<' NAME '>')?
    ;

label
    : '::' NAME '::'
    ;

laststat
    // "continue" is a luau addition and actually not a reserved keyword
    : 'return' explist? | 'break' | 'continue'
    ;

funcname
    : NAME ('.' NAME)* (':' NAME)?
    ;

funcbody
    : ('<' genericTypeParameterList '>')? OPEN_PARENS parlist? CLOSE_PARENS (':' '...'? returnType ) block 'end'
    ;

parlist
    : bindinglist (',' '...')? 
    | '...'
    ;

explist
    : (exp ',')* exp
    ;

namelist
    : NAME (',' NAME)*
    ;

binding
    : NAME (':' type ('?')?)?
    ;

bindinglist
    : binding (',' bindinglist)?
    ;

var
    : (NAME | OPEN_PARENS exp CLOSE_PARENS varSuffix) varSuffix*
    ;

varlist
    : var (',' var)*
    ;

prefixexp
    : varOrExp nameAndArgs*
    ;

functioncall
    : varOrExp nameAndArgs+
    ;

exp
    : (asexp | operatorUnary exp) ( binop exp )*
    ;

ifelseexp
    : 'if' exp 'then' exp ('elseif' exp 'then' exp)* 'else' exp
    ;

asexp
    : simpleexp ('::' type)?
    ;

simpleexp
    : NIL | BOOLEAN
    | number
    | string
    | '...'
    | 'function' funcbody
    | prefixexp
    | ifelseexp
    | tableconstructor;

varOrExp
    : var 
    | OPEN_PARENS exp CLOSE_PARENS
    ;

varSuffix
    : nameAndArgs* (OPEN_BRACKET exp CLOSE_BRACKET | '.' NAME)
    ;

nameAndArgs
    : (':' NAME)? args
    ;

args
    : OPEN_PARENS explist? CLOSE_PARENS 
    | tableconstructor 
    | string
    ;

functiondef
    : 'function' funcbody
    ;

tableconstructor
    : OPEN_BRACE fieldlist? CLOSE_BRACE
    ;

fieldlist
    : field (fieldsep field)* fieldsep?
    ;

field
    : OPEN_BRACKET exp CLOSE_BRACKET ASSIGNMENT exp 
    | NAME ASSIGNMENT exp 
    | exp
    ;

fieldsep
    : ',' 
    | ';'
    ;

compoundop
    : '+=' 
    | '-=' 
    
    | '*=' 
    | '/=' 
    | '%=' 
    | '^=' 
    | '..=';

binop: operatorAddSub 
    | operatorMulDivMod 
    | operatorPower 
    | operatorStrcat 
    | operatorComparison 
    | operatorAnd 
    | operatorOr 
    | operatorBitwise;

operatorOr
	: 'or';

operatorAnd
	: 'and';

operatorComparison
	: '<' 
    | '>' 
    | '<=' 
    | '>=' 
    | '~=' 
    | '=='
    ;

ASSIGNMENT
    : '='
    ;

operatorStrcat
	: '..';

operatorAddSub
	: '+' 
    | '-'
    ;

operatorMulDivMod
	: '*' 
    | '/' 
    | '%' 
    | '//'
    ;

operatorBitwise
	: '&' 
    | '|' 
    | '~' 
    | '<<' 
    | '>>'
    ;

operatorUnary
    : 'not' 
    | '#' 
    | '-' 
    | '~'
    ;

operatorPower
    : '^';

number
    : INT 
    | HEX 
    | FLOAT 
    | HEX_FLOAT
    ;

string
    : NORMAL_STRING 
    | LONG_STRING 
    | INTERPOLATED_STRING
    ;

simpleType
    : NIL
    | singletonType
    | NAME ('.' NAME)? ('<' typeParams '>')?
    | 'typeof' OPEN_PARENS exp CLOSE_PARENS
    | tableType
    | functionType
    ;

singletonType
    : NORMAL_STRING 
    | BOOLEAN
    ;

type
    : simpleType ('?')?
    | type ('|' type)
    | type ('&' type)
    ;

genericTypePackParameter
    : NAME '...' ('=' (typePack | variadicTypePack | genericTypePack))?
    ;

genericTypeParameterList
    : NAME ('=' type)? (',' genericTypeParameterList)? 
    | genericTypePackParameter (',' genericTypePackParameter)*
    ;

typeList
    : type (',' typeList)? | variadicTypePack
    ;

typeParams
    : (type | typePack | variadicTypePack | genericTypePack) (',' typeParams)?
    ;

typePack
    : OPEN_PARENS (typeList)? CLOSE_PARENS
    ;

genericTypePack
    : NAME '...'
    ;

variadicTypePack
    : '...' type
    ;

returnType
    : type 
    | typePack
    ;

tableIndexer
    : OPEN_BRACKET type CLOSE_BRACKET ':' type
    ;

tableProp
    : NAME ':' type
    ;

tablePropOrIndexer
    : tableProp 
    | tableIndexer
    ;

propList
    : tablePropOrIndexer (fieldsep tablePropOrIndexer)* fieldsep?
    ;

tableType
    : OPEN_BRACE propList CLOSE_BRACE
    ;

functionType
    : ('<' genericTypeParameterList '>')? OPEN_PARENS (typeList)? CLOSE_PARENS '->' returnType
    ;

// LEXER

LOCAL
    : 'local'
    ;

REQUIRE
    : 'require'
    ;

NIL 
    : 'nil' 
    ;

BOOLEAN 
    : 'true' 
    | 'false' 
    ;

NAME
    : [a-zA-Z_][a-zA-Z_0-9]*
    ;

NORMAL_STRING
    : '"'  (~["\\\r\n\u0085\u2028\u2029] | EscapeSequence | '\\\n')* '"'
    | '\'' (~['\\\r\n\u0085\u2028\u2029] | EscapeSequence | '\\\n')* '\''
    ;

INTERPOLATED_STRING
    : '`' (~[`\\\r\n\u0085\u2028\u2029] | EscapeSequence | '\\\n')* '`'
    ;

LONG_STRING
    : OPEN_BRACKET NESTED_STR CLOSE_BRACKET
    ;

fragment
NESTED_STR
    : '=' NESTED_STR '='
    | OPEN_BRACKET .*? CLOSE_BRACKET
    ;

INT
    : Digit+
    ;

HEX
    : '0' [xX] HexDigit+
    ;

FLOAT
    : Digit+ '.' Digit* ExponentPart?
    | '.' Digit+ ExponentPart?
    | Digit+ ExponentPart
    ;

HEX_FLOAT
    : '0' [xX] HexDigit+ '.' HexDigit* HexExponentPart?
    | '0' [xX] '.' HexDigit+ HexExponentPart?
    | '0' [xX] HexDigit+ HexExponentPart
    ;

OPEN_BRACE
    : '{'
    ;

CLOSE_BRACE
    : '}'
    ;

OPEN_BRACKET
    : '['
    ;
CLOSE_BRACKET
    : ']'
    ;

OPEN_PARENS:
    '('
    ;

CLOSE_PARENS
    : ')'
    ;

NL
    : '\r\n' | '\r' | '\n'
    | '\u0085' // <Next Line CHARACTER (U+0085)>'
    | '\u2028' //'<Line Separator CHARACTER (U+2028)>'
    | '\u2029' //'<Paragraph Separator CHARACTER (U+2029)>'
    ;

COMMA
    : ','
    ;

fragment
ExponentPart
    : [eE] [+-]? Digit+
    ;

fragment
HexExponentPart
    : [pP] [+-]? Digit+
    ;

fragment
EscapeSequence
    : '\\' [abfnrtvz"'`|$#\\]   // World of Warcraft Lua additionally escapes |$# 
    | NL
    | DecimalEscape
    | HexEscape
    | UtfEscape
    ;

fragment
DecimalEscape
    : '\\' Digit
    | '\\' Digit Digit
    | '\\' [0-2] Digit Digit
    ;

fragment
HexEscape
    : '\\' 'x' HexDigit HexDigit
    ;

fragment
UtfEscape
    : '\\' 'u{' HexDigit+ '}'
    ;

fragment
Digit
    : [0-9]
    ;

fragment
HexDigit
    : [0-9a-fA-F]
    ;

fragment
StartingSingleCommentLineInputCharacter
    : ~[[\r\n\u0085\u2028\u2029]
    ;

fragment
SingleLineInputCharacter
    : ~[\r\n\u0085\u2028\u2029]
    ;

COMMENT
    : '--[' NESTED_STR ']' -> channel(HIDDEN)
    ;

LINE_COMMENT
    : '--' (NL | StartingSingleCommentLineInputCharacter SingleLineInputCharacter*) -> channel(HIDDEN)
    ;

WS
    : [ \n\r\t\u000B\u000C\u0000]+ -> channel(HIDDEN)
    ;

SHEBANG
    : '#' '!' SingleLineInputCharacter* -> channel(HIDDEN)
    ;

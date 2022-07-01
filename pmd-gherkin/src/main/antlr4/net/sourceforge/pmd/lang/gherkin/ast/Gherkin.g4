grammar Gherkin;

// PARSER

main
    // start comment needed because each comment should start on a new line except for the start comment
    : STARTCOMMENT? feature description* instructionLine* NL* EOF
    ;

feature
    : (NL* tagline)* NL* FEATURE?
    ;

instructionLine
    : NL+ (instruction | datatable)
    ;

instruction
    : rulex description* // the name "rule" is not allowed by ANTLR (used for internal usage), so calling it rulex
    | stepInstruction description* (NL+ stepDescription description*)* (NL+ step)*
    | tagline
    | instructionDescription description*
    ;

stepInstruction
    : background
    | scenario
    | scenarioOutline
    ;

background: BACKGROUND ;
rulex: RULEX ;
scenario: SCENARIO ;
scenarioOutline : SCENARIOOUTLINE ;

step : stepItem description*;

stepItem
    : and
    | anystep
    | but
    | datatable
    | given
    | then
    | when
    | (NL* tagline )* NL* examples
    ;

tagline
    : TAG+
    ;

and: AND ;
anystep: ANYSTEP ;
but: BUT ;
datatable: DATATABLE+ ;
given: GIVEN ;
then: THEN ;
when: WHEN ;
examples: EXAMPLES ;

// Descriptions
instructionDescription: text | PARAMETER | AND | ANYSTEP | BUT | GIVEN | THEN | WHEN | SCENARIO ; // We have to deal with overlaps with keywords
stepDescription: text | PARAMETER ; // We have to deal with overlaps with keywords
description: text | PARAMETER | TAG | AND | ANYSTEP | BUT | DATATABLE | GIVEN | THEN | WHEN | SCENARIO | SCENARIOOUTLINE | STARTCOMMENT ; // We have to deal with overlaps with keywords

text: TOKEN+ ;

// LEXER

// skipped

BOMUTF8 : '\u00EF\u00BB\u00BF' -> skip ;

BOMUTF16 : '\uFEFF' -> skip ;

WHITESPACE: [ \t]+ -> channel(1) ;

COMMENT: '\r'?'\n' [ \t]* '#' ~[\r\n]* -> channel(2) ;

STARTCOMMENT: '#' ~[\r\n]* ;

DOCSTRING1
    : '"""' .*? '"""' ;

DOCSTRING2
    : '```' .*? '```' ;

// Instructions
BACKGROUND: 'Background:' ;
EXAMPLES: ('Examples:' | 'Scenarios:') ;
FEATURE: 'Feature:';
RULEX: 'Rule:' ;
SCENARIO: ('Example:' | 'Scenario:') ;
SCENARIOOUTLINE : 'Scenario ' ('Outline:' | 'Template:') ;

// Steps
AND: 'And' ;
ANYSTEP: '*' ;
BUT: 'But' ;
DATATABLE: '|' DATATABLEID? ; // must be an ID because it can contain a space
GIVEN: 'Given' ;
THEN: 'Then' ;
WHEN: 'When' ;

TAG: '@' ELEMENT+ ;
PARAMETER: '<' PARID '>' | '"' '<' PARID '>' '"' | '\'' '<' PARID '>' '\'';
fragment PARID: [A-Za-z0-9] ([!-=?-~ ]* [!-=?-~])?; // start with an alpha numerical and then all printable characters and end with a non-space
fragment ID: (IDELEMENT | ' ')* IDELEMENT (IDELEMENT | ' ')*; // ID should contain at least one non-whitespace character otherwise the trailing | with a trailing space will match
fragment DATATABLEID: (DATATABLEELEMENT | ' ')* DATATABLEELEMENT (DATATABLEELEMENT | ' ')*; // ID should contain at least one non-whitespace character otherwise the trailing | with a trailing space will match
fragment DATATABLEELEMENT: ELEMENT | '<' | '>' | '"' | '\'' | '\\|' ;
fragment IDELEMENT: ELEMENT | '|' ;
fragment ELEMENT: [!-&(-;=?-{}~\u00A0-\uFFFF] ;

NL: '\r'? '\n' ;
TOKEN: [!-{}-~\u00A0-\uFFFF]+ ; // match everything that isn't matched yet



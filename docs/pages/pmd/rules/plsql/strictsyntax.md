---
title: Strict Syntax
summary: The Strict Syntax ruleset contains rules that highlight invalid plsql syntax, which works, but should be avoided.
permalink: pmd_rules_plsql_strictsyntax.html
folder: pmd/rules/plsql
sidebaractiveurl: /pmd_rules_plsql.html
editmepath: ../pmd-plsql/src/main/resources/rulesets/plsql/strictsyntax.xml
---
## MisplacedPragma
**Since:** 5.5.2

**Priority:** Medium (3)

Oracle states that the PRAQMA AUTONOMOUS_TRANSACTION must be in the declaration block,
but the code does not complain, when being compiled on the 11g DB.
https://docs.oracle.com/cd/B28359_01/appdev.111/b28370/static.htm#BABIIHBJ

**Example(s):**
```
create or replace package inline_pragma_error is

end;
/

create or replace package body inline_pragma_error is
  procedure do_transaction(p_input_token        in varchar(200)) is
  PRAGMA AUTONOMOUS_TRANSACTION; /* this is correct place for PRAGMA */
  begin
    PRAGMA AUTONOMOUS_TRANSACTION; /* this is the wrong place for PRAGMA -> violation */
    /* do something */
    COMMIT;
   end do_transaction;

end inline_pragma_error;
/
```


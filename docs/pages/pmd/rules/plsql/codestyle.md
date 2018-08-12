---
title: Code Style
summary: Rules which enforce a specific coding style.
permalink: pmd_rules_plsql_codestyle.html
folder: pmd/rules/plsql
sidebaractiveurl: /pmd_rules_plsql.html
editmepath: ../pmd-plsql/src/main/resources/category/plsql/codestyle.xml
keywords: Code Style, MisplacedPragma, ForLoopNaming
language: PLSQL
---
## ForLoopNaming

**Since:** PMD 6.7.0

**Priority:** Medium (3)

In case you have loops please name the loop variables more meaningful.

**This rule is defined by the following XPath expression:**
``` xpath
//CursorForLoopStatement[
    $allowSimpleLoops = 'false' or
    (Statement//CursorForLoopStatement or ancestor::CursorForLoopStatement)
]
/ForIndex[not(matches(@Image, $cursorPattern))]
|
//ForStatement[
    $allowSimpleLoops = 'false' or
    (Statement//ForStatement or ancestor::ForStatement)
]
/ForIndex[not(matches(@Image, $indexPattern))]
```

**Example(s):**

``` sql
-- good example
BEGIN
FOR company IN (SELECT * FROM companies) LOOP
  FOR contact IN (SELECT * FROM contacts) LOOP
    FOR party IN (SELECT * FROM parties) LOOP
      NULL;
    END LOOP;
  END LOOP;
END LOOP;
END;
/

-- bad example
BEGIN
FOR c1 IN (SELECT * FROM companies) LOOP
  FOR c2 IN (SELECT * FROM contacts) LOOP
    FOR c3 IN (SELECT * FROM parties) LOOP
      NULL;
    END LOOP;
  END LOOP;
END LOOP;
END;
/
```

**This rule has the following properties:**

|Name|Default Value|Description|Multivalued|
|----|-------------|-----------|-----------|
|allowSimpleLoops|false|Ignore simple loops, that are not nested|no|
|cursorPattern|[a-zA-Z_0-9]{5,}|The pattern used for the curosr loop variable|no|
|indexPattern|[a-zA-Z_0-9]{5,}|The pattern used for the index loop variable|no|

**Use this rule by referencing it:**
``` xml
<rule ref="category/plsql/codestyle.xml/ForLoopNaming" />
```

## MisplacedPragma

**Since:** PMD 5.5.2

**Priority:** Medium (3)

Oracle states that the PRAQMA AUTONOMOUS_TRANSACTION must be in the declaration block,
but the code does not complain, when being compiled on the 11g DB.
https://docs.oracle.com/cd/B28359_01/appdev.111/b28370/static.htm#BABIIHBJ

**This rule is defined by the following XPath expression:**
``` xpath
//ProgramUnit/Pragma
```

**Example(s):**

``` sql
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

**Use this rule by referencing it:**
``` xml
<rule ref="category/plsql/codestyle.xml/MisplacedPragma" />
```


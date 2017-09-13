---
title: Tom Kyte's Despair
summary: Rules based on Thomas Kyte's recommendations on http://asktom.oracle.com/ and http://tkyte.blogspot.com/.
permalink: pmd_rules_plsql_TomKytesDespair.html
folder: pmd/rules/plsql
sidebaractiveurl: /pmd_rules_plsql.html
editmepath: ../pmd-plsql/src/main/resources/rulesets/plsql/TomKytesDespair.xml
keywords: Tom Kyte's Despair, TomKytesDespair
---
## TomKytesDespair

**Since:** PMD 5.1

**Priority:** Medium (3)

"WHEN OTHERS THEN NULL" hides all errors - (Re)RAISE an exception or call RAISE_APPLICATION_ERROR

```
//ExceptionHandler[QualifiedName/@Image='OTHERS' and upper-case(Statement/UnlabelledStatement/Expression/@Image)='NULL']
```

**Example(s):**

``` sql
CREATE OR REPLACE PACKAGE BODY update_planned_hrs
IS

PROCEDURE set_new_planned (p_emp_id IN NUMBER, p_project_id IN NUMBER, p_hours IN NUMBER)
IS
BEGIN
   UPDATE employee_on_activity ea
   SET ea.ea_planned_hours = p_hours
   WHERE
            ea.ea_emp_id = p_emp_id
            AND ea.ea_proj_id = p_project_id;

EXCEPTION
          WHEN NO_DATA_FOUND THEN
          RAISE_APPLICATION_ERROR (-20100, 'No such employee or project');

END set_new_planned;

FUNCTION existing_planned (p_emp_id IN NUMBER, p_project_id IN NUMBER) RETURN NUMBER

IS

existing_hours NUMBER(4);

BEGIN
   SELECT ea.ea_planned_hours INTO existing_hours
   FROM employee_on_activity ea
   WHERE
            ea.ea_emp_id = p_emp_id
            AND ea.ea_proj_id = p_project_id;

   RETURN (existing_hours);

   EXCEPTION
          WHEN OTHERS THEN NULL;

   END existing_planned;

END update_planned_hrs;
/
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/plsql/TomKytesDespair.xml/TomKytesDespair" />
```


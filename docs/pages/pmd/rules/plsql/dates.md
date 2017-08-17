---
title: PLSQL DATETIME
summary: The Dates ruleset deals with PLSQL DATETIME usages.
permalink: pmd_rules_plsql_dates.html
folder: pmd/rules/plsql
sidebaractiveurl: /pmd_rules_plsql.html
editmepath: ../pmd-plsql/src/main/resources/rulesets/plsql/dates.xml
keywords: PLSQL DATETIME, TO_DATEWithoutDateFormat, TO_DATE_TO_CHAR, TO_TIMESTAMPWithoutDateFormat
---
## TO_DATE_TO_CHAR

**Since:** PMD 5.1

**Priority:** Medium (3)

TO_DATE(TO_CHAR(date-variable)) used to remove time component - use TRUNC(date-veriable)

```
//PrimaryExpression
    [PrimaryPrefix/Name/@Image='TO_DATE']
    [count(PrimarySuffix/Arguments/ArgumentList/Argument) = 1]
    [.//PrimaryExpression
        [PrimaryPrefix/Name/@Image='TO_CHAR']
        [count(PrimarySuffix/Arguments/ArgumentList/Argument) = 1]
    ]
```

**Example(s):**

``` sql
CREATE OR REPLACE PACKAGE BODY date_utilities
IS
 
-- Take single parameter, relyimg on current default NLS date format
FUNCTION strip_time (p_date IN DATE) RETURN DATE
IS
BEGIN
   RETURN TO_DATE(TO_CHAR(p_date));
END strip_time;


END date_utilities;
/
```

## TO_DATEWithoutDateFormat

**Since:** PMD 5.1

**Priority:** Medium (3)

TO_DATE without date format- use TO_DATE(expression, date-format)

```
//PrimaryExpression[PrimaryPrefix/Name/@Image='TO_DATE'  and count(PrimarySuffix/Arguments/ArgumentList/Argument) = 1 ]
```

**Example(s):**

``` sql
CREATE OR REPLACE PACKAGE BODY date_utilities
IS

-- Take single parameter, relyimg on current default NLS date format
FUNCTION to_date_single_parameter (p_date_string IN VARCHAR2) RETURN DATE
IS
BEGIN
   RETURN TO_DATE(p_date_string);
END to_date_single_parameter ;

-- Take 2 parameters, using an explicit date format string
FUNCTION to_date_two_parameters (p_date_string IN VARCHAR2, p_format_mask IN VARCHAR2) RETURN DATE
IS
BEGIN
   TO_DATE(p_date_string, p_date_format);
END to_date_two_parameters;

-- Take 3 parameters, using an explicit date format string and an explicit language
FUNCTION to_date_three_parameters (p_date_string IN VARCHAR2, p_format_mask IN VARCHAR2, p_nls_language VARCHAR2 ) RETURN DATE
IS
BEGIN
   TO_DATE(p_date_string, p_format_mask, p_nls_language);
END to_date_three_parameters;

END date_utilities;
/
```

## TO_TIMESTAMPWithoutDateFormat

**Since:** PMD 5.1

**Priority:** Medium (3)

TO_TIMESTAMP without date format- use TO_TIMESTAMP(expression, date-format)

```
//PrimaryExpression[PrimaryPrefix/Name/@Image='TO_TIMESTAMP'  and count(PrimarySuffix/Arguments/ArgumentList/Argument) = 1 ]
```

**Example(s):**

``` sql
CREATE OR REPLACE PACKAGE BODY date_utilities
IS

-- Take single parameter, relyimg on current default NLS date format
FUNCTION to_timestamp_single_parameter (p_date_string IN VARCHAR2) RETURN DATE
IS
BEGIN
   RETURN TO_TIMESTAMP(p_date_string);
END to_timestamp_single_parameter;

-- Take 2 parameters, using an explicit date format string
FUNCTION to_timestamp_two_parameters (p_date_string IN VARCHAR2, p_format_mask IN VARCHAR2) RETURN DATE
IS
BEGIN
   TO_TIMESTAMP(p_date_string, p_date_format);
END to_timestamp_two_parameters;

-- Take 3 parameters, using an explicit date format string and an explicit language
FUNCTION to_timestamp_three_parameters (p_date_string IN VARCHAR2, p_format_mask IN VARCHAR2, p_nls_language VARCHAR2 ) RETURN DATE
IS
BEGIN
   TO_TIMESTAMP(p_date_string, p_format_mask, p_nls_language);
END to_timestamp_three_parameters;

END date_utilities;
/
```


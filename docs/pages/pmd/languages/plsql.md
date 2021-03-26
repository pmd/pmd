---
title: PLSQL
permalink: pmd_languages_plsql.html
last_updated: March 2021 (6.33.0)
---

## Parsing Exclusions

The grammar for PLSQL used in PMD has several bugs and might not parse all DDL scripts
without errors. However, it should be best practice to call PMD for _every_ DDL script.
Thus, we introduce the following workaround to cope with the situation.

We introduce two special comments `PMD-EXCLUDE-BEGIN` and `PMD-EXCLUDE-END`
which cause PMD to treat the source in between these comments more or less
like a multi-line comment, or in other words, just not try to parse them.

It is good practice to include a reason for excluding inside the
`-- PMD-EXCUDE-BEGIN` comment separated by a colon.

The `PMD-EXCLUDE-BEGIN` and `PMD-EXLUDE-END` comment lines must not contain
other statements, e.g. `do_xy(); -- PMD-EXCLUDE-BEGIN` is invalid.

Example:

```
begin
  do_something();
  -- PMD-EXCLUDE-BEGIN: PMD does not like dbms_lob.trim (clash with TrimExpression)
  dbms_lob.trim(the_blob, 1000);
  -- PMD-EXCLUDE-END
  do_something_else();
end;
```

The existence of exclusions can be detected with the attributes
`ExcludedRangesCount` and `ExcludedLinesCount` of the top-level ASTInput node.
If nothing is excluded, both values are 0 (zero).
Otherwise, `ExcludedRangesCount` contains the number of excluded line-ranges
and `ExcludedLinesCount` is the total number of excluded lines.
A future version of PMD might pass the line excluded line ranges,
source fragments and the corresponding reason comments
as child nodes of the top-level ASTInput node.

In order to keep track where such parse exclusions are used, you could create
a custom XPath rule with the following expression:

    /Input[@ExcludedRangesCount > 0]

This will find all files with at least one excluded range.

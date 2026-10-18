
## RuleAInCodestyle

Sample rule doc for tests.

## RuleBInCodestyle

This rule description references {% rule NotExistingRule %}. This is wrong.
This rule description references {% rule "NotExistingRule" %}. This is wrong (with quotes).

This rule description references {% rule RuleAInCodestyle %}. This is correct.

This rule description references {% rule java/codestyle/RuleAInCodestyle %}. This is correct.

This rule description references {% rule java/bestpractices/AvoidPrintStackTrace %}. This is correct.

This rule description references {% rule java/notexistingcategory/AvoidPrintStackTrace %}. This is wrong.

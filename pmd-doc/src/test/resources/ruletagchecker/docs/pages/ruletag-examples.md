---
title: Sample Page with rule tags
permalink: rule_tag_samples.html
---

This is a link to the rule AvoidPrintStackTrace: {% rule "java/bestpractices/AvoidPrintStackTrace" %}.
This is a link to the rule AvoidPrintStackTrace without quotes: {% rule java/bestpractices/AvoidPrintStackTrace %}.

This is the same link, but the rule tag is not closed properly: {% rule "java/bestpractices/AvoidPrintStackTrace" %).

Now this is link to a rule inside a category, which doesn't exist:
{% rule "java/notexistingcategory/AvoidPrintStackTrace" %}.

This is link to a rule, which doesn't exist: {% rule "java/bestpractices/NotExistingRule" %}.

This is a rule tag, which has only one quote: {% rule "java/bestpractices/OtherRule %}.
This is a rule tag, which has only one quote: {% rule java/bestpractices/OtherRule" %}.

This is a rule tag, that references a rule in the same category: {% rule OtherRule %} (correct).
This is a rule tag, that references a rule in the same category: {% rule "OtherRule" %} (correct).
This is a rule tag, that references a rule in the same category: {% rule "OtherRule %} (missing quote).
This is a rule tag, that references a rule in the same category: {% rule OtherRule" %} (missing quote).

<!--
    <author email="tom@infoether.com">Tom Copeland</author>
-->

# Best Practices

## Choose the rules that are right for you

Running every ruleset will result in a huge number of rule violations, most of which will be unimportant.
Having to sort through a thousand line report to find the few you're really interested in takes
all the fun out of things.

Instead, start with some of the obvious rulesets - just run [unusedcode][1] and fix any unused locals and fields.
Then, run [empty][8] and fix all the empty `if` statements and such-like. After that, take [unnecessary][9]
and fix these violations. Then, run [basic][2] and fix the remaining violations.
Then peruse the [design][3] and [controversial][4] rulesets and use the ones
you like [via a custom ruleset][5].

## PMD rules are not set in stone

Generally, pick the ones you like, and ignore or [suppress][6] the warnings you don't like. It's just a tool.

## PMD IDE plugins are nice

Using PMD within your IDE is much more enjoyable than flipping back and forth
between an HTML report and your IDE. Most IDE plugins have the "click on the rule
violation and jump to that line of code" feature. Find the PMD plugin for your IDE, install it,
and soon you'll be fixing problems much faster.

Suggestions?  Comments?  Post them [here][7]. Thanks!


[1]: ../pmd-java/rules/index.html#Unused_Code 
[2]: ../pmd-java/rules/index.html#Basic
[3]: ../pmd-java/rules/index.html#Design
[4]: ../pmd-java/rules/index.html#Controversial
[5]: ../usage/howtomakearuleset.html
[6]: ../usage/suppressing.html
[7]: https://github.com/pmd/pmd/issues
[8]: ../pmd-java/rules/index.html#Empty_Code
[9]: ../pmd-java/rules/index.html#Unnecessary


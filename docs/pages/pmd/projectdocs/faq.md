---
title: FAQ
sidebar: pmd_sidebar
permalink: pmd_projectdocs_faq.html
folder: pmd/userdocs
---

## Rules

### How useful are the rules?

Have this clear: having a projects with no pmd violations does not mean at all, I repeat,
it does not mean at all, not at the minimum expected, that the project has any quality.
For illustrating this I'll tell a little story taken from my work (a sadly real story).
Some classes had fields that were reported as unused, (unused code ruleset) as developers
saw this, they wanted to remove the violation, (not fix the code, fix the violation) so
the action took was to add useless log sentences with something like:
'unused variable '+unusedVariable. Believe it or not, the code was worse than the original
and reported less pmd violations.

In a more positive way: use the rules as you see them fix, don't try to remove violations
per-se, try to review the code and see if the particular cases you are using are correct or not.


## Development

### In which order are nodes visited?

The parser performs a depth-first traversal.
Consider the given source:

    public class Foo {
        String name;
        private class Bar {
            String x;
        }
        int total;
    }

The visiting order here will be:

1.  Class Foo
2.  Field name
3.  Class Bar
4.  Field x
5.  Field total

Note that the total field of Foo will be visited after visiting the fields in Bar.
You must take this into account for certain rules.

### Is there a simple way of getting fields from a given class?

Yes, the symbol table can supply that information.

FIXME: add code example

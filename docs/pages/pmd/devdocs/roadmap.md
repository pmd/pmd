---
title: Roadmap
tags: [devdocs]
permalink: pmd_devdocs_roadmap.html
author: >
    Tom Copeland <tom@infoether.com>, Ryan Gustavson, Romain Pelisse <belaran@gmail.com>,
    Juan Martín Sotuyo Dodero <juansotuyo@gmail.com>, Andreas Dangel <adangel@users.sourceforge.net>
---

TODO:

* Update
* Future direction
* projects, plans
* Google Summer of Code


# Future directions

Want to know what's coming? Or, better, wanna contribute ? Here is the page listing what are our plans -
when we have ones, for the future of PMD. It also give you hints at part of the code we would like to clean -
that you may want to clean to contribute to the project!

Of course, an easy way to contribute is too check out the [bug tracker](https://github.com/pmd/pmd/issues) and see if you can fix some issues -
some could be quite easy, we simply have not the time to look at them all!

At last, if you want to contribute, register on the [pmd-devel](https://sourceforge.net/projects/pmd/lists/pmd-devel) mailing list, and come discuss with us!

## Roadmap

This roadmap contains all the different 'workshops' PMD's developers are working right now.

*   **Better symbol analysis**: See below.
*   **Data Flow Analysis**: See below.
*   **Code Cleanups**: See below.

*Please note that, of course, there is no warranty about when those 'features' will be finished, if they ever are.*

## Better symbol analysis

Currently PMD only looks at one source file at a time.  Instead, it should resolve symbols across classes.
This will eliminate some open bugs and enable a lot more rules to be written. However, it'll taken some doing,
because it'll require parsing of class files. Lots of work here.

## Data flow analysis (DFA)

Raik Schroeder, a graduate student at [Fachhochschule Stralsund](http://www.fh-stralsund.de/) has written a DFA layer that should enable
us to write some more complicated rules - stuff like common subexpression elimination, loop invariant code motion
(and code hoisting suggestions), shrink wrapping, and partial redundancy elimination. The code is currently in the `net.sourceforge.pmd.dfa` packages, and we're going through it now figuring out what rules we can write
that use it.  We should be able to use it to simplify some current rules, as well.

## Other changes we'll like to see...

**These are things which really should be done, but just haven't been gotten to yet:**

*   Enhance Rule Designer to allow testing of the violation suppress Regex and XPath.
*   Remove the type resolution specific rules. Merge these back into the
    standard rules.  In general, a Rule should use TR when it can, and fall
    back on non-TR approach otherwise.  No need for separate Rules for TR/non-TR.
*   Reconcile the util.designer and util.viewer packages.  Two versions of the
    same thing.  Designer is more up to date, but Viewer has a nice MVC design.
*   Need a JUnit test to check for "dead" Rules, that is those not used by any RuleSet.
*   Rule JUnit tests should verify the Test class follows expected naming
    conventions just like the Rules need to.
*   Do we have a rule to style check for multiple declarations and chained
    assignments? (e.g. int a, b;  int a = b = x;)

<strong>These are food for thought, perhaps future items.  If you think you'd like to
work on one of these, check with pmd-devel to see what the current thoughts
on the topic.</strong>

*   CPD needs work on use of Language.  It currently is hardcoded to only
    handled Java 1.4.  Integrate CPD needs into core PMD where appropriate.
    Otherwise, drive CPD behavior based off of core PMD, instead of duplicating
    some logic.

*   Need a more flexible and powerful scheme for classifying files to various
    Languages.  At a minimum, should have the ability to specify which
    file extensions you want to be used for a language (e.g. not everyone uses
    .jsp for JSP extensions, some use .jspx, .xhtml, etc.).  Also, consider
    hooks into the LanguageVersionDiscoverer process for classifying a
    File/String to a LanguageVersion of a specific Language, one could imaging
    using a 'magic' system like Unix uses to tell different versions of files
    apart based on actual content.

*   Should we change Node interface to something like 'Node&lt;T extends Node&lt;T&gt;&gt;',
    and then declare the language specific node interfaces to be something like
    'JavaNode extends Node&lt;JavaNode&gt;'?  This could allow anything on the Node
    interface to return the language specific node type instead of generic
    node.  For example, ASTStatement.jjtGetParent() to return a JavaNode,
    instead of a Node.  This is a rather huge change, as the Node interface is
    one of the pervasive things in the PMD code base.  Is the extra work of using
    the Node interface with properly with generics, worth the omission of
    occasional some casting?

*   Should multiple Languages be able to claim a single source file?  Imagine
    XML format JSP file, for which you've defined a ruleset which uses JSP and
    XML rules.  Stating that certain XML rules also can map to the JSP language
    extensions could be useful.  This means Source file to LanguageVersion
    mapping is not 1-1, but 1-many, we'd need to deal with this accordingly.

*   Additional changes to Rule organization within RuleSets as discussed on
    [this forum thread](http://sourceforge.net/p/pmd/discussion/188194/thread/b840897c).

*   Figure out a way to allow Rules to deal with parentheses and blocks, which
    introduce certain repetitive (and generally ignorable for most Rules)
    structures into the AST tree.  Some rules are making special effort
    (e.g. ConfusingTernaryRule) to detect these AST patterns.  Perhaps a
    "normalized" AST structure can be created which will make the AST appear
    consistent regardless of how many parens are presented, or how many blocks
    have been created (e.g. default block inserted, duplicates collapsed).
    This should be configurable on per Rule basis similar to TR and SymbolTable.

## Code cleanups

Some of the code is a bit sloppy:

*   RuleSetFactory is a mess.  It needs to be refactored into something that has layers, or decorators, or something.
*   Cleanups would be welcome for ConstructorCallsOverridableMethod and DoubleCheckedLocking
*   The Designer GUI is a bit messed up; the bottom panes look funny.
*   The grammar has some odd bits:
    *   BlockStatement has an odd hack for class definitions inside methods
    *   enumLookahead() seems like a bit of overkill, can it use Modifiers somehow?
    *   The whole "discardable node" thing seems wasteful
    *   Does ExtendsList need that 'extendsMoreThanOne' thing?
    *   ClassOrInterfaceBodyDeclaration has a monstrous lookahead to check for enums
    *   ClassOrInterfaceType gloms together dotted names... is that the right thing to do?
    *   Some complicated annotations are currently broken

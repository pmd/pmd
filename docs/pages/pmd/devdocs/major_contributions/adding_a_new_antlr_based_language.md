---
title: Adding PMD support for a new ANTLR grammar based language
short_title: Adding a new language with ANTLR
tags: [devdocs, extending]
summary: "How to add a new language to PMD using ANTLR grammar."
last_updated: July 21, 2019
sidebar: pmd_sidebar
permalink: pmd_devdocs_major_adding_new_language_antlr.html
folder: pmd/devdocs

# needs to be changed to branch master instead of pmd/7.0.x
# https://github.com/pmd/pmd/blob/pmd/7.0.x -> https://github.com/pmd/pmd/blob/master
---


## 1.  Start with a new sub-module.
*   See pmd-swift for examples.

## 2.  Implement an AST parser for your language
*   ANTLR will generate the parser for you based on the grammar file. The grammar file needs to be placed in the
    folder `src/main/antlr4` in the appropriate sub package `ast` of the language. E.g. for swift, the grammar
    file is [Swift.g4](https://github.com/pmd/pmd/blob/pmd/7.0.x/pmd-swift/src/main/antlr4/net/sourceforge/pmd/lang/swift/ast/Swift.g4)
    and is placed in the package `net.sourceforge.pmd.lang.swift.ast`.

## 3.  Create AST node classes
*   The individual AST nodes are generated, but you need to define the common interface for them.
*   You need a need to define the supertype interface for all nodes of the language. For that, we provide
    [`AntlrNode`](https://github.com/pmd/pmd/blob/pmd/7.0.x/pmd-core/src/main/java/net/sourceforge/pmd/lang/ast/impl/antlr4/AntlrNode.java).
*   See [`SwiftNode`](https://github.com/pmd/pmd/blob/pmd/7.0.x/pmd-swift/src/main/java/net/sourceforge/pmd/lang/swift/ast/SwiftNode.java)
    as an example.
*   Additionally, you need several base classes:
    *   a language specific inner node - these nodes represent the production rules from the grammar.
        In Antlr, they are called "ParserRuleContext". We call them "InnerNode". Use the
        base class from pmd-core
        [`BaseAntlrInnerNode`](https://github.com/pmd/pmd/blob/pmd/7.0.x/pmd-core/src/main/java/net/sourceforge/pmd/lang/ast/impl/antlr4/BaseAntlrInnerNode.java)
        . And example is [`SwiftInnerNode`](https://github.com/pmd/pmd/blob/pmd/7.0.x/pmd-swift/src/main/java/net/sourceforge/pmd/lang/swift/ast/SwiftInnerNode.java).
    *   a language specific root node - this provides the root of the AST and our parser will return
        subtypes of this node. The root node itself is a "InnerNode".
        See [`SwiftRootNode`](https://github.com/pmd/pmd/blob/pmd/7.0.x/pmd-swift/src/main/java/net/sourceforge/pmd/lang/swift/ast/SwiftRootNode.java).
    *   a language specific terminal node.
        See [`SwiftTerminalNode`](https://github.com/pmd/pmd/blob/pmd/7.0.x/pmd-swift/src/main/java/net/sourceforge/pmd/lang/swift/ast/SwiftTerminalNode.java).
    *   a language specific error node.
        See [`SwiftErrorNode`](https://github.com/pmd/pmd/blob/pmd/7.0.x/pmd-swift/src/main/java/net/sourceforge/pmd/lang/swift/ast/SwiftErrorNode.java).
*   In order for the generated code to match and use our custom classes, we have a common ant script, that fiddles with
    the generated code. The ant script is [`antlr4-wrapper.xml`](https://github.com/pmd/pmd/blob/pmd/7.0.x/antlr4-wrapper.xml) and
    does not need to be adjusted - it has plenty of parameters to set. The ant script is added in the
    language module's `pom.xml` where the parameters are set (e.g. name of root name class). Have a look at
    Swift's example: [`pmd-swift/pom.xml`](https://github.com/pmd/pmd/blob/pmd/7.0.x/pmd-swift/pom.xml).
*   You can add additional methods in your "InnerNode" (e.g. `SwiftInnerNode`) that are available on all nodes.
    But on most cases you won't need to do anything.

## 4.  Generate your parser
*   Make sure, you have the property `<antlr4.visitor>true</antlr4.visitor>` in your `pom.xml` file.
*   This is just a matter of building the language module. ANTLR is called via ant, and this step is added
    to the phase `generate-sources`. So you can just call e.g. `./mvnw generate-sources -pl pmd-swift` to
    have the parser generated.
*   The generated code will be placed under `target/generated-sources/antlr4` and will not be committed to
    source control.
*   You should review the [swift pom](https://github.com/pmd/pmd/blob/pmd/7.0.x/pmd-swift/pom.xml).

## 5.  Create a TokenManager
*   This is needed to support CPD (copy paste detection)
*   We provide a default implementation using [`AntlrTokenManager`](https://github.com/pmd/pmd/blob/pmd/7.0.x/pmd-core/src/main/java/net/sourceforge/pmd/cpd/internal/AntlrTokenizer.java).
*   You must create your own "AntlrTokenizer" such as we do with
    [`SwiftTokenizer`](https://github.com/pmd/pmd/blob/pmd/7.0.x/pmd-swift/src/main/java/net/sourceforge/pmd/cpd/SwiftTokenizer.java).
*   If you wish to filter specific tokens (e.g. comments to support CPD suppression via "CPD-OFF" and "CPD-ON")
    you can create your own implementation of
    [`AntlrTokenFilter`](https://github.com/pmd/pmd/blob/pmd/7.0.x/pmd-core/src/main/java/net/sourceforge/pmd/cpd/token/AntlrTokenFilter.java).
    You'll need to override then the protected method `getTokenFilter(AntlrTokenManager)`
    and return your custom filter. See the tokenizer for C# as an exmaple:
    [`CsTokenizer`](https://github.com/pmd/pmd/blob/pmd/7.0.x/pmd-cs/src/main/java/net/sourceforge/pmd/cpd/CsTokenizer.java).
    
    If you don't need a custom token filter, you don't need to override the method. It returns the default
    `AntlrTokenFilter` which doesn't filter anything.

## 6.  Create a PMD parser “adapter”
*   Create your own parser, that adapts the ANLTR interface to PMD's parser interface.
*   We provide a [`AntlrBaseParser`](https://github.com/pmd/pmd/blob/pmd/7.0.x/pmd-core/src/main/java/net/sourceforge/pmd/lang/ast/impl/antlr4/AntlrBaseParser.java)
    implementation that you need to extend to create your own adapter as we do with
    [`PmdSwiftParser`](https://github.com/pmd/pmd/blob/pmd/7.0.x/pmd-swift/src/main/java/net/sourceforge/pmd/lang/swift/ast/PmdSwiftParser.java).

## 7.  Create a rule violation factory
*   This is an optional step. Most like, the default implementation will do what you need.
    The default implementation is [`DefaultRuleViolationFactory`](https://github.com/pmd/pmd/blob/pmd/7.0.x/pmd-core/src/main/java/net/sourceforge/pmd/lang/rule/impl/DefaultRuleViolationFactory.java).
*   The purpose of a rule violation factory is to create a rule violation instance for your handler (spoiler).
    In case you want to provide additional data in your rule violation, you can create a custom one. However,
    adding additional date here is discouraged, as you would need a custom renderer to actually use this
    additional data. Such extensions are not language agnostic.

## 8.  Create a version handler
*   Now you need to create your version handler, as we did with [`SwiftHandler`](https://github.com/pmd/pmd/blob/pmd/7.0.x/pmd-swift/src/main/java/net/sourceforge/pmd/lang/swift/SwiftHandler.java).
*   This class is sort of a gateway between PMD and all parsing logic specific to your language. It has 2 purposes:
    *   `getRuleViolationFactory` method returns an instance of your rule violation factory *(see step #7)*.
        By default, this returns the default rule violation factory.
    *   `getParser` returns an instance of your parser adapter *(see step #6)*.
        That's the only method, that needs to be implemented here.

## 9.  Create a parser visitor adapter
*   A parser visitor adapter is not needed anymore with PMD 7. The visitor interface now provides a default
    implementation.
*   The visitor for ANTLR based AST is generated along the parser from the ANTLR grammar file. The
    base interface for a visitor is [`AstVisitor`](https://github.com/pmd/pmd/blob/pmd/7.0.x/pmd-core/src/main/java/net/sourceforge/pmd/lang/ast/AstVisitor.java).
*   The generated visitor class for Swift is called `SwiftVisitor`.
*   In order to help use this visitor later on, a base visitor class should be created.
    See [`SwiftVisitorBase`](https://github.com/pmd/pmd/blob/pmd/7.0.x/pmd-swift/src/main/java/net/sourceforge/pmd/lang/swift/ast/SwiftVisitorBase.java)
    as an example.

## 10. Create a rule chain visitor
*   This step is not needed anymore. For using rule chain, there is no additional adjustment necessary anymore
    in the languages.
*   This feature has been merged into AbstractRule via the overridable method
    {% jdoc !!core::lang.rule.AbstractRule#buildTargetSelector() %}. Individual rules can make use of this optimization
    by overriding this method and return an appropriate RuleTargetSelector.

## 11. Make PMD recognize your language
*   Create your own subclass of `net.sourceforge.pmd.lang.BaseLanguageModule`, see Swift as an example:
    [`SwiftLanguageModule`](https://github.com/pmd/pmd/blob/pmd/7.0.x/pmd-swift/src/main/java/net/sourceforge/pmd/lang/swift/SwiftLanguageModule.java).
*   Add your default version with `addDefaultVersion` in your language module's constructor.
*   Add for each additional version of your language a call to `addVersion` as well.
*   Create the service registration via the text file `src/main/resources/META-INF/services/net.sourceforge.pmd.lang.Language`. Add your fully qualified class name as a single line into it.

## 12. Create an abstract rule class for the language
*   You need to create your own `AbstractRule` in order to interface your language with PMD's generic rule
    execution.
*   See [`AbstractSwiftRule`](https://github.com/pmd/pmd/blob/pmd/7.0.x/pmd-swift/src/main/java/net/sourceforge/pmd/lang/swift/AbstractSwiftRule.java) as an example.
*   While the rule basically just extends
    [`AntlrBaseRule`](https://github.com/pmd/pmd/blob/pmd/7.0.x/pmd-core/src/main/java/net/sourceforge/pmd/lang/ast/impl/antlr4/AntlrBaseRule.java) without adding anything, every language should have its own base class for rule.
    This helps to organize the code.
*   All other rules for your language should extend this class. The purpose of this class is to provide a visitor
    via the method `buildVisitor()` for analyzing the AST. The provided visitor only implements the visit methods
    for specific AST nodes. The other node types use the default behavior and you don't need to care about them.

## 13. Create rules
*   Creating rules is already pretty well documented in PMD - and it’s no different for a new language, except you
    may have different AST nodes.
*   PMD supports 2 types of rules, through visitors or XPath.
*   To add a visitor rule:
    *   You need to extend the abstract rule you created on the previous step, you can use the swift
    rule [UnavailableFunctionRule](https://github.com/pmd/pmd/blob/pmd/7.0.x/pmd-swift/src/main/java/net/sourceforge/pmd/lang/swift/rule/bestpractices/UnavailableFunctionRule.java)
    as an example. Note, that all rule classes should be suffixed with `Rule` and should be placed
    in a package the corresponds to their category.
*   To add an XPath rule you can follow our guide [Writing XPath Rules](pmd_userdocs_extending_writing_xpath_rules.html).

## 14. Test the rules
*   See UnavailableFunctionRuleTest for example. Each rule has it's own test class.
*   You have to create the category rule set for your language *(see pmd-swift/src/main/resources/bestpractices.xml for example)*
*   When executing the test class
    *   this triggers the unit test to read the corresponding XML file with the rule test data
        *(see `UnavailableFunctionRule.xml` for example)*
    *   This test XML file contains sample pieces of code which should trigger a specified number of
        violations of this rule. The unit test will execute the rule on this piece of code, and verify
        that the number of violations matches.
*   To verify the validity of all the created rulesets, create a subclass of `AbstractRuleSetFactoryTest` (*see `RuleSetFactoryTest` in pmd-swift for example)*.
    This will load all rulesets and verify, that all required attributes are provided.

    *Note:* You'll need to add your ruleset to `categories.properties`, so that it can be found.

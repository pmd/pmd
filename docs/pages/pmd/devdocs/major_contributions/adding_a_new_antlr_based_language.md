---
title: Adding PMD support for a new ANTLR grammar based language
short_title: Adding a new language with ANTLR
tags: [devdocs, extending]
summary: "How to add a new language to PMD using ANTLR grammar."
last_updated: July 21, 2019
sidebar: pmd_sidebar
permalink: pmd_devdocs_major_adding_new_language.html
folder: pmd/devdocs
---


## 1.  Start with a new sub-module.
*   See pmd-swift for examples.

## 2.  Implement an AST parser for your language
*	ANTLR gives you this for free.

## 3.  Create AST node classes
*	We provide an [AntlrBaseNode](https://github.com/pmd/pmd/blob/master/pmd-core/src/main/java/net/sourceforge/pmd/lang/ast/AntlrBaseNode.java). 
*	We override ANTLR auto-generated code to provide this for free, you need to add an ANT script similar to the [swift scrip](https://github.com/pmd/pmd/blob/master/pmd-swift/src/main/ant/antlr4.xml).
*	You can extend AntlrBaseNode and override any method that you require, but on most cases you won't need to do anything.

## 4.  Compile your parser 
*	We override ANTLR auto-generated code to provide this for free, similar to the step before, you will need to use the ANT script.
*	You should review the [swift pom](https://github.com/pmd/pmd/blob/master/pmd-swift/pom.xml). Don't forget to enable visitor generation property. 

## 5.  Create a TokenManager
*   We provide a default implementation using [AntlrTokenManager](https://github.com/pmd/pmd/blob/master/pmd-core/src/main/java/net/sourceforge/pmd/lang/antlr/AntlrTokenManager.java) that uses an [AntlrTokenizer](https://github.com/pmd/pmd/blob/master/pmd-core/src/main/java/net/sourceforge/pmd/cpd/AntlrTokenizer.java). 
*	You must create your own AntlrTokenizer such as we do with [SwiftTokenizer](https://github.com/pmd/pmd/blob/master/pmd-swift/src/main/java/net/sourceforge/pmd/cpd/SwiftTokenizer.java).
*	If you wish to filter specific tokens you can create your own implementation of [BaseTokenFilter](https://github.com/pmd/pmd/blob/master/pmd-core/src/main/java/net/sourceforge/pmd/cpd/token/internal/BaseTokenFilter.java) as we did with [SwiftTokenFilter](https://github.com/pmd/pmd/blob/master/pmd-core/src/main/java/net/sourceforge/pmd/cpd/token/AntlrTokenFilter.java).

## 6.  Create a PMD parser “adapter”
*   We provide a [BaseParser](https://github.com/pmd/pmd/blob/master/pmd-core/src/main/java/net/sourceforge/pmd/lang/antlr/AntlrBaseParser.java) implementation that you need to extend to create your own adapter as we do with [SwiftParserAdapter](https://github.com/pmd/pmd/blob/master/pmd-swift/src/main/java/net/sourceforge/pmd/lang/swift/SwiftParserAdapter.java).

## 7.  Create a rule violation factory
*	We provide a [AntlrRuleViolationFactory](https://github.com/pmd/pmd/blob/master/pmd-core/src/main/java/net/sourceforge/pmd/lang/antlr/AntlrRuleViolationFactory.java) as base implementation, you can use that for most scenarios.
*   The purpose of this class is to create a rule violation instance for your handler (spoiler).

## 8.  Create a version handler
*   Now you need to create your version handler, as we did with [SwiftHandler](https://github.com/pmd/pmd/blob/master/pmd-swift/src/main/java/net/sourceforge/pmd/lang/swift/SwiftHandler.java).
*   This class is sort of a gateway between PMD and all parsing logic specific to your language. It has 2 purposes:
    *   `getRuleViolationFactory` method returns an instance of your rule violation factory *(see step #7)*
    *   `getParser` returns an instance of your parser adapter *(see step #6)*

## 9.  Create a parser visitor adapter
*	We provide an [AbstractAntlrVisitor](https://github.com/pmd/pmd/blob/master/pmd-core/src/main/java/net/sourceforge/pmd/lang/antlr/AbstractAntlrVisitor.java) as default implementation, to be able to use this you should also add it to the ANT script we talked about on step #3
*   The purpose of this class is to serve as a pass-through `visitor` implementation, which, for all AST types in your language, just executes visit on the base AST type

## 10. Create a rule chain visitor
*   We provide an [AntlrRuleChainVisitor](https://github.com/pmd/pmd/blob/master/pmd-core/src/main/java/net/sourceforge/pmd/lang/antlr/AntlrRuleChainVisitor.java), you can use that for most scenarios.
*   If you wish to create your own, you should `implement` two `important` methods:
    *   `indexNodes` generates a map of "node type" to "list of nodes of that type". This is used to visit all applicable nodes when a rule is applied.
    *   `visit` method should evaluate what kind of rule is being applied, and execute appropriate logic. Usually it will just check if the rule is a "parser visitor" kind of rule specific to your language, then execute the visitor. If it’s an XPath rule, then we just need to execute evaluate on that.

## 11. Make PMD recognize your language
*   Create your own subclass of `net.sourceforge.pmd.lang.BaseLanguageModule`, see Swift as an example.
*   You’ll need to refer the rule chain visitor created in step #10.
*   Add for each version of your language a call to `addVersion` in your language module’s constructor.
*   Create the service registration via the text file `src/main/resources/META-INF/services/net.sourceforge.pmd.lang.Language`. Add your fully qualified class name as a single line into it.

## 12. Create an abstract rule class for the language
*	You need to create your own `AbstractRule`, our AbstractAntlrVisitor implements this and makes the connection with ANTLR via our ANT script (see step #3).
*	You will have an auto-generated XBaseVisitor class (similar to SwiftBaseVisitor) that you will have to extend as we did with [AbstractSwiftRule](https://github.com/pmd/pmd/blob/master/pmd-swift/src/main/java/net/sourceforge/pmd/lang/swift/AbstractSwiftRule.java).
*   All other rules for your language should extend this class. The purpose of this class is to implement visit methods for all AST types to simply delegate to default behavior. This is useful because most rules care only about specific AST nodes, but PMD needs to know what to do with each node - so this just lets you use default behavior for nodes you don’t care about.

## 13. Create rules
*   Creating rules is already pretty well documented in PMD - and it’s no different for a new language, except you may have different AST nodes.
*	PMD supports 2 types of rules, through visitors or XPath. 
*	To add a visitor rule:
	*	You need to extend the abstract rule you created on the previous step, you can use [this rule](https://github.com/pmd/pmd/blob/master/pmd-swift/src/main/java/net/sourceforge/pmd/lang/swift/rule/bestpractices/ProhibitedInterfaceBuilderRule.java) as an example.
*	To add an XPath rule you can follow our [guide](https://pmd.github.io/pmd-6.15.0/pmd_userdocs_extending_writing_xpath_rules.html).

## 14. Test the rules
*   See BasicRulesTest for example
*   You have to create a rule set for your language *(see vm/basic.xml for example)*
*   For each rule in this set you want to test, call `addRule` method in setUp of the unit test
    *   This triggers the unit test to read the corresponding XML file with rule test data *(see `EmptyForeachStmtRule.xml` for example)*
    *   This test XML file contains sample pieces of code which should trigger a specified number of violations of this rule. The unit test will execute the rule on this piece of code, and verify that the number of violations matches
*   To verify the validity of the created ruleset, create a subclass of `AbstractRuleSetFactoryTest` (*see `RuleSetFactoryTest` in pmd-vm for example)*.
    This will load all rulesets and verify, that all required attributes are provided.

    *Note:* You'll need to add your ruleset to `rulesets.properties`, so that it can be found.

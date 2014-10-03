# Howto add a new language to PMD

1.  Implement an AST parser for your language
    - ideally an AST parser should be implemented as a JJT file (see VmParser.jjt or Java.jjt for example)
    - there is nothing preventing any other parser implementation, as long as you have some way to convert an input stream into an AST tree. Doing it as a JJT simplifies maintenance down the road.
    - See this link for reference: https://javacc.java.net/doc/JJTree.html
2. Create AST node classes
    - for each AST node that your parser can generate, there should be a class
    - the name of the AST class should be "AST" + "whatever is the name of the node in JJT file".
    -- for example, if JJT contains a node called "IfStatement", there should be a class called "ASTIfStatement"
    - each AST class should have two constructors: one that takes an int id; and one that takes an instance of the parser, and an int id
    - it's a good idea to create a parent AST class for all AST classes of the language. This simplies rule creation later. See SimpleNode for Velocity and AbstractJavaNode for Java for example.
    - Note: These AST node classes are generated usually once by javacc/jjtree and can then be modified as needed.
3. Compile your parser (if using JJT)
    - an ant script is being used to compile jjt files into classes. This is in alljavacc.xml file.
    - in the file, create a new target for your language. Use vmjjtree or javajjtree as an example.
    - inside the alljavacctarget, add your new target to the "depends" list just before cleanup
4. Create a TokenManager
    - create a new class that implements the TokenManager interface (see VmTokenManager or JavaTokenManager for example)
5. Create a PMD parser "adapter"
    - create a new class that extends AbstractParser
    - there are two important methods to implement
    -- createTokenManager method should return a new instance of a token manager for your language (see step #4)
    -- parse method should return the root node of the AST tree obtained by parsing the Reader source
    -- see VmParser class as an example
6. Create a rule violation factory
    - extend AbstractRuleViolationFactory (see VmRuleViolationFactory for example)
    - the purpose of this class is to createa rule violation instance specific to your language
7. Create a version handler
    - extend AbstractLanguageVersionHandler (see VmHandler for example)
    - this class is sort of a gateway between PMD and all parsing logic specific to your language. It has 3 purposes:
    -- getRuleViolationFactory method returns an instance of your rule violation factory (see step #6)
    -- getParser returns an instance of your parser adapter (see step #5)
    -- getDumpFacade returns a VisitorStarter that allows to dump a text representation of the AST into a writer (likely for debugging purposes)
8. Create a parser visitor adapter
    - if you use JJT to generate your parser, it should also generate an interface for a parser visitor (see VmParserVisitor for example)
    - create a class that implements this auto-generated interface (see VmParserVisitorAdapter for example)
    - the purpose of this class is to serve as a pass-through visitor implementation, which, for all AST types in your language, just executes visit on the base AST type
9. Create a rule chain visitor
    - extend AbstractRuleChainVisitor (see VmRuleChainVisitor for example)
    - this class should implement two important methods:
    -- indexNodes generates a map of "node type" to "list of nodes of that type". This is used to visit all applicable nodes when a rule is applied.
    -- visit method should evaluate what kind of rule is being applied, and execute appropriate logic. Usually it will just check if the rule is a "parser visitor" kind of rule specific to your language, then execute the visitor. If it's an XPath rule, then we just need to execute evaluate on that.
10. Make PMD recognize your language
    - In the net.sourceforge.pmd.lang.Language enum, add a new entry for your language. This entry shold refer to the rule chain visitor created in step #9
    - In the net.sourceforge.pmd.lang.LanguageVersion enum, add a new entry for each version of your language. The entries should refer to the Language enum just created, and the version handler created in step #7
11. Create an abstract rule class for the language
    - extend AbstractRule and implement the parser visitor interface for your language (see AbstractVmRule for example)
    - all other rules for your language should extend this class. The purpose of this class is to implement visit methods for all AST types to simply delegate to default behavior. This is useful because most rules care only about specific AST nodes, but PMD needs to know what to do with each node - so this just lets you use default behavior for nodes you don't care about.
12. Create rules
    - rules are ceated by extending the abstract rule class created in step #11 (see EmptyForeachStmtRule for example)
    - creating rules is already pretty well documented in PMD - and it's no different for a new language, except you may have different AST nodes.
13. Test the rules
    - see BasicRulesTest for example
    - you have to create a rule set for your language (see vm/basic.xml for example)
    - for each rule in this set you want to test, call addRule method in setUp of the unit test
    -- this triggers the unit test to read the corresponding XML file with rule test data (see EmptyForeachStmtRule.xml for example)
    -- this test XML file contains sample pieces of code which should trigger a specified number of violations of this rule. The unit test will execute the rule on this piece of code, and verify that the number of violations matches

package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.XPathRule;

/**
 * @author daniels
 *
 * Cannot Implement:
 * <LI> Cyclomatic Complexity Rule - don't understand it
 * <LI> Import From Same Package Rule - cannot check for sub packages
 * <LI> StringToString - may be possible, but a better grammar would help.
 * <LI> UnnecessaryConversionTemporaryRule - don't understand it
 * <LI> UnusedFormalParameter - may be possible, but a better grammar would help. 
 * <LI> UnusedImportsRule - may be possible, but a better grammar would help.
 * <LI> UnusedLocalVariableFieldRule - may be possible, but a better grammar would help.
 * <LI> UnusedPrivateFieldRule - may be possible, but a better grammar would help.
 * <LI> UnusedPrivateMethodRule - may be possible, but a better grammar would help.
 * <HR> 
 *
 * Partial Implementation
 * <LI> DuplicateImportsRuleTest - cannot detect specific vs. general imports  
 * 
 * <HR>
 *  
 * Differing Implementation
 * <LI> AvoidDuplicateLiteralsRule - marks all duplicate nodes
 *
 */
public class XPathRuleTest extends RuleTst {

    XPathRule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.setMessage("XPath Rule Failed");
    }
    
//    public void testUnusedFormalParameterRule() throws Throwable {
//        rule.addProperty("xpath", "//MethodDeclaration[@Private='true'][@Native='false']//FormalParameters//VariableDeclaratorId");
//        runTestFromFile("UnusedFormalParam1.java", 1, rule);
//        runTestFromFile("UnusedFormalParam2.java", 0, rule);
//        runTestFromFile("UnusedFormalParam3.java", 0, rule);
//        runTestFromFile("UnusedFormalParam4.java", 0, rule);
//    }
    
//	public void testUnnecessaryConversionTemporaryRule() throws Throwable{
//        runTestFromFile("UnnecessaryTemporary.java", 6, rule);
//	}
	
//    public void testStringToString() throws Throwable {
//        runTestFromFile("StringToString1.java", 1, rule);
//        runTestFromFile("StringToString2.java", 1, rule);
//        runTestFromFile("StringToString3.java", 1, rule);
//        runTestFromFile("StringToString4.java", 0, rule);
//        runTestFromFile("StringToString5.java", 0, rule);
//        runTestFromFile("StringToString6.java", 1, rule);
//        
//    }

    public void testDeeplyNestedIfStmtsRule() throws Throwable {
        rule.addProperty(
            "xpath",
            "//IfStatement[count(ancestor::IfStatement[not(Statement[2])]) > {0}]");
        rule.addProperty("subst", "1");
        runTestFromFile("AvoidDeeplyNestedIfStmtsRule1.java", 1, rule);
        runTestFromFile("AvoidDeeplyNestedIfStmtsRule2.java", 0, rule);
    }

    /**
     * This differs from the original in that ALL duplicates are marked. 
     * @throws Throwable
     */
    public void testAvoidDuplicateLiteralsRule() throws Throwable {
        //fail("I wouldn't even know where to begin");
        rule.addProperty(
            "xpath",
            "//Literal[@Image = preceding::Literal/@Image or @Image = following::Literal/@Image]");
        runTestFromFile("AvoidDuplicateLiterals1.java", 4, rule);
        runTestFromFile("AvoidDuplicateLiterals2.java", 0, rule);
        runTestFromFile("AvoidDuplicateLiterals3.java", 0, rule);
    }

    /**
     * @todo Figure it out.
     */
    public void testAvoidReassigningParameters() throws Throwable {
        //        fail("I wouldn't even know where to begin");
        rule.addProperty(
            "xpath",
            "//PrimaryExpression[following-sibling::AssignmentOperator][PrimaryPrefix/Name/@Image = ancestor::MethodDeclaration/MethodDeclarator/FormalParameters/FormalParameter/VariableDeclaratorId/@Image]");
        runTestFromFile("AvoidReassigningParameters1.java", 1, rule);
        runTestFromFile("AvoidReassigningParameters2.java", 0, rule);
        runTestFromFile("AvoidReassigningParameters3.java", 1, rule);
        runTestFromFile("AvoidReassigningParameters4.java", 0, rule);
        runTestFromFile("AvoidReassigningParameters5.java", 0, rule);
        runTestFromFile("AvoidReassigningParameters6.java", 0, rule);
    }

    public void testSimplifyBooleanReturns() throws Throwable {
        rule.addProperty("xpath", "//IfStatement[count(.//ReturnStatement//BooleanLiteral) = 2]");
        runTestFromFile("SimplifyBooleanReturns1.java", 1, rule);
        runTestFromFile("SimplifyBooleanReturns2.java", 1, rule);
        runTestFromFile("SimplifyBooleanReturns3.java", 0, rule);
    }

    /**
     * @todo Only a partial implementation
     * Can't figure out how to work with the ImportOnDemand nodes
     */
    public void testDuplicateImportsRule() throws Throwable {
        rule.addProperty(
            "xpath",
            "//ImportDeclaration"
                + "[preceding::ImportDeclaration/Name/@Image = Name/@Image]");
        runTestFromFile("DuplicateImports.java", 1, rule);
        runTestFromFile("DuplicateImports2.java", 1, rule);
        //runTestFromFile("DuplicateImports3.java", 1, rule);
        runTestFromFile("DuplicateImports4.java", 0, rule);
    }
}

package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.XPathRule;
import test.net.sourceforge.pmd.testframework.RuleTst;

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
        runTestFromString(AvoidDeeplyNestedIfStmtsRuleTest.TEST1, 1, rule);
        runTestFromString(AvoidDeeplyNestedIfStmtsRuleTest.TEST2, 0, rule);
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
        runTestFromString(AvoidDuplicateLiteralsRuleTest.TEST1, 4, rule);
        runTestFromString(AvoidDuplicateLiteralsRuleTest.TEST2, 0, rule);
        runTestFromString(AvoidDuplicateLiteralsRuleTest.TEST3, 0, rule);
    }

    /**
     * @todo Figure it out.
     */
    public void testAvoidReassigningParameters() throws Throwable {
        //        fail("I wouldn't even know where to begin");
        rule.addProperty(
            "xpath",
            "//PrimaryExpression[following-sibling::AssignmentOperator][PrimaryPrefix/Name/@Image = ancestor::MethodDeclaration/MethodDeclarator/FormalParameters/FormalParameter/VariableDeclaratorId/@Image]");
        runTestFromString(AvoidReassigningParametersRuleTest.TEST1, 1, rule);
        runTestFromString(AvoidReassigningParametersRuleTest.TEST2, 0, rule);
        runTestFromString(AvoidReassigningParametersRuleTest.TEST3, 1, rule);
        runTestFromString(AvoidReassigningParametersRuleTest.TEST4, 0, rule);
        runTestFromString(AvoidReassigningParametersRuleTest.TEST5, 0, rule);
        runTestFromString(AvoidReassigningParametersRuleTest.TEST6, 0, rule);
    }
}

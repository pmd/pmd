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
//        runTest("UnusedFormalParam1.java", 1, rule);
//        runTest("UnusedFormalParam2.java", 0, rule);
//        runTest("UnusedFormalParam3.java", 0, rule);
//        runTest("UnusedFormalParam4.java", 0, rule);
//    }
    
//	public void testUnnecessaryConversionTemporaryRule() throws Throwable{
//        runTest("UnnecessaryTemporary.java", 6, rule);
//	}
	
//    public void testStringToString() throws Throwable {
//        runTest("StringToString1.java", 1, rule);
//        runTest("StringToString2.java", 1, rule);
//        runTest("StringToString3.java", 1, rule);
//        runTest("StringToString4.java", 0, rule);
//        runTest("StringToString5.java", 0, rule);
//        runTest("StringToString6.java", 1, rule);
//        
//    }


    public void testDeeplyNestedIfStmtsRule() throws Throwable {
        rule.addProperty(
            "xpath",
            "//IfStatement[count(ancestor::IfStatement[not(Statement[2])]) > {0}]");
        rule.addProperty("subst", "1");
        runTest("AvoidDeeplyNestedIfStmtsRule1.java", 1, rule);
        runTest("AvoidDeeplyNestedIfStmtsRule2.java", 0, rule);
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
        runTest("AvoidDuplicateLiterals1.java", 4, rule);
        runTest("AvoidDuplicateLiterals2.java", 0, rule);
        runTest("AvoidDuplicateLiterals3.java", 0, rule);
    }

    /**
     * @todo Figure it out.
     */
    public void testAvoidReassigningParameters() throws Throwable {
        //        fail("I wouldn't even know where to begin");
        rule.addProperty(
            "xpath",
            "//PrimaryExpression[following-sibling::AssignmentOperator][PrimaryPrefix/Name/@Image = ancestor::MethodDeclaration/MethodDeclarator/FormalParameters/FormalParameter/VariableDeclaratorId/@Image]");
        runTest("AvoidReassigningParameters1.java", 1, rule);
        runTest("AvoidReassigningParameters2.java", 0, rule);
        runTest("AvoidReassigningParameters3.java", 1, rule);
        runTest("AvoidReassigningParameters4.java", 0, rule);
        runTest("AvoidReassigningParameters5.java", 0, rule);
        runTest("AvoidReassigningParameters6.java", 0, rule);
    }

    public void testDontImportJavaLang() throws Throwable {
        rule.addProperty(
            "xpath",
            "//ImportDeclaration"
                + "[starts-with(Name/@Image, 'java.lang')]"
                + "[not(starts-with(Name/@Image, 'java.lang.ref'))]"
                + "[not(starts-with(Name/@Image, 'java.lang.reflect'))]");
        runTest("DontImportJavaLang1.java", 1, rule);
        runTest("DontImportJavaLang2.java", 1, rule);
        runTest("DontImportJavaLang3.java", 0, rule);
    }

    public void testSimplifyBooleanReturns() throws Throwable {
        rule.addProperty("xpath", "//IfStatement[count(.//ReturnStatement//BooleanLiteral) = 2]");
        runTest("SimplifyBooleanReturns1.java", 1, rule);
        runTest("SimplifyBooleanReturns2.java", 1, rule);
        runTest("SimplifyBooleanReturns3.java", 0, rule);
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
        runTest("DuplicateImports.java", 1, rule);
        runTest("DuplicateImports2.java", 1, rule);
        //runTest("DuplicateImports3.java", 1, rule);
        runTest("DuplicateImports4.java", 0, rule);
    }
}

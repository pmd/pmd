package test.net.sourceforge.pmd.rules;
import net.sourceforge.pmd.rules.XPathRule;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Node;

import javax.xml.transform.TransformerException;

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
    
    public void testStringInstantiationRule() throws Throwable {
        rule.addProperty("xpath", "//AllocationExpression[Name/@Image='String'][count(.//Expression) < 2][not(ArrayDimsAndInits)]");
        runTest("StringInstantiation1.java", 2, rule);
        runTest("StringInstantiation2.java", 0, rule);
        runTest("StringInstantiation3.java", 0, rule);
        runTest("StringInstantiation4.java", 0, rule);
    }

	public void testSimplifyBooleanReturns() throws Throwable {
	    rule.addProperty("xpath", "//IfStatement[count(.//ReturnStatement//BooleanLiteral) = 2]");
        runTest("SimplifyBooleanReturns1.java", 1, rule);
        runTest("SimplifyBooleanReturns2.java", 1, rule);
        runTest("SimplifyBooleanReturns3.java", 0, rule);
	}

    public void testShortMethodName() throws Throwable {
        rule.addProperty("xpath", "//MethodDeclarator[string-length(@Image) < 3]");
        runTest("ShortMethodName0.java", 0, rule);
        runTest("ShortMethodName1.java", 1, rule);
        runTest("ShortMethodName2.java", 2, rule);
        runTest("ShortMethodName3.java", 1, rule);
    }

	public void testShortVariable() throws Throwable {
        rule.addProperty("xpath", "//VariableDeclaratorId[string-length(@Image) < 3][not(ancestor::ForInit)]");
        runTest("ShortVariableField.java", 1, rule);
        runTest("ShortVariableLocal.java", 1, rule);
        runTest("ShortVariableFor.java", 0, rule);
        runTest("ShortVariableParam.java", 1, rule);
        runTest("ShortVariableNone.java", 0, rule);
	}

    public void testLongVariableRule() throws Throwable {
        rule.addProperty("xpath", "//VariableDeclaratorId[string-length(@Image) > 12]");
        runTest("LongVariableField.java", 1, rule);
        runTest("LongVariableLocal.java", 1, rule);
        runTest("LongVariableFor.java", 1, rule);
        runTest("LongVariableParam.java", 1, rule);
        runTest("LongVariableNone.java", 0, rule);
    }

    public void testJUnitStaticSuiteRule() throws Throwable {
        rule.addProperty(
            "xpath",
            "//MethodDeclaration[not(@Static='true') or not(@Public='true')][MethodDeclarator/@Image='suite']");
        runTest("junit/JUnitStaticSuite1.java", 1, rule);
        runTest("junit/JUnitStaticSuite2.java", 0, rule);
        runTest("junit/JUnitStaticSuite3.java", 1, rule);
    }

    public void testJUnitSpellingRule() throws Throwable {
        rule.addProperty(
            "xpath",
            "//MethodDeclarator[(not(@Image = 'setUp') and translate(@Image, 'SETuP', 'setUp') = 'setUp') or (not(@Image = 'tearDown') and translate(@Image, 'TEARdOWN', 'tearDown') = 'tearDown')][FormalParameters[count(*) = 0]]");
        runTest("junit/JUnitSpelling1.java", 2, rule);
        runTest("junit/JUnitSpelling2.java", 2, rule);
        runTest("junit/JUnitSpelling3.java", 0, rule);
        runTest("junit/JUnitSpelling4.java", 0, rule);
        runTest("junit/JUnitSpelling5.java", 0, rule);
    }

    public void testIfStmtsMustUseBraces() throws Throwable {
        rule.addProperty("xpath", "//IfStatement[count(*) < 3][not(Statement/Block)]");
        runTest("IfStmtsMustUseBraces1.java", 1, rule);
        runTest("IfStmtsMustUseBraces2.java", 0, rule);
    }

    public void testIfElseStmtsMustUseBraces() throws Throwable {
        rule.addProperty("xpath", "//IfStatement[count(*) > 2][not(Statement/Block)]");
        runTest("IfElseStmtsNeedBraces1.java", 1, rule);
        runTest("IfElseStmtsNeedBraces2.java", 0, rule);
    }

    public void testForLoopsMustUseBracesRule() throws Throwable {
        rule.addProperty("xpath", "//ForStatement[not(Statement/Block)]");
        runTest("ForLoopsNeedBraces1.java", 1, rule);
        runTest("ForLoopsNeedBraces2.java", 0, rule);
        runTest("ForLoopsNeedBraces3.java", 1, rule);
        runTest("ForLoopsNeedBraces4.java", 1, rule);
        runTest("ForLoopsNeedBraces5.java", 1, rule);
    }

    public void testWhileLoopsMustUseBraces() throws Throwable {
        rule.addProperty("xpath", "//WhileStatement[not(Statement/Block)]");
        runTest("WhileLoopsNeedBraces1.java", 1, rule);
        runTest("WhileLoopsNeedBraces2.java", 0, rule);
    }

    public void testAssignmentInOperand() throws Throwable {
        rule.addProperty(
            "xpath",
            "//*[name()='WhileStatement' or name()='IfStatement'][Expression//AssignmentOperator]");
        runTest("AssignmentInOperand1.java", 1, rule);
        runTest("AssignmentInOperand2.java", 0, rule);
        runTest("AssignmentInOperand3.java", 0, rule);
        runTest("AssignmentInOperand4.java", 1, rule);
    }

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

    public Node assertNodeName(Node root, String path, String name) throws TransformerException {
        Node node = XPathAPI.selectSingleNode(root, path);
        assertNotNull("Node is null", node);
        assertEquals(name, node.getNodeName());
        return node;
    }
}

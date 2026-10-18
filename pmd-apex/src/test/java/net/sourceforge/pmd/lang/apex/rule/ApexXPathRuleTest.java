/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule;

import static net.sourceforge.pmd.lang.test.ast.TestUtilsKt.assertSize;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.apex.ast.ApexParserTestBase;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.lang.rule.xpath.XPathRule;
import net.sourceforge.pmd.reporting.Report;
import net.sourceforge.pmd.util.treeexport.XmlTreeRenderer;

/**
 * @author daniels
 */
class ApexXPathRuleTest extends ApexParserTestBase {

    private XPathRule makeXPath(String expression) {
        return apex.newXpathRule(expression);
    }


    @Test
    void testFileNameInXpath() {
        Report report = apex.executeRule(makeXPath("/UserClass[pmd:fileName() = 'Foo.cls']"),
                                         "class Foo {}",
                                         FileId.fromPathLikeString("src/Foo.cls"));

        assertSize(report, 1);
    }

    @Test
    void testBooleanExpressions() {
        Report report = apex.executeRuleOnResource(makeXPath("//BooleanExpression[@Op='&&']"),
                                                   "BooleanExpressions.cls");
        assertSize(report, 1);
    }

    @Test
    void testDmlRunAsModeIsVisibleInXpathAndXml() throws IOException {
        String source = "public class DmlModes {\n"
            + "    void example(Account a, Account b) {\n"
            + "        insert a;\n"
            + "        insert as user a;\n"
            + "        insert as system a;\n"
            + "        update a;\n"
            + "        update as user a;\n"
            + "        update as system a;\n"
            + "        upsert a;\n"
            + "        upsert as user a;\n"
            + "        upsert as system a;\n"
            + "        delete a;\n"
            + "        delete as user a;\n"
            + "        delete as system a;\n"
            + "        undelete a;\n"
            + "        undelete as user a;\n"
            + "        undelete as system a;\n"
            + "        merge a b;\n"
            + "        merge as user a b;\n"
            + "        merge as system a b;\n"
            + "    }\n"
            + "}";

        List<String> dmlNodes = Arrays.asList("DmlInsertStatement", "DmlUpdateStatement", "DmlUpsertStatement",
                                              "DmlDeleteStatement", "DmlUndeleteStatement", "DmlMergeStatement");

        for (String nodeName : dmlNodes) {
            assertSize(apex.executeRule(makeXPath("//" + nodeName), source), 3);
            assertSize(apex.executeRule(makeXPath("//" + nodeName + "[not(@RunAsMode)]"), source), 1);
            assertSize(apex.executeRule(makeXPath("//" + nodeName + "[@RunAsMode='USER_MODE']"), source), 1);
            assertSize(apex.executeRule(makeXPath("//" + nodeName + "[@RunAsMode='SYSTEM_MODE']"), source), 1);
        }

        Node root = apex.parse(source).getMainNode();
        StringBuilder xml = new StringBuilder();
        new XmlTreeRenderer().renderSubtree(root, xml);
        String xmlText = xml.toString();
        assertEquals(6, occurrencesOf(xmlText, "RunAsMode='USER_MODE'"));
        assertEquals(6, occurrencesOf(xmlText, "RunAsMode='SYSTEM_MODE'"));
    }

    private int occurrencesOf(String text, String substring) {
        return text.split(substring, -1).length - 1;
    }
}

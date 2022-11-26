/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xml.rule;

import static net.sourceforge.pmd.lang.ast.test.TestUtilsKt.assertSize;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.lang.xml.XmlParsingHelper;

class XmlXPathRuleTest {

    private static final String A_URI = "http://soap.sforce.com/2006/04/metadata";
    private static final String FXML_IMPORTS = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                                               + "\n"
                                               + "<!--suppress JavaFxDefaultTag -->\n"
                                               + "\n"
                                               + "<?import javafx.scene.layout.AnchorPane?>\n"
                                               + "<?import javafx.scene.layout.BorderPane?>\n"
                                               + "<?import javafx.scene.control.Tooltip?>\n"
                                               + "<?import javafx.scene.control.Label?>\n"
                                               + "<?import org.kordamp.ikonli.javafx.FontIcon?>\n"
                                               + "<AnchorPane prefHeight=\"750.0\" prefWidth=\"1200.0\" stylesheets=\"@../css/designer.css\" xmlns=\"http://javafx.com/javafx/8\" xmlns:fx=\"http://javafx.com/fxml/1\">\n"
                                               + "</AnchorPane>";
    final XmlParsingHelper xml = XmlParsingHelper.XML;

    private Rule makeXPath(String expression) {
        return makeXPath(expression, "");
    }

    private Rule makeXPath(String expression, String nsUri) {
        DomXPathRule rule = new DomXPathRule(expression, nsUri);
        rule.setLanguage(xml.getLanguage());
        rule.setMessage("XPath Rule Failed");
        return rule;
    }


    @Test
    void testFileNameInXpath() {
        Report report = xml.executeRule(makeXPath("//b[pmd:fileName() = 'Foo.xml']"),
                                        "<a><b></b></a>",
                                        "src/Foo.xml");

        assertSize(report, 1);
    }

    @Test
    void testTextFunctionInXpath() {
        // https://github.com/pmd/pmd/issues/915
        Report report = xml.executeRule(makeXPath("//app[text()[1]='app2']"),
                                        "<a><app>app2</app></a>");

        assertSize(report, 1);
    }

    @Test
    void testRootNodeWildcardUri() {
        // https://github.com/pmd/pmd/issues/3413#issuecomment-1072614398
        Report report = xml.executeRule(makeXPath("/*:Flow"),
                                        "<Flow xmlns=\"http://soap.sforce.com/2006/04/metadata\">\n"
                                        + "</Flow>");

        assertSize(report, 1);
    }

    @Test
    void testNoNamespaceRoot() {
        Report report = xml.executeRule(makeXPath("/Flow"),
                                        "<Flow>\n"
                                        + "</Flow>");

        assertSize(report, 1);
    }

    @Test
    void testNamespaceDescendantWrongDefaultUri() {
        Report report = xml.executeRule(makeXPath("//a"),
                                        "<Flow xmlns='" + A_URI + "'><a/></Flow>");

        assertSize(report, 0);
    }

    @Test
    void testNamespaceDescendantOkUri() {
        Report report = xml.executeRule(makeXPath("//a", A_URI),
                                        "<Flow xmlns='" + A_URI + "'><a/></Flow>");

        assertSize(report, 1);

        report = xml.executeRule(makeXPath("//*:a"),
                                 "<Flow xmlns='" + A_URI + "'><a/></Flow>");

        assertSize(report, 1);
    }

    @Test
    void testNamespaceDescendantWildcardUri() {
        Report report = xml.executeRule(makeXPath("//*:a"),
                                        "<Flow xmlns='" + A_URI + "'><a/></Flow>");

        assertSize(report, 1);
    }

    @Test
    void testNamespacePrefixDescendantWildcardUri() {
        Report report = xml.executeRule(makeXPath("//*:Flow"),
                                        "<my:Flow xmlns:my='" + A_URI + "'><a/></my:Flow>");

        assertSize(report, 1);
    }

    @Test
    void testNamespacePrefixDescendantOkUri() {
        Report report = xml.executeRule(makeXPath("//Flow", A_URI),
                                        "<my:Flow xmlns:my='" + A_URI + "'><a/></my:Flow>");

        assertSize(report, 1);
    }

    @Test
    void testNamespacePrefixDescendantWrongUri() {
        Report report = xml.executeRule(makeXPath("//Flow", "wrongURI"),
                                        "<my:Flow xmlns:my='" + A_URI + "'><a/></my:Flow>");

        assertSize(report, 0);
    }

    @Test
    void testRootExpr() {
        Report report = xml.executeRule(makeXPath("/"),
                                        "<Flow><a/></Flow>");

        assertSize(report, 1);
    }

    @Test
    void testProcessingInstructions() {
        Report report = xml.executeRule(makeXPath("/child::processing-instruction()", "http://javafx.com/javafx/8"),
                                        FXML_IMPORTS);

        assertSize(report, 5);
    }

    @Test
    void testProcessingInstructionsNamed() {
        Report report = xml.executeRule(makeXPath("/child::processing-instruction('import')"),
                                        FXML_IMPORTS);

        assertSize(report, 5);
    }

    @Test
    void testProcessingInstructionXML() {
        // <?xml ?> does not create a PI
        Report report = xml.executeRule(makeXPath("/child::processing-instruction('xml')", "http://javafx.com/javafx/8"),
                                        FXML_IMPORTS);

        assertSize(report, 0);
    }

    @Test
    void testComments() {
        Report report = xml.executeRule(makeXPath("/child::comment()[fn:starts-with(fn:string(.), 'suppress')]"),
                                        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                                        + "<!--suppress JavaFxDefaultTag -->\n"
                                        + "<AnchorPane prefHeight=\"750.0\" prefWidth=\"1200.0\" stylesheets=\"@../css/designer.css\" xmlns=\"http://javafx.com/javafx/8\" xmlns:fx=\"http://javafx.com/fxml/1\">\n"
                                        + "</AnchorPane>");

        assertSize(report, 1);
    }

    @Test
    void testXmlNsFunctions() {
        // https://github.com/pmd/pmd/issues/2766
        Report report = xml.executeRule(
            makeXPath("/manifest[namespace-uri-for-prefix('android', .) = 'http://schemas.android.com/apk/res/android']"),
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
            + "<manifest xmlns:android=\"http://schemas.android.com/apk/res/android\"\n"
            + "         package=\"com.a.b\">\n"
            + "\n"
            + "        <application\n"
            + "             android:allowBackup=\"true\"\n"
            + "             android:icon=\"@mipmap/ic_launcher\"\n"
            + "             android:label=\"@string/app_name\"\n"
            + "             android:roundIcon=\"@mipmap/ic_launcher_round\"\n"
            + "             android:supportsRtl=\"true\"\n"
            + "             android:theme=\"@style/AppTheme\">\n"
            + "         <activity android:name=\".MainActivity\">\n"
            + "             <intent-filter>\n"
            + "                 <action android:name=\"android.intent.action.MAIN\" />\n"
            + "\n"
            + "                 <category android:name=\"android.intent.category.LAUNCHER\" />\n"
            + "             </intent-filter>\n"
            + "         </activity>\n"
            + "     </application>\n"
            + "\n"
            + "</manifest>");

        assertSize(report, 1);
    }

    @Test
    void testLocationFuns() {
        Rule rule = makeXPath("//Flow[pmd:startLine(.) != pmd:endLine(.)]");
        Report report = xml.executeRule(rule, "<Flow><a/></Flow>");
        assertSize(report, 0);
        report = xml.executeRule(rule, "<Flow>\n<a/>\n</Flow>");
        assertSize(report, 1);
    }

}

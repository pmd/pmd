/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.apex.ApexLanguageModule;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.ast.ApexParserTest;
import net.sourceforge.pmd.lang.apex.ast.ApexParserTestHelpers;
import net.sourceforge.pmd.lang.apex.ast.ApexParserVisitorAdapter;
import net.sourceforge.pmd.lang.apex.metrics.signature.ApexOperationSigMask;

import apex.jorje.semantic.ast.compilation.Compilation;

/**
 * @author Clément Fournier
 */
public class ApexMetricsVisitorTest extends ApexParserTest {

    @Test
    public void testProjectMirrorNotNull() {
        assertNotNull(ApexMetrics.getFacade().getProjectMirror());
    }


    @Test
    public void testOperationsAreThere() {
        ApexNode<Compilation> acu = parseAndVisitForString("public with sharing class MetadataDeployController \n"
                                                               + "{\n"
                                                               + "\tprivate class Foo {\n"
                                                               + "}\n"
                                                               + "\n"
                                                               + "\tglobal String ZipData { get; set; }\t\n"
                                                               + "\t\n"
                                                               + "\tpublic MetadataService.AsyncResult AsyncResult {get; private set;}\n"
                                                               + "\t\n"
                                                               + "\tpublic String getPackageXml(String page)\n"
                                                               + "\t{\n"
                                                               + "\t\treturn '<?xml version=\"1.0\" encoding=\"UTF-8\"?>' + \n"
                                                               + "\t\t\t'<Package xmlns=\"http://soap.sforce.com/2006/04/metadata\">' + \n"
                                                               + "    \t\t\t'<types>' + \n"
                                                               + "        \t\t\t'<members>HelloWorld</members>' +\n"
                                                               + "        \t\t\t'<name>ApexClass</name>' + \n"
                                                               + "    \t\t\t'</types>' + \n"
                                                               + "    \t\t\t'<version>26.0</version>' + \n"
                                                               + "\t\t\t'</Package>';\t\t\n"
                                                               + "\t}\n"
                                                               + "\t\n"
                                                               + "\tpublic String getHelloWorldMetadata()\n"
                                                               + "\t{\n"
                                                               + "\t\treturn '<?xml version=\"1.0\" encoding=\"UTF-8\"?>' +\n"
                                                               + "\t\t\t'<ApexClass xmlns=\"http://soap.sforce.com/2006/04/metadata\">' +\n"
                                                               + "\t\t\t    '<apiVersion>28.0</apiVersion>' + \n"
                                                               + "\t\t\t    '<status>Active</status>' +\n"
                                                               + "\t\t\t'</ApexClass>';\t\t\n"
                                                               + "\t}\n"
                                                               + "\t\n"
                                                               + "\tpublic String getHelloWorld()\t\n"
                                                               + "\t{\n"
                                                               + "\t\treturn 'public class HelloWorld' + \n"
                                                               + "\t\t\t'{' + \n"
                                                               + "\t\t\t\t'public static void helloWorld()' +\n"
                                                               + "\t\t\t\t'{' + \n"
                                                               + "\t\t\t\t\t'System.debug(\\' Hello World\\');' +\n"
                                                               + "\t\t\t\t'}' +\n"
                                                               + "\t\t\t'}';\n"
                                                               + "\t}"
                                                               + "}");

        final ApexSignatureMatcher toplevel = ApexMetrics.getFacade().getProjectMirror();

        final ApexOperationSigMask opMask = new ApexOperationSigMask();

        // We could parse qnames from string but probably simpler to do that
        acu.jjtAccept(new ApexParserVisitorAdapter() {
            @Override
            public Object visit(ASTMethod node, Object data) {
                if (!node.getImage().matches("(<clinit>|<init>|clone)")) {
                    assertTrue(toplevel.hasMatchingSig(node.getQualifiedName(), opMask));
                }

                return data;
            }
        }, null);
    }


    static ApexNode<Compilation> parseAndVisitForString(String source) {
        LanguageVersionHandler languageVersionHandler = LanguageRegistry.getLanguage(ApexLanguageModule.NAME)
                                                                        .getDefaultVersion().getLanguageVersionHandler();
        ApexNode<Compilation> acu = ApexParserTestHelpers.parse(source);
        languageVersionHandler.getSymbolFacade().start(acu);
        languageVersionHandler.getMetricsVisitorFacade().start(acu);
        return acu;
    }
}

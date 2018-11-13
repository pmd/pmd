/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.multifile;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.apex.ApexLanguageModule;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.ast.ApexParserTest;
import net.sourceforge.pmd.lang.apex.ast.ApexParserTestHelpers;
import net.sourceforge.pmd.lang.apex.ast.ApexParserVisitorAdapter;
import net.sourceforge.pmd.lang.apex.metrics.ApexSignatureMatcher;
import net.sourceforge.pmd.lang.apex.metrics.signature.ApexOperationSigMask;

import apex.jorje.semantic.ast.compilation.Compilation;

/**
 * @author Clément Fournier
 */
public class ApexMultifileVisitorTest extends ApexParserTest {

    @Test
    public void testProjectMirrorNotNull() {
        assertNotNull(ApexProjectMirror.INSTANCE);
    }


    @Test
    public void testOperationsAreThere() throws IOException {
        ApexNode<Compilation> acu = parseAndVisitForString(
            IOUtils.toString(ApexMultifileVisitorTest.class.getResourceAsStream("MetadataDeployController.cls"),
                    StandardCharsets.UTF_8));

        final ApexSignatureMatcher toplevel = ApexProjectMirror.INSTANCE;

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
        languageVersionHandler.getMultifileFacade().start(acu);
        return acu;
    }
}

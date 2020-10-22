/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.multifile;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.ast.ApexParserTestBase;
import net.sourceforge.pmd.lang.apex.ast.ApexVisitorBase;
import net.sourceforge.pmd.lang.apex.metrics.ApexSignatureMatcher;
import net.sourceforge.pmd.lang.apex.metrics.signature.ApexOperationSigMask;

import apex.jorje.semantic.ast.compilation.Compilation;

/**
 * @author Clément Fournier
 */
public class ApexMultifileVisitorTest extends ApexParserTestBase {

    @Test
    public void testProjectMirrorNotNull() {
        assertNotNull(ApexProjectMirror.INSTANCE);
    }


    @Test
    public void testOperationsAreThere() throws IOException {
        ApexNode<Compilation> acu = parseResource("MetadataDeployController.cls");

        final ApexSignatureMatcher toplevel = ApexProjectMirror.INSTANCE;

        final ApexOperationSigMask opMask = new ApexOperationSigMask();

        // We could parse qnames from string but probably simpler to do that
        acu.acceptVisitor(new ApexVisitorBase<Void, Void>() {
            @Override
            public Void visit(ASTMethod node, Void data1) {
                if (!node.isSynthetic()) {
                    assertTrue(toplevel.hasMatchingSig(node.getQualifiedName(), opMask));
                }

                return data1;
            }
        }, null);
    }


}

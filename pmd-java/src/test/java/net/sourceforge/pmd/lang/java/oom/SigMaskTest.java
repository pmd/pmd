/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import net.sourceforge.pmd.lang.java.ParserTst;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.oom.visitor.FieldSigMask;
import net.sourceforge.pmd.lang.java.oom.visitor.FieldSignature;
import net.sourceforge.pmd.lang.java.oom.visitor.OperationSigMask;
import net.sourceforge.pmd.lang.java.oom.visitor.OperationSignature;
import net.sourceforge.pmd.lang.java.oom.visitor.SigMask;

/**
 * @author Clément Fournier
 */
public class SigMaskTest extends ParserTst {

    /**
     * Ensure any method is covered by an empty mask.
     */
    @Test
    public void testEmptyOperationMask() {
        final String TEST = "class Bzaz{ "
            + "public void foo(){} "
            + "void bar(){} "
            + "protected void foo(int x){} "
            + "private void rand(){}}";

        List<ASTMethodDeclaration> nodes = getOrderedNodes(ASTMethodDeclaration.class, TEST);
        SigMask<OperationSignature> mask = new OperationSigMask();

        for (ASTMethodDeclaration node : nodes) {
            assertTrue(mask.covers(OperationSignature.buildFor(node)));
        }
    }

    /**
     * Ensure any field is covered by an empty mask.
     */
    @Test
    public void testEmptyFieldMask() {
        final String TEST = "class Bzaz{"
            + "public String x;"
            + "private int y;"
            + "protected String z;"
            + "int s;"
            + "public final int t;"
            + "private final int a;"
            + "protected final double u;"
            + "final long v;"
            + "}";


        List<ASTFieldDeclaration> nodes = getOrderedNodes(ASTFieldDeclaration.class, TEST);
        SigMask<FieldSignature> mask = new FieldSigMask();

        for (ASTFieldDeclaration node : nodes) {
            assertTrue(mask.covers(FieldSignature.buildFor(node)));
        }
    }


}

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.impl;

import java.util.List;

import org.jaxen.JaxenException;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration.TypeKind;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.metrics.signature.JavaFieldSigMask;
import net.sourceforge.pmd.lang.java.metrics.signature.JavaSignature.Visibility;
import net.sourceforge.pmd.lang.metrics.MetricOptions;

/**
 * Number of public attributes.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public class NopaMetric extends AbstractJavaClassMetric {

    @Override
    public boolean supports(ASTAnyTypeDeclaration node) {
        return node.getTypeKind() == TypeKind.CLASS;
    }


    @Override
    public double computeFor(ASTAnyTypeDeclaration node, MetricOptions options) {
        int nopa = 0;
        try {
            List<ASTFieldDeclaration> decls
                = node.findChildNodesWithXPathChecked(ASTFieldDeclaration.class,
                                                      "ClassOrInterfaceBody"
                                                          + "/ClassOrInterfaceBodyDeclaration"
                                                          + "/FieldDeclaration");

            JavaFieldSigMask mask = new JavaFieldSigMask();
            mask.restrictVisibilitiesTo(Visibility.PUBLIC);

            for (ASTFieldDeclaration decl : decls) {
                if (mask.covers(decl.getSignature())) {
                    nopa++;
                }
            }

        } catch (JaxenException je) {
            throw new RuntimeException(je);
        }


        return (double) nopa;
    }

}

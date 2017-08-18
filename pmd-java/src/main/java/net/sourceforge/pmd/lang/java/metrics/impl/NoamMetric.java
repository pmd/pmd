/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.impl;

import java.util.List;

import org.jaxen.JaxenException;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration.TypeKind;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.metrics.signature.JavaOperationSigMask;
import net.sourceforge.pmd.lang.java.metrics.signature.JavaOperationSignature.Role;
import net.sourceforge.pmd.lang.java.metrics.signature.JavaSignature.Visibility;
import net.sourceforge.pmd.lang.metrics.MetricOptions;

/**
 * Number of accessor methods.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public class NoamMetric extends AbstractJavaClassMetric {

    @Override
    public boolean supports(ASTAnyTypeDeclaration node) {
        return node.getTypeKind() == TypeKind.CLASS;
    }


    @Override
    public double computeFor(ASTAnyTypeDeclaration node, MetricOptions options) {
        int noam = 0;
        try {
            List<ASTMethodDeclaration> decls
                = node.findChildNodesWithXPathChecked(ASTMethodDeclaration.class,
                                                      "ClassOrInterfaceBody"
                                                          + "/ClassOrInterfaceBodyDeclaration"
                                                          + "/MethodDeclaration");

            JavaOperationSigMask mask = new JavaOperationSigMask();
            mask.restrictRolesTo(Role.GETTER_OR_SETTER);
            mask.restrictVisibilitiesTo(Visibility.PUBLIC);

            for (ASTMethodDeclaration decl : decls) {
                if (mask.covers(decl.getSignature())) {
                    noam++;
                }
            }

        } catch (JaxenException je) {
            throw new RuntimeException(je);
        }


        return (double) noam;
    }
}

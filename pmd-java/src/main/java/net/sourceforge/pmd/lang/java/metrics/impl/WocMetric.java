/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.impl;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration.TypeKind;
import net.sourceforge.pmd.lang.java.multifile.signature.JavaOperationSigMask;
import net.sourceforge.pmd.lang.java.multifile.signature.JavaOperationSignature.Role;
import net.sourceforge.pmd.lang.java.multifile.signature.JavaSignature.Visibility;
import net.sourceforge.pmd.lang.metrics.MetricOptions;

/**
 * Weight of class.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public class WocMetric extends AbstractJavaClassMetric {

    @Override
    public boolean supports(ASTAnyTypeDeclaration node) {
        return node.getTypeKind() == TypeKind.CLASS;
    }


    @Override
    public double computeFor(ASTAnyTypeDeclaration node, MetricOptions options) {

        JavaOperationSigMask mask = new JavaOperationSigMask();
        mask.forbid(Role.CONSTRUCTOR, Role.GETTER_OR_SETTER);
        mask.restrictVisibilitiesTo(Visibility.PUBLIC);

        int functionalMethods = countMatchingOpSigs(node, mask);

        mask.coverAllRoles();

        int totalPublicMethods = countMatchingOpSigs(node, mask);

        return functionalMethods / (double) totalPublicMethods;
    }
}

/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.lang.ast.QualifiableNode;

/**
 * @author Clément Fournier
 */
public interface ApexQualifiableNode extends QualifiableNode {

    @Override
    ApexQualifiedName getQualifiedName();
}

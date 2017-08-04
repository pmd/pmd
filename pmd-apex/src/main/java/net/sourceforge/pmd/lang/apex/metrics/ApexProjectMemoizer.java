/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClassOrInterface;
import net.sourceforge.pmd.lang.metrics.BasicProjectMemoizer;

/**
 * @author Clément Fournier
 */
class ApexProjectMemoizer extends BasicProjectMemoizer<ASTUserClassOrInterface<?>, ASTMethod> {
}

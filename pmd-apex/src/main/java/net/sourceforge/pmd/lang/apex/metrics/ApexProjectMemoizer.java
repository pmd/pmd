/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClassOrInterface;
import net.sourceforge.pmd.lang.metrics.BasicProjectMemoizer;

/**
 * Memoizer for Apex metrics.
 *
 * @author Clément Fournier
 */
@Deprecated
class ApexProjectMemoizer extends BasicProjectMemoizer<ASTUserClassOrInterface<?>, ASTMethod> {
}

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.ast;

import java.util.List;

import net.sourceforge.pmd.lang.modelica.resolver.ResolutionResult;
import net.sourceforge.pmd.lang.modelica.resolver.ResolvableEntity;

final class Helper {
    private Helper() {
    }

    // For Rule Designer
    static String getResolvedTo(ResolutionResult<ResolvableEntity> result) {
        List<ResolvableEntity> decls = result.getBestCandidates();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < decls.size(); ++i) {
            if (i != 0) {
                sb.append(", ");
            }
            sb.append(decls.get(i).getDescriptiveName());
        }
        return sb.toString();
    }
}

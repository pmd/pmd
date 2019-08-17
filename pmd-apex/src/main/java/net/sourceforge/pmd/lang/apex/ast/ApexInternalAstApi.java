package net.sourceforge.pmd.lang.apex.ast;

import java.util.Map;

import net.sourceforge.pmd.annotation.InternalApi;


@InternalApi
public final class ApexInternalAstApi {


    public static Map<Integer, String> getNoPmdComments(ApexNode<?> node) {
        if (node instanceof ApexRootNode) {
            return ((ApexRootNode<?>) node).getNoPmdComments();
        } else {
            throw new IllegalArgumentException("Unexpected " + node);
        }
    }

}

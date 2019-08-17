package net.sourceforge.pmd;

import java.util.List;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.Report.SuppressedViolation;

/**
 * Generic rule violation suppressor.
 */
public interface ViolationSuppressor {


    @Nullable
    SuppressedViolation suppressOrNull(RuleViolation violation);


    static ViolationSuppressor noop() {
        return rv -> null;
    }


    static ViolationSuppressor compose(List<ViolationSuppressor> suppressors) {
        return rv -> {
            for (ViolationSuppressor suppressor : suppressors) {
                SuppressedViolation suppressed = suppressor.suppressOrNull(rv);
                if (suppressed != null) {
                    return suppressed;
                }
            }
            return null;
        };
    }


    static ViolationSuppressor noPmdCommentSuppressor(Map<Integer, String> noPmdComments) {
        return rv -> {
            if (noPmdComments.containsKey(rv.getBeginLine())) {
                return new SuppressedViolation(rv, true, noPmdComments.get(rv.getBeginLine()));
            }
            return null;
        };
    }

}

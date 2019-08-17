package net.sourceforge.pmd;

import java.util.Map;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.Report.SuppressedViolation;

/**
 * Generic rule violation suppressor.
 */
public interface ViolationSuppressor {

    String NOPMD_COMMENT_ID = "//NOPMD";


    String id();

    @Nullable
    SuppressedViolation suppressOrNull(RuleViolation violation);


    // TODO hide, internal
    static ViolationSuppressor noPmdCommentSuppressor(Map<Integer, String> noPmdComments) {
        return new ViolationSuppressor() {
            @Override
            public String id() {
                return NOPMD_COMMENT_ID;
            }

            @Override
            public @Nullable SuppressedViolation suppressOrNull(RuleViolation rv) {
                if (noPmdComments.containsKey(rv.getBeginLine())) {
                    return new SuppressedViolation(rv, this, noPmdComments.get(rv.getBeginLine()));
                }
                return null;
            }
        };
    }

}

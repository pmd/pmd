/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.declaration.TriggerDeclaration.TriggerCase;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * This maps the apex enum {@link TriggerCase} to the PMD equivalent.
 */
public enum TriggerUsage {

    AFTER_DELETE(TriggerCase.TRIGGER_AFTER_DELETE),

    AFTER_INSERT(TriggerCase.TRIGGER_AFTER_INSERT),

    AFTER_UNDELETE(TriggerCase.TRIGGER_AFTER_UNDELETE),

    AFTER_UPDATE(TriggerCase.TRIGGER_AFTER_UPDATE),

    BEFORE_DELETE(TriggerCase.TRIGGER_BEFORE_DELETE),

    BEFORE_INSERT(TriggerCase.TRIGGER_BEFORE_INSERT),

    BEFORE_UNDELETE(TriggerCase.TRIGGER_BEFORE_UNDELETE),

    BEFORE_UPDATE(TriggerCase.TRIGGER_BEFORE_UPDATE);

    private final TriggerCase triggerCase;

    private static final Map<TriggerCase, TriggerUsage> APEX_TO_PMD;

    static {
        APEX_TO_PMD = new HashMap<>();
        for (TriggerUsage triggerUsage : TriggerUsage.values()) {
            APEX_TO_PMD.put(triggerUsage.getTriggerCase(), triggerUsage);
        }
    }

    TriggerUsage(TriggerCase triggerCase) {
        this.triggerCase = triggerCase;
    }

    private TriggerCase getTriggerCase() {
        return triggerCase;
    }

    public static TriggerUsage of(TriggerCase triggerCase) {
        TriggerUsage result = APEX_TO_PMD.get(triggerCase);
        if (result != null) {
            return result;
        }
        throw new NoSuchElementException("Unknown TriggerUsage value '" + triggerCase.name() + "'");
    }
}

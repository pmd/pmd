/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * This maps the apex enum {@link apex.jorje.data.ast.TriggerUsage} to the PMD equivalent.
 */
public enum TriggerUsage {

    AFTER_DELETE(apex.jorje.data.ast.TriggerUsage.AFTER_DELETE),

    AFTER_INSERT(apex.jorje.data.ast.TriggerUsage.AFTER_INSERT),

    AFTER_UNDELETE(apex.jorje.data.ast.TriggerUsage.AFTER_UNDELETE),

    AFTER_UPDATE(apex.jorje.data.ast.TriggerUsage.AFTER_UPDATE),

    BEFORE_DELETE(apex.jorje.data.ast.TriggerUsage.BEFORE_DELETE),

    BEFORE_INSERT(apex.jorje.data.ast.TriggerUsage.BEFORE_INSERT),

    BEFORE_UNDELETE(apex.jorje.data.ast.TriggerUsage.BEFORE_UNDELETE),

    BEFORE_UPDATE(apex.jorje.data.ast.TriggerUsage.BEFORE_UPDATE);

    private final apex.jorje.data.ast.TriggerUsage apexTriggerUsage;

    private static final Map<apex.jorje.data.ast.TriggerUsage, TriggerUsage> APEX_TO_PMD;

    static {
        APEX_TO_PMD = new HashMap<>();
        for (TriggerUsage triggerUsage : TriggerUsage.values()) {
            APEX_TO_PMD.put(triggerUsage.getApexTriggerUsage(), triggerUsage);
        }
    }

    TriggerUsage(apex.jorje.data.ast.TriggerUsage apexTriggerUsage) {
        this.apexTriggerUsage = apexTriggerUsage;
    }

    private apex.jorje.data.ast.TriggerUsage getApexTriggerUsage() {
        return apexTriggerUsage;
    }

    public static TriggerUsage of(apex.jorje.data.ast.TriggerUsage apexTriggerUsage) {
        TriggerUsage result = APEX_TO_PMD.get(apexTriggerUsage);
        if (result != null) {
            return result;
        }
        throw new NoSuchElementException("Unknown TriggerUsage value '" + apexTriggerUsage.name() + "'");
    }
}

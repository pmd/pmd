/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.javacc;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.PMD;

public abstract class AbstractTokenManager {

    protected Map<Integer, String> suppressMap = new HashMap<>();
    protected String suppressMarker = PMD.SUPPRESS_MARKER;

    public void setSuppressMarker(String marker) {
        this.suppressMarker = marker;
    }

    public Map<Integer, String> getSuppressMap() {
        return suppressMap;
    }
}

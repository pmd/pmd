/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;

public abstract class AbstractTokenManager implements TokenManager<JavaccToken> {

    protected Map<Integer, String> suppressMap = new HashMap<>();
    protected String suppressMarker = PMD.SUPPRESS_MARKER;
    protected String fileName;

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setSuppressMarker(String marker) {
        this.suppressMarker = marker;
    }

    public Map<Integer, String> getSuppressMap() {
        return suppressMap;
    }
}

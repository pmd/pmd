/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ast;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.PMD;

public abstract class AbstractTokenManager {

    // Because the TokenMgrError class does not have access to the TokenManager instance, we
    // cannot store the file name as an instance field, but must use a static.
    private static ThreadLocal<String> fileName = new ThreadLocal<>();

    protected Map<Integer, String> suppressMap = new HashMap<>();
    protected String suppressMarker = PMD.SUPPRESS_MARKER;

    public static void setFileName(String fileName) {
	AbstractTokenManager.fileName.set(fileName);
    }

    public static String getFileName() {
	String fileName = AbstractTokenManager.fileName.get();
	return fileName == null ? "(no file name provided)" : fileName;
    }

    public void setSuppressMarker(String marker) {
	this.suppressMarker = marker;
    }

    public Map<Integer, String> getSuppressMap() {
	return suppressMap;
    }
}

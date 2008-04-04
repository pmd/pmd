/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang;


/**
 * This is a generic implementation of the Parser interface.
 * 
 * @see Parser
 */
public abstract class AbstractParser implements Parser {
    private String excludeMarker;

    public String getExcludeMarker() {
	return excludeMarker;
    }

    public void setExcludeMarker(String excludeMarker) {
	this.excludeMarker = excludeMarker;
    }
}

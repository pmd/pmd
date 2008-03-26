/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.parsers;

/**
 * This is a generic implementation of the Parser interface.
 * 
 * @see Parser
 */
public abstract class AbstractParser implements Parser {
    private String marker;

    public String getExcludeMarker() {
	return marker;
    }

    public void setExcludeMarker(String marker) {
	this.marker = marker;
    }
}

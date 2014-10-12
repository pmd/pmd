/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import java.util.ArrayList;

/**
 * GoLang
 *
 * @author oinume@gmail.com
 */
public class GoTokenizer extends AbstractTokenizer {
    public GoTokenizer() {
        // setting markers for "string" in Go
        this.stringToken = new ArrayList<String>();
        this.stringToken.add("\"");
        this.stringToken.add("`");

        // setting markers for 'ignorable character' in Go
        this.ignorableCharacter = new ArrayList<String>();
        this.ignorableCharacter.add(";");

        // setting markers for 'ignorable string' in Go
        this.ignorableStmt = new ArrayList<String>();
    }
}

/*
 * User: tom
 * Date: Jul 9, 2002
 * Time: 2:52:07 PM
 */
package net.sourceforge.pmd;

public class ExternalRuleID {

    private String filename;
    private String ruleName;

    public ExternalRuleID(String id) {
        int afterXML = id.indexOf(".xml") + 5;
        filename = id.substring(0, afterXML-1);
        ruleName = id.substring(afterXML);
    }

    public String getFilename() {
        return filename;
    }

    public String getRuleName() {
        return ruleName;
    }

    public String toString() {
        return filename + "/" + ruleName;
    }
}

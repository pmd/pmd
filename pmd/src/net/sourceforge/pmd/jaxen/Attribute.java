/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.jaxen;

import net.sourceforge.pmd.ast.Node;

/**
 * @author daniels
 *
 */
public class Attribute {
    
    private Node parent;
    private String name;
    private String value;

	public Attribute(Node parent, String name, String value) {
	    this.parent = parent;
	    this.name = name;
	    this.value = value;
	}

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public String toString() {
        return name + ":" + value + ":" + parent;
    }
}

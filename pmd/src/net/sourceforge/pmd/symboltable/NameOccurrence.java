/*
 * User: tom
 * Date: Oct 1, 2002
 * Time: 11:05:43 AM
 */
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.*;

import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.List;

public class NameOccurrence {

    private SimpleNode occurrenceLocation;
    private String image;
    private NameOccurrence qualifiedName;

    public NameOccurrence(SimpleNode occurrenceLocation, String image) {
        this.occurrenceLocation = occurrenceLocation;
        this.image = image;
    }

    public void setNameWhichThisQualifies(NameOccurrence qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    public NameOccurrence getNameForWhichThisIsAQualifier() {
        return qualifiedName;
    }

    // do these two methods justify keeping a node as an instance var?
    public Scope getScope() {
        return occurrenceLocation.getScope();
    }

    public int getBeginLine() {
        return occurrenceLocation.getBeginLine();
    }
    // do these two methods justify keeping a node as an instance var?

    public boolean isThisOrSuper() {
        return image.equals("this") || image.equals("super");
    }

    public boolean equals(Object o) {
        NameOccurrence n = (NameOccurrence)o;
        return n.getImage().equals(getImage());
    }

    public String getImage() {
        return image;
    }

    public int hashCode() {
        return getImage().hashCode();
    }

    public String toString() {
        return getImage() + ":" + occurrenceLocation.getBeginLine();
    }
}

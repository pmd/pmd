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

    public boolean isOnLeftHandSide() {
        SimpleNode top = null;
        if (occurrenceLocation.jjtGetParent() instanceof ASTPrimaryExpression) {
            top = (SimpleNode)occurrenceLocation.jjtGetParent().jjtGetParent();
        } else if (occurrenceLocation.jjtGetParent().jjtGetParent() instanceof ASTPrimaryExpression) {
            top = (SimpleNode)occurrenceLocation.jjtGetParent().jjtGetParent().jjtGetParent();
        } else {
            throw new RuntimeException("Found a NameOccurrence that didn't have an ASTPrimary Expression as parent or grandparent.  Parent = " + occurrenceLocation.jjtGetParent() + " and grandparent = " + occurrenceLocation.jjtGetParent().jjtGetParent());
        }

        return top.jjtGetNumChildren() > 1 && top.jjtGetChild(1) instanceof ASTAssignmentOperator;
    }

    public Scope getScope() {
        return occurrenceLocation.getScope();
    }


    public int getBeginLine() {
        return occurrenceLocation.getBeginLine();
    }

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
        return getImage() + ":" + occurrenceLocation.getBeginLine() + ":" + occurrenceLocation.getClass();
    }
}

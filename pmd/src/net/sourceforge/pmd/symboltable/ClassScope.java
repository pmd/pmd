/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.util.Applier;

import java.util.ArrayList;
import java.util.List;

public class ClassScope extends AbstractScope {

    // FIXME - this breaks give sufficiently nested code
    private static int anonymousInnerClassCounter = 1;
    private String className;

    public ClassScope(String className) {
        this.className = className;
        anonymousInnerClassCounter = 1;
    }

    /**
     * This is only for anonymous inner classes
     *
     * FIXME - should have name like Foo$1, not Anonymous$1
     * to get this working right, the parent scope needs
     * to be passed in when instantiating a ClassScope
     */
    public ClassScope() {
        //this.className = getParent().getEnclosingClassScope().getClassName() + "$" + String.valueOf(anonymousInnerClassCounter);
        this.className = "Anonymous$" + String.valueOf(anonymousInnerClassCounter);
        anonymousInnerClassCounter++;
    }

    public ClassScope getEnclosingClassScope() {
        return this;
    }

    public String getClassName() {
        return this.className;
    }

    public void addDeclaration(MethodNameDeclaration decl) {
        methodNames.put(decl, new ArrayList());
    }

    protected NameDeclaration findVariableHere(NameOccurrence occurrence) {
        if (occurrence.isThisOrSuper() || occurrence.getImage().equals(className)) {
            if (variableNames.isEmpty()) {
                // this could happen if you do this:
                // public class Foo {
                //  private String x = super.toString();
                // }
                return null;
            }
            // return any name declaration, since all we really want is to get the scope
            // for example, if there's a
            // public class Foo {
            //  private static final int X = 2;
            //  private int y = Foo.X;
            // }
            // we'll look up Foo just to get a handle to the class scope
            // and then we'll look up X.
            return (NameDeclaration) variableNames.keySet().iterator().next();
        }

        List images = new ArrayList();
        images.add(occurrence.getImage());
        if (occurrence.getImage().startsWith(className)) {
            images.add(clipClassName(occurrence.getImage()));
        }
        ImageFinderFunction finder = new ImageFinderFunction(images);
        Applier.apply(finder, variableNames.keySet().iterator());
        return finder.getDecl();
    }

    public String toString() {
        return "ClassScope:" + className + ":" + super.glomNames();
    }

    private String clipClassName(String in) {
        int firstDot = in.indexOf('.');
        return in.substring(firstDot + 1);
    }
}

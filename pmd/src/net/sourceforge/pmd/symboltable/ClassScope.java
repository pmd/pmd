package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.util.Applier;

import java.util.ArrayList;
import java.util.List;

public class ClassScope extends AbstractScope {

    // TODO this is a hack, it will break given sufficiently nested classes
    private static int anonymousCounter = 1;

    private String className;

    public ClassScope(String className) {
        this.className = className;
        anonymousCounter = 0;
    }

    public ClassScope() {
        this.className = "Anonymous$" + String.valueOf(anonymousCounter);
        anonymousCounter++;
    }

    public Scope getEnclosingClassScope() {
        return this;
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

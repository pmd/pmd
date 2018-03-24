/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices.missingoverride;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.AbstractJavaTypeNode;
import net.sourceforge.pmd.lang.java.ast.JavaNode;

public abstract class HierarchyWithSeveralBridges<T extends Node> {

    abstract void foo(T node);

    public abstract static class SubclassOne<T extends JavaNode> extends HierarchyWithSeveralBridges<T> {

        @Override
        abstract void foo(T node);
    }

    public abstract static class SubclassTwo<T extends AbstractJavaTypeNode> extends SubclassOne<T> {
        @Override
        void foo(T node) {

        }
    }


    public static class Concrete extends SubclassTwo<ASTType> {

        // bridges: foo(AbstractJavaTypeNode), foo(JavaNode), foo(Node)

        @Override
        void foo(ASTType node) {

        }
    }
}

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.ast.ASTFormalParameter;
import net.sourceforge.pmd.ast.ASTResultType;
import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.util.CollectionUtil;

public class LooseCoupling extends AbstractRule {

	// TODO - these should be brought in via external properties
//    private static final Set implClassNames = CollectionUtil.asSet( new Object[] {
//    	"ArrayList", "HashSet", "HashMap", "LinkedHashMap", "LinkedHashSet", "TreeSet", "TreeMap", "Vector",
//    	"java.util.ArrayList", "java.util.HashSet", "java.util.HashMap",
//    	"java.util.LinkedHashMap", "java.util.LinkedHashSet", "java.util.TreeSet",
//    	"java.util.TreeMap", "java.util.Vector" 
//    	});

    public LooseCoupling() {
        super();
    }

    public Object visit(ASTClassOrInterfaceType node, Object data) {
        Node parent = node.jjtGetParent().jjtGetParent().jjtGetParent();
        String typeName = node.getImage();
        if (CollectionUtil.isCollectionType(typeName, false) && (parent instanceof ASTFieldDeclaration || parent instanceof ASTFormalParameter || parent instanceof ASTResultType)) {
            addViolation(data, node, typeName);
        }
        return data;
    }
}

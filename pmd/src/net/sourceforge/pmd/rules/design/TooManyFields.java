/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.ast.SimpleNode;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class TooManyFields extends AbstractRule {
    private Map stats ;
    private Map nodes ;
    private int maxFields;
    
    
    public TooManyFields() {
        super();
        if (hasProperty("maxfields")) {
            maxFields = getIntProperty("maxfields");
        } else {
            maxFields = 10;
        }        
    }
    
    public Object visit(ASTCompilationUnit node, Object data) {
        stats = new HashMap(5);
        nodes = new HashMap(5);

        List l = node.findChildrenOfType(ASTFieldDeclaration.class);
        
        if (l!=null && !l.isEmpty()) {
            for (Iterator it = l.iterator() ; it.hasNext() ; ) {
                ASTFieldDeclaration fd = (ASTFieldDeclaration) it.next();
                ASTClassOrInterfaceDeclaration p = (ASTClassOrInterfaceDeclaration)fd.jjtGetParent().jjtGetParent().jjtGetParent();
                if (!p.isInterface()) {
                    processField(fd);
                }
            }
        }
        for (Iterator it = stats.keySet().iterator() ; it.hasNext() ; ) {
            String k = (String) it.next();
            int val = ((Integer)stats.get(k)).intValue();
            SimpleNode n = (SimpleNode) nodes.get(k);
            if (val>maxFields) {
                // TODO add violation with 
//				RuleContext ctx = (RuleContext) data;
//				RuleViolation ruleViolation = createRuleViolation(ctx, node.getBeginLine(), MessageFormat.format(getMessage(), new Object[]{methodName}));
//                ctx.getReport().addRuleViolation(ruleViolation);
                addViolation((RuleContext) data, n);

            }
        }
        return data;
    }
    
    private void processField(ASTFieldDeclaration fd) {
        ASTClassOrInterfaceDeclaration clazz = (ASTClassOrInterfaceDeclaration) fd.getFirstParentOfType(ASTClassOrInterfaceDeclaration.class);
        String key = clazz.getImage();
        if (!stats.containsKey(key)) {
            stats.put(key, new Integer(0));
            nodes.put(key, clazz);
        }
        Integer i = new Integer(((Integer) stats.get(key)).intValue()+1);
        stats.put(key,i);
    }

}

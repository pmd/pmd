package net.sourceforge.pmd.rules;

import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAdditiveExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.symboltable.NameOccurrence;

/**
 * Detects and flags the occurrences of specific method calls against an instance of
 * a designated class. I.e. String.indexOf. The goal is to be able to suggest more
 * efficient/modern ways of implementing the same function.
 * 
 * Concrete subclasses are expected to provide the name of the target class and an 
 * array of method names that we are looking for. We then pass judgement on any literal
 * arguments we find in the subclass as well.
 * 
 * @author Brian Remedios 
 * @version $Revision$
 */
public abstract class AbstractPoorMethodCall extends AbstractJavaRule {
    
    
    /**
     * The name of the type the method will be invoked against.
     * @return String
     */
    protected abstract String targetTypename();
    
    /**
     * Return the names of all the methods we are scanning for, no brackets or
     * argument types.
     * 
     * @return String[]
     */
    protected abstract String[] methodNames();
    
    /**
     * Returns whether the string argument at the stated position being sent to 
     * the method is ok or not. Return true if you want to record the method call 
     * as a violation, false otherwise.
     * 
     * @param argIndex int
     * @param arg String
     * @return boolean
     */
    protected abstract boolean isViolationArgument(int argIndex, String arg);
    
    /**
     * Returns whether the name occurrence is one of the method calls
     * we are interested in.
     * 
     * @param occurrence NameOccurrence
     * @return boolean
     */
    private boolean isNotedMethod(NameOccurrence occurrence) {
        
        if (occurrence == null) return false;
        
        String methodCall = occurrence.getImage();      
        String[] methodNames = methodNames();
        
        for (int i=0; i<methodNames.length; i++) {
            if (methodCall.indexOf(methodNames[i]) != -1) return true;
        }
        return false;
    }
        
    /**
     * Returns whether the value argument is a single character string.
     * 
     * @param value String
     * @return boolean
     */
    public static boolean isSingleCharAsString(String value) {
        return value.length() == 3 && value.charAt(0) == '\"';
    }
    
    /**
     * Method visit.
     * @param node ASTVariableDeclaratorId
     * @param data Object
     * @return Object
     * @see net.sourceforge.pmd.lang.java.ast.JavaParserVisitor#visit(ASTVariableDeclaratorId, Object)
     */
    public Object visit(ASTVariableDeclaratorId node, Object data) {
        
        if (!node.getNameDeclaration().getTypeImage().equals(targetTypename())) {
            return data;
        }
        
        for (NameOccurrence occ: node.getUsages()) {
            if (isNotedMethod(occ.getNameForWhichThisIsAQualifier())) {
        	Node parent = occ.getLocation().jjtGetParent().jjtGetParent();
                if (parent instanceof ASTPrimaryExpression) {
                    // bail out if it's something like indexOf("a" + "b")
                    List additives = parent.findChildrenOfType(ASTAdditiveExpression.class);
                    if (!additives.isEmpty()) {
                        return data;
                    }
                    List literals = parent.findChildrenOfType(ASTLiteral.class);
                    for (int l=0; l<literals.size(); l++) {
                        ASTLiteral literal = (ASTLiteral)literals.get(l);
                        if (isViolationArgument(l, literal.getImage())) {
                            addViolation(data, occ.getLocation());
                        }
                    }
                }
            }
        }
        return data;
    }
}


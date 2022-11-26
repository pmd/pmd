package net.sourceforge.pmd.lang.java.symbols.internal.ast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTMemberValue;
import net.sourceforge.pmd.lang.java.ast.ASTMemberValueArrayInitializer;
import net.sourceforge.pmd.lang.java.ast.ASTMemberValuePair;
import net.sourceforge.pmd.lang.java.types.JAnnotation;
import net.sourceforge.pmd.lang.java.types.JAnnotation.MemberValuePair;

final class AstAnnotationParser {

    private AstAnnotationParser() {
        throw new AssertionError("Can't instantiate utility classes");
    }
    
    public static JAnnotation fromAst(ASTAnnotation annotation) {
        List<JAnnotation.MemberValuePair> pairs = new ArrayList<>();
        annotation.getMembers().forEach(m -> pairs.add(fromMemberValueAST(m)));
        // TODO : is calling getTypeMirror here valid?
        return new JAnnotation(annotation.getTypeMirror(), pairs);
    }
    
    private static MemberValuePair fromMemberValueAST(ASTMemberValuePair node) {
        return new MemberValuePair(node.getName(), resolveValue(node.getValue()));
    }
    
    private static Object resolveValue(ASTMemberValue memberValue) {
        if (memberValue instanceof ASTAnnotation) {
            return fromAst((ASTAnnotation) memberValue);
        } else if (memberValue instanceof ASTMemberValueArrayInitializer) {
            final ASTMemberValueArrayInitializer arrayValue = (ASTMemberValueArrayInitializer) memberValue;
            final Object[] value = new Object[memberValue.getNumChildren()]; 
            final Iterator<ASTMemberValue> it = arrayValue.iterator();
            
            for (int i = 0; i < value.length; i++) {
                value[i] = resolveValue(it.next());
            }
            
            return value;
        }
        
        // TODO : If it's a constant expression, but not a literal, this will fail
        return memberValue.getConstValue();
    }
}

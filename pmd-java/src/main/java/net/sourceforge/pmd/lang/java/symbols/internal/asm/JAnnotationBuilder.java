package net.sourceforge.pmd.lang.java.symbols.internal.asm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.objectweb.asm.AnnotationVisitor;

import net.sourceforge.pmd.lang.java.types.JAnnotation;
import net.sourceforge.pmd.lang.java.types.JClassType;

abstract class JAnnotationBuilder extends AnnotationVisitor {

    private final String annotationName;
    protected List<JAnnotation.MemberValuePair> valuePairs = Collections.emptyList();
    
    protected JAnnotationBuilder(String descriptor) {
        super(AsmSymbolResolver.ASM_API_V);
        annotationName = descriptor;
    }
    
    @Override
    public abstract void visitEnd();

    @Override
    public void visit(String name, Object value) {
        addValuePair(new JAnnotation.MemberValuePair(name, value));
    }
    
    @Override
    public AnnotationVisitor visitArray(final String name) {
        final JAnnotationBuilder self = this;
        return new JAnnotationBuilder("phony") {
            @Override
            public void visitEnd() {
                final Object[] value = this.valuePairs.stream()
                        .map(JAnnotation.MemberValuePair::getValue)
                        .collect(Collectors.toList()).toArray(new Object[0]);
                self.addValuePair(new JAnnotation.MemberValuePair(name, value));
            }
        };
    }
    
    @Override
    public AnnotationVisitor visitAnnotation(String name, String descriptor) {
        final JAnnotationBuilder self = this;
        return new JAnnotationBuilder(descriptor) {
            @Override
            public void visitEnd() {
                self.addValuePair(new JAnnotation.MemberValuePair(name, this.build()));
            }
        };
    }
    
    @Override
    public void visitEnum(String name, String descriptor, String value) {
        // TODO Auto-generated method stub
        super.visitEnum(name, descriptor, value);
    }
    
    protected JAnnotation build() {
        JClassType type = null; // Obtain from annotationName
        return new JAnnotation(type, valuePairs);
    }
    
    private void addValuePair(JAnnotation.MemberValuePair pair) {
        if (valuePairs.isEmpty()) {
            // Ensure it's not an immutable empty list
            valuePairs = new ArrayList<>();
        }
        
        valuePairs.add(pair);
    }
    
    static class ClassJAnnotationBuilder extends JAnnotationBuilder {
        private final ClassStub classStub;

        public ClassJAnnotationBuilder(String descriptor, ClassStub classStub) {
            super(descriptor);
            this.classStub = classStub;
        }
        
        @Override
        public void visitEnd() {
            classStub.addAnnotation(build());
        }
    }
}

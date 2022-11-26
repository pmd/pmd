package net.sourceforge.pmd.lang.java.types;

import java.util.Collections;
import java.util.List;

public final class JAnnotation {

    private final JClassType type;
    private final List<MemberValuePair> memberValuePairs;
    
    public JAnnotation(final JClassType type, final List<MemberValuePair> memberValuePairs) {
        this.type = type;
        this.memberValuePairs = Collections.unmodifiableList(memberValuePairs);
    }

    public JClassType getAnnotationType() {
        return type;
    }
    
    public List<MemberValuePair> getMemberValuePairs() {
        return memberValuePairs;
    }
    
    public static class MemberValuePair {
        private final String name;
        private final Object value;
        
        public MemberValuePair(String name, Object value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        /**
         * Retrieves the associated value for this pair. It can be one of:
         *  - A literal value, of any type.
         *  - Another JAnnotation instance
         *  - An array, of either literals or JAnnotations.
         */
        public Object getValue() {
            return value;
        }
    }
}

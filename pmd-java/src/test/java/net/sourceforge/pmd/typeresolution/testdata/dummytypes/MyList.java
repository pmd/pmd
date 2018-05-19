/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution.testdata.dummytypes;

import java.util.List;

// This is a stub for src/test/resources/net/sourceforge/pmd/lang/java/ast/jdkversiontests/java10/LocalVariableTypeInference_typeres.java
public class MyList {

    public void checkIterator(List<?> other) {
        //var oit = other.iterator();
        //oit.hasNext();
    }
}

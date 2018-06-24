package net.sourceforge.pmd.typeresolution.testdata.dummytypes;

import java.util.List;
import java.util.ArrayList;

// note: This source is java10.
// the same class exists in src/test/java/net/sourceforge/pmd/typeresolution/testdata/dummytypes/MyList.java
// compilable by java7. This is important, so that
// this class has a type.
public class MyList {

    public void checkIterator(List<?> other) {
        var oit = other.iterator();
        oit.hasNext();
    }
}

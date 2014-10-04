package net.sourceforge.pmd.typeresolution;

import java.util.ArrayList;
import java.util.List;

public class ClassWithImportOnDemand {

    public List foo() {
        return new ArrayList();
    }
}

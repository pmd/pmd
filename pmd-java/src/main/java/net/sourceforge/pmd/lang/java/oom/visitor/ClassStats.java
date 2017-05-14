/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.visitor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Statistics about a class. Gathers information about the contained members and their signatures, subclasses and
 * superclasses.
 *
 * @author Clément Fournier
 */
public class ClassStats {
    private Map<OperationSignature, List<String>> operations = new HashMap<>();
    private Map<FieldSignature, List<String>> fields = new HashMap<>();

    // References to the hierarchy
    // We store strings so that classes not analysed are ignored
    private String superclass;
    private List<String> subclasses;

    //TODO
}

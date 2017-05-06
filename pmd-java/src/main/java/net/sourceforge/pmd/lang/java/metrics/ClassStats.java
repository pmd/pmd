/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.metrics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Clément Fournier
 *
 */
public class ClassStats {
    private Map<OperationSignature, List<String>> operations = new HashMap<>();
}

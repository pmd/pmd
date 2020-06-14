/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import static net.sourceforge.pmd.properties.PropertyFactory.booleanProperty;
import static net.sourceforge.pmd.properties.PropertyFactory.intProperty;
import static net.sourceforge.pmd.properties.PropertyFactory.stringProperty;
import static net.sourceforge.pmd.properties.constraints.NumericConstraints.positive;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.properties.PropertyDescriptor;


public class AvoidDuplicateLiteralsRule extends AbstractJavaRule {

    public static final PropertyDescriptor<Integer> THRESHOLD_DESCRIPTOR
            = intProperty("maxDuplicateLiterals")
                             .desc("Max duplicate literals")
                             .require(positive()).defaultValue(4).build();

    public static final PropertyDescriptor<Integer> MINIMUM_LENGTH_DESCRIPTOR = intProperty("minimumLength").desc("Minimum string length to check").require(positive()).defaultValue(3).build();

    public static final PropertyDescriptor<Boolean> SKIP_ANNOTATIONS_DESCRIPTOR =
            booleanProperty("skipAnnotations")
                    .desc("Skip literals within annotations").defaultValue(false).build();

    private static final PropertyDescriptor<Set<String>> EXCEPTION_LIST_DESCRIPTOR
        = stringProperty("exceptionList")
                         .desc("List of literals to ignore. "
                                          + "A literal is ignored if its image can be found in this list. "
                                          + "Components of this list should not be surrounded by double quotes.")
                         .map(Collectors.toSet())
                         .defaultValue(Collections.emptySet())
                         .delim(',')
                         .build();

    private Map<String, List<ASTLiteral>> literals = new HashMap<>();
    private Set<String> exceptions = new HashSet<>();
    private int minLength;

    public AvoidDuplicateLiteralsRule() {
        definePropertyDescriptor(THRESHOLD_DESCRIPTOR);
        definePropertyDescriptor(MINIMUM_LENGTH_DESCRIPTOR);
        definePropertyDescriptor(SKIP_ANNOTATIONS_DESCRIPTOR);
        definePropertyDescriptor(EXCEPTION_LIST_DESCRIPTOR);
    }

    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        literals.clear();

        if (getProperty(EXCEPTION_LIST_DESCRIPTOR) != null) {
            exceptions = getProperty(EXCEPTION_LIST_DESCRIPTOR);
        }

        minLength = 2 + getProperty(MINIMUM_LENGTH_DESCRIPTOR);

        super.visit(node, data);

        processResults(data);

        return data;
    }

    private void processResults(Object data) {

        int threshold = getProperty(THRESHOLD_DESCRIPTOR);

        for (Map.Entry<String, List<ASTLiteral>> entry : literals.entrySet()) {
            List<ASTLiteral> occurrences = entry.getValue();
            if (occurrences.size() >= threshold) {
                ASTLiteral first = occurrences.get(0);
                String rawImage = first.getEscapedStringLiteral();
                Object[] args = {rawImage, occurrences.size(), first.getBeginLine(), };
                addViolation(data, first, args);
            }
        }
    }

    @Override
    public Object visit(ASTLiteral node, Object data) {
        if (!node.isStringLiteral()) {
            return data;
        }
        String image = node.getImage();

        // just catching strings of 'minLength' chars or more (including the
        // enclosing quotes)
        if (image.length() < minLength) {
            return data;
        }

        // skip any exceptions
        if (exceptions.contains(image.substring(1, image.length() - 1))) {
            return data;
        }

        // Skip literals in annotations
        if (getProperty(SKIP_ANNOTATIONS_DESCRIPTOR) && node.getFirstParentOfType(ASTAnnotation.class) != null) {
            return data;
        }

        if (literals.containsKey(image)) {
            List<ASTLiteral> occurrences = literals.get(image);
            occurrences.add(node);
        } else {
            List<ASTLiteral> occurrences = new ArrayList<>();
            occurrences.add(node);
            literals.put(image, occurrences);
        }

        return data;
    }

}

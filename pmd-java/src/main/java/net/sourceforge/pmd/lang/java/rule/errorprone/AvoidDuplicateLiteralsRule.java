/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import static net.sourceforge.pmd.properties.PropertyFactory.booleanProperty;
import static net.sourceforge.pmd.properties.PropertyFactory.intProperty;
import static net.sourceforge.pmd.properties.PropertyFactory.stringListProperty;
import static net.sourceforge.pmd.properties.constraints.NumericConstraints.positive;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTStringLiteral;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.properties.PropertyDescriptor;


public class AvoidDuplicateLiteralsRule extends AbstractJavaRulechainRule {

    public static final PropertyDescriptor<Integer> THRESHOLD_DESCRIPTOR
            = intProperty("maxDuplicateLiterals")
                             .desc("Max duplicate literals")
                             .require(positive()).defaultValue(4).build();

    public static final PropertyDescriptor<Integer> MINIMUM_LENGTH_DESCRIPTOR = intProperty("minimumLength").desc("Minimum string length to check").require(positive()).defaultValue(3).build();

    public static final PropertyDescriptor<Boolean> SKIP_ANNOTATIONS_DESCRIPTOR =
            booleanProperty("skipAnnotations")
                    .desc("Skip literals within annotations").defaultValue(false).build();

    private static final PropertyDescriptor<List<String>> EXCEPTION_LIST_DESCRIPTOR
        = stringListProperty("exceptionList")
                         .desc("List of literals to ignore. "
                                          + "A literal is ignored if its image can be found in this list. "
                                          + "Components of this list should not be surrounded by double quotes.")
                         .defaultValue(Collections.emptyList())
                         .delim(',')
                         .build();

    private Map<String, SortedSet<ASTStringLiteral>> literals = new HashMap<>();
    private Set<String> exceptions = new HashSet<>();
    private int minLength;

    public AvoidDuplicateLiteralsRule() {
        super(ASTStringLiteral.class);
        definePropertyDescriptor(THRESHOLD_DESCRIPTOR);
        definePropertyDescriptor(MINIMUM_LENGTH_DESCRIPTOR);
        definePropertyDescriptor(SKIP_ANNOTATIONS_DESCRIPTOR);
        definePropertyDescriptor(EXCEPTION_LIST_DESCRIPTOR);
    }

    @Override
    public void start(RuleContext ctx) {
        super.start(ctx);

        literals.clear();

        if (getProperty(EXCEPTION_LIST_DESCRIPTOR) != null) {
            exceptions = new HashSet<>(getProperty(EXCEPTION_LIST_DESCRIPTOR));
        }

        minLength = 2 + getProperty(MINIMUM_LENGTH_DESCRIPTOR);

    }

    @Override
    public void end(RuleContext ctx) {
        processResults(ctx);
        super.end(ctx);
    }

    private void processResults(Object data) {

        int threshold = getProperty(THRESHOLD_DESCRIPTOR);

        for (Map.Entry<String, SortedSet<ASTStringLiteral>> entry : literals.entrySet()) {
            SortedSet<ASTStringLiteral> occurrences = entry.getValue();
            if (occurrences.size() >= threshold) {
                ASTStringLiteral first = occurrences.first();
                String rawImage = first.getImage();
                Object[] args = {rawImage, occurrences.size(), first.getBeginLine(), };
                addViolation(data, first, args);
            }
        }
    }

    @Override
    public Object visit(ASTStringLiteral node, Object data) {
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
        if (getProperty(SKIP_ANNOTATIONS_DESCRIPTOR) && node.ancestors(ASTAnnotation.class).nonEmpty()) {
            return data;
        }

        // This is a rulechain rule - the nodes might be visited out of order. Therefore sort the occurrences.
        SortedSet<ASTStringLiteral> occurrences = literals.computeIfAbsent(image,
                key -> new TreeSet<>(Node.COORDS_COMPARATOR));
        occurrences.add(node);

        return data;
    }

}

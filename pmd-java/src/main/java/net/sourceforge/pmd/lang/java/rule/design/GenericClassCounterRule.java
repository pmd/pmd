/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.rule.regex.RegexHelper;
import net.sourceforge.pmd.properties.StringMultiProperty;
import net.sourceforge.pmd.properties.StringProperty;

/**
 * <p>
 * A generic rule that can be configured to "count" classes of certain type
 * based on either their name (full name, prefix, suffixes anything can be
 * matched with a regex), and/or their type.
 * </p>
 *
 * <p>Example of configurations:</p>
 *
 * <pre>
 *     &lt;!-- Property order is MANDATORY !!! --&gt;
 *     &lt;!-- Several regexes may be provided to ensure a match... --&gt;
 *     &lt;property name="nameMatch" description="a regex on which to match"
 *         value="^Abstract.*Bean*$,^*EJB*$"/&gt;
 *     &lt;!-- An operand to refine match strategy TODO: Not implemented yet !!! --&gt;
 *     &lt;property name"operand" description=""
 *         value="and"/&gt; &lt;!-- possible values are and/or --&gt;
 *     &lt;!-- Must be a full name to ensure type control !!! --&gt;
 *     &lt;property name="typeMatch" description="a regex to match on implements/extends classname"
 *         value="javax.servlet.Filter"/&gt;
 *     &lt;!-- Define after how many occurences one should log a violation --&gt;
 *     &lt;property name="threshold" description="Defines how many occurences are legal"
 *         value="2"/&gt;
 *     &lt;!-- TODO: Add a parameter to allow "ignore" pattern based on name --&gt;
 * </pre>
 *
 * @author Ryan Gutafson, rgustav@users.sourceforge.net
 * @author Romain PELISSE, belaran@gmail.com
 *
 */
public class GenericClassCounterRule extends AbstractJavaRule {

    // Class is unused, properties won't be converted
    private static final StringMultiProperty NAME_MATCH_DESCRIPTOR = new StringMultiProperty("nameMatch",
            "A series of regex, separated by ',' to match on the classname", new String[] { "" }, 1.0f, ',');

    private static final StringProperty OPERAND_DESCRIPTOR = new StringProperty("operand",
            "or/and value to refined match criteria", new String(), 2.0f);

    private static final StringMultiProperty TYPE_MATCH_DESCRIPTOR = new StringMultiProperty("typeMatch",
            "A series of regex, separated by ',' to match on implements/extends classname", new String[] { "" }, 3.0f,
            ',');

    // TODO - this should be an IntegerProperty instead?
    private static final StringProperty THRESHOLD_DESCRIPTOR = new StringProperty("threshold",
            "Defines how many occurences are legal", new String(), 4.0f);

    private List<Pattern> namesMatch = new ArrayList<>(0);
    private List<Pattern> typesMatch = new ArrayList<>(0);
    private List<Node> matches = new ArrayList<>(0);
    private List<String> simpleClassname = new ArrayList<>(0);

    // When the rule is finished, this field will be used.
    @SuppressWarnings("PMD")
    private String operand;
    private int threshold;

    private static String counterLabel;

    public GenericClassCounterRule() {
        definePropertyDescriptor(NAME_MATCH_DESCRIPTOR);
        definePropertyDescriptor(OPERAND_DESCRIPTOR);
        definePropertyDescriptor(TYPE_MATCH_DESCRIPTOR);
        definePropertyDescriptor(THRESHOLD_DESCRIPTOR);
    }

    protected void init() {
        // Creating the attribute name for the rule context
        counterLabel = this.getClass().getSimpleName() + ".number of match";
        // Constructing the request from the input parameters
        this.namesMatch = RegexHelper.compilePatternsFromList(getProperty(NAME_MATCH_DESCRIPTOR));
        this.operand = getProperty(OPERAND_DESCRIPTOR);
        this.typesMatch = RegexHelper.compilePatternsFromList(getProperty(TYPE_MATCH_DESCRIPTOR));
        String thresholdAsString = getProperty(THRESHOLD_DESCRIPTOR);
        this.threshold = Integer.valueOf(thresholdAsString);
        // Initializing list of match
        this.matches = new ArrayList<>();

    }

    @Override
    public void start(RuleContext ctx) {
        // Adding the proper attribute to the context
        ctx.setAttribute(counterLabel, new AtomicLong());
        super.start(ctx);
    }

    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        init();
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTImportDeclaration node, Object data) {
        // Is there any imported types that match ?
        for (Pattern pattern : this.typesMatch) {
            if (RegexHelper.isMatch(pattern, node.getImportedName())) {
                if (simpleClassname == null) {
                    simpleClassname = new ArrayList<>(1);
                }
                simpleClassname.add(node.getImportedName());
            }
            // FIXME: use type resolution framework to deal with star import ?
        }
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTClassOrInterfaceType classType, Object data) {
        // Is extends/implements list using one of the previous match on import ?
        // FIXME: use type resolution framework to deal with star import ?
        for (String matchType : simpleClassname) {
            if (searchForAMatch(matchType, classType)) {
                addAMatch(classType, data);
            }
        }
        // TODO: implements the "operand" functionnality
        // Is there any names that actually match ?
        for (Pattern pattern : this.namesMatch) {
            if (RegexHelper.isMatch(pattern, classType.getImage())) {
                addAMatch(classType, data);
            }
        }
        return super.visit(classType, data);
    }

    private void addAMatch(Node node, Object data) {
        // We have a match, we increment
        RuleContext ctx = (RuleContext) data;
        AtomicLong total = (AtomicLong) ctx.getAttribute(counterLabel);
        total.incrementAndGet();
        // And we keep a ref on the node for the report generation
        this.matches.add(node);
    }

    private boolean searchForAMatch(String matchType, Node node) {
        String xpathQuery = "//ClassOrInterfaceDeclaration[(./ExtendsList/ClassOrInterfaceType[@Image = '" + matchType
                + "']) or (./ImplementsList/ClassOrInterfaceType[@Image = '" + matchType + "'])]";

        return node.hasDescendantMatchingXPath(xpathQuery);
    }

    @Override
    public void end(RuleContext ctx) {
        AtomicLong total = (AtomicLong) ctx.getAttribute(counterLabel);
        // Do we have a violation ?
        if (total.get() > this.threshold) {
            for (Node node : this.matches) {
                addViolation(ctx, node, new Object[] { total });
            }
            // Cleaning the context for the others rules
            ctx.removeAttribute(counterLabel);
            super.end(ctx);
        }
    }
}

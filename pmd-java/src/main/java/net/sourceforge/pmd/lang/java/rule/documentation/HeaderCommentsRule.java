/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.documentation;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.properties.EnumeratedProperty;
import net.sourceforge.pmd.properties.StringMultiProperty;

/**
 * Restrictions regarding the legal placement and content of the file header.
 *
 * @author Brian Remedios
 */
public class HeaderCommentsRule extends AbstractCommentRule {

    // Rule is not used and not implemented, properties won't be converted

    private static final String[] REQUIRED_WORKDS = new String[] { "copyright" };
    private static final String[] REQUIRED_TAGS = new String[] { "author", "version" };

    public static final StringMultiProperty REQUIRED_TERMS_DESCRIPTOR = new StringMultiProperty("requiredTerms",
            "Expected terms or phrases in the code header", REQUIRED_WORKDS, 1.0f, '|');

    public static final StringMultiProperty REQUIRED_TAGS_DESCRIPTOR = new StringMultiProperty("requiredTags",
            "Expected tags in the header", REQUIRED_TAGS, 2.0f, '|');

    enum RequiredHeaderPlacement {
        BeforePackageDeclaration("Before package"),
        BeforeImportStatements("Before imports"),
        BeforeTypeDeclaration("Before types"),
        Anywhere("Anywhere");

        private final String label;

        RequiredHeaderPlacement(String theLabel) {
            label = theLabel;
        }

        public static String[] labels() {
            String[] labels = new String[values().length];
            int i = 0;
            for (RequiredHeaderPlacement placement : values()) {
                labels[i++] = placement.label;
            }
            return labels;
        }
    }

    public static final EnumeratedProperty<RequiredHeaderPlacement> HEADER_PLACEMENT_DESCRIPTOR = new EnumeratedProperty<>(
            "headerPlacement", "Placement of the header comment", RequiredHeaderPlacement.labels(),
            RequiredHeaderPlacement.values(), 0, RequiredHeaderPlacement.class, 3.0f);

    public HeaderCommentsRule() {
        definePropertyDescriptor(REQUIRED_TERMS_DESCRIPTOR);
        definePropertyDescriptor(REQUIRED_TAGS_DESCRIPTOR);
        definePropertyDescriptor(HEADER_PLACEMENT_DESCRIPTOR);
    }

    @Override
    public Object visit(ASTCompilationUnit cUnit, Object data) {

        // SortedMap<Integer, Object> itemsByLineNumber =
        // orderedCommentsAndDeclarations(cUnit);

        return super.visit(cUnit, data);
    }
}

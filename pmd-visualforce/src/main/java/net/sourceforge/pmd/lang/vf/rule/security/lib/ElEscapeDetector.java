/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.rule.security.lib;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.vf.DataType;
import net.sourceforge.pmd.lang.vf.ast.ASTArguments;
import net.sourceforge.pmd.lang.vf.ast.ASTDotExpression;
import net.sourceforge.pmd.lang.vf.ast.ASTElExpression;
import net.sourceforge.pmd.lang.vf.ast.ASTExpression;
import net.sourceforge.pmd.lang.vf.ast.ASTIdentifier;
import net.sourceforge.pmd.lang.vf.ast.ASTNegationExpression;
import net.sourceforge.pmd.lang.vf.ast.AbstractVFNode;
import net.sourceforge.pmd.lang.vf.ast.VfTypedNode;

/**
 * Helps detect visualforce encoding in EL Expressions
 * (porting over code previously living in VfUnescapeElRule for reusability)
 */

public final class ElEscapeDetector {

    public boolean innerContainsSafeFields(final AbstractVFNode expression) {
        for (int i = 0; i < expression.getNumChildren(); i++) {
            Node child = expression.getChild(i);

            if (child instanceof ASTIdentifier) {
                switch (child.getImage().toLowerCase(Locale.ROOT)) {
                case "id":
                case "size":
                case "caseNumber":
                    return true;
                default:
                }
            }

            if (child instanceof ASTArguments) {
                if (containsSafeFields((ASTArguments) child)) {
                    return true;
                }
            }

            if (child instanceof ASTDotExpression) {
                if (innerContainsSafeFields((ASTDotExpression) child)) {
                    return true;
                }
            }

        }

        return false;
    }

    public boolean containsSafeFields(final AbstractVFNode expression) {
        final ASTExpression ex = expression.getFirstChildOfType(ASTExpression.class);

        return ex != null && innerContainsSafeFields(ex);

    }

    public boolean startsWithSafeResource(final ASTElExpression el) {
        final ASTExpression expression = el.getFirstChildOfType(ASTExpression.class);
        if (expression != null) {
            final ASTNegationExpression negation = expression.getFirstChildOfType(ASTNegationExpression.class);
            if (negation != null) {
                return true;
            }

            final ASTIdentifier id = expression.getFirstChildOfType(ASTIdentifier.class);
            if (id != null) {
                String lowerCaseId = id.getImage().toLowerCase(Locale.ROOT);
                List<ASTArguments> args = expression.findChildrenOfType(ASTArguments.class);

                if (!args.isEmpty()) {
                    switch (lowerCaseId) {
                    case "urlfor":
                    case "casesafeid":
                    case "begins":
                    case "contains":
                    case "len":
                    case "getrecordids":
                    case "linkto":
                    case "sqrt":
                    case "round":
                    case "mod":
                    case "log":
                    case "ln":
                    case "exp":
                    case "abs":
                    case "floor":
                    case "ceiling":
                    case "nullvalue":
                    case "isnumber":
                    case "isnull":
                    case "isnew":
                    case "isblank":
                    case "isclone":
                    case "year":
                    case "month":
                    case "day":
                    case "datetimevalue":
                    case "datevalue":
                    case "date":
                    case "now":
                    case "today":
                        return true;

                    default:
                    }
                } else {
                    // has no arguments
                    switch (lowerCaseId) {
                    case "$action":
                    case "$page":
                    case "$site":
                    case "$resource":
                    case "$label":
                    case "$objecttype":
                    case "$component":
                    case "$remoteaction":
                    case "$messagechannel":
                        return true;

                    default:
                    }
                }
            }
        }
        return false;
    }

    public boolean doesElContainAnyUnescapedIdentifiers(final ASTElExpression elExpression, Escaping escape) {
        return doesElContainAnyUnescapedIdentifiers(elExpression, EnumSet.of(escape));

    }

    public boolean doesElContainAnyUnescapedIdentifiers(final ASTElExpression elExpression,
                                                         EnumSet<Escaping> escapes) {
        if (elExpression == null) {
            return false;
        }

        final Set<ASTIdentifier> nonEscapedIds = new HashSet<>();

        final List<ASTExpression> exprs = elExpression.findChildrenOfType(ASTExpression.class);
        for (final ASTExpression expr : exprs) {

            if (innerContainsSafeFields(expr)) {
                continue;
            }

            if (expressionContainsSafeDataNodes(expr)) {
                continue;
            }

            final List<ASTIdentifier> ids = expr.findChildrenOfType(ASTIdentifier.class);
            for (final ASTIdentifier id : ids) {
                boolean isEscaped = false;

                for (Escaping e : escapes) {

                    if (id.getImage().equalsIgnoreCase(e.toString())) {
                        isEscaped = true;
                        break;
                    }

                    if (e.equals(Escaping.ANY)) {
                        for (Escaping esc : Escaping.values()) {
                            if (id.getImage().equalsIgnoreCase(esc.toString())) {
                                isEscaped = true;
                                break;
                            }
                        }
                    }

                }

                if (!isEscaped) {
                    nonEscapedIds.add(id);
                }
            }

        }

        return !nonEscapedIds.isEmpty();
    }

    /**
     * Return true if the type of all data nodes can be determined and none of them require escaping
     * @param expression
     */
    public boolean expressionContainsSafeDataNodes(ASTExpression expression) {
        try {
            for (VfTypedNode node : expression.getDataNodes().keySet()) {
                DataType dataType = node.getDataType();
                if (dataType == null || dataType.requiresEscaping) {
                    return false;
                }
            }

            return true;
        } catch (ASTExpression.DataNodeStateException e) {
            return false;
        }
    }

    public enum Escaping {
        HTMLENCODE("HTMLENCODE"),
        URLENCODE("URLENCODE"),
        JSINHTMLENCODE("JSINHTMLENCODE"),
        JSENCODE("JSENCODE"),
        ANY("ANY");

        private final String text;

        Escaping(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

}

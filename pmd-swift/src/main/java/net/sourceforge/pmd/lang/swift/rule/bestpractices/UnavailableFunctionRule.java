/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.rule.bestpractices;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.document.Chars;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;
import net.sourceforge.pmd.lang.swift.ast.SwiftParser.SwAttribute;
import net.sourceforge.pmd.lang.swift.ast.SwiftParser.SwAttributes;
import net.sourceforge.pmd.lang.swift.ast.SwiftParser.SwCodeBlock;
import net.sourceforge.pmd.lang.swift.ast.SwiftParser.SwFunctionDeclaration;
import net.sourceforge.pmd.lang.swift.ast.SwiftParser.SwInitializerDeclaration;
import net.sourceforge.pmd.lang.swift.ast.SwiftParser.SwStatement;
import net.sourceforge.pmd.lang.swift.ast.SwiftVisitor;
import net.sourceforge.pmd.lang.swift.ast.SwiftVisitorBase;
import net.sourceforge.pmd.lang.swift.rule.AbstractSwiftRule;
import net.sourceforge.pmd.reporting.RuleContext;

public class UnavailableFunctionRule extends AbstractSwiftRule {

    private static final Pattern AVAILABLE_UNAVAILABLE = Pattern.compile("^\\s*@available\\s*\\(\\s*\\*\\s*,\\s*unavailable\\s*\\)\\s*$", Pattern.CASE_INSENSITIVE);
    private static final String FATAL_ERROR = "fatalError";

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(SwFunctionDeclaration.class, SwInitializerDeclaration.class);
    }

    @Override
    public SwiftVisitor<RuleContext, ?> buildVisitor() {
        return new SwiftVisitorBase<RuleContext, Void>() {

            @Override
            public Void visitFunctionDeclaration(final SwFunctionDeclaration ctx, RuleContext ruleCtx) {
                if (ctx == null) {
                    return null;
                }

                if (shouldIncludeUnavailableModifier(ctx.functionBody().codeBlock())) {
                    final SwAttributes attributes = ctx.functionHead().attributes();
                    if (attributes == null || !hasUnavailableModifier(attributes.attribute())) {
                        ruleCtx.addViolation(ctx);
                    }
                }

                return null;
            }

            @Override
            public Void visitInitializerDeclaration(final SwInitializerDeclaration ctx, RuleContext ruleCtx) {
                if (ctx == null) {
                    return null;
                }

                if (shouldIncludeUnavailableModifier(ctx.initializerBody().codeBlock())) {
                    final SwAttributes attributes = ctx.initializerHead().attributes();
                    if (attributes == null || !hasUnavailableModifier(attributes.attribute())) {
                        ruleCtx.addViolation(ctx);
                    }
                }

                return null;
            }

            private boolean shouldIncludeUnavailableModifier(final SwCodeBlock ctx) {
                if (ctx == null || ctx.statements() == null) {
                    return false;
                }

                final List<SwStatement> statements = ctx.statements().statement();

                return statements.size() == 1 && FATAL_ERROR.equals(statements.get(0).getFirstAntlrToken().getText());
            }

            private boolean hasUnavailableModifier(final List<SwAttribute> attributes) {
                return attributes.stream().anyMatch(attr -> {
                        Chars text = attr.getTextDocument().sliceTranslatedText(attr.getTextRegion());
                        Matcher matcher = AVAILABLE_UNAVAILABLE.matcher(text);
                        return matcher.matches();
                    }
                );
            }
        };
    }

}

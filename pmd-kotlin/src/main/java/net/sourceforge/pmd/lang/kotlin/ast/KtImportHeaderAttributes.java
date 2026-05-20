/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.annotation.Experimental;

/**
 * @since 7.25.0
 * @experimental See {@link AttributeView}.
 */
@Experimental
public final class KtImportHeaderAttributes extends AttributeView<KotlinParser.KtImportHeader> {
    public KtImportHeaderAttributes(KotlinParser.KtImportHeader node) {
        super(node);
    }

    public @Nullable String getName() {
        for (int i = 0; i < node.getNumChildren(); i++) {
            KotlinNode child = node.getChild(i);
            if (child instanceof KotlinParser.KtIdentifier) {
                return buildFqnFromIdentifier(child);
            }
        }
        return null;
    }

    private static @Nullable String buildFqnFromIdentifier(KotlinNode identifierNode) {
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < identifierNode.getNumChildren(); j++) {
            KotlinNode part = identifierNode.getChild(j);
            if (part instanceof KotlinParser.KtSimpleIdentifier && part.getNumChildren() > 0) {
                KotlinNode token = part.getChild(0);
                if (token instanceof KotlinTerminalNode) {
                    if (sb.length() > 0) {
                        sb.append('.');
                    }
                    sb.append(((KotlinTerminalNode) token).getText());
                }
            }
        }
        return sb.length() > 0 ? sb.toString() : null;
    }
}

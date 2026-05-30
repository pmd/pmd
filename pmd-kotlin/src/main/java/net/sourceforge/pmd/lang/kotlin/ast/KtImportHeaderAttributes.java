/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.kotlin.ast.internal.KotlinAstUtil;

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
                return KotlinAstUtil.dottedTextOf(child);
            }
        }
        return null;
    }
}

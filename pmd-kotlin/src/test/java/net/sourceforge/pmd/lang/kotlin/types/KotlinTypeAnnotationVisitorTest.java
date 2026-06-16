/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.types;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Collections;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtFunctionDeclaration;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtKotlinFile;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtPropertyDeclaration;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParsingHelper;

import nl.stokpop.typemapper.analyzer.KotlinTypeMapper;
import nl.stokpop.typemapper.model.TypedAst;

class KotlinTypeAnnotationVisitorTest {

    private static final KotlinParsingHelper PARSER = KotlinParsingHelper.DEFAULT;

    private static KotlinTypeAnnotationVisitor visitorFor(String source) {
        TypedAst ast = KotlinTypeMapper.fromSources(
                Collections.singletonMap("snippet.kt", source),
                Collections.emptyList());
        return new KotlinTypeAnnotationVisitor(ast);
    }

    @Test
    void propertyDeclarationTypeNameSet() {
        String source = "val x: String = \"hello\"";
        KtKotlinFile root = PARSER.parse(source);
        visitorFor(source).annotate(root, "snippet.kt");

        KtPropertyDeclaration prop = root.descendants(KtPropertyDeclaration.class).first();
        assertNotNull(prop);
        assertNotNull(KotlinNodeTypeData.getTypeName(prop));
    }

    @Test
    void functionDeclarationReturnTypeNameSet() {
        String source = "fun greet(): String = \"hi\"";
        KtKotlinFile root = PARSER.parse(source);
        visitorFor(source).annotate(root, "snippet.kt");

        KtFunctionDeclaration fn = root.descendants(KtFunctionDeclaration.class).first();
        assertNotNull(fn);
        assertNotNull(KotlinNodeTypeData.getReturnTypeName(fn));
    }

    @Test
    void unknownFileProducesNoAnnotations() {
        String source = "val x: String = \"hello\"";
        KtKotlinFile root = PARSER.parse(source);
        visitorFor(source).annotate(root, "other.kt");

        KtPropertyDeclaration prop = root.descendants(KtPropertyDeclaration.class).first();
        assertNotNull(prop);
        assertNull(KotlinNodeTypeData.getTypeName(prop));
    }

    @Test
    void returnTypeNameMatchesKotlinType() {
        String source = "fun count(): Int = 42";
        KtKotlinFile root = PARSER.parse(source);
        visitorFor(source).annotate(root, "snippet.kt");

        KtFunctionDeclaration fn = root.descendants(KtFunctionDeclaration.class).first();
        assertEquals("kotlin.Int", KotlinNodeTypeData.getReturnTypeName(fn));
    }
}

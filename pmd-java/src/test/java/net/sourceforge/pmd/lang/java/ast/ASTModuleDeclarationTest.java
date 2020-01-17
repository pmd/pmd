/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

public class ASTModuleDeclarationTest extends BaseParserTest {

    @Test
    public final void jdk9ModuleInfo() {
        ASTCompilationUnit ast = java9.parseResource("jdkversiontests/jdk9_module_info.java");
        List<ASTModuleDeclaration> modules = ast.findDescendantsOfType(ASTModuleDeclaration.class);
        assertEquals(1, modules.size());
        ASTModuleDeclaration module = modules.get(0);
        assertTrue(module.isOpen());
        assertEquals("com.example.foo", module.getImage());
        assertEquals(7, module.getNumChildren());
        List<ASTModuleDirective> directives = module.findChildrenOfType(ASTModuleDirective.class);
        assertEquals(7, directives.size());

        // requires com.example.foo.http;
        assertEquals(ASTModuleDirective.DirectiveType.REQUIRES.name(), directives.get(0).getType());
        assertNull(directives.get(0).getRequiresModifier());
        assertEquals("com.example.foo.http", directives.get(0).getFirstChildOfType(ASTModuleName.class).getImage());

        // requires java.logging;
        assertEquals(ASTModuleDirective.DirectiveType.REQUIRES.name(), directives.get(1).getType());
        assertNull(directives.get(1).getRequiresModifier());
        assertEquals("java.logging", directives.get(1).getFirstChildOfType(ASTModuleName.class).getImage());

        // requires transitive com.example.foo.network;
        assertEquals(ASTModuleDirective.DirectiveType.REQUIRES.name(), directives.get(2).getType());
        assertEquals(ASTModuleDirective.RequiresModifier.TRANSITIVE.name(), directives.get(2).getRequiresModifier());
        assertEquals("com.example.foo.network", directives.get(2).getFirstChildOfType(ASTModuleName.class).getImage());

        // exports com.example.foo.bar;
        assertEquals(ASTModuleDirective.DirectiveType.EXPORTS.name(), directives.get(3).getType());
        assertNull(directives.get(3).getRequiresModifier());
        assertEquals("com.example.foo.bar", directives.get(3).getFirstChildOfType(ASTName.class).getImage());

        // exports com.example.foo.internal to com.example.foo.probe;
        assertEquals(ASTModuleDirective.DirectiveType.EXPORTS.name(), directives.get(4).getType());
        assertNull(directives.get(4).getRequiresModifier());
        assertEquals("com.example.foo.internal", directives.get(4).getFirstChildOfType(ASTName.class).getImage());
        assertEquals("com.example.foo.probe", directives.get(4).getFirstChildOfType(ASTModuleName.class).getImage());

        // uses com.example.foo.spi.Intf;
        assertEquals(ASTModuleDirective.DirectiveType.USES.name(), directives.get(5).getType());
        assertNull(directives.get(5).getRequiresModifier());
        assertEquals("com.example.foo.spi.Intf", directives.get(5).getFirstChildOfType(ASTName.class).getImage());

        // provides com.example.foo.spi.Intf with com.example.foo.Impl;
        assertEquals(ASTModuleDirective.DirectiveType.PROVIDES.name(), directives.get(6).getType());
        assertNull(directives.get(6).getRequiresModifier());
        assertEquals("com.example.foo.spi.Intf", directives.get(6).getFirstChildOfType(ASTName.class).getImage());
        assertEquals("com.example.foo.Impl", directives.get(6).findChildrenOfType(ASTName.class).get(1).getImage());
    }

}

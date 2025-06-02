/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.InputStream;

import org.junit.jupiter.api.Test;
import org.pcollections.PSet;

import net.sourceforge.pmd.lang.java.symbols.JModuleSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue;
import net.sourceforge.pmd.lang.java.types.TypeSystem;

class ModuleStubTest {
    @Test
    void loadModuleInfo() {
        TypeSystem ts = TypeSystem.usingClasspath(name -> {
            ClassLoader classLoader = ModuleStubTest.class.getClassLoader();

            // make sure to load our special prepared test resources
            if (name.startsWith("net/sourceforge/pmd") || name.startsWith("test.net.sourceforge.pmd/")) {
                String prefixForTest = "net/sourceforge/pmd/lang/java/symbols/modules/";
                String nameToLookup = name;
                if (name.endsWith("/module-info.class")) {
                    nameToLookup = "module-info.class";
                }
                InputStream lookup = classLoader.getResourceAsStream(prefixForTest + nameToLookup);
                if (lookup != null) {
                    return lookup;
                }
            }

            return classLoader.getResourceAsStream(name);
        });
        JModuleSymbol moduleSymbol = ts.getModuleSymbol("test.net.sourceforge.pmd");
        assertThat(moduleSymbol.getExportedPackages(), hasSize(3));
        assertThat(moduleSymbol.getExportedPackages(), hasItems("net.sourceforge.pmd", "net.sourceforge.pmd.annotation", "net.sourceforge.pmd.lang"));

        PSet<SymbolicValue.SymAnnot> annotations = moduleSymbol.getDeclaredAnnotations();
        assertThat(annotations.size(), is(1));
        assertEquals("net.sourceforge.pmd.annotation.ModuleTestExperimental", annotations.stream().findFirst().get().getBinaryName());
    }
}

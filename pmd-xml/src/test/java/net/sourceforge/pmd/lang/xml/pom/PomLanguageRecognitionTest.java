/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xml.pom;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static net.sourceforge.pmd.util.CollectionUtil.setOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersionDiscoverer;
import net.sourceforge.pmd.lang.xml.XmlLanguageModule;

class PomLanguageRecognitionTest {



    @Test
    void testPomLanguageRecognizesPomXML() {

        PomLanguageModule pom = PomLanguageModule.getInstance();
        XmlLanguageModule xml = XmlLanguageModule.getInstance();
        LanguageRegistry reg = new LanguageRegistry(setOf(xml, pom));

        LanguageVersionDiscoverer discoverer = new LanguageVersionDiscoverer(reg);

        List<Language> languagesForFile = discoverer.getLanguagesForFile("pom.xml");

        assertEquals(languagesForFile, listOf(pom));
    }

    @Test
    void testPomLanguageDefaultsToXMLIfPOMNotLoaded() {

        XmlLanguageModule xml = XmlLanguageModule.getInstance();
        LanguageRegistry reg = new LanguageRegistry(setOf(xml));

        LanguageVersionDiscoverer discoverer = new LanguageVersionDiscoverer(reg);

        List<Language> languagesForFile = discoverer.getLanguagesForFile("pom.xml");

        assertEquals(languagesForFile, listOf(xml));
    }


}

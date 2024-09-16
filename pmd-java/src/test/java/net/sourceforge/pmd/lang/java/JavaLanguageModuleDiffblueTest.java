package net.sourceforge.pmd.lang.java;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;
import java.util.Set;
import net.sourceforge.pmd.lang.LanguageVersion;
import org.junit.jupiter.api.Test;

class JavaLanguageModuleDiffblueTest {
  /**
   * Method under test: default or parameterless constructor of
   * {@link JavaLanguageModule}
   */
  @Test
  void testNewJavaLanguageModule() {
    // Arrange and Act
    JavaLanguageModule actualJavaLanguageModule = new JavaLanguageModule();

    // Assert
    List<LanguageVersion> versions = actualJavaLanguageModule.getVersions();
    assertEquals(23, versions.size());
    LanguageVersion getResult = versions.get(0);
    assertEquals("1.3", getResult.getVersion());
    LanguageVersion getResult2 = versions.get(1);
    assertEquals("1.4", getResult2.getVersion());
    LanguageVersion getResult3 = versions.get(2);
    assertEquals("1.5", getResult3.getVersion());
    LanguageVersion getResult4 = versions.get(20);
    assertEquals("22-preview", getResult4.getVersion());
    LanguageVersion defaultVersion = actualJavaLanguageModule.getDefaultVersion();
    assertEquals("23", defaultVersion.getVersion());
    LanguageVersion latestVersion = actualJavaLanguageModule.getLatestVersion();
    assertEquals("23-preview", latestVersion.getVersion());
    assertEquals("Java 1.3", getResult.getName());
    assertEquals("Java 1.3", getResult.getShortName());
    assertEquals("Java 1.4", getResult2.getName());
    assertEquals("Java 1.4", getResult2.getShortName());
    assertEquals("Java 1.5", getResult3.getName());
    assertEquals("Java 1.5", getResult3.getShortName());
    assertEquals("Java 22-preview", getResult4.getName());
    assertEquals("Java 22-preview", getResult4.getShortName());
    assertEquals("Java 23", defaultVersion.getName());
    assertEquals("Java 23", defaultVersion.getShortName());
    assertEquals("Java 23-preview", latestVersion.getName());
    assertEquals("Java 23-preview", latestVersion.getShortName());
    assertEquals("Java", actualJavaLanguageModule.getName());
    assertEquals("Java", actualJavaLanguageModule.getShortName());
    assertEquals("java 1.3", getResult.getTerseName());
    assertEquals("java 1.4", getResult2.getTerseName());
    assertEquals("java 1.5", getResult3.getTerseName());
    assertEquals("java 22-preview", getResult4.getTerseName());
    assertEquals("java 23", defaultVersion.getTerseName());
    assertEquals("java 23-preview", latestVersion.getTerseName());
    List<String> extensions = actualJavaLanguageModule.getExtensions();
    assertEquals(1, extensions.size());
    assertEquals("java", extensions.get(0));
    assertEquals("java", actualJavaLanguageModule.getId());
    Set<String> versionNamesAndAliases = actualJavaLanguageModule.getVersionNamesAndAliases();
    assertEquals(29, versionNamesAndAliases.size());
    assertTrue(versionNamesAndAliases.contains("10"));
    assertTrue(versionNamesAndAliases.contains("11"));
    assertTrue(versionNamesAndAliases.contains("12"));
    assertTrue(versionNamesAndAliases.contains("13"));
    assertTrue(versionNamesAndAliases.contains("22"));
    assertTrue(versionNamesAndAliases.contains("23"));
    assertTrue(actualJavaLanguageModule.getDependencies().isEmpty());
    assertSame(actualJavaLanguageModule, getResult.getLanguage());
    assertSame(actualJavaLanguageModule, getResult2.getLanguage());
    assertSame(actualJavaLanguageModule, getResult3.getLanguage());
    assertSame(actualJavaLanguageModule, getResult4.getLanguage());
    assertSame(actualJavaLanguageModule, latestVersion.getLanguage());
    assertSame(actualJavaLanguageModule, defaultVersion.getLanguage());
    assertSame(latestVersion, versions.get(22));
    assertSame(defaultVersion, versions.get(21));
  }
}

package net.sourceforge.pmd.cache.internal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

import net.sourceforge.pmd.lang.document.FileId;

import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.rule.Rule;
import net.sourceforge.pmd.reporting.RuleViolation;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class CachedRuleViolationDiffblueTest {
    /**
     * Method under test: {@link CachedRuleViolation#getRule()}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testGetRule() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot read the array length because "b" is null
        //       at java.base/java.io.DataInputStream.readFully(DataInputStream.java:205)
        //       at java.base/java.io.DataInputStream.readUnsignedShort(DataInputStream.java:341)
        //       at java.base/java.io.DataInputStream.readUTF(DataInputStream.java:575)
        //       at java.base/java.io.DataInputStream.readUTF(DataInputStream.java:550)
        //       at net.sourceforge.pmd.cache.internal.CachedRuleViolation.loadFromStream(CachedRuleViolation.java:88)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        CachedRuleViolation cachedRuleViolation = null;

        // Act
        Rule actualRule = cachedRuleViolation.getRule();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test:
     * {@link CachedRuleViolation#loadFromStream(DataInputStream, FileId, CachedRuleMapper)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testLoadFromStream() throws IOException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot read the array length because "b" is null
        //       at java.base/java.io.DataInputStream.readFully(DataInputStream.java:205)
        //       at java.base/java.io.DataInputStream.readUnsignedShort(DataInputStream.java:341)
        //       at java.base/java.io.DataInputStream.readUTF(DataInputStream.java:575)
        //       at java.base/java.io.DataInputStream.readUTF(DataInputStream.java:550)
        //       at net.sourceforge.pmd.cache.internal.CachedRuleViolation.loadFromStream(CachedRuleViolation.java:88)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        DataInputStream stream = null;
        FileId fileFileId = null;
        CachedRuleMapper mapper = null;

        // Act
        CachedRuleViolation actualLoadFromStreamResult = CachedRuleViolation.loadFromStream(stream, fileFileId, mapper);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test:
     * {@link CachedRuleViolation#storeToStream(DataOutputStream, RuleViolation)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testStoreToStream() throws IOException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot read the array length because "b" is null
        //       at java.base/java.io.DataInputStream.readFully(DataInputStream.java:205)
        //       at java.base/java.io.DataInputStream.readUnsignedShort(DataInputStream.java:341)
        //       at java.base/java.io.DataInputStream.readUTF(DataInputStream.java:575)
        //       at java.base/java.io.DataInputStream.readUTF(DataInputStream.java:550)
        //       at net.sourceforge.pmd.cache.internal.CachedRuleViolation.loadFromStream(CachedRuleViolation.java:88)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        DataOutputStream stream = null;
        RuleViolation violation = null;

        // Act
        CachedRuleViolation.storeToStream(stream, violation);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Methods under test:
     * <ul>
     *   <li>{@link CachedRuleViolation#getAdditionalInfo()}
     *   <li>{@link CachedRuleViolation#getDescription()}
     *   <li>{@link CachedRuleViolation#getLocation()}
     * </ul>
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testGettersAndSetters() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Exception in arrange section.
        //   Diffblue Cover was unable to construct an instance of the class under test using
        //   net.sourceforge.pmd.cache.internal.CachedRuleViolation.getAdditionalInfo().
        //   The arrange section threw
        //   java.lang.NullPointerException: Cannot read the array length because "b" is null
        //       at java.base/java.io.DataInputStream.readFully(DataInputStream.java:205)
        //       at java.base/java.io.DataInputStream.readUnsignedShort(DataInputStream.java:341)
        //       at java.base/java.io.DataInputStream.readUTF(DataInputStream.java:575)
        //       at java.base/java.io.DataInputStream.readUTF(DataInputStream.java:550)
        //       at net.sourceforge.pmd.cache.internal.CachedRuleViolation.loadFromStream(CachedRuleViolation.java:88)
        //   See https://diff.blue/R081 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        CachedRuleViolation cachedRuleViolation = null;

        // Act
        Map<String, String> actualAdditionalInfo = cachedRuleViolation.getAdditionalInfo();
        String actualDescription = cachedRuleViolation.getDescription();
        FileLocation actualLocation = cachedRuleViolation.getLocation();

        // Assert
        // TODO: Add assertions on result
    }
}

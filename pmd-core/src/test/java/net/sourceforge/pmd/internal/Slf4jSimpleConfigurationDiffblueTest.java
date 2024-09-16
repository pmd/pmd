package net.sourceforge.pmd.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.event.Level;

class Slf4jSimpleConfigurationDiffblueTest {
    /**
     * Method under test:
     * {@link Slf4jSimpleConfiguration#reconfigureDefaultLogLevel(Level)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testReconfigureDefaultLogLevel() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Diffblue AI was unable to find a test

        // Arrange
        // TODO: Populate arranged inputs
        Level level = Level.ERROR;

        // Act
        Slf4jSimpleConfiguration.reconfigureDefaultLogLevel(level);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link Slf4jSimpleConfiguration#getDefaultLogLevel()}
     */
    @Test
    void testGetDefaultLogLevel() {
        // Arrange, Act and Assert
        assertEquals(Level.INFO, Slf4jSimpleConfiguration.getDefaultLogLevel());
    }

    /**
     * Method under test: {@link Slf4jSimpleConfiguration#disableLogging(Class)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testDisableLogging() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Diffblue AI was unable to find a test

        // Arrange
        // TODO: Populate arranged inputs
        Class<?> clazz = null;

        // Act
        Slf4jSimpleConfiguration.disableLogging(clazz);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link Slf4jSimpleConfiguration#isSimpleLogger()}
     */
    @Test
    void testIsSimpleLogger() {
        // Arrange, Act and Assert
        assertTrue(Slf4jSimpleConfiguration.isSimpleLogger());
    }

    /**
     * Method under test: {@link Slf4jSimpleConfiguration#installJulBridge()}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testInstallJulBridge() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Diffblue AI was unable to find a test

        // Arrange and Act
        // TODO: Populate arranged inputs
        Slf4jSimpleConfiguration.installJulBridge();

        // Assert
        // TODO: Add assertions on result
    }
}

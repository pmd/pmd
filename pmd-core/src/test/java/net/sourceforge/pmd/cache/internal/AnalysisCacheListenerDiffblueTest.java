package net.sourceforge.pmd.cache.internal;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import javax.management.loading.MLet;

import net.sourceforge.pmd.internal.util.ClasspathClassLoader;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.lang.rule.internal.RuleSets;
import net.sourceforge.pmd.reporting.FileAnalysisListener;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class AnalysisCacheListenerDiffblueTest {
    /**
     * Method under test: {@link AnalysisCacheListener#startFileAnalysis(TextFile)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testStartFileAnalysis() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Parameter language version is null
        //       at net.sourceforge.pmd.util.AssertionUtil.requireParamNotNull(AssertionUtil.java:209)
        //       at net.sourceforge.pmd.lang.document.StringTextFile.<init>(StringTextFile.java:25)
        //       at net.sourceforge.pmd.lang.document.SimpleTestTextFile.<init>(SimpleTestTextFile.java:15)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        AnalysisCacheListener analysisCacheListener = null;
        TextFile file = null;

        // Act
        FileAnalysisListener actualStartFileAnalysisResult = analysisCacheListener.startFileAnalysis(file);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link AnalysisCacheListener#close()}
     */
    @Test
    void testClose() throws IOException {
        // Arrange
        FileAnalysisCache cache = mock(FileAnalysisCache.class);
        doNothing().when(cache)
                .checkValidity(Mockito.<RuleSets>any(), Mockito.<ClassLoader>any(), Mockito.<Collection<TextFile>>any());
        doNothing().when(cache).persist();
        RuleSets ruleSets = new RuleSets(new ArrayList<>());
        ArrayList<File> files = new ArrayList<>();
        ClasspathClassLoader classLoader = new ClasspathClassLoader("Classpath",
                new ClasspathClassLoader(files, new MLet()));

        // Act
        (new AnalysisCacheListener(cache, ruleSets, classLoader, new ArrayList<>())).close();

        // Assert
        verify(cache).checkValidity(isA(RuleSets.class), isA(ClassLoader.class), isA(Collection.class));
        verify(cache).persist();
    }

    /**
     * Method under test:
     * {@link AnalysisCacheListener#AnalysisCacheListener(AnalysisCache, RuleSets, ClassLoader, Collection)}
     */
    @Test
    void testNewAnalysisCacheListener() throws IOException {
        // Arrange
        FileAnalysisCache cache = mock(FileAnalysisCache.class);
        doNothing().when(cache)
                .checkValidity(Mockito.<RuleSets>any(), Mockito.<ClassLoader>any(), Mockito.<Collection<TextFile>>any());
        RuleSets ruleSets = new RuleSets(new ArrayList<>());
        ArrayList<File> files = new ArrayList<>();
        ClasspathClassLoader classLoader = new ClasspathClassLoader("Classpath",
                new ClasspathClassLoader(files, new MLet()));

        // Act
        new AnalysisCacheListener(cache, ruleSets, classLoader, new ArrayList<>());

        // Assert
        verify(cache).checkValidity(isA(RuleSets.class), isA(ClassLoader.class), isA(Collection.class));
    }
}

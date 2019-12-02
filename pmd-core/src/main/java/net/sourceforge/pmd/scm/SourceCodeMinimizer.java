/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.scm;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collection;

import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.scm.invariants.Invariant;
import net.sourceforge.pmd.scm.invariants.InvariantOperations;
import net.sourceforge.pmd.scm.strategies.MinimizationStrategy;
import net.sourceforge.pmd.scm.strategies.MinimizerOperations;

public class SourceCodeMinimizer implements InvariantOperations, MinimizerOperations {
    private static final class ContinueException extends Exception { }

    private static final class ExitException extends Exception { }

    private final Language language;
    private final Invariant invariant;
    private final MinimizationStrategy strategy;
    private final Parser parser;
    private final Charset sourceCharset;
    private final ASTCutter cutter;
    private final Node originalRootNode;

    private Node currentRootNode;

    public SourceCodeMinimizer(SCMConfiguration configuration) throws IOException {
        language = configuration.getLanguageHandler();
        parser = language.getParser();
        invariant = configuration.getInvariantCheckerConfig().createChecker();
        strategy = configuration.getStrategyConfig().createStrategy();

        Path inputFile = Paths.get(configuration.getInputFileName());
        Path outputFile = Paths.get(configuration.getOutputFileName());
        Files.copy(inputFile, outputFile, StandardCopyOption.REPLACE_EXISTING);
        sourceCharset = configuration.getSourceCharset();
        cutter = new ASTCutter(parser, sourceCharset, outputFile);
        originalRootNode = cutter.commitChange();
        currentRootNode = originalRootNode;
    }

    @Override
    public BufferedReader getScratchReader() throws IOException {
        return Files.newBufferedReader(cutter.getScratchFile(), sourceCharset);
    }

    @Override
    public Parser getParser() {
        return parser;
    }

    @Override
    public Language getLanguage() {
        return language;
    }

    @Override
    public void tryCleanup() throws Exception {
        cutter.writeCleanedUpSource();
        if (invariant.checkIsSatisfied()) {
            try {
                currentRootNode = cutter.commitChange();
            } catch (ParseException ex) {
                return /* unsuccessful */;
            }
            throw new ContinueException();
        }
    }

    @Override
    public void tryRemoveNodes(Collection<Node> nodesToRemove) throws Exception {
        cutter.writeTrimmedSource(nodesToRemove);
        if (invariant.checkIsSatisfied()) {
            try {
                currentRootNode = cutter.commitChange();
            } catch (ParseException ex) {
                return /* unsuccessful */;
            }
            throw new ContinueException();
        }
    }

    @Override
    public void forceRemoveNodesAndExit(Collection<Node> nodesToRemove) throws Exception {
        cutter.writeTrimmedSource(nodesToRemove);
        cutter.commitChange();
        throw new ExitException();
    }

    @Override
    public Node getOriginalRoot() {
        return originalRootNode;
    }

    private int getCurrentFileSize() {
        return (int) cutter.getScratchFile().toFile().length();
    }

    private int getNodeCount(Node subtree) {
        int result = 1;
        for (int i = 0; i < subtree.jjtGetNumChildren(); ++i) {
            result += getNodeCount(subtree.jjtGetChild(i));
        }
        return result;
    }

    private void printStats(String when, int originalSize, int originalNodeCount) {
        int currentSize = getCurrentFileSize();
        int currentNodeCount = getNodeCount(currentRootNode);
        int pcSize = currentSize * 100 / originalSize;
        int pcNodes = currentNodeCount * 100 / originalNodeCount;
        System.out.println(when + ": size "
                + currentSize + " bytes (" + pcSize + "%), "
                + currentNodeCount + " nodes (" + pcNodes + "%)");
        System.out.flush();
    }

    public void runMinimization() throws Exception {
        strategy.initialize(this, originalRootNode);
        invariant.initialize(this, originalRootNode);

        final int originalSize = getCurrentFileSize();
        final int originalNodeCount = getNodeCount(originalRootNode);
        System.out.println("Original file: " + originalSize + " bytes, " + originalNodeCount + " nodes.");
        System.out.flush();

        try {
            tryCleanup();
        } catch (ContinueException ex) {
            printStats("After initial white-space cleanup", originalSize, originalNodeCount);
        }

        int passNumber = 0;
        boolean shouldContinue = true;
        while (shouldContinue) {
            passNumber += 1;
            boolean performCleanup = passNumber % 10 == 0;
            try {
                if (performCleanup) {
                    tryCleanup();
                } else {
                    strategy.performSinglePass(currentRootNode);
                    shouldContinue = false;
                }
            } catch (ContinueException ex) {
                shouldContinue = true;
            } catch (ExitException ex) {
                shouldContinue = false;
            }

            String cleanupLabel = performCleanup ? " (white-space cleanup)" : "";

            printStats("After pass #" + passNumber + cleanupLabel, originalSize, originalNodeCount);
        }

        try {
            tryCleanup();
        } catch (ContinueException ex) {
            printStats("After final white-space cleanup", originalSize, originalNodeCount);
        }

        cutter.rollbackChange(); // to the last committed state
        cutter.close();
    }
}

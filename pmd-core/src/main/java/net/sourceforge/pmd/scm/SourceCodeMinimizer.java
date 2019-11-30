/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.scm;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collection;

import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.scm.invariants.Invariant;
import net.sourceforge.pmd.scm.strategies.MinimizationStrategy;

public class SourceCodeMinimizer implements MinimizerOperations {
    private final Invariant invariant;
    private final MinimizationStrategy strategy;
    private final ASTCutter cutter;
    private final Node originalRootNode;

    private Node currentRootNode;

    public SourceCodeMinimizer(SCMConfiguration configuration) throws IOException {
        Language handler = configuration.getLanguageHandler();
        Parser parser = handler.getParser();
        invariant = configuration.getInvariantCheckerConfig().createChecker();
        strategy = configuration.getStrategyConfig().createStrategy();

        Path inputFile = Paths.get(configuration.getInputFileName());
        Path outputFile = Paths.get(configuration.getOutputFileName());
        Files.copy(inputFile, outputFile, StandardCopyOption.REPLACE_EXISTING);
        cutter = new ASTCutter(parser, configuration.getSourceCharset(), outputFile);
        originalRootNode = cutter.commitChange();
        currentRootNode = originalRootNode;
    }

    @Override
    public boolean testInvariant() throws Exception {
        return invariant.checkIsSatisfied();
    }

    @Override
    public void removeNodes(Collection<Node> nodesToRemove) throws Exception {
        cutter.writeTrimmedSource(nodesToRemove);
    }

    @Override
    public Node getOriginalRoot() {
        return originalRootNode;
    }

    public void runMinimization() throws Exception {
        strategy.initialize(originalRootNode);
        boolean shouldContinue = true;
        while (shouldContinue) {
            switch (strategy.performSinglePass(this, currentRootNode)) {
            case ROLLBACK_AND_EXIT:
                cutter.rollbackChange();
                shouldContinue = false;
                break;
            case COMMIT_AND_EXIT:
                cutter.commitChange();
                shouldContinue = false;
                break;
            case COMMIT_AND_CONTINUE:
                cutter.commitChange();
                break;
            default:
                // should be unreachable
                throw new IllegalStateException();
            }
        }
    }
}

/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.errorprone;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import net.sourceforge.pmd.lang.apex.ast.ASTApexFile;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.document.TextRange2d;
import net.sourceforge.pmd.reporting.RuleContext;

import io.github.apexdevtools.api.Issue;
import io.github.apexdevtools.api.IssueLocation;

/**
 * Reports semantic errors found by the Apex Language Server multifile analysis.
 */
public class ApexSemanticErrorsRule extends AbstractApexRule { // NOPMD - this rule intentionally runs once per file

    @Override
    public Object visit(ASTApexFile node, Object data) {
        RuleContext context = asCtx(data);
        Set<String> reportedIssues = new HashSet<>();

        for (Issue issue : node.getGlobalIssues()) {
            if (!Boolean.TRUE.equals(issue.isError()) || !belongsToFile(issue, node)) {
                continue;
            }

            FileLocation location = toFileLocation(node, issue.fileLocation());
            String deduplicationKey = deduplicationKey(issue, location);
            if (!reportedIssues.add(deduplicationKey)) {
                continue;
            }

            Node reportNode = findSmallestContainingNode(node, location.toRange2d());
            context.addViolationWithPosition(reportNode, node.getAstInfo(), location, "{0}", issue.message());
        }

        return data;
    }

    private static boolean belongsToFile(Issue issue, ASTApexFile node) {
        String issuePath = issue.filePath();
        String currentPath = node.getTextDocument().getFileId().getAbsolutePath();
        if (issuePath == null || currentPath == null) {
            return false;
        }
        if (issuePath.equals(currentPath)) {
            return true;
        }

        try {
            Path normalizedIssuePath = Paths.get(issuePath).toAbsolutePath().normalize();
            Path normalizedCurrentPath = Paths.get(currentPath).toAbsolutePath().normalize();
            return normalizedIssuePath.equals(normalizedCurrentPath);
        } catch (InvalidPathException ignored) {
            return false;
        }
    }

    private static FileLocation toFileLocation(ASTApexFile node, IssueLocation issueLocation) {
        int startLine = issueLocation.startLineNumber();
        int startColumn = issueLocation.startCharOffset() + 1;
        int endLine = issueLocation.endLineNumber();
        int endColumn = issueLocation.endCharOffset() + 1;

        // Apex-LS ranges use an inclusive end offset. PMD ranges use an exclusive end column.
        if (startLine != endLine || startColumn != endColumn) {
            endColumn++;
        }

        try {
            TextRange2d range = TextRange2d.range2d(startLine, startColumn, endLine, endColumn);
            return FileLocation.range(node.getTextDocument().getFileId(), range);
        } catch (IllegalArgumentException ignored) {
            return node.getReportLocation();
        }
    }

    private static Node findSmallestContainingNode(Node node, TextRange2d issueRange) {
        for (Node child : node.children()) {
            if (child.getReportLocation().toRange2d().contains(issueRange)) {
                return findSmallestContainingNode(child, issueRange);
            }
        }
        return node;
    }

    private static String deduplicationKey(Issue issue, FileLocation location) {
        return issue.filePath() + '\u0000'
            + location.getStartLine() + '\u0000'
            + location.getStartColumn() + '\u0000'
            + location.getEndLine() + '\u0000'
            + location.getEndColumn() + '\u0000'
            + issue.rule().name() + '\u0000'
            + issue.message();
    }
}

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.properties.StringProperty;

/**
 * Renderer to another HTML format.
 */
public class YAHTMLRenderer extends AbstractAccumulatingRenderer {

    public static final String NAME = "yahtml";
    // TODO 7.0.0 use PropertyDescriptor<Optional<File>> with a constraint that the file is an existing directory
    public static final StringProperty OUTPUT_DIR = new StringProperty("outputDir", "Output directory.", null, 0);

    private SortedMap<String, ReportNode> reportNodesByPackage = new TreeMap<>();

    public YAHTMLRenderer() {
        // YA = Yet Another?
        super(NAME, "Yet Another HTML format.");
        definePropertyDescriptor(OUTPUT_DIR);
    }

    @Override
    public String defaultFileExtension() {
        return "html";
    }

    private void addViolation(RuleViolation violation) {
        String packageName = violation.getPackageName();

        // report each part of the package name: e.g. net.sf.pmd.test will create nodes for
        // net, net.sf, net.sf.pmd, and net.sf.pmd.test
        int index = packageName.indexOf('.', 0);
        while (index > -1) {
            String currentPackage = packageName.substring(0, index);
            ReportNode reportNode = reportNodesByPackage.get(currentPackage);
            if (reportNode == null) {
                reportNode = new ReportNode(currentPackage);
                reportNodesByPackage.put(currentPackage, reportNode);
            }
            reportNode.incrementViolations();

            int oldIndex = index;
            index = packageName.indexOf('.', index + 1);
            if (index == -1 && oldIndex != packageName.length()) {
                index = packageName.length();
            }
        }

        // add one node per class collecting the actual violations
        String fqClassName = packageName + "." + violation.getClassName();
        ReportNode classNode = reportNodesByPackage.get(fqClassName);
        if (classNode == null) {
            classNode = new ReportNode(packageName, violation.getClassName());
            reportNodesByPackage.put(fqClassName, classNode);
        }
        classNode.addRuleViolation(violation);

        // count the overall violations in the root node
        ReportNode rootNode = reportNodesByPackage.get(ReportNode.ROOT_NODE_NAME);
        if (rootNode == null) {
            rootNode = new ReportNode("Aggregate");
            reportNodesByPackage.put(ReportNode.ROOT_NODE_NAME, rootNode);
        }
        rootNode.incrementViolations();
    }

    @Override
    public void end() throws IOException {
        String outputDir = getProperty(OUTPUT_DIR);

        Iterator<RuleViolation> violations = report.iterator();
        while (violations.hasNext()) {
            addViolation(violations.next());
        }

        renderIndex(outputDir);
        renderClasses(outputDir);

        writer.write("<h3 align=\"center\">The HTML files are located "
                + (outputDir == null ? "above the project directory" : "in '" + outputDir + '\'') + ".</h3>" + PMD.EOL);
    }

    private void renderIndex(String outputDir) throws IOException {
        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(new File(outputDir, "index.html").toPath(), StandardCharsets.UTF_8))) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("    <head>");
            out.println("        <meta charset=\"UTF-8\">");
            out.println("        <title>PMD</title>");
            out.println("    </head>");
            out.println("    <body>");
            out.println("    <h2>Package View</h2>");
            out.println("    <table border=\"1\" align=\"center\" cellspacing=\"0\" cellpadding=\"3\">");
            out.println("        <tr><th>Package</th><th>Class</th><th>#</th></tr>");

            for (ReportNode node : reportNodesByPackage.values()) {
                out.print("        <tr><td><b>");
                out.print(node.getPackageName());
                out.print("</b></td> <td>");
                if (node.hasViolations()) {
                    out.print("<a href=\"");
                    out.print(node.getClassName());
                    out.print(".html");
                    out.print("\">");
                    out.print(node.getClassName());
                    out.print("</a>");
                } else {
                    out.print(node.getClassName());
                }
                out.print("</td> <td>");
                out.print(node.getViolationCount());
                out.print("</td></tr>");
                out.println();
            }

            out.println("    </table>");
            out.println("    </body>");
            out.println("</html>");
        }
    }

    private void renderClasses(String outputDir) throws IOException {
        for (ReportNode node : reportNodesByPackage.values()) {
            if (node.hasViolations()) {
                try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(new File(outputDir, node.getClassName() + ".html").toPath(), StandardCharsets.UTF_8))) {
                    out.println("<!DOCTYPE html>");
                    out.println("<html>");
                    out.println("    <head>");
                    out.println("        <meta charset=\"UTF-8\">");
                    out.print("        <title>PMD - ");
                    out.print(node.getClassName());
                    out.println("</title>");
                    out.println("    </head>");
                    out.println("    <body>");
                    out.println("        <h2>Class View</h2>");
                    out.print("        <h3 align=\"center\">Class: ");
                    out.print(node.getClassName());
                    out.println("</h3>");
                    out.println("        <table border=\"\" align=\"center\" cellspacing=\"0\" cellpadding=\"3\">");
                    out.println("        <tr><th>Method</th><th>Violation</th></tr>");
                    for (RuleViolation violation : node.getViolations()) {
                        out.print("        <tr><td>");
                        out.print(violation.getMethodName());
                        out.print("</td><td>");
                        out.print("<table border=\"0\">");

                        out.print(renderViolationRow("Rule:", violation.getRule().getName()));
                        out.print(renderViolationRow("Description:", violation.getDescription()));

                        if (StringUtils.isNotBlank(violation.getVariableName())) {
                            out.print(renderViolationRow("Variable:", violation.getVariableName()));
                        }

                        out.print(renderViolationRow("Line:", violation.getEndLine() > 0
                                ? violation.getBeginLine() + " and " + violation.getEndLine()
                                : String.valueOf(violation.getBeginLine())));

                        out.print("</table>");

                        out.print("</td></tr>");
                        out.println();
                    }
                    out.println("        </table>");
                    out.println("    </body>");
                    out.println("</html>");
                }
            }
        }
    }

    private String renderViolationRow(String name, String value) {
        StringBuilder row = new StringBuilder(40 + name.length() + value.length());
        row.append("<tr><td><b>")
            .append(name)
            .append("</b></td>")
            .append("<td>")
            .append(value)
            .append("</td></tr>");
        return row.toString();
    }

    private static class ReportNode {
        // deliberately starts with a space, so that it is sorted before the packages
        private static final String ROOT_NODE_NAME = " <root> ";

        private final String packageName;
        private final String className;
        private int violationCount;
        private final List<RuleViolation> violations = new LinkedList<>();

        ReportNode(String packageName) {
            this.packageName = packageName;
            this.className = "-";
        }

        ReportNode(String packageName, String className) {
            this.packageName = packageName;
            this.className = className;
        }

        public void incrementViolations() {
            violationCount++;
        }

        public void addRuleViolation(RuleViolation violation) {
            violations.add(violation);
        }

        public String getPackageName() {
            return packageName;
        }

        public String getClassName() {
            return className;
        }

        public int getViolationCount() {
            return violationCount + violations.size();
        }

        public List<RuleViolation> getViolations() {
            return violations;
        }

        public boolean hasViolations() {
            return !violations.isEmpty();
        }

        @Override
        public String toString() {
            return "ReportNode[packageName=" + packageName
                + ",className=" + className
                + ",violationCount=" + violationCount
                + ",violations=" + violations.size()
                + "]";
        }
    }
}

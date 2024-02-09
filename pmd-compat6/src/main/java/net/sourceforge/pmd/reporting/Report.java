/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.reporting;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.lang.document.FileId;

public class Report extends net.sourceforge.pmd.Report {

    public static class ConfigurationError extends net.sourceforge.pmd.Report.ConfigurationError {
        public ConfigurationError(Rule theRule, String theIssue) {
            super(theRule, theIssue);
        }
    }

    public static class ProcessingError extends net.sourceforge.pmd.Report.ProcessingError {
        public ProcessingError(Throwable error, FileId file) {
            super(error, file);
        }
    }

    public static class SuppressedViolation extends net.sourceforge.pmd.Report.SuppressedViolation {
        private final RuleViolation rv;

        public SuppressedViolation(RuleViolation rv, ViolationSuppressor suppressor, String userMessage) {
            super(rv, suppressor, userMessage);
            this.rv = rv;
        }

        @Override
        public net.sourceforge.pmd.reporting.RuleViolation getRuleViolation() {
            return rv;
        }
    }

    public static final class GlobalReportBuilderListener extends net.sourceforge.pmd.Report.GlobalReportBuilderListener {
    }

    public static final class ReportBuilderListener extends net.sourceforge.pmd.Report.ReportBuilderListener {
    }
}

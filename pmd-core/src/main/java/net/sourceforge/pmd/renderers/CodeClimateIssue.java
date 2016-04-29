/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.PMD;

/**
 * Structure for the Code Climate Issue spec (https://github.com/codeclimate/spec/blob/master/SPEC.md#issues)
 */
public class CodeClimateIssue {
    public final String type = "issue";
    public String check_name;
    public String description;
    public Content content;
    public String[] categories;
    public Location location;
    public String severity;
    public int remediation_points;

    /**
     * Location structure
     */
    public static class Location {
        public String path;
        public Lines lines;

        private class Lines {
            public int begin;
            public int end;
        }

        public Location(String path, int beginLine, int endLine) {
        	this.path = path;
            this.lines = new Lines();
            lines.begin = beginLine;
            lines.end = endLine;
        }
    }

    /**
     * Content structure
     */
    public static class Content {
        public String body;

        /**
         * Strip out all newlines from the body
         * @param {String} body The text to compose the content from
         */
        public Content(String body) {
            this.body = body.replace(PMD.EOL, " ");
        }
    }
}

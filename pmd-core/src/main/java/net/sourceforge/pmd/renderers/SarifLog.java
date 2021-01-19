/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SarifLog {

    /**
     * The URI of the JSON schema corresponding to the version.
     */
    private String $schema = "https://raw.githubusercontent.com/oasis-tcs/sarif-spec/master/Schemata/sarif-schema-2.1.0.json";

    /**
     * The SARIF format version of this log file.
     */
    private String version = "2.1.0";

    /**
     * The set of runs contained in this log file.
     */
    private List<Run> runs;

    /**
     * A location within a programming artifact.
     */
    @Data
    @Accessors(chain = true)
    public static class Location {

        /**
         * Value that distinguishes this location from all other locations within a single result object.
         */
        private Integer id;

        /**
         * Identifies the artifact and region.
         */
        private PhysicalLocation physicalLocation;
    }

    /**
     * Specifies the location of an artifact.
     */
    @Data
    public static class ArtifactLocation {

        /**
         * A string containing a valid relative or absolute URI.
         */
        private String uri;

        /**
         * A string which indirectly specifies the absolute URI with respect to which a relative URI in the "uri" property
         * is interpreted.
         */
        private String uriBaseId;

        /**
         * The index within the run artifacts array of the artifact object associated with the artifact location.
         */
        private Integer index;
    }


    /**
     * A physical location relevant to a result. Specifies a reference to a programming artifact together with a range
     * of bytes or characters within that artifact.
     */
    @Data
    @Accessors(chain = true)
    public static class PhysicalLocation {

        /**
         * The location of the artifact.
         */
        private ArtifactLocation artifactLocation;

        /**
         * Specifies a portion of the artifact.
         */
        private Region region;
    }

    /**
     * Key/value pairs that provide additional information about the object.
     */
    @Data
    public static class PropertyBag {

        /**
         * The name of the rule set.
         */
        private String ruleset;

        /**
         * The pmd priority of the rule.
         */
        private Integer priority;
    }

    /**
     * A region within an artifact where a result was detected.
     */
    @Data
    @Accessors(chain = true)
    public static class Region {

        /**
         * The line number of the first character in the region.
         */
        private Integer startLine;

        /**
         * The column number of the first character in the region.
         */
        private Integer startColumn;

        /**
         * The line number of the last character in the region.
         */
        private Integer endLine;

        /**
         * The column number of the character following the end of the region.
         */
        private Integer endColumn;
    }

    /**
     * A result produced by an analysis tool.
     */
    @Data
    @Accessors(chain = true)
    public static class Result {

        /**
         * The stable, unique identifier of the rule, if any, to which this result is relevant.
         */
        private String ruleId;

        /**
         * The index link the rule, if any, to which this result is relevant.
         */
        private Integer ruleIndex;

        /**
         * A message that describes the result. The first sentence of the message only will be displayed when visible
         * space is limited.
         */
        private Message message;

        /**
         * The set of locations where the result was detected. Specify only one location unless the problem indicated by
         * the result can only be corrected by making a change at every specified location.
         */
        private List<Location> locations;

        /**
         * Key/value pairs that provide additional information about the address.
         */
        private PropertyBag properties;
    }

    /**
     * Encapsulates a message intended to be read by the end user.
     */
    @Data
    @Accessors(chain = true)
    public static class Message {

        /**
         * A plain text message string.
         */
        private String text;

        /**
         * A Markdown message string.
         */
        private String markdown;

        /**
         * The identifier for this message.
         */
        private String id;
    }

    /**
     * Describes a single run of an analysis tool, and contains the reported output of that run.
     */
    @Data
    @Accessors(chain = true)
    public static class Run {

        /**
         * Information about the tool or tool pipeline that generated the results in this run. A run can only contain
         * results produced by a single tool or tool pipeline. A run can aggregate results from multiple log files, as long
         * as context around the tool run (tool command-line arguments and the like) is identical for all aggregated files.
         */
        private Tool tool;

        /**
         * The set of results contained in an SARIF log. The results array can be omitted when a run is solely exporting
         * rules metadata. It must be present (but may be empty) if a log file represents an actual scan.
         */
        private List<Result> results;
    }

    /**
     * The analysis tool that was run.
     */
    @Data
    @Accessors(chain = true)
    public static class Tool {

        /**
         * The analysis tool that was run.
         */
        private Component driver;
    }

    /**
     * A component, such as a plug-in or the driver, of the analysis tool that was run.
     */
    @Data
    public static class Component {
        /**
         * The name of the tool component.
         */
        private String name;

        /**
         * The tool component version, in whatever format the component natively provides.
         */
        private String version;

        /**
         * The absolute URI at which information about this version of the tool component can be found.
         */
        private String informationUri;

        /**
         * An array of reportingDescriptor objects relevant to the analysis performed by the tool component.
         */
        private List<ReportingDescriptor> rules;
    }

    /**
     * Metadata that describes a specific report produced by the tool, as part of the analysis it provides or its runtime
     * reporting.
     */
    @Data
    @Accessors(chain = true)
    public static class ReportingDescriptor {

        /**
         * A stable, opaque identifier for the report.
         */
        private String id;

        /**
         * A report identifier that is understandable to an end user.
         */
        private String name;

        /**
         * A concise description of the report. Should be a single sentence that is understandable when visible space is
         * limited to a single line of text.
         */
        private MultiformatMessage shortDescription;

        /**
         * A description of the report. Should, as far as possible, provide details sufficient to enable resolution of any
         * problem indicated by the result.
         */
        private MultiformatMessage fullDescription;

        /**
         * A set of name/value pairs with arbitrary names. Each value is a multiformatMessageString object, which holds
         * message strings in plain text and (optionally) Markdown format. The strings can include placeholders, which can
         * be used to construct a message in combination with an arbitrary number of additional string arguments.
         */
        private MultiformatMessage messageStrings;

        /**
         * A URI where the primary documentation for the report can be found.
         */
        private String helpUri;

        /**
         * Provides the primary documentation for the report, useful when there is no online documentation.
         */
        private MultiformatMessage help;

        /**
         * Key/value pairs that provide additional information about the report.
         */
        private PropertyBag properties;
    }

    /**
     * A message string or message format string rendered in multiple formats.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MultiformatMessage {

        /**
         * A plain text message string or format string.
         */
        private String text;

        /**
         * A Markdown message string or format string.
         */
        private String markdown;

        public MultiformatMessage(String text) {
            this.text = text;
        }
    }
}

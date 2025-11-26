/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers.internal.sarif;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.google.gson.annotations.SerializedName;

/**
 * This class represents a SARIF report that can be serialized with Gson into Json.
 *
 * <p>To create a new log, use the builders, e.g. {@link SarifLog#builder()}.
 * <p>This class tries to use the same names as in the official specification for
 * Sarif.
 *
 * @see <a href="https://docs.oasis-open.org/sarif/sarif/v2.1.0/sarif-v2.1.0.html">Static Analysis Results Interchange Format (SARIF) Version 2.1.0</a>
 */
public final class SarifLog {
    @SerializedName("$schema")
    private String schema;
    private String version;
    private List<Run> runs;

    /**
     * A location within a programming artifact.
     */
    public static final class Location {
        private Integer id;
        private PhysicalLocation physicalLocation;

        private Location(final Integer id, final PhysicalLocation physicalLocation) {
            this.id = id;
            this.physicalLocation = physicalLocation;
        }

        public static final class LocationBuilder {
            private Integer id;
            private PhysicalLocation physicalLocation;

            private LocationBuilder() {
                // make default ctor private; factory method Location#builder() should be used.
            }

            /**
             * Value that distinguishes this location from all other locations within a single result object.
             *
             * @return {@code this}.
             */
            public SarifLog.Location.LocationBuilder id(final Integer id) {
                this.id = id;
                return this;
            }

            /**
             * Identifies the artifact and region.
             *
             * @return {@code this}.
             */
            public SarifLog.Location.LocationBuilder physicalLocation(final PhysicalLocation physicalLocation) {
                this.physicalLocation = physicalLocation;
                return this;
            }

            public SarifLog.Location build() {
                return new SarifLog.Location(this.id, this.physicalLocation);
            }

            @Override
            public String toString() {
                return "SarifLog.Location.LocationBuilder(id=" + this.id + ", physicalLocation=" + this.physicalLocation + ")";
            }
        }

        public static SarifLog.Location.LocationBuilder builder() {
            return new SarifLog.Location.LocationBuilder();
        }

        /**
         * Value that distinguishes this location from all other locations within a single result object.
         */
        public Integer getId() {
            return this.id;
        }

        /**
         * Identifies the artifact and region.
         */
        public PhysicalLocation getPhysicalLocation() {
            return this.physicalLocation;
        }

        /**
         * Value that distinguishes this location from all other locations within a single result object.
         *
         * @return {@code this}.
         */
        public SarifLog.Location setId(final Integer id) {
            this.id = id;
            return this;
        }

        /**
         * Identifies the artifact and region.
         *
         * @return {@code this}.
         */
        public SarifLog.Location setPhysicalLocation(final PhysicalLocation physicalLocation) {
            this.physicalLocation = physicalLocation;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Location location = (Location) o;
            return Objects.equals(id, location.id) && Objects.equals(physicalLocation, location.physicalLocation);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, physicalLocation);
        }

        @Override
        public String toString() {
            return "SarifLog.Location(id=" + this.getId() + ", physicalLocation=" + this.getPhysicalLocation() + ")";
        }
    }


    /**
     * Specifies the location of an artifact.
     */
    public static final class ArtifactLocation {
        private String uri;
        private String uriBaseId;
        private Integer index;

        private ArtifactLocation(final String uri, final String uriBaseId, final Integer index) {
            this.uri = uri;
            this.uriBaseId = uriBaseId;
            this.index = index;
        }

        public static final class ArtifactLocationBuilder {
            private String uri;
            private String uriBaseId;
            private Integer index;

            private ArtifactLocationBuilder() {
                // make default ctor private. Use factory method ArtifactLocation#builder() instead.
            }

            /**
             * A string containing a valid relative or absolute URI.
             *
             * @return {@code this}.
             */
            public SarifLog.ArtifactLocation.ArtifactLocationBuilder uri(final String uri) {
                this.uri = uri;
                return this;
            }

            /**
             * A string which indirectly specifies the absolute URI with respect to which a relative URI in the "uri" property
             * is interpreted.
             *
             * @return {@code this}.
             */
            public SarifLog.ArtifactLocation.ArtifactLocationBuilder uriBaseId(final String uriBaseId) {
                this.uriBaseId = uriBaseId;
                return this;
            }

            /**
             * The index within the run artifacts array of the artifact object associated with the artifact location.
             *
             * @return {@code this}.
             */
            public SarifLog.ArtifactLocation.ArtifactLocationBuilder index(final Integer index) {
                this.index = index;
                return this;
            }

            public SarifLog.ArtifactLocation build() {
                return new SarifLog.ArtifactLocation(this.uri, this.uriBaseId, this.index);
            }

            @Override
            public String toString() {
                return "SarifLog.ArtifactLocation.ArtifactLocationBuilder(uri=" + this.uri + ", uriBaseId=" + this.uriBaseId + ", index=" + this.index + ")";
            }
        }

        public static SarifLog.ArtifactLocation.ArtifactLocationBuilder builder() {
            return new SarifLog.ArtifactLocation.ArtifactLocationBuilder();
        }

        /**
         * A string containing a valid relative or absolute URI.
         */
        public String getUri() {
            return this.uri;
        }

        /**
         * A string which indirectly specifies the absolute URI with respect to which a relative URI in the "uri" property
         * is interpreted.
         */
        public String getUriBaseId() {
            return this.uriBaseId;
        }

        /**
         * The index within the run artifacts array of the artifact object associated with the artifact location.
         */
        public Integer getIndex() {
            return this.index;
        }

        /**
         * A string containing a valid relative or absolute URI.
         *
         * @return {@code this}.
         */
        public SarifLog.ArtifactLocation setUri(final String uri) {
            this.uri = uri;
            return this;
        }

        /**
         * A string which indirectly specifies the absolute URI with respect to which a relative URI in the "uri" property
         * is interpreted.
         *
         * @return {@code this}.
         */
        public SarifLog.ArtifactLocation setUriBaseId(final String uriBaseId) {
            this.uriBaseId = uriBaseId;
            return this;
        }

        /**
         * The index within the run artifacts array of the artifact object associated with the artifact location.
         *
         * @return {@code this}.
         */
        public SarifLog.ArtifactLocation setIndex(final Integer index) {
            this.index = index;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ArtifactLocation that = (ArtifactLocation) o;
            return Objects.equals(uri, that.uri) && Objects.equals(uriBaseId, that.uriBaseId) && Objects.equals(index, that.index);
        }

        @Override
        public int hashCode() {
            return Objects.hash(uri, uriBaseId, index);
        }

        @Override
        public String toString() {
            return "SarifLog.ArtifactLocation(uri=" + this.getUri() + ", uriBaseId=" + this.getUriBaseId() + ", index=" + this.getIndex() + ")";
        }
    }


    /**
     * A physical location relevant to a result. Specifies a reference to a programming artifact together with a range
     * of bytes or characters within that artifact.
     */
    public static final class PhysicalLocation {
        private ArtifactLocation artifactLocation;
        private Region region;

        private PhysicalLocation(final ArtifactLocation artifactLocation, final Region region) {
            this.artifactLocation = artifactLocation;
            this.region = region;
        }


        public static final class PhysicalLocationBuilder {
            private ArtifactLocation artifactLocation;
            private Region region;

            private PhysicalLocationBuilder() {
                // make default ctor private; use factory method PhysicalLocation#builder() instead.
            }

            /**
             * The location of the artifact.
             *
             * @return {@code this}.
             */
            public SarifLog.PhysicalLocation.PhysicalLocationBuilder artifactLocation(final ArtifactLocation artifactLocation) {
                this.artifactLocation = artifactLocation;
                return this;
            }

            /**
             * Specifies a portion of the artifact.
             *
             * @return {@code this}.
             */
            public SarifLog.PhysicalLocation.PhysicalLocationBuilder region(final Region region) {
                this.region = region;
                return this;
            }

            public SarifLog.PhysicalLocation build() {
                return new SarifLog.PhysicalLocation(this.artifactLocation, this.region);
            }

            @Override
            public String toString() {
                return "SarifLog.PhysicalLocation.PhysicalLocationBuilder(artifactLocation=" + this.artifactLocation + ", region=" + this.region + ")";
            }
        }

        public static SarifLog.PhysicalLocation.PhysicalLocationBuilder builder() {
            return new SarifLog.PhysicalLocation.PhysicalLocationBuilder();
        }

        /**
         * The location of the artifact.
         */
        public ArtifactLocation getArtifactLocation() {
            return this.artifactLocation;
        }

        /**
         * Specifies a portion of the artifact.
         */
        public Region getRegion() {
            return this.region;
        }

        /**
         * The location of the artifact.
         *
         * @return {@code this}.
         */
        public SarifLog.PhysicalLocation setArtifactLocation(final ArtifactLocation artifactLocation) {
            this.artifactLocation = artifactLocation;
            return this;
        }

        /**
         * Specifies a portion of the artifact.
         *
         * @return {@code this}.
         */
        public SarifLog.PhysicalLocation setRegion(final Region region) {
            this.region = region;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            PhysicalLocation that = (PhysicalLocation) o;
            return Objects.equals(artifactLocation, that.artifactLocation) && Objects.equals(region, that.region);
        }

        @Override
        public int hashCode() {
            return Objects.hash(artifactLocation, region);
        }

        @Override
        public String toString() {
            return "SarifLog.PhysicalLocation(artifactLocation=" + this.getArtifactLocation() + ", region=" + this.getRegion() + ")";
        }
    }

    /**
     * Key/value pairs that provide additional information about the object.
     */
    public static final class PropertyBag {
        private String ruleset;
        private Integer priority;
        private Set<String> tags;

        private PropertyBag(final String ruleset, final Integer priority, final Set<String> tags) {
            this.ruleset = ruleset;
            this.priority = priority;
            this.tags = tags;
        }


        public static final class PropertyBagBuilder {
            private String ruleset;
            private Integer priority;
            private Set<String> tags;

            private PropertyBagBuilder() {
                // make default ctor private; use factoy method PropertyBag#builder() instead.
            }

            /**
             * The name of the rule set.
             *
             * @return {@code this}.
             */
            public SarifLog.PropertyBag.PropertyBagBuilder ruleset(final String ruleset) {
                this.ruleset = ruleset;
                return this;
            }

            /**
             * The pmd priority of the rule.
             *
             * @return {@code this}.
             */
            public SarifLog.PropertyBag.PropertyBagBuilder priority(final Integer priority) {
                this.priority = priority;
                return this;
            }

            /**
             * A set of distinct strings that provide additional information. This is SARIF 2.1.0 Schema.
             *
             * @return {@code this}.
             */
            public SarifLog.PropertyBag.PropertyBagBuilder tags(final Set<String> tags) {
                this.tags = tags;
                return this;
            }

            public SarifLog.PropertyBag build() {
                return new SarifLog.PropertyBag(this.ruleset, this.priority, this.tags);
            }

            @Override
            public String toString() {
                return "SarifLog.PropertyBag.PropertyBagBuilder(ruleset=" + this.ruleset + ", priority=" + this.priority + ", tags=" + this.tags + ")";
            }
        }

        public static SarifLog.PropertyBag.PropertyBagBuilder builder() {
            return new SarifLog.PropertyBag.PropertyBagBuilder();
        }

        /**
         * The name of the rule set.
         */
        public String getRuleset() {
            return this.ruleset;
        }

        /**
         * The pmd priority of the rule.
         */
        public Integer getPriority() {
            return this.priority;
        }

        /**
         * A set of distinct strings that provide additional information. This is SARIF 2.1.0 Schema.
         */
        public Set<String> getTags() {
            return this.tags;
        }

        /**
         * The name of the rule set.
         *
         * @return {@code this}.
         */
        public SarifLog.PropertyBag setRuleset(final String ruleset) {
            this.ruleset = ruleset;
            return this;
        }

        /**
         * The pmd priority of the rule.
         *
         * @return {@code this}.
         */
        public SarifLog.PropertyBag setPriority(final Integer priority) {
            this.priority = priority;
            return this;
        }

        /**
         * The set of distinct strings that provide additional information. This is SARIF 2.1.0 Schema.
         *
         * @return {@code this}.
         */
        public SarifLog.PropertyBag setTags(final Set<String> tags) {
            this.tags = tags;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            PropertyBag that = (PropertyBag) o;
            return Objects.equals(ruleset, that.ruleset) && Objects.equals(priority, that.priority) && Objects.equals(tags, that.tags);
        }

        @Override
        public int hashCode() {
            return Objects.hash(ruleset, priority, tags);
        }

        @Override
        public String toString() {
            return "SarifLog.PropertyBag(ruleset=" + this.getRuleset() + ", priority=" + this.getPriority() + ", tags=" + this.getTags() + ")";
        }
    }


    /**
     * A region within an artifact where a result was detected.
     */
    public static final class Region {
        private Integer startLine;
        private Integer startColumn;
        private Integer endLine;
        private Integer endColumn;

        private Region(final Integer startLine, final Integer startColumn, final Integer endLine, final Integer endColumn) {
            this.startLine = startLine;
            this.startColumn = startColumn;
            this.endLine = endLine;
            this.endColumn = endColumn;
        }


        public static final class RegionBuilder {
            private Integer startLine;
            private Integer startColumn;
            private Integer endLine;
            private Integer endColumn;

            private RegionBuilder() {
                // make default ctor private; use factory method Region#builder() instead.
            }

            /**
             * The line number of the first character in the region.
             *
             * @return {@code this}.
             */
            public SarifLog.Region.RegionBuilder startLine(final Integer startLine) {
                this.startLine = startLine;
                return this;
            }

            /**
             * The column number of the first character in the region.
             *
             * @return {@code this}.
             */
            public SarifLog.Region.RegionBuilder startColumn(final Integer startColumn) {
                this.startColumn = startColumn;
                return this;
            }

            /**
             * The line number of the last character in the region.
             *
             * @return {@code this}.
             */
            public SarifLog.Region.RegionBuilder endLine(final Integer endLine) {
                this.endLine = endLine;
                return this;
            }

            /**
             * The column number of the character following the end of the region.
             *
             * @return {@code this}.
             */
            public SarifLog.Region.RegionBuilder endColumn(final Integer endColumn) {
                this.endColumn = endColumn;
                return this;
            }

            public SarifLog.Region build() {
                return new SarifLog.Region(this.startLine, this.startColumn, this.endLine, this.endColumn);
            }

            @Override
            public String toString() {
                return "SarifLog.Region.RegionBuilder(startLine=" + this.startLine + ", startColumn=" + this.startColumn + ", endLine=" + this.endLine + ", endColumn=" + this.endColumn + ")";
            }
        }

        public static SarifLog.Region.RegionBuilder builder() {
            return new SarifLog.Region.RegionBuilder();
        }

        /**
         * The line number of the first character in the region.
         */
        public Integer getStartLine() {
            return this.startLine;
        }

        /**
         * The column number of the first character in the region.
         */
        public Integer getStartColumn() {
            return this.startColumn;
        }

        /**
         * The line number of the last character in the region.
         */
        public Integer getEndLine() {
            return this.endLine;
        }

        /**
         * The column number of the character following the end of the region.
         */
        public Integer getEndColumn() {
            return this.endColumn;
        }

        /**
         * The line number of the first character in the region.
         *
         * @return {@code this}.
         */
        public SarifLog.Region setStartLine(final Integer startLine) {
            this.startLine = startLine;
            return this;
        }

        /**
         * The column number of the first character in the region.
         *
         * @return {@code this}.
         */
        public SarifLog.Region setStartColumn(final Integer startColumn) {
            this.startColumn = startColumn;
            return this;
        }

        /**
         * The line number of the last character in the region.
         *
         * @return {@code this}.
         */
        public SarifLog.Region setEndLine(final Integer endLine) {
            this.endLine = endLine;
            return this;
        }

        /**
         * The column number of the character following the end of the region.
         *
         * @return {@code this}.
         */
        public SarifLog.Region setEndColumn(final Integer endColumn) {
            this.endColumn = endColumn;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Region region = (Region) o;
            return Objects.equals(startLine, region.startLine) && Objects.equals(startColumn, region.startColumn) && Objects.equals(endLine, region.endLine) && Objects.equals(endColumn, region.endColumn);
        }

        @Override
        public int hashCode() {
            return Objects.hash(startLine, startColumn, endLine, endColumn);
        }

        @Override
        public String toString() {
            return "SarifLog.Region(startLine=" + this.getStartLine() + ", startColumn=" + this.getStartColumn() + ", endLine=" + this.getEndLine() + ", endColumn=" + this.getEndColumn() + ")";
        }
    }


    /**
     * A result produced by an analysis tool.
     */
    public static final class Result {
        private String ruleId;
        private Integer ruleIndex;
        private Message message;
        private String level;
        private List<Location> locations;
        private PropertyBag properties;

        private Result(final String ruleId, final Integer ruleIndex, final Message message, final String level, final List<Location> locations, final PropertyBag properties) {
            this.ruleId = ruleId;
            this.ruleIndex = ruleIndex;
            this.message = message;
            this.level = level;
            this.locations = locations;
            this.properties = properties;
        }


        public static final class ResultBuilder {
            private String ruleId;
            private Integer ruleIndex;
            private Message message;
            private String level;
            private List<Location> locations;
            private PropertyBag properties;

            private ResultBuilder() {
                // make default ctor private; use factory method Result#builder() instead.
            }

            /**
             * The stable, unique identifier of the rule, if any, to which this result is relevant.
             *
             * @return {@code this}.
             */
            public SarifLog.Result.ResultBuilder ruleId(final String ruleId) {
                this.ruleId = ruleId;
                return this;
            }

            /**
             * The index link the rule, if any, to which this result is relevant.
             *
             * @return {@code this}.
             */
            public SarifLog.Result.ResultBuilder ruleIndex(final Integer ruleIndex) {
                this.ruleIndex = ruleIndex;
                return this;
            }

            /**
             * A message that describes the result. The first sentence of the message only will be displayed when visible
             * space is limited.
             *
             * @return {@code this}.
             */
            public SarifLog.Result.ResultBuilder message(final Message message) {
                this.message = message;
                return this;
            }

            /**
             * Specifies the severity level of the result. It is derived from PMD's defined rule priorities (1,2 = error, 3 = warning, 4,5 = note).
             *
             * @return {@code this}.
             * @see net.sourceforge.pmd.lang.rule.RulePriority
             */
            public SarifLog.Result.ResultBuilder level(final String level) {
                this.level = level;
                return this;
            }

            /**
             * The set of locations where the result was detected. Specify only one location unless the problem indicated by
             * the result can only be corrected by making a change at every specified location.
             *
             * @return {@code this}.
             */
            public SarifLog.Result.ResultBuilder locations(final List<Location> locations) {
                this.locations = locations;
                return this;
            }

            /**
             * Key/value pairs that provide additional information about the address.
             *
             * @return {@code this}.
             */
            public SarifLog.Result.ResultBuilder properties(final PropertyBag properties) {
                this.properties = properties;
                return this;
            }

            public SarifLog.Result build() {
                return new SarifLog.Result(this.ruleId, this.ruleIndex, this.message, this.level, this.locations, this.properties);
            }

            @Override
            public String toString() {
                return "SarifLog.Result.ResultBuilder(ruleId=" + this.ruleId + ", ruleIndex=" + this.ruleIndex + ", message=" + this.message + ", level=" + this.level + ", locations=" + this.locations + ", properties=" + this.properties + ")";
            }
        }

        public static SarifLog.Result.ResultBuilder builder() {
            return new SarifLog.Result.ResultBuilder();
        }

        /**
         * The stable, unique identifier of the rule, if any, to which this result is relevant.
         */
        public String getRuleId() {
            return this.ruleId;
        }

        /**
         * The index link the rule, if any, to which this result is relevant.
         */
        public Integer getRuleIndex() {
            return this.ruleIndex;
        }

        /**
         * A message that describes the result. The first sentence of the message only will be displayed when visible
         * space is limited.
         */
        public Message getMessage() {
            return this.message;
        }

        /**
         * Specifies the severity level of the result. It is derived from PMD's defined rule priorities (1,2 = error, 3 = warning, 4,5 = note).
         *
         * @see net.sourceforge.pmd.lang.rule.RulePriority
         */
        public String getLevel() {
            return this.level;
        }

        /**
         * The set of locations where the result was detected. Specify only one location unless the problem indicated by
         * the result can only be corrected by making a change at every specified location.
         */
        public List<Location> getLocations() {
            return this.locations;
        }

        /**
         * Key/value pairs that provide additional information about the address.
         */
        public PropertyBag getProperties() {
            return this.properties;
        }

        /**
         * The stable, unique identifier of the rule, if any, to which this result is relevant.
         *
         * @return {@code this}.
         */
        public SarifLog.Result setRuleId(final String ruleId) {
            this.ruleId = ruleId;
            return this;
        }

        /**
         * The index link the rule, if any, to which this result is relevant.
         *
         * @return {@code this}.
         */
        public SarifLog.Result setRuleIndex(final Integer ruleIndex) {
            this.ruleIndex = ruleIndex;
            return this;
        }

        /**
         * A message that describes the result. The first sentence of the message only will be displayed when visible
         * space is limited.
         *
         * @return {@code this}.
         */
        public SarifLog.Result setMessage(final Message message) {
            this.message = message;
            return this;
        }

        /**
         * Specifies the severity level of the result. It is derived from PMD's defined rule priorities (1,2 = error, 3 = warning, 4,5 = note).
         *
         * @return {@code this}.
         * @see net.sourceforge.pmd.lang.rule.RulePriority
         */
        public SarifLog.Result setLevel(final String level) {
            this.level = level;
            return this;
        }

        /**
         * The set of locations where the result was detected. Specify only one location unless the problem indicated by
         * the result can only be corrected by making a change at every specified location.
         *
         * @return {@code this}.
         */
        public SarifLog.Result setLocations(final List<Location> locations) {
            this.locations = locations;
            return this;
        }

        /**
         * Key/value pairs that provide additional information about the address.
         *
         * @return {@code this}.
         */
        public SarifLog.Result setProperties(final PropertyBag properties) {
            this.properties = properties;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Result result = (Result) o;
            return Objects.equals(ruleId, result.ruleId) && Objects.equals(ruleIndex, result.ruleIndex) && Objects.equals(message, result.message) && Objects.equals(level, result.level) && Objects.equals(locations, result.locations) && Objects.equals(properties, result.properties);
        }

        @Override
        public int hashCode() {
            return Objects.hash(ruleId, ruleIndex, message, level, locations, properties);
        }

        @Override
        public String toString() {
            return "SarifLog.Result(ruleId=" + this.getRuleId() + ", ruleIndex=" + this.getRuleIndex() + ", message=" + this.getMessage() + ", level=" + this.getLevel() + ", locations=" + this.getLocations() + ", properties=" + this.getProperties() + ")";
        }
    }


    /**
     * Encapsulates a message intended to be read by the end user.
     */
    public static final class Message {
        private String text;
        private String markdown;
        private String id;

        private Message(final String text, final String markdown, final String id) {
            this.text = text;
            this.markdown = markdown;
            this.id = id;
        }


        public static final class MessageBuilder {
            private String text;
            private String markdown;
            private String id;

            private MessageBuilder() {
                // make default ctor private; use factory method Message#builder() instead.
            }

            /**
             * A plain text message string.
             *
             * @return {@code this}.
             */
            public SarifLog.Message.MessageBuilder text(final String text) {
                this.text = text;
                return this;
            }

            /**
             * A Markdown message string.
             *
             * @return {@code this}.
             */
            public SarifLog.Message.MessageBuilder markdown(final String markdown) {
                this.markdown = markdown;
                return this;
            }

            /**
             * The identifier for this message.
             *
             * @return {@code this}.
             */
            public SarifLog.Message.MessageBuilder id(final String id) {
                this.id = id;
                return this;
            }

            public SarifLog.Message build() {
                return new SarifLog.Message(this.text, this.markdown, this.id);
            }

            @Override
            public String toString() {
                return "SarifLog.Message.MessageBuilder(text=" + this.text + ", markdown=" + this.markdown + ", id=" + this.id + ")";
            }
        }

        public static SarifLog.Message.MessageBuilder builder() {
            return new SarifLog.Message.MessageBuilder();
        }

        /**
         * A plain text message string.
         */
        public String getText() {
            return this.text;
        }

        /**
         * A Markdown message string.
         */
        public String getMarkdown() {
            return this.markdown;
        }

        /**
         * The identifier for this message.
         */
        public String getId() {
            return this.id;
        }

        /**
         * A plain text message string.
         *
         * @return {@code this}.
         */
        public SarifLog.Message setText(final String text) {
            this.text = text;
            return this;
        }

        /**
         * A Markdown message string.
         *
         * @return {@code this}.
         */
        public SarifLog.Message setMarkdown(final String markdown) {
            this.markdown = markdown;
            return this;
        }

        /**
         * The identifier for this message.
         *
         * @return {@code this}.
         */
        public SarifLog.Message setId(final String id) {
            this.id = id;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Message message = (Message) o;
            return Objects.equals(text, message.text) && Objects.equals(markdown, message.markdown) && Objects.equals(id, message.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(text, markdown, id);
        }

        @Override
        public String toString() {
            return "SarifLog.Message(text=" + this.getText() + ", markdown=" + this.getMarkdown() + ", id=" + this.getId() + ")";
        }
    }


    /**
     * Describes a single run of an analysis tool, and contains the reported output of that run.
     */
    public static final class Run {
        private Tool tool;
        private List<Result> results;
        private List<Invocation> invocations;

        private Run(final Tool tool, final List<Result> results, final List<Invocation> invocations) {
            this.tool = tool;
            this.results = results;
            this.invocations = invocations;
        }


        public static final class RunBuilder {
            private Tool tool;
            private List<Result> results;
            private List<Invocation> invocations;

            private RunBuilder() {
                // make default ctor private; use factory method Run#builder() instead.
            }

            /**
             * Information about the tool or tool pipeline that generated the results in this run. A run can only contain
             * results produced by a single tool or tool pipeline. A run can aggregate results from multiple log files, as long
             * as context around the tool run (tool command-line arguments and the like) is identical for all aggregated files.
             *
             * @return {@code this}.
             */
            public SarifLog.Run.RunBuilder tool(final Tool tool) {
                this.tool = tool;
                return this;
            }

            /**
             * Adds a result to the set of results contained in an SARIF log. The results array can be omitted when
             * a run is solely exporting rules metadata. It must be present (but may be empty) if a log file
             * represents an actual scan.
             *
             * @return {@code this}.
             */
            public SarifLog.Run.RunBuilder result(final Result result) {
                if (this.results == null) {
                    this.results = new ArrayList<>();
                }
                this.results.add(result);
                return this;
            }

            /**
             * The set of results contained in an SARIF log. The results array can be omitted when a run is solely exporting
             * rules metadata. It must be present (but may be empty) if a log file represents an actual scan.
             *
             * @return {@code this}.
             */
            public SarifLog.Run.RunBuilder results(final Collection<? extends Result> results) {
                if (results == null) {
                    throw new java.lang.NullPointerException("results cannot be null");
                }
                if (this.results == null) {
                    this.results = new ArrayList<>();
                }
                this.results.addAll(results);
                return this;
            }

            public SarifLog.Run.RunBuilder clearResults() {
                if (this.results != null) {
                    this.results.clear();
                }
                return this;
            }

            /**
             * The set of invocations providing information about the tool execution such as configuration errors or runtime
             * exceptions.
             *
             * @return {@code this}.
             */
            public SarifLog.Run.RunBuilder invocations(final List<Invocation> invocations) {
                this.invocations = invocations;
                return this;
            }

            public SarifLog.Run build() {
                List<Result> results;
                switch (this.results == null ? 0 : this.results.size()) {
                case 0:
                    results = Collections.emptyList();
                    break;
                case 1:
                    results = Collections.singletonList(this.results.get(0));
                    break;
                default:
                    results = Collections.unmodifiableList(new ArrayList<>(this.results));
                }
                return new SarifLog.Run(this.tool, results, this.invocations);
            }

            @Override
            public String toString() {
                return "SarifLog.Run.RunBuilder(tool=" + this.tool + ", results=" + this.results + ", invocations=" + this.invocations + ")";
            }
        }

        public static SarifLog.Run.RunBuilder builder() {
            return new SarifLog.Run.RunBuilder();
        }

        /**
         * Information about the tool or tool pipeline that generated the results in this run. A run can only contain
         * results produced by a single tool or tool pipeline. A run can aggregate results from multiple log files, as long
         * as context around the tool run (tool command-line arguments and the like) is identical for all aggregated files.
         */
        public Tool getTool() {
            return this.tool;
        }

        /**
         * The set of results contained in an SARIF log. The results array can be omitted when a run is solely exporting
         * rules metadata. It must be present (but may be empty) if a log file represents an actual scan.
         */
        public List<Result> getResults() {
            return this.results;
        }

        /**
         * The set of invocations providing information about the tool execution such as configuration errors or runtime
         * exceptions.
         */
        public List<Invocation> getInvocations() {
            return this.invocations;
        }

        /**
         * Information about the tool or tool pipeline that generated the results in this run. A run can only contain
         * results produced by a single tool or tool pipeline. A run can aggregate results from multiple log files, as long
         * as context around the tool run (tool command-line arguments and the like) is identical for all aggregated files.
         *
         * @return {@code this}.
         */
        public SarifLog.Run setTool(final Tool tool) {
            this.tool = tool;
            return this;
        }

        /**
         * The set of results contained in an SARIF log. The results array can be omitted when a run is solely exporting
         * rules metadata. It must be present (but may be empty) if a log file represents an actual scan.
         *
         * @return {@code this}.
         */
        public SarifLog.Run setResults(final List<Result> results) {
            this.results = results;
            return this;
        }

        /**
         * The set of invocations providing information about the tool execution such as configuration errors or runtime
         * exceptions.
         *
         * @return {@code this}.
         */
        public SarifLog.Run setInvocations(final List<Invocation> invocations) {
            this.invocations = invocations;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Run run = (Run) o;
            return Objects.equals(tool, run.tool) && Objects.equals(results, run.results) && Objects.equals(invocations, run.invocations);
        }

        @Override
        public int hashCode() {
            return Objects.hash(tool, results, invocations);
        }

        @Override
        public String toString() {
            return "SarifLog.Run(tool=" + this.getTool() + ", results=" + this.getResults() + ", invocations=" + this.getInvocations() + ")";
        }
    }


    /**
     * The analysis tool that was run.
     */
    public static final class Tool {
        private Component driver;

        private Tool(final Component driver) {
            this.driver = driver;
        }


        public static final class ToolBuilder {
            private Component driver;

            private ToolBuilder() {
                // make default ctor private; use factory method Tool#builder() instead.
            }

            /**
             * The analysis tool that was run.
             *
             * @return {@code this}.
             */
            public SarifLog.Tool.ToolBuilder driver(final Component driver) {
                this.driver = driver;
                return this;
            }

            public SarifLog.Tool build() {
                return new SarifLog.Tool(this.driver);
            }

            @Override
            public String toString() {
                return "SarifLog.Tool.ToolBuilder(driver=" + this.driver + ")";
            }
        }

        public static SarifLog.Tool.ToolBuilder builder() {
            return new SarifLog.Tool.ToolBuilder();
        }

        /**
         * The analysis tool that was run.
         */
        public Component getDriver() {
            return this.driver;
        }

        /**
         * The analysis tool that was run.
         *
         * @return {@code this}.
         */
        public SarifLog.Tool setDriver(final Component driver) {
            this.driver = driver;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Tool tool = (Tool) o;
            return Objects.equals(driver, tool.driver);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(driver);
        }

        @Override
        public String toString() {
            return "SarifLog.Tool(driver=" + this.getDriver() + ")";
        }
    }


    /**
     * A component, such as a plug-in or the driver, of the analysis tool that was run.
     */
    public static final class Component {
        private String name;
        private String version;
        private String informationUri;
        private List<ReportingDescriptor> rules;

        private Component(final String name, final String version, final String informationUri, final List<ReportingDescriptor> rules) {
            this.name = name;
            this.version = version;
            this.informationUri = informationUri;
            this.rules = rules;
        }

        public static final class ComponentBuilder {
            private String name;
            private String version;
            private String informationUri;
            private List<ReportingDescriptor> rules;

            private ComponentBuilder() {
                // make default ctor private; use factory method Component#builder() instead.
            }

            /**
             * The name of the tool component.
             *
             * @return {@code this}.
             */
            public SarifLog.Component.ComponentBuilder name(final String name) {
                this.name = name;
                return this;
            }

            /**
             * The tool component version, in whatever format the component natively provides.
             *
             * @return {@code this}.
             */
            public SarifLog.Component.ComponentBuilder version(final String version) {
                this.version = version;
                return this;
            }

            /**
             * The absolute URI at which information about this version of the tool component can be found.
             *
             * @return {@code this}.
             */
            public SarifLog.Component.ComponentBuilder informationUri(final String informationUri) {
                this.informationUri = informationUri;
                return this;
            }

            /**
             * An array of reportingDescriptor objects relevant to the analysis performed by the tool component.
             *
             * @return {@code this}.
             */
            public SarifLog.Component.ComponentBuilder rules(final List<ReportingDescriptor> rules) {
                this.rules = rules;
                return this;
            }

            public SarifLog.Component build() {
                return new SarifLog.Component(this.name, this.version, this.informationUri, this.rules);
            }

            @Override
            public String toString() {
                return "SarifLog.Component.ComponentBuilder(name=" + this.name + ", version=" + this.version + ", informationUri=" + this.informationUri + ", rules=" + this.rules + ")";
            }
        }

        public static SarifLog.Component.ComponentBuilder builder() {
            return new SarifLog.Component.ComponentBuilder();
        }

        public SarifLog.Component.ComponentBuilder toBuilder() {
            return new SarifLog.Component.ComponentBuilder().name(this.name).version(this.version).informationUri(this.informationUri).rules(this.rules);
        }

        /**
         * The name of the tool component.
         */
        public String getName() {
            return this.name;
        }

        /**
         * The tool component version, in whatever format the component natively provides.
         */
        public String getVersion() {
            return this.version;
        }

        /**
         * The absolute URI at which information about this version of the tool component can be found.
         */
        public String getInformationUri() {
            return this.informationUri;
        }

        /**
         * An array of reportingDescriptor objects relevant to the analysis performed by the tool component.
         */
        public List<ReportingDescriptor> getRules() {
            return this.rules;
        }

        /**
         * The name of the tool component.
         *
         * @return {@code this}.
         */
        public SarifLog.Component setName(final String name) {
            this.name = name;
            return this;
        }

        /**
         * The tool component version, in whatever format the component natively provides.
         *
         * @return {@code this}.
         */
        public SarifLog.Component setVersion(final String version) {
            this.version = version;
            return this;
        }

        /**
         * The absolute URI at which information about this version of the tool component can be found.
         *
         * @return {@code this}.
         */
        public SarifLog.Component setInformationUri(final String informationUri) {
            this.informationUri = informationUri;
            return this;
        }

        /**
         * An array of reportingDescriptor objects relevant to the analysis performed by the tool component.
         *
         * @return {@code this}.
         */
        public SarifLog.Component setRules(final List<ReportingDescriptor> rules) {
            this.rules = rules;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Component component = (Component) o;
            return Objects.equals(name, component.name) && Objects.equals(version, component.version) && Objects.equals(informationUri, component.informationUri) && Objects.equals(rules, component.rules);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, version, informationUri, rules);
        }

        @Override
        public String toString() {
            return "SarifLog.Component(name=" + this.getName() + ", version=" + this.getVersion() + ", informationUri=" + this.getInformationUri() + ", rules=" + this.getRules() + ")";
        }
    }


    /**
     * Metadata that describes a specific report produced by the tool, as part of the analysis it provides or its runtime
     * reporting.
     */
    public static final class ReportingDescriptor {
        private String id;
        private String name;
        private MultiformatMessage shortDescription;
        private MultiformatMessage fullDescription;
        private MultiformatMessage messageStrings;
        private String helpUri;
        private MultiformatMessage help;
        private PropertyBag properties;
        private ReportingConfiguration defaultConfiguration;

        private ReportingDescriptor(final String id, final String name, final MultiformatMessage shortDescription, final MultiformatMessage fullDescription, final MultiformatMessage messageStrings, final String helpUri, final MultiformatMessage help, final PropertyBag properties, final ReportingConfiguration defaultConfiguration) {
            this.id = id;
            this.name = name;
            this.shortDescription = shortDescription;
            this.fullDescription = fullDescription;
            this.messageStrings = messageStrings;
            this.helpUri = helpUri;
            this.help = help;
            this.properties = properties;
            this.defaultConfiguration = defaultConfiguration;
        }


        public static final class ReportingDescriptorBuilder {
            private String id;
            private String name;
            private MultiformatMessage shortDescription;
            private MultiformatMessage fullDescription;
            private MultiformatMessage messageStrings;
            private String helpUri;
            private MultiformatMessage help;
            private PropertyBag properties;
            private ReportingConfiguration defaultConfiguration;

            private ReportingDescriptorBuilder() {
                // make default ctor private; use factory method ReportingDescriptor#builder() instead.
            }

            /**
             * A stable, opaque identifier for the report.
             *
             * @return {@code this}.
             */
            public SarifLog.ReportingDescriptor.ReportingDescriptorBuilder id(final String id) {
                this.id = id;
                return this;
            }

            /**
             * A report identifier that is understandable to an end user.
             *
             * @return {@code this}.
             */
            public SarifLog.ReportingDescriptor.ReportingDescriptorBuilder name(final String name) {
                this.name = name;
                return this;
            }

            /**
             * A concise description of the report. Should be a single sentence that is understandable when visible space is
             * limited to a single line of text.
             *
             * @return {@code this}.
             */
            public SarifLog.ReportingDescriptor.ReportingDescriptorBuilder shortDescription(final MultiformatMessage shortDescription) {
                this.shortDescription = shortDescription;
                return this;
            }

            /**
             * A description of the report. Should, as far as possible, provide details sufficient to enable resolution of any
             * problem indicated by the result.
             *
             * @return {@code this}.
             */
            public SarifLog.ReportingDescriptor.ReportingDescriptorBuilder fullDescription(final MultiformatMessage fullDescription) {
                this.fullDescription = fullDescription;
                return this;
            }

            /**
             * A set of name/value pairs with arbitrary names. Each value is a multiformatMessageString object, which holds
             * message strings in plain text and (optionally) Markdown format. The strings can include placeholders, which can
             * be used to construct a message in combination with an arbitrary number of additional string arguments.
             *
             * @return {@code this}.
             */
            public SarifLog.ReportingDescriptor.ReportingDescriptorBuilder messageStrings(final MultiformatMessage messageStrings) {
                this.messageStrings = messageStrings;
                return this;
            }

            /**
             * A URI where the primary documentation for the report can be found.
             *
             * @return {@code this}.
             */
            public SarifLog.ReportingDescriptor.ReportingDescriptorBuilder helpUri(final String helpUri) {
                this.helpUri = helpUri;
                return this;
            }

            /**
             * Provides the primary documentation for the report, useful when there is no online documentation.
             *
             * @return {@code this}.
             */
            public SarifLog.ReportingDescriptor.ReportingDescriptorBuilder help(final MultiformatMessage help) {
                this.help = help;
                return this;
            }

            /**
             * Key/value pairs that provide additional information about the report.
             *
             * @return {@code this}.
             */
            public SarifLog.ReportingDescriptor.ReportingDescriptorBuilder properties(final PropertyBag properties) {
                this.properties = properties;
                return this;
            }

            /**
             * The default configuration of a rule, can contain a default level and other properties.
             *
             * @return {@code this}.
             */
            public SarifLog.ReportingDescriptor.ReportingDescriptorBuilder defaultConfiguration(final ReportingConfiguration defaultConfiguration) {
                this.defaultConfiguration = defaultConfiguration;
                return this;
            }

            public SarifLog.ReportingDescriptor build() {
                return new SarifLog.ReportingDescriptor(this.id, this.name, this.shortDescription, this.fullDescription, this.messageStrings, this.helpUri, this.help, this.properties, this.defaultConfiguration);
            }

            @Override
            public String toString() {
                return "SarifLog.ReportingDescriptor.ReportingDescriptorBuilder(id=" + this.id + ", name=" + this.name + ", shortDescription=" + this.shortDescription + ", fullDescription=" + this.fullDescription + ", messageStrings=" + this.messageStrings + ", helpUri=" + this.helpUri + ", help=" + this.help + ", properties=" + this.properties + ", defaultConfiguration=" + this.defaultConfiguration + ")";
            }
        }

        public static SarifLog.ReportingDescriptor.ReportingDescriptorBuilder builder() {
            return new SarifLog.ReportingDescriptor.ReportingDescriptorBuilder();
        }

        /**
         * A stable, opaque identifier for the report.
         */
        public String getId() {
            return this.id;
        }

        /**
         * A report identifier that is understandable to an end user.
         */
        public String getName() {
            return this.name;
        }

        /**
         * A concise description of the report. Should be a single sentence that is understandable when visible space is
         * limited to a single line of text.
         */
        public MultiformatMessage getShortDescription() {
            return this.shortDescription;
        }

        /**
         * A description of the report. Should, as far as possible, provide details sufficient to enable resolution of any
         * problem indicated by the result.
         */
        public MultiformatMessage getFullDescription() {
            return this.fullDescription;
        }

        /**
         * A set of name/value pairs with arbitrary names. Each value is a multiformatMessageString object, which holds
         * message strings in plain text and (optionally) Markdown format. The strings can include placeholders, which can
         * be used to construct a message in combination with an arbitrary number of additional string arguments.
         */
        public MultiformatMessage getMessageStrings() {
            return this.messageStrings;
        }

        /**
         * A URI where the primary documentation for the report can be found.
         */
        public String getHelpUri() {
            return this.helpUri;
        }

        /**
         * Provides the primary documentation for the report, useful when there is no online documentation.
         */
        public MultiformatMessage getHelp() {
            return this.help;
        }

        /**
         * Key/value pairs that provide additional information about the report.
         */
        public PropertyBag getProperties() {
            return this.properties;
        }

        /**
         * The default configuration of a rule, can contain a default level and other properties.
         */
        public ReportingConfiguration getDefaultConfiguration() {
            return this.defaultConfiguration;
        }

        /**
         * A stable, opaque identifier for the report.
         *
         * @return {@code this}.
         */
        public SarifLog.ReportingDescriptor setId(final String id) {
            this.id = id;
            return this;
        }

        /**
         * A report identifier that is understandable to an end user.
         *
         * @return {@code this}.
         */
        public SarifLog.ReportingDescriptor setName(final String name) {
            this.name = name;
            return this;
        }

        /**
         * A concise description of the report. Should be a single sentence that is understandable when visible space is
         * limited to a single line of text.
         *
         * @return {@code this}.
         */
        public SarifLog.ReportingDescriptor setShortDescription(final MultiformatMessage shortDescription) {
            this.shortDescription = shortDescription;
            return this;
        }

        /**
         * A description of the report. Should, as far as possible, provide details sufficient to enable resolution of any
         * problem indicated by the result.
         *
         * @return {@code this}.
         */
        public SarifLog.ReportingDescriptor setFullDescription(final MultiformatMessage fullDescription) {
            this.fullDescription = fullDescription;
            return this;
        }

        /**
         * A set of name/value pairs with arbitrary names. Each value is a multiformatMessageString object, which holds
         * message strings in plain text and (optionally) Markdown format. The strings can include placeholders, which can
         * be used to construct a message in combination with an arbitrary number of additional string arguments.
         *
         * @return {@code this}.
         */
        public SarifLog.ReportingDescriptor setMessageStrings(final MultiformatMessage messageStrings) {
            this.messageStrings = messageStrings;
            return this;
        }

        /**
         * A URI where the primary documentation for the report can be found.
         *
         * @return {@code this}.
         */
        public SarifLog.ReportingDescriptor setHelpUri(final String helpUri) {
            this.helpUri = helpUri;
            return this;
        }

        /**
         * Provides the primary documentation for the report, useful when there is no online documentation.
         *
         * @return {@code this}.
         */
        public SarifLog.ReportingDescriptor setHelp(final MultiformatMessage help) {
            this.help = help;
            return this;
        }

        /**
         * Key/value pairs that provide additional information about the report.
         *
         * @return {@code this}.
         */
        public SarifLog.ReportingDescriptor setProperties(final PropertyBag properties) {
            this.properties = properties;
            return this;
        }

        /**
         * The default configuration of a rule, can contain a default level and other properties.
         *
         * @return {@code this}.
         */
        public SarifLog.ReportingDescriptor setDefaultConfiguration(final ReportingConfiguration defaultConfiguration) {
            this.defaultConfiguration = defaultConfiguration;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ReportingDescriptor that = (ReportingDescriptor) o;
            return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(shortDescription, that.shortDescription) && Objects.equals(fullDescription, that.fullDescription) && Objects.equals(messageStrings, that.messageStrings) && Objects.equals(helpUri, that.helpUri) && Objects.equals(help, that.help) && Objects.equals(properties, that.properties) && Objects.equals(defaultConfiguration, that.defaultConfiguration);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name, shortDescription, fullDescription, messageStrings, helpUri, help, properties, defaultConfiguration);
        }

        @Override
        public String toString() {
            return "SarifLog.ReportingDescriptor(id=" + this.getId() + ", name=" + this.getName() + ", shortDescription=" + this.getShortDescription() + ", fullDescription=" + this.getFullDescription() + ", messageStrings=" + this.getMessageStrings() + ", helpUri=" + this.getHelpUri() + ", help=" + this.getHelp() + ", properties=" + this.getProperties() + ", defaultConfiguration=" + this.getDefaultConfiguration() + ")";
        }
    }


    /**
     * Configure the Sarif reporting defined by a reportingDescriptor.
     * Can be used as the defaultConfiguration of a reportingDescriptor.
     * Can also be used in configurationOverride to override those defaults.
     */
    public static final class ReportingConfiguration {
        private Boolean enabled;
        private String level;
        private Double rank;
        private PropertyBag parameters;

        private ReportingConfiguration(final Boolean enabled, final String level, final Double rank, final PropertyBag parameters) {
            this.enabled = enabled;
            this.level = level;
            this.rank = rank;
            this.parameters = parameters;
        }

        public static final class ReportingConfigurationBuilder {
            private Boolean enabled;
            private String level;
            private Double rank;
            private PropertyBag parameters;

            private ReportingConfigurationBuilder() {
                // make default ctor private; use factory method ReportingConfiguration#builder() instead.
            }

            /**
             * Boolean, to dis- and enable the config based on matching a rule (through the Descriptor).
             *
             * <p>Default: true
             */
            public SarifLog.ReportingConfiguration.ReportingConfigurationBuilder enabled(final Boolean enabled) {
                this.enabled = enabled;
                return this;
            }

            /**
             * Takes the levelProperty of the Descriptor (if present) or provides a default/override.
             *
             * <p>Default: warning
             */
            public SarifLog.ReportingConfiguration.ReportingConfigurationBuilder level(final String level) {
                this.level = level;
                return this;
            }

            /**
             * Takes the rank/priority of the Descriptor (if present) or provides a default/override value between 1.0 and 100.0.
             *
             * <p>Default: -1.0
             */
            public SarifLog.ReportingConfiguration.ReportingConfigurationBuilder rank(final Double rank) {
                this.rank = rank;
                return this;
            }

            /**
             * Define configuration information (propertyBag) specific to the Descriptor.
             */
            public SarifLog.ReportingConfiguration.ReportingConfigurationBuilder parameters(final PropertyBag parameters) {
                this.parameters = parameters;
                return this;
            }

            public SarifLog.ReportingConfiguration build() {
                return new SarifLog.ReportingConfiguration(this.enabled, this.level, this.rank, this.parameters);
            }

            @Override
            public String toString() {
                return "SarifLog.ReportingConfiguration.ReportingConfigurationBuilder(enabled=" + this.enabled + ", level=" + this.level + ", rank=" + this.rank + ", parameters=" + this.parameters + ")";
            }
        }

        public static SarifLog.ReportingConfiguration.ReportingConfigurationBuilder builder() {
            return new SarifLog.ReportingConfiguration.ReportingConfigurationBuilder();
        }

        /**
         * Boolean, to dis- and enable the config based on matching a rule (through the Descriptor).
         */
        public Boolean getEnabled() {
            return this.enabled;
        }

        /**
         * Takes the levelProperty of the Descriptor (if present) or provides a default/override.
         */
        public String getLevel() {
            return this.level;
        }

        /**
         * Takes the rank/priority of the Descriptor (if present) or provides a default/override value between 1.0 and 100.0.
         */
        public Double getRank() {
            return this.rank;
        }

        /**
         * Define configuration information (propertyBag) specific to the Descriptor.
         */
        public PropertyBag getParameters() {
            return this.parameters;
        }

        /**
         * Boolean, to dis- and enable the config based on matching a rule (through the Descriptor).
         *
         * @return {@code this}.
         */
        public SarifLog.ReportingConfiguration setEnabled(final Boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        /**
         * Takes the levelProperty of the Descriptor (if present) or provides a default/override.
         *
         * @return {@code this}.
         */
        public SarifLog.ReportingConfiguration setLevel(final String level) {
            this.level = level;
            return this;
        }

        /**
         * Takes the rank/priority of the Descriptor (if present) or provides a default/override value between 1.0 and 100.0.
         *
         * @return {@code this}.
         */
        public SarifLog.ReportingConfiguration setRank(final Double rank) {
            this.rank = rank;
            return this;
        }

        /**
         * Define configuration information (propertyBag) specific to the Descriptor.
         *
         * @return {@code this}.
         */
        public SarifLog.ReportingConfiguration setParameters(final PropertyBag parameters) {
            this.parameters = parameters;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ReportingConfiguration that = (ReportingConfiguration) o;
            return Objects.equals(enabled, that.enabled) && Objects.equals(level, that.level) && Objects.equals(rank, that.rank) && Objects.equals(parameters, that.parameters);
        }

        @Override
        public int hashCode() {
            return Objects.hash(enabled, level, rank, parameters);
        }

        @Override
        public String toString() {
            return "SarifLog.ReportingConfiguration(enabled=" + this.getEnabled() + ", level=" + this.getLevel() + ", rank=" + this.getRank() + ", parameters=" + this.getParameters() + ")";
        }
    }


    /**
     * A message string or message format string rendered in multiple formats.
     */
    public static final class MultiformatMessage {
        private String text;
        private String markdown;

        public MultiformatMessage(String text) {
            this.text = text;
        }

        public MultiformatMessage(final String text, final String markdown) {
            this.text = text;
            this.markdown = markdown;
        }

        public static final class MultiformatMessageBuilder {
            private String text;
            private String markdown;

            private MultiformatMessageBuilder() {
                // make default ctor private; use factory method MultiformatMessage#builder() instead.
            }

            /**
             * A plain text message string or format string.
             *
             * @return {@code this}.
             */
            public SarifLog.MultiformatMessage.MultiformatMessageBuilder text(final String text) {
                this.text = text;
                return this;
            }

            /**
             * A Markdown message string or format string.
             *
             * @return {@code this}.
             */
            public SarifLog.MultiformatMessage.MultiformatMessageBuilder markdown(final String markdown) {
                this.markdown = markdown;
                return this;
            }

            public SarifLog.MultiformatMessage build() {
                return new SarifLog.MultiformatMessage(this.text, this.markdown);
            }

            @Override
            public String toString() {
                return "SarifLog.MultiformatMessage.MultiformatMessageBuilder(text=" + this.text + ", markdown=" + this.markdown + ")";
            }
        }

        public static SarifLog.MultiformatMessage.MultiformatMessageBuilder builder() {
            return new SarifLog.MultiformatMessage.MultiformatMessageBuilder();
        }

        /**
         * A plain text message string or format string.
         */
        public String getText() {
            return this.text;
        }

        /**
         * A Markdown message string or format string.
         */
        public String getMarkdown() {
            return this.markdown;
        }

        /**
         * A plain text message string or format string.
         *
         * @return {@code this}.
         */
        public SarifLog.MultiformatMessage setText(final String text) {
            this.text = text;
            return this;
        }

        /**
         * A Markdown message string or format string.
         *
         * @return {@code this}.
         */
        public SarifLog.MultiformatMessage setMarkdown(final String markdown) {
            this.markdown = markdown;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            MultiformatMessage that = (MultiformatMessage) o;
            return Objects.equals(text, that.text) && Objects.equals(markdown, that.markdown);
        }

        @Override
        public int hashCode() {
            return Objects.hash(text, markdown);
        }

        @Override
        public String toString() {
            return "SarifLog.MultiformatMessage(text=" + this.getText() + ", markdown=" + this.getMarkdown() + ")";
        }
    }


    /**
     * An exception information object, for the tool runtime errors.
     */
    public static final class Exception {
        private String message;

        private Exception(final String message) {
            this.message = message;
        }

        public static final class ExceptionBuilder {
            private String message;

            private ExceptionBuilder() {
                // make default ctor private; use factory method Exception#builder() instead.
            }

            /**
             * A plain text message string or format string.
             *
             * @return {@code this}.
             */
            public SarifLog.Exception.ExceptionBuilder message(final String message) {
                this.message = message;
                return this;
            }

            public SarifLog.Exception build() {
                return new SarifLog.Exception(this.message);
            }

            @Override
            public String toString() {
                return "SarifLog.Exception.ExceptionBuilder(message=" + this.message + ")";
            }
        }

        public static SarifLog.Exception.ExceptionBuilder builder() {
            return new SarifLog.Exception.ExceptionBuilder();
        }

        public SarifLog.Exception.ExceptionBuilder toBuilder() {
            return new SarifLog.Exception.ExceptionBuilder().message(this.message);
        }

        /**
         * A plain text message string or format string.
         */
        public String getMessage() {
            return this.message;
        }

        /**
         * A plain text message string or format string.
         *
         * @return {@code this}.
         */
        public SarifLog.Exception setMessage(final String message) {
            this.message = message;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Exception exception = (Exception) o;
            return Objects.equals(message, exception.message);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(message);
        }

        @Override
        public String toString() {
            return "SarifLog.Exception(message=" + this.getMessage() + ")";
        }
    }


    /**
     * An associated rule to the toolConfigurationNotification.
     */
    public static final class AssociatedRule {
        private String id;

        private AssociatedRule(final String id) {
            this.id = id;
        }

        public static final class AssociatedRuleBuilder {
            private String id;

            private AssociatedRuleBuilder() {
                // make default ctor private; use factory method AssociatedRule#builder() instead.
            }

            /**
             * The stable, unique identifier of the rule, if any, to which this result is relevant.
             *
             * @return {@code this}.
             */
            public SarifLog.AssociatedRule.AssociatedRuleBuilder id(final String id) {
                this.id = id;
                return this;
            }

            public SarifLog.AssociatedRule build() {
                return new SarifLog.AssociatedRule(this.id);
            }

            @Override
            public String toString() {
                return "SarifLog.AssociatedRule.AssociatedRuleBuilder(id=" + this.id + ")";
            }
        }

        public static SarifLog.AssociatedRule.AssociatedRuleBuilder builder() {
            return new SarifLog.AssociatedRule.AssociatedRuleBuilder();
        }

        public SarifLog.AssociatedRule.AssociatedRuleBuilder toBuilder() {
            return new SarifLog.AssociatedRule.AssociatedRuleBuilder().id(this.id);
        }

        /**
         * The stable, unique identifier of the rule, if any, to which this result is relevant.
         */
        public String getId() {
            return this.id;
        }

        /**
         * The stable, unique identifier of the rule, if any, to which this result is relevant.
         *
         * @return {@code this}.
         */
        public SarifLog.AssociatedRule setId(final String id) {
            this.id = id;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            AssociatedRule that = (AssociatedRule) o;
            return Objects.equals(id, that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(id);
        }

        @Override
        public String toString() {
            return "SarifLog.AssociatedRule(id=" + this.getId() + ")";
        }
    }


    /**
     * An invocation property to specify tool configuration errors.
     */
    public static final class ToolConfigurationNotification {
        private AssociatedRule associatedRule;
        private Message message;

        private ToolConfigurationNotification(final AssociatedRule associatedRule, final Message message) {
            this.associatedRule = associatedRule;
            this.message = message;
        }

        public static final class ToolConfigurationNotificationBuilder {
            private AssociatedRule associatedRule;
            private Message message;

            private ToolConfigurationNotificationBuilder() {
                // make default ctor private; use factory method ToolConfigurationNotification#builder() instead.
            }

            /**
             * An associated rule.
             *
             * @return {@code this}.
             */
            public SarifLog.ToolConfigurationNotification.ToolConfigurationNotificationBuilder associatedRule(final AssociatedRule associatedRule) {
                this.associatedRule = associatedRule;
                return this;
            }

            /**
             * A message component to detail the configuration error.
             *
             * @return {@code this}.
             */
            public SarifLog.ToolConfigurationNotification.ToolConfigurationNotificationBuilder message(final Message message) {
                this.message = message;
                return this;
            }

            public SarifLog.ToolConfigurationNotification build() {
                return new SarifLog.ToolConfigurationNotification(this.associatedRule, this.message);
            }

            @Override
            public String toString() {
                return "SarifLog.ToolConfigurationNotification.ToolConfigurationNotificationBuilder(associatedRule=" + this.associatedRule + ", message=" + this.message + ")";
            }
        }

        public static SarifLog.ToolConfigurationNotification.ToolConfigurationNotificationBuilder builder() {
            return new SarifLog.ToolConfigurationNotification.ToolConfigurationNotificationBuilder();
        }

        public SarifLog.ToolConfigurationNotification.ToolConfigurationNotificationBuilder toBuilder() {
            return new SarifLog.ToolConfigurationNotification.ToolConfigurationNotificationBuilder().associatedRule(this.associatedRule).message(this.message);
        }

        /**
         * An associated rule.
         */
        public AssociatedRule getAssociatedRule() {
            return this.associatedRule;
        }

        /**
         * A message component to detail the configuration error.
         */
        public Message getMessage() {
            return this.message;
        }

        /**
         * An associated rule.
         *
         * @return {@code this}.
         */
        public SarifLog.ToolConfigurationNotification setAssociatedRule(final AssociatedRule associatedRule) {
            this.associatedRule = associatedRule;
            return this;
        }

        /**
         * A message component to detail the configuration error.
         *
         * @return {@code this}.
         */
        public SarifLog.ToolConfigurationNotification setMessage(final Message message) {
            this.message = message;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ToolConfigurationNotification that = (ToolConfigurationNotification) o;
            return Objects.equals(associatedRule, that.associatedRule) && Objects.equals(message, that.message);
        }

        @Override
        public int hashCode() {
            return Objects.hash(associatedRule, message);
        }

        @Override
        public String toString() {
            return "SarifLog.ToolConfigurationNotification(associatedRule=" + this.getAssociatedRule() + ", message=" + this.getMessage() + ")";
        }
    }


    /**
     * An invocation property to specify tool runtime errors.
     */
    public static final class ToolExecutionNotification {
        private List<Location> locations;
        private Message message;
        private Exception exception;

        private ToolExecutionNotification(final List<Location> locations, final Message message, final Exception exception) {
            this.locations = locations;
            this.message = message;
            this.exception = exception;
        }

        public static final class ToolExecutionNotificationBuilder {
            private List<Location> locations;
            private Message message;
            private Exception exception;

            private ToolExecutionNotificationBuilder() {
                // make default ctor private; use factory method ToolExecutionNotification#builder() instead.
            }

            /**
             * A list of related locations to the error.
             *
             * @return {@code this}.
             */
            public SarifLog.ToolExecutionNotification.ToolExecutionNotificationBuilder locations(final List<Location> locations) {
                this.locations = locations;
                return this;
            }

            /**
             * A message component to detail the runtime error.
             *
             * @return {@code this}.
             */
            public SarifLog.ToolExecutionNotification.ToolExecutionNotificationBuilder message(final Message message) {
                this.message = message;
                return this;
            }

            /**
             * A exception component to detail the tool exception.
             *
             * @return {@code this}.
             */
            public SarifLog.ToolExecutionNotification.ToolExecutionNotificationBuilder exception(final Exception exception) {
                this.exception = exception;
                return this;
            }

            public SarifLog.ToolExecutionNotification build() {
                return new SarifLog.ToolExecutionNotification(this.locations, this.message, this.exception);
            }

            @Override
            public String toString() {
                return "SarifLog.ToolExecutionNotification.ToolExecutionNotificationBuilder(locations=" + this.locations + ", message=" + this.message + ", exception=" + this.exception + ")";
            }
        }

        public static SarifLog.ToolExecutionNotification.ToolExecutionNotificationBuilder builder() {
            return new SarifLog.ToolExecutionNotification.ToolExecutionNotificationBuilder();
        }

        public SarifLog.ToolExecutionNotification.ToolExecutionNotificationBuilder toBuilder() {
            return new SarifLog.ToolExecutionNotification.ToolExecutionNotificationBuilder().locations(this.locations).message(this.message).exception(this.exception);
        }

        /**
         * A list of related locations to the error.
         */
        public List<Location> getLocations() {
            return this.locations;
        }

        /**
         * A message component to detail the runtime error.
         */
        public Message getMessage() {
            return this.message;
        }

        /**
         * A exception component to detail the tool exception.
         */
        public Exception getException() {
            return this.exception;
        }

        /**
         * A list of related locations to the error.
         *
         * @return {@code this}.
         */
        public SarifLog.ToolExecutionNotification setLocations(final List<Location> locations) {
            this.locations = locations;
            return this;
        }

        /**
         * A message component to detail the runtime error.
         *
         * @return {@code this}.
         */
        public SarifLog.ToolExecutionNotification setMessage(final Message message) {
            this.message = message;
            return this;
        }

        /**
         * A exception component to detail the tool exception.
         *
         * @return {@code this}.
         */
        public SarifLog.ToolExecutionNotification setException(final Exception exception) {
            this.exception = exception;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ToolExecutionNotification that = (ToolExecutionNotification) o;
            return Objects.equals(locations, that.locations) && Objects.equals(message, that.message) && Objects.equals(exception, that.exception);
        }

        @Override
        public int hashCode() {
            return Objects.hash(locations, message, exception);
        }

        @Override
        public String toString() {
            return "SarifLog.ToolExecutionNotification(locations=" + this.getLocations() + ", message=" + this.getMessage() + ", exception=" + this.getException() + ")";
        }
    }


    /**
     * An invocation component to specify tool invocation details/errors.
     */
    public static final class Invocation {
        private Boolean executionSuccessful;
        private List<ToolConfigurationNotification> toolConfigurationNotifications;
        private List<ToolExecutionNotification> toolExecutionNotifications;

        private Invocation(final Boolean executionSuccessful, final List<ToolConfigurationNotification> toolConfigurationNotifications, final List<ToolExecutionNotification> toolExecutionNotifications) {
            this.executionSuccessful = executionSuccessful;
            this.toolConfigurationNotifications = toolConfigurationNotifications;
            this.toolExecutionNotifications = toolExecutionNotifications;
        }

        public static final class InvocationBuilder {
            private Boolean executionSuccessful;
            private List<ToolConfigurationNotification> toolConfigurationNotifications;
            private List<ToolExecutionNotification> toolExecutionNotifications;

            private InvocationBuilder() {
                // make default ctor private; use factory method Invocation#builder() instead.
            }

            /**
             * An indicator of execution status.
             */
            public SarifLog.Invocation.InvocationBuilder executionSuccessful(final Boolean executionSuccessful) {
                this.executionSuccessful = executionSuccessful;
                return this;
            }

            /**
             * A list of associated tool configuration errors.
             */
            public SarifLog.Invocation.InvocationBuilder toolConfigurationNotifications(final List<ToolConfigurationNotification> toolConfigurationNotifications) {
                this.toolConfigurationNotifications = toolConfigurationNotifications;
                return this;
            }

            /**
             * A list of associated tool runtime errors.
             */
            public SarifLog.Invocation.InvocationBuilder toolExecutionNotifications(final List<ToolExecutionNotification> toolExecutionNotifications) {
                this.toolExecutionNotifications = toolExecutionNotifications;
                return this;
            }

            public SarifLog.Invocation build() {
                return new SarifLog.Invocation(this.executionSuccessful, this.toolConfigurationNotifications, this.toolExecutionNotifications);
            }

            @Override
            public String toString() {
                return "SarifLog.Invocation.InvocationBuilder(executionSuccessful=" + this.executionSuccessful + ", toolConfigurationNotifications=" + this.toolConfigurationNotifications + ", toolExecutionNotifications=" + this.toolExecutionNotifications + ")";
            }
        }

        public static SarifLog.Invocation.InvocationBuilder builder() {
            return new SarifLog.Invocation.InvocationBuilder();
        }

        public SarifLog.Invocation.InvocationBuilder toBuilder() {
            return new SarifLog.Invocation.InvocationBuilder().executionSuccessful(this.executionSuccessful).toolConfigurationNotifications(this.toolConfigurationNotifications).toolExecutionNotifications(this.toolExecutionNotifications);
        }

        public Boolean getExecutionSuccessful() {
            return this.executionSuccessful;
        }

        public List<ToolConfigurationNotification> getToolConfigurationNotifications() {
            return this.toolConfigurationNotifications;
        }

        public List<ToolExecutionNotification> getToolExecutionNotifications() {
            return this.toolExecutionNotifications;
        }

        public SarifLog.Invocation setExecutionSuccessful(final Boolean executionSuccessful) {
            this.executionSuccessful = executionSuccessful;
            return this;
        }

        public SarifLog.Invocation setToolConfigurationNotifications(final List<ToolConfigurationNotification> toolConfigurationNotifications) {
            this.toolConfigurationNotifications = toolConfigurationNotifications;
            return this;
        }

        public SarifLog.Invocation setToolExecutionNotifications(final List<ToolExecutionNotification> toolExecutionNotifications) {
            this.toolExecutionNotifications = toolExecutionNotifications;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Invocation that = (Invocation) o;
            return Objects.equals(executionSuccessful, that.executionSuccessful) && Objects.equals(toolConfigurationNotifications, that.toolConfigurationNotifications) && Objects.equals(toolExecutionNotifications, that.toolExecutionNotifications);
        }

        @Override
        public int hashCode() {
            return Objects.hash(executionSuccessful, toolConfigurationNotifications, toolExecutionNotifications);
        }

        @Override
        public String toString() {
            return "SarifLog.Invocation(executionSuccessful=" + this.getExecutionSuccessful() + ", toolConfigurationNotifications=" + this.getToolConfigurationNotifications() + ", toolExecutionNotifications=" + this.getToolExecutionNotifications() + ")";
        }
    }

    private static String defaultSchema() {
        return "https://json.schemastore.org/sarif-2.1.0.json";
    }

    private static String defaultVersion() {
        return "2.1.0";
    }

    private SarifLog(final String schema, final String version, final List<Run> runs) {
        this.schema = schema;
        this.version = version;
        this.runs = runs;
    }


    public static final class SarifLogBuilder {
        private boolean schemaSet;
        private String schemaValue;
        private boolean versionSet;
        private String versionValue;
        private List<Run> runs;

        private SarifLogBuilder() {
            // make default ctor private; use factory method SarifLog#builder() instead.
        }

        /**
         * The URI of the JSON schema corresponding to the version.
         *
         * @return {@code this}.
         */
        public SarifLog.SarifLogBuilder schema(final String schema) {
            this.schemaValue = schema;
            schemaSet = true;
            return this;
        }

        /**
         * The SARIF format version of this log file.
         *
         * @return {@code this}.
         */
        public SarifLog.SarifLogBuilder version(final String version) {
            this.versionValue = version;
            versionSet = true;
            return this;
        }

        /**
         * The set of runs contained in this log file.
         *
         * @return {@code this}.
         */
        public SarifLog.SarifLogBuilder runs(final List<Run> runs) {
            this.runs = runs;
            return this;
        }

        public SarifLog build() {
            String schemaValue = this.schemaValue;
            if (!this.schemaSet) {
                schemaValue = SarifLog.defaultSchema();
            }
            String versionValue = this.versionValue;
            if (!this.versionSet) {
                versionValue = SarifLog.defaultVersion();
            }
            return new SarifLog(schemaValue, versionValue, this.runs);
        }

        @Override
        public String toString() {
            return "SarifLog.SarifLogBuilder(schema$value=" + this.schemaValue + ", version$value=" + this.versionValue + ", runs=" + this.runs + ")";
        }
    }

    public static SarifLog.SarifLogBuilder builder() {
        return new SarifLog.SarifLogBuilder();
    }

    /**
     * The URI of the JSON schema corresponding to the version.
     */
    public String getSchema() {
        return this.schema;
    }

    /**
     * The SARIF format version of this log file.
     */
    public String getVersion() {
        return this.version;
    }

    /**
     * The set of runs contained in this log file.
     */
    public List<Run> getRuns() {
        return this.runs;
    }

    /**
     * The URI of the JSON schema corresponding to the version.
     *
     * @return {@code this}.
     */
    public SarifLog setSchema(final String schema) {
        this.schema = schema;
        return this;
    }

    /**
     * The SARIF format version of this log file.
     *
     * @return {@code this}.
     */
    public SarifLog setVersion(final String version) {
        this.version = version;
        return this;
    }

    /**
     * The set of runs contained in this log file.
     *
     * @return {@code this}.
     */
    public SarifLog setRuns(final List<Run> runs) {
        this.runs = runs;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SarifLog sarifLog = (SarifLog) o;
        return Objects.equals(schema, sarifLog.schema) && Objects.equals(version, sarifLog.version) && Objects.equals(runs, sarifLog.runs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(schema, version, runs);
    }

    @Override
    public String toString() {
        return "SarifLog(schema=" + this.getSchema() + ", version=" + this.getVersion() + ", runs=" + this.getRuns() + ")";
    }
}

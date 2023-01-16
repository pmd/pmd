/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cache;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.document.TextRange2d;
import net.sourceforge.pmd.util.StringUtil;

/**
 * A {@link RuleViolation} implementation that is immutable, and therefore cache friendly
 *
 * @deprecated This is internal API, will be hidden with 7.0.0
 */
@Deprecated
@InternalApi
public final class CachedRuleViolation implements RuleViolation {

    private final CachedRuleMapper mapper;

    private final String description;
    private final FileLocation location;
    private final String ruleClassName;
    private final String ruleName;
    private final String ruleTargetLanguage;
    private final Map<String, String> additionalInfo;

    private CachedRuleViolation(final CachedRuleMapper mapper, final String description,
                                final String fileName, final String ruleClassName, final String ruleName,
                                final String ruleTargetLanguage, final int beginLine, final int beginColumn,
                                final int endLine, final int endColumn,
                                final Map<String, String> additionalInfo) {
        this.mapper = mapper;
        this.description = description;
        this.location = FileLocation.range(fileName, TextRange2d.range2d(beginLine, beginColumn, endLine, endColumn));
        this.ruleClassName = ruleClassName;
        this.ruleName = ruleName;
        this.ruleTargetLanguage = ruleTargetLanguage;
        this.additionalInfo = additionalInfo;
    }

    @Override
    public Rule getRule() {
        // The mapper may be initialized after cache is loaded, so use it lazily
        return mapper.getRuleForClass(ruleClassName, ruleName, ruleTargetLanguage);
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public FileLocation getLocation() {
        return location;
    }

    @Override
    public Map<String, String> getAdditionalInfo() {
        return additionalInfo;
    }

    /**
     * Helper method to load a {@link CachedRuleViolation} from an input stream.
     *
     * @param stream The stream from which to load the violation.
     * @param fileName The name of the file on which this rule was reported.
     * @param mapper The mapper to be used to obtain rule instances from the active rulesets.
     * @return The loaded rule violation.
     * @throws IOException
     */
    /* package */ static CachedRuleViolation loadFromStream(final DataInputStream stream,
            final String fileName, final CachedRuleMapper mapper) throws IOException {
        final String description = stream.readUTF();
        final String ruleClassName = stream.readUTF();
        final String ruleName = stream.readUTF();
        final String ruleTargetLanguage = stream.readUTF();
        final int beginLine = stream.readInt();
        final int beginColumn = stream.readInt();
        final int endLine = stream.readInt();
        final int endColumn = stream.readInt();
        final Map<String, String> additionalInfo = readAdditionalInfo(stream);
        return new CachedRuleViolation(mapper, description, fileName, ruleClassName, ruleName, ruleTargetLanguage,
                                       beginLine, beginColumn, endLine, endColumn, additionalInfo);
    }

    private static @NonNull Map<String, String> readAdditionalInfo(DataInputStream stream) throws IOException {
        int numAdditionalInfoKeyValuePairs = stream.readInt();
        if (numAdditionalInfoKeyValuePairs == 0) {
            return Collections.emptyMap();
        }

        Map<String, String> additionalInfo = new LinkedHashMap<>();
        while (numAdditionalInfoKeyValuePairs-- > 0) {
            final String key = stream.readUTF();
            final String value = stream.readUTF();
            additionalInfo.put(key, value);
        }
        return Collections.unmodifiableMap(additionalInfo);
    }

    /**
     * Helper method to store a {@link RuleViolation} in an output stream to be later
     * retrieved as a {@link CachedRuleViolation}
     *
     * @param stream    The stream on which to store the violation.
     * @param violation The rule violation to cache.
     */
    /* package */ static void storeToStream(final DataOutputStream stream,
            final RuleViolation violation) throws IOException {
        stream.writeUTF(StringUtil.nullToEmpty(violation.getDescription()));
        stream.writeUTF(StringUtil.nullToEmpty(violation.getRule().getRuleClass()));
        stream.writeUTF(StringUtil.nullToEmpty(violation.getRule().getName()));
        stream.writeUTF(StringUtil.nullToEmpty(violation.getRule().getLanguage().getTerseName()));
        FileLocation location = violation.getLocation();
        stream.writeInt(location.getStartPos().getLine());
        stream.writeInt(location.getStartPos().getColumn());
        stream.writeInt(location.getEndPos().getColumn());
        stream.writeInt(location.getEndPos().getColumn());
        Map<String, String> additionalInfo = violation.getAdditionalInfo();
        stream.writeInt(additionalInfo.size());
        for (Entry<String, String> entry : additionalInfo.entrySet()) {
            stream.writeUTF(entry.getKey());
            stream.writeUTF(StringUtil.nullToEmpty(entry.getValue()));

        }
    }

}

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cache;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleViolation;

/**
 * A {@link RuleViolation} implementation that is immutable, and therefore cache friendly
 */
public final class CachedRuleViolation implements RuleViolation {

    private final CachedRuleMapper mapper;

    private final String description;
    private final String fileName;
    private final String ruleClassName;
    private final int beginLine;
    private final int beginColumn;
    private final int endLine;
    private final int endColumn;
    private final String packageName;
    private final String className;
    private final String methodName;
    private final String variableName;

    private CachedRuleViolation(final CachedRuleMapper mapper, final String description,
            final String fileName, final String ruleClassName, final int beginLine,
            final int beginColumn, final int endLine, final int endColumn, final String packageName,
            final String className, final String methodName, final String variableName) {
        this.mapper = mapper;
        this.description = description;
        this.fileName = fileName;
        this.ruleClassName = ruleClassName;
        this.beginLine = beginLine;
        this.beginColumn = beginColumn;
        this.endLine = endLine;
        this.endColumn = endColumn;
        this.packageName = packageName;
        this.className = className;
        this.methodName = methodName;
        this.variableName = variableName;
    }

    @Override
    public Rule getRule() {
        // The mapper may be initialized after cache is loaded, so use it lazily
        return mapper.getRuleForClass(ruleClassName);
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean isSuppressed() {
        return false; // By definition, if cached, it was not suppressed
    }

    @Override
    public String getFilename() {
        return fileName;
    }

    @Override
    public int getBeginLine() {
        return beginLine;
    }

    @Override
    public int getBeginColumn() {
        return beginColumn;
    }

    @Override
    public int getEndLine() {
        return endLine;
    }

    @Override
    public int getEndColumn() {
        return endColumn;
    }

    @Override
    public String getPackageName() {
        return packageName;
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public String getMethodName() {
        return methodName;
    }

    @Override
    public String getVariableName() {
        return variableName;
    }

    /**
     * Helper method to load a {@link CachedRuleViolation} from an input stream.
     *
     * @param stream The stream from which to load the violation.
     * @param mapper The mapper to be used to obtain rule instances from the active rulesets.
     * @return The loaded rule violation.
     * @throws IOException
     */
    /* package */ static CachedRuleViolation loadFromStream(final DataInputStream stream,
            final CachedRuleMapper mapper) throws IOException {
        final String description = stream.readUTF();
        final String fileName = stream.readUTF();
        final String ruleClassName = stream.readUTF();
        final int beginLine = stream.readInt();
        final int beginColumn = stream.readInt();
        final int endLine = stream.readInt();
        final int endColumn = stream.readInt();
        final String packageName = stream.readUTF();
        final String className = stream.readUTF();
        final String methodName = stream.readUTF();
        final String variableName = stream.readUTF();

        return new CachedRuleViolation(mapper, description, fileName, ruleClassName, beginLine, beginColumn,
                endLine, endColumn, packageName, className, methodName, variableName);
    }

    /**
     * Helper method to store a {@link RuleViolation} in an output stream to be later
     * retrieved as a {@link CachedRuleViolation}
     *
     * @param stream The stream on which to store the violation.
     * @param violation The rule violation to cache.
     * @throws IOException
     */
    /* package */ static void storeToStream(final DataOutputStream stream,
            final RuleViolation violation) throws IOException {
        stream.writeUTF(violation.getDescription());
        stream.writeUTF(violation.getFilename());
        stream.writeUTF(violation.getRule().getRuleClass());
        stream.writeInt(violation.getBeginLine());
        stream.writeInt(violation.getBeginColumn());
        stream.writeInt(violation.getEndLine());
        stream.writeInt(violation.getEndColumn());
        stream.writeUTF(violation.getPackageName());
        stream.writeUTF(violation.getClassName());
        stream.writeUTF(violation.getMethodName());
        stream.writeUTF(violation.getVariableName());
    }
}

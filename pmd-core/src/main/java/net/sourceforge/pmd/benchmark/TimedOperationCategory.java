/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.benchmark;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.ast.AstProcessingStage;


/**
 * Category of an operation to measure.
 *
 * @author Juan Martín Sotuyo Dodero
 */
public final class TimedOperationCategory implements Comparable<TimedOperationCategory> {
    public static final TimedOperationCategory LOAD_RULES = new TimedOperationCategory("Load rules");
    public static final TimedOperationCategory COLLECT_FILES = new TimedOperationCategory("Collect files");
    public static final TimedOperationCategory PARSER = new TimedOperationCategory("Parser");
    public static final TimedOperationCategory RULE = new TimedOperationCategory("Rule");
    public static final TimedOperationCategory RULECHAIN_RULE = new TimedOperationCategory("Rulechain rule");
    public static final TimedOperationCategory RULECHAIN_VISIT = new TimedOperationCategory("Rulechain visit");
    public static final TimedOperationCategory REPORTING = new TimedOperationCategory("Reporting");
    public static final TimedOperationCategory FILE_PROCESSING = new TimedOperationCategory("File processing");
    public static final TimedOperationCategory UNACCOUNTED = new TimedOperationCategory("Unaccounted");

    @Deprecated
    public static final TimedOperationCategory QUALIFIED_NAME_RESOLUTION = new TimedOperationCategory("Qualified name resolution");
    @Deprecated
    public static final TimedOperationCategory SYMBOL_TABLE = new TimedOperationCategory("Symbol table");
    @Deprecated
    public static final TimedOperationCategory DFA = new TimedOperationCategory("Data-flow analysis");
    @Deprecated
    public static final TimedOperationCategory TYPE_RESOLUTION = new TimedOperationCategory("Type resolution");
    @Deprecated
    public static final TimedOperationCategory MULTIFILE_ANALYSIS = new TimedOperationCategory("Multifile analysis");

    private static final Map<AstProcessingStage<?>, TimedOperationCategory> POOL = new HashMap<>();

    private final String displayName;
    private static int currentOrdinal = 0;
    private final AstProcessingStage<?> astProcessingStage;
    private final int ordinal;

    private TimedOperationCategory(String displayName) {
        this.displayName = displayName;
        this.astProcessingStage = null;
        this.ordinal = currentOrdinal++;
    }


    private TimedOperationCategory(String displayName, AstProcessingStage<?> stage) {
        this.displayName = displayName;
        this.astProcessingStage = stage;
        this.ordinal = stage.hashCode(); // AstProcessingStages on same language compare to the same value
    }


    /**
     * Returns the language this operation is registered for
     * if this task is language-specific. Otherwise returns
     * an empty optional.
     */
    public Optional<Language> getLanguage() {
        return Optional.ofNullable(astProcessingStage).map(AstProcessingStage::getLanguage);
    }


    public String displayName() {
        return displayName;
    }


    private int getOrdinal() {
        return ordinal;
    }


    @Override
    public String toString() {
        return displayName();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TimedOperationCategory that = (TimedOperationCategory) o;
        return Objects.equals(displayName, that.displayName)
                && Objects.equals(astProcessingStage, that.astProcessingStage);
    }


    @Override
    public int hashCode() {
        return Objects.hash(displayName, astProcessingStage);
    }


    @Override
    public int compareTo(TimedOperationCategory o) {
        if (ordinal < o.ordinal) {
            return -1;
        } else if (ordinal > o.ordinal) {
            return 1;
        } else {
            return astProcessingStage.compare(o.astProcessingStage);
        }
    }


    /**
     * Builds a new language-specific category for this processing stage.
     *
     * @param stage A processing stage.
     *
     * @return A new category
     */
    public static TimedOperationCategory forStage(AstProcessingStage<?> stage) {
        return POOL.computeIfAbsent(stage, s -> new TimedOperationCategory(s.getDisplayName(), s));
    }
}

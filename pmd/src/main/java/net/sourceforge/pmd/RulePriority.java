/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

/**
 * These are the possible Rule priority values.
 * 
 * For backward compatibility, priorities range in value from 1 to 5, with 5
 * being the lowest priority. This means the ordinal value of the Enum should be
 * avoided in favor of {@link RulePriority#getPriority()} and
 * {@link RulePriority#valueOf(int)}
 *
 * @see <a href="http://pmd.sourceforge.net/pmd-5.1.2/rule-guidelines.html">How to define rules priority</a>
 */
public enum RulePriority {

    /** High: Change absolutely required. Behavior is critically broken/buggy */
    HIGH(1, "High"),
    /** Medium to high: Change highly recommended. Behavior is quite likely to be broken/buggy. */
    MEDIUM_HIGH(2, "Medium High"),
    /** Medium: Change recommended. Behavior is confusing, perhaps buggy, and/or against standards/best practices. */
    MEDIUM(3, "Medium"),
    /** Medium to low: Change optional. Behavior is not likely to be buggy, but more just flies in the face of standards/style/good taste. */
    MEDIUM_LOW(4, "Medium Low"),
    /** Low: Change highly optional. Nice to have, such as a consistent naming policy for package/class/fields... */
    LOW(5, "Low");

    private final int priority;
    private final String name;

    private RulePriority(int priority, String name) {
        this.priority = priority;
        this.name = name;
    }

    /**
     * Get the priority value as a number. This is the value to be used in the
     * externalized form of a priority (e.g. in RuleSet XML).
     *
     * @return The <code>int</code> value of the priority.
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Get the descriptive name of this priority.
     *
     * @return The descriptive name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the descriptive name of the priority.
     * @return descriptive name of the priority
     * @see #getName()
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Get the priority which corresponds to the given number as returned by
     * {@link RulePriority#getPriority()}. If the number is an invalid value,
     * then {@link RulePriority#LOW} will be returned.
     *
     * @param priority The numeric priority value.
     * @return The priority.
     */
    public static RulePriority valueOf(int priority) {
        try {
            return RulePriority.values()[priority - 1];
        } catch (ArrayIndexOutOfBoundsException e) {
            return LOW;
        }
    }
}

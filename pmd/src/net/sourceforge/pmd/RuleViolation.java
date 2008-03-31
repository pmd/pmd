/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

/**
 * A RuleViolation is created by a Rule when it identifies a violation of the
 * Rule constraints.
 * 
 * @see Rule
 */
public interface RuleViolation {

    /**
     * Get the Rule which identified this violation.
     * @return The identifying Rule.
     */
    Rule getRule();

    /**
     * Get the description of this violation.
     * 
     * @return The description.
     */
    String getDescription();

    /**
     * Indicates whether this violation has been suppressed.
     * @return <code>true</code> if this violation is suppressed, <code>false</code> otherwise.
     */
    boolean isSuppressed();

    /**
     * Get the source file name in which this violation was identified.
     * 
     * @return The source file name.
     */
    String getFilename();

    /**
     * Get the begin line number in the source file in which this violation was
     * identified.
     * 
     * @return Begin line number.
     */
    int getBeginLine();

    /**
     * Get the column number of the begin line in the source file
     * in which this violation was identified.
     * 
     * @return Begin column number.
     */
    int getBeginColumn();

    /**
     * Get the end line number in the source file in which this violation was
     * identified.
     * 
     * @return End line number.
     */
    int getEndLine();

    /**
     * Get the column number of the end line in the source file
     * in which this violation was identified.
     * 
     * @return End column number.
     */
    int getEndColumn();

    /**
     * Get the package name of the Class in which this violation was identified.
     * 
     * @return The package name.
     */
    // TODO Isn't this Java specific?
    String getPackageName();

    /**
     * Get the name of the Class in which this violation was identified.
     * 
     * @return The Class name.
     */
    // TODO Isn't this Java specific?
    String getClassName();

    /**
     * Get the method name in which this violation was identified.
     * 
     * @return The method name.
     */
    // TODO Isn't this Java specific?
    String getMethodName();

    /**
     * Get the variable name on which this violation was identified.
     * 
     * @return The variable name.
     */
    String getVariableName();
}

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

/**
 * A convenience exception wrapper. Contains the original exception, if any.
 * Also, contains a severity number (int). Zero implies no severity. The higher
 * the number the greater the severity.
 * 
 * @author Donald A. Leckie
 * @version $Revision$, $Date$
 * @since August 30, 2002
 */
public class PMDException extends Exception {
    private static final long serialVersionUID = 6938647389367956874L;

    private int severity;

    /**
     * Creates a new PMD exception with the specified message.
     * @param message the message
     */
    public PMDException(String message) {
        super(message);
    }

    /**
     * Creates a new PMD exception with the specified message and the given reason as root cause.
     * @param message the message
     * @param reason the root cause
     */
    public PMDException(String message, Exception reason) {
        super(message, reason);
    }

    public void setSeverity(int severity) {
        this.severity = severity;
    }

    public int getSeverity() {
        return severity;
    }
}

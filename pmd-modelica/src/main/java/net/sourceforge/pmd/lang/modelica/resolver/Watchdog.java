/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.resolver;

/**
 * A watchdog counter initialized with some value. Throws an exception after the specified {@link #decrement} calls.
 *
 * Is used to break possible resolution loops.
 */
public class Watchdog {
    private int counter;

    public static final class CountdownException extends Exception {
        // no specific logic, just kind of marker
    }

    Watchdog(int initial) {
        counter = initial;
    }

    void decrement() throws CountdownException {
        counter -= 1;
        if (counter < 0) {
            throw new CountdownException();
        }
    }
}

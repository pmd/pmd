/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.util;

/**
 * 
 * @author Brian Remedios
 */
public final class DateTimeUtil {

	private DateTimeUtil() {}
	
	/**
	 * 
	 * @param milliseconds
	 * @return String
	 */
	public static String asHoursMinutesSeconds(long milliseconds) {
		
		if (milliseconds < 0) {
		    throw new IllegalArgumentException();
		}
		
		long seconds = 0;
        long minutes = 0;
        long hours = 0;

        if (milliseconds > 1000) {
            seconds = milliseconds / 1000;
        }

        if (seconds > 60) {
            minutes = seconds / 60;
            seconds = seconds % 60;
        }

        if (minutes > 60) {
            hours = minutes / 60;
            minutes = minutes % 60;
        }

        StringBuilder res = new StringBuilder();
        if (hours > 0) {
            res.append(hours).append("h ");
        }
        if (hours > 0 || minutes > 0) {
            res.append(minutes).append("m ");
        }
        res.append(seconds).append('s');
        return res.toString();
	}
}

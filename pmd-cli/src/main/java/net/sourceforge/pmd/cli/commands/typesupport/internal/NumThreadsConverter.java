/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli.commands.typesupport.internal;

import picocli.CommandLine.ITypeConverter;

/**
 * Parses a number of threads, either an integer or a float followed by the letter C.
 */
public class NumThreadsConverter implements ITypeConverter<Integer> {
    @Override
    public Integer convert(String s) {
        boolean isCoreMultiplied = s.endsWith("C");
        if (isCoreMultiplied) {
            s = s.substring(0, s.length() - 1); // remove the C
            try {
                float f = Float.parseFloat(s);
                return (int) (f * Runtime.getRuntime().availableProcessors());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("'" + s + "' is not a float or integer");
            }
        }
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("'" + s + "' is not an integer");
        }
    }
}

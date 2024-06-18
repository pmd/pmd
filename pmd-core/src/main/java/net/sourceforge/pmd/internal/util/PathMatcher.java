/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.internal.util;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public final class PathMatcher implements Predicate<String> {

    private final Pattern pattern;
    private final String glob;

    private PathMatcher(Pattern pattern, String glob) {
        this.pattern = pattern;
        this.glob = glob;
    }

    @Override
    public String toString() {
        return glob;
    }

    @Override
    public boolean test(String path) {
        // make platform independent
        path = path.replace('\\', '/');
        return pattern.matcher(path).matches();
    }

    public static PathMatcher compileRegex(String regex) {
        return new PathMatcher(Pattern.compile(regex), regex);
    }


}

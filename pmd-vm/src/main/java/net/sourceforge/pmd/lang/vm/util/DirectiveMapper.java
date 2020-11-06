/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vm.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.lang.vm.directive.Break;
import net.sourceforge.pmd.lang.vm.directive.Define;
import net.sourceforge.pmd.lang.vm.directive.Directive;
import net.sourceforge.pmd.lang.vm.directive.Evaluate;
import net.sourceforge.pmd.lang.vm.directive.Foreach;
import net.sourceforge.pmd.lang.vm.directive.Include;
import net.sourceforge.pmd.lang.vm.directive.Literal;
import net.sourceforge.pmd.lang.vm.directive.Macro;
import net.sourceforge.pmd.lang.vm.directive.Parse;
import net.sourceforge.pmd.lang.vm.directive.Stop;

/**
 * @deprecated for removal in PMD 7.0.0
 */
@Deprecated
public final class DirectiveMapper {
    private DirectiveMapper() { }

    private static final Map<String, Directive> DIRECTIVE_MAP = new HashMap<>();

    private static final Set<String> DIRECTIVE_NAMES = new HashSet<>();

    static {
        DIRECTIVE_MAP.put("foreach", new Foreach());
        DIRECTIVE_MAP.put("include", new Include());
        DIRECTIVE_MAP.put("parse", new Parse());
        DIRECTIVE_MAP.put("macro", new Macro());
        DIRECTIVE_MAP.put("literal", new Literal());
        DIRECTIVE_MAP.put("evaluate", new Evaluate());
        DIRECTIVE_MAP.put("break", new Break());
        DIRECTIVE_MAP.put("define", new Define());
        DIRECTIVE_MAP.put("stop", new Stop());

        for (Directive d : DIRECTIVE_MAP.values()) {
            DIRECTIVE_NAMES.add(d.getName());
        }
    }

    public static Directive getDirective(String directiveName) {
        return DIRECTIVE_MAP.get(directiveName);
    }

    public static boolean isDirective(String name) {
        return DIRECTIVE_NAMES.contains(name);
    }

}

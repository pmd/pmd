package net.sourceforge.pmd.lang.vm.util;

import java.util.HashMap;
import java.util.Map;

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

public class DirectiveMapper {

	private static final Map<String, Directive> DIRECTIVE_MAP = new HashMap<String, Directive>();
	
	static {
		DIRECTIVE_MAP.put("directive.1", new Foreach());
		DIRECTIVE_MAP.put("directive.2", new Include());
		DIRECTIVE_MAP.put("directive.3", new Parse());
		DIRECTIVE_MAP.put("directive.4", new Macro());
		DIRECTIVE_MAP.put("directive.5", new Literal());
		DIRECTIVE_MAP.put("directive.6", new Evaluate());
		DIRECTIVE_MAP.put("directive.7", new Break());
		DIRECTIVE_MAP.put("directive.8", new Define());
		DIRECTIVE_MAP.put("directive.9", new Stop());
	}
	
	public static Directive getDirective(String directiveName) {
		return DIRECTIVE_MAP.get(directiveName);
	}
	
}

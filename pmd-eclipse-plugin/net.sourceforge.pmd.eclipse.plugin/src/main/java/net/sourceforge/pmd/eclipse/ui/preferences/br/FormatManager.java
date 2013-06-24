package net.sourceforge.pmd.eclipse.ui.preferences.br;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;

/**
 * 
 * @author Brian Remedios
 */
public class FormatManager {

	private FormatManager() {}
	
	private static final Map<Class<?>, ValueFormatter> formattersByType = new HashMap<Class<?>, ValueFormatter>();

	static {   // used to render property values in short form in main table
	    formattersByType.put(String.class,      ValueFormatter.StringFormatter);
        formattersByType.put(String[].class,    ValueFormatter.MultiStringFormatter);
        formattersByType.put(Boolean.class,     ValueFormatter.BooleanFormatter);
        formattersByType.put(Boolean[].class,   ValueFormatter.ObjectArrayFormatter);
        formattersByType.put(Integer.class,     ValueFormatter.NumberFormatter);
        formattersByType.put(Integer[].class,   ValueFormatter.ObjectArrayFormatter);
        formattersByType.put(Long.class,        ValueFormatter.NumberFormatter);
        formattersByType.put(Long[].class,      ValueFormatter.ObjectArrayFormatter);
        formattersByType.put(Float.class,       ValueFormatter.NumberFormatter);
        formattersByType.put(Float[].class,     ValueFormatter.ObjectArrayFormatter);
        formattersByType.put(Double.class,      ValueFormatter.NumberFormatter);
        formattersByType.put(Double[].class,    ValueFormatter.ObjectArrayFormatter);
        formattersByType.put(Character.class,   ValueFormatter.ObjectFormatter);
        formattersByType.put(Character[].class, ValueFormatter.ObjectArrayFormatter);
        formattersByType.put(Class.class,       ValueFormatter.TypeFormatter);
        formattersByType.put(Class[].class,     ValueFormatter.MultiTypeFormatter);
        formattersByType.put(Method.class,      ValueFormatter.MethodFormatter);
        formattersByType.put(Method[].class,    ValueFormatter.MultiMethodFormatter);
        formattersByType.put(Object[].class,    ValueFormatter.ObjectArrayFormatter);
        
        formattersByType.put(RulePriority.class,	ValueFormatter.PriorityFormatter);
        formattersByType.put(Language.class,		ValueFormatter.LanguageFormatter);
        formattersByType.put(LanguageVersion.class,	ValueFormatter.LanguageVersionFormatter);
	}
	
	public static ValueFormatter formatterFor(Class<?> type) {
		return formattersByType.get(type);
	}
}

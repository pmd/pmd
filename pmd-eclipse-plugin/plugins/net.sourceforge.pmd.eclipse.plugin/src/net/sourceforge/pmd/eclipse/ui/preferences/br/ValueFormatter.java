package net.sourceforge.pmd.eclipse.ui.preferences.br;

import java.lang.reflect.Method;

import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.eclipse.plugin.UISettings;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.MethodEditorFactory;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.MultiTypeEditorFactory;
import net.sourceforge.pmd.eclipse.util.Util;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.util.ClassUtil;

/**
 *
 * @author Brian Remedios
 */
public interface ValueFormatter {

    String format(Object value);
    
    void format(Object value, StringBuilder target);
        
    ValueFormatter StringFormatter = new BasicValueFormatter() {
        public void format(Object value, StringBuilder target) {
            target.append(value == null ? "" : value);
        }
        public String format(Object value) {
        	return value == null ? "" : value.toString();
        }
    };

    ValueFormatter MultiStringFormatter = new BasicValueFormatter() {
        public void format(Object value, StringBuilder target) {
            target.append('[');
            Util.asString((Object[])value, ", ", target);
            target.append(']');
        }
    };

    ValueFormatter NumberFormatter = new BasicValueFormatter() {
        public void format(Object value, StringBuilder target) {
            target.append(value == null ? "?" : value);
        }
    };

    ValueFormatter BooleanFormatter = new BasicValueFormatter() {
        public void format(Object value, StringBuilder target) {
            target.append(value == null ? "?" : value);
        }
    };

    ValueFormatter TypeFormatter = new BasicValueFormatter() {
        public void format(Object value, StringBuilder target) {
            target.append(value == null ? "" : ClassUtil.asShortestName((Class<?>)value));
        }
    };

    ValueFormatter MultiTypeFormatter = new BasicValueFormatter() {
        public void format(Object value, StringBuilder target) {
            target.append('[');
            Util.asString(MultiTypeEditorFactory.shortNamesFor((Class<?>[])value), ", ", target);
            target.append(']');
        }
    };

    ValueFormatter MethodFormatter = new BasicValueFormatter() {
        public void format(Object value, StringBuilder target) {
            if (value == null) return;
            target.append(
               Util.signatureFor((Method) value, MethodEditorFactory.UnwantedPrefixes)
               );
        }
    };

    ValueFormatter MultiMethodFormatter = new BasicValueFormatter() {
        public void format(Object value, StringBuilder target) {
            target.append('[');
            Object[] methods = ((Object[])value);
            if (methods == null || methods.length == 0) {
                target.append(']');
                return;
            }
            MethodFormatter.format(methods[0], target);
            for (int i=1; i<methods.length; i++) {
                target.append(',');
                MethodFormatter.format(methods[i], target);
            }
            target.append(']');
        }
    };

    ValueFormatter ObjectFormatter = new BasicValueFormatter() {
        public void format(Object value, StringBuilder target) {
          target.append(value == null ? "" : value);
        }
    };

    ValueFormatter ObjectArrayFormatter = new BasicValueFormatter() {
        public void format(Object value, StringBuilder target) {
           target.append('[');
           Util.asString((Object[])value, ", ", target);
           target.append(']');
        }
    };
    
// =================================================================
    
    ValueFormatter PriorityFormatter = new BasicValueFormatter() {
        public String format(Object value) {
           return UISettings.labelFor((RulePriority)value);
        }
    };
    
    ValueFormatter LanguageFormatter = new BasicValueFormatter() {
        public String format(Object value) {
           return ((Language)value).getName();
        }
    };
    
    ValueFormatter LanguageVersionFormatter = new BasicValueFormatter() {
        public String format(Object value) {
           return ((LanguageVersion)value).getName();
        }
    };
}

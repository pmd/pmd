package net.sourceforge.pmd.eclipse.ui.preferences.br;

import java.lang.reflect.Method;

import net.sourceforge.pmd.eclipse.ui.preferences.editors.MethodEditorFactory;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.MultiTypeEditorFactory;
import net.sourceforge.pmd.eclipse.util.Util;
import net.sourceforge.pmd.util.ClassUtil;

/**
 * 
 * @author Brian Remedios
 */
public interface ValueFormatter {

    void format(Object value, StringBuilder target);
    
    ValueFormatter StringFormatter = new ValueFormatter() {
        public void format(Object value, StringBuilder target) {
            target.append(value == null ? "" : value);
        }
    };
    
    ValueFormatter MultiStringFormatter = new ValueFormatter() {
        public void format(Object value, StringBuilder target) {
            target.append('[');
            Util.asString((Object[])value, ", ", target);
            target.append(']');
        }
    };
    
    ValueFormatter NumberFormatter = new ValueFormatter() {
        public void format(Object value, StringBuilder target) {
            target.append(value == null ? "?" : value);
        }
    };
    
    ValueFormatter BooleanFormatter = new ValueFormatter() {
        public void format(Object value, StringBuilder target) {
            target.append(value == null ? "?" : value);
        }
    };
    
    ValueFormatter TypeFormatter = new ValueFormatter() {
        public void format(Object value, StringBuilder target) {    
            target.append(value == null ? "" : ClassUtil.asShortestName((Class<?>)value));
        }
    };
    
    ValueFormatter MultiTypeFormatter = new ValueFormatter() {
        public void format(Object value, StringBuilder target) {
            target.append('[');
            Util.asString(MultiTypeEditorFactory.shortNamesFor((Class<?>[])value), ", ", target);
            target.append(']');
        }
    };
    
    ValueFormatter MethodFormatter = new ValueFormatter() {
        public void format(Object value, StringBuilder target) {
            if (value == null) return;
            target.append(
               Util.signatureFor((Method) value, MethodEditorFactory.UnwantedPrefixes)
               );
        }
    };
    
    ValueFormatter MultiMethodFormatter = new ValueFormatter() {
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
    
    ValueFormatter ObjectFormatter = new ValueFormatter() {
        public void format(Object value, StringBuilder target) {         
          target.append(value == null ? "" : value);
        }
    };
    
    ValueFormatter ObjectArrayFormatter = new ValueFormatter() {
        public void format(Object value, StringBuilder target) { 
           target.append('[');
           Util.asString((Object[])value, ", ", target);
           target.append(']');
        }
    };
}

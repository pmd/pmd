package net.sourceforge.pmd.eclipse.ui.preferences.br;

import net.sourceforge.pmd.util.ClassUtil;


public interface ValueFormatter {

    void format(Object value, StringBuilder target);
    
    ValueFormatter StringFormatter = new ValueFormatter() {
        public void format(Object value, StringBuilder target) {
            target.append(value == null ? "" : value);
        }
    };
    
    ValueFormatter MultiStringFormatter = new ValueFormatter() {
        public void format(Object value, StringBuilder target) {
            Util.asString((Object[])value, ", ", target);
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
            target.append(value == null ? "" : ClassUtil.asShortestName((Class)value));
        }
    };
    
    ValueFormatter MultiTypeFormatter = new ValueFormatter() {
        public void format(Object value, StringBuilder target) {
            target.append('[');
            Util.asString(MultiTypeEditorFactory.shortNamesFor((Class[])value), ", ", target);
            target.append(']');
        }
    };
    
    ValueFormatter ObjectArrayFormatter = new ValueFormatter() {
        public void format(Object value, StringBuilder target) {           
           Util.asString((Object[])value, ", ", target);
        }
    };
    
    ValueFormatter ObjectFormatter = new ValueFormatter() {
        public void format(Object value, StringBuilder target) {           
          target.append(value == null ? "" : value);
        }
    };
}

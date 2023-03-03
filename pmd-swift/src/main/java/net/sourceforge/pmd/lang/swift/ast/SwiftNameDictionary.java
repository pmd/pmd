/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.ast;

import org.antlr.v4.runtime.Vocabulary;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrNameDictionary;


final class SwiftNameDictionary extends AntlrNameDictionary {

    SwiftNameDictionary(Vocabulary vocab, String[] ruleNames) {
        super(vocab, ruleNames);
    }

    @Override
    protected @Nullable String nonAlphaNumName(String name) {
        { // limit scope of 'sup', which would be null outside of here anyway
            String sup = super.nonAlphaNumName(name);
            if (sup != null) {
                return sup;
            }
        }

        if (name.charAt(0) == '#' && StringUtils.isAlphanumeric(name.substring(1))) {
            return "directive-" + name.substring(1);
        }

        switch (name) {
        case "unowned(safe)": return "unowned-safe";
        case "unowned(unsafe)": return "unowned-unsafe";
        case "getter:": return "getter";
        case "setter:": return "setter";
        case "OSXApplicationExtension\u00AD": return "OSXApplicationExtension-";
        default: return null;
        }
    }
}

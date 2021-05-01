/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.internal.util.AssertionUtil;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;

/**
 * A mapping of variables to types.
 */
public final class TypingContext extends MapFunction<JVariableSymbol, @Nullable JTypeMirror> {

    public static final TypingContext EMPTY = new TypingContext(Collections.emptyMap());

    private TypingContext(Map<JVariableSymbol, @Nullable JTypeMirror> map) {
        super(map);
    }

    @Override
    public @Nullable JTypeMirror apply(JVariableSymbol var) {
        return getMap().get(var);
    }

    public TypingContext andThen(TypingContext other) {
        AssertionUtil.requireParamNotNull("other", other);
        if (other.isEmpty()) {
            return this;
        }

        Map<JVariableSymbol, JTypeMirror> newMap = new HashMap<>(this.getMap());
        newMap.putAll(other.getMap());
        return new TypingContext(newMap);
    }

}

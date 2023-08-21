/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.util.AssertionUtil;

/**
 * A mapping of variables to types.
 */
public final class TypingContext extends MapFunction<JVariableSymbol, @Nullable JTypeMirror> {

    /**
     * Empty context. Corresponds to defaulting all lambda param types
     * to their value in the AST.
     */
    public static final TypingContext DEFAULT = new TypingContext(null, Collections.emptyMap());

    private final @Nullable TypingContext parent;

    private TypingContext(@Nullable TypingContext parent, Map<JVariableSymbol, @Nullable JTypeMirror> map) {
        super(map);
        this.parent = parent;
    }

    @Override
    public @Nullable JTypeMirror apply(JVariableSymbol var) {
        JTypeMirror t = getMap().get(var);
        if (t == null && parent != null) {
            // try with parent
            return parent.apply(var);
        }
        return t;
    }

    /**
     * Return a new typing context which uses this one as a parent.
     */
    public TypingContext andThen(Map<JVariableSymbol, @Nullable JTypeMirror> map) {
        return new TypingContext(this, map);
    }

    public TypingContext andThenZip(List<JVariableSymbol> symbols, List<JTypeMirror> types) {
        AssertionUtil.requireParamNotNull("symbols", symbols);
        AssertionUtil.requireParamNotNull("types", types);
        if (symbols.size() != types.size()) {
            throw new IllegalArgumentException("Wrong size");
        } else if (symbols.isEmpty()) {
            return this;
        }

        Map<JVariableSymbol, JTypeMirror> newMap = new HashMap<>(symbols.size() + this.getMap().size());
        newMap.putAll(getMap());
        for (int i = 0; i < symbols.size(); i++) {
            newMap.put(symbols.get(i), types.get(i));
        }
        return this.andThen(newMap);
    }

    public static TypingContext zip(List<JVariableSymbol> symbols, List<JTypeMirror> types) {
        return DEFAULT.andThenZip(symbols, types);
    }

}

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Properties;

/**
 * Language implementation for Lua
 */
public class LuaLanguage extends AbstractLanguage {

    public LuaLanguage() {
        this(System.getProperties());
    }

    /**
     * Creates a new Lua Language instance.
     */
    public LuaLanguage(Properties properties) {
        super("Lua", "lua", new LuaTokenizer(), ".lua");
        setProperties(properties);
    }

    @Override
    public final void setProperties(Properties properties) {
        LuaTokenizer tokenizer = (LuaTokenizer) getTokenizer();
        tokenizer.setProperties(properties);
    }
}

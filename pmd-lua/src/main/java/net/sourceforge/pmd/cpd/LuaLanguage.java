/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

/**
 * Language implementation for Lua
 */
public class LuaLanguage extends AbstractLanguage {

    /**
     * Creates a new Lua Language instance.
     */
    public LuaLanguage() {
        super("Lua", "lua", new LuaTokenizer(), ".lua");
    }
}

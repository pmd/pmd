/*
 * User: tom
 * Date: Jun 17, 2002
 * Time: 5:44:22 PM
 */
package com.infoether.pmd;

import com.infoether.pmd.ast.JavaParserVisitorAdapter;

public abstract class AbstractRule extends JavaParserVisitorAdapter {
    public String getName() {return getClass().getName();}

    public boolean equals(Object o) {
        Rule r = (Rule)o;
        return r.getName().equals(getName());
    }

    public int hashCode() {
        return getName().hashCode();
    }
}

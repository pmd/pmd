/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import net.sourceforge.pmd.annotation.InternalApi;

@InternalApi
abstract class AbstractSelectStatement extends AbstractPLSQLNode {

    private boolean distinct;
    private boolean unique;
    private boolean all;

    AbstractSelectStatement(int i) {
        super(i);
    }

    protected void setDistinct(boolean distinct) {
        this.distinct = true;
    }

    public boolean isDistinct() {
        return distinct;
    }

    protected void setUnique(boolean unique) {
        this.unique = unique;
    }

    public boolean isUnique() {
        return unique;
    }

    protected void setAll(boolean all) {
        this.all = all;
    }

    public boolean isAll() {
        return all;
    }

}

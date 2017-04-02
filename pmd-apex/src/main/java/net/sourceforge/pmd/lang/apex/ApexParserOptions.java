/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex;

import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.util.StringUtil;

public class ApexParserOptions extends ParserOptions {

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (1237);
        result = prime * result + (1237);
        result = prime * result;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final ApexParserOptions that = (ApexParserOptions) obj;
        return StringUtil.isSame(this.suppressMarker, that.suppressMarker, false, false, false);
    }
}

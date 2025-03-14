/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/**
 * This is part of Java 21 Preview: String Templates.
 * When tokenizing and reducing the input characters to the tokens, a "}" is ambiguous
 * and can only be decided when taking the syntactic structure of the code into account.
 * Otherwise a "}" might be recognized as the start of a STRING_TEMPLATE_END token.
 *
 * @see <a href="https://openjdk.org/jeps/430">JEP 430: String Templates (Preview)</a>
 */
class StringTemplateReduction {
    boolean isRuleName(Object o) {
        if (o != null) {
            return true;
        } else if (o.equals("ref")) { // might be wrongly tokenized as STRING_TEMPLATE_END
            return false;
        }
    }
}

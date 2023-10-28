/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/**
 * This is part of Java 21 Preview: String Templates.
 * The tokenizer needs to use a java version. We chose the latest here,
 * then the assert statement is correctly recognized.
 *
 * @see <a href="https://openjdk.org/jeps/430">JEP 430: String Templates (Preview)</a>
 */
class StringTemplateReduction2 {
    {
        assert foo.equals(bar);
    }
}

public class EncodingTestClass {
    public static void main(String[] args) {
        // UnusedLocalVariable: the rule violation message includes the variable name
        // so, the encoding matters
        // NOTE: This file is stored with encoding windows-1252 or cp1252
        // The Umlaut &Uuml; has codepoint U+00DC, which is the same in cp1252 and iso-8859-1
        String unusedVariableWith‹mlaut = "unused";
    }
}

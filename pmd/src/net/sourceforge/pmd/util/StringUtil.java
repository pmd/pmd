package net.sourceforge.pmd.util;

public class StringUtil {

    public static String replaceString(String d, char oldChar, String newString) {
        StringBuffer desc = new StringBuffer();
        int index = d.indexOf(oldChar);
        int last = 0;
        while (index != -1) {
            desc.append(d.substring(last, index));
            desc.append(newString);
            last = index + 1;
            index = d.indexOf(oldChar, last);
        }
        desc.append(d.substring(last));
        return desc.toString();
    }

}

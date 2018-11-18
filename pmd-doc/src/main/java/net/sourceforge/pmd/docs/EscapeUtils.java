/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.docs;

import java.util.List;

public final class EscapeUtils {
    private EscapeUtils() {
        // This is a utility class
    }

    public static String escapeMarkdown(String unescaped) {
        return unescaped.replace("\\", "\\\\")
                .replace("*", "\\*")
                .replace("_", "\\_")
                .replace("~", "\\~")
                .replace("[", "\\[")
                .replace("]", "\\]")
                .replace("|", "\\|");
    }

    private enum State {
        S, LT, LT_H, LT_H_T, LT_H_T_T, LT_H_T_T_P, LT_H_T_T_P1, LT_H_T_T_P_S, LT_H_T_T_P_S1;
    }

    public static String escapeSingleLine(String line) {
        StringBuilder escaped = new StringBuilder(line.length() + 16);
        State s = State.S;
        boolean needsEscape = true;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '`') {
                needsEscape = !needsEscape;
            }
            switch (s) {
            case S:
                if (c == '<') {
                    s = State.LT;
                } else if (c == '>') {
                    if (needsEscape && i > 0) {
                        escaped.append("&gt;");
                    } else {
                        escaped.append(c);
                    }
                } else if (c == '"') {
                    if (needsEscape) {
                        escaped.append("&quot;");
                    } else {
                        escaped.append(c);
                    }
                } else {
                    escaped.append(c);
                }
                break;
            case LT:
                if (c == 'h' || c == 'H') {
                    s = State.LT_H;
                } else {
                    if (needsEscape) {
                        escaped.append("&lt;").append(c);
                    } else {
                        escaped.append("<").append(c);
                    }
                    s = State.S;
                }
                break;
            case LT_H:
                if (c == 't' || c == 'T') {
                    s = State.LT_H_T;
                } else {
                    escaped.append("&lt;h").append(c);
                    s = State.S;
                }
                break;
            case LT_H_T:
                if (c == 't' || c == 'T') {
                    s = State.LT_H_T_T;
                } else {
                    escaped.append("&lt;ht").append(c);
                    s = State.S;
                }
                break;
            case LT_H_T_T:
                if (c == 'p' || c == 'P') {
                    s = State.LT_H_T_T_P;
                } else {
                    escaped.append("&lt;htt").append(c);
                    s = State.S;
                }
                break;
            case LT_H_T_T_P:
                if (c == 's' || c == 'S') {
                    s = State.LT_H_T_T_P_S;
                } else if (c == ':') {
                    escaped.append("<http:");
                    s = State.LT_H_T_T_P1;
                } else {
                    escaped.append("&lt;htt").append(c);
                    s = State.S;
                }
                break;
            case LT_H_T_T_P1:
                escaped.append(c);
                if (c == '>') {
                    s = State.S;
                }
                break;
            case LT_H_T_T_P_S:
                if (c == ':') {
                    escaped.append("<https:");
                    s = State.LT_H_T_T_P_S1;
                } else {
                    escaped.append("&lt;https").append(c);
                    s = State.S;
                }
                break;
            case LT_H_T_T_P_S1:
                escaped.append(c);
                if (c == '>') {
                    s = State.S;
                }
                break;
            default:
                escaped.append(c);
                break;
            }
        }
        return escaped.toString();
    }

    public static List<String> escapeLines(List<String> lines) {
        boolean needsEscape = true;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.startsWith("```")) {
                needsEscape = !needsEscape;
            }
            if (needsEscape && !line.startsWith("    ")) {
                line = escapeSingleLine(line);
            }
            lines.set(i, line);
        }
        return lines;
    }
}

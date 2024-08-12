package net.sourceforge.pmd.lang.rule;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.reporting.RuleContext;

/**
 * This is the basic Rule interface for PMD metric rules.
 */
public interface MetricRule extends Rule {

    static void addViolation(Node node, RuleContext ctx, String checkName, Type type, String name, String value) {
        ctx.addViolationWithMessage(node, "{0}: {1} ''{2}` {3}", checkName, type, name, String.valueOf(value));
    }

    static void addViolation(Node node, RuleContext ctx, String checkName, Type type, String name, int value) {
        addViolation(node, ctx, checkName, type, name, "" + value);
    }

    static Infos getInfos(String message) {
        String checkName = message.substring(0, message.indexOf(":"));
        String type = message.substring(message.indexOf(":") + 2, message.indexOf("'") - 1);
        String name = message.substring(message.indexOf("'") + 1, message.indexOf("`"));
        String value = message.substring(message.indexOf("`") + 2);
        return new Infos(checkName, Type.valueOf(type), name, value);
    }

    enum Type {
        METHOD,
        CLASS
    }

    final class Infos {
        public final String checkName;
        public final Type type;
        public final String nodeName;
        public final String value;

        private Infos(String checkName, Type type, String nodeName, String value) {
            this.checkName = checkName;
            this.type = type;
            this.nodeName = nodeName;
            this.value = value;
        }
    }
}

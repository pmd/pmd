package net.sourceforge.pmd.eclipse.ui.preferences.panelmanagers;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.ui.preferences.br.ValueChangeListener;
import net.sourceforge.pmd.lang.rule.RuleReference;
import net.sourceforge.pmd.util.StringUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 *
 * @author Brian Remedios
 */
public class ExamplePanelManager extends AbstractRulePanelManager {

    private Text exampleField;

    public static final String ID = "example";

    public ExamplePanelManager(String theTitle, EditorUsageMode theMode, ValueChangeListener theListener) {
        super(ID, theTitle, theMode, theListener);
    }

    protected boolean canManageMultipleRules() { return false; }

    protected void clearControls() {
        exampleField.setText("");
    }

    public void showControls(boolean flag) {

        exampleField.setVisible(flag);
    }

    protected void updateOverridenFields() {

        Rule rule = soleRule();

        if (rule instanceof RuleReference) {
            RuleReference ruleReference = (RuleReference)rule;
            exampleField.setBackground(ruleReference.getOverriddenExamples() != null ? overridenColour: null);
        }
    }

    public Control setupOn(Composite parent) {

        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);

        Composite panel = new Composite(parent, 0);
        GridLayout layout = new GridLayout(2, false);
        panel.setLayout(layout);

        exampleField = newTextField(panel);
        gridData = new GridData(GridData.FILL_BOTH);
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalSpan = 1;
        exampleField.setLayoutData(gridData);

        exampleField.addListener(SWT.FocusOut, new Listener() {
            public void handleEvent(Event event) {

                Rule soleRule = soleRule();

                String cleanValue = exampleField.getText().trim();
                String existingValue = soleRule.getDescription();

                if (StringUtil.areSemanticEquals(existingValue, cleanValue)) return;

                soleRule.setDescription(cleanValue);
                valueChanged(null, cleanValue);
            }
        });

        return panel;
    }

    private void formatExampleOn(StringBuilder sb, String example) {
        // TODO - adjust for common leading whitespace on all lines - see StringUtil facilities
   //     sb.append(example.trim());

        String[] lines = example.split("\n");
        List<String> realLines = new ArrayList<String>(lines.length);
        for (String line : lines) if (!StringUtil.isEmpty(line)) realLines.add(line);
        lines = realLines.toArray(new String[realLines.size()]);

        int trimDepth = StringUtil.maxCommonLeadingWhitespaceForAll(lines);
        if (trimDepth > 0) {
            lines = StringUtil.trimStartOn(lines, trimDepth);
        }
        for (String line : lines) {
            sb.append(line).append(PMD.EOL);
        }
    }

    private String examples(Rule rule) {

        List<String> examples = rule.getExamples();
        if (examples.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();
        formatExampleOn(sb, examples.get(0));

        for (int i=1; i<examples.size(); i++) {
            sb.append("----------");
            formatExampleOn(sb, examples.get(i));
        }

        return sb.toString();
    }

    protected void adapt() {

        Rule soleRule = soleRule();

        if (soleRule == null) {
            shutdown(exampleField);
        } else {
            show(exampleField, examples(soleRule));
        }
    }

}

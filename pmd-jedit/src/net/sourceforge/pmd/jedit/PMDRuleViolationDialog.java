/*
 * User: tom
 * Date: Jul 12, 2002
 * Time: 11:08:26 AM
 */
package net.sourceforge.pmd.jedit;

import org.gjt.sp.jedit.jEdit;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleViolation;

public class PMDRuleViolationDialog {

    private JList list;
    private JDialog dialog;

    private static class RuleViolationListModel implements ListModel {

        private Report report;

        public RuleViolationListModel(Report report) {
            this.report = report;
        }
        public int getSize() {
            return report.size();
        }

        public Object getElementAt(int index) {
            int reportIndex = 0;
            for (Iterator i = report.iterator(); i.hasNext();) {
                RuleViolation rv = (RuleViolation)i.next();
                if (reportIndex == index) {
                    return "Line " + rv.getLine() + ":" + rv.getDescription();
                }
                reportIndex++;
            }
            throw new RuntimeException("Hm, the Report size is " + report.size() + " and you asked for rule violation " + index);
        }

        public void addListDataListener(ListDataListener l) {}
        public void removeListDataListener(ListDataListener l) {}
    }


    private RuleViolationListModel rvListModel;

    public PMDRuleViolationDialog(Report report) {
	if (report.isEmpty()) {
		JOptionPane.showMessageDialog(jEdit.getFirstView(), "No errors found");
		return;
	}
        dialog = new JDialog(jEdit.getFirstView(), PMDJEditPlugin.NAME, true);

        rvListModel = new RuleViolationListModel(report);

        //main part of the dialog
        list = new JList(rvListModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        JScrollPane listScroller = new JScrollPane(list);

        JPanel listPane = new JPanel();
        listPane.setLayout(new BoxLayout(listPane, BoxLayout.Y_AXIS));
        listPane.add(listScroller);
        listPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JPanel buttonPane = new JPanel();
        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PMDRuleViolationDialog.this.dialog.dispose();
            }
        });
        dialog.getRootPane().setDefaultButton(okButton);
        buttonPane.add(okButton);

        dialog.getContentPane().add(listPane, BorderLayout.CENTER);
        dialog.getContentPane().add(buttonPane, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(jEdit.getFirstView());
        dialog.pack();
        dialog.setVisible(true);
    }
}

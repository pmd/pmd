
package net.sourceforge.pmd.gel;

import java.awt.Dialog;
import com.gexperts.gel.Editor;
import com.gexperts.gel.Gel;
import com.gexperts.gel.GelAction;
import com.gexperts.gel.Project;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.Vector;
import java.util.Properties;
import java.util.StringTokenizer;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.cpd.FileFinder;
import net.sourceforge.pmd.cpd.JavaLanguage;

/** Description of the Class */
public class PMDPlugin implements GelAction {
  private boolean selectedAll;
  private boolean haveFile;
  private boolean haveProject;
  private String savedFilename;
  private JDialog options;
  private Gel savedGel;
  private String[] rulesetFilenames;
  private boolean[] rulesetInuse;
  private JCheckBox[] rulesetChecks;
  private static final String defaultRulesets = "rulesets/unusedcode.xml,rulesets/basic.xml";

  /**Constructor for the PMDPlugin object */
  public PMDPlugin() { }

  /**
   * Called by Gel to see if plugin is invokable (active)
   *
   * @param gel Our pointer back to Gel
   * @return The active value
   */
  public boolean isActive(Gel gel) {
    boolean haveFile = (gel.getEditor() != null &&
      gel.getEditor().getFileName() != null &&
      gel.getEditor().getFileName().endsWith(".java"));
    boolean haveProject = (gel.getProject() != null);
    return (haveFile || haveProject);
  }

  /**
   * Called by Gel to invoke the plugin
   *
   * @param gel Our pointer back to Gel
   */
  public void perform(Gel gel) {
  int yyyy= 5;
    try {
      selectedAll = true;
      savedGel = gel;
      haveFile = (gel.getEditor() != null &&
        gel.getEditor().getFileName() != null &&
        gel.getEditor().getFileName().endsWith(".java"));
      if (haveFile) {
        savedFilename = gel.getEditor().getFileName();
        if (savedFilename == null) {
          savedFilename = "Unnamed.java";
        }
      }
      haveProject = (gel.getProject() != null);
      createConfigPanel(haveFile, haveProject);
    } catch (Exception ex) {
      gel.addMessage("ERROR " + ex.getClass().getName() + ":" + ex.getMessage());
    }
  }

  /** Description of the Method */
  private void invokePlugin() {
    java.io.Reader reader = null;
    try {
      PMD pmd = new PMD();
      RuleContext ctx = new RuleContext();
      RuleSetFactory rsf = new RuleSetFactory();
      RuleSet ruleSet = new RuleSet();
      String rules = ""; // change later to StringBuffer
      for (int i = 0; i < rulesetFilenames.length; i++) {
        if (rulesetInuse[i]) {
          if (rules.length() != 0) {
            rules += ",";
          }
          rules += rulesetFilenames[i];
        }
      }
      if (rules.length() == 0) {
        return;
      }
      savedGel.addMessage("PMD plugin activated on " + (selectedAll ? "all files" : savedFilename));
      ruleSet.addRuleSet(rsf.createRuleSet(rules));
      ctx.setReport(new Report());
      if (!selectedAll) {
        String code = savedGel.getEditor().getContents();
        ctx.setSourceCodeFilename(savedFilename);
        reader = new StringReader(code);
        pmd.processFile(reader, ruleSet, ctx);
      } else {
        for (Iterator iter = savedGel.getProject().getSourcePaths().iterator(); iter.hasNext(); ) {
          String srcDir = (String) iter.next();
          FileFinder ff = new FileFinder();
          java.util.List files = ff.findFilesFrom(srcDir, new net.sourceforge.pmd.cpd.JavaLanguage.JavaFileOrDirectoryFilter(), true);
          Iterator fileIter = files.iterator();
          while (fileIter.hasNext()) {
            File fileName = (File) fileIter.next();
            ctx.setSourceCodeFilename(fileName.getAbsolutePath());
            reader = new FileReader(fileName);
            pmd.processFile(reader, ruleSet, ctx);
            reader.close();
          }
        }
      }
      Report r = ctx.getReport();
      if (r.isEmpty()) {
        savedGel.addMessage("No problems found");
      } else {
        savedGel.addMessage(r.size() + " problems found");
        Iterator i = r.iterator();
        while (i.hasNext()) {
          RuleViolation rv = (RuleViolation) i.next();
          savedGel.addMessage(rv.getFilename() + ":" + rv.getLine() + ":" + rv.getDescription());
        }
      }
    } catch (Exception ex) {
      savedGel.addMessage("ERROR " + ex.getClass().getName() + ":" + ex.getMessage());
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (Exception ex1) {}
      }
    }
    savedGel.addMessage("Done");
  }

  /**
   * Gets the name attribute of the PMDPlugin object
   *
   * @return The name value
   */
  public String getName() {
    return "PMD";
  }

  private void createConfigPanel(boolean haveFile, boolean haveProject) {
    final JFrame optionsFrame = new JFrame();
    options = new JDialog(optionsFrame, "PMD Options");
    Border padding = BorderFactory.createEmptyBorder(20, 20, 5, 5);
    JPanel outerPanel = new JPanel();
    options.getContentPane().add(outerPanel);
    outerPanel.setLayout(new BorderLayout());

    // options
    JPanel optionsPanel = new JPanel();
    optionsPanel.setBorder(padding);
    ButtonGroup btngroup = new ButtonGroup();
    optionsPanel.setLayout(new GridLayout(haveFile && haveProject ? 2 : 1, 1));

    if (haveFile) {
      JRadioButton rbfile = new JRadioButton("Process current file: " + savedFilename, !haveProject);
      btngroup.add(rbfile);
      rbfile.addItemListener(
        new ItemListener() {
          public void itemStateChanged(ItemEvent ev) {
            selectedAll = false;
          }
        });
      optionsPanel.add(rbfile);
    }

    if (haveProject) {
      JRadioButton rball = new JRadioButton("Process all files in sourcepath", true);
      btngroup.add(rball);
      rball.addItemListener(
        new ItemListener() {
          public void itemStateChanged(ItemEvent e) {
            selectedAll = true;
          }
        });
      optionsPanel.add(rball);
    }

    outerPanel.add(optionsPanel, BorderLayout.NORTH);

    // rules
    JPanel rulesPanel = new JPanel();
    rulesPanel.setBorder(padding);
    outerPanel.add(rulesPanel, BorderLayout.CENTER);
    Properties props = new Properties(System.getProperties());
    java.io.InputStream in = null;
    try {
      in = this.getClass().getResourceAsStream("/rulesets/rulesets.properties");
      props.load(in);
    } catch (Exception ex) {
      savedGel.showMessage("Error getting rulesets file: " + ex.getMessage());
      return;
    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (Exception ex1) {}
      }
    }
    String files = props.getProperty("rulesets.filenames");
    StringTokenizer st = new StringTokenizer(files, ",");
    int size = st.countTokens();
    rulesPanel.setLayout(new GridLayout(size, 1));
    int index = 0;
    rulesetFilenames = new String[size];
    rulesetInuse = new boolean[size];
    rulesetChecks = new JCheckBox[size];
    while (st.hasMoreTokens()) {
      String label = st.nextToken();
      boolean initialValue = (defaultRulesets.indexOf(label) != -1);
      JCheckBox jb = new JCheckBox(label, initialValue);
      rulesPanel.add(jb);
      rulesetFilenames[index] = label;
      rulesetInuse[index] = initialValue;
      rulesetChecks[index] = jb;
      jb.addItemListener(
        new ItemListener() {
          public void itemStateChanged(ItemEvent ce) {
            int i = 0;
            while (i < rulesetChecks.length) {
              if (ce.getSource() == rulesetChecks[i]) {
                break;
              }
              i++;
            }
            rulesetInuse[i] = (ce.getStateChange() == ItemEvent.SELECTED);
          }
        });
      index++;
    }

    // buttons
    JPanel buttonsPanel = new JPanel();
    buttonsPanel.setBorder(padding);
    JButton processButton = new JButton("Process");
    buttonsPanel.add(processButton);
    processButton.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          options.setVisible(false);
          options.dispose();
          optionsFrame.dispose();
          options = null;
          invokePlugin();
        }
      });
    JButton cancelButton = new JButton("Cancel");
    buttonsPanel.add(cancelButton);
    cancelButton.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          options.setVisible(false);
          options.dispose();
          optionsFrame.dispose();
          options = null;
        }
      });
    outerPanel.add(buttonsPanel, BorderLayout.SOUTH);

    options.setDefaultCloseOperation(2);
    options.pack();
    options.setLocationRelativeTo(optionsFrame);
    options.setVisible(true);
  }

}
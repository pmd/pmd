
package net.sourceforge.pmd.gel;
import java.util.AbstractList;
import com.gexperts.gel.Gel;
import com.gexperts.gel.GelAction;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
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

/** This class allows Gel to invoke PMD on Java files */
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
  private static final String DEFAULT_RULES =
    "rulesets/unusedcode.xml,rulesets/basic.xml,rulesets/imports.xml";
  private static final String PROPERTIES_FILE =
    "/rulesets/rulesets.properties";
  private Properties descriptions = new Properties();

  /**
   * Called by Gel to see if plugin is invokable (active)
   *
   * @param gel Our pointer back to Gel
   * @return The active value
   */
  public boolean isActive(Gel gel) {
    boolean haveFile = (gel.getEditor() != null
      && gel.getEditor().getFileName() != null
      && gel.getEditor().getFileName().endsWith(".java"));
    boolean haveProject = (gel.getProject() != null);
    return (haveFile || haveProject);
  }

  /**
   * Called by Gel to invoke the plugin
   *
   * @param gel Our pointer back to Gel
   */
  public void perform(Gel gel) {
    try {
      selectedAll = true;
      savedGel = gel;
      haveFile = (gel.getEditor() != null
        && gel.getEditor().getFileName() != null
        && gel.getEditor().getFileName().endsWith(".java"));
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

  /** Does the actual work of calling PMD */
  private void invokePlugin() {
    java.io.Reader reader = null;
    try {
      PMD pmd = new PMD();
      RuleContext ctx = new RuleContext();
      RuleSetFactory rsf = new RuleSetFactory();
      RuleSet ruleSet = new RuleSet();
      StringBuffer rules = new StringBuffer();
      for (int i = 0; i < rulesetFilenames.length; i++) {
        if (rulesetInuse[i]) {
          if (rules.length() != 0) {
            rules.append(",");
          }
          rules.append(rulesetFilenames[i]);
        }
      }
      if (rules.length() == 0) {
        return;
      }
      savedGel.addMessage("PMD plugin activated on "
        + (selectedAll ? "all files" : savedFilename));
      ruleSet.addRuleSet(rsf.createRuleSet(rules.toString()));
      ctx.setReport(new Report());
      if (!selectedAll) {
        String code = savedGel.getEditor().getContents();
        ctx.setSourceCodeFilename(savedFilename);
        reader = new StringReader(code);
        pmd.processFile(reader, ruleSet, ctx);
      } else {
        Iterator iter = savedGel.getProject().getSourcePaths().iterator();
        while (iter.hasNext()) {
          String srcDir = (String) iter.next();
          FileFinder ff = new FileFinder();
          java.util.List files = ff.findFilesFrom(srcDir,
            new JavaLanguage.JavaFileOrDirectoryFilter(), true);
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
          savedGel.addMessage(rv.getFilename() + ":"
            + rv.getLine() + ":"
            + rv.getDescription());
        }
      }
    } catch (Exception ex) {
      savedGel.addMessage("ERROR " + ex.getClass().getName() + ":" + ex.getMessage());
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (Exception ex1) { }
      }
    }
    savedGel.addMessage("Done");
    savedGel = null;
  }

  /**
   * Gel uses this name in the plugin menu
   *
   * @return The name value
   */
  public String getName() {
    return "PMD";
  }

  private void createConfigPanel(boolean haveFile, boolean haveProject) {
    final JFrame optionsFrame = new JFrame();
    Font headingFont = new Font("SansSerif", Font.BOLD, 12);
//    javax.swing.UIManager.put("ToolTipUI","JoeToolTipUI");

    options = new JDialog(optionsFrame, "PMD Options");
    Border padding = BorderFactory.createEmptyBorder(10, 10, 5, 5);
    JPanel outerPanel = new JPanel();
    options.getContentPane().add(outerPanel);
    outerPanel.setLayout(new BorderLayout());

    // file options
    JPanel fileOptionsPanel = new JPanel();
    fileOptionsPanel.setBorder(padding);
    ButtonGroup btngroup = new ButtonGroup();
    fileOptionsPanel.setLayout(new GridLayout(haveFile && haveProject ? 3 : 2, 1));
    JLabel fileOptionsLabel = new JLabel("Files to process:");
    fileOptionsLabel.setFont(headingFont);
    fileOptionsPanel.add(fileOptionsLabel);

    if (haveFile) {
      JRadioButton rbfile = new JRadioButton("Process current file: " + savedFilename, !haveProject);
      btngroup.add(rbfile);
      rbfile.addItemListener(
        new ItemListener() {
          public void itemStateChanged(ItemEvent ev) {
            selectedAll = false;
          }
        });
      fileOptionsPanel.add(rbfile);
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
      fileOptionsPanel.add(rball);
    }

    outerPanel.add(fileOptionsPanel, BorderLayout.NORTH);

    // rules
    JPanel rulesPanel = new JPanel();
    rulesPanel.setBorder(padding);
    outerPanel.add(rulesPanel, BorderLayout.CENTER);
    Properties props = new Properties(System.getProperties());
    java.io.InputStream in = null;
    try {
      in = this.getClass().getResourceAsStream(PROPERTIES_FILE);
      props.load(in);
    } catch (Exception ex) {
      savedGel.showMessage("Error getting rulesets file (" + PROPERTIES_FILE
        + "): " + ex.getMessage());
      return;
    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (Exception ex1) { }
      }
    }
    String files = props.getProperty("rulesets.filenames");
    StringTokenizer st = new StringTokenizer(files, ",");
    int size = st.countTokens();
    rulesPanel.setLayout(new GridLayout(size + 1, 1));
    JLabel rulesLabel = new JLabel("Rulesets to use:");
    rulesLabel.setFont(headingFont);
    rulesPanel.add(rulesLabel);
    int index = 0;
    rulesetFilenames = new String[size];
    rulesetInuse = new boolean[size];
    rulesetChecks = new JCheckBox[size];
    while (st.hasMoreTokens()) {
      String label = st.nextToken();
      boolean initialValue = (DEFAULT_RULES.indexOf(label) != -1);
      JCheckBox jb = new JCheckBox(label, initialValue);
      rulesPanel.add(jb);
      jb.setToolTipText(getDescription(label));
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
    processButton.setMnemonic('p');
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
    cancelButton.setMnemonic('c');
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

  private static final String START_TOKEN = "<description>";
  private static final String END_TOKEN = "</description>";

  // simplistic XML parsing routine - only one field of interest
  private String getDescription(String rulesFilename) {
    if (descriptions.containsKey(rulesFilename)) {
      return descriptions.getProperty(rulesFilename);
    }
    BufferedReader br = null;
    try {
      int endPosn = 0;
      int startPosn;
      br = new BufferedReader(
        new java.io.InputStreamReader(
        this.getClass().getClassLoader().getResourceAsStream(rulesFilename)));
      StringBuffer sb = new StringBuffer();
      String line;
      boolean found = false;
      while (true) {
        line = br.readLine();
        if (line == null) {
          break;
        }
        sb.append(line);
        sb.append(" \n");
        endPosn = line.indexOf(END_TOKEN);
        if (endPosn != -1) {
          found = true;
          break;
        }
      }
      if (!found) {
        return rulesFilename;
      }
      String lines = sb.toString();
      startPosn = lines.indexOf(START_TOKEN);
      endPosn = lines.indexOf(END_TOKEN);
      if (startPosn == -1) {
        return rulesFilename;
      }
      return (lines.substring(startPosn + START_TOKEN.length(), endPosn).trim());
    } catch (Exception ex) {
      return rulesFilename;
    } finally {
      try {
        if (br != null) {
          br.close();
        }
      } catch (Exception ignore) { }
    }
  }

}
/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.util.designer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.SourceType;
import net.sourceforge.pmd.TargetJDK1_3;
import net.sourceforge.pmd.TargetJDK1_4;
import net.sourceforge.pmd.TargetJDK1_5;
import net.sourceforge.pmd.util.StringUtil;
import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.ast.ParseException;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.jaxen.DocumentNavigator;
import net.sourceforge.pmd.jaxen.MatchesFunction;
import net.sourceforge.pmd.jsp.ast.JspCharStream;
import net.sourceforge.pmd.jsp.ast.JspParser;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.jaxen.BaseXPath;
import org.jaxen.JaxenException;
import org.jaxen.XPath;

public class Designer implements ClipboardOwner {

	private static final char LABEL_IMAGE_SEPARATOR = ':';
	private static final Color IMAGE_TEXT_COLOR = Color.BLUE;

	private interface Parser { public SimpleNode parse(StringReader sr); };

	private static final Parser jdkParser1_3 = new Parser() {
		public SimpleNode parse(StringReader sr) { return (new TargetJDK1_3()).createParser(sr).CompilationUnit(); };
	};
	
	private static final Parser jdkParser1_4 = new Parser() {
		public SimpleNode parse(StringReader sr) { return (new TargetJDK1_4()).createParser(sr).CompilationUnit(); };
	};
	
	private static final Parser jdkParser1_5 = new Parser() {
		public SimpleNode parse(StringReader sr) { return (new TargetJDK1_5()).createParser(sr).CompilationUnit(); };
	};
	
	private static final Parser jspParser = new Parser() {
		public SimpleNode parse(StringReader sr) { return (new JspParser(new JspCharStream(sr))).CompilationUnit(); };
	};
	
	private static final Object[][] sourceTypeSets = new Object[][] {
		{ "JDK 1.3", SourceType.JAVA_13, jdkParser1_3 },
		{ "JDK 1.4", SourceType.JAVA_14, jdkParser1_4 },
		{ "JDK 1.5", SourceType.JAVA_15, jdkParser1_5 },
		{ "JSP", 	 SourceType.JSP, 	 jspParser }
		};
	
	private static final int defaultSourceTypeSelectionIndex = 1; // JDK 1.4
	

    private SimpleNode getCompilationUnit() {
    	    	
    	Parser parser = (Parser)sourceTypeSets[selectedSourceTypeIndex()][2];
    	return parser.parse(new StringReader(codeEditorPane.getText()));
    }
    
    private SourceType getSourceType() {
    	
    	return (SourceType)sourceTypeSets[selectedSourceTypeIndex()][1];
    }
    
    private int selectedSourceTypeIndex() {
    	for (int i=0; i<sourceTypeMenuItems.length; i++) {
    		if (sourceTypeMenuItems[i].isSelected()) return i;
    	}
    	throw new RuntimeException("Initial default source type not specified");
    }
    
    private class ExceptionNode implements TreeNode {

    	private Object 			item;    	
    	private ExceptionNode[] kids;
    	
    	public ExceptionNode(Object theItem) {
    		item = theItem;
    		
    		if (item instanceof ParseException) createKids();
    	}
    	
    	// each line in the error message becomes a separate tree node
    	private void createKids() {
    		    		
    		String message = ((ParseException)item).getMessage();    		
            String[] lines = StringUtil.substringsOf(message, PMD.EOL);

			kids = new ExceptionNode[lines.length];
			for (int i=0; i<lines.length; i++) {
				kids[i] = new ExceptionNode(lines[i]);
			};
    	}
    	
		public int getChildCount() { return kids == null ? 0 : kids.length; }
		public boolean getAllowsChildren() {return false; }
		public boolean isLeaf() { return kids == null; }
		public TreeNode getParent() { return null; }
		public TreeNode getChildAt(int childIndex) { return kids[childIndex]; }
		public String label() {	return item.toString();	}
		
		public Enumeration children() {
			Enumeration e = new Enumeration() {
				int i = 0;
				public boolean hasMoreElements() { 
					return kids != null && i < kids.length;
				}

				public Object nextElement() { return kids[i++]; }
				};
			return e;
		}
		
		public int getIndex(TreeNode node) {
			for (int i=0; i<kids.length; i++) {
				if (kids[i] == node) return i;
			}
			return -1;
		}
    }
    
    // Tree node that wraps the AST node for the tree widget and
    // any possible children they may have.
    private class ASTTreeNode implements TreeNode {

    	private Node 			node;
    	private ASTTreeNode 	parent;
    	private ASTTreeNode[] 	kids;
    	
    	public ASTTreeNode(Node theNode) {
    		node = theNode;
    		
    		Node prnt = node.jjtGetParent();
    		if (prnt != null) parent = new ASTTreeNode(prnt);    		
    	}
    	
		public int getChildCount() { return node.jjtGetNumChildren(); }
		public boolean getAllowsChildren() { return false;	}
		public boolean isLeaf() { return node.jjtGetNumChildren() == 0;	}
		public TreeNode getParent() { return parent; }
		
		public Enumeration children() {
			
			if (getChildCount() > 0) getChildAt(0);	// force it to build kids
			
			Enumeration e = new Enumeration() {
				int i = 0;
				public boolean hasMoreElements() { 
					return kids != null && i < kids.length;
				}
				public Object nextElement() { return kids[i++]; }
				};
			return e;
		}

		public TreeNode getChildAt(int childIndex) {
			
			if (kids == null) {
				kids = new ASTTreeNode[node.jjtGetNumChildren()];
    			for (int i=0; i<kids.length; i++) {
    				kids[i] = new ASTTreeNode(node.jjtGetChild(i));
    				}
				}			
			return kids[childIndex];
		}

		public int getIndex(TreeNode node) {

			for (int i=0; i<kids.length; i++) {
				if (kids[i] == node) return i;
			}
			return -1;
		}
    	
		public String label() {
			if (node instanceof SimpleNode) {
				SimpleNode sn = (SimpleNode)node;
				if (sn.getImage() == null) return node.toString();
				return node.toString() + LABEL_IMAGE_SEPARATOR + sn.getImage();
			}
			return node.toString();
		}
    }
    
    // Create our own renderer to ensure we don't show the default icon
    // and render any node image data in a different colour to hightlight 
    // it.
    
    private class ASTCellRenderer extends DefaultTreeCellRenderer {
    	
    	private ASTTreeNode node;

    	public Icon getIcon() { return null; };
    	
    	public Component getTreeCellRendererComponent(JTree tree, Object value,	boolean sel,boolean expanded,boolean leaf, int row,  boolean hasFocus) {

    		if (value instanceof ASTTreeNode) {
    			node = (ASTTreeNode)value;
    		}
    		return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
    	}
    	
    	// overwrite the image data (if present) in a different colour
    	public void paint(Graphics g) {
    		
    		super.paint(g);
    		
    		if (node == null) return;
    		
    		String text = node.label();
    		int separatorPos = text.indexOf(LABEL_IMAGE_SEPARATOR);
    		if (separatorPos < 0) return;
    		    		
    		String label = text.substring(0, separatorPos+1);
    		String image = text.substring(separatorPos+1);

    		FontMetrics fm = g.getFontMetrics();
    		int width = SwingUtilities.computeStringWidth(fm, label);
    		
    		g.setColor(IMAGE_TEXT_COLOR);
    		g.drawString(image, width, fm.getMaxAscent());
    	}
    }
    
    // Special tree variant that knows how to retrieve node labels and
    // provides the ability to expand all nodes at once.
    
    private class ASTTreeWidget extends JTree {
    	
    	public ASTTreeWidget(Vector items) {
    		super(items);
    	}
    	
        public String convertValueToText(Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        	if (value == null) return "";
        	if (value instanceof ASTTreeNode) {
        		return ((ASTTreeNode)value).label();
        	}        	
        	if (value instanceof ExceptionNode) {
        		return ((ExceptionNode)value).label();
        	}        	
        	return value.toString();
    	}
        
        public void expandAll(boolean expand) {
            TreeNode root = (TreeNode)getModel().getRoot();
            expandAll(new TreePath(root), expand);
        }
        
        private void expandAll(TreePath parent, boolean expand) {
            // Traverse children
            TreeNode node = (TreeNode)parent.getLastPathComponent();
            if (node.getChildCount() >= 0) {
                for (Enumeration e=node.children(); e.hasMoreElements(); ) {
                    TreeNode n = (TreeNode)e.nextElement();
                    TreePath path = parent.pathByAddingChild(n);
                    expandAll(path, expand);
                }
            }
        
            if (expand) {
                expandPath(parent);
            } else {
                collapsePath(parent);
            }
        }        
    }
    
    private void loadTreeData(TreeNode rootNode) {
    	astWidget.setModel(new DefaultTreeModel(rootNode));
    	astWidget.expandAll(true);
    }
    
    private class ShowListener implements ActionListener {
        public void actionPerformed(ActionEvent ae) {
            MyPrintStream ps = new MyPrintStream();
            System.setOut(ps);
            TreeNode tn;
            try {
                SimpleNode lastCompilationUnit = getCompilationUnit();
                tn = new ASTTreeNode(lastCompilationUnit);
            } catch (ParseException pe) {            	
            	tn = new ExceptionNode(pe);
            	}
            
            loadTreeData(tn);
        }
    }

    private class DFAListener implements ActionListener {
        public void actionPerformed(ActionEvent ae) {

           DFAGraphRule dfaGraphRule = new DFAGraphRule();
           RuleSet rs = new RuleSet();
           SourceType sourceType = getSourceType();
           if (!sourceType.equals(SourceType.JSP)){
               rs.addRule(dfaGraphRule);
           }
           RuleContext ctx = new RuleContext();
           ctx.setSourceCodeFilename("[no filename]");
           StringReader reader = new StringReader(codeEditorPane.getText());
           PMD pmd = new PMD();
           pmd.setJavaVersion(sourceType);
           
           try {
                pmd.processFile(reader, rs, ctx);
//           } catch (PMDException pmde) {
//               loadTreeData(new ExceptionNode(pmde));
           } catch (Exception e) {
               e.printStackTrace();
           		}
           
           List methods = dfaGraphRule.getMethods();
           if (methods != null && !methods.isEmpty()) {
               dfaPanel.resetTo(methods, codeEditorPane);
               dfaPanel.repaint();
           }
        }
    }

    private class XPathListener implements ActionListener {
        public void actionPerformed(ActionEvent ae) {
            xpathResults.clear();
            if (xpathQueryArea.getText().length() == 0) {
                xpathResults.addElement("XPath query field is empty");
                xpathResultList.repaint();
                codeEditorPane.requestFocus();
                return;
            }
            SimpleNode c = getCompilationUnit();
            try {
                XPath xpath = new BaseXPath(xpathQueryArea.getText(), new DocumentNavigator());
                for (Iterator iter = xpath.selectNodes(c).iterator(); iter.hasNext();) {
                    StringBuffer sb = new StringBuffer();
                    Object obj = iter.next();
                    if (obj instanceof String) {
                        System.out.println("Result was a string: " + ((String) obj));
                    } else if (!(obj instanceof Boolean)) {
                        // if it's a Boolean and it's 'false', what does that mean?
                        SimpleNode node = (SimpleNode) obj;
                        String name = node.getClass().getName().substring(node.getClass().getName().lastIndexOf('.') + 1);
                        String line = " at line " + String.valueOf(node.getBeginLine());
                        sb.append(name).append(line).append(PMD.EOL);
                        xpathResults.addElement(sb.toString().trim());
                    }
                }
                if (xpathResults.isEmpty()) {
                    xpathResults.addElement("No matching nodes " + System.currentTimeMillis());
                }
            } catch (ParseException pe) {
                xpathResults.addElement(pe.fillInStackTrace().getMessage());
            } catch (JaxenException je) {
                xpathResults.addElement(je.fillInStackTrace().getMessage());
            }
            xpathResultList.repaint();
            xpathQueryArea.requestFocus();
        }
    }

    private final CodeEditorTextPane codeEditorPane = new CodeEditorTextPane();
    private final ASTTreeWidget astWidget			= new ASTTreeWidget(new Vector());
    private DefaultListModel xpathResults			= new DefaultListModel();
    private final JList xpathResultList				= new JList(xpathResults);
    private final JTextArea xpathQueryArea			= new JTextArea(15, 30);
    private final JFrame frame 						= new JFrame("PMD Rule Designer");
    private final DFAPanel dfaPanel					= new DFAPanel();
    private final JRadioButtonMenuItem[] sourceTypeMenuItems = new JRadioButtonMenuItem[sourceTypeSets.length];
    
    public Designer() {
        MatchesFunction.registerSelfInSimpleContext();

        xpathQueryArea.setFont(new Font("Verdana", Font.PLAIN, 16));
        JSplitPane controlSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(codeEditorPane), createXPathQueryPanel());
        JSplitPane resultsSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createASTPanel(), createXPathResultPanel());

        JTabbedPane tabbed = new JTabbedPane();
        tabbed.addTab("Abstract Syntax Tree / XPath", resultsSplitPane);
        tabbed.addTab("Data Flow Analysis", dfaPanel);
        try {
            // Remove when minimal runtime support is >= JDK 1.4
            Method setMnemonicAt = JTabbedPane.class.getMethod("setMnemonicAt", new Class[]{Integer.TYPE, Integer.TYPE});
            if (setMnemonicAt != null) {
                //        // Compatible with >= JDK 1.4
                //        tabbed.setMnemonicAt(0, KeyEvent.VK_A);
                //        tabbed.setMnemonicAt(1, KeyEvent.VK_D);
                setMnemonicAt.invoke(tabbed, new Object[]{new Integer(0), new Integer(KeyEvent.VK_A)});
                setMnemonicAt.invoke(tabbed, new Object[]{new Integer(1), new Integer(KeyEvent.VK_D)});
            }
        } catch (NoSuchMethodException nsme) { // Runtime is < JDK 1.4
        } catch (IllegalAccessException e) { // Runtime is >= JDK 1.4 but there was an error accessing the function
            e.printStackTrace();
            throw new InternalError("Runtime reports to be >= JDK 1.4 yet String.split(java.lang.String) is broken.");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            throw new InternalError("Runtime reports to be >= JDK 1.4 yet String.split(java.lang.String) is broken.");
        } catch (InvocationTargetException e) { // Runtime is >= JDK 1.4 but there was an error accessing the function
            e.printStackTrace();
            throw new InternalError("Runtime reports to be >= JDK 1.4 yet String.split(java.lang.String) is broken.");
        }

        JSplitPane containerSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, controlSplitPane, tabbed);
        containerSplitPane.setContinuousLayout(true);

        JMenuBar menuBar = createMenuBar();
        frame.setJMenuBar(menuBar);
        frame.getContentPane().add(containerSplitPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenHeight = screenSize.height;
        int screenWidth = screenSize.width;
        
        frame.setSize(screenHeight - (screenHeight / 4), screenHeight - (screenHeight / 4));
        frame.setLocation((screenWidth / 2) - frame.getWidth() / 2, (screenHeight / 2) - frame.getHeight() / 2);
        frame.setVisible(true);
        frame.pack();
        frame.show();
        resultsSplitPane.setDividerLocation(resultsSplitPane.getMaximumDividerLocation() - (resultsSplitPane.getMaximumDividerLocation() / 2));
        //containerSplitPane.setDividerLocation(containerSplitPane.getMaximumDividerLocation() / 2);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("JDK");
        ButtonGroup group = new ButtonGroup();
                
        for (int i=0; i<sourceTypeSets.length; i++) {
        	JRadioButtonMenuItem button = new JRadioButtonMenuItem(sourceTypeSets[i][0].toString());
        	sourceTypeMenuItems[i] = button;
        	group.add(button);
        	menu.add(button);
        }
        sourceTypeMenuItems[defaultSourceTypeSelectionIndex].setSelected(true);
        menuBar.add(menu);

        JMenu actionsMenu = new JMenu("Actions");
        JMenuItem copyXMLItem = new JMenuItem("Copy xml to clipboard");
        copyXMLItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                copyXmlToClipboard();
            }
        });
        actionsMenu.add(copyXMLItem);
        JMenuItem createRuleXMLItem = new JMenuItem("Create rule XML");
        createRuleXMLItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createRuleXML();
            }
        });
        actionsMenu.add(createRuleXMLItem);
        menuBar.add(actionsMenu);        
        
        return menuBar;
    }

    private void createRuleXML() {
        JPanel rulenamePanel = new JPanel();
        rulenamePanel.setLayout(new FlowLayout());
        rulenamePanel.add(new JLabel("Rule name"));
        final JTextField rulenameField = new JTextField(30);
        rulenamePanel.add(rulenameField);
        JPanel rulemsgPanel = new JPanel();
        rulemsgPanel.setLayout(new FlowLayout());
        rulemsgPanel.add(new JLabel("Rule msg"));
        final JTextField rulemsgField = new JTextField(60);
        rulemsgPanel.add(rulemsgField);
        JPanel ruledescPanel = new JPanel();
        ruledescPanel.setLayout(new FlowLayout());
        ruledescPanel.add(new JLabel("Rule desc"));
        final JTextField ruledescField = new JTextField(60);
        ruledescPanel.add(ruledescField);
        JPanel ruleXMLPanel = new JPanel();
        final JTextArea ruleXMLArea = new JTextArea(30, 50);
        ruleXMLPanel.add(ruleXMLArea);
        JButton go = new JButton("Create rule XML");
        go.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                StringBuffer sb = new StringBuffer();
                sb.append("<rule  name=\"" + rulenameField.getText() + "\"" + PMD.EOL);
                sb.append("  message=\"" + rulemsgField.getText() + "\"" + PMD.EOL);
                sb.append("  class=\"" + (xpathQueryArea.getText().length() == 0 ? "" : "net.sourceforge.pmd.rules.XPathRule") + "\">" + PMD.EOL);
                sb.append("  <description>" + PMD.EOL);
                sb.append("  " + ruledescField.getText() + PMD.EOL);
                sb.append("  </description>" + PMD.EOL);
                if (xpathQueryArea.getText().length() != 0) {
                    sb.append("  <properties>" + PMD.EOL);
                    sb.append("    <property name=\"xpath\">" + PMD.EOL);
                    sb.append("    <value>" + PMD.EOL);
                    sb.append("<![CDATA[" + PMD.EOL);
                    sb.append(xpathQueryArea.getText() + PMD.EOL);
                    sb.append("]]>" + PMD.EOL);
                    sb.append("    </value>" + PMD.EOL);
                    sb.append("    </property>" + PMD.EOL);
                    sb.append("  </properties>" + PMD.EOL);
                }
                sb.append("  <priority>3</priority>" + PMD.EOL);
                sb.append("  <example>" + PMD.EOL);
                sb.append("<![CDATA[" + PMD.EOL);
                sb.append(codeEditorPane.getText());
                sb.append("]]>" + PMD.EOL);
                sb.append("  </example>" + PMD.EOL);
                sb.append("</rule>" + PMD.EOL);

                ruleXMLArea.setText(sb.toString());
            }
        });

        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BorderLayout());
        fieldsPanel.add(rulenamePanel, BorderLayout.NORTH);
        fieldsPanel.add(rulemsgPanel, BorderLayout.CENTER);
        fieldsPanel.add(ruledescPanel, BorderLayout.SOUTH);

        JPanel fieldBtnPanel = new JPanel();
        fieldBtnPanel.setLayout(new BorderLayout());
        fieldBtnPanel.add(fieldsPanel, BorderLayout.NORTH);
        fieldBtnPanel.add(go, BorderLayout.SOUTH);

        JPanel outer = new JPanel(new BorderLayout());
        outer.add(fieldBtnPanel, BorderLayout.NORTH);
        outer.add(ruleXMLPanel, BorderLayout.SOUTH);

        JDialog d = new JDialog(frame);
        d.setSize(200, 300);
        d.getContentPane().add(outer);
        int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
        int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        d.setLocation((screenWidth / 2) - frame.getWidth() / 2, (screenHeight / 2) - frame.getHeight() / 2);
        d.setVisible(true);
        d.pack();
        d.show();
    }

    private JComponent createASTPanel() {
    	astWidget.setCellRenderer(new ASTCellRenderer());    	
        return new JScrollPane(astWidget);
    }
    
    private JComponent createXPathResultPanel() {
        xpathResults.addElement("No results yet");
        xpathResultList.setBorder(BorderFactory.createLineBorder(Color.black));
        xpathResultList.setFixedCellWidth(300);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.getViewport().setView(xpathResultList);
        return scrollPane;
    }

    private JPanel createXPathQueryPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        xpathQueryArea.setBorder(BorderFactory.createLineBorder(Color.black));
        JScrollPane scrollPane = new JScrollPane(xpathQueryArea);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        final JButton b = createGoButton();

        p.add(new JLabel("XPath Query (if any)"), BorderLayout.NORTH);
        p.add(scrollPane, BorderLayout.CENTER);
        p.add(b, BorderLayout.SOUTH);

        return p;
    }

    private JButton createGoButton() {
        JButton b = new JButton("Go");
        b.setMnemonic('g');
        b.addActionListener(new ShowListener());
        b.addActionListener(codeEditorPane);
        b.addActionListener(new XPathListener());
        b.addActionListener(new DFAListener());
        return b;
    }

    public static void main(String[] args) {
        new Designer();
    }

    private final void copyXmlToClipboard() {
        if (codeEditorPane.getText() != null && codeEditorPane.getText().trim().length() > 0) {
            String xml = "";
            SimpleNode cu = getCompilationUnit();
            if (cu != null) {
                try {
                    xml = getXmlString(cu);
                } catch (IOException e) {
                    e.printStackTrace();
                    xml = "Error trying to construct XML representation";
                }
            }
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(xml), this);
        }
    }

    /**
     * Returns an unformatted xml string (without the declaration)
     *
     * @param node
     * @return String
     * @throws java.io.IOException
     */
    private String getXmlString(SimpleNode node) throws IOException {
        StringWriter writer = new StringWriter();
        XMLSerializer xmlSerializer = new XMLSerializer(writer, new OutputFormat("XML", "UTF-8", true));
        xmlSerializer.asDOMSerializer();
        xmlSerializer.serialize(node.asXml());
        return writer.toString();
    }

    public void lostOwnership(Clipboard clipboard, Transferable contents) {
    }
}


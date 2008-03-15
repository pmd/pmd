/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.util.designer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
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
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.JTextComponent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.SourceType;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.AccessNode;
import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.ast.ParseException;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.jaxen.DocumentNavigator;
import net.sourceforge.pmd.jaxen.MatchesFunction;
import net.sourceforge.pmd.jaxen.TypeOfFunction;
import net.sourceforge.pmd.parsers.Parser;
import net.sourceforge.pmd.sourcetypehandlers.SourceTypeHandler;
import net.sourceforge.pmd.sourcetypehandlers.SourceTypeHandlerBroker;
import net.sourceforge.pmd.symboltable.ClassNameDeclaration;
import net.sourceforge.pmd.symboltable.ClassScope;
import net.sourceforge.pmd.symboltable.LocalScope;
import net.sourceforge.pmd.symboltable.MethodNameDeclaration;
import net.sourceforge.pmd.symboltable.MethodScope;
import net.sourceforge.pmd.symboltable.NameOccurrence;
import net.sourceforge.pmd.symboltable.Scope;
import net.sourceforge.pmd.symboltable.SourceFileScope;
import net.sourceforge.pmd.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.util.NumericConstants;
import net.sourceforge.pmd.util.StringUtil;

import org.jaxen.BaseXPath;
import org.jaxen.JaxenException;
import org.jaxen.XPath;

public class Designer implements ClipboardOwner {

	private static final char LABEL_IMAGE_SEPARATOR = ':';

	private static final Object[][] sourceTypeSets = new Object[][] {
		{ "JDK 1.3", SourceType.JAVA_13 },
		{ "JDK 1.4", SourceType.JAVA_14 },
		{ "JDK 1.5", SourceType.JAVA_15 },
		{ "JDK 1.6", SourceType.JAVA_16 },
		{ "JDK 1.7", SourceType.JAVA_17 },
		{ "JSP", 	 SourceType.JSP }
		};

	private static final int defaultSourceTypeSelectionIndex = 2; // Java 1.5

    private SimpleNode getCompilationUnit() {
    	SourceTypeHandler handler = SourceTypeHandlerBroker.getVisitorsFactoryForSourceType(getSourceType());
    	Parser parser = handler.getParser();
    	SimpleNode simpleNode = (SimpleNode)parser.parse(new StringReader(codeEditorPane.getText()));
    	handler.getSymbolFacade().start(simpleNode);
    	handler.getTypeResolutionFacade(null).start(simpleNode);
    	return simpleNode;
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
			}
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

		public Scope getScope() {
    		if (node instanceof SimpleNode)
    			return ((SimpleNode)node).getScope();
    		return null;
    	}

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
                if (sn.getLabel() != null) {
                    return node.toString() + LABEL_IMAGE_SEPARATOR + sn.getLabel();
                }
				if (sn.getImage() == null) {
                    return node.toString();
                }
				return node.toString() + LABEL_IMAGE_SEPARATOR + sn.getImage();
			}
			return node.toString();
		}

		public String getToolTipText() {
			String tooltip = "";
		    if (node instanceof SimpleNode) {
		        SimpleNode sn = (SimpleNode)node;
		        tooltip = "Line: " + sn.getBeginLine() + " Column: " + sn.getBeginColumn();
		    }

		    if (node instanceof AccessNode)
		    {
		    	AccessNode accessNode = (AccessNode)node;
		    	if ( ! "".equals(tooltip))
		    		tooltip += ",";
		    	tooltip += accessNode.isAbstract() ? " Abstract" : "";
		    	tooltip += accessNode.isStatic() ? " Static" : "";
		    	tooltip += accessNode.isFinal() ? " Final" : "";
		    	tooltip += accessNode.isNative() ? " Native" : "";
		    	tooltip += accessNode.isPrivate() ? " Private" : "";
		    	tooltip += accessNode.isSynchronized() ? " Synchronised" : "";
		    	tooltip += accessNode.isTransient() ? " Transient" : "";
		    	tooltip += accessNode.isVolatile() ? " Volatile" : "";
		    	tooltip += accessNode.isStrictfp() ? " Strictfp" : "";
		    }
		    return tooltip;
		}
    }

    private TreeCellRenderer createNoImageTreeCellRenderer() {
    	DefaultTreeCellRenderer treeCellRenderer = new DefaultTreeCellRenderer();
    	treeCellRenderer.setLeafIcon(null);
    	treeCellRenderer.setOpenIcon(null);
    	treeCellRenderer.setClosedIcon(null);
    	return treeCellRenderer;
    }

    // Special tree variant that knows how to retrieve node labels and
    // provides the ability to expand all nodes at once.
    private class TreeWidget extends JTree {

		private static final long serialVersionUID = 1L;

		public TreeWidget(Object[] items) {
    		super(items);
            setToolTipText("");
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

        public String getToolTipText(MouseEvent e) {
            if (getRowForLocation(e.getX(), e.getY()) == -1) return null;
            TreePath curPath = getPathForLocation(e.getX(), e.getY());
            if (curPath.getLastPathComponent() instanceof ASTTreeNode) {
            	return ((ASTTreeNode)curPath.getLastPathComponent()).getToolTipText();
            } else {
            	return super.getToolTipText(e);
            }
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

    private void loadASTTreeData(TreeNode rootNode) {
    	astTreeWidget.setModel(new DefaultTreeModel(rootNode));
    	astTreeWidget.expandAll(true);
    }

    private void loadSymbolTableTreeData(TreeNode rootNode) {
    	if (rootNode != null) {
	    	symbolTableTreeWidget.setModel(new DefaultTreeModel(rootNode));
	    	symbolTableTreeWidget.expandAll(true);
    	} else {
    		symbolTableTreeWidget.setModel(null);
    	}
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

            loadASTTreeData(tn);
            loadSymbolTableTreeData(null);
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

           List<ASTMethodDeclaration> methods = dfaGraphRule.getMethods();
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
                xpathResults.addElement("XPath query field is empty.");
                xpathResultList.repaint();
                codeEditorPane.requestFocus();
                return;
            }
            SimpleNode c = getCompilationUnit();
            try {
                XPath xpath = new BaseXPath(xpathQueryArea.getText(), new DocumentNavigator());
                for (Iterator iter = xpath.selectNodes(c).iterator(); iter.hasNext();) {
                    Object obj = iter.next();
                    if (obj instanceof String) {
                        System.out.println("Result was a string: " + ((String) obj));
                    } else if (!(obj instanceof Boolean)) {
                        // if it's a Boolean and it's 'false', what does that mean?
                        xpathResults.addElement(obj);
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

    private class SymbolTableListener implements TreeSelectionListener {
    	public void valueChanged(TreeSelectionEvent e) {
    		if (e.getNewLeadSelectionPath() != null) {
    			ASTTreeNode astTreeNode = (ASTTreeNode)e.getNewLeadSelectionPath().getLastPathComponent();

    			DefaultMutableTreeNode symbolTableTreeNode = new DefaultMutableTreeNode();
    			DefaultMutableTreeNode selectedAstTreeNode = new DefaultMutableTreeNode("AST Node: " + astTreeNode.label());
    			symbolTableTreeNode.add(selectedAstTreeNode);

	    		List<Scope> scopes = new ArrayList<Scope>();
	    		Scope scope = astTreeNode.getScope();
	    		while (scope != null)
	    		{
	    			scopes.add(scope);
	    			scope = scope.getParent();
	    		}
	    		Collections.reverse(scopes);
	    		for (int i = 0; i < scopes.size(); i++) {
	    			scope = scopes.get(i);
	    			DefaultMutableTreeNode scopeTreeNode =  new DefaultMutableTreeNode("Scope: " + scope.getClass().getSimpleName());
	    			selectedAstTreeNode.add(scopeTreeNode);
	    			if (!(scope instanceof MethodScope || scope instanceof LocalScope)) {
	    				if (!scope.getClassDeclarations().isEmpty()) {
				    		for (ClassNameDeclaration classNameDeclaration: scope.getClassDeclarations().keySet()) {
				    			DefaultMutableTreeNode classNameDeclarationTreeNode = new DefaultMutableTreeNode("Class name declaration: " + classNameDeclaration);
				    			scopeTreeNode.add(classNameDeclarationTreeNode);
				    			for (NameOccurrence nameOccurrence: scope.getClassDeclarations().get(classNameDeclaration)) {
					    			DefaultMutableTreeNode nameOccurenceTreeNode = new DefaultMutableTreeNode("Name occurrence: " + nameOccurrence);
					    			classNameDeclarationTreeNode.add(nameOccurenceTreeNode);
				    			}
				    		}
	    				}
	    			}
	    			if (scope instanceof ClassScope) {
	    				ClassScope classScope = (ClassScope)scope;
	    				if (!classScope.getMethodDeclarations().isEmpty()) {
				    		for (MethodNameDeclaration methodNameDeclaration: classScope.getMethodDeclarations().keySet()) {
				    			DefaultMutableTreeNode methodNameDeclarationTreeNode = new DefaultMutableTreeNode("Method name declaration: " + methodNameDeclaration);
				    			scopeTreeNode.add(methodNameDeclarationTreeNode);
				    			for (NameOccurrence nameOccurrence: classScope.getMethodDeclarations().get(methodNameDeclaration)) {
					    			DefaultMutableTreeNode nameOccurenceTreeNode = new DefaultMutableTreeNode("Name occurrence: " + nameOccurrence);
					    			methodNameDeclarationTreeNode.add(nameOccurenceTreeNode);
				    			}
				    		}
	    				}
	    			}
	    			if (!(scope instanceof SourceFileScope)) {
	    				if (!scope.getVariableDeclarations().isEmpty()) {
				    		for (VariableNameDeclaration variableNameDeclaration: scope.getVariableDeclarations().keySet()) {
				    			DefaultMutableTreeNode variableNameDeclarationTreeNode = new DefaultMutableTreeNode("Variable name declaration: " + variableNameDeclaration);
				    			scopeTreeNode.add(variableNameDeclarationTreeNode);
				    			for (NameOccurrence nameOccurrence: scope.getVariableDeclarations().get(variableNameDeclaration)) {
					    			DefaultMutableTreeNode nameOccurenceTreeNode = new DefaultMutableTreeNode("Name occurrence: " + nameOccurrence);
					    			variableNameDeclarationTreeNode.add(nameOccurenceTreeNode);
				    			}
				    		}
	    				}
	    			}
	    		}
	    		loadSymbolTableTreeData(symbolTableTreeNode);
    		}
        }
    }

    private class CodeHighlightListener implements TreeSelectionListener {
        public void valueChanged(TreeSelectionEvent e) {
            if (e.getNewLeadSelectionPath() != null) {
                ASTTreeNode selected = (ASTTreeNode)e.getNewLeadSelectionPath().getLastPathComponent();
                if (selected != null && selected.node instanceof SimpleNode) {
                    SimpleNode node = (SimpleNode) selected.node;

                    codeEditorPane.select(node);
                }
            }
        }
    }

    private class ASTListCellRenderer extends JLabel implements ListCellRenderer {
        private static final long serialVersionUID = 1L;

        public Component getListCellRendererComponent(
                JList list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {

            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            String text;
            if (value instanceof SimpleNode) {
                SimpleNode node = (SimpleNode) value;
                StringBuffer sb = new StringBuffer();
                String name = node.getClass().getName().substring(node.getClass().getName().lastIndexOf('.') + 1);
                sb.append(name)
                    .append(" at line ").append(node.getBeginLine())
                    .append(" column ").append(node.getBeginColumn())
                    .append(PMD.EOL);
                text = sb.toString();
            } else {
                text = value.toString();
            }
            setText(text);
            return this;
        }
    }

    private class ASTSelectionListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) {
            ListSelectionModel lsm = (ListSelectionModel)e.getSource();
            if (!lsm.isSelectionEmpty()) {
                Object o = xpathResults.get(lsm.getMinSelectionIndex());
                if (o instanceof SimpleNode) {
                    codeEditorPane.select((SimpleNode) o);
                }
            }
        }
    }

	private boolean exitOnClose = true;
    private final CodeEditorTextPane codeEditorPane = new CodeEditorTextPane();
    private final TreeWidget astTreeWidget			= new TreeWidget(new Object[0]);
    private DefaultListModel xpathResults			= new DefaultListModel();
    private final JList xpathResultList				= new JList(xpathResults);
    private final JTextArea xpathQueryArea			= new JTextArea(15, 30);
    private final TreeWidget symbolTableTreeWidget	= new TreeWidget(new Object[0]);
    private final JFrame frame 						= new JFrame("PMD Rule Designer (v " + PMD.VERSION + ')');
    private final DFAPanel dfaPanel					= new DFAPanel();
    private final JRadioButtonMenuItem[] sourceTypeMenuItems = new JRadioButtonMenuItem[sourceTypeSets.length];

	public Designer(String[] args) {
		if (args.length > 0) {
			exitOnClose = !args[0].equals("-noexitonclose");
		}

		MatchesFunction.registerSelfInSimpleContext();
        TypeOfFunction.registerSelfInSimpleContext();

        xpathQueryArea.setFont(new Font("Verdana", Font.PLAIN, 16));
        JSplitPane controlSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createCodeEditorPanel(), createXPathQueryPanel());

        JSplitPane astAndSymbolTablePane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, createASTPanel(), createSymbolTableResultPanel());

        JSplitPane resultsSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, astAndSymbolTablePane, createXPathResultPanel());

        JTabbedPane tabbed = new JTabbedPane();
        tabbed.addTab("Abstract Syntax Tree / XPath / Symbol Table", resultsSplitPane);
        tabbed.addTab("Data Flow Analysis", dfaPanel);
        try {
            // Remove when minimal runtime support is >= JDK 1.4
            Method setMnemonicAt = JTabbedPane.class.getMethod("setMnemonicAt", new Class[]{Integer.TYPE, Integer.TYPE});
            if (setMnemonicAt != null) {
                //        // Compatible with >= JDK 1.4
                //        tabbed.setMnemonicAt(0, KeyEvent.VK_A);
                //        tabbed.setMnemonicAt(1, KeyEvent.VK_D);
                setMnemonicAt.invoke(tabbed, new Object[]{NumericConstants.ZERO, KeyEvent.VK_A});
                setMnemonicAt.invoke(tabbed, new Object[]{NumericConstants.ONE, KeyEvent.VK_D});
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
        frame.setDefaultCloseOperation(exitOnClose ? JFrame.EXIT_ON_CLOSE : JFrame.DISPOSE_ON_CLOSE);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenHeight = screenSize.height;
        int screenWidth = screenSize.width;

        frame.pack();
        frame.setSize((screenWidth*3/4),(screenHeight*3/4));
        frame.setLocation((screenWidth -frame.getWidth()) / 2, (screenHeight  - frame.getHeight()) / 2);
        frame.setVisible(true);
        int horozontalMiddleLocation = controlSplitPane.getMaximumDividerLocation() * 3 / 5;
        controlSplitPane.setDividerLocation(horozontalMiddleLocation);
        containerSplitPane.setDividerLocation(containerSplitPane.getMaximumDividerLocation() / 2);
        astAndSymbolTablePane.setDividerLocation(astAndSymbolTablePane.getMaximumDividerLocation()/3);
        resultsSplitPane.setDividerLocation(horozontalMiddleLocation);
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
    	CreateXMLRulePanel rulePanel = new CreateXMLRulePanel(xpathQueryArea, codeEditorPane);
    	JFrame xmlframe = new JFrame("Create XML Rule");
    	xmlframe.setContentPane(rulePanel);
    	xmlframe.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        xmlframe.setSize(new Dimension(600, 700));
        xmlframe.addComponentListener(new java.awt.event.ComponentAdapter() {
        	  public void componentResized(ComponentEvent e) {
        	    JFrame tmp = (JFrame)e.getSource();
        	    if (tmp.getWidth()<600 || tmp.getHeight()<700) {
        	      tmp.setSize(600, 700);
        	    }
        	  }
        	});
        int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
        int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        xmlframe.pack();
        xmlframe.setLocation((screenWidth - xmlframe.getWidth()) / 2, (screenHeight - xmlframe.getHeight()) / 2);
        xmlframe.setVisible(true);
    }

    private JComponent createCodeEditorPanel()
    {
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        codeEditorPane.setBorder(BorderFactory.createLineBorder(Color.black));
        makeTextComponentUndoable(codeEditorPane);

        p.add(new JLabel("Source code:"), BorderLayout.NORTH);
        p.add(new JScrollPane(codeEditorPane), BorderLayout.CENTER);

        return p;
    }

    private JComponent createASTPanel() {
    	astTreeWidget.setCellRenderer(createNoImageTreeCellRenderer());
    	TreeSelectionModel model = astTreeWidget.getSelectionModel();
    	model.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    	model.addTreeSelectionListener(new SymbolTableListener());
    	model.addTreeSelectionListener(new CodeHighlightListener());
        return new JScrollPane(astTreeWidget);
    }

    private JComponent createXPathResultPanel() {
        xpathResults.addElement("No XPath results yet, run an XPath Query first.");
        xpathResultList.setBorder(BorderFactory.createLineBorder(Color.black));
        xpathResultList.setFixedCellWidth(300);
        xpathResultList.setCellRenderer(new ASTListCellRenderer());
        xpathResultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        xpathResultList.getSelectionModel().addListSelectionListener(new ASTSelectionListener());
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.getViewport().setView(xpathResultList);
        return scrollPane;
    }

    private JPanel createXPathQueryPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        xpathQueryArea.setBorder(BorderFactory.createLineBorder(Color.black));
        makeTextComponentUndoable(xpathQueryArea);
        JScrollPane scrollPane = new JScrollPane(xpathQueryArea);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        final JButton b = createGoButton();

        p.add(new JLabel("XPath Query (if any):"), BorderLayout.NORTH);
        p.add(scrollPane, BorderLayout.CENTER);
        p.add(b, BorderLayout.SOUTH);

        return p;
    }

    private JComponent createSymbolTableResultPanel() {
    	symbolTableTreeWidget.setCellRenderer(createNoImageTreeCellRenderer());
        return new JScrollPane(symbolTableTreeWidget);
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

    private static void makeTextComponentUndoable(JTextComponent textConponent) {
        final UndoManager undoManager = new UndoManager();
        textConponent.getDocument().addUndoableEditListener(new UndoableEditListener() {
			     public void undoableEditHappened(
			       UndoableEditEvent evt) {
			         undoManager.addEdit(evt.getEdit());
			     }
  			 });
        ActionMap actionMap = textConponent.getActionMap();
        InputMap inputMap = textConponent.getInputMap();
        actionMap.put("Undo", new AbstractAction("Undo") {
		         public void actionPerformed(ActionEvent evt) {
		             try {
		                 if (undoManager.canUndo()) {
		                     undoManager.undo();
		                 }
		             } catch (CannotUndoException e) {
		             }
		         }
        	 });
        inputMap.put(KeyStroke.getKeyStroke("control Z"), "Undo");

        actionMap.put("Redo", new AbstractAction("Redo") {
			    public void actionPerformed(ActionEvent evt) {
			        try {
			            if (undoManager.canRedo()) {
			                undoManager.redo();
			            }
			        } catch (CannotRedoException e) {
			        }
			    }
            });
        inputMap.put(KeyStroke.getKeyStroke("control Y"), "Redo");
    }

    public static void main(String[] args) {
        new Designer(args);
    }

    private final void copyXmlToClipboard() {
        if (codeEditorPane.getText() != null && codeEditorPane.getText().trim().length() > 0) {
            String xml = "";
            SimpleNode cu = getCompilationUnit();
            if (cu != null) {
                try {
                    xml = getXmlString(cu);
                } catch (TransformerException e) {
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
     * @throws TransformerException if the XML cannot be converted to a string
     */
    private String getXmlString(SimpleNode node) throws TransformerException {
        StringWriter writer = new StringWriter();

        Source source = new DOMSource(node.asXml());
        Result result = new StreamResult(writer);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        try {
            transformerFactory.setAttribute("indent-number", 4);   //For java 5
        } catch (IllegalArgumentException e) {
            //Running on Java 1.4 which does not support this attribute
        }
        Transformer xformer = transformerFactory.newTransformer();
        xformer.setOutputProperty(OutputKeys.INDENT, "yes");
        xformer.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "4");   //For java 1.4
        xformer.transform(source, result);

        return writer.toString();
    }

    public void lostOwnership(Clipboard clipboard, Transferable contents) {
    }
}


package net.sourceforge.pmd.jedit;

//Imports
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.textarea.Selection;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.BorderLayout;
import java.util.List;
import java.util.ArrayList;
//End of Imports

/**
  *    A GUI Component to display Duplicate code.
  *
  *    @created 05 Apr 2003
  *    @author Jiger Patel
  *
  */

public class CPDDuplicateCodeViewer  extends JPanel
{
	JTree tree;
	DefaultTreeModel treeModel = new DefaultTreeModel(new DefaultMutableTreeNode("CPD Results",true));
	View view;

	public CPDDuplicateCodeViewer(View view)
	{
		this.view = view;
		setLayout(new BorderLayout());
		tree = new JTree(treeModel);
		tree.getSelectionModel().setSelectionMode
		(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addTreeSelectionListener(new TreeSelectionListener()
									  {
										  public void valueChanged(TreeSelectionEvent e)
										  {
											  DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
											  if (node != null)
											  {
												  //System.out.println("Node is " + node +" class "+ node.getClass());
												  if (node.isLeaf() && node instanceof Duplicate)
												  {
													  Duplicate duplicate = (Duplicate)node;
													  gotoDuplicate(duplicate);
													  //System.out.println("Got!! " + duplicate);
												  }
											  }
										  }
									  });


		add(new JScrollPane(tree));

	}//End of CPDDuplicateCodeViewer constructor


	public void refreshTree()
	{
		treeModel.reload();
	}

	public void gotoDuplicate(final Duplicate duplicate)
	{
		if(duplicate != null)
		{
			final Buffer buffer = jEdit.openFile(view,duplicate.getFilename());

			VFSManager.runInAWTThread(new Runnable()
			{
				public void run()
				{
					view.setBuffer(buffer);

					int start = buffer.getLineStartOffset(duplicate.getBeginLine());
					int end = buffer.getLineEndOffset(duplicate.getEndLine()-3);
					//Log.log(Log.DEBUG, this.getClass(), "Start Line "+ duplicate.getBeginLine() + " End Line "+ duplicate.getEndLine() + " Start " + start + " End "+ end);
					//Since an AIOOB Exception is thrown if the end is the end of file. we do a -1 from end to fix it.
					view.getTextArea().setSelection(new Selection.Range(start,end -1));
					view.getTextArea().moveCaretPosition(start);
				}
			});
		}
	}

	public DefaultMutableTreeNode getRoot()
	{
		return (DefaultMutableTreeNode)treeModel.getRoot();
	}

	public void addDuplicates(Duplicates duplicates)
	{
		//System.out.println("Inside addDuplicates " + duplicates +" Root child count "+ treeModel.getChildCount(treeModel.getRoot()));
		getRoot().add(duplicates);
		//vecDuplicates.addElement(duplicates);
	}

	public class Duplicates extends DefaultMutableTreeNode
	{
		List vecduplicate = new ArrayList();
		String message, sourcecode;

		public Duplicates(String message, String sourcecode)
		{
			this.message = message;
			this.sourcecode = sourcecode;
		}

		public String getSourceCode()
		{
			return sourcecode;
		}

		public void addDuplicate(Duplicate duplicate)
		{
			add(duplicate);
			//vecduplicate.addElement(duplicate);
		}

		public String toString()
		{
			return message;
		}
	}

	public class Duplicate extends DefaultMutableTreeNode
	{
		private final String filename;
		private final int beginLine, endLine;

		public Duplicate(String filename,int beginLine, int endLine)
		{
			this.filename = filename;
			this.beginLine = beginLine;
			this.endLine = endLine;
		}

		public String getFilename()
		{
			return filename;
		}

		public int getBeginLine()
		{
			return beginLine;
		}

		public int getEndLine()
		{
			return endLine;
		}

		public String toString()
		{
			return filename + ":"+ (getBeginLine()+1)+"-"+(getEndLine()-2);
		}
	}

	public void expandAll()
	{
		int row = 0;
		while (row < tree.getRowCount())
		{
			tree.expandRow(row);
			row++;
		}
	}

	public void collapseAll()
	{
		int row = tree.getRowCount() - 1;
		while (row >= 0) {
			tree.collapseRow(row);
			row--;
		}
	}

	public void clearDuplicates()
	{
		getRoot().removeAllChildren();
	}

}//End of class CPDDuplicateCodeViewer


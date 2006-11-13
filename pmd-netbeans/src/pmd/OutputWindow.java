/*
 *  Copyright (c) 2002-2006, the pmd-netbeans team
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification,
 *  are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 *  ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 *  DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 *  CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 *  LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 *  OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 *  DAMAGE.
 */

package pmd;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import org.openide.awt.StatusDisplayer;
import org.openide.cookies.LineCookie;
import org.openide.loaders.DataObject;
import org.openide.text.Line;
import org.openide.text.Line.Set;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author Tomasz Slota <tomslot@gmail.com>
 */
public class OutputWindow extends TopComponent {
    private JTable tblResults = new JTable();
    private ViolationsTableModel tblMdlResults = new ViolationsTableModel();
    private TableSorter tblSorter = null;
    private static OutputWindow instance = null;
    
    /** Creates a new instance of OutputWindow */
    private OutputWindow() {
        setLayout(new BorderLayout());
        
        tblSorter = new TableSorter(tblMdlResults);
        tblSorter.setTableHeader(tblResults.getTableHeader());
        tblResults.setModel(tblSorter);
        tblResults.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblResults.getTableHeader().setToolTipText(
                "Click to specify sorting; Control-Click to specify secondary sorting");
        
        JScrollPane scrlPane = new JScrollPane(tblResults);
        scrlPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrlPane);
        
        
        tblResults.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e){
                if (e.getClickCount() == 2){
                    int row = tblResults.rowAtPoint(e.getPoint());
                    selectResultRow(row);
                }
            }
        });
    }
    
    public boolean selectNextResult(){
        int selectedRow = tblResults.getSelectedRow();
        
        if (selectedRow == -1){
            return false;
        }
        
        selectedRow ++;
        
        if (selectedRow == tblResults.getRowCount()){
            selectedRow = 0;
        }
        
        tblResults.getSelectionModel().setSelectionInterval(selectedRow, selectedRow);
        selectResultRow(selectedRow);
        return true;
    }
    
    public boolean selectPreviousResult(){
        int selectedRow = tblResults.getSelectedRow();
        
        if (selectedRow == -1){
            return false;
        }
        
        selectedRow --;
        
        if (selectedRow == -1){
            selectedRow = tblResults.getRowCount() - 1;
        }
        
        tblResults.getSelectionModel().setSelectionInterval(selectedRow, selectedRow);
        selectResultRow(selectedRow);
        return true;
    }
    
    public static OutputWindow getInstance(){
        if (instance == null){
            instance = new OutputWindow();
        }
        
        return instance;
    }
    
    public void open() {
        Mode m = WindowManager.getDefault().findMode("output");
        if (m != null) {
            m.dockInto(this);
        }
        super.open();
    }
    
    protected String preferredID(){
        return "PMDOutput";
    }
    
    public int getPersistenceType(){
        return PERSISTENCE_NEVER;
    }
    
    public void setViolations(Fault violations[]){
        tblMdlResults.setViolations(violations);
        
        if (violations.length > 0){
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    selectResultRow(0);
                    tblResults.getSelectionModel().setSelectionInterval(0, 0);
                }
            });
        }
    }
    
    private void selectResultRow(int row){
        PMDAnnotation.clearAll();
        String faultTxtRepresentation = (String) tblSorter.getValueAt(row, 3);
        if (faultTxtRepresentation.charAt(faultTxtRepresentation.length()-1) == '\n') {
            faultTxtRepresentation = faultTxtRepresentation.substring(0, faultTxtRepresentation.length()-1);
        }
        DataObject object = FaultRegistry.getInstance().getDataObject( faultTxtRepresentation );
        LineCookie cookie = ( LineCookie )object.getCookie( LineCookie.class );
        Set lineset = cookie.getLineSet();
        int lineNum = Fault.getLineNum(faultTxtRepresentation);
        Line line = lineset.getOriginal(lineNum - 1);
        String msg = Fault.getErrorMessage(faultTxtRepresentation);
        PMDAnnotation annotation = PMDAnnotation.getNewInstance();
        annotation.setErrorMessage( msg );
        annotation.attach(line);
        line.addPropertyChangeListener( annotation );
        line.show(Line.SHOW_GOTO);
        StatusDisplayer.getDefault().setStatusText( msg );
    }
    
    private class ViolationsTableModel extends AbstractTableModel{
        private final String columnNames[] = new String[]{"Location", "Rule Name", "Recommendation"};
        private Fault violations[] = new Fault[0];
        
        public int getRowCount() {
            return violations.length;
        }
        
        public int getColumnCount() {
            return columnNames.length;
        }
        
        public String getColumnName(int column){
            return columnNames[column];
        }
        
        public Object getValueAt(int row, int col) {
            Fault violation = violations[row];
            
            String value = null;
            
            switch (col){
                case 0:
                    value = getLocation(violation);
                    break;
                case 1:
                    value = getRuleName(violation);
                    break;
                case 2: //
                    value = getRecommendation(violation);
                    break;
                case 3: // not displayed, used by the mouse listener
                    value = violation.getFault();
                    break;
            }
            
            return value;
        }
        
        public void setViolations(Fault violations[]){
            this.violations = violations;
            fireTableDataChanged();
        }
    }
    
    private static String getLocation(Fault fault){
        String faultStr = fault.getFault();
        return faultStr.substring(0, faultStr.indexOf("]") + 1);
    }
    
    private static String getRuleName(Fault fault){
        String msg = fault.getMessage();
        return msg.substring(0, msg.indexOf(","));
    }
    
    private static String getRecommendation(Fault fault){
        String msg = fault.getMessage();
        return msg.substring(msg.indexOf(",") + 1);
    }
}

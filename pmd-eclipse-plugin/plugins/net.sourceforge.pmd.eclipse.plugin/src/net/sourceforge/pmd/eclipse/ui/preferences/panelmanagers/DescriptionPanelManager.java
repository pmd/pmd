package net.sourceforge.pmd.eclipse.ui.preferences.panelmanagers;

import java.net.MalformedURLException;
import java.net.URL;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.ui.preferences.br.ValueChangeListener;
import net.sourceforge.pmd.util.StringUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;

/**
 * 
 * @author Brian Remedios
 */
public class DescriptionPanelManager extends AbstractRulePanelManager {

    private Text descriptionBox;
    private Text externalURLField;    
    private Label extURLLabel;
    private Button browseButton;
    private Label messageLabel;
    private Text  messageField;
        
    public DescriptionPanelManager(ValueChangeListener theListener) {
        super(theListener);
    }

    protected boolean canManageMultipleRules() { return false; }
    
    protected void clearControls() {
        descriptionBox.setText("");
        externalURLField.setText("");
    }
    
    protected void setVisible(boolean flag) {
        
        descriptionBox.setVisible(flag);
        externalURLField.setVisible(flag);
        browseButton.setVisible(flag);
        extURLLabel.setVisible(flag);
        messageLabel.setVisible(flag);
        messageField.setVisible(flag);
    }
   
    public Control setupOn(Composite parent) {
        
        initializeOn(parent);
        
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        
        Composite panel = new Composite(parent, 0);
        GridLayout layout = new GridLayout(3, false);
        panel.setLayout(layout);
                
        descriptionBox = newTextField(panel); 
        gridData = new GridData(GridData.FILL_BOTH);
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalSpan = 3;
        descriptionBox.setLayoutData(gridData);              
      
        descriptionBox.addListener(SWT.FocusOut, new Listener() {
            public void handleEvent(Event event) {
                               
                Rule soleRule = soleRule();
                
                String cleanValue = descriptionBox.getText().trim();
                String existingValue = soleRule.getDescription();
                
                if (StringUtil.areSemanticEquals(existingValue, cleanValue)) return;
                
                soleRule.setDescription(cleanValue);
                valueChanged(null, cleanValue);         
            }
        }); 
        
        buildExternalUrlPanel(panel, "External URL:");        
        buildMessagePanel(panel, "Message:");
        
        return panel;    
    }
        
    private void buildExternalUrlPanel(Composite parent, String urlLabel) {
                        
        GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
        
        extURLLabel = new Label(parent, 0);
        extURLLabel.setText(urlLabel);
        gridData.horizontalSpan = 1;
        gridData.grabExcessHorizontalSpace = false;
        extURLLabel.setLayoutData(gridData);        
        
        externalURLField = new Text(parent, SWT.BORDER);
        gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 1;
        gridData.grabExcessHorizontalSpace = true;
        externalURLField.setLayoutData(gridData);
        
        browseButton = buildExternalInfoUrlButton(parent);
        gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
        gridData.horizontalSpan = 1;
        browseButton.setLayoutData(gridData);
        
        externalURLField.addListener(SWT.FocusOut, new Listener() {
            public void handleEvent(Event e) {
              handleExternalURLChange();
            }
          });        
        externalURLField.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {             
                adjustBrowseButton();
            }           
        });
    }
    
    private void buildMessagePanel(Composite parent, String messageLbl) {
                
        GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
        
        messageLabel = new Label(parent, 0);
        messageLabel.setText(messageLbl);
        gridData.horizontalSpan = 1;
        gridData.grabExcessHorizontalSpace = false;
        messageLabel.setLayoutData(gridData);        
        
        messageField = new Text(parent, SWT.BORDER);
        gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 2;
        gridData.grabExcessHorizontalSpace = true;
        messageField.setLayoutData(gridData);
                
        messageField.addListener(SWT.FocusOut, new Listener() {
            public void handleEvent(Event e) {
              handleMessageChange();
            }
          });
    }
    
    private void handleExternalURLChange() {
        
        String newURL = externalURLField.getText().trim();
        Rule rule = soleRule();
        
        if (!StringUtil.areSemanticEquals(rule.getExternalInfoUrl().trim(), newURL)) {
            rule.setExternalInfoUrl(newURL);
            valueChanged(null, newURL);
        }
        
       adjustBrowseButton();
    }
    
    private void handleMessageChange() {
        
        String newMessage = messageField.getText().trim();
        Rule rule = soleRule();
        
        if (!StringUtil.areSemanticEquals(rule.getMessage().trim(), newMessage)) {
            rule.setMessage(newMessage);
        }
    }
    
    private Button buildExternalInfoUrlButton(Composite parent) {
        
        final Button button = new Button(parent, SWT.PUSH);
        button.setText("Browse");

        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                String url = externalURLField.getText();
                if (url.length() > 0) {
                    try {
                        IWebBrowser browser = PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser();
                        browser.openURL(new URL(url));
                    } catch (PartInitException e) {
                        e.printStackTrace();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        return button;
    }
    
    protected void adapt() {
        
        Rule soleRule = soleRule();
        
        if (soleRule == null) {
            shutdown(descriptionBox);
            shutdown(externalURLField);
            shutdown(messageField);
        } else {
            show(descriptionBox, soleRule.getDescription().trim());
            show(externalURLField, soleRule.getExternalInfoUrl().trim());
            show(messageField, soleRule.getMessage().trim());
        }
        
        adjustBrowseButton();
    }

    public static boolean isValidURL(String url) {
        
        if (StringUtil.isEmpty(url)) return false;
        
        String urlUC = url.toUpperCase();       
        if (!urlUC.startsWith("HTTP")) return false;
        
        for (int i=0; i<url.length(); i++) {
            if (Character.isWhitespace(url.charAt(i))) return false;
        }
        
        return true;
    }
    
    private boolean hasValidURL() {
        String url = externalURLField.getText().trim();
        return isValidURL(url);
    }
    
    private void adjustBrowseButton() {
        
        boolean isValid = hasValidURL();
        
        browseButton.setEnabled(isValid);
        externalURLField.setForeground(
            isValid ? textColour : errorColour
            );
    }

    protected String[] fieldErrors() {
        
        return hasValidURL() ? 
               StringUtil.EMPTY_STRINGS :
               new String[] { "Invalid external URL" };
    }
}

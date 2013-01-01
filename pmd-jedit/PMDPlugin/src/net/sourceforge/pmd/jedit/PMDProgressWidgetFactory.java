package net.sourceforge.pmd.jedit;

import org.gjt.sp.jedit.gui.statusbar.*;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import javax.swing.JComponent;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import org.gjt.sp.jedit.View;

/**
 * A progress bar for PMD to display when processing multiple files. Increments
 * the progress bar as each file is processed.
 */
public class PMDProgressWidgetFactory implements StatusWidgetFactory {

    HashMap<View, ProgressWidget> cache = new HashMap<View, ProgressWidget>();

    public Widget getWidget( View view ) {
        ProgressWidget widget = cache.get( view );
        if ( widget == null ) {
            widget = new ProgressWidget();
            cache.put( view, widget );
        }
        widget.setVisible( true );
        return widget;
    }

    /**
     * Sets the maximum value for the progress bar displayed for the given view.
     * @param view The View displaying the progress bar.
     * @param max The number of files to be processed.
     */
    public void setBounds( View view, int max ) {
        ProgressWidget widget = cache.get( view );
        if ( widget == null ) {
            return;
        }
        widget.setMaximum( max );
    }

    public class ProgressWidget implements Widget, PropertyChangeListener {

        JProgressBar progressBar = null;

        public ProgressWidget() {
            progressBar = new JProgressBar(0, 0 );
            progressBar.setValue(0 );
            progressBar.setString( "" );
            progressBar.setStringPainted( true );
            progressBar.setVisible(false);
        }
        
        public JComponent getComponent() {
            return progressBar;
        }

        public void update() {
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    progressBar.setValue( progressBar.getValue() + 1 );
                    progressBar.setString( String.valueOf( ( int ) ( 100 * progressBar.getValue() / progressBar.getMaximum() ) ) + '%' );
                }
            } );
        }

        /**
         * Calls <code>update</code>. For use by a SwingWorker.
         * @param event Not used.
         */
        public void propertyChange( PropertyChangeEvent event ) {
            update();
        }

        /**
         * Not used.
         */
        public void propertiesChanged() {
        }

        public void setMaximum( int max ) {
            progressBar.setMaximum( max );
        }

        public void complete() {
            progressBar.setValue( progressBar.getMaximum() - 1 );
            update();

            int delay = 2000;
            ActionListener timerTask = new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    SwingUtilities.invokeLater( new Runnable() {
                        public void run() {
                            progressBar.setVisible( false );
                        }
                    } );
                }
            };
            javax.swing.Timer timer = new javax.swing.Timer( delay, timerTask );
            timer.setRepeats( false );
            timer.start();
        }

        public void setVisible( boolean visible ) {
            progressBar.setVisible( visible );
        }
    }
}

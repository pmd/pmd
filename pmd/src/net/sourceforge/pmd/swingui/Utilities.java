package net.sourceforge.pmd.swingui;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;

import javax.swing.ImageIcon;

class Utilities
{

    /**
     *******************************************************************************
     *
     * @param fileName
     *
     * @return
     */
    protected static final ImageIcon getImageIcon(String fileName)
    {
        final byte[][] buffer = new byte[1][];

        try
        {
            InputStream resource =  PMDLookAndFeel.class.getResourceAsStream(fileName);

            if (resource == null)
            {
                return null;
            }

            BufferedInputStream in;
            ByteArrayOutputStream out;
            int n;

            in = new BufferedInputStream(resource);
            out = new ByteArrayOutputStream(1024);
            buffer[0] = new byte[1024];

            while ((n = in.read(buffer[0])) > 0)
            {
                out.write(buffer[0], 0, n);
            }

            in.close();
            out.flush();
            buffer[0] = out.toByteArray();
        }
        catch (IOException ioe)
        {
            return null;
        }

        if (buffer[0] == null)
        {
            return null;
        }

        if (buffer[0].length == 0)
        {
            return null;
        }

        return new ImageIcon(buffer[0]);
    }
}
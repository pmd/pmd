class UnusedFormalParam2 {
    private void readObject (java.io.ObjectInputStream s) throws java.io.IOException
    {
        String str = s.readUTF ();
        String[] args = null;
        java.util.Properties props = null;
        org.omg.CORBA.Object obj = org.omg.CORBA.ORB.init (args, props).string_to_object (str);
        org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl) obj)._get_delegate ();
        _set_delegate (delegate);
    }
}
--
-- From https://github.com/pmd/pmd/pull/1988
--
declare

  w_java_source clob := q'%
create or replace and compile java source named PdfUtils as
  /** BLOB encryption class using AES 128bit
  * Load class to oracle db. Class file is on the oracle serever:
  *      sql script:
  *               exec sys.dbms_java.loadjava('-v -r /tsfa/tia7400/PdfPassword.class');
  *      command line:
  *              loadjava -user tia -resolve -verbose -r /tsfa/tia7400/PdfPassword.java
  *              dropjava -user tia -resolve -verbose -r /tsfa/tia7400/PdfPassword.java
  */
    /**
     * Function reads input blob, encrypts and returns it to the caller
     * @author Karol Wozniak
     * @param secret password key
     * @param fortuneBLOB blob var
     * @return oracle.sql.BLOB
     */
    public static BLOB encryptPdf(String secret, Blob blobVar) throws Exception {
        System.out.println("Start readBlob");
        blobfile = writeToBlob(boas.toByteArray());
        is.close();
        reader.close();
        System.out.println("encrypted using password " + secret);
        System.out.println("End readBlob");
        return blobfile;
    }
};
%';

begin
  null;
end;
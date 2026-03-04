--
-- Examples for XMLType constructors
-- see https://docs.oracle.com/en/database/oracle/oracle-database/23/arpls/XMLTYPE.html#GUID-59081026-A42A-4352-B5EA-A6E433A51F5E

DECLARE
   l_xml1 XMLTYPE := XMLTYPE('<Catalog>
   <Book>
      <Author>Steven Feuerstein</Author>
      <Title>Oracle PLSQL Language Pocket Reference</Title>
      <Genre>Programming</Genre>
      <Price>18.25</Price>
      <PublishDate>2015-09-04</PublishDate>
      <Description>A guide to Oracle PLSQL Language Fundamentals.</Description>
   </Book>
</Catalog>');

   l_xml2 XMLTYPE := XMLTYPE('<Catalog>
   <Book>
      <Author>Steven Feuerstein</Author>
      <Title>Oracle PLSQL Language Pocket Reference</Title>
      <Genre>Programming</Genre>
      <Price>18.25</Price>
      <PublishDate>2015-09-04</PublishDate>
      <Description>A guide to Oracle PLSQL Language Fundamentals.</Description>
   </Book>
</Catalog>', 'schema');

   l_xml3 XMLTYPE := XMLTYPE('<Catalog>
   <Book>
      <Author>Steven Feuerstein</Author>
      <Title>Oracle PLSQL Language Pocket Reference</Title>
      <Genre>Programming</Genre>
      <Price>18.25</Price>
      <PublishDate>2015-09-04</PublishDate>
      <Description>A guide to Oracle PLSQL Language Fundamentals.</Description>
   </Book>
</Catalog>', 'schema',
    1 --validated
    );

   l_xml4 XMLTYPE := XMLTYPE('<Catalog>
   <Book>
      <Author>Steven Feuerstein</Author>
      <Title>Oracle PLSQL Language Pocket Reference</Title>
      <Genre>Programming</Genre>
      <Price>18.25</Price>
      <PublishDate>2015-09-04</PublishDate>
      <Description>A guide to Oracle PLSQL Language Fundamentals.</Description>
   </Book>
</Catalog>', 'schema',
    1, --validated
    1 --wellformed
    );

BEGIN
END;
/

-- https://docs.oracle.com/en/database/oracle/oracle-database/23/adxdb/PLSQL-APIs-for-XMLType.html#GUID-4B7856FD-9104-497C-81F1-0B3C3F7DE81F
-- Example 11-1 Creating and Manipulating a DOM Document

CREATE TABLE person OF XMLType;

DECLARE
  var       XMLType;
  doc       DBMS_XMLDOM.DOMDocument;
  ndoc      DBMS_XMLDOM.DOMNode;
  docelem   DBMS_XMLDOM.DOMElement;
  node      DBMS_XMLDOM.DOMNode;
  childnode DBMS_XMLDOM.DOMNode;
  nodelist  DBMS_XMLDOM.DOMNodelist;
  buf       VARCHAR2(2000);
BEGIN
  var := XMLType('<PERSON><NAME>ramesh</NAME></PERSON>');

  -- Create DOMDocument handle
  doc     := DBMS_XMLDOM.newDOMDocument(var);
  ndoc    := DBMS_XMLDOM.makeNode(doc);

  DBMS_XMLDOM.writeToBuffer(ndoc, buf);
  DBMS_OUTPUT.put_line('Before:'||buf);

  docelem := DBMS_XMLDOM.getDocumentElement(doc);

  -- Access element
  nodelist := DBMS_XMLDOM.getElementsByTagName(docelem, 'NAME');
  node := DBMS_XMLDOM.item(nodelist, 0);
  childnode := DBMS_XMLDOM.getFirstChild(node);

  -- Manipulate element
  DBMS_XMLDOM.setNodeValue(childnode, 'raj');
  DBMS_XMLDOM.writeToBuffer(ndoc, buf);
  DBMS_OUTPUT.put_line('After:'||buf);
  DBMS_XMLDOM.freeDocument(doc);
  INSERT INTO person VALUES (var);
END;
/

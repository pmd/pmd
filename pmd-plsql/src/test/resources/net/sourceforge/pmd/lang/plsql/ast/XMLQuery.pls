--
-- See https://github.com/pmd/pmd/issues/4441
--

DECLARE
   --
   l_xml XMLTYPE := XMLTYPE('<Catalog>
   <Book>
      <Author>Steven Feuerstein</Author>
      <Title>Oracle PLSQL Language Pocket Reference</Title>
      <Genre>Programming</Genre>
      <Price>18.25</Price>
      <PublishDate>2015-09-04</PublishDate>
      <Description>A guide to Oracle PLSQL Language Fundamentals.</Description>
   </Book>
</Catalog>');
   --
   l_tagname VARCHAR2(100) := '$p//Author';
   l_result  VARCHAR2(4000);
   --
BEGIN
   --
   SELECT XMLCAST(XMLQUERY(l_tagname PASSING l_xml AS "p" RETURNING CONTENT) AS VARCHAR2(4000))
     INTO l_result
     FROM DUAL;
   --
   DBMS_OUTPUT.put_line(l_result);
   --
END;

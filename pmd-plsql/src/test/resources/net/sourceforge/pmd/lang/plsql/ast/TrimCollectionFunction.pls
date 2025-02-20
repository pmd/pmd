--
-- See https://github.com/pmd/pmd/issues/5522
--

-- https://docs.oracle.com/en/database/oracle/oracle-database/23/lnpls/collection-methods.html#GUID-6AF582B1-9C50-4858-AE6C-B14DD051ACD1
DECLARE
  nt nt_type := nt_type(11, 22, 33, 44, 55, 66);
BEGIN
  print_nt(nt);

  nt.TRIM;       -- Trim last element, pseudocolumn syntax
  print_nt(nt);

  nt.DELETE(4);  -- Delete fourth element
  print_nt(nt);

  nt.TRIM(2);    -- Trim last two elements
  print_nt(nt);
END;
/

-- https://github.com/Qualtagh/OracleDBUtils/blob/master/p_utils.9.sql#241
function distinguishXML() return text is
  retNodes XMLSequenceType := XMLSequenceType();
  capacity pls_integer := 32;
  j pls_integer := 1;
begin
  retNodes.trim( capacity - j + 1 );
  return 'done';
end;
/

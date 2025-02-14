--
-- See https://github.com/pmd/pmd/issues/5522
--

function distinguishXML() return text is
begin
  select trim(x + y) from dual;
  return trim(x + y);
end;
/

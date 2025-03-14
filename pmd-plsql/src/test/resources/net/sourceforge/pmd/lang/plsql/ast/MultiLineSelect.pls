PROCEDURE IsNull (
  inStatusId IN number
)
is
  V_BUF varchar(1);
BEGIN
select '1'  
into V_BUF 
from dual;
end;
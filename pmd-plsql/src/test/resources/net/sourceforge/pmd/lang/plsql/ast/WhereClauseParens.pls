--
-- Where Clause With Parentheses
-- See https://github.com/pmd/pmd/issues/1828
--

BEGIN

select *
from dual
where (dummy = X or 1 = 2)
and 1=1;

select *
from dual
where (dummy <= X or 1 = 2)
and 1=1;

select *
FROM dual
WHERE (dummy, 'X') in (select dummy, 'X' from dual);

END;
/

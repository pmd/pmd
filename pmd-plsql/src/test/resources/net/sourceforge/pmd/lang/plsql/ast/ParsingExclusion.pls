begin
  do_something();
  -- pmd-exclude-begin: PMD does not like dbms_lob.trim (clash with TrimExpression)
  dbms_lob.trim(the_blob, 1000);
  -- pmd-exclude-end
  do_something_else(x);
end;
/

select dummy from dual a
where 1=1
  -- pmd-exclude-begin: PMD does not like scalar subqueries in WHERE conditions
  and 'J' = (select max('J') from dual b)
  -- pmd-exclude-end
;

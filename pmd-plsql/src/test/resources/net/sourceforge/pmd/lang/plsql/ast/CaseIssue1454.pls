create or replace procedure PMD_TEST_CASE( a IN NUMBER) AS

begin

    CASE WHEN a >= 100 THEN -- Test the same with <=,!=,<> all cases fail both with IF and CASE statements
      dbms_output.put_line('Number is greater than equal to 100');
    WHEN a <= 100 THEN
      dbms_output.put_line('Number is less than equal to 100');
    WHEN a < 100 THEN
      dbms_output.put_line('Number is less than 100');
    WHEN a > 100 THEN
      dbms_output.put_line('Number is greater than 100');
    WHEN a = 42 THEN
      dbms_output.put_line('Number is equal to 42');
    WHEN a != 42 THEN
      dbms_output.put_line('Number is not 42');
    WHEN a ~= 42 THEN
      dbms_output.put_line('Number is not 42');
    WHEN a ^= 42 THEN
      dbms_output.put_line('Number is not 42');
    WHEN a <> 42 THEN
      dbms_output.put_line('Number is not 42');
    ELSE
      dbms_output.put_line('Number is less than 100');
    END CASE;

exception
  when others then 
    dbms_output.put_line('ERROR: ' || sqlerrm);

end PMD_TEST_CASE;

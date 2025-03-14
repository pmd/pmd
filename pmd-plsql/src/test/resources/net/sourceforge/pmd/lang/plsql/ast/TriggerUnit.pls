create or replace trigger test_trigger
instead of update
on test_table
for each row
begin
  test.clr;
end;
/

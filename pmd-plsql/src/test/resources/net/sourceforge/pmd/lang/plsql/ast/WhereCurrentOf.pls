declare
  thisstudent student%rowtype;

  cursor maths_student is
    select *
    from   student
    where  sid in (select sid
                   from   take
                   where  cid = 'cs145')
    for    update;

begin
  open maths_student;
  loop
    fetch maths_student
      into thisstudent;
    exit when(maths_student%notfound);
    if (thisstudent.gpa < 4.0) then
      update student
      set    gpa = 4.0
      where  current of maths_student;
    end if;
  end loop;

  close maths_student;
end;

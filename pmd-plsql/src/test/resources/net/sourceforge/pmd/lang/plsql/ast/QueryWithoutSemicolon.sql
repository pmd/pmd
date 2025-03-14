create or replace view beneficiary as
       select os.obj_seq_no,
              nvl(os.n01, 0) procentage
              from   object_slave os
              where  obj_slave_type_id = 'BENEFICIARY'
/
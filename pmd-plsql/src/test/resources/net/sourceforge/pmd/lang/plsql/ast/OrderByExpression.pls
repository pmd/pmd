BEGIN

select * from
  (select distinct
     o.opp_id, 
     b.is_lead_opp,
     b.bundle_type,
     decode(first_value(o.opp_id) over(order by decode(o.OPPST_CODE,2,2,0) desc, h.dmltime), o.opp_id,'A',NULL)
     first_value
     from OPPORTUNITIES o);

END;

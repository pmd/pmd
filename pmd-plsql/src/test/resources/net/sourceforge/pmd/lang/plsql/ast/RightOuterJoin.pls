BEGIN
SELECT times.time_id, product, quantity
INTO r_record
FROM inventory 
   RIGHT OUTER JOIN times ON (times.time_id = inventory.time_id) 
   ORDER BY  2,1;
END;
/
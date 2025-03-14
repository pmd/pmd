--
-- Example 6-40 SET TRANSACTION Statement in Read-Only Transaction
-- from https://docs.oracle.com/en/database/oracle/oracle-database/18/lnpls/static-sql.html#GUID-EC9CC8B7-5DDD-4B60-83BF-686A9FD43B3D
--
DECLARE
  daily_order_total    NUMBER(12,2);
  weekly_order_total   NUMBER(12,2); 
  monthly_order_total  NUMBER(12,2);
BEGIN
   COMMIT; -- end previous transaction
   SET TRANSACTION READ ONLY NAME 'Calculate Order Totals';

   SELECT SUM (order_total)
   INTO daily_order_total
   FROM orders
   WHERE order_date = SYSDATE;

   SELECT SUM (order_total)
   INTO weekly_order_total
   FROM orders
   WHERE order_date = SYSDATE - 7;

   SELECT SUM (order_total)
   INTO monthly_order_total
   FROM orders
   WHERE order_date = SYSDATE - 30;

   COMMIT; -- ends read-only transaction
END;
/
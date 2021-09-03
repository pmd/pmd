--
-- Samples from https://docs.oracle.com/en/database/oracle/oracle-database/18/sqlrf/XMLTABLE.html
--

BEGIN

SELECT warehouse_name warehouse,
   warehouse2."Water", warehouse2."Rail"
   FROM warehouses,
   XMLTABLE('/Warehouse'
      PASSING warehouses.warehouse_spec
      COLUMNS 
         "Water" varchar2(6) PATH 'WaterAccess',
         "Rail" varchar2(6) PATH 'RailAccess')
      warehouse2;

--
-- Samples from https://docs.oracle.com/en/database/oracle/oracle-database/18/adxdb/xquery-and-XML-DB.html
--
 SELECT po.reference, li.*
    FROM po_binaryxml p,
         XMLTable('/PurchaseOrder' PASSING p.OBJECT_VALUE
                  COLUMNS
                    reference VARCHAR2(30) PATH 'Reference',
                    lineitem  XMLType      PATH 'LineItems/LineItem') po,
         XMLTable('/LineItem' PASSING po.lineitem
                  COLUMNS
                    itemno      NUMBER(38)    PATH '@ItemNumber',
                    description VARCHAR2(256) PATH 'Description',
                    partno      VARCHAR2(14)  PATH 'Part/@Id',
                    quantity    NUMBER(12, 2) PATH 'Part/@Quantity',
                    unitprice   NUMBER(8, 4)  PATH 'Part/@UnitPrice') li;

SELECT OBJECT_VALUE
  FROM purchaseorder
  WHERE XMLExists('/PurchaseOrder[SpecialInstructions="Expedite"]'
                  PASSING OBJECT_VALUE);


SELECT XMLCast(XMLQuery('/PurchaseOrder/Reference' PASSING OBJECT_VALUE
                                                   RETURNING CONTENT)
               AS VARCHAR2(100)) "REFERENCE"
  FROM purchaseorder
  WHERE XMLExists('/PurchaseOrder[SpecialInstructions="Expedite"]'
                  PASSING OBJECT_VALUE);


UPDATE oe.purchaseorder p SET p.OBJECT_VALUE =
  XMLQuery(
    'copy $i :=
       $p1 modify (for $j in $i/PurchaseOrder/LineItems
                     return (#ora:child-element-name LineItem #)
                            {insert node $p2 into $j)
                  return $i'
    PASSING p.OBJECT_VALUE AS "p1", :1 AS "p2" RETURNING CONTENT)
  WHERE XMLQuery(
          '/PurchaseOrder/Reference/text()'
          PASSING p.OBJECT_VALUE RETURNING CONTENT).getStringVal() =
            'EMPTY_LINES';

SELECT XMLQuery('(#ora:invalid_path empty #)
                 {exists($p/PurchaseOrder//NotInTheSchema)}'
                PASSING OBJECT_VALUE AS "p" RETURNING CONTENT)
  FROM oe.purchaseorder p;

SELECT XMLQuery('(#ora:view_on_null empty #)
                 {for $i in fn:collection("oradb:/PUBLIC/NULL_TEST")/ROW 
                  return $i}'
                RETURNING CONTENT)
  FROM DUAL;

SELECT XMLQuery('(#ora:view_on_null null #)
                 {for $i in fn:collection("oradb:/PUBLIC/NULL_TEST")/ROW 
                  return $i}'
                RETURNING CONTENT)
  FROM DUAL;

SELECT XMLQuery('(#ora:no_xmlquery_rewrite#) (: Do not optimize expression :)
                 {for $i in (#ora:xmlquery_rewrite#) (: Optimize subexp. :)
                            {fn:collection("oradb:/HR/REGIONS")},
                      $j in (#ora:xmlquery_rewrite#) (: Optimize subexpr. :)
                            {fn:collection("oradb:/HR/COUNTRIES")}
                  where $i/ROW/REGION_ID = $j/ROW/REGION_ID
                    and $i/ROW/REGION_NAME = $regionname
                  return $j}'
         PASSING CAST('&REGION' AS VARCHAR2(40)) AS "regionname"
           RETURNING CONTENT)
  AS asian_countries FROM DUAL;

SELECT XMLQuery('declare default element namespace
                 "http://xmlns.oracle.com/xdb/xdbconfig.xsd"; (: :)
                 (#ora:transform_keep_schema#)
                 {copy $NEWXML :=
                   $XML modify (for $CFG in $NEWXML/xdbconfig//httpconfig 
                                  return (replace value of node
                                          $CFG/http-port with xs:int($PORTNO)))
                               return $NEWXML}'
                PASSING CFG AS "XML", 81 as "PORTNO" RETURNING CONTENT)
  FROM DUAL;

SELECT XMLELEMENT("Emp", 
   XMLFOREST(e.employee_id AS foo, e.last_name last_name, e.salary))
   "Emp Element"
   FROM employees e WHERE employee_id = 204;

END;

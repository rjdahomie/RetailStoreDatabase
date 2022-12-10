DROP INDEX IF EXISTS units_ordered_idx;
DROP INDEX IF EXISTS product_name_idx;
DROP INDEX IF EXISTS manager_id_idx;
DROP INDEX IF EXISTS orderTime_idx;

CREATE INDEX units_ordered_idx
ON Orders
USING BTREE(unitsOrdered);

CREATE INDEX product_name_idx
ON Product
USING BTREE(productName);

CREATE INDEX manager_id_idx
on Store
USING BTREE(manager_id);

CREATE INDEX orderTime_idx
on Orders
USING BTREE(orderTime);
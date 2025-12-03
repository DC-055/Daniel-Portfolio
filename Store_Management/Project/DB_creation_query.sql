-- Create the Order table
CREATE TABLE Order_Table (
    order_num SERIAL PRIMARY KEY,
    amount INT
);

-- Create the ShippingMethod table
CREATE TABLE ShippingMethod_Table (
	shipping_method_id SERIAL PRIMARY KEY,
	shipping_method_name VARCHAR(20)
);

-- Create the WebsiteOrder table
CREATE TABLE WebsiteOrder_Table (
    order_num INT PRIMARY KEY,
    shipping_method_id INT,
    shipping_price DECIMAL(6, 2),
    FOREIGN KEY (order_num) REFERENCES Order_Table(order_num) ON DELETE CASCADE,
	FOREIGN KEY (shipping_method_id) REFERENCES ShippingMethod_Table(shipping_method_id)
);


-- Create the DeliveryCompany table
CREATE TABLE DeliveryCompany_Table (
	company_name VARCHAR(20),
    whatsapp_number VARCHAR(10) PRIMARY KEY,
    contact_name VARCHAR(20)
);

-- Create the DeliveryOrder table
CREATE TABLE DeliveryOrder_Table(
	company_name VARCHAR(20),
    whatsapp_number VARCHAR(10),
    order_num INT PRIMARY KEY,
	FOREIGN KEY (order_num) REFERENCES WebsiteOrder_Table(order_num) ON DELETE CASCADE,
	FOREIGN KEY (whatsapp_number) REFERENCES DeliveryCompany_Table(whatsapp_number) ON DELETE CASCADE
);


-- Create the Customer table
CREATE TABLE Customer_Table (
    customer_mobile VARCHAR(10) PRIMARY KEY,
    customer_name VARCHAR(20),
    order_num INT,
    FOREIGN KEY (order_num) REFERENCES Order_Table(order_num) ON DELETE CASCADE
);

-- Create the Product table
CREATE TABLE Product_Table (
    catalog_num SERIAL PRIMARY KEY,
    product_name VARCHAR(20),
    cost_price INT,
    selling_price INT,
    stock INT
);

-- Create the WholesaleProduct table
CREATE TABLE WholesaleProduct_Table (
    catalog_num INT PRIMARY KEY,
    FOREIGN KEY (catalog_num) REFERENCES Product_Table(catalog_num) ON DELETE CASCADE
);

-- Create the StoreProduct table
CREATE TABLE StoreProduct_Table (
    catalog_num INT PRIMARY KEY,
    FOREIGN KEY (catalog_num) REFERENCES Product_Table(catalog_num) ON DELETE CASCADE
);

-- Create the WebsiteProduct table
CREATE TABLE WebsiteProduct_Table (
    catalog_num INT PRIMARY KEY,
    dest_country VARCHAR(20),
    price_in_dollars DECIMAL(10, 2),
    prod_weight DECIMAL(10, 2),
    import_tax DECIMAL(10, 2),
    FOREIGN KEY (catalog_num) REFERENCES Product_Table(catalog_num) ON DELETE CASCADE
);

-- Create the OrderProduct table
CREATE TABLE OrderProduct_Table (
    order_num INT PRIMARY KEY,
    catalog_num INT,
    FOREIGN KEY (order_num) REFERENCES Order_Table(order_num) ON DELETE CASCADE,
    FOREIGN KEY (catalog_num) REFERENCES Product_Table(catalog_num) ON DELETE CASCADE
);

--Shipping methods:
INSERT INTO ShippingMethod_Table (shipping_method_name) VALUES ('Standard Shipping');
INSERT INTO ShippingMethod_Table (shipping_method_name) VALUES ('Express Shipping');



--Shipping companies:

INSERT INTO DeliveryCompany_Table (company_name, whatsapp_number, contact_name) VALUES ('Fedex','0525683317','Itzhak Genuth');
INSERT INTO DeliveryCompany_Table (company_name, whatsapp_number, contact_name) VALUES ('DHL','0548792577','Hadas Lapid');

/*
-- Create the Company table
CREATE TABLE Company_Table (
    company_name VARCHAR(20) PRIMARY KEY ,
	comp_num SERIAL,
	order_num INT,
    FOREIGN KEY (order_num) REFERENCES Order_Table(order_num)


SELECT 
    OrderProduct_Table.order_num, 
    Product_Table.catalog_num,
    Product_Table.product_name, 
    Product_Table.cost_price, 
    Product_Table.selling_price, 
    Order_Table.amount
FROM 
    OrderProduct_Table
INNER JOIN 
    Product_Table 
ON 
    OrderProduct_Table.catalog_num = Product_Table.catalog_num
INNER JOIN 
    Order_Table 
ON 
    OrderProduct_Table.order_num = Order_Table.order_num;
*/



/*
DROP TABLE Customer_Table;
DROP TABLE OrderProduct_Table;
DROP TABLE WebsiteProduct_Table;
DROP TABLE StoreProduct_Table;
DROP TABLE WholesaleProduct_Table;
DROP TABLE Product_Table;
DROP TABLE DeliveryOrder_Table;
DROP TABLE DeliveryCompany_Table;
DROP TABLE WebsiteOrder_Table;
DROP TABLE ShippingMethod_Table;
DROP TABLE Order_Table;

select * from Product_Table;
select * from WebsiteProduct_Table;
select * from StoreProduct_Table;
select * from WholesaleProduct_Table;
select * from Customer_Table;
select * from Order_Table;
select * from OrderProduct_Table;
select * from ShippingMethod_Table;
select * from WebsiteOrder_Table;
select * from DeliveryCompany_Table;
select * from DeliveryOrder_Table;
*/


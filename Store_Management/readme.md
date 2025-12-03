# Store Management System – Database Project

This project is a store management system designed to handle both physical and online products. It focuses on correct database design, data integrity, and full support for different order types, including in-store, website, and wholesale orders.

The system was developed as part of an academic database systems course.

---

## Project Overview

The system manages core store operations such as:
- Product storage and inventory control
- Order creation and tracking
- Receipt and order amount handling
- Online order delivery through courier companies

It supports:
- Physical store orders  
- Website (online) orders  
- Wholesale orders  

For website orders, the system selects shipping based on the customer’s chosen shipping method and connects the order to a delivery company.

---

## Main Features

- Add, remove, and restock products
- Search products by catalog number
- Manage multiple product types:
  - Store products  
  - Website products  
  - Wholesale products
- Place and manage orders of all types
- Track all past orders
- Handle shipping methods for online orders
- Connect website orders to delivery companies
- Store customer information for all orders

---

## System Users

- **Shop owners / employees** – main users of the system  
- **Delivery companies** – receive website delivery orders  
- **Customers** – place orders through shop employees (not directly in the system)

---

## Database Design

The system is built around a structured ERD that models:
- Products and their different types
- Orders and website-specific orders
- Customers
- Shipping methods
- Delivery companies

Key relationships include:
- Orders are made by customers  
- Orders contain products  
- Website orders are shipped by delivery companies  
- Products are divided into store, website, and wholesale products  

---

## Project Tables

Main tables used in the system:

- `Product_Table`
- `WholesaleProduct_Table`
- `StoreProduct_Table`
- `WebsiteProduct_Table`
- `Order_Table`
- `OrderProduct_Table`
- `Customer_Table`
- `WebsiteOrder_Table`
- `ShippingMethod_Table`
- `DeliveryCompany_Table`
- `DeliveryOrder_Table`

All tables were built with proper:
- Primary keys  
- Foreign keys  
- Referential integrity  
- Normalized structure

---

## Technologies Used

- **Programming Language:** Java  
- **Database:** PostgreSQL  
- **Development Tools:** pgAdmin, JDBC  
- **Design:** ERD modeling and relational schema design

---

## How to Run the Project

1. Create a PostgreSQL database.
2. Run the SQL scripts to create all project tables.
3. Insert initial data if provided.
4. Configure database connection in the Java project (JDBC).
5. Run the Java application through your IDE.

Make sure PostgreSQL is running before launching the program.

---

## Educational Goals

This project demonstrates:
- Proper relational database design
- Use of primary and foreign keys
- Handling inheritance in database modeling
- Managing real-world store data scenarios
- Integration between Java and PostgreSQL using JDBC

---


## Notes

This project was created for academic purposes as part of a database systems course. The focus is on correct data modeling, integrity, and structured system design rather than UI or production deployment.


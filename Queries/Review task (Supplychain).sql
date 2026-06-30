CREATE TABLE Supplychain (
    ship_id INT PRIMARY KEY,
    stock_capacity FLOAT,
    chemical VARCHAR(100),
    destination VARCHAR(100),
    destination_id INT
);

INSERT INTO SupplyChain
(ship_id, stock_capacity, chemical, destination, destination_id)
VALUES
(11, 250.50, 'Alcohol', 'Mumbai', 1),
(12, 180.75, 'Acetone', 'Delhi', 2),
(13, 320.00, 'Ethanol', 'Chennai', 3),
(14, 275.25, 'Methanol', 'Kochi', 4),
(15, 410.80, 'HJK', 'Hyderabad', 5),
(16, 195.60, 'EWR', 'Pune', 6),
(17, 360.40, 'Phenol', 'Surat', 7),
(18, 290.90, 'XYZ', 'Jaipur', 8),
(19, 150.30, 'Hydrogen', 'Lucknow', 9),
(20, 500.00, 'Ethanol', 'Bhopal', 10);

SELECT * FROM Supplychain;

CREATE TABLE Chemical (
    chem_id INT PRIMARY KEY,
    ship_id INT,
    stock FLOAT,
    FOREIGN KEY (ship_id) REFERENCES SupplyChain(ship_id)
);

INSERT INTO Chemical (chem_id, ship_id, stock)
VALUES
(1, 11, 75000),
(2, 12, 5000),
(3, 13, 8000),
(4, 14, 6000),
(5, 15, 9000),
(6, 16, 7000),
(7, 17, 5500),
(8, 18, 7500),
(9, 19, 6500),
(10, 20, 10000);


SELECT * FROM Chemical;

SELECT c.chem_id,c.stock,s.destination
FROM Chemical c
JOIN SupplyChain s
ON c.ship_id = s.ship_id
WHERE c.stock > 5000;





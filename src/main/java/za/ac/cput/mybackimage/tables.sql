    carvote_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    carname VARCHAR(255),
    votes INT
);

INSERT INTO carvotes (carname) VALUES ('Rolls Royce Ghost');
INSERT INTO carvotes (carname) VALUES ('BMW i7 xDrive60');
INSERT INTO carvotes (carname) VALUES ('Mercedis AMG G63S');
INSERT INTO carvotes (carname) VALUES ('Bentley Continental GT');
INSERT INTO carvotes (carname) VALUES ('Audi Q8');
DROP TABLE IF EXISTS team;

CREATE TABLE team (
 league                                 VARCHAR(18) NOT NULL,
 pos                                    INT NOT NULL,
 name                                   VARCHAR(60) NOT NULL,
 pld                                    INT NOT NULL, 
 w                                      INT NOT NULL,
 d                                      INT NOT NULL,
 l                                      INT NOT NULL,
 gf                                     INT NOT NULL,
 ga                                     INT NOT NULL,
 gd                                     INT NOT NULL, 
 pts                                    INT NOT NULL,
 PRIMARY KEY (name)
) ENGINE InnoDB;
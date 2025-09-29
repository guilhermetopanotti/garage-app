-- Criar setores
INSERT INTO garage_sector (sector, base_price, max_capacity) VALUES ('A', 1.0, 10);
INSERT INTO garage_sector (sector, base_price, max_capacity) VALUES ('B', 2.0, 10);

-- Criar 10 vagas para o setor A
INSERT INTO spot (sector, occupied) VALUES ('A', false);
INSERT INTO spot (sector, occupied) VALUES ('A', false);
INSERT INTO spot (sector, occupied) VALUES ('A', false);
INSERT INTO spot (sector, occupied) VALUES ('A', false);
INSERT INTO spot (sector, occupied) VALUES ('A', false);
INSERT INTO spot (sector, occupied) VALUES ('A', false);
INSERT INTO spot (sector, occupied) VALUES ('A', false);
INSERT INTO spot (sector, occupied) VALUES ('A', false);
INSERT INTO spot (sector, occupied) VALUES ('A', false);
INSERT INTO spot (sector, occupied) VALUES ('A', false);

-- Criar 10 vagas para o setor B
INSERT INTO spot (sector, occupied) VALUES ('B', false);
INSERT INTO spot (sector, occupied) VALUES ('B', false);
INSERT INTO spot (sector, occupied) VALUES ('B', false);
INSERT INTO spot (sector, occupied) VALUES ('B', false);
INSERT INTO spot (sector, occupied) VALUES ('B', false);
INSERT INTO spot (sector, occupied) VALUES ('B', false);
INSERT INTO spot (sector, occupied) VALUES ('B', false);
INSERT INTO spot (sector, occupied) VALUES ('B', false);
INSERT INTO spot (sector, occupied) VALUES ('B', false);
INSERT INTO spot (sector, occupied) VALUES ('B', false);

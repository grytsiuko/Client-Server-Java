CREATE TABLE category(
	id serial PRIMARY KEY,
	name VARCHAR (100) UNIQUE NOT NULL,
	description VARCHAR (255)
);

CREATE TABLE product(
	id serial PRIMARY KEY,
	name VARCHAR (100) UNIQUE NOT NULL,
	producer VARCHAR (100) NOT NULL,
	description VARCHAR (255),
	amount INTEGER NOT NULL CHECK (amount >= 0),
	price NUMERIC(7,2) NOT NULL CHECK (price > 0),
	category_id INTEGER NOT NULL REFERENCES category(id) ON DELETE CASCADE
);
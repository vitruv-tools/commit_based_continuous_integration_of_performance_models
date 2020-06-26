CREATE TABLE users (
	id INTEGER NOT NULL,
	nickname VARCHAR(20) UNIQUE NOT NULL,
	firstname VARCHAR(20),
	lastname VARCHAR(20),
	email VARCHAR(50) NOT NULL,
	password VARCHAR(20) NOT NULL,
	balance FLOAT,
	rating INTEGER,
	creation_date DATE,
	userclass VARCHAR(20),
	region_id INTEGER NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE items (
	id INTEGER NOT NULL,
	name VARCHAR(100),
	description VARCHAR(2048),
	initial_quantity INTEGER NOT NULL,
	initial_price FLOAT NOT NULL,
	buy_now_price FLOAT,
	reserve_price FLOAT,
	start_date TIMESTAMP,
	end_date TIMESTAMP,
	seller_id INTEGER NOT NULL,
	category_id INTEGER NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE bids (
	id INTEGER NOT NULL,
	bid_price FLOAT NOT NULL,
	max_bid_price FLOAT NOT NULL,
	bid_date TIMESTAMP,
	quantity INTEGER NOT NULL,
	user_id INTEGER NOT NULL,
	item_id INTEGER NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE categories (
	id INTEGER NOT NULL,
	name VARCHAR(50),
	PRIMARY KEY (id)
);

CREATE TABLE regions (
	id INTEGER NOT NULL,
	name VARCHAR(25),
	PRIMARY KEY (id)
);

CREATE TABLE comments (
	id INTEGER NOT NULL, 
	comment VARCHAR(2048), 
	comment_date TIMESTAMP, 
	rating INTEGER, 
	to_user_id INTEGER NOT NULL, 
	from_user_id INTEGER NOT NULL, 
	item_id INTEGER NOT NULL, 
	PRIMARY KEY (id)
);

CREATE TABLE buy_now (
	id INTEGER NOT NULL,
	buy_now_date TIMESTAMP,
	quantity INTEGER NOT NULL,
	item_id INTEGER NOT NULL,
	buyer_id INTEGER NOT NULL,	
	PRIMARY KEY (id)
);

CREATE TABLE inventory_items (
	id INTEGER NOT NULL,
	item_id INTEGER NOT NULL,
	available_quantity INTEGER NOT NULL,
	version INTEGER NOT NULL,
	PRIMARY KEY (id)
);

ALTER TABLE users ADD CONSTRAINT FK_users_region_id FOREIGN KEY (region_id) REFERENCES regions (id);
ALTER TABLE inventory_items ADD CONSTRAINT inventoryitemsitem FOREIGN KEY (item_id) REFERENCES items (id);
ALTER TABLE items ADD CONSTRAINT items_category_id FOREIGN KEY (category_id) REFERENCES categories (id);
ALTER TABLE items ADD CONSTRAINT FK_items_seller_id FOREIGN KEY (seller_id) REFERENCES users (id);
ALTER TABLE bids ADD CONSTRAINT FK_bids_item_id FOREIGN KEY (item_id) REFERENCES items (id);
ALTER TABLE bids ADD CONSTRAINT FK_bids_user_id FOREIGN KEY (user_id) REFERENCES users (id);
ALTER TABLE comments ADD CONSTRAINT commentsto_user_id FOREIGN KEY (to_user_id) REFERENCES users (id);
ALTER TABLE comments ADD CONSTRAINT comments_item_id FOREIGN KEY (item_id) REFERENCES items (id);
ALTER TABLE comments ADD CONSTRAINT commentsfromuserid FOREIGN KEY (from_user_id) REFERENCES users (id);
ALTER TABLE buy_now ADD CONSTRAINT FK_buy_now_item_id FOREIGN KEY (item_id) REFERENCES items (id);
ALTER TABLE buy_now ADD CONSTRAINT buy_now_buyer_id FOREIGN KEY (buyer_id) REFERENCES users (id);

CREATE TABLE SEQUENCE (SEQ_NAME VARCHAR(50) NOT NULL, SEQ_COUNT DECIMAL, PRIMARY KEY (SEQ_NAME));

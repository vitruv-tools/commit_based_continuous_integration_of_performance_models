-- Categories
INSERT INTO categories (id, name) VALUES (1, 'Home');
INSERT INTO categories (id, name) VALUES (2, 'Books');
INSERT INTO categories (id, name) VALUES (3, 'Office/Business');
INSERT INTO categories (id, name) VALUES (4, 'Movies');
INSERT INTO categories (id, name) VALUES (5, 'Music');
INSERT INTO categories (id, name) VALUES (6, 'Photo');
INSERT INTO categories (id, name) VALUES (7, 'Clothing');
INSERT INTO categories (id, name) VALUES (8, 'Sports');
INSERT INTO categories (id, name) VALUES (9, 'Tickets/Travel');
INSERT INTO categories (id, name) VALUES (10, 'Toys');
INSERT INTO categories (id, name) VALUES (11, 'Hobbies');
INSERT INTO categories (id, name) VALUES (12, 'Computers');
INSERT INTO categories (id, name) VALUES (13, 'Electronics');
INSERT INTO categories (id, name) VALUES (14, 'Automotive');
INSERT INTO categories (id, name) VALUES (15, 'Everything else');
INSERT INTO categories (id, name) VALUES (16, 'Coins');
INSERT INTO categories (id, name) VALUES (17, 'Jewelry');
INSERT INTO categories (id, name) VALUES (18, 'Antiques/Art');
INSERT INTO categories (id, name) VALUES (19, 'Collectibles');

-- Regions
INSERT INTO regions (id, name) VALUES (1, 'Alabany');
INSERT INTO regions (id, name) VALUES (2, 'Atlanta');
INSERT INTO regions (id, name) VALUES (3, 'Boston');
INSERT INTO regions (id, name) VALUES (4, 'Buffalo');
INSERT INTO regions (id, name) VALUES (5, 'Chicago');
INSERT INTO regions (id, name) VALUES (6, 'Cleveland');
INSERT INTO regions (id, name) VALUES (7, 'Detroit');
INSERT INTO regions (id, name) VALUES (8, 'Honolulu');
INSERT INTO regions (id, name) VALUES (9, 'Houston');
INSERT INTO regions (id, name) VALUES (10, 'Los Angeles');
INSERT INTO regions (id, name) VALUES (11, 'Milwaukee');
INSERT INTO regions (id, name) VALUES (12, 'New Orleans');
INSERT INTO regions (id, name) VALUES (13, 'Phoenix');
INSERT INTO regions (id, name) VALUES (14, 'Portland');
INSERT INTO regions (id, name) VALUES (15, 'San Antonio');
INSERT INTO regions (id, name) VALUES (16, 'San Diego');
INSERT INTO regions (id, name) VALUES (17, 'Seattle');
INSERT INTO regions (id, name) VALUES (18, 'St Louis');
INSERT INTO regions (id, name) VALUES (19, 'Washington');

-- Users
INSERT INTO users (id, nickname, firstname, lastname, email, password, balance, rating, creation_date, region_id, userclass) VALUES (1, 'darryl', 'Darryl', 'Sloan', 'Darryl.Sloan@mail.com', 'JKX29UWD9HQ', 500.50, 10, NOW(), 10, 'GOLD');
INSERT INTO users (id, nickname, firstname, lastname, email, password, balance, rating, creation_date, region_id, userclass) VALUES (2, 'ethan', 'Ethan', 'Benson', 'Ethan.Benson@mail.com', 'OKK69ZXQ7MI', 0.00, 8, NOW(), 10, 'SILVER');
INSERT INTO users (id, nickname, firstname, lastname, email, password, balance, rating, creation_date, region_id, userclass) VALUES (3, 'shelly', 'Shelly', 'Franklin', 'Shelly.Franklin@mail.com', 'GLZ43DIN6SS', 350.00, 7, NOW(), 16, 'SILVER');

-- Items
INSERT INTO items (id, name, description, initial_quantity, initial_price, buy_now_price, reserve_price, start_date, end_date, seller_id, category_id) VALUES (1, 'Joy Division - Unknown Pleasures', 'Out of print in the U.S. UK standard single disc pressing of the 1979 debut album from the moody Manchester quartet.', 5, 7.99, 9.99, 8.49, {fn TIMESTAMPADD(SQL_TSI_DAY, -20, CURRENT_TIMESTAMP)}, {fn TIMESTAMPADD(SQL_TSI_DAY, -10, CURRENT_TIMESTAMP)}, 1, 5);
INSERT INTO items (id, name, description, initial_quantity, initial_price, buy_now_price, reserve_price, start_date, end_date, seller_id, category_id) VALUES (2, 'Beatsteaks - Muffensausen', 'Muffensausen CD+DVD Box-Set. Die Beatsteaks präsentieren: Muffensausen. Das bisher grösste und teuerste Projekt ihrer Bandgeschichte. Vergesst Ben Hur und Twilight.', 3, 15.99, 18.99, 17.99, {fn TIMESTAMPADD(SQL_TSI_DAY, -2, CURRENT_TIMESTAMP)}, {fn TIMESTAMPADD(SQL_TSI_DAY, 9, CURRENT_TIMESTAMP)}, 1, 5);
INSERT INTO items (id, name, description, initial_quantity, initial_price, buy_now_price, reserve_price, start_date, end_date, seller_id, category_id) VALUES (3, 'Phoenix - Bankrupt! (Limited Deluxe Edition) ', '2013 release, the fifth album from the French Alt-Rock/Electro quartet.', 10, 11.99, 15.99, 13.99, {fn TIMESTAMPADD(SQL_TSI_DAY, -3, CURRENT_TIMESTAMP)}, {fn TIMESTAMPADD(SQL_TSI_DAY, 8, CURRENT_TIMESTAMP)}, 1, 5);
INSERT INTO items (id, name, description, initial_quantity, initial_price, buy_now_price, reserve_price, start_date, end_date, seller_id, category_id) VALUES (4, 'Joy Division - Closer', 'Classic second & final from 1980 featuring "Heart & Soul". A true work of art. ', 3, 7.99, 9.99, 8.49, {fn TIMESTAMPADD(SQL_TSI_DAY, -4, CURRENT_TIMESTAMP)}, {fn TIMESTAMPADD(SQL_TSI_DAY, 7, CURRENT_TIMESTAMP)}, 1, 5);
INSERT INTO items (id, name, description, initial_quantity, initial_price, buy_now_price, reserve_price, start_date, end_date, seller_id, category_id) VALUES (5, '2 x Eels Concert Ticket', 'Eels Concert in Berlin', 1, 70.00, 99.00, 85.00, {fn TIMESTAMPADD(SQL_TSI_DAY, -8, CURRENT_TIMESTAMP)}, {fn TIMESTAMPADD(SQL_TSI_DAY, 6, CURRENT_TIMESTAMP)}, 2, 9);
INSERT INTO items (id, name, description, initial_quantity, initial_price, buy_now_price, reserve_price, start_date, end_date, seller_id, category_id) VALUES (6, 'Interpol - Turn on the Bright Lights (Remastered)', 'Turn On The Bright Lights is now fully remastered in a beautiful deluxe hardbound book with unreleased photos and a second disc of bonus tracks, many unreleased, plus the demos and B-sides and a DVD.', 1, 14.99, 18.99, 16.99, {fn TIMESTAMPADD(SQL_TSI_DAY, -4, CURRENT_TIMESTAMP)}, {fn TIMESTAMPADD(SQL_TSI_DAY, 16, CURRENT_TIMESTAMP)}, 2, 5);
INSERT INTO items (id, name, description, initial_quantity, initial_price, buy_now_price, reserve_price, start_date, end_date, seller_id, category_id) VALUES (7, 'Eels Concert Tickets', 'Concert in Berlin', 2, 50.00, 69.00, 55.00, {fn TIMESTAMPADD(SQL_TSI_DAY, -14, CURRENT_TIMESTAMP)}, {fn TIMESTAMPADD(SQL_TSI_DAY, -4, CURRENT_TIMESTAMP)}, 1, 9);

-- Bids
INSERT INTO bids (id, bid_price, max_bid_price, bid_date, quantity, user_id, item_id) VALUES (1, 8.50, 8.98, {fn TIMESTAMPADD(SQL_TSI_DAY, -15, CURRENT_TIMESTAMP)}, 1, 2, 1);
INSERT INTO bids (id, bid_price, max_bid_price, bid_date, quantity, user_id, item_id) VALUES (2, 16.00, 16.50, {fn TIMESTAMPADD(SQL_TSI_DAY, -1, CURRENT_TIMESTAMP)}, 1, 2, 2);
INSERT INTO bids (id, bid_price, max_bid_price, bid_date, quantity, user_id, item_id) VALUES (3, 16.50, 17.50, {fn TIMESTAMPADD(SQL_TSI_DAY, -1, CURRENT_TIMESTAMP)}, 1, 3, 2);
INSERT INTO bids (id, bid_price, max_bid_price, bid_date, quantity, user_id, item_id) VALUES (4, 12.50, 14.00, {fn TIMESTAMPADD(SQL_TSI_DAY, -2, CURRENT_TIMESTAMP)}, 1, 3, 3);
INSERT INTO bids (id, bid_price, max_bid_price, bid_date, quantity, user_id, item_id) VALUES (5, 60.00, 66.00, {fn TIMESTAMPADD(SQL_TSI_DAY, -5, CURRENT_TIMESTAMP)}, 1, 3, 7);
INSERT INTO bids (id, bid_price, max_bid_price, bid_date, quantity, user_id, item_id) VALUES (6, 8.50, 8.55, {fn TIMESTAMPADD(SQL_TSI_DAY, -3, CURRENT_TIMESTAMP)}, 2, 3, 4);
INSERT INTO bids (id, bid_price, max_bid_price, bid_date, quantity, user_id, item_id) VALUES (7, 79.00, 89.50, {fn TIMESTAMPADD(SQL_TSI_DAY, -6, CURRENT_TIMESTAMP)}, 1, 3, 5);
INSERT INTO bids (id, bid_price, max_bid_price, bid_date, quantity, user_id, item_id) VALUES (8, 92.50, 95.00, {fn TIMESTAMPADD(SQL_TSI_DAY, -5, CURRENT_TIMESTAMP)}, 1, 1, 5);

-- BuyNows
INSERT INTO buy_now (id, buy_now_date, quantity, item_id, buyer_id) VALUES (1, {fn TIMESTAMPADD(SQL_TSI_DAY, -3, CURRENT_TIMESTAMP)}, 1, 6, 3);
INSERT INTO buy_now (id, buy_now_date, quantity, item_id, buyer_id) VALUES (2, {fn TIMESTAMPADD(SQL_TSI_DAY, -2, CURRENT_TIMESTAMP)}, 1, 3, 2);

-- InventoryItems
INSERT INTO inventory_items (id, item_id, available_quantity, version) VALUES (1, 1, 4, 1);
INSERT INTO inventory_items (id, item_id, available_quantity, version) VALUES (2, 2, 3, 1);
INSERT INTO inventory_items (id, item_id, available_quantity, version) VALUES (3, 3, 9, 1);
INSERT INTO inventory_items (id, item_id, available_quantity, version) VALUES (4, 4, 3, 1);
INSERT INTO inventory_items (id, item_id, available_quantity, version) VALUES (5, 5, 1, 1);
INSERT INTO inventory_items (id, item_id, available_quantity, version) VALUES (6, 6, 0, 1);
INSERT INTO inventory_items (id, item_id, available_quantity, version) VALUES (7, 7, 1, 1);

-- Comments
INSERT INTO comments (id, comment, comment_date, rating, to_user_id, from_user_id, item_id) VALUES (1, 'No problems, everything was fine.', {fn TIMESTAMPADD(SQL_TSI_DAY, -7, CURRENT_TIMESTAMP)}, 10, 1, 2, 1);
INSERT INTO comments (id, comment, comment_date, rating, to_user_id, from_user_id, item_id) VALUES (2, 'Seller was not very responsive!', {fn TIMESTAMPADD(SQL_TSI_DAY, -2, CURRENT_TIMESTAMP)}, 7, 1, 3, 7);
INSERT INTO comments (id, comment, comment_date, rating, to_user_id, from_user_id, item_id) VALUES (3, 'Top seller.', {fn TIMESTAMPADD(SQL_TSI_DAY, -1, CURRENT_TIMESTAMP)}, 10, 2, 3, 6);

-- SEQUENCE table
INSERT INTO SEQUENCE(SEQ_NAME, SEQ_COUNT) values ('SEQ_GEN', 20);
-- SEQUENCE table update
-- INSERT INTO SEQUENCE(SEQ_NAME, SEQ_COUNT) values ('SEQ_GEN', 0);
-- UPDATE SEQUENCE SET SEQ_COUNT = 20 WHERE SEQ_NAME = 'SEQ_GEN';

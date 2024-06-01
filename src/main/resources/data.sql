INSERT
INTO users (
    full_name,
    email,
    password,
    avatar_url,
    location,
    rating,
    telegram_id,
    role,
    display_email,
    display_telegram)
VALUES
    ('Peter Berger',  'peter.berger@testmail.com',  '$2a$10$cFNxO/yS0zBfPhtnFQ779Od3LiMoqSG0zrh7.Pb98onbi4Oajwwqy', null, 'Lviv',    3, null, 'ROLE_USER',  false, false),
    ('Laura Splice',  'laura.splice@testmail.com',  '$2a$10$cFNxO/yS0zBfPhtnFQ779Od3LiMoqSG0zrh7.Pb98onbi4Oajwwqy', null, 'Dnipro',  4, null, 'ROLE_USER',  false, false),
    ('Amiya Shiro',   'amiya.shiro@testmail.com',   '$2a$10$cFNxO/yS0zBfPhtnFQ779Od3LiMoqSG0zrh7.Pb98onbi4Oajwwqy', null, 'Kharkiv', 3, null, 'ROLE_USER',  false, false),
    ('Saria Darkoff', 'saria.darkoff@testmail.com', '$2a$10$cFNxO/yS0zBfPhtnFQ779Od3LiMoqSG0zrh7.Pb98onbi4Oajwwqy', null, 'Kyiv',    3, null, 'ROLE_USER',  false, false),
    ('Admin',         'admin.bookti@testmail.com',  '$2a$10$Ir2Mm0HKzHmlZZXfagNnV.9cNfXAeeJV47sckzQXGLoQYnG4albMa', null, 'Kyiv',    0, null, 'ROLE_ADMIN', false, false);

INSERT
INTO books (
    owner_id,
    title,
    author,
    genre,
    description,
    image_name,
    image_url,
    language,
    publication_year,
    exchange_format)
VALUES
    (1, 'The Great Gatsby',                                  'F.Scott Fitzgerald',          'fiction', 'Description...', null, null, 'english', 2024, 'gift'),
    (1, 'Golden Fool',                                       'Robin Hobb',                  'fiction', 'Description...', null, null, 'english', 2024, 'exchange'),
    (2, 'Seven Blades in Black',                             'Sam Sykes',                   'fiction', 'Description...', null, null, 'english', 2024, 'exchange'),
    (3, 'One Hundred Years of Solitude',                     'Gabriel García Márquez',      'fiction', 'Description...', null, null, 'english', 2024, 'exchange'),
    (2, 'Love in the Time of Cholera',                       'Gabriel García Márquez',      'fiction', 'Description...', null, null, 'english', 2024, 'gift'),
    (4, 'American Prometheus: The Inspiration',              'Kai Bird, Martin J. Sherwin', 'fiction', 'Description...', null, null, 'english', 2024, 'gift'),
    (4, 'The 48 Lows of Power',                              'Robert Greene',               'fiction', 'Description...', null, null, 'english', 2024, 'exchange'),
    (1, 'Can`t Hurt Me: Master Your Mind and Defy the Odds', 'David Goggins',               'fiction', 'Description...', null, null, 'english', 2024, 'exchange'),
    (1, 'The Woman in Me',                                   'Britney Spears',              'fiction', 'Description...', null, null, 'english', 2024, 'gift'),
    (3, 'Jackie: Public, Private, Secret',                   'J, Randy Taraborrelli',       'fiction', 'Description...', null, null, 'english', 2024, 'exchange');


INSERT
INTO user_review
(reviewer_name, message, rating, creation_date, avatar_url, user_id, reviewers_id)
VALUES ('Peter Berger', 'Text Review one!)', 4, '2024-05-10',
        'https://cdn.iconscout.com/icon/free/png-256/free-avatar-370-456322.png', 2, 3),
       ('Laura Splice', 'Text Review two!)', 3, '2024-05-09',
        'https://cdn.iconscout.com/icon/free/png-256/free-avatar-370-456322.png', 2, 2),
       ('Amiya Shiro', 'Text Review next!', 2, '2024-05-11',
        'https://cdn.iconscout.com/icon/free/png-256/free-avatar-370-456322.png', 2, 4),
       ('Saria Darkoff', 'Text Review next next!', 4, '2024-05-04',
        'https://cdn.iconscout.com/icon/free/png-256/free-avatar-370-456322.png', 2, 3);

INSERT INTO clients (id, address, document, name, password, type, created_at, updated_at)
VALUES
    ('5e445995-20fc-45b4-b144-24d37ad93509', '4 Privet Drive, Little Whinging, Surrey', '12345678901', 'Harry Potter', 'gryffindor', 'CUSTOMER', now(), now()),
    ('40b28fcf-647c-4beb-bb6c-5bd7407f61e3', 'The Shire, Hobbiton', '12345678901234', 'Frodo Baggins', 'theonering', 'CUSTOMER', now(), now()),
    ('26f3268f-8042-43c7-a140-9f1efceb8a1a', 'Hogwarts School of Witchcraft and Wizardry', '12345678901235', 'Gringotts Bank', 'dragon', 'COMPANY', now(), now()),
    ('fb7ded38-2df2-4f91-bec2-baa557cb49e9', 'Middle-earth', '12345678904', 'The Fellowship of the Ring', 'fellowship', 'COMPANY', now(), now());

INSERT INTO accounts (id, agency, client_id, number, status, created_at, updated_at)
VALUES
    (RANDOM_UUID(), 1234, '5e445995-20fc-45b4-b144-24d37ad93509', 1, 'ACTIVE', now(), now()),
    (RANDOM_UUID(), 5678, '40b28fcf-647c-4beb-bb6c-5bd7407f61e3', 1, 'ACTIVE', now(), now()),
    (RANDOM_UUID(), 1234, '26f3268f-8042-43c7-a140-9f1efceb8a1a', 2, 'ACTIVE', now(), now()),
    (RANDOM_UUID(), 5678, 'fb7ded38-2df2-4f91-bec2-baa557cb49e9', 2, 'INACTIVE', now(), now()),
    (RANDOM_UUID(), 5678, 'fb7ded38-2df2-4f91-bec2-baa557cb49e9', 3, 'ACTIVE', now(), now());
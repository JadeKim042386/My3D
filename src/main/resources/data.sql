-- 기업
insert into company (company_name, homepage)
values ('(주)My3D', 'www.my3d.com');

-- Refresh Token
insert into user_refresh_token (refresh_token, reissue_count)
values ('eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3MDUwNTkzNjAsImV4cCI6MTczNjU5OTM2MH0.Zz-gxdujoo71RZvV59zImALwFY-_kzKTZlwOAoJgIqQ',
        0),
       ('eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3MDUwNTkzNjAsImV4cCI6MTczNjU5OTM2MH0.Zz-gxdujoo71RZvV59zImALwFY-_kzKTZlwOAoJgIqQ',
        0),
       ('eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3MDUwNTkzNjAsImV4cCI6MTczNjU5OTM2MH0.Zz-gxdujoo71RZvV59zImALwFY-_kzKTZlwOAoJgIqQ',
        0);

-- 테스트 계정 3개
insert into user_account (company_id, user_refresh_token_id, email, user_password, nickname, phone, detail, street,
                          zipcode, user_role, created_at, modified_at)
values (null, 1, 'jk042386@gmail.com', '$2a$10$P3b4xJ2sY4t6zMhEroFV5OeVcyspmj8EA4.dskRDwaU/H0Wd5Alfa', 'Joo',
        '01012341234', 'detail', 'street', '11111', 'ADMIN', now(), now()),
       (null, 2, 'a@gmail.com', '$2a$10$P3b4xJ2sY4t6zMhEroFV5OeVcyspmj8EA4.dskRDwaU/H0Wd5Alfa', 'A', '01011111111',
        'detail', 'street', '11111', 'USER', now(), now()),
       (1, 3, 'jujoo042386@gmail.com', '$2a$10$P3b4xJ2sY4t6zMhEroFV5OeVcyspmj8EA4.dskRDwaU/H0Wd5Alfa', 'Jujoo',
        '01022222222', 'detail', 'street', '11111', 'COMPANY', now(), now());

-- 치수 옵션
insert into dimension_option (option_name)
values ('option1'),
       ('option2'),
       ('option3'),
       ('option4'),
       ('option5'),
       ('option6'),
       ('option7'),
       ('option8'),
       ('option9'),
       ('option10');

-- 파일 10개
insert into article_file (dimension_option_id, byte_size, original_file_name, file_name, file_extension)
values (1, 4172, 'model0.stl', '5a93b139-bd32-4514-a798-e0cd2471995e.stl', 'stl'),
       (2, 9598, 'model1.stp', 'da50408b-7655-4a80-9927-2890007e9e30.stp', 'stp'),
       (3, 3861, 'model2.stp', '9d495214-d376-4b0e-a03b-67b5c2b5afdb.stp', 'stp'),
       (4, 9412, 'model3.stl', '36bab91a-ab82-49c6-a17c-911761eb892c.stl', 'stl'),
       (5, 3957, 'model4.stp', '18f39d74-294c-49bb-b31d-ec0d64c193a5.stp', 'stp'),
       (6, 4692, 'model5.stl', 'e6cbed0d-f1df-4002-bc25-27998f7ab44f.stl', 'stl'),
       (7, 8659, 'model6.stp', '9bc6cfbf-8740-4981-a331-f8f0fa2565c9.stp', 'stp'),
       (8, 1605, 'model7.stp', '575612c8-d349-4951-89c8-3c748dc9b884.stp', 'stp'),
       (9, 1973, 'model8.stl', '1a3c4eb6-f117-4465-bf88-62be0ea7f9c3.stl', 'stl'),
       (10, 5555, 'model9.stl', '1a3c4eb6-f117-4465-bf88-62be0ea7f9cq.stl', 'stl');

-- 게시글 10개
insert into article (article_file_id, user_account_id, title, content, created_at, modified_at, article_type,
                     article_category, like_count)
values (1, 1, 'Morbi non lectus.',
        'Duis consequat dui nec nisi volutpat eleifend. Donec ut dolor. Morbi vel lectus in quam fringilla rhoncus.',
        '2022-12-15 02:41:19', '2023-02-23 23:27:58', 'MODEL', 'MUSIC', 1),
       (2, 1, 'Phasellus id sapien in sapien iaculis congue.',
        'Phasellus sit amet erat. Nulla tempus. Vivamus in felis eu sapien cursus vestibulum.', '2022-07-29 07:00:14',
        '2023-02-10 04:07:29', 'MODEL', 'MUSIC', 0),
       (3, 1, 'Morbi non quam nec dui luctus rutrum.',
        'Proin leo odio, porttitor id, consequat in, consequat ut, nulla. Sed accumsan felis. Ut at dolor quis odio consequat varius.',
        '2023-05-25 14:26:02', '2023-04-12 06:13:46', 'MODEL', 'MUSIC', 0),
       (4, 3, 'Etiam vel augue.',
        'Sed sagittis. Nam congue, risus semper porta volutpat, quam pede lobortis ligula, sit amet eleifend pede libero quis orci. Nullam molestie nibh in lectus.',
        '2023-03-27 23:12:02', '2022-07-15 10:32:48', 'MODEL', 'MUSIC', 0),
       (5, 1, 'Curabitur convallis.',
        'Fusce posuere felis sed lacus. Morbi sem mauris, laoreet ut, rhoncus aliquet, pulvinar sed, nisl. Nunc rhoncus dui vel sem.',
        '2023-02-07 19:12:16', '2023-03-29 15:11:34', 'MODEL', 'MUSIC', 0),
       (6, 3, 'Donec vitae nisi.',
        'Curabitur in libero ut massa volutpat convallis. Morbi odio odio, elementum eu, interdum eu, tincidunt in, leo. Maecenas pulvinar lobortis est.',
        '2022-10-26 10:56:28', '2023-02-24 10:22:47', 'MODEL', 'MUSIC', 1),
       (7, 3, 'Vivamus metus arcu, adipiscing molestie, hendrerit at, vulputate vitae, nisl.',
        'In sagittis dui vel nisl. Duis ac nibh. Fusce lacus purus, aliquet at, feugiat non, pretium quis, lectus.',
        '2023-02-11 18:39:15', '2022-08-29 05:07:08', 'MODEL', 'CARS_VEHICLES', 1),
       (8, 1, 'In congue.',
        'Vestibulum ac est lacinia nisi venenatis tristique. Fusce congue, diam id ornare imperdiet, sapien urna pretium nisl, ut volutpat sapien arcu sed augue. Aliquam erat volutpat.',
        '2023-01-15 05:59:09', '2023-03-26 17:46:37', 'MODEL', 'CARS_VEHICLES', 0),
       (9, 3, 'In hac habitasse platea dictumst.',
        'Curabitur in libero ut massa volutpat convallis. Morbi odio odio, elementum eu, interdum eu, tincidunt in, leo. Maecenas pulvinar lobortis est.',
        '2023-01-06 15:54:30', '2022-08-20 04:28:34', 'MODEL', 'FASHION_STYLE', 2),
       (10, 3,
        'Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Nulla dapibus dolor vel est.',
        'Quisque porta volutpat erat. Quisque erat eros, viverra eget, congue eget, semper rutrum, nulla. Nunc purus.',
        '2022-10-05 14:50:19', '2022-07-31 12:38:29', 'MODEL', 'FASHION_STYLE', 1);

-- 치수
insert into dimension (dimension_option_id, dim_name, dim_value, dim_unit)
values (1, '높이', 100.0, 'MM'),
       (2, '폭', 100.0, 'MM'),
       (3, '높이', 100.0, 'MM'),
       (4, '폭', 100.0, 'MM'),
       (5, '높이', 100.0, 'MM'),
       (6, '폭', 100.0, 'MM'),
       (7, '높이', 100.0, 'MM'),
       (8, '폭', 100.0, 'MM'),
       (9, '높이', 100.0, 'MM'),
       (10, '폭', 100.0, 'MM');

-- 댓글 10개
insert into article_comment (user_account_id, article_id, content, parent_comment_id, modified_at, created_at)
values (1, 6, 'Aliquam sit amet diam in magna bibendum imperdiet.', null, '2023-02-22 05:42:56', '2023-02-22 05:42:56'),
       (2, 8, 'Ut at dolor quis odio consequat varius.', null, '2023-02-27 11:43:51', '2022-12-28 19:25:53'),
       (1, 1, 'In hac habitasse platea dictumst.', null, '2023-06-10 10:38:09', '2023-06-20 03:54:32'),
       (2, 10, 'Aliquam quis turpis eget elit sodales scelerisque.', null, '2023-05-24 11:18:46',
        '2023-02-12 01:11:03'),
       (3, 5, 'In hac habitasse platea dictumst.', null, '2022-08-21 15:51:26', '2022-08-21 15:51:26'),
       (2, 6, 'Proin leo odio, porttitor id, consequat in, consequat ut, nulla.', 1, '2023-06-20 21:55:40',
        '2022-07-21 16:24:58'),
       (1, 9, 'Curabitur convallis.', null, '2022-09-10 06:39:06', '2022-09-10 06:39:06'),
       (2, 3, 'Cras in purus eu magna vulputate luctus.', null, '2022-08-23 13:29:00', '2022-10-02 21:19:58'),
       (3, 5, 'Aenean auctor gravida sem.', 5, '2022-07-15 03:56:20', '2023-01-20 15:00:17'),
       (3, 2, 'Curabitur gravida nisi at nibh.', null, '2023-03-23 16:56:34', '2022-12-10 23:41:13');

-- 좋아요 6개
insert into article_like (article_id, user_account_id, created_at, modified_at)
values (1, 1, '2022-10-26 21:15:44', '2022-10-26 21:15:44'),
       (9, 1, '2023-07-23 03:48:34', '2023-07-23 03:48:34'),
       (9, 2, '2022-09-30 18:29:27', '2022-09-30 18:29:27'),
       (10, 1, '2022-11-20 06:46:07', '2022-11-20 06:46:07'),
       (7, 3, '2023-03-12 16:43:47', '2023-03-12 16:43:47'),
       (6, 3, '2023-06-23 21:19:43', '2023-06-23 21:19:43');

-- 알람
insert into alarm (receiver_id, alarm_type, article_id, sender_id, target_id, is_checked, modified_at, created_at)
values (3, 'NEW_COMMENT', 10, 2, 4, false, now(), now()),
       (3, 'NEW_COMMENT', 10, 1, 4, false, now(), now());
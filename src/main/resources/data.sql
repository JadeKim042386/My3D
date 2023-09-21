-- 기업
insert into company (company_name, homepage, created_at, modified_at, created_by, modified_by) values
('(주)My3D', 'www.my3d.com', now(), now(), 'jujoo', 'jujoo');

-- 테스트 계정 3개
insert into user_account (email, user_password, nickname, phone, detail, street, zipcode, sign_up, user_role, company_id, created_at, created_by, modified_at, modified_by) values
('jk042386@gmail.com', '$2a$10$P3b4xJ2sY4t6zMhEroFV5OeVcyspmj8EA4.dskRDwaU/H0Wd5Alfa', 'Joo', '01012341234', null, null, null, true, 'ADMIN', null, now(), 'joo', now(), 'joo'),
('a@gmail.com', '$2a$10$P3b4xJ2sY4t6zMhEroFV5OeVcyspmj8EA4.dskRDwaU/H0Wd5Alfa', 'A', '01011111111', null, null, null, true, 'USER', null, now(), 'a', now(), 'a'),
('jujoo042386@gmail.com', '$2a$10$P3b4xJ2sY4t6zMhEroFV5OeVcyspmj8EA4.dskRDwaU/H0Wd5Alfa', 'Jujoo', '01022222222', null, null, null, true, 'COMPANY', 1, now(), 'jujoo', now(), 'jujoo');

-- 가격
insert into price (price_value, delivery_price, created_at, modified_at, created_by, modified_by) values
(10000, 3000, '2022-10-26 21:15:44', '2022-10-26 21:15:44', 'joo', 'joo'),
(10000, 3000, '2022-10-26 21:15:44', '2022-10-26 21:15:44', 'joo', 'joo'),
(10000, 3000, '2022-10-26 21:15:44', '2022-10-26 21:15:44', 'joo', 'joo'),
(10000, 3000, '2022-10-26 21:15:44', '2022-10-26 21:15:44', 'joo', 'joo'),
(10000, 3000, '2022-10-26 21:15:44', '2022-10-26 21:15:44', 'joo', 'joo'),
(10000, 3000, '2022-10-26 21:15:44', '2022-10-26 21:15:44', 'joo', 'joo'),
(10000, 3000, '2022-10-26 21:15:44', '2022-10-26 21:15:44', 'joo', 'joo'),
(10000, 3000, '2022-10-26 21:15:44', '2022-10-26 21:15:44', 'joo', 'joo'),
(10000, 3000, '2022-10-26 21:15:44', '2022-10-26 21:15:44', 'joo', 'joo'),
(10000, 3000, '2022-10-26 21:15:44', '2022-10-26 21:15:44', 'joo', 'joo');

-- 게시글 10개
insert into article (email, title, summary, content, created_by, modified_by, created_at, modified_at, article_type, article_category, like_count, price_id) values
('jk042386@gmail.com', 'Morbi non lectus.', 'Morbi non lectus.', 'Duis consequat dui nec nisi volutpat eleifend. Donec ut dolor. Morbi vel lectus in quam fringilla rhoncus.', 'joo', 'joo', '2022-12-15 02:41:19', '2023-02-23 23:27:58', 'MODEL', 'MUSIC', 1, 1),
('jk042386@gmail.com', 'Phasellus id sapien in sapien iaculis congue.', 'Phasellus id sapien in sapien iaculis congue.', 'Phasellus sit amet erat. Nulla tempus. Vivamus in felis eu sapien cursus vestibulum.', 'joo', 'joo', '2022-07-29 07:00:14', '2023-02-10 04:07:29', 'MODEL', 'MUSIC', 0, 2),
('jk042386@gmail.com', 'Morbi non quam nec dui luctus rutrum.', 'Morbi non quam nec dui luctus rutrum.', 'Proin leo odio, porttitor id, consequat in, consequat ut, nulla. Sed accumsan felis. Ut at dolor quis odio consequat varius.', 'joo', 'joo', '2023-05-25 14:26:02', '2023-04-12 06:13:46', 'MODEL', 'MUSIC', 0, 3),
('jk042386@gmail.com', 'Etiam vel augue.', 'Etiam vel augue.', 'Sed sagittis. Nam congue, risus semper porta volutpat, quam pede lobortis ligula, sit amet eleifend pede libero quis orci. Nullam molestie nibh in lectus.', 'joo', 'joo', '2023-03-27 23:12:02', '2022-07-15 10:32:48', 'MODEL', 'MUSIC', 0, 4),
('jk042386@gmail.com', 'Curabitur convallis.', 'Curabitur convallis.', 'Fusce posuere felis sed lacus. Morbi sem mauris, laoreet ut, rhoncus aliquet, pulvinar sed, nisl. Nunc rhoncus dui vel sem.', 'joo', 'joo', '2023-02-07 19:12:16', '2023-03-29 15:11:34', 'MODEL', 'MUSIC', 0, 5),
('jujoo042386@gmail.com', 'Donec vitae nisi.', 'Donec vitae nisi.', 'Curabitur in libero ut massa volutpat convallis. Morbi odio odio, elementum eu, interdum eu, tincidunt in, leo. Maecenas pulvinar lobortis est.', 'Hayward', 'Valentina', '2022-10-26 10:56:28', '2023-02-24 10:22:47', 'MODEL', 'MUSIC', 1, 6),
('jujoo042386@gmail.com', 'Vivamus metus arcu, adipiscing molestie, hendrerit at, vulputate vitae, nisl.', 'Vivamus metus arcu,', 'In sagittis dui vel nisl. Duis ac nibh. Fusce lacus purus, aliquet at, feugiat non, pretium quis, lectus.', 'Remus', 'Natale', '2023-02-11 18:39:15', '2022-08-29 05:07:08', 'MODEL', 'CARS_VEHICLES', 1, 7),
('jk042386@gmail.com', 'In congue.', 'In congue.', 'Vestibulum ac est lacinia nisi venenatis tristique. Fusce congue, diam id ornare imperdiet, sapien urna pretium nisl, ut volutpat sapien arcu sed augue. Aliquam erat volutpat.', 'Dicky', 'Dud', '2023-01-15 05:59:09', '2023-03-26 17:46:37', 'MODEL', 'CARS_VEHICLES', 0, 8),
('jujoo042386@gmail.com', 'In hac habitasse platea dictumst.', 'In hac habitasse platea dictumst.', 'Curabitur in libero ut massa volutpat convallis. Morbi odio odio, elementum eu, interdum eu, tincidunt in, leo. Maecenas pulvinar lobortis est.', 'Karee', 'Virgie', '2023-01-06 15:54:30', '2022-08-20 04:28:34', 'MODEL', 'FASHION_STYLE', 2, 9),
('jujoo042386@gmail.com',  'Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Nulla dapibus dolor vel est.', 'Vestibulum ante ipsum', 'Quisque porta volutpat erat. Quisque erat eros, viverra eget, congue eget, semper rutrum, nulla. Nunc purus.', 'Florina', 'Maurine', '2022-10-05 14:50:19', '2022-07-31 12:38:29', 'MODEL', 'FASHION_STYLE', 1, 10);

-- 파일 10개
insert into article_file (article_id, byte_size, original_file_name, file_name, file_extension, created_at, modified_at, created_by, modified_by) values
(1, 4172, 'model0.stl', '5a93b139-bd32-4514-a798-e0cd2471995e.stl', 'stl', '2023-01-01 08:52:42', '2023-01-16 13:24:34', 'joo', 'joo'),
(2, 9598, 'model1.stp', 'da50408b-7655-4a80-9927-2890007e9e30.stp', 'stp', '2023-04-20 13:15:23', '2023-04-12 14:04:14', 'jujoo', 'jujoo'),
(3, 3861, 'model2.stp','9d495214-d376-4b0e-a03b-67b5c2b5afdb.stp', 'stp', '2023-03-19 17:20:14', '2023-05-08 04:26:34', 'jujoo', 'jujoo'),
(4, 9412, 'model3.stl', '36bab91a-ab82-49c6-a17c-911761eb892c.stl', 'stl', '2023-05-24 09:10:39', '2022-09-11 06:27:46', 'a', 'a'),
(5, 3957, 'model4.stp', '18f39d74-294c-49bb-b31d-ec0d64c193a5.stp', 'stp', '2022-08-10 15:43:09', '2022-09-09 19:17:20', 'a', 'a'),
(6, 4692, 'model5.stl', 'e6cbed0d-f1df-4002-bc25-27998f7ab44f.stl', 'stl', '2022-09-26 17:37:48', '2023-02-08 13:22:56', 'joo', 'joo'),
(7, 8659, 'model6.stp', '9bc6cfbf-8740-4981-a331-f8f0fa2565c9.stp', 'stp', '2023-02-06 04:49:18', '2023-08-05 15:54:58', 'joo', 'joo'),
(8, 1605, 'model7.stp', '575612c8-d349-4951-89c8-3c748dc9b884.stp', 'stp', '2023-01-21 23:34:41', '2022-09-14 00:52:47', 'joo', 'joo'),
(9, 1973, 'model8.stl', '1a3c4eb6-f117-4465-bf88-62be0ea7f9c3.stl', 'stl', '2022-11-01 01:49:02', '2023-05-21 20:08:55', 'a', 'a'),
(10, 5555, 'model9.stl', '1a3c4eb6-f117-4465-bf88-62be0ea7f9cq.stl', 'stl', '2022-11-01 01:59:02', '2023-05-21 20:08:30', 'a', 'a');

-- 댓글 10개
insert into article_comment (email, article_id, content, parent_comment_id, created_by, modified_at, modified_by, created_at) values
('jk042386@gmail.com', 6, 'Aliquam sit amet diam in magna bibendum imperdiet.', null, 'joo', '2023-02-22 05:42:56', 'joo', '2023-02-22 05:42:56'),
('a@gmail.com', 8, 'Ut at dolor quis odio consequat varius.', null, 'joo', '2023-02-27 11:43:51', 'joo', '2022-12-28 19:25:53'),
('jk042386@gmail.com', 1, 'In hac habitasse platea dictumst.', null, 'joo', '2023-06-10 10:38:09', 'joo', '2023-06-20 03:54:32'),
('a@gmail.com', 10, 'Aliquam quis turpis eget elit sodales scelerisque.', null, 'a', '2023-05-24 11:18:46', 'a', '2023-02-12 01:11:03'),
('jujoo042386@gmail.com', 5, 'In hac habitasse platea dictumst.', null, 'a', '2022-08-21 15:51:26', 'a', '2022-08-21 15:51:26'),
('a@gmail.com', 6, 'Proin leo odio, porttitor id, consequat in, consequat ut, nulla.', 1, 'a', '2023-06-20 21:55:40', 'a', '2022-07-21 16:24:58'),
('jk042386@gmail.com', 9, 'Curabitur convallis.', null, 'jujoo', '2022-09-10 06:39:06', 'jujoo', '2022-09-10 06:39:06'),
('a@gmail.com', 3, 'Cras in purus eu magna vulputate luctus.', null, 'jujoo', '2022-08-23 13:29:00', 'jujoo', '2022-10-02 21:19:58'),
('jujoo042386@gmail.com', 5, 'Aenean auctor gravida sem.', 5, 'jujoo', '2022-07-15 03:56:20', 'jujoo', '2023-01-20 15:00:17'),
('jujoo042386@gmail.com', 2, 'Curabitur gravida nisi at nibh.', null, 'jujoo', '2023-03-23 16:56:34', 'jujoo', '2022-12-10 23:41:13');

-- 좋아요 6개
insert into article_like (article_id, email, created_at, modified_at, created_by, modified_by) values
(1, 'jk042386@gmail.com', '2022-10-26 21:15:44', '2022-10-26 21:15:44', 'joo', 'joo'),
(9, 'jk042386@gmail.com', '2023-07-23 03:48:34', '2023-07-23 03:48:34', 'joo', 'joo'),
(9, 'a@gmail.com', '2022-09-30 18:29:27', '2022-09-30 18:29:27', 'a', 'a'),
(10, 'jk042386@gmail.com', '2022-11-20 06:46:07', '2022-11-20 06:46:07', 'joo', 'joo'),
(7, 'jujoo042386@gmail.com', '2023-03-12 16:43:47', '2023-03-12 16:43:47', 'jujoo', 'jujoo'),
(6, 'jujoo042386@gmail.com', '2023-06-23 21:19:43', '2023-06-23 21:19:43', 'jujoo', 'jujoo');

-- 상품옵션
insert into good_option (article_id, option_name, add_price, printing_tech, material, created_at, modified_at, created_by, modified_by) values
(1, 'option1', 0, 'LSA', 'lesin', '2022-10-26 21:15:44', '2022-10-26 21:15:44', 'joo', 'joo'),
(1, 'option2', 1000, 'LSA', 'lesin', '2022-10-26 21:15:44', '2022-10-26 21:15:44', 'joo', 'joo');

-- 치수
insert into dimension (good_option_id, dim_name, dim_value, dim_unit, created_at, modified_at, created_by, modified_by) values
(1, '높이', 100.0, 'MM', '2022-10-26 21:15:44', '2022-10-26 21:15:44', 'joo', 'joo'),
(2, '폭', 100.0, 'MM', '2022-10-26 21:15:44', '2022-10-26 21:15:44', 'joo', 'joo');

-- 주문
insert into orders (email, company_id, status, product_name, detail, street, zipcode, created_by, modified_at, modified_by, created_at) values
('a@gmail.com', 1, 'REQUEST', 'example product1', '111', '서울특별시', '11111', 'a', '2023-05-24 11:18:46', 'a', '2023-02-12 01:11:03'),
('a@gmail.com', 1, 'EXAMINE', 'example product2', '111', '서울특별시', '11111', 'a', '2023-05-24 11:18:46', 'a', '2023-02-12 01:11:03'),
('a@gmail.com', 1, 'PRINTING', 'example product3', '111', '서울특별시', '11111', 'a', '2023-05-24 11:18:46', 'a', '2023-02-12 01:11:03'),
('a@gmail.com', 1, 'DELIVERY', 'example product4', '111', '서울특별시', '11111', 'a', '2023-05-24 11:18:46', 'a', '2023-02-12 01:11:03');

-- 알람
insert into alarm (email, alarm_type, from_user_nickname, target_id, is_checked, created_by, modified_at, modified_by, created_at) values
('jujoo042386@gmail.com', 'NEW_COMMENT_ON_POST', 'a', 4, false, 'a', now(), 'a', now()),
('jujoo042386@gmail.com', 'NEW_LIKE_ON_POST', 'a', 4, false, 'a', now(), 'a', now()),
('jujoo042386@gmail.com', 'NEW_ORDER', 'a', 4, false, 'a', now(), 'a', now());


-- 테스트 계정 3개
insert into user_account (user_id, user_password, nickname, email, user_role, created_at, created_by, modified_at, modified_by) values
('joo', '{noop}1234asdf', 'Joo', 'jk042386@gmail.com', 'ADMIN', now(), 'joo', now(), 'joo'),
('a', '{noop}1234asdf', 'A', 'a@gmail.com', 'USER', now(), 'a', now(), 'a'),
('jujoo', '{noop}1234asdf', 'Jujoo', 'jujoo042386@gmail.com', 'COMPANY', now(), 'jujoo', now(), 'jujoo');

-- 파일 10개
insert into article_file (byte_size, original_file_name, file_name, file_extension, created_at, modified_at, created_by, modified_by) values
(4172, 'model0.stl', '5a93b139-bd32-4514-a798-e0cd2471995e.stl', 'stl', '2023-01-01 08:52:42', '2023-01-16 13:24:34', 'joo', 'joo'),
(9598, 'model1.stp', 'da50408b-7655-4a80-9927-2890007e9e30.stp', 'stp', '2023-04-20 13:15:23', '2023-04-12 14:04:14', 'jujoo', 'jujoo'),
(3861, 'model2.stp','9d495214-d376-4b0e-a03b-67b5c2b5afdb.stp', 'stp', '2023-03-19 17:20:14', '2023-05-08 04:26:34', 'jujoo', 'jujoo'),
(9412, 'model3.stl', '36bab91a-ab82-49c6-a17c-911761eb892c.stl', 'stl', '2023-05-24 09:10:39', '2022-09-11 06:27:46', 'a', 'a'),
(3957, 'model4.stp', '18f39d74-294c-49bb-b31d-ec0d64c193a5.stp', 'stp', '2022-08-10 15:43:09', '2022-09-09 19:17:20', 'a', 'a'),
(4692, 'model5.stl', 'e6cbed0d-f1df-4002-bc25-27998f7ab44f.stl', 'stl', '2022-09-26 17:37:48', '2023-02-08 13:22:56', 'joo', 'joo'),
(8659, 'model6.stp', '9bc6cfbf-8740-4981-a331-f8f0fa2565c9.stp', 'stp', '2023-02-06 04:49:18', '2023-08-05 15:54:58', 'joo', 'joo'),
(1605, 'model7.stp', '575612c8-d349-4951-89c8-3c748dc9b884.stp', 'stp', '2023-01-21 23:34:41', '2022-09-14 00:52:47', 'joo', 'joo'),
(1973, 'model8.stl', '1a3c4eb6-f117-4465-bf88-62be0ea7f9c3.stl', 'stl', '2022-11-01 01:49:02', '2023-05-21 20:08:55', 'a', 'a'),
(5555, 'model9.stl', '1a3c4eb6-f117-4465-bf88-62be0ea7f9cq.stl', 'stl', '2022-11-01 01:59:02', '2023-05-21 20:08:30', 'a', 'a');

-- 게시글 10개
insert into article (user_id, article_file_id, title, content, created_by, modified_by, created_at, modified_at, article_type, article_category, like_count) values
('joo', 1, 'Morbi non lectus.', 'Duis consequat dui nec nisi volutpat eleifend. Donec ut dolor. Morbi vel lectus in quam fringilla rhoncus.', 'joo', 'joo', '2022-12-15 02:41:19', '2023-02-23 23:27:58', 'MODEL', 'MUSIC', 1),
('joo', 2, 'Phasellus id sapien in sapien iaculis congue.', 'Phasellus sit amet erat. Nulla tempus. Vivamus in felis eu sapien cursus vestibulum.', 'joo', 'joo', '2022-07-29 07:00:14', '2023-02-10 04:07:29', 'MODEL', 'MUSIC', 0),
('joo', 3, 'Morbi non quam nec dui luctus rutrum.', 'Proin leo odio, porttitor id, consequat in, consequat ut, nulla. Sed accumsan felis. Ut at dolor quis odio consequat varius.', 'joo', 'joo', '2023-05-25 14:26:02', '2023-04-12 06:13:46', 'MODEL', 'MUSIC', 0),
('joo', 4, 'Etiam vel augue.', 'Sed sagittis. Nam congue, risus semper porta volutpat, quam pede lobortis ligula, sit amet eleifend pede libero quis orci. Nullam molestie nibh in lectus.', 'joo', 'joo', '2023-03-27 23:12:02', '2022-07-15 10:32:48', 'MODEL', 'MUSIC', 0),
('joo', 5, 'Curabitur convallis.', 'Fusce posuere felis sed lacus. Morbi sem mauris, laoreet ut, rhoncus aliquet, pulvinar sed, nisl. Nunc rhoncus dui vel sem.', 'joo', 'joo', '2023-02-07 19:12:16', '2023-03-29 15:11:34', 'MODEL', 'MUSIC', 0),
('a', 6, 'Donec vitae nisi.', 'Curabitur in libero ut massa volutpat convallis. Morbi odio odio, elementum eu, interdum eu, tincidunt in, leo. Maecenas pulvinar lobortis est.', 'Hayward', 'Valentina', '2022-10-26 10:56:28', '2023-02-24 10:22:47', 'MODEL', 'MUSIC', 1),
('a', 7, 'Vivamus metus arcu, adipiscing molestie, hendrerit at, vulputate vitae, nisl.', 'In sagittis dui vel nisl. Duis ac nibh. Fusce lacus purus, aliquet at, feugiat non, pretium quis, lectus.', 'Remus', 'Natale', '2023-02-11 18:39:15', '2022-08-29 05:07:08', 'MODEL', 'CARS_VEHICLES', 1),
('joo', 8, 'In congue.', 'Vestibulum ac est lacinia nisi venenatis tristique. Fusce congue, diam id ornare imperdiet, sapien urna pretium nisl, ut volutpat sapien arcu sed augue. Aliquam erat volutpat.', 'Dicky', 'Dud', '2023-01-15 05:59:09', '2023-03-26 17:46:37', 'MODEL', 'CARS_VEHICLES', 0),
('jujoo', 9, 'In hac habitasse platea dictumst.', 'Curabitur in libero ut massa volutpat convallis. Morbi odio odio, elementum eu, interdum eu, tincidunt in, leo. Maecenas pulvinar lobortis est.', 'Karee', 'Virgie', '2023-01-06 15:54:30', '2022-08-20 04:28:34', 'MODEL', 'FASHION_STYLE', 2),
('jujoo', 10, 'Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Nulla dapibus dolor vel est.', 'Quisque porta volutpat erat. Quisque erat eros, viverra eget, congue eget, semper rutrum, nulla. Nunc purus.', 'Florina', 'Maurine', '2022-10-05 14:50:19', '2022-07-31 12:38:29', 'MODEL', 'FASHION_STYLE', 1);

-- 댓글 10개
insert into article_comment (user_id, article_id, content, parent_comment_id, created_by, modified_at, modified_by, created_at) values
('joo', 6, 'Aliquam sit amet diam in magna bibendum imperdiet.', null, 'joo', '2023-02-22 05:42:56', 'joo', '2023-02-22 05:42:56'),
('a', 8, 'Ut at dolor quis odio consequat varius.', null, 'joo', '2023-02-27 11:43:51', 'joo', '2022-12-28 19:25:53'),
('joo', 1, 'In hac habitasse platea dictumst.', null, 'joo', '2023-06-10 10:38:09', 'joo', '2023-06-20 03:54:32'),
('a', 10, 'Aliquam quis turpis eget elit sodales scelerisque.', null, 'a', '2023-05-24 11:18:46', 'a', '2023-02-12 01:11:03'),
('jujoo', 5, 'In hac habitasse platea dictumst.', null, 'a', '2022-08-21 15:51:26', 'a', '2022-08-21 15:51:26'),
('a', 6, 'Proin leo odio, porttitor id, consequat in, consequat ut, nulla.', 1, 'a', '2023-06-20 21:55:40', 'a', '2022-07-21 16:24:58'),
('joo', 9, 'Curabitur convallis.', null, 'jujoo', '2022-09-10 06:39:06', 'jujoo', '2022-09-10 06:39:06'),
('a', 3, 'Cras in purus eu magna vulputate luctus.', null, 'jujoo', '2022-08-23 13:29:00', 'jujoo', '2022-10-02 21:19:58'),
('jujoo', 5, 'Aenean auctor gravida sem.', 5, 'jujoo', '2022-07-15 03:56:20', 'jujoo', '2023-01-20 15:00:17'),
('jujoo', 2, 'Curabitur gravida nisi at nibh.', null, 'jujoo', '2023-03-23 16:56:34', 'jujoo', '2022-12-10 23:41:13');

-- 좋아요 6개
insert into article_like (article_id, user_id, created_at, modified_at, created_by, modified_by) values
(1, 'joo', '2022-10-26 21:15:44', '2022-10-26 21:15:44', 'joo', 'joo'),
(9, 'joo', '2023-07-23 03:48:34', '2023-07-23 03:48:34', 'joo', 'joo'),
(9, 'a', '2022-09-30 18:29:27', '2022-09-30 18:29:27', 'a', 'a'),
(10, 'joo', '2022-11-20 06:46:07', '2022-11-20 06:46:07', 'joo', 'joo'),
(7, 'jujoo', '2023-03-12 16:43:47', '2023-03-12 16:43:47', 'jujoo', 'jujoo'),
(6, 'jujoo', '2023-06-23 21:19:43', '2023-06-23 21:19:43', 'jujoo', 'jujoo');

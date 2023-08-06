CREATE TABLE `user_account` (
	`user_id`	varchar(50)	NOT NULL,
	`email`	varchar(100)	NOT NULL,
	`user_password`	varchar(255)	NOT NULL,
	`nickname`	varchar(100)	NOT NULL,
	`created_at`	datetime	NOT NULL,
	`modified_at`	datetime	NULL,
	`created_by`	varchar(100)	NOT NULL,
	`modified_by`	varchar(100)	NULL,
	PRIMARY KEY (`user_id`),
    UNIQUE (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='유저';

CREATE TABLE `article` (
	`article_id`	bigint	NOT NULL,
	`user_id`	varchar(50)	NOT NULL,
	`title`	varchar(255)	NOT NULL,
	`content`	longtext	NOT NULL,
	`article_type`	varchar(50)	NOT NULL,
	`category`	varchar(50)	NULL,
	`created_at`	datetime	NOT NULL,
	`modified_at`	datetime	NULL,
	`created_by`	varchar(100)	NOT NULL,
	`modified_by`	varchar(100)	NULL,
    PRIMARY KEY (`article_id`),
    FOREIGN KEY (`user_id`) REFERENCES `user_account` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='게시글';

CREATE TABLE `article_comment` (
	`article_comment_id`	bigint	NOT NULL,
	`user_id`	varchar(50)	NOT NULL,
	`article_id`	bigint	NOT NULL,
	`content`	longtext	NOT NULL,
	`parent_comment_id`	bigint	NULL,
	`created_at`	datetime	NOT NULL,
	`modified_at`	datetime	NULL,
	`created_by`	varchar(100)	NOT NULL,
	`modified_by`	varchar(100)	NULL,
    PRIMARY KEY (`article_comment_id`),
    FOREIGN KEY (`user_id`) REFERENCES `user_account` (`user_id`),
    FOREIGN KEY (`article_id`) REFERENCES `article` (`article_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='댓글';

CREATE TABLE `like` (
	`like_id`	bigint	NOT NULL,
	`article_id`	bigint	NOT NULL,
	`user_id`	varchar(50)	NOT NULL,
	`created_at`	datetime	NOT NULL,
	`modified_at`	datetime	NULL,
	`created_by`	varchar(100)	NOT NULL,
	`modified_by`	varchar(100)	NULL,
	PRIMARY KEY (`like_id`),
    FOREIGN KEY (`user_id`) REFERENCES `user_account` (`user_id`),
    FOREIGN KEY (`article_id`) REFERENCES `article` (`article_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='좋아요';

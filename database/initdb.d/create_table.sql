CREATE TABLE `user_account` (
	`email`	varchar(100)	NOT NULL,
	`user_password`	varchar(255)	NOT NULL,
	`nickname`	varchar(100)	NOT NULL,
    `phone`	varchar(11) NULL,
    `detail` varchar(50) NULL,
    `street` varchar(100) NULL,
    `zipcode` varchar(10) NULL,
    `sign_up` boolean DEFAULT false NOT NULL,
	`user_role` varchar(10) NOT NULL,
	`company_name` varchar(100) NULL,
    `homepage` varchar(255) NULL,
	`created_at`	datetime	DEFAULT CURRENT_TIMESTAMP   NOT NULL,
	`modified_at`	datetime	NOT NULL,
	`created_by`	varchar(100)	NOT NULL,
	`modified_by`	varchar(100)	NOT NULL,
	PRIMARY KEY (`email`),
    UNIQUE (`nickname`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='유저';

CREATE TABLE `article_file` (
    `id`	bigint	AUTO_INCREMENT NOT NULL,
    `byte_size`	bigint	NOT NULL,
    `original_file_name`	varchar(100)	NOT NULL,
    `file_name`	varchar(100)	NOT NULL,
    `file_extension`	varchar(100)	NOT NULL,
    `created_at`	datetime	DEFAULT CURRENT_TIMESTAMP   NOT NULL,
    `modified_at`	datetime	NOT NULL,
    `created_by`	varchar(100)	NOT NULL,
    `modified_by`	varchar(100)	NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE (`file_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='파일';

CREATE TABLE `article` (
	`id`	bigint	AUTO_INCREMENT NOT NULL,
	`email`   varchar(50)	NOT NULL,
    `article_file_id`   bigint  NOT NULL,
	`title`	varchar(255)	NOT NULL,
	`content`	varchar(255)	NOT NULL,
	`article_type`	varchar(50)	NOT NULL,
	`article_category`	varchar(50)	NULL,
	`like_count` int(11) DEFAULT 0 NOT NULL,
	`created_at`	datetime	DEFAULT CURRENT_TIMESTAMP   NOT NULL,
	`modified_at`	datetime	NOT NULL,
	`created_by`	varchar(100)	NOT NULL,
	`modified_by`	varchar(100)	NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`email`) REFERENCES `user_account` (`email`),
    FOREIGN KEY (`article_file_id`) REFERENCES `article_file` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='게시글';

CREATE TABLE `article_comment` (
	`id`	bigint	AUTO_INCREMENT NOT NULL,
    `email`   varchar(50)	NOT NULL,
	`article_id`	bigint	NOT NULL,
	`content`	varchar(255)	NOT NULL,
	`parent_comment_id`	bigint	NULL,
	`created_at`	datetime	DEFAULT CURRENT_TIMESTAMP   NOT NULL,
	`modified_at`	datetime	NOT NULL,
	`created_by`	varchar(100)	NOT NULL,
	`modified_by`	varchar(100)	NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`email`) REFERENCES `user_account` (`email`),
    FOREIGN KEY (`article_id`) REFERENCES `article` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='댓글';

CREATE TABLE `article_like` (
	`id`	bigint	AUTO_INCREMENT NOT NULL,
	`article_id`	bigint	NOT NULL,
	`email`	varchar(50)	NOT NULL,
	`created_at`	datetime	DEFAULT CURRENT_TIMESTAMP   NOT NULL,
	`modified_at`	datetime	NOT NULL,
	`created_by`	varchar(100)	NOT NULL,
	`modified_by`	varchar(100)	NOT NULL,
	PRIMARY KEY (`id`),
    FOREIGN KEY (`email`) REFERENCES `user_account` (`email`),
    FOREIGN KEY (`article_id`) REFERENCES `article` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='좋아요';

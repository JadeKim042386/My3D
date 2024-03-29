﻿CREATE TABLE `company` (
    `id` bigint	AUTO_INCREMENT NOT NULL,
    `company_name` varchar(100) NULL,
    `homepage` varchar(255) NULL,
    PRIMARY KEY (`id`),
    INDEX company_name_idx (company_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='기업';

CREATE TABLE `user_refresh_token` (
    `id` bigint	AUTO_INCREMENT NOT NULL,
    `refresh_token`	varchar(255)	NOT NULL,
    `reissue_count` bigint default 0 NOT NULL,
    PRIMARY KEY (`id`),
    INDEX reissue_count_idx (reissue_count)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='RefreshToken';

CREATE TABLE `user_account` (
    `id` bigint	AUTO_INCREMENT NOT NULL,
    `company_id`	bigint,
    `user_refresh_token_id`	bigint NOT NULL,
    `email`	varchar(100)	NOT NULL,
    `user_password`	varchar(255)	NOT NULL,
    `nickname`	varchar(100)	NOT NULL,
    `phone`	varchar(11) NULL,
    `detail` varchar(50) NULL,
    `street` varchar(100) NULL,
    `zipcode` varchar(10) NULL,
    `user_role` varchar(10) NOT NULL,
    `created_at`	datetime	DEFAULT CURRENT_TIMESTAMP   NOT NULL,
    `modified_at`	datetime	NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX nickname_idx (`nickname`),
    UNIQUE INDEX email_idx (`email`),
    FOREIGN KEY (`company_id`) REFERENCES `company` (`id`),
    FOREIGN KEY (`user_refresh_token_id`) REFERENCES `user_refresh_token` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='유저';

CREATE TABLE `dimension_option` (
    `id`	bigint	AUTO_INCREMENT NOT NULL,
    `option_name`	varchar(50)	NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='치수 옵션';

CREATE TABLE `article_file` (
    `id`	bigint	AUTO_INCREMENT NOT NULL,
    `dimension_option_id` bigint NOT NULL,
    `byte_size`	bigint	NOT NULL,
    `original_file_name`	varchar(100)	NOT NULL,
    `file_name`	varchar(100)    UNIQUE  NOT NULL,
    `file_extension`	varchar(100)	NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`dimension_option_id`) REFERENCES `dimension_option` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='파일';

CREATE TABLE `article` (
    `id`	bigint	AUTO_INCREMENT NOT NULL,
    `article_file_id`   bigint	NOT NULL,
    `user_account_id`   bigint	NOT NULL,
    `title`	varchar(255)	NOT NULL,
    `content`	varchar(255)	NOT NULL,
    `article_type`	varchar(50)	NOT NULL,
    `article_category`	varchar(50)	NULL,
    `like_count` int DEFAULT 0 NOT NULL,
    `created_at`	datetime	DEFAULT CURRENT_TIMESTAMP   NOT NULL,
    `modified_at`	datetime	NOT NULL,
    PRIMARY KEY (`id`),
    INDEX likeCount_idx (like_count),
    INDEX title_idx (title),
    INDEX articleCategory_idx (article_category),
    INDEX article_id_and_user_id_idx (id, user_account_id),
    FOREIGN KEY (`article_file_id`) REFERENCES `article_file` (`id`),
    FOREIGN KEY (`user_account_id`) REFERENCES `user_account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='게시글';

CREATE TABLE `dimension` (
     `id`	bigint	AUTO_INCREMENT NOT NULL,
     `dimension_option_id`	bigint	NOT NULL,
     `dim_name`	varchar(50)	NOT NULL,
     `dim_value`	float	default 0 NOT NULL,
     `dim_unit`	varchar(10)	NOT NULL,
     PRIMARY KEY (`id`),
     FOREIGN KEY (`dimension_option_id`) REFERENCES `dimension_option` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='치수';

CREATE TABLE `article_comment` (
	`id`	bigint	AUTO_INCREMENT NOT NULL,
    `user_account_id`   bigint	NOT NULL,
	`article_id`	bigint	NOT NULL,
	`content`	varchar(255)	NOT NULL,
	`parent_comment_id`	bigint	NULL,
	`created_at`	datetime	DEFAULT CURRENT_TIMESTAMP   NOT NULL,
	`modified_at`	datetime	NOT NULL,
    PRIMARY KEY (`id`),
    INDEX comment_id_and_user_account_id_idx (id, user_account_id),
    FOREIGN KEY (`user_account_id`) REFERENCES `user_account` (`id`),
    FOREIGN KEY (`article_id`) REFERENCES `article` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='댓글';

CREATE TABLE `article_like` (
	`id`	bigint	AUTO_INCREMENT NOT NULL,
	`article_id`	bigint	NOT NULL,
	`user_account_id`	bigint	NOT NULL,
	`created_at`	datetime	DEFAULT CURRENT_TIMESTAMP   NOT NULL,
	`modified_at`	datetime	NOT NULL,
	PRIMARY KEY (`id`),
    INDEX user_account_id_and_article_id_idx (user_account_id, article_id),
    INDEX article_id_idx (article_id),
    FOREIGN KEY (`user_account_id`) REFERENCES `user_account` (`id`),
    FOREIGN KEY (`article_id`) REFERENCES `article` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='좋아요';

CREATE TABLE `alarm` (
    `id`	bigint	AUTO_INCREMENT NOT NULL,
    `alarm_type` varchar(50) NOT NULL,
    `target_id` bigint NOT NULL,
    `is_checked` boolean DEFAULT false NOT NULL,
    `article_id` bigint NOT NULL,
    `sender_id` bigint NOT NULL,
    `receiver_id` bigint NOT NULL,
    `created_at`	datetime	DEFAULT CURRENT_TIMESTAMP   NOT NULL,
    `modified_at`	datetime	NOT NULL,
    PRIMARY KEY (`id`),
    INDEX receiver_idx (receiver_id),
    FOREIGN KEY (`article_id`) REFERENCES `article` (`id`),
    FOREIGN KEY (`sender_id`) REFERENCES `user_account` (`id`),
    FOREIGN KEY (`receiver_id`) REFERENCES `user_account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='알람';

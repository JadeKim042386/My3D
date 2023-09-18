CREATE TABLE `company` (
    `id` bigint	AUTO_INCREMENT NOT NULL,
    `company_name` varchar(100) NOT NULL,
    `homepage` varchar(255) NULL,
    `created_at`	datetime	DEFAULT CURRENT_TIMESTAMP   NOT NULL,
    `modified_at`	datetime	NOT NULL,
    `created_by`	varchar(100)	NOT NULL,
    `modified_by`	varchar(100)	NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='기업';

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
	`company_id` bigint  NULL,
	`created_at`	datetime	DEFAULT CURRENT_TIMESTAMP   NOT NULL,
	`modified_at`	datetime	NOT NULL,
	`created_by`	varchar(100)	NOT NULL,
	`modified_by`	varchar(100)	NOT NULL,
	PRIMARY KEY (`email`),
    UNIQUE (`nickname`),
    FOREIGN KEY (`company_id`) REFERENCES `company` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='유저';

Create TABLE `price` (
    `id`	bigint	AUTO_INCREMENT NOT NULL,
    `price_value` int(11) Default 0 NOT NULL ,
    `delivery_price` int(11) Default 0 NOT NULL ,
    `created_at`	datetime	DEFAULT CURRENT_TIMESTAMP   NOT NULL,
    `modified_at`	datetime	NOT NULL,
    `created_by`	varchar(100)	NOT NULL,
    `modified_by`	varchar(100)	NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='가격';

CREATE TABLE `article` (
	`id`	bigint	AUTO_INCREMENT NOT NULL,
	`email`   varchar(50)	NOT NULL,
    `price_id`   bigint  NOT NULL,
	`title`	varchar(255)	NOT NULL,
    `summary`	varchar(255)	NOT NULL,
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
    FOREIGN KEY (`price_id`) REFERENCES `price` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='게시글';

CREATE TABLE `article_file` (
    `id`	bigint	AUTO_INCREMENT NOT NULL,
    `article_id`	bigint	NOT NULL,
    `byte_size`	bigint	NOT NULL,
    `original_file_name`	varchar(100)	NOT NULL,
    `file_name`	varchar(100)	NOT NULL,
    `file_extension`	varchar(100)	NOT NULL,
    `created_at`	datetime	DEFAULT CURRENT_TIMESTAMP   NOT NULL,
    `modified_at`	datetime	NOT NULL,
    `created_by`	varchar(100)	NOT NULL,
    `modified_by`	varchar(100)	NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE (`file_name`),
    FOREIGN KEY (`article_id`) REFERENCES `article` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='파일';

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

CREATE TABLE `good_option` (
    `id`	bigint	AUTO_INCREMENT NOT NULL,
    `article_id`	bigint	NOT NULL,
    `option_name`	varchar(50)	NOT NULL,
    `add_price`	int(50)	default 0 NOT NULL,
    `printing_tech`	varchar(50)	NOT NULL,
    `material`	varchar(50)	NOT NULL,
    `created_at`	datetime	DEFAULT CURRENT_TIMESTAMP   NOT NULL,
    `modified_at`	datetime	NOT NULL,
    `created_by`	varchar(100)	NOT NULL,
    `modified_by`	varchar(100)	NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`article_id`) REFERENCES `article` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='상품옵션';

CREATE TABLE `dimension` (
   `id`	bigint	AUTO_INCREMENT NOT NULL,
   `good_option_id`	bigint	NOT NULL,
   `dim_name`	varchar(50)	NOT NULL,
   `dim_value`	float	default 0 NOT NULL,
   `dim_unit`	varchar(10)	NOT NULL,
   `created_at`	datetime	DEFAULT CURRENT_TIMESTAMP   NOT NULL,
   `modified_at`	datetime	NOT NULL,
   `created_by`	varchar(100)	NOT NULL,
   `modified_by`	varchar(100)	NOT NULL,
   PRIMARY KEY (`id`),
   FOREIGN KEY (`good_option_id`) REFERENCES `good_option` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='치수';

CREATE TABLE `orders` (
    `id`	bigint	AUTO_INCREMENT NOT NULL,
    `email`	varchar(50)	NOT NULL,
    `company_id` bigint	NOT NULL,
    `status` varchar(10) NOT NULL,
    `product_name` varchar(255) NOT NULL,
    `detail` varchar(50) NOT NULL,
    `street` varchar(100) NOT NULL,
    `zipcode` varchar(10) NOT NULL,
    `created_at`	datetime	DEFAULT CURRENT_TIMESTAMP   NOT NULL,
    `modified_at`	datetime	NOT NULL,
    `created_by`	varchar(100)	NOT NULL,
    `modified_by`	varchar(100)	NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`email`) REFERENCES `user_account` (`email`),
    FOREIGN KEY (`company_id`) REFERENCES `company` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='주문';

CREATE TABLE `items` (
  `id` int(11) NOT NULL,
  `name` varchar(45) DEFAULT NULL,
  `fromid` int(11) DEFAULT NULL,
  `toid` int(11) DEFAULT NULL,
  `article` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `item_attributes` (
  `item_id` int(11) NOT NULL,
  `key` varchar(45) NOT NULL,
  `value` varchar(250) NOT NULL,
  `chance` int(11) DEFAULT NULL,
  `random_min` int(11) DEFAULT NULL,
  `random_max` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

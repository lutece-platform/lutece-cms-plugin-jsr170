--
-- Structure for table jsr170_portlet
--
DROP TABLE IF EXISTS jsr170_portlet;
CREATE TABLE jsr170_portlet (
  id_portlet INT DEFAULT '0' NOT NULL,
  id_view INT DEFAULT NULL,
  PRIMARY KEY (id_portlet)
);
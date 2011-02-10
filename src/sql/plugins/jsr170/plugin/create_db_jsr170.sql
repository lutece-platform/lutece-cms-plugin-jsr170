--
-- Structure for table jsr170_view
--
DROP TABLE IF EXISTS jsr170_view;
CREATE TABLE jsr170_view (
  id_view INT DEFAULT '0' NOT NULL,
  id_workspace INT DEFAULT '0' NOT NULL,
  workgroup VARCHAR(45) DEFAULT '' NOT NULL,
  view_name VARCHAR(45) DEFAULT '' NOT NULL,
  path VARCHAR(128) DEFAULT NULL,
  PRIMARY KEY (id_view)
);

--
-- Structure for table jsr170_workspace
--

DROP TABLE IF EXISTS jsr170_workspace;
CREATE TABLE jsr170_workspace (
  id_workspace INT DEFAULT '0' NOT NULL,
  workspace_name VARCHAR(45) DEFAULT '' NOT NULL,
  workgroup VARCHAR(45) DEFAULT '' NOT NULL,
  jcr_type VARCHAR(45) DEFAULT '' NOT NULL,
  workspace_label VARCHAR(45) DEFAULT '' NOT NULL,
  user VARCHAR(45) DEFAULT '' NOT NULL,
  password VARCHAR(45) DEFAULT '' NOT NULL,
  PRIMARY KEY (id_workspace)
);

--
-- Structure for table jsr170_view_role
--

DROP TABLE IF EXISTS jsr170_view_role;
CREATE TABLE jsr170_view_role (
  id_view INT DEFAULT '0' NOT NULL,
  access_right VARCHAR(45) DEFAULT '' NOT NULL,
  role VARCHAR(45) DEFAULT '' NOT NULL,
  PRIMARY KEY (id_view,access_right,role)
);

--
-- Structure for table jsr170_lock
--

DROP TABLE IF EXISTS jsr170_lock;
CREATE TABLE jsr170_lock (
  id_user VARCHAR(100) NOT NULL,
  id_lock VARCHAR(100) NOT NULL,
  id_workspace INT NOT NULL,
  creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  id_file VARCHAR(100) NOT NULL,
  PRIMARY KEY (id_lock,id_workspace)
);

SET FOREIGN_KEY_CHECKS=0;

DROP TABLE IF EXISTS quartz_job;
CREATE TABLE quartz_job (
  id bigint unsigned NOT NULL COMMENT '自增主键',
  job_name varchar(50) NOT NULL COMMENT '任务名称',
  job_group varchar(50) NOT NULL COMMENT '任务分组',
  job_class_name varchar(100) NOT NULL COMMENT '执行类',
  cron_expression varchar(100) NOT NULL COMMENT 'cron表达式',
  trigger_state varchar(15) NOT NULL  COMMENT '任务状态',
  old_job_name varchar(50) NOT NULL DEFAULT '' COMMENT '修改之前的任务名称',
  old_job_group varchar(50) NOT NULL DEFAULT '' COMMENT '修改之前的任务分组',
  description varchar(100) NOT NULL COMMENT '描述',
  create_time timestamp NOT NULL COMMENT '创建时间',
  update_time timestamp NOT NULL COMMENT '修改时间',
  PRIMARY KEY (id),
  UNIQUE KEY un_group_name (job_group,job_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='定时任务';

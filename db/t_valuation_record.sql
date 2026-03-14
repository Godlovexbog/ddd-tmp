-- 估值记录表
CREATE TABLE t_valuation_record (
    id              BIGINT          NOT NULL    AUTO_INCREMENT  COMMENT '主键ID',
    project_id      BIGINT          NOT NULL                    COMMENT '项目ID',
    project_code    VARCHAR(50)     NOT NULL                    COMMENT '项目代码',
    target_source   VARCHAR(50)     NULL                        COMMENT '标的来源',
    target_name     VARCHAR(100)    NULL                        COMMENT '标的名称',
    target_amount   DECIMAL(18,2)   NOT NULL    DEFAULT 0       COMMENT '标的金额',
    valuation_date  DATE            NOT NULL                    COMMENT '估值日期',
    target_share    DECIMAL(18,6)   NULL                        COMMENT '标的份额',
    account_code    VARCHAR(50)     NULL                        COMMENT '账户代码',
    gmt_create      DATETIME        NOT NULL    DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
    gmt_modified    DATETIME        NOT NULL    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    is_deleted      TINYINT         NOT NULL    DEFAULT 0       COMMENT '是否删除: 0-否, 1-是',
    
    PRIMARY KEY (id),
    INDEX idx_valuation_project (project_id),
    INDEX idx_valuation_date (valuation_date),
    INDEX idx_valuation_project_code (project_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='估值记录表';

-- 现金交付记录表
CREATE TABLE t_cash_delivery_record (
    id                  BIGINT          NOT NULL    AUTO_INCREMENT  COMMENT '主键ID',
    project_id          BIGINT          NOT NULL                    COMMENT '项目ID',
    cash_amount         DECIMAL(18,2)   NOT NULL    DEFAULT 0       COMMENT '现金金额',
    delivery_status     CHAR(1)         NOT NULL    DEFAULT '0'     COMMENT '交付状态: 0-PENDING, 1-SUCCESS, 2-FAILED',
    delivery_date       DATETIME        NOT NULL                    COMMENT '交付时间',
    gmt_create          DATETIME        NOT NULL    DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
    gmt_modified        DATETIME        NOT NULL    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    is_deleted          TINYINT         NOT NULL    DEFAULT 0       COMMENT '是否删除: 0-否, 1-是',
    
    PRIMARY KEY (id),
    INDEX idx_cash_delivery_project (project_id),
    INDEX idx_cash_delivery_date (delivery_date),
    INDEX idx_cash_delivery_status (delivery_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='现金交付记录表';

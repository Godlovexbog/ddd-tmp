-- 金融产品交付记录表
CREATE TABLE t_financial_product_delivery_record (
    id                  BIGINT          NOT NULL    AUTO_INCREMENT  COMMENT '主键ID',
    project_id          BIGINT          NOT NULL                    COMMENT '项目ID',
    product_code        VARCHAR(50)     NOT NULL                    COMMENT '产品代码',
    product_name        VARCHAR(100)    NULL                        COMMENT '产品名称',
    delivery_amount     DECIMAL(18,2)   NOT NULL    DEFAULT 0       COMMENT '交付金额',
    delivery_status     CHAR(1)         NOT NULL    DEFAULT '0'     COMMENT '交付状态: 0-PENDING, 1-SUCCESS, 2-FAILED',
    delivery_date       DATETIME        NOT NULL                    COMMENT '交付时间',
    gmt_create          DATETIME        NOT NULL    DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
    gmt_modified        DATETIME        NOT NULL    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    is_deleted          TINYINT         NOT NULL    DEFAULT 0       COMMENT '是否删除: 0-否, 1-是',
    
    PRIMARY KEY (id),
    INDEX idx_financial_delivery_project (project_id),
    INDEX idx_financial_delivery_date (delivery_date),
    INDEX idx_financial_delivery_status (delivery_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='金融产品交付记录表';

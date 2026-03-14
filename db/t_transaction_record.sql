-- 交易记录表
CREATE TABLE t_transaction_record (
    id                  BIGINT          NOT NULL    AUTO_INCREMENT  COMMENT '主键ID',
    target_id           BIGINT          NOT NULL                    COMMENT '标的ID',
    target_code         VARCHAR(50)     NOT NULL                    COMMENT '标的代码',
    target_source       VARCHAR(50)     NULL                        COMMENT '标的来源',
    transaction_share   DECIMAL(18,6)   NULL                        COMMENT '交易份额',
    transaction_amount  DECIMAL(18,2)   NOT NULL    DEFAULT 0       COMMENT '交易金额',
    transaction_status  CHAR(1)         NOT NULL    DEFAULT '0'     COMMENT '交易状态: 0-PENDING, 1-SUCCESS, 2-FAILED',
    transaction_date    DATETIME        NOT NULL                    COMMENT '交易时间',
    gmt_create          DATETIME        NOT NULL    DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
    gmt_modified        DATETIME        NOT NULL    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    is_deleted          TINYINT         NOT NULL    DEFAULT 0       COMMENT '是否删除: 0-否, 1-是',
    
    PRIMARY KEY (id),
    INDEX idx_transaction_target (target_id),
    INDEX idx_transaction_date (transaction_date),
    INDEX idx_transaction_status (transaction_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交易记录表';

-- =====================================================
-- 估值计算模块测试数据
-- 场景说明：
-- 1. 估值记录金额 = 10000，估值日期 = 2024-01-01
-- 2. 成功交易：2000（2024-01-15）、5000（2024-01-20）
-- 3. 失败交易：5000（2024-01-10）- 应被忽略
-- 4. 成功现金交付：3000（2024-02-01）
-- 5. 成功金融产品交付：4000（2024-03-01）
-- 6. 估值日期之前的交易：2000（2023-12-15）- 应被忽略
-- 
-- 预期计算结果：10000 + 2000 + 5000 + 3000 + 4000 = 24000
-- =====================================================

-- 1. 估值记录（基础金额 10000，估值日期 2024-01-01）
INSERT INTO t_valuation_record (project_id, project_code, target_source, target_name, target_amount, valuation_date, target_share, account_code, gmt_create, gmt_modified, is_deleted) VALUES
(1, 'P001', '银行理财', '某银行理财产品', 10000.00, '2024-01-01', 10000.000000, 'ACC001', NOW(), NOW(), 0);

-- 2. 交易记录
-- 2.1 成功交易（估值日期之后）- 2024-01-15
INSERT INTO t_transaction_record (target_id, target_code, target_source, transaction_share, transaction_amount, transaction_status, transaction_date, gmt_create, gmt_modified, is_deleted) VALUES
(1, 'T001', '银行理财', 2000.000000, 2000.00, '1', '2024-01-15 10:00:00', NOW(), NOW(), 0);

-- 2.2 成功交易（估值日期之后）- 2024-01-20
INSERT INTO t_transaction_record (target_id, target_code, target_source, transaction_share, transaction_amount, transaction_status, transaction_date, gmt_create, gmt_modified, is_deleted) VALUES
(1, 'T001', '银行理财', 5000.000000, 5000.00, '1', '2024-01-20 10:00:00', NOW(), NOW(), 0);

-- 2.3 失败交易（应被忽略）- 2024-01-10
INSERT INTO t_transaction_record (target_id, target_code, target_source, transaction_share, transaction_amount, transaction_status, transaction_date, gmt_create, gmt_modified, is_deleted) VALUES
(1, 'T001', '银行理财', 5000.000000, 5000.00, '2', '2024-01-10 10:00:00', NOW(), NOW(), 0);

-- 2.4 成功交易（估值日期之前 - 应被忽略）- 2023-12-15
INSERT INTO t_transaction_record (target_id, target_code, target_source, transaction_share, transaction_amount, transaction_status, transaction_date, gmt_create, gmt_modified, is_deleted) VALUES
(1, 'T001', '银行理财', 2000.000000, 2000.00, '1', '2023-12-15 10:00:00', NOW(), NOW(), 0);

-- 2.5 处理中交易（应被忽略）
INSERT INTO t_transaction_record (target_id, target_code, target_source, transaction_share, transaction_amount, transaction_status, transaction_date, gmt_create, gmt_modified, is_deleted) VALUES
(1, 'T001', '银行理财', 1000.000000, 1000.00, '0', '2024-02-10 10:00:00', NOW(), NOW(), 0);

-- 3. 现金交付记录
-- 3.1 成功现金交付 - 2024-02-01
INSERT INTO t_cash_delivery_record (project_id, cash_amount, delivery_status, delivery_date, gmt_create, gmt_modified, is_deleted) VALUES
(1, 3000.00, '1', '2024-02-01 10:00:00', NOW(), NOW(), 0);

-- 3.2 失败现金交付（应被忽略）
INSERT INTO t_cash_delivery_record (project_id, cash_amount, delivery_status, delivery_date, gmt_create, gmt_modified, is_deleted) VALUES
(1, 1500.00, '2', '2024-02-15 10:00:00', NOW(), NOW(), 0);

-- 4. 金融产品交付记录
-- 4.1 成功金融产品交付 - 2024-03-01
INSERT INTO t_financial_product_delivery_record (project_id, product_code, product_name, delivery_amount, delivery_status, delivery_date, gmt_create, gmt_modified, is_deleted) VALUES
(1, 'FP001', '某基金产品', 4000.00, '1', '2024-03-01 10:00:00', NOW(), NOW(), 0);

-- 4.2 成功金融产品交付（估值日期之前 - 应被忽略）
INSERT INTO t_financial_product_delivery_record (project_id, product_code, product_name, delivery_amount, delivery_status, delivery_date, gmt_create, gmt_modified, is_deleted) VALUES
(1, 'FP002', '历史基金产品', 2500.00, '1', '2023-12-20 10:00:00', NOW(), NOW(), 0);

-- =====================================================
-- 验证查询
-- =====================================================

-- 查看估值记录
SELECT * FROM t_valuation_record WHERE is_deleted = 0;

-- 查看成功交易（估值日期之后）
SELECT * FROM t_transaction_record 
WHERE transaction_date >= '2024-01-01' AND transaction_status = '1' AND is_deleted = 0;

-- 查看成功交付（估值日期之后）
SELECT * FROM t_cash_delivery_record 
WHERE delivery_date >= '2024-01-01' AND delivery_status = '1' AND is_deleted = 0;

SELECT * FROM t_financial_product_delivery_record 
WHERE delivery_date >= '2024-01-01' AND delivery_status = '1' AND is_deleted = 0;

-- 预期结果验证
SELECT 
    (SELECT target_amount FROM t_valuation_record WHERE project_id = 1 AND is_deleted = 0 LIMIT 1) as 估值金额
    +
    (SELECT COALESCE(SUM(transaction_amount), 0) FROM t_transaction_record 
     WHERE transaction_date >= '2024-01-01' AND transaction_status = '1' AND is_deleted = 0) as 交易金额
    +
    (SELECT COALESCE(SUM(cash_amount), 0) FROM t_cash_delivery_record 
     WHERE delivery_date >= '2024-01-01' AND delivery_status = '1' AND is_deleted = 0) as 现金交付金额
    +
    (SELECT COALESCE(SUM(delivery_amount), 0) FROM t_financial_product_delivery_record 
     WHERE delivery_date >= '2024-01-01' AND delivery_status = '1' AND is_deleted = 0) as 金融产品交付金额
as 实时估值;

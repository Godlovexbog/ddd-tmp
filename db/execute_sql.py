#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Valuation Module SQL Executor
执行估值模块测试数据SQL脚本
"""

import os
import sys

try:
    import mysql.connector
except ImportError:
    print("正在安装 mysql-connector-python...")
    os.system(f"{sys.executable} -m pip install mysql-connector-python -q")
    import mysql.connector

DB_CONFIG = {
    'host': '127.0.0.1',
    'port': 3306,
    'user': 'root',
    'password': 'laowei',
    'database': 'blog-user'
}

SQL_FILES = [
    'db/t_valuation_record.sql',
    'db/t_transaction_record.sql',
    'db/t_cash_delivery_record.sql',
    'db/t_financial_product_delivery_record.sql',
    'db/test_data.sql'
]

def read_sql_file(filepath):
    """读取SQL文件内容"""
    with open(filepath, 'r', encoding='utf-8') as f:
        return f.read()

def split_sql_statements(sql_content):
    """分割SQL语句"""
    statements = []
    current_stmt = []
    
    for line in sql_content.split('\n'):
        line = line.strip()
        if not line or line.startswith('--'):
            continue
        current_stmt.append(line)
        if line.endswith(';'):
            statements.append(' '.join(current_stmt))
            current_stmt = []
    
    if current_stmt:
        statements.append(' '.join(current_stmt))
    
    return statements

def execute_sql_file(conn, filepath):
    """执行SQL文件"""
    print(f"  执行: {filepath}")
    sql_content = read_sql_file(filepath)
    statements = split_sql_statements(sql_content)
    
    cursor = conn.cursor()
    executed = 0
    
    for stmt in statements:
        if not stmt.strip() or stmt.strip() == ';':
            continue
        
        try:
            # 对于非查询语句，使用 multi=True 来处理多个结果
            cursor.execute(stmt)
            try:
                cursor.fetchall()  # 消耗所有结果
            except:
                pass  # 没有结果集
            
            if not stmt.strip().upper().startswith('SELECT'):
                executed += 1
        except mysql.connector.Error as e:
            # 忽略已存在的表等错误
            if 'already exists' not in str(e).lower() and 'duplicate' not in str(e).lower():
                print(f"    警告: {e}")
    
    cursor.close()
    return executed

def main():
    print("=" * 60)
    print("估值模块 - SQL执行器")
    print("=" * 60)
    print()
    
    # 连接数据库
    print("[1/4] 连接数据库...")
    try:
        conn = mysql.connector.connect(**DB_CONFIG)
        print("  数据库连接成功!")
    except mysql.connector.Error as e:
        print(f"  数据库连接失败: {e}")
        return
    
    try:
        # 执行SQL文件
        print("[2/4] 执行SQL文件...")
        total_executed = 0
        
        for sql_file in SQL_FILES:
            if os.path.exists(sql_file):
                count = execute_sql_file(conn, sql_file)
                total_executed += count
            else:
                print(f"  文件不存在: {sql_file}")
        
        conn.commit()
        print(f"  共执行 {total_executed} 条SQL语句")
        
        # 验证查询
        print()
        print("[3/4] 验证结果:")
        print("-" * 60)
        
        cursor = conn.cursor()
        
        # 记录数统计
        tables = [
            ('t_valuation_record', '估值记录'),
            ('t_transaction_record', '交易记录'),
            ('t_cash_delivery_record', '现金交付记录'),
            ('t_financial_product_delivery_record', '金融产品交付记录')
        ]
        
        for table, name in tables:
            cursor.execute(f"SELECT COUNT(*) FROM {table} WHERE is_deleted = 0")
            count = cursor.fetchone()[0]
            print(f"  {name}: {count} 条")
        
        # 计算实时估值
        print()
        print("[4/4] 实时估值计算结果:")
        print("-" * 60)
        
        calc_sql = """
        SELECT 
            (SELECT COALESCE(target_amount, 0) FROM t_valuation_record WHERE project_id = 1 AND is_deleted = 0 LIMIT 1) as 估值金额,
            (SELECT COALESCE(SUM(transaction_amount), 0) FROM t_transaction_record WHERE target_id = 1 AND transaction_status = '1' AND is_deleted = 0) as 交易金额,
            (SELECT COALESCE(SUM(cash_amount), 0) FROM t_cash_delivery_record WHERE project_id = 1 AND delivery_status = '1' AND is_deleted = 0) as 现金交付金额,
            (SELECT COALESCE(SUM(delivery_amount), 0) FROM t_financial_product_delivery_record WHERE project_id = 1 AND delivery_status = '1' AND is_deleted = 0) as 金融产品交付金额,
            (SELECT COALESCE(target_amount, 0) FROM t_valuation_record WHERE project_id = 1 AND is_deleted = 0 LIMIT 1) +
            (SELECT COALESCE(SUM(transaction_amount), 0) FROM t_transaction_record WHERE target_id = 1 AND transaction_status = '1' AND is_deleted = 0) +
            (SELECT COALESCE(SUM(cash_amount), 0) FROM t_cash_delivery_record WHERE project_id = 1 AND delivery_status = '1' AND is_deleted = 0) +
            (SELECT COALESCE(SUM(delivery_amount), 0) FROM t_financial_product_delivery_record WHERE project_id = 1 AND delivery_status = '1' AND is_deleted = 0) as 实时估值
        """
        
        cursor.execute(calc_sql)
        result = cursor.fetchone()
        
        print(f"  估值金额: {result[0]}")
        print(f"  + 交易金额: {result[1]}")
        print(f"  + 现金交付金额: {result[2]}")
        print(f"  + 金融产品交付金额: {result[3]}")
        print(f"  = 实时估值: {result[4]}")
        
        cursor.close()
        
        print()
        print("=" * 60)
        print("成功! 测试数据已插入到数据库中。")
        print("=" * 60)
        
    except Exception as e:
        print(f"错误: {e}")
        conn.rollback()
    
    finally:
        conn.close()

if __name__ == '__main__':
    main()

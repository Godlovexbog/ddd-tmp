#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
生成功能点计算Excel表格
"""

import openpyxl
from openpyxl.styles import Font, Alignment, PatternFill, Border, Side
from openpyxl.utils import get_column_letter

def create_function_point_excel():
    wb = openpyxl.Workbook()
    
    # Sheet 1: 功能点明细表
    ws1 = wb.active
    ws1.title = "功能点明细表"
    
    # Headers
    headers = ["功能类型", "序号", "功能名称", "数据表", "DET数量", "FTR数量", "复杂度", "权重", "功能点数", "备注"]
    header_fill = PatternFill(start_color="4472C4", end_color="4472C4", fill_type="solid")
    header_font = Font(bold=True, color="FFFFFF")
    
    for col, header in enumerate(headers, 1):
        cell = ws1.cell(row=1, column=col, value=header)
        cell.fill = header_fill
        cell.font = header_font
        cell.alignment = Alignment(horizontal='center', vertical='center')
    
    # Data
    data = [
        ["ILF", 1, "t_valuation_record", "t_valuation_record", 7, 1, "低", 6, 6, "估值记录表"],
        ["ILF", 2, "t_transaction_record", "t_transaction_record", 7, 1, "低", 6, 6, "交易记录表"],
        ["ILF", 3, "t_cash_delivery_record", "t_cash_delivery_record", 4, 1, "低", 6, 6, "现金交付记录表"],
        ["ILF", 4, "t_financial_product_delivery_record", "t_financial_product_delivery_record", 5, 1, "低", 6, 6, "金融产品交付记录表"],
        ["EI", 5, "创建估值记录", "t_valuation_record", 8, 1, "低", 3, 3, "POST /api/valuation/record"],
        ["EQ", 6, "查询估值记录（ID）", "t_valuation_record", 7, 1, "低", 3, 3, "GET /api/valuation/record/{id}"],
        ["EQ", 7, "按项目ID查询", "t_valuation_record", 7, 1, "低", 3, 3, "GET /api/valuation/record/project/{projectId}"],
        ["EQ", 8, "按项目代码查询", "t_valuation_record", 7, 1, "低", 3, 3, "GET /api/valuation/record/code/{projectCode}"],
        ["EI", 9, "删除估值记录", "t_valuation_record", 1, 1, "低", 3, 3, "DELETE /api/valuation/record/{id}"],
        ["EO", 10, "按项目ID计算估值", "4表关联", 1, 4, "中", 4, 4, "GET /api/valuation/calculate/project/{projectId}"],
        ["EO", 11, "按项目代码计算估值", "4表关联", 1, 4, "中", 4, 4, "GET /api/valuation/calculate/code/{projectCode}"],
        ["EI", 12, "创建交易记录", "t_transaction_record", 7, 1, "低", 3, 3, "POST /api/valuation/transaction"],
        ["EI", 13, "删除交易记录", "t_transaction_record", 1, 1, "低", 3, 3, "DELETE /api/valuation/transaction/{id}"],
        ["EI", 14, "创建交付记录", "交付记录表", 7, 1, "低", 3, 3, "POST /api/valuation/delivery"],
        ["EI", 15, "删除交付记录", "交付记录表", 1, 1, "低", 3, 3, "DELETE /api/valuation/delivery/{id}"],
    ]
    
    for row_idx, row_data in enumerate(data, 2):
        for col_idx, value in enumerate(row_data, 1):
            cell = ws1.cell(row=row_idx, column=col_idx, value=value)
            cell.alignment = Alignment(horizontal='center', vertical='center')
            # ILF行高亮
            if value == "ILF":
                cell.fill = PatternFill(start_color="E2EFDA", end_color="E2EFDA", fill_type="solid")
    
    # 汇总行
    summary_row = 17
    ws1.cell(row=summary_row, column=1, value="合计").font = Font(bold=True)
    ws1.cell(row=summary_row, column=8, value="=SUM(I2:I16)").font = Font(bold=True)
    ws1.cell(row=summary_row, column=9, value="=SUM(I2:I16)").font = Font(bold=True)
    
    # 设置列宽
    col_widths = [10, 6, 20, 35, 10, 10, 8, 8, 12, 40]
    for i, width in enumerate(col_widths, 1):
        ws1.column_dimensions[get_column_letter(i)].width = width
    
    # Sheet 2: 功能点汇总
    ws2 = wb.create_sheet("功能点汇总")
    
    headers2 = ["分类", "项目", "数值"]
    for col, header in enumerate(headers2, 1):
        cell = ws2.cell(row=1, column=col, value=header)
        cell.fill = header_fill
        cell.font = header_font
        cell.alignment = Alignment(horizontal='center', vertical='center')
    
    summary_data = [
        ["功能点统计", "数据功能点(ILF)", 24],
        ["功能点统计", "事务功能点(EI)", 12],
        ["功能点统计", "事务功能点(EO)", 8],
        ["功能点统计", "事务功能点(EQ)", 9],
        ["功能点统计", "事务功能点合计", 29],
        ["功能点统计", "未调整功能点(UFP)", "=B5+B8"],
        ["", "", ""],
        ["系统特征", "数据通信", 2],
        ["系统特征", "分布式处理", 1],
        ["系统特征", "性能", 2],
        ["系统特征", "硬件利用率", 1],
        ["系统特征", "事务率", 3],
        ["系统特征", "在线数据录入", 3],
        ["系统特征", "终端用户效率", 2],
        ["系统特征", "在线更新", 2],
        ["系统特征", "复杂处理", 1],
        ["系统特征", "可复用性", 1],
        ["系统特征", "易于安装", 2],
        ["系统特征", "易于操作", 2],
        ["系统特征", "多场地", 1],
        ["系统特征", "支持变更", 2],
        ["系统特征", "特征评分合计", "=SUM(B10:B21)"],
        ["", "", ""],
        ["计算结果", "TCF调整因子", "=14*(0.65+B22*0.01)"],
        ["计算结果", "调整后功能点(AFP)", "=B6*(B24/14)"],
        ["计算结果", "四舍五入", "=ROUND(B25,0)"],
        ["", "", ""],
        ["工作量估算", "基准(小时/FP)", 8],
        ["工作量估算", "总工时(小时)", "=B28*B27"],
        ["工作量估算", "总工时(人天)", "=B29/8"],
        ["工作量估算", "含缓冲(1.2倍)", "=B30*1.2"],
        ["工作量估算", "3人团队(天)", "=B31/3"],
        ["工作量估算", "5人团队(天)", "=B31/5"],
    ]
    
    for row_idx, row_data in enumerate(summary_data, 2):
        for col_idx, value in enumerate(row_data, 1):
            cell = ws2.cell(row=row_idx, column=col_idx, value=value)
            if isinstance(value, str) and value.startswith("="):
                cell.number_format = '0.00'
            if col_idx == 3 and isinstance(value, (int, float)):
                cell.alignment = Alignment(horizontal='right')
    
    ws2.column_dimensions['A'].width = 15
    ws2.column_dimensions['B'].width = 20
    ws2.column_dimensions['C'].width = 15
    
    # Sheet 3: 复杂度判定标准
    ws3 = wb.create_sheet("复杂度判定标准")
    
    headers3 = ["类型", "复杂度", "判定条件"]
    for col, header in enumerate(headers3, 1):
        cell = ws3.cell(row=1, column=col, value=header)
        cell.fill = header_fill
        cell.font = header_font
    
    complexity_data = [
        ["数据功能", "低", "字段 ≤19 且 记录类型 ≤1 且 引用文件 ≤1"],
        ["数据功能", "中", "字段 20-50 或 记录类型 2-3 或 引用文件 2-3"],
        ["数据功能", "高", "字段 >50 或 记录类型 >3 或 引用文件 >3"],
        ["", "", ""],
        ["事务功能", "低", "数据元素 ≤5 且 引用文件 ≤1"],
        ["事务功能", "中", "数据元素 6-19 或 引用文件 2-3"],
        ["事务功能", "高", "数据元素 >19 或 引用文件 >3"],
    ]
    
    for row_idx, row_data in enumerate(complexity_data, 2):
        for col_idx, value in enumerate(row_data, 1):
            ws3.cell(row=row_idx, column=col_idx, value=value)
    
    ws3.column_dimensions['A'].width = 15
    ws3.column_dimensions['B'].width = 10
    ws3.column_dimensions['C'].width = 50
    
    # 保存文件
    output_path = "F:/code/java/ddd-tmp/openspec/specs/功能点计算表.xlsx"
    wb.save(output_path)
    print(f"Excel文件已生成: {output_path}")

if __name__ == "__main__":
    create_function_point_excel()

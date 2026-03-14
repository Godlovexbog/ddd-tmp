package net.zhaixing.blog.user;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqlExecutor {

    private static final String URL = "jdbc:mysql://127.0.0.1:3306/blog-user?autoReconnect=true&autoReconnectForPools=true&useUnicode=true&characterEncoding=utf8&useSSL=false&allowMultiQueries=true&allowPublicKeyRetrieval=true&serverTimezone=GMT%2B8&rewriteBatchedStatements=true";
    private static final String USER = "root";
    private static final String PASSWORD = "laowei";

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java SqlExecutor <sql_file>");
            System.out.println("Example: java SqlExecutor db/test_data.sql");
            return;
        }

        String sqlFile = args[0];
        System.out.println("Executing SQL file: " + sqlFile);

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            System.out.println("Database connected successfully!");

            String sqlContent = readSqlFile(sqlFile);
            List<String> sqlStatements = parseSqlStatements(sqlContent);

            int executed = 0;
            for (String sql : sqlStatements) {
                if (sql.trim().isEmpty()) continue;

                try (Statement stmt = conn.createStatement()) {
                    boolean result = stmt.execute(sql);
                    if (!sql.trim().toUpperCase().startsWith("SELECT")) {
                        System.out.println("Executed: " + sql.substring(0, Math.min(50, sql.length())) + "...");
                        executed++;
                    }
                } catch (SQLException e) {
                    System.err.println("Error executing: " + sql.substring(0, Math.min(100, sql.length())));
                    System.err.println("Error: " + e.getMessage());
                }
            }

            System.out.println("\nTotal statements executed: " + executed);
            System.out.println("\n--- Verification Queries ---");
            runVerificationQueries(conn);

        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Failed to read SQL file: " + e.getMessage());
        }
    }

    private static String readSqlFile(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    private static List<String> parseSqlStatements(String sqlContent) {
        List<String> statements = new ArrayList<>();
        StringBuilder currentStatement = new StringBuilder();

        for (String line : sqlContent.split("\n")) {
            String trimmed = line.trim();

            if (trimmed.isEmpty() || trimmed.startsWith("--")) {
                continue;
            }

            currentStatement.append(" ").append(trimmed);

            if (trimmed.endsWith(";")) {
                statements.add(currentStatement.toString());
                currentStatement = new StringBuilder();
            }
        }

        if (currentStatement.length() > 0) {
            statements.add(currentStatement.toString());
        }

        return statements;
    }

    private static void runVerificationQueries(Connection conn) throws SQLException {
        String[] verificationSqls = {
            "SELECT 't_valuation_record' as table_name, COUNT(*) as count FROM t_valuation_record WHERE is_deleted = 0",
            "SELECT 't_transaction_record' as table_name, COUNT(*) as count FROM t_transaction_record WHERE is_deleted = 0",
            "SELECT 't_cash_delivery_record' as table_name, COUNT(*) as count FROM t_cash_delivery_record WHERE is_deleted = 0",
            "SELECT 't_financial_product_delivery_record' as table_name, COUNT(*) as count FROM t_financial_product_delivery_record WHERE is_deleted = 0",
            "SELECT " +
                "(SELECT target_amount FROM t_valuation_record WHERE project_id = 1 AND is_deleted = 0 LIMIT 1) as valuation_amount " +
                "+ (SELECT COALESCE(SUM(transaction_amount), 0) FROM t_transaction_record WHERE target_id = 1 AND transaction_status = '1' AND is_deleted = 0) as transaction_amount " +
                "+ (SELECT COALESCE(SUM(cash_amount), 0) FROM t_cash_delivery_record WHERE project_id = 1 AND delivery_status = '1' AND is_deleted = 0) as cash_delivery_amount " +
                "+ (SELECT COALESCE(SUM(delivery_amount), 0) FROM t_financial_product_delivery_record WHERE project_id = 1 AND delivery_status = '1' AND is_deleted = 0) as product_delivery_amount as total_valuation"
        };

        try (Statement stmt = conn.createStatement()) {
            for (String sql : verificationSqls) {
                try (ResultSet rs = stmt.executeQuery(sql)) {
                    while (rs.next()) {
                        int colCount = rs.getMetaData().getColumnCount();
                        StringBuilder row = new StringBuilder();
                        for (int i = 1; i <= colCount; i++) {
                            row.append(rs.getMetaData().getColumnName(i)).append("=").append(rs.getString(i)).append(" | ");
                        }
                        System.out.println(row);
                    }
                }
            }
        }
    }
}

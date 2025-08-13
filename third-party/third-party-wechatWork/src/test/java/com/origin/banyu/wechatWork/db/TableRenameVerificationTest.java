package com.origin.banyu.wechatWork.db;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Java Test: 验证 external_users -> wechatwork_external_users 重命名是否已完成
 * 运行前需提供数据库连接信息（任一方式）：
 * - 环境变量：DB_URL, DB_USER, DB_PASSWORD
 * - 或系统属性：-DDB_URL=... -DDB_USER=... -DDB_PASSWORD=...
 *
 * 示例：
 * mvn -q -pl third-party/third-party-wechatWork -DDB_URL="jdbc:mysql://127.0.0.1:3306/yourdb?useSSL=false&serverTimezone=UTC" -DDB_USER=root -DDB_PASSWORD=xxx -Dtest=com.origin.banyu.wechatWork.db.TableRenameVerificationTest test
 */
public class TableRenameVerificationTest {

    private static String envOrProp(String key) {
        String v = System.getenv(key);
        if (v == null || v.isBlank()) {
            v = System.getProperty(key);
        }
        return v;
    }

    @Test
    void verifyExternalUsersTableRenamed() throws Exception {
        String url = envOrProp("DB_URL");
        String user = envOrProp("DB_USER");
        String password = envOrProp("DB_PASSWORD");

        Assumptions.assumeTrue(url != null && !url.isBlank(), "Skip: DB_URL not provided");
        Assumptions.assumeTrue(user != null && !user.isBlank(), "Skip: DB_USER not provided");
        Assumptions.assumeTrue(password != null, "Skip: DB_PASSWORD not provided");

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            int newCount = countTable(conn, "wechatwork_external_users");
            int oldCount = countTable(conn, "external_users");

            // 新表必须存在
            Assertions.assertTrue(newCount >= 1, "Expected table 'wechatwork_external_users' to exist in current schema");
            // 旧表不可存在
            Assertions.assertEquals(0, oldCount, "Old table 'external_users' should not exist in current schema");
        }
    }

    private int countTable(Connection conn, String tableName) throws Exception {
        String sql = "select count(*) from information_schema.tables where table_schema = database() and table_name = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tableName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        }
    }
}



import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class test_bcrypt {
    public static void main(String[] args) {
        System.out.println("=== BCrypt密码验证测试 ===");
        
        // 数据库中的密码哈希
        String dbPassword = "$2a$10$ySG2lkvjFHY5O0./CPIE1OI8VJsuKYEzOYzqIa7AJR6sEgSzUFOAm";
        String testPassword = "123456";
        
        System.out.println("数据库密码哈希: " + dbPassword);
        System.out.println("测试密码: " + testPassword);
        
        // 使用不同强度的BCryptPasswordEncoder
        BCryptPasswordEncoder encoder10 = new BCryptPasswordEncoder(10);
        BCryptPasswordEncoder encoder12 = new BCryptPasswordEncoder(12);
        
        boolean matches10 = encoder10.matches(testPassword, dbPassword);
        boolean matches12 = encoder12.matches(testPassword, dbPassword);
        
        System.out.println("BCryptPasswordEncoder(10) 验证结果: " + matches10);
        System.out.println("BCryptPasswordEncoder(12) 验证结果: " + matches12);
        
        // 生成新的密码哈希进行对比
        String newHash10 = encoder10.encode(testPassword);
        String newHash12 = encoder12.encode(testPassword);
        
        System.out.println("\n新生成的哈希(强度10): " + newHash10);
        System.out.println("新生成的哈希(强度12): " + newHash12);
        
        boolean newMatches10 = encoder10.matches(testPassword, newHash10);
        boolean newMatches12 = encoder12.matches(testPassword, newHash12);
        
        System.out.println("新哈希验证结果(强度10): " + newMatches10);
        System.out.println("新哈希验证结果(强度12): " + newMatches12);
        
        // 解析数据库密码哈希
        System.out.println("\n=== 解析数据库密码哈希 ===");
        if (dbPassword.startsWith("$2a$")) {
            String[] parts = dbPassword.split("\\$");
            if (parts.length == 4) {
                String version = parts[1];
                String cost = parts[2];
                String saltAndHash = parts[3];
                String salt = saltAndHash.substring(0, 22);
                String hash = saltAndHash.substring(22);
                
                System.out.println("版本: " + version);
                System.out.println("成本因子: " + cost);
                System.out.println("盐值: " + salt);
                System.out.println("哈希值: " + hash);
            }
        }
    }
} 
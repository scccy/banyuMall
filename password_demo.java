import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class password_demo {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        
        String dbPassword = "$2a$10$ySG2lkvjFHY5O0./CPIE1OI8VJsuKYEzOYzqIa7AJR6sEgSzUFOAm";
        String testPassword = "123456";
        
        System.out.println("数据库密码: " + dbPassword);
        System.out.println("测试密码: " + testPassword);
        
        boolean matches = encoder.matches(testPassword, dbPassword);
        System.out.println("密码匹配结果: " + matches);
        
        // 生成一个新的密码哈希进行对比
        String newHash = encoder.encode(testPassword);
        System.out.println("新生成的哈希: " + newHash);
        System.out.println("新哈希匹配结果: " + encoder.matches(testPassword, newHash));
    }
} 
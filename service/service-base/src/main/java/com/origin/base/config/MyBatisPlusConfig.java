package com.origin.base.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

@Configuration
@ConditionalOnClass(MybatisConfiguration.class)
public class MyBatisPlusConfig {
    
    /**
     * 添加分页插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL)); // 如果配置多个插件, 切记分页最后添加
        // 如果有多数据源可以不配具体类型, 否则都建议配上具体的 DbType
        System.out.println("分页插件加载成功");
        return interceptor;
    }
    
    /**
     * 配置SqlSessionFactory，添加mapper扫描路径
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory(SqlSessionFactoryBean sqlSessionFactoryBean) throws Exception {
        // 设置mapper文件扫描路径
        sqlSessionFactoryBean.setMapperLocations(
            new PathMatchingResourcePatternResolver().getResources("classpath*:mapper/*.xml")
        );
        return sqlSessionFactoryBean.getObject();
    }
}


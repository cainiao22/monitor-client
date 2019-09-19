package com.qding.bigdata.monitorjobClient.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;


/**
 * Created by yanpf on 2017/9/19.
 */

@Configuration
@PropertySource("classpath:/dataSource/hive.properties")
@MapperScan(basePackages = "com.qding.bigdata.monitorjobClient.dao.hive", sqlSessionTemplateRef  = "hiveSqlSessionTemplate")
public class HiveDataSourceConfig {

    @Bean(name = "hiveDataSource")
    @ConfigurationProperties(prefix = "datasource.hive")
    @Primary
    public DataSource getDataSource() {
        return DataSourceBuilder.create().build();
    }
    @Bean(name = "hiveSqlSessionFactory")
    @Primary
    public SqlSessionFactory getSqlSessionFactory(@Qualifier("hiveDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/hive/*.xml"));
        return bean.getObject();
    }
    @Bean(name = "hiveTransactionManager")
    @Primary
    public PlatformTransactionManager getTransactionManager(@Qualifier("hiveDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
    @Bean(name = "hiveSqlSessionTemplate")
    @Primary
    public SqlSessionTemplate getSqlSessionTemplate(@Qualifier("hiveSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}

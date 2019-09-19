package com.qding.bigdata.monitorjobClient.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;


/**
 * Created by yanpf on 2017/9/19.
 */

@Configuration
@PropertySource("classpath:/dataSource/ds.properties")
@MapperScan(basePackages = "com.qding.bigdata.monitorjobClient.dao.ds", sqlSessionTemplateRef  = "dsSqlSessionTemplate")
public class DsDataSourceConfig {

    @Bean(name = "dsDataSource")
    @ConfigurationProperties(prefix = "datasource.ds")
    public DataSource getDataSource() {
        return DataSourceBuilder.create().build();
    }
    @Bean(name = "dsSqlSessionFactory")
    public SqlSessionFactory getSqlSessionFactory(@Qualifier("dsDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/ds/*.xml"));
        return bean.getObject();
    }
    @Bean(name = "dsTransactionManager")
    public PlatformTransactionManager getTransactionManager(@Qualifier("dsDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
    @Bean(name = "dsSqlSessionTemplate")
    public SqlSessionTemplate getSqlSessionTemplate(@Qualifier("dsSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}

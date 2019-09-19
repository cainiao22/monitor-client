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
@PropertySource("classpath:/dataSource/dw.properties")
@MapperScan(basePackages = "com.qding.bigdata.monitorjobClient.dao.dw", sqlSessionTemplateRef  = "dwSqlSessionTemplate")
public class DwDatasourceConfig {

    @Bean(name = "dwDataSource")
    @ConfigurationProperties(prefix = "datasource.dw")
    public DataSource getDataSource() {
        return DataSourceBuilder.create().build();
    }
    @Bean(name = "dwSqlSessionFactory")
    public SqlSessionFactory getSqlSessionFactory(@Qualifier("dwDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/dw/*.xml"));
        return bean.getObject();
    }
    @Bean(name = "dwTransactionManager")
    public PlatformTransactionManager getTransactionManager(@Qualifier("dwDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
    @Bean(name = "dwSqlSessionTemplate")
    public SqlSessionTemplate getSqlSessionTemplate(@Qualifier("dwSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}

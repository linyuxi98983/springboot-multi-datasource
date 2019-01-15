package com.imooc.config.mybatis;

import com.imooc.config.datasource.DataSourceConfiguration;
import com.imooc.config.datasource.DataSourceContextHolder;
import com.imooc.config.datasource.DynamicDataSourceRouter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
@AutoConfigureAfter({ DataSourceConfiguration.class })
@MapperScan(basePackages = { "com.imooc.dao.mapper" })
@EnableTransactionManagement
@ConditionalOnClass({ EnableTransactionManagement.class })
public class MybatisConfiguration extends MybatisAutoConfiguration
{
    
    @Resource(name = "masterDataSource")
    private DataSource masterDataSource;
    
    @Resource(name = "slaveDataSource")
    private DataSource slaveDataSource;
    
    @Resource(name = "slave2DataSource")
    private DataSource slave2DataSource;
    
    public MybatisConfiguration(MybatisProperties properties, ObjectProvider<Interceptor[]> interceptorsProvider,
            ResourceLoader resourceLoader, ObjectProvider<DatabaseIdProvider> databaseIdProvider,
            ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider)
    {
        super(properties, interceptorsProvider, resourceLoader, databaseIdProvider, configurationCustomizersProvider);
    }
    
    @Bean
    public SqlSessionFactory sqlSessionFactorys() throws Exception
    {
        return super.sqlSessionFactory(dynamicDataSource());
    }
    
    @Bean(name = "dynamicDataSource")
    public AbstractRoutingDataSource dynamicDataSource()
    {
        DynamicDataSourceRouter proxy = new DynamicDataSourceRouter();
        Map<Object, Object> targetDataSources = new HashMap<>(3);
        targetDataSources.put("slaveDataSource", slaveDataSource);
        targetDataSources.put("slave2DataSource", slave2DataSource);
        
        proxy.setDefaultTargetDataSource(slave2DataSource);
        proxy.setTargetDataSources(targetDataSources);
        
        DataSourceContextHolder.dataSourceKeys.add(targetDataSources.keySet());
        DataSourceContextHolder.slaveDataSourceKeys.add(slaveDataSource);
        DataSourceContextHolder.slaveDataSourceKeys.add(slave2DataSource);
        return proxy;
    }
    
    @Bean(name = "transactionManager")
    public DataSourceTransactionManager transactionManagers()
    {
        log.info("-------------------- transactionManager init ---------------------");
        return new DataSourceTransactionManager(dynamicDataSource());
    }
    
    @Bean
    @Primary
    @ConfigurationProperties(prefix = "mybatis")
    public SqlSessionFactoryBean sqlSessionFactoryBean()
    {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        // 配置数据源，此处配置为关键配置，如果没有将 dynamicDataSource 作为数据源则不能实现切换
        sqlSessionFactoryBean.setDataSource(dynamicDataSource());
        
        return sqlSessionFactoryBean;
    }
    
}

package com.jsj.shardingjdbcdemo.config;

import io.shardingsphere.core.api.ShardingDataSourceFactory;
import io.shardingsphere.core.api.config.ShardingRuleConfiguration;
import io.shardingsphere.core.api.config.TableRuleConfiguration;
import io.shardingsphere.core.api.config.strategy.InlineShardingStrategyConfiguration;
import io.shardingsphere.core.api.config.strategy.StandardShardingStrategyConfiguration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 配置分库分表规则和数据源整合
 */
@Configuration
@MapperScan(basePackages = ShardingDataBaseConfig.PACKAGE, sqlSessionFactoryRef = "sqlSessionFactory")
public class ShardingDataBaseConfig {
    /**
     * dao层的包路径
     */
    static final String PACKAGE = "com.jsj.shardingjdbcdemo.dao";

    /**
     * mapper文件的相对路径
     */
    private static final String MAPPER_LOCATION = "classpath:mappers/*.xml";

    /**
     * ShardingDataSource数据源,主数据源必须使用@Primary注解进行标识
     *
     * @param dataSource0
     * @param dataSource1
     * @return
     * @throws SQLException
     */
    @Primary
    @Bean(name = "shardingDataSource")
    public DataSource getDataSource(@Qualifier("ds_0") DataSource dataSource0,
                                    @Qualifier("ds_1") DataSource dataSource1) throws SQLException {
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        shardingRuleConfig.getTableRuleConfigs().add(getOrderTableRuleConfiguration());
        shardingRuleConfig.getTableRuleConfigs().add(getOrderItemTableRuleConfiguration());
        shardingRuleConfig.getBindingTableGroups().add("t_order, t_order_item");
        //配置根据user_id的分库算法(一般分库都是根据user_id分的)
        shardingRuleConfig.setDefaultDatabaseShardingStrategyConfig(
                new StandardShardingStrategyConfiguration("user_id", new DatabaseShardingAlgorithm()));
        //配置根据order_id的分表算法
        shardingRuleConfig.setDefaultTableShardingStrategyConfig(
                new StandardShardingStrategyConfiguration("order_id", new TablePreciseShardingAlgorithm(),
                        new TableRangeShardingAlgorithm()));
        Map<String, DataSource> dataSourceMap = new HashMap<>();
        dataSourceMap.put("ds_0", dataSource0);
        dataSourceMap.put("ds_1", dataSource1);
        // 获取数据源对象
        return ShardingDataSourceFactory.createDataSource(dataSourceMap, shardingRuleConfig, new ConcurrentHashMap<>(),
                new Properties());

    }

    /**
     * t_order逻辑表和数据节点配置
     *
     * @return
     */
    private static TableRuleConfiguration getOrderTableRuleConfiguration() {
        // 配置Order表规则
        TableRuleConfiguration orderTableRuleConfig = new TableRuleConfiguration();
        orderTableRuleConfig.setLogicTable("t_order");
        orderTableRuleConfig.setActualDataNodes("ds_${0..1}.t_order_${0..1}");
        orderTableRuleConfig.setKeyGeneratorColumnName("order_id");
//        // 配置分库 + 分表策略
//        orderTableRuleConfig.setDatabaseShardingStrategyConfig(
//                new InlineShardingStrategyConfiguration("user_id", "ds_${user_id % 2}"));
//        orderTableRuleConfig.setTableShardingStrategyConfig(
//                new InlineShardingStrategyConfiguration("order_id", "t_order_${order_id % 2}"));
        return orderTableRuleConfig;
    }

    /**
     * t_order_item逻辑表和数据节点配置
     *
     * @return
     */
    private static TableRuleConfiguration getOrderItemTableRuleConfiguration() {
        TableRuleConfiguration result = new TableRuleConfiguration();
        result.setLogicTable("t_order_item");
        result.setActualDataNodes("ds_${0..1}.t_order_item_${0..1}");
        return result;
    }

    /**
     * 创建该数据源的事务管理
     *
     * @param dataSource
     * @return
     * @throws SQLException
     */
    @Primary
    @Bean(name = "transactionManager")
    public DataSourceTransactionManager primaryTransactionManager(@Qualifier("shardingDataSource") DataSource dataSource)
            throws SQLException {
        return new DataSourceTransactionManager(dataSource);
    }

    /**
     * 创建Mybatis的连接会话工厂实例
     *
     * @param primaryDataSource
     * @return
     * @throws Exception
     */
    @Primary
    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory primarySqlSessionFactory(@Qualifier("shardingDataSource") DataSource primaryDataSource)
            throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        // 设置数据源bean
        sessionFactory.setDataSource(primaryDataSource);
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver()
                // 设置mapper文件路径
                .getResources(ShardingDataBaseConfig.MAPPER_LOCATION));
        return sessionFactory.getObject();
    }
}

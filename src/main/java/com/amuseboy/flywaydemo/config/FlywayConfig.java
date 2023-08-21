package com.amuseboy.flywaydemo.config;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.Map;

/**
 * @description:
 * @projectName: flyway-demo
 * @see: PACKAGE_NAME
 * @author: 刘培振
 * @createTime: 2023/8/19 17:42
 * @version:1.0
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableTransactionManagement
public class FlywayConfig {

    private final DataSource dataSource;

    @Value("${spring.flyway.locations}")
    private String SQL_LOCATION;

    @Value("${spring.flyway.baseline-on-migrate}")
    private boolean BASELINE_ON_MIGRATE;

//    @Value("${spring.flyway.table}")
//    private String VERSION_TABLE;

//    @Value("${spring.flyway.out-of-order}")
//    private boolean OUT_OF_ORDER;

//    @Value("${spring.flyway.validate-on-migrate}")
//    private boolean VALIDATE_ON_MIGRATE;

    @Bean
    public void migrateOrder() {
        DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
        Map<String, DataSource> dataSources = ds.getDataSources();
        dataSources.forEach((k, v) -> {
            log.info("正在执行多数据源生成数据库文件 " + SQL_LOCATION + "/" + k);
            Flyway flyway = Flyway.configure()
                    .dataSource(v)
                    .locations(SQL_LOCATION + "/" + k)
                    .baselineOnMigrate(BASELINE_ON_MIGRATE)
//                    .table(VERSION_TABLE)
//                    .outOfOrder(OUT_OF_ORDER)
//                    .validateOnMigrate(VALIDATE_ON_MIGRATE)
                    .load();
            flyway.migrate();
        });
    }

}

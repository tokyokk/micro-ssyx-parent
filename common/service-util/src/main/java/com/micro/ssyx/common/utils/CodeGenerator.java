package com.micro.ssyx.common.utils;


import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

public class CodeGenerator {

    public static void main(final String[] args) {

        // 1、创建代码生成器
        final AutoGenerator mpg = new AutoGenerator();

        // 2、全局配置
        // 全局配置
        final GlobalConfig gc = new GlobalConfig();
        gc.setOutputDir("/Users/micro/Desktop/micro-ssyx-parent/service/service-order" + "/src/main/java");

        // 去掉Service接口的首字母I
        gc.setServiceName("%sService");
        gc.setAuthor("micro");
        gc.setOpen(false);
        mpg.setGlobalConfig(gc);

        // 3、数据源配置
        final DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl("jdbc:mysql://localhost:3306/community-order?serverTimezone=GMT%2B8&useSSL=false&allowPublicKeyRetrieval=true ");
        dsc.setDriverName("com.mysql.cj.jdbc.Driver");
        dsc.setUsername("root");
        dsc.setPassword("root");
        dsc.setDbType(DbType.MYSQL);
        mpg.setDataSource(dsc);

        // 4、包配置
        final PackageConfig pc = new PackageConfig();
        // com.micro.ssyx.product.
        pc.setParent("com.micro.ssyx");
        // 模块名
        pc.setModuleName("order");
        pc.setController("controller");
        pc.setService("service");
        pc.setMapper("mapper");
        mpg.setPackageInfo(pc);

        /* 5、策略配置 */
        final StrategyConfig strategy = new StrategyConfig();

        strategy.setInclude("order_info", "order_item");

        // 数据库表映射到实体的命名策略
        strategy.setNaming(NamingStrategy.underline_to_camel);

        // 数据库表字段映射到实体的命名策略
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        // lombok 模型 @Accessors(chain = true) setter链式操作
        strategy.setEntityLombokModel(true);

        // restful api风格控制器
        strategy.setRestControllerStyle(true);
        // url中驼峰转连字符
        strategy.setControllerMappingHyphenStyle(true);

        mpg.setStrategy(strategy);

        // 6、执行
        mpg.execute();
    }
}

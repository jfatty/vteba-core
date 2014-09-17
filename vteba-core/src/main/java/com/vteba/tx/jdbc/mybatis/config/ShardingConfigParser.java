package com.vteba.tx.jdbc.mybatis.config;

import java.io.InputStream;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vteba.io.DefaultResourceLoader;
import com.vteba.io.Resource;
import com.vteba.io.ResourceLoader;
import com.vteba.tx.jdbc.mybatis.strategy.ShardingStrategy;

/**
 * 使用dom4j解析分表配置文件
 * @author yinlei 
 * @since 2013-12-16 15:26
 */
public class ShardingConfigParser {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ShardingConfigParser.class);
    
    @SuppressWarnings("unchecked")
    public static ShardingConfigFactory parse(InputStream input) throws Exception {
        final ShardingConfigFactory configHolder = ShardingConfigFactory.getInstance();

        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(input);
        Element rootElement = document.getRootElement();
        
        List<Element> ignoreList = rootElement.elements("ignoreList");
        if (ignoreList != null) {
            
        }
        
        List<Element> parseList = rootElement.elements("parseList");
        if (parseList != null) {
            
        }
        
        List<Element> strategyList = rootElement.elements("strategy");
        
        if (strategyList != null) {
            for (Element strategy : strategyList) {
                String table = strategy.attributeValue("tableName");

                String className = strategy.attributeValue("strategyClass");
                try {
                    Class<?> clazz = Class.forName(className);
                    ShardingStrategy shardingStrategy = (ShardingStrategy) clazz.newInstance();

                    configHolder.register(table, shardingStrategy);
                } catch (ClassNotFoundException e) {
                    LOGGER.error("没有找到类class=[]。", className, e.getMessage());
                } catch (InstantiationException e) {
                    LOGGER.error("实例化类class=[]异常。", className, e.getMessage());
                } catch (IllegalAccessException e) {
                    LOGGER.error("非法访问异常，class=[]。", className, e.getMessage());
                }
            }
        }

        return configHolder;
    }
    
    public static void main(String[] aa) {
        ResourceLoader loader = new DefaultResourceLoader();
        Resource resource = loader.getResource("classpath:sharding-config.xml");
        try {
            parse(resource.getInputStream());
        } catch (Exception e) {
            
        }
    }
}

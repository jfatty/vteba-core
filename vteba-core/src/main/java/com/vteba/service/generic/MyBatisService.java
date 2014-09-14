package com.vteba.service.generic;

import java.util.List;

import org.apache.ibatis.annotations.Param;

/**
 * 基于MyBatis的通用service接口。
 * @author yinlei
 * @since 2013-12-14 19:40
 * @param <T> 实体类
 * @param <BEAN> 查询参数Bean类，MyBatis自动生成的
 * @param <ID> 实体ID类
 */
public interface MyBatisService<T, BEAN, ID> {

	/**
     * 根据bean所携带条件进行count计数。
     * @param bean 查询条件
     * @date 2014-02-27 17:59:34
     */
    int count(BEAN bean);

    /**
     * 根据bean所携带条件删除记录。
     * @param bean 查询条件
     * @date 2014-02-27 17:59:34
     */
    int deleteBatch(BEAN bean);

    /**
	 * 根据主键删除记录。
	 * @param id 主键id
     * @date 2014-02-27 17:59:34
	 */
	public int deleteById(ID id);
    
    /**
     * 插入记录，所有字段都不能为空。
     * @param record 要被保存的数据
     * @date 2014-02-27 17:59:34
     */
    //int saveAll(T record);

    /**
     * 插入记录，只有非空字段才会插入到数据库。
     * @param record 要被保存的数据
     * @date 2014-02-27 17:59:34
     */
    int save(T record);

    /**
     * 根据Criteria所携带条件查询数据，不含BLOB字段。
     * @param bean 查询条件
     * @date 2014-02-27 17:59:34
     */
    List<T> queryList(BEAN bean);

    /**
     * 根据主键查询数据。
     * @param id 主键
     * @date 2014-02-27 17:59:34
     */
    T queryById(ID id);

    /**
     * 根据Criteria所携带条件更新指定字段。
     * @param record 要更新的数据
     * @param bean update的where条件
     * @date 2014-02-27 17:59:34
     */
    int updateBatch(@Param("record") T record, @Param("example") BEAN bean);

    /**
     * 根据Criteria所携带条件更新所有字段，不含BLOB字段。
     * @param record 要更新的数据
     * @param bean update的where条件
     * @date 2014-02-27 17:59:34
     */
    //int updateAllBatch(@Param("record") T record, @Param("example") BEAN bean);

    /**
     * 根据主键更新指定字段的数据。
     * @param record 要更新的数据，含有Id
     * @date 2014-02-27 17:59:34
     */
    int updateById(T record);

    /**
     * 根据主键更新所有字段的数据，不含BLOB字段。
     * @param record 要更新的数据，含有Id
     * @date 2014-02-27 17:59:34
     */
    //int updateAllById(T record);

}

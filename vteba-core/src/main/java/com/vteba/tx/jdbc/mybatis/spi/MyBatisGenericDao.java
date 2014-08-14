package com.vteba.tx.jdbc.mybatis.spi;

import java.util.List;

import com.vteba.tx.jdbc.mybatis.mapper.BaseMapper;

/**
 * MyBatis统一泛型接口
 * @author yinlei 
 * @since 2013-12-2 9:21
 */
public interface MyBatisGenericDao<T, V, ID> extends BaseMapper {
    /**
     * 根据Criteria所携带条件进行count计数。
     * @param example 查询条件
     *
     * @date 2014-02-28 17:55:06
     */
    int countByExample(V example);

    /**
     * 根据Criteria所携带条件删除记录。
     * @param example 查询条件
     *
     * @date 2014-02-28 17:55:06
     */
    int deleteByExample(V example);

    /**
     * 根据主键删除记录。
     * @param id 主键id
     *
     * @date 2014-02-28 17:55:06
     */
    int deleteByPrimaryKey(ID id);

    /**
     * 插入记录，所有字段都不能为空。
     * @param record 要被保存的数据
     *
     * @date 2014-02-28 17:55:06
     */
    int insert(T record);

    /**
     * 插入记录，只有非空字段才会插入到数据库。
     * @param record 要被保存的数据
     *
     * @date 2014-02-28 17:55:06
     */
    int insertSelective(T record);

    /**
     * 根据Criteria所携带条件查询数据，不含BLOB字段。
     * @param example 查询条件
     *
     * @date 2014-02-28 17:55:06
     */
    List<T> selectByExample(V example);

    /**
     * 根据主键查询数据。
     * @param id 主键
     *
     * @date 2014-02-28 17:55:06
     */
    T selectByPrimaryKey(ID id);
}

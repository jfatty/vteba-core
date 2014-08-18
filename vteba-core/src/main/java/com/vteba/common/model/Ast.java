package com.vteba.common.model;

import java.io.Serializable;
import java.util.Date;

/**
 * 标记可以被hibernate拦截器拦截的实体
 * @author yinlei 
 * @since 2013-8-18
 */
public interface Ast extends Serializable {
    public void setCtime(Date ctime);
    public void setMtime(Date mtime);
    public void setDatasource(String datasource);
    public void setModifysource(String modifysource);
    
}

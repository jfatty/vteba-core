package com.vteba.tx.matrix.table;

import org.joda.time.DateTime;

/**
 * 按星期分表规则
 * @author 尹雷 
 * @see
 * @since 2013-12-5 11:28
 */
public class WeekTableRuler {
    public String getTableName(String table) {
        DateTime dateTime = new DateTime();
        dateTime.getWeekOfWeekyear();
        table = table + "_" + dateTime.getWeekyear() + dateTime.getWeekOfWeekyear() + "w";
        return table;
    }
    
    public TableRulerType getRulerType() {
        return TableRulerType.WEEK;
    }
    
    public String buildQueryString(String sql) {
        return sql;
    }
}

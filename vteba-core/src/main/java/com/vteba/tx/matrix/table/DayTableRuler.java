package com.vteba.tx.matrix.table;

import com.vteba.utils.date.DateUtils;


public class DayTableRuler {
    public String getTableName(String table) {
        table = table + "_" + DateUtils.toDateString("yyyyMMdd") + "d";
        return table;
    }
    
    public TableRulerType getRulerType() {
        return TableRulerType.DAY;
    }
}

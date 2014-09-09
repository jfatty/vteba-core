package com.vteba.tx.matrix.table;

import com.vteba.utils.date.DateUtils;


public class MonthTableRuler {
    public String getTableName(String table) {
        table = table + "_" + DateUtils.toDateString("yyyyMM") + "m";
        return table;
    }
    
    public TableRulerType getRulerType() {
        return TableRulerType.MONTH;
    }
}

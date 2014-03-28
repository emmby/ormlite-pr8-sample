package com.groupon.dbtest.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "bugcount" )
public class BugCount {
    @DatabaseField( columnName = "_id", generatedId = true)
    protected Long id;

    @DatabaseField
    private int count;

    public BugCount() {
    }

    public BugCount(int count) {
        this.count = count;
    }

    public String getTitle() {
        return count + " little bugs in the code";
    }
}

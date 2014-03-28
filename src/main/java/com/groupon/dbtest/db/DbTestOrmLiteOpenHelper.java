package com.groupon.dbtest.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.groupon.dbtest.model.BugCount;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.Random;
import java.util.concurrent.Callable;

public class DbTestOrmLiteOpenHelper extends OrmLiteSqliteOpenHelper {
    public static int DATABASE_VERSION=5;

    protected Dao<BugCount,Long> bugCountDao;

    public DbTestOrmLiteOpenHelper(Context context ) {
        super(context.getApplicationContext(),"dbtest",null,DATABASE_VERSION);
        try {
            bugCountDao = getDao(BugCount.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, BugCount.class);
            bugCountDao.create(new BugCount(99));
            addSomeBugCounts(bugCountDao, 4);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    // Really this belongs in the DAO
    public static void addSomeBugCounts(final Dao<BugCount, Long> bugCountDao, final int count)  {
        final Random rand = new Random();
        try {
            bugCountDao.callBatchTasks(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    for( int i=0; i<count; ++i )
                        bugCountDao.create(new BugCount(rand.nextInt(99)));
                    return null;
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        bugCountDao.notifyContentChanged(); // You must manually notify for batch operations
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, BugCount.class,true);
            onCreate(database,connectionSource);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

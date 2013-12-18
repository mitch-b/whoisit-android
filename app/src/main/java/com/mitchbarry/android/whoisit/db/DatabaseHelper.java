package com.mitchbarry.android.whoisit.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.mitchbarry.android.whoisit.core.PhoneGroup;
import com.mitchbarry.android.whoisit.core.PhoneMatch;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mitchell on 12/13/13.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    public static final String TAG = "DatabaseHelper";
    public static final String DATABASE_NAME = "WhoIsIt.sqlite";
    private static final int DATABASE_VERSION = 1;

    private Dao<PhoneGroup, Integer> phoneGroupDao = null;
    private Dao<PhoneMatch, Integer> phoneMatchDao = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, PhoneMatch.class);
            TableUtils.createTable(connectionSource, PhoneGroup.class);
        } catch (SQLException sqle) {
            Log.e(TAG, "Can't create database", sqle);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        List<String> sqlStatements = new ArrayList<String>();
        if (oldVersion < 1 && newVersion >= 1) {
            // initial database
        }

//        if (oldVersion < 2 && newVersion >= 2) {
//
//        }

        try {
            for (String sql : sqlStatements) {
                sqLiteDatabase.execSQL(sql);
            }
        } catch (android.database.SQLException sqle) {
            Log.e(TAG, "Failure during onUpgrade", sqle);
        }
    }

    public Dao<PhoneMatch, Integer> getPhoneMatchDao() {
        if (phoneMatchDao == null) {
            try {
                phoneMatchDao = getDao(PhoneMatch.class);
            } catch (SQLException sqle) {
                Log.e(TAG, "Error generating PhoneMatch DAO", sqle);
            }
        }
        return phoneMatchDao;
    }
    public Dao<PhoneGroup, Integer> getPhoneGroupDao() {
        if (phoneGroupDao == null) {
            try {
                phoneGroupDao = getDao(PhoneGroup.class);
            } catch (SQLException sqle) {
                Log.e(TAG, "Error generating PhoneGroup DAO", sqle);
            }
        }
        return phoneGroupDao;
    }
}

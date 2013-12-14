package com.mitchbarry.android.whoisit.db;

import android.content.Context;
import android.util.Log;
import com.mitchbarry.android.whoisit.core.PhoneGroup;
import com.mitchbarry.android.whoisit.core.PhoneMatch;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Mitchell on 12/13/13.
 */
public class DatabaseManager {
    public static final String TAG = "DatabaseManager";

    static private DatabaseManager instance;
    private DatabaseHelper helper;
    private DatabaseManager (Context context) {
        helper = new DatabaseHelper(context);
    }
    static public void init(Context context) {
        if (instance == null) {
            instance = new DatabaseManager(context);
        }
    }
    static public DatabaseManager getInstance() {
        return instance;
    }
    private DatabaseHelper getHelper() {
        return helper;
    }

    public List<PhoneGroup> getPhoneGroups() {
        List<PhoneGroup> phoneGroups = null;
        try {
            phoneGroups = getHelper().getPhoneGroupDao().queryForAll();
        } catch (SQLException sqle) {
            Log.e(TAG, "Failed to get PhoneGroups", sqle);
        }
        return phoneGroups;
    }
    public void addPhoneGroup(PhoneGroup group) {
        try {
            getHelper().getPhoneGroupDao().create(group);
        } catch (SQLException sqle) {
            Log.e(TAG, "Error when creating PhoneGroup", sqle);
        }
    }
    public void deletePhoneGroup(PhoneGroup group) {
        try {
            getHelper().getPhoneGroupDao().delete(group);
        } catch (SQLException sqle) {
            Log.e(TAG, "Error when deleting PhoneGroup", sqle);
        }
    }

    public List<PhoneMatch> getPhoneMatches() {
        List<PhoneMatch> phoneMatches = null;
        try {
            phoneMatches = getHelper().getPhoneMatchDao().queryForAll();
        } catch (SQLException sqle) {
            Log.e(TAG, "Failed to get PhoneMatches", sqle);
        }
        return phoneMatches;
    }
    public void addPhoneMatch(PhoneMatch group) {
        try {
            getHelper().getPhoneMatchDao().create(group);
        } catch (SQLException sqle) {
            Log.e(TAG, "Error when creating PhoneMatch", sqle);
        }
    }
    public void deletePhoneMatch(PhoneMatch group) {
        try {
            getHelper().getPhoneMatchDao().delete(group);
        } catch (SQLException sqle) {
            Log.e(TAG, "Error when deleting PhoneMatch", sqle);
        }
    }
}

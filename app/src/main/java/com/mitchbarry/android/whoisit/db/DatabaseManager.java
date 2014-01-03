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
    private final String TAG = this.getClass().getSimpleName();

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
    public PhoneGroup getPhoneGroup(int id) {
        PhoneGroup phoneGroup = null;
        try {
            phoneGroup = getHelper().getPhoneGroupDao().queryForId(id);
        } catch (SQLException sqle) {
            Log.e(TAG, "Failed to get PhoneGroup", sqle);
        }
        return phoneGroup;
    }
    public int addPhoneGroup(PhoneGroup group) {
        int id = -1;
        try {
            getHelper().getPhoneGroupDao().create(group);
            id = group.getId();
        } catch (SQLException sqle) {
            Log.e(TAG, "Error when creating PhoneGroup", sqle);
        }
        return id;
    }
    public void updatePhoneGroup(PhoneGroup group) {
        try {
            getHelper().getPhoneGroupDao().update(group);
        } catch (SQLException sqle) {
            Log.e(TAG, "Error when updating PhoneGroup", sqle);
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
            if (phoneMatches.size() > 0) {
                for (PhoneMatch match : phoneMatches) {

                }
            }
        } catch (SQLException sqle) {
            Log.e(TAG, "Failed to get PhoneMatches", sqle);
        }
        return phoneMatches;
    }
    public int addPhoneMatch(PhoneMatch match) {
        int id = -1;
        try {
            getHelper().getPhoneMatchDao().create(match);
            id = match.getId();
        } catch (SQLException sqle) {
            Log.e(TAG, "Error when creating PhoneMatch", sqle);
        }
        return id;
    }
    public void updatePhoneMatch(PhoneMatch match) {
        try {
            getHelper().getPhoneMatchDao().update(match);
        } catch (SQLException sqle) {
            Log.e(TAG, "Error when updating PhoneMatch", sqle);
        }
    }
    public void deletePhoneMatch(PhoneMatch match) {
        try {
            getHelper().getPhoneMatchDao().delete(match);
        } catch (SQLException sqle) {
            Log.e(TAG, "Error when deleting PhoneMatch", sqle);
        }
    }
}

package com.mitchbarry.android.whoisit.core;

import android.content.Context;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.mitchbarry.android.whoisit.db.DatabaseManager;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Mitchell on 12/12/13.
 */
@DatabaseTable(tableName = "phoneGroups")
public class PhoneGroup implements Serializable {
    public static final String NAME_FIELD_NAME = "name";
    public static final String RINGTONE_FIELD_NAME = "ringtone";
    public static final String VIBRATE_FIELD_NAME = "vibrate";

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(columnName = NAME_FIELD_NAME, canBeNull = false, unique = true)
    private String name;
    @DatabaseField(columnName = RINGTONE_FIELD_NAME, canBeNull = true)
    private String ringtone;
    @DatabaseField(columnName = VIBRATE_FIELD_NAME, canBeNull = true)
    private String vibrate;
    @ForeignCollectionField(eager = true)
    ForeignCollection<PhoneMatch> matches;

    public PhoneGroup() {
        // no-arg constructor required by ORMLite
    }

    public PhoneGroup(String name) {
        this.name = name;
    }

    public void updateFromDB(Context context) {
        DatabaseManager.init(context);
        PhoneGroup group = DatabaseManager.getInstance().getPhoneGroup(this.id);
        this.matches = group.getMatches();
        this.name = group.getName();
        this.ringtone = group.getRingtone();
        this.vibrate = group.getVibrate();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRingtone() {
        return ringtone;
    }

    public void setRingtone(String ringtone) {
        this.ringtone = ringtone;
    }

    public String getVibrate() {
        return vibrate;
    }

    public void setVibrate(String vibrate) {
        this.vibrate = vibrate;
    }

    public ForeignCollection<PhoneMatch> getMatches() {
        return matches;
    }

    public void setMatches(ForeignCollection<PhoneMatch> matches) {
        this.matches = matches;
    }
}

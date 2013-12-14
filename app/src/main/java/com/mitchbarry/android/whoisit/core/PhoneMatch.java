package com.mitchbarry.android.whoisit.core;

/**
 * Created by Mitchell on 12/12/13.
 */
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "phoneMatches")
public class PhoneMatch implements Serializable {
    public static final String GROUP_ID_FIELD_NAME = "groupId";
    public static final String PATTERN_FIELD_NAME = "pattern";
    public static final String RINGTONE_FIELD_NAME = "ringtone";
    public static final String VIBRATE_FIELD_NAME = "vibrate";

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = GROUP_ID_FIELD_NAME)
    private PhoneGroup group;
    @DatabaseField(columnName = PATTERN_FIELD_NAME, canBeNull = false)
    private String pattern;
    @DatabaseField(columnName = RINGTONE_FIELD_NAME, canBeNull = true)
    private String ringtone;
    @DatabaseField(columnName = VIBRATE_FIELD_NAME, canBeNull = true)
    private String vibrate;

    public PhoneMatch() {
        // no-arg constructor required by ORMLite
    }

    public PhoneMatch(PhoneGroup group, String pattern, String ringtone, String vibrate) {
        this.group = group;
        this.pattern = pattern;
        this.ringtone = ringtone;
        this.vibrate = vibrate;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("id=").append(id);
        sb.append(", ").append("pattern=").append(pattern);
        sb.append(", ").append("ringtone=").append(ringtone);
        sb.append(", ").append("vibrate=").append(vibrate);
        return sb.toString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PhoneGroup getGroup() {
        return group;
    }

    public void setGroup(PhoneGroup group) {
        this.group = group;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
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
}

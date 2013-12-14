package com.mitchbarry.android.whoisit.core;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Mitchell on 12/12/13.
 */
@DatabaseTable(tableName = "phoneGroups")
public class PhoneGroup implements Serializable {
    public static final String NAME_FIELD_NAME = "name";

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(columnName = NAME_FIELD_NAME, canBeNull = false, unique = true)
    private String name;
    @ForeignCollectionField(eager = true)
    ForeignCollection<PhoneMatch> matches;

    public PhoneGroup() {
        // no-arg constructor required by ORMLite
    }

    public PhoneGroup(String name, List<PhoneMatch> matches) {
        this.name = name;
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

    public ForeignCollection<PhoneMatch> getMatches() {
        return matches;
    }

    public void setMatches(ForeignCollection<PhoneMatch> matches) {
        this.matches = matches;
    }
}

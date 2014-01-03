package com.mitchbarry.android.whoisit.core;

import android.content.Context;
import android.util.Log;
import com.j256.ormlite.dao.ForeignCollection;
import com.mitchbarry.android.whoisit.db.DatabaseManager;

import java.util.List;
import java.util.regex.PatternSyntaxException;

/**
 * Created by Mitchell on 12/22/13.
 */
public class WhoIsItMatcher {
    private static final String TAG = "WhoIsItMatcher";
    private static Context context = null;

    public static void init(Context context) {
        WhoIsItMatcher.context = context;
    }

    public static PhoneGroup findMatchingGroup(String incomingNumber) {
        DatabaseManager.init(context);

        List<PhoneGroup> groups = DatabaseManager.getInstance().getPhoneGroups();
        if (groups != null) {
            for (PhoneGroup group : groups) {
                ForeignCollection<PhoneMatch> matches = group.getMatches();
                if (matches.size() > 0) {
                    for (PhoneMatch match : matches) {
                        try {
                            if (WhoIsItMatcher.matches(incomingNumber, match.getPattern())) {
                                return group;
                            }
                        } catch (PatternSyntaxException pse) {
                            Log.e(TAG, "Invalid pattern syntax for match", pse);
                        }
                    }
                }
            }
        }
        return null;
    }

    public static Boolean matches(String incomingNumber, String regexMatch) {
        return incomingNumber.matches(regexMatch.replace("*", ".*"));
    }


}

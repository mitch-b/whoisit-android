package com.mitchbarry.android.whoisit.ui;

import static com.mitchbarry.android.whoisit.core.Constants.Extra.PHONE_GROUP;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.j256.ormlite.dao.ForeignCollection;
import com.mitchbarry.android.whoisit.Injector;
import com.mitchbarry.android.whoisit.R;
import com.mitchbarry.android.whoisit.core.PhoneGroup;
import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.mitchbarry.android.whoisit.core.PhoneMatch;
import com.mitchbarry.android.whoisit.db.DatabaseManager;

import java.util.Collections;
import java.util.List;

public class PhoneGroupListFragment  extends ItemListFragment<PhoneGroup> {
    public static final String TAG = "PhoneGroupListFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText(R.string.no_phone_groups);

    }

    @Override
    protected void configureList(Activity activity, ListView listView) {
        super.configureList(activity, listView);

        listView.setFastScrollEnabled(true);
        listView.setDividerHeight(0);

        getListAdapter().addHeader(activity.getLayoutInflater()
                        .inflate(R.layout.phone_group_list_item_labels, null));
    }

    @Override
    public Loader<List<PhoneGroup>> onCreateLoader(int id, Bundle args) {
        final List<PhoneGroup> initialItems = items;
        return new ThrowableLoader<List<PhoneGroup>>(getActivity(), items) {
            @Override
            public List<PhoneGroup> loadData() throws Exception {
                DatabaseManager.init(getContext());
                try {
                    List<PhoneGroup> latest = null;

                    if(getActivity() != null)
                        latest = DatabaseManager.getInstance().getPhoneGroups();

                    if (latest != null)
                        return latest;
                    else
                        return Collections.emptyList();
                } catch (Exception e) {
                    Log.e(TAG, "Error getting PhoneGroups in ListFragment", e);
                    Activity activity = getActivity();
                    if (activity != null)
                        activity.finish();
                    return initialItems;
                }
            }
        };

    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        PhoneGroup phoneGroup = ((PhoneGroup) l.getItemAtPosition(position));

        startActivity(new Intent(getActivity(), PhoneGroupActivity.class).putExtra(PHONE_GROUP, phoneGroup));
    }

    public void onListItemLongClick(AdapterView<?> parent, View v, int position, long id) {
        final PhoneGroup group = (PhoneGroup) getListView().getItemAtPosition(position);
        final Context context = v.getContext();
        new AlertDialog.Builder(context)
                .setTitle("Delete Group")
                .setMessage(getString(R.string.delete_group_message))
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        DatabaseManager.init(context);
                        ForeignCollection<PhoneMatch> matches = group.getMatches();
                        for (PhoneMatch match : matches) {
                            DatabaseManager.getInstance().deletePhoneMatch(match);
                        }
                        DatabaseManager.getInstance().deletePhoneGroup(group);
                        forceRefresh();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Do nothing.
            }
        }).show();
    }

    @Override
    public void onLoadFinished(Loader<List<PhoneGroup>> loader, List<PhoneGroup> items) {
        super.onLoadFinished(loader, items);

    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.error_loading_phone_groups;
    }

    @Override
    protected SingleTypeAdapter<PhoneGroup> createAdapter(List<PhoneGroup> items) {
        return new PhoneGroupListAdapter(getActivity().getLayoutInflater(), items);
    }
}

package com.mitchbarry.android.whoisit.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.mitchbarry.android.whoisit.Injector;
import com.mitchbarry.android.whoisit.R;
import com.mitchbarry.android.whoisit.core.PhoneGroup;
import com.mitchbarry.android.whoisit.core.PhoneMatch;
import com.mitchbarry.android.whoisit.db.DatabaseManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.mitchbarry.android.whoisit.core.Constants.Extra.PHONE_GROUP;

public class PhoneMatchListFragment extends ItemListFragment<PhoneMatch> {
    public static final String TAG = "PhoneMatchListFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText(R.string.no_phone_matches);

    }

    @Override
    protected void configureList(Activity activity, ListView listView) {
        super.configureList(activity, listView);

        listView.setFastScrollEnabled(true);
        listView.setDividerHeight(0);
    }

    @Override
    public Loader<List<PhoneMatch>> onCreateLoader(int id, Bundle args) {
        final List<PhoneMatch> initialItems = items;
        final PhoneGroup phoneGroup = (PhoneGroup) args.getSerializable(PHONE_GROUP);
        return new ThrowableLoader<List<PhoneMatch>>(getActivity(), items) {
            @Override
            public List<PhoneMatch> loadData() throws Exception {
                try {
                    List<PhoneMatch> latest = new ArrayList<PhoneMatch>();
                    if(getActivity() != null) {
                        if (phoneGroup != null) {
                            // if provided with a PhoneGroup, only pull
                            // matches associated with it
                            phoneGroup.updateFromDB(getContext());
                            for (PhoneMatch match : phoneGroup.getMatches()) {
                                latest.add(match);
                            }
                        }
                    }

                    if (latest != null)
                        return latest;
                    else
                        return Collections.emptyList();
                } catch (Exception e) {
                    Log.e(TAG, "Error getting PhoneMatches in ListFragment", e);
                    Activity activity = getActivity();
                    if (activity != null)
                        activity.finish();
                    return initialItems;
                }
            }
        };

    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        // PhoneMatch phoneMatch = ((PhoneMatch) l.getItemAtPosition(position));
    }

    public void onListItemLongClick(AdapterView<?> parent, View v, int position, long id) {
        final PhoneMatch match = (PhoneMatch) getListView().getItemAtPosition(position);
        final Context context = v.getContext();
        new AlertDialog.Builder(context)
                .setTitle("Delete Match")
                .setMessage(getString(R.string.delete_match_message))
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        DatabaseManager.init(context);
                        DatabaseManager.getInstance().deletePhoneMatch(match);
                        forceRefresh();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Do nothing.
            }
        }).show();
    }

    @Override
    public void onLoadFinished(Loader<List<PhoneMatch>> loader, List<PhoneMatch> items) {
        super.onLoadFinished(loader, items);

    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.error_loading_phone_matches;
    }

    @Override
    protected SingleTypeAdapter<PhoneMatch> createAdapter(List<PhoneMatch> items) {
        return new PhoneMatchListAdapter(getActivity().getLayoutInflater(), items);
    }
}

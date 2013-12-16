package com.mitchbarry.android.whoisit.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.mitchbarry.android.whoisit.Injector;
import com.mitchbarry.android.whoisit.R;
import com.mitchbarry.android.whoisit.core.PhoneMatch;
import com.mitchbarry.android.whoisit.db.DatabaseManager;

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
        return new ThrowableLoader<List<PhoneMatch>>(getActivity(), items) {
            @Override
            public List<PhoneMatch> loadData() throws Exception {
                DatabaseManager.init(getContext());
                try {
                    List<PhoneMatch> latest = null;

                    if(getActivity() != null)
                        latest = DatabaseManager.getInstance().getPhoneMatches();

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
        PhoneMatch phoneMatch = ((PhoneMatch) l.getItemAtPosition(position));
        // TODO: start dialog here
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

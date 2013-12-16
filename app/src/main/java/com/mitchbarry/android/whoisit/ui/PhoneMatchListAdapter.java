package com.mitchbarry.android.whoisit.ui;

import android.text.TextUtils;
import android.view.LayoutInflater;
import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.mitchbarry.android.whoisit.R;
import com.mitchbarry.android.whoisit.core.PhoneMatch;

import java.util.List;

/**
 * Adapter to display a list of phone matches
 */
public class PhoneMatchListAdapter extends SingleTypeAdapter<PhoneMatch> {
    /**
     * @param inflater
     * @param items
     */
    public PhoneMatchListAdapter(LayoutInflater inflater, List<PhoneMatch> items) {
        super(inflater, R.layout.phone_match_list_item);

        setItems(items);
    }

    /**
     * @param inflater
     */
    public PhoneMatchListAdapter(LayoutInflater inflater) {
        this(inflater, null);

    }

    @Override
    public long getItemId(final int position) {
        final String id = getItem(position).getPattern() + getItem(position).getGroup().getId();
        return !TextUtils.isEmpty(id) ? id.hashCode() : super
                .getItemId(position);
    }

    @Override
    protected int[] getChildViewIds() {
        return new int[] { R.id.tv_match };
    }

    @Override
    protected void update(int position, PhoneMatch phoneMatch) {
        setText(0, phoneMatch.getPattern());
    }
}

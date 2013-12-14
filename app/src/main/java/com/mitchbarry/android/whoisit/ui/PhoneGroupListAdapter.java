package com.mitchbarry.android.whoisit.ui;

import android.text.TextUtils;
import android.view.LayoutInflater;

import com.mitchbarry.android.whoisit.R;
import com.mitchbarry.android.whoisit.core.PhoneGroup;
import com.github.kevinsawicki.wishlist.SingleTypeAdapter;

import java.util.List;

/**
 * Adapter to display a list of traffic items
 */
public class PhoneGroupListAdapter extends SingleTypeAdapter<PhoneGroup> {
    /**
     * @param inflater
     * @param items
     */
    public PhoneGroupListAdapter(LayoutInflater inflater, List<PhoneGroup> items) {
        super(inflater, R.layout.phone_group_list_item);

        setItems(items);
    }

    /**
     * @param inflater
     */
    public PhoneGroupListAdapter(LayoutInflater inflater) {
        this(inflater, null);

    }

    @Override
    public long getItemId(final int position) {
        final String id = getItem(position).getName();
        return !TextUtils.isEmpty(id) ? id.hashCode() : super
                .getItemId(position);
    }

    @Override
    protected int[] getChildViewIds() {
        return new int[] { R.id.tv_name };
    }

    @Override
    protected void update(int position, PhoneGroup phoneGroup) {
        setText(0, phoneGroup.getName());
    }

}

package com.mitchbarry.android.whoisit.ui;

import static com.mitchbarry.android.whoisit.core.Constants.Extra.PHONE_GROUP;
import android.os.Bundle;
import android.widget.TextView;

import com.mitchbarry.android.whoisit.R;
import com.mitchbarry.android.whoisit.core.PhoneGroup;

import butterknife.InjectView;

public class PhoneGroupActivity extends BootstrapActivity {

    @InjectView(R.id.tv_name) protected TextView name;

    protected PhoneGroup phoneGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.phone_group_view);

        if(getIntent() != null && getIntent().getExtras() != null) {
            phoneGroup = (PhoneGroup) getIntent().getExtras().getSerializable(PHONE_GROUP);
        }

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        name.setText(phoneGroup.getName());
    }
}

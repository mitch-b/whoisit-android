package com.mitchbarry.android.whoisit.ui;

import android.os.Bundle;
import android.widget.TextView;
import butterknife.InjectView;
import com.mitchbarry.android.whoisit.R;
import com.mitchbarry.android.whoisit.core.PhoneGroup;

import static com.mitchbarry.android.whoisit.core.Constants.Extra.PHONE_GROUP;

public class AboutActivity extends BootstrapActivity {

    @InjectView(R.id.tv_description) protected TextView description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.about_view);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        description.setText("This app is open source on GitHub! http://github.com/mitch-b/whoisit-android");
    }
}

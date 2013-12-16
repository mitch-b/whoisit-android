package com.mitchbarry.android.whoisit.ui;

import static com.mitchbarry.android.whoisit.core.Constants.Extra.PHONE_GROUP;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.mitchbarry.android.whoisit.R;
import com.mitchbarry.android.whoisit.core.PhoneGroup;

import butterknife.InjectView;
import com.mitchbarry.android.whoisit.core.PhoneMatch;
import com.mitchbarry.android.whoisit.db.DatabaseManager;

import java.util.ArrayList;

public class PhoneGroupActivity extends BootstrapActivity implements View.OnClickListener {
    public static final String TAG = "PhoneGroupActivity";

    @InjectView(R.id.edit_name) protected EditText name;
    @InjectView(R.id.list_matches) protected ListView list_matches;
    @InjectView(R.id.btnAddMatch) protected Button btnAddMatch;

    protected PhoneGroup phoneGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.phone_group_modify_view);

        if(getIntent() != null && getIntent().getExtras() != null) {
            phoneGroup = (PhoneGroup) getIntent().getExtras().getSerializable(PHONE_GROUP);
        }

        btnAddMatch.setOnClickListener(this);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DatabaseManager.init(this);

        setupViews();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (phoneGroup != null) {
            phoneGroup.setName(name.getText().toString());
            DatabaseManager.getInstance().updatePhoneGroup(phoneGroup);
        } else { 
            phoneGroup = new PhoneGroup(name.getText().toString());
            DatabaseManager.getInstance().addPhoneGroup(phoneGroup);
        }
    }

    private void setupViews() {
        if (phoneGroup != null) {
            name.setText(phoneGroup.getName());
            list_matches.setAdapter(new PhoneMatchListAdapter(this.getLayoutInflater(), new ArrayList<PhoneMatch>(phoneGroup.getMatches())));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAddMatch:
                final EditText match = new EditText(this);
                match.setHint(getString(R.string.add_match_hint));
                new AlertDialog.Builder(this)
                        .setTitle("Phone Match")
                        .setMessage(getString(R.string.add_match_message))
                        .setView(match)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Editable value = match.getText();
                                String pattern = value.toString();
                                if (!TextUtils.isEmpty(pattern)) {
                                    PhoneMatch phoneMatch = new PhoneMatch(phoneGroup, pattern);
                                    DatabaseManager.getInstance().addPhoneMatch(phoneMatch);
                                }
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Do nothing.
                    }
                }).show();
                break;
            default:
                break;
        }
    }
}

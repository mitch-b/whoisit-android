package com.mitchbarry.android.whoisit.ui;

import static com.mitchbarry.android.whoisit.core.Constants.Extra.PHONE_GROUP;
import static com.mitchbarry.android.whoisit.core.Constants.FragmentTags.PHONE_MATCH_FRAGMENT;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import com.mitchbarry.android.whoisit.R;
import com.mitchbarry.android.whoisit.core.PhoneGroup;

import butterknife.InjectView;
import com.mitchbarry.android.whoisit.core.PhoneMatch;
import com.mitchbarry.android.whoisit.db.DatabaseManager;

public class PhoneGroupActivity extends BootstrapActivity implements View.OnClickListener {
    public static final String TAG = "PhoneGroupActivity";
    private final int RINGTONE_REQUEST_CODE = 1234;

    @InjectView(R.id.edit_name) protected EditText name;
    @InjectView(R.id.btnAddMatch) protected Button btnAddMatch;
    @InjectView(R.id.ringtone_button) protected Button btnRingtone;
    @InjectView(R.id.parentLayout) protected LinearLayout parentLayout;

    protected PhoneGroup phoneGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.phone_group_modify_view);

        if(getIntent() != null && getIntent().getExtras() != null) {
            phoneGroup = (PhoneGroup) getIntent().getExtras().getSerializable(PHONE_GROUP);
        }

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                adjustButtonEnable(s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                adjustButtonEnable(s.toString());
            }
        });
        btnAddMatch.setOnClickListener(this);
        btnRingtone.setOnClickListener(this);
        parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DatabaseManager.init(this);

        setupViews();
    }

    private void adjustButtonEnable(String input) {
        if (input.toString().length() > 0) {
            btnRingtone.setEnabled(true);
            btnAddMatch.setEnabled(true);
        } else {
            btnRingtone.setEnabled(false);
            btnAddMatch.setEnabled(false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        String newName = name.getText().toString();
        if (phoneGroup != null) {
            if (!TextUtils.isEmpty(newName)) {
                phoneGroup.setName(newName);
            }
            DatabaseManager.getInstance().updatePhoneGroup(phoneGroup);
        } else {
            validatePhoneGroup();
        }
    }

    /**
     * Will return true if phone group already exists
     * or if we were able to successfully create it.
     *
     * Will return false and display Toast to user if no name given.
     * @return boolean true or false depending on if PhoneGroup exists in SQLite
     */
    private boolean validatePhoneGroup() {
        if (phoneGroup != null)
            return true;

        String nameText = name.getText().toString();

        if (TextUtils.isEmpty(nameText)) {
            Toast.makeText(getApplicationContext(),
                    "You must enter a Phone Group name first!",
                    Toast.LENGTH_LONG
            ).show();
            return false;
        } else {
            return createNewPhoneGroup(nameText);
        }
    }
    private boolean createNewPhoneGroup(String name) {
        try {
            phoneGroup = new PhoneGroup(name);
            phoneGroup.setId(DatabaseManager.getInstance().addPhoneGroup(phoneGroup));
            phoneGroup.updateFromDB(this);
            removePhoneMatchFragment();
            addPhoneMatchFragment();
            return true;
        } catch (Exception e) {
            // this isn't great, but it'll do
            Log.e(TAG, "Failed while creating Phone Group", e);
            return false;
        }
    }

    private void setupViews() {
        if (phoneGroup != null) {
            name.setText(phoneGroup.getName());
            if (!TextUtils.isEmpty(phoneGroup.getRingtone()))
                btnRingtone.setText(RingtoneManager.getRingtone(this, Uri.parse(phoneGroup.getRingtone())).getTitle(this));
        } else {
            btnRingtone.setEnabled(false);
            btnAddMatch.setEnabled(false);
        }
        addPhoneMatchFragment();
    }

    private void addPhoneMatchFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        PhoneMatchListFragment listFragment = new PhoneMatchListFragment();
        Bundle args = new Bundle();
        args.putSerializable(PHONE_GROUP, phoneGroup);
        listFragment.setArguments(args);
        ft.replace(R.id.fragment_holder, listFragment, PHONE_MATCH_FRAGMENT);
        ft.commit();
    }
    private void removePhoneMatchFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.remove(fm.findFragmentByTag(PHONE_MATCH_FRAGMENT);
        ft.commit();
    }

    private void refreshPhoneMatchFragment() {
        PhoneMatchListFragment listFragment = (PhoneMatchListFragment)
                getSupportFragmentManager().findFragmentByTag(PHONE_MATCH_FRAGMENT);
        listFragment.forceRefresh();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAddMatch:
                final EditText match = new EditText(this);
                match.setHint(getString(R.string.add_match_hint));
                match.setInputType(InputType.TYPE_CLASS_PHONE);
                new AlertDialog.Builder(this)
                        .setTitle("Phone Match")
                        .setMessage(getString(R.string.add_match_message))
                        .setView(match)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Editable value = match.getText();
                                String pattern = value.toString();
                                if (!TextUtils.isEmpty(pattern)) {
                                    if (validatePhoneGroup()) {
                                        PhoneMatch phoneMatch = new PhoneMatch(phoneGroup, pattern);
                                        DatabaseManager.getInstance().addPhoneMatch(phoneMatch);
                                        DatabaseManager.getInstance().updatePhoneGroup(phoneGroup);
                                        refreshPhoneMatchFragment();
                                    }
                                }
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Do nothing.
                    }
                }).show();
                break;
            case R.id.ringtone_button:
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, getString(R.string.ringtone_select_message));
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, false);
                startActivityForResult(intent, RINGTONE_REQUEST_CODE);
                break;
            default:
                break;
        }
    }

    void setRingtone(Uri ringtoneUri) {
        if (validatePhoneGroup()) {
            phoneGroup.setRingtone(ringtoneUri.toString());
            DatabaseManager.init(this);
            DatabaseManager.getInstance().updatePhoneGroup(phoneGroup);
            btnRingtone.setText(RingtoneManager.getRingtone(this, ringtoneUri).getTitle(this));
        } else {
            btnRingtone.setText(getString(R.string.phone_group_ringtone_none));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RINGTONE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                    if (uri != null)
                        setRingtone(uri);
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

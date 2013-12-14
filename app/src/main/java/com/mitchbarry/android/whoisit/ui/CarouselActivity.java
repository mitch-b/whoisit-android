

package com.mitchbarry.android.whoisit.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;


import com.mitchbarry.android.whoisit.R;
import com.mitchbarry.android.whoisit.core.PhoneGroup;
import com.mitchbarry.android.whoisit.core.PhoneMatch;
import com.mitchbarry.android.whoisit.db.DatabaseManager;
import com.viewpagerindicator.TitlePageIndicator;

import butterknife.InjectView;
import butterknife.Views;


/**
 * Activity to view the carousel and view pager indicator with fragments.
 */
public class CarouselActivity extends BootstrapFragmentActivity {

    @InjectView(R.id.tpi_header) TitlePageIndicator indicator;
    @InjectView(R.id.vp_pages) ViewPager pager;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.carousel_view);

        // View injection with Butterknife
        Views.inject(this);

        // Set up navigation drawer
        mTitle = mDrawerTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                    /* Host activity */
                mDrawerLayout,           /* DrawerLayout object */
                R.drawable.ic_drawer,    /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,    /* "open drawer" description */
                R.string.drawer_close) { /* "close drawer" description */

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        initScreen();

        DatabaseManager.init(this);
        if (DatabaseManager.getInstance().getPhoneGroups().size() == 0) {
            PhoneGroup group = new PhoneGroup();
            group.setName("ConAgra");
            PhoneMatch match = new PhoneMatch(group, "402522.*", "content://media/internal/audio/media/85", null);
            DatabaseManager.getInstance().addPhoneGroup(group);
            DatabaseManager.getInstance().addPhoneMatch(match);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }



    private void initScreen() {
        pager.setAdapter(new BootstrapPagerAdapter(getResources(), getSupportFragmentManager()));

        indicator.setViewPager(pager);
        pager.setCurrentItem(1);

        setNavListeners();
    }

    private void setNavListeners() {
        findViewById(R.id.menu_item_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawers();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch(item.getItemId()) {
            case android.R.id.home:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

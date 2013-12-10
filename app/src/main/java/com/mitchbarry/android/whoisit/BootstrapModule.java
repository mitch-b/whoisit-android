package com.mitchbarry.android.whoisit;

import android.accounts.AccountManager;
import android.content.Context;

import com.mitchbarry.android.whoisit.authenticator.BootstrapAuthenticatorActivity;
import com.mitchbarry.android.whoisit.authenticator.LogoutService;
import com.mitchbarry.android.whoisit.core.CheckIn;
import com.mitchbarry.android.whoisit.core.TimerService;
import com.mitchbarry.android.whoisit.ui.BootstrapTimerActivity;
import com.mitchbarry.android.whoisit.ui.CarouselActivity;
import com.mitchbarry.android.whoisit.ui.CheckInsListFragment;
import com.mitchbarry.android.whoisit.ui.ItemListFragment;
import com.mitchbarry.android.whoisit.ui.NewsActivity;
import com.mitchbarry.android.whoisit.ui.NewsListFragment;
import com.mitchbarry.android.whoisit.ui.UserActivity;
import com.mitchbarry.android.whoisit.ui.UserListFragment;
import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger module for setting up provides statements.
 * Register all of your entry points below.
 */
@Module
(
        complete = false,

        injects = {
                BootstrapApplication.class,
                BootstrapAuthenticatorActivity.class,
                CarouselActivity.class,
                BootstrapTimerActivity.class,
                CheckInsListFragment.class,
                NewsActivity.class,
                NewsListFragment.class,
                UserActivity.class,
                UserListFragment.class,
                TimerService.class
        }

)
public class BootstrapModule  {

    @Singleton
    @Provides
    Bus provideOttoBus() {
        return new Bus();
    }

    @Provides
    @Singleton
    LogoutService provideLogoutService(final Context context, final AccountManager accountManager) {
        return new LogoutService(context, accountManager);
    }

}

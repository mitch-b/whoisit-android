package com.mitchbarry.android.whoisit;

import com.mitchbarry.android.whoisit.ui.CarouselActivity;
import com.mitchbarry.android.whoisit.ui.PhoneGroupActivity;
import com.mitchbarry.android.whoisit.ui.PhoneGroupListFragment;
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
                WhoIsItApplication.class,
                CarouselActivity.class,
                PhoneGroupActivity.class,
                PhoneGroupListFragment.class
        },

        library = true

)
public class WhoIsItModule {

    @Singleton
    @Provides
    Bus provideOttoBus() {
        return new Bus();
    }
}

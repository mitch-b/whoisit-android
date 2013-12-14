package com.mitchbarry.android.whoisit;

import dagger.Module;

/**
 * Add all the other modules to this one.
 */
@Module
(
    includes = {
            AndroidModule.class,
            WhoIsItModule.class
    }
)
public class RootModule {
}



package com.mitchbarry.android.whoisit.core.core;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import com.mitchbarry.android.whoisit.core.WhoIsItService;
import com.mitchbarry.android.whoisit.core.User;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Unit tests of client API
 */
public class BootstrapApiClientUtilTest {

    @Test
    @Ignore("Requires the API to use basic authentication. Parse.com api does not. See WhoIsItService for more info.")
    public void shouldCreateClient() throws Exception {
        List<User> users = new WhoIsItService("demo@androidbootstrap.com", "foobar").getUsers();

        assertThat(users.get(0).getUsername(), notNullValue());
    }
}

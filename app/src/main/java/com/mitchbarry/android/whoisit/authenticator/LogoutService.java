package com.mitchbarry.android.whoisit.authenticator;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.content.Context;
import com.mitchbarry.android.whoisit.core.Constants;
import com.mitchbarry.android.whoisit.util.Ln;
import com.mitchbarry.android.whoisit.util.SafeAsyncTask;

import javax.inject.Inject;


/**
 * Class used for logging a user out.
 */
public class LogoutService {

    protected Context context;
    protected AccountManager accountManager;

    @Inject
    public LogoutService(Context context, AccountManager accountManager) {
        this.context = context;
        this.accountManager = accountManager;
    }

    public void logout(final Runnable onSuccess) {

        new LogoutTask(context, onSuccess).execute();
    }

    private static class LogoutTask extends SafeAsyncTask<Boolean> {

        private final Context context;
        private Runnable onSuccess;

        protected LogoutTask(Context context, Runnable onSuccess) {
            this.context = context;
            this.onSuccess = onSuccess;
        }

        @Override
        public Boolean call() throws Exception {

            final Account[] accounts = AccountManager.get(context).getAccountsByType(Constants.Auth.BOOTSTRAP_ACCOUNT_TYPE);
            if(accounts.length > 0) {
                AccountManagerFuture<Boolean> removeAccountFuture = AccountManager.get(context).removeAccount
                        (accounts[0], null, null);
                if(removeAccountFuture.getResult() == true) {
                    return true;
                } else {
                    return false;
                }
            }

            return false;
        }

        @Override
        protected void onSuccess(Boolean accountWasRemoved) throws Exception {
            super.onSuccess(accountWasRemoved);

            Ln.d("Logout succeeded: %s", accountWasRemoved);
            onSuccess.run();

        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
            Ln.e(e.getCause(), "Logout failed.");
        }
    }
}

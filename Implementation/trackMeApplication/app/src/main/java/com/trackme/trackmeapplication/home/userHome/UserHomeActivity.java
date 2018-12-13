package com.trackme.trackmeapplication.home.userHome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.Toolbar;

import com.trackme.trackmeapplication.R;
import com.trackme.trackmeapplication.account.login.UserLoginActivity;
import com.trackme.trackmeapplication.account.register.UserProfileActivity;
import com.trackme.trackmeapplication.baseUtility.BaseDelegationActivity;

import butterknife.BindView;

public class UserHomeActivity extends BaseDelegationActivity<
        UserHomeContract.UserHomeView,
        UserHomePresenter,
        UserHomeDelegate> implements UserHomeContract.UserHomeView {

    @BindView(R.id.toolbar) protected Toolbar toolbar;
    @BindView(R.id.tab_layout)protected TabLayout tabLayout;
    private SharedPreferences sp;
    private String username;

    @NonNull
    @Override
    protected UserHomePresenter getPresenterInstance() {
        return new UserHomePresenter();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_user_home);
        setSupportActionBar(toolbar);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        sp = getSharedPreferences("login", MODE_PRIVATE);
        username = sp.getString("username", null);

        super.onCreate(savedInstanceState);

        mDelegate.configureToolbar();
    }

    @Override
    protected UserHomeDelegate instantiateDelegateInstance() {
        return new UserHomeDelegate();
    }

    @Override
    public void onBackPressed() {
        if (!mDelegate.closeDrawer()){
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        username = sp.getString("username", null);
        super.onResume();
    }

    @Override
    public void navigateToUserProfile() {
        Intent intent = new Intent(this, UserProfileActivity.class);
        startActivity(intent);
    }

    @Override
    public void navigateToUserSettings() {
        Intent intent = new Intent(this, UserSettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public void navigateToUserLogin() {
        Intent intent = new Intent(this, UserLoginActivity.class);
        /*TODO*/
        sp.edit().putBoolean("user_logged", false).apply();
        startActivity(intent);
        finish();
    }

    @Override
    public UserHomeActivity getActivity() {
        return this;
    }

    @Override
    public String getUsername() {
        return username;
    }

}

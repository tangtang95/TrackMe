package com.trackme.trackmeapplication.home.businessHome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.Toolbar;

import com.trackme.trackmeapplication.R;
import com.trackme.trackmeapplication.account.login.UserLoginActivity;
import com.trackme.trackmeapplication.account.network.AccountNetworkImp;
import com.trackme.trackmeapplication.account.network.AccountNetworkInterface;
import com.trackme.trackmeapplication.baseUtility.BaseDelegationActivity;
import com.trackme.trackmeapplication.baseUtility.Constant;

import butterknife.BindView;

/**
 * Business Home class. The main class for the business user with its menu for creating and managing
 * request data.
 *
 * @author Mattia Tibaldi
 * @see BaseDelegationActivity
 */
public class BusinessHomeActivity extends BaseDelegationActivity<
        BusinessHomeContract.BusinessHomeView,
        BusinessHomePresenter,
        BusinessHomeDelegate> implements BusinessHomeContract.BusinessHomeView {

    @BindView(R.id.toolbar) protected Toolbar toolbar;
    @BindView(R.id.tab_layout)protected TabLayout tabLayout;

    private SharedPreferences sp;
    private String mail;

    @NonNull
    @Override
    protected BusinessHomePresenter getPresenterInstance() {
        return new BusinessHomePresenter();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_business_home);
        setSupportActionBar(toolbar);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        sp = getSharedPreferences(Constant.LOGIN_SHARED_DATA_NAME, MODE_PRIVATE);
        mail = sp.getString(Constant.SD_EMAIL_DATA_KEY, null);

        super.onCreate(savedInstanceState);

        mDelegate.configureToolbar();
    }

    @Override
    protected BusinessHomeDelegate instantiateDelegateInstance() {
        return new BusinessHomeDelegate();
    }

    @Override
    public void onBackPressed() {
        if (!mDelegate.closeDrawer()){
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        mail = sp.getString(Constant.SD_EMAIL_DATA_KEY, null);
        super.onResume();
    }

    @Override
    public void navigateToBusinessProfile() {
        /*TODO*/
    }

    @Override
    public void navigateToBusinessSettings() {
        /*TODO*/
    }

    @Override
    public void navigateToUserLogin() {
        Intent intent = new Intent(this, UserLoginActivity.class);
        AccountNetworkInterface accountNetwork = AccountNetworkImp.getInstance();
        accountNetwork.thirdPartyLogout(mail);
        sp.edit().putBoolean(Constant.BUSINESS_LOGGED_BOOLEAN_VALUE_KEY, false).apply();
        startActivity(intent);
        finish();
    }

    @Override
    public BusinessHomeActivity getActivity() {
        return this;
    }

    @Override
    public String getMail() {
        return mail;
    }
}

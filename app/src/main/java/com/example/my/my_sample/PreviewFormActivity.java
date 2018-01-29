package com.example.my.my_sample;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

public class PreviewFormActivity extends SingleFragmentActivity {

    public static final String EXTRA_EMAIL = "com.example.transcend.sample.email";
    public static final String EXTRA_PHONE = "com.example.transcend.sample.phone";
    public static final String EXTRA_PASSWORD = "com.example.transcend.sample.password";
    public static final String EXTRA_PHOTOPATH = "com.example.transcend.sample.photopath";

    private static final String TAG = "INFO_PREVIEW_ACTIVITY";

    public static Intent newIntent(Context packageContext, String email, String phone, String password, String photoPath){
        Intent intent  = new Intent(packageContext,PreviewFormActivity.class);
        intent.putExtra(EXTRA_EMAIL,email);
        intent.putExtra(EXTRA_PHONE,phone);
        intent.putExtra(EXTRA_PASSWORD,password);
        intent.putExtra(EXTRA_PHOTOPATH,photoPath);
        return  intent;
    }

    @Override
    protected Fragment createFragment(){

        String email =  getIntent().getStringExtra(EXTRA_EMAIL);
        String phone =  getIntent().getStringExtra(EXTRA_PHONE);
        String password =  getIntent().getStringExtra(EXTRA_PASSWORD);
        String photopath = getIntent().getStringExtra(EXTRA_PHOTOPATH);
        return PreviewFormFragment.newInstance(email,phone,password,photopath);
    }
}

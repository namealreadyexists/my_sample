package com.example.my.my_sample;

import android.support.v4.app.Fragment;

public class InputFormActivity extends  SingleFragmentActivity {

    @Override
    protected Fragment createFragment(){
        return new InputFormFragment();
    }
}

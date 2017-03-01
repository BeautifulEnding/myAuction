package com.example.administrator.study.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.study.R;

/**
 * Created by Administrator on 2016/12/1 0001.
 */

public class MessageFragment extends Fragment{
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.messagefragmen_layout, container, false);
    }
}

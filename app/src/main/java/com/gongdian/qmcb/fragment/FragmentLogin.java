
package com.gongdian.qmcb.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.gongdian.qmcb.R;
import com.gongdian.qmcb.activity.RegisterActivity;
import com.gongdian.qmcb.utils.Constant;


public class FragmentLogin extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.no_login, null);
        ImageView imageView = (ImageView)rootView.findViewById(R.id.head);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), RegisterActivity.class);
                getActivity().startActivityForResult(intent, Constant.LoginResultCode);
            }
        });

        return rootView;
    }


}


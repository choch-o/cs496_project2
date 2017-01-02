package com.example.q.project2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by q on 2016-12-30.
 */

public class TabCFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.tab3, container, false);
        Button alarmBtn = (Button) rootView.findViewById(R.id.call_alarm_btn);
        alarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), AlarmActivity.class);
                getActivity().startActivity(i);
            }
        });
        return rootView;
    }
}

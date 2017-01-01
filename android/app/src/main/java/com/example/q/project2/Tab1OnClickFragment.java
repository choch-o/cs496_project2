package com.example.q.project2;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by q on 2016-12-26.
 */

public class Tab1OnClickFragment extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceStates) {
        View view = inflater.inflate(R.layout.tab1_onclick, container);
        TextView name = (TextView) view.findViewById(R.id.tab1_onclick_name);
        TextView number = (TextView) view.findViewById(R.id.tab1_onclick_number);
        final Bundle params = getArguments();

        name.setText(params.getString("name"));
        number.setText(params.getString("number"));

        LinearLayout phone_num = (LinearLayout) view.findViewById(R.id.tab1_phone_num);
        phone_num.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + params.getString("number")));
                getActivity().startActivity(i);
            }
        });

        return view;
    }

}

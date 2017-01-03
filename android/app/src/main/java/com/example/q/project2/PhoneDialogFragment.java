package com.example.q.project2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by q on 2017-01-03.
 */

public class PhoneDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {
    private EditText enterPhone;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        enterPhone = new EditText(getActivity());
        enterPhone.setInputType(InputType.TYPE_CLASS_PHONE);
        return new AlertDialog.Builder(getActivity())
                .setTitle("Phone Number")
                .setMessage("Enter your phone number:")
                .setPositiveButton("SAVE", this)
                .setNegativeButton("CANCEL", null)
                .setView(enterPhone)
                .create();

        /*
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_enter_phone, null);
        enterPhone = (EditText) view.findViewById(R.id.enterPhone);

        Log.d("IN DIALOG", "INININININ");
        builder.setView(view)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String phoneNumber = enterPhone.getText().toString();
                        mListener.onDialogPositiveClick(PhoneDialogFragment.this, phoneNumber);
                    }
                });

        return builder.create();
        */
    }

    @Override
    public void onClick(DialogInterface dialog, int position) {
        String phoneNumber = enterPhone.getText().toString();
         MainActivity callingActivity = (MainActivity) getActivity();
         callingActivity.onUserEnterPhone(phoneNumber);
//        TabCFragment.onUserEnterPhone(phoneNumber);
        dialog.dismiss();
    }
}

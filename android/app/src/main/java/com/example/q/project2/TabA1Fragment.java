package com.example.q.project2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

// Instances of this class are fragments representing a single
// object in our collection.
public class TabA1Fragment extends Fragment {
    private JSONArray contact_list;
    private AccessToken accessToken =  AccessToken.getCurrentAccessToken();
    private ArrayList<String> str_contact_list;
    private ArrayAdapter<String> adapter;
    private ListView list_view;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab1_phone, container, false);

        list_view = (ListView) view.findViewById(R.id.tab1);

        showContacts();

        return view;
    }

    private JSONArray getContactList() {
        JSONArray contact_list = new JSONArray();

        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = new String[] {
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };
        String sort_order = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;

        Cursor cursor_contacts = getActivity().getContentResolver().query(uri, projection, null, null, sort_order);

        if (cursor_contacts.getCount() == 0) {
            cursor_contacts.close();
            return null;
        } else {
            try {
                cursor_contacts.moveToFirst();
                do {
                    JSONObject jobject = new JSONObject();
                    String name_str = cursor_contacts.getString(cursor_contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phone_num_str = cursor_contacts.getString(cursor_contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    jobject.put("name", name_str);
                    jobject.put("phone_number", phone_num_str);
                    contact_list.put(jobject);
                } while (cursor_contacts.moveToNext());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        uploadContacts(contact_list);
        return contact_list;
    }

    public ArrayList<String> parse_JSONArray(JSONArray jarray) {
        ArrayList<String> str_list = new ArrayList<String>();

        for (int i = 0; i < jarray.length(); i++) {
            try {
                JSONObject jobject = jarray.getJSONObject(i);
                String str = jobject.getString("name");
                str_list.add(str);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return str_list;
    }

    private void showContacts() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            contact_list = getContactList();
            str_contact_list = parse_JSONArray(contact_list);
            adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, str_contact_list);
            list_view.setAdapter(adapter);
            list_view.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Tab1OnClickFragment dialog_fragment = new Tab1OnClickFragment();
                    Bundle args = new Bundle();
                    try {
                        JSONObject item = contact_list.getJSONObject(position);
                        args.putString("name", item.getString("name"));
                        args.putString("number", item.getString("phone_number"));
                        dialog_fragment.setArguments(args);
                        dialog_fragment.show(getActivity().getSupportFragmentManager(), "name and phone number");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                showContacts();
            } else {
                Toast.makeText(getActivity(), "Until you grant the permission, we cannot display the contacts", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public class Contact {
        String phonenum;
        String name;

        public Contact() {}

        public Contact(String phonenum, String name) {
            this.phonenum = phonenum;
            this.name = name;
        }

        public String getPhonenum() {
            return phonenum;
        }
        public void setPhonenum(String phonenum) {
            this.phonenum = phonenum;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
    }

    public void uploadContacts(JSONArray contacts) {
        try {
            for (int i = 0; i < contacts.length(); i++) {
                JSONObject object = contacts.getJSONObject(i);
                object.put("is_facebook", false);
                object.put("last_update", new java.util.Date());
                object.put("user", accessToken.getUserId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String url = "http://52.78.52.132:3000" + "/upload_contacts";
        OkHttpHandler handler = new OkHttpHandler();
        String result = null;
        try {
            result = handler.execute(url, contacts.toString()).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}

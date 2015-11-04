package com.smartinnovationtechnology.ishopping;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.github.mrengineer13.snackbar.SnackBar;

/**
 * Created by Shamyyoun on 7/22/2015.
 */
public class ContactFragment extends Fragment {
    private Activity activity;
    private EditText textName;
    private EditText textEmail;
    private EditText textMessage;
    private Button buttonSend;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_contact, container, false);
        initComponents(rootView);

        return rootView;
    }

    /**
     * method, used to initialize components
     */
    private void initComponents(ViewGroup rootView) {
        activity = getActivity();
        textName = (EditText) rootView.findViewById(R.id.text_name);
        textEmail = (EditText) rootView.findViewById(R.id.text_email);
        textMessage = (EditText) rootView.findViewById(R.id.text_message);
        buttonSend = (Button) rootView.findViewById(R.id.button_send);

        // add listeners
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // validate inputs
                if (textName.getText().toString().trim().isEmpty()) {
                    textName.setError(getString(R.string.required));
                    return;
                }
                if (textEmail.getText().toString().trim().isEmpty()) {
                    textEmail.setError(getString(R.string.required));
                    return;
                }
                if (textMessage.getText().toString().trim().isEmpty()) {
                    textMessage.setError(getString(R.string.required));
                    return;
                }

                // show success msg
                new SnackBar.Builder(activity)
                        .withMessageId(R.string.message_sent_successfully)
                        .withDuration(SnackBar.MED_SNACK)
                        .show();

                // remove inputs
                textName.setText("");
                textEmail.setText("");
                textMessage.setText("");
            }
        });
    }
}

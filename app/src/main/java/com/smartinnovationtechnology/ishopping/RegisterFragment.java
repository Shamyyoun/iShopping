package com.smartinnovationtechnology.ishopping;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.github.mrengineer13.snackbar.SnackBar;

/**
 * Created by Shamyyoun on 7/22/2015.
 */
public class RegisterFragment extends Fragment {
    private MainActivity activity;
    private EditText textUsername;
    private EditText textEmail;
    private EditText textPassword;
    private Button buttonRegister;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_register, container, false);
        initComponents(rootView);

        return rootView;
    }

    /**
     * method, used to initialize components
     */
    private void initComponents(ViewGroup rootView) {
        activity = (MainActivity) getActivity();
        textUsername = (EditText) rootView.findViewById(R.id.text_username);
        textEmail = (EditText) rootView.findViewById(R.id.text_email);
        textPassword = (EditText) rootView.findViewById(R.id.text_password);
        buttonRegister = (Button) rootView.findViewById(R.id.button_register);

        // add listeners
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // validate inputs
                if (textUsername.getText().toString().trim().isEmpty()) {
                    textUsername.setError(getString(R.string.required));
                    return;
                }
                if (textEmail.getText().toString().trim().isEmpty()) {
                    textEmail.setError(getString(R.string.required));
                    return;
                }
                if (textPassword.getText().toString().trim().isEmpty()) {
                    textPassword.setError(getString(R.string.required));
                    return;
                }

                // show success msg
                new SnackBar.Builder(activity)
                        .withMessageId(R.string.registered_successfully)
                        .withDuration(SnackBar.MED_SNACK)
                        .show();

                // goto main fragment
                FragmentManager fm = activity.getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.container_main, new HomeFragment());
                ft.commit();
                activity.setSelectedMenuItemId(R.id.view_home);
            }
        });
    }
}

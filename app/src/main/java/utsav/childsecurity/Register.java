package utsav.childsecurity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class Register extends Activity {

    EditText name;
    EditText email;
    EditText phone;
    EditText password;
    Button register;
    ParseUser user;
    Integer i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        phone = (EditText) findViewById(R.id.phone);
        password = (EditText) findViewById(R.id.password);

        register = (Button) findViewById(R.id.register);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().equals("") || email.getText().toString().equals("") || phone.getText().toString().equals("") || password.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Some fields are left blank...", Toast.LENGTH_LONG).show();
                } else
                    NetAsync(v);
            }
        });
    }

    private class NetCheck extends AsyncTask<String, String, Boolean> {
        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            nDialog = new ProgressDialog(Register.this);
            nDialog.setMessage("Loading..");
            nDialog.setTitle("Checking Network");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... args) {
            ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
            return cd.isConnectingToInternet();
        }

        @Override
        protected void onPostExecute(Boolean th) {
            if (th) {
                nDialog.dismiss();
                new ProcessRegister().execute();
            } else {
                nDialog.dismiss();
                new AlertDialog.Builder(Register.this)
                        .setTitle("Error")
                        .setMessage("You are not connected to internet")
                        .setNegativeButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        }
    }


    private class ProcessRegister extends AsyncTask<String, String, Integer> {

        /**
         * Defining Process dialog
         */
        private ProgressDialog pDialog;

        String name_, emailid, mobile, upassword;


        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            name_ = name.getText().toString();
            emailid = email.getText().toString();
            mobile = phone.getText().toString();
            upassword = password.getText().toString();

            pDialog = new ProgressDialog(Register.this);
            pDialog.setTitle("Contacting Servers");
            pDialog.setMessage("Registering ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected Integer doInBackground(String... args) {

            user = new ParseUser();
            user.setUsername(emailid);
            user.setEmail(emailid);
            user.setPassword(upassword);
            user.put("Name", name_);
            user.put("Mobile", mobile);
            try {
                user.signUp();
                i = 0;
            } catch (ParseException e) {
                i = e.getCode();
                e.printStackTrace();
            }
            return i;
        }

        @Override
        protected void onPostExecute(Integer i) {
            pDialog.dismiss();
            if (i == 0) {
                user = ParseUser.getCurrentUser();
                Boolean bool = user.getBoolean("emailVerified");
                if (bool) {
                    startActivity(new Intent(getApplicationContext(), LoggedIn.class));
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Please verify your email before logging in.", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Error code: " + i, Toast.LENGTH_LONG).show();
            }
        }
    }

    public void NetAsync(View view) {
        new NetCheck().execute();
    }
}

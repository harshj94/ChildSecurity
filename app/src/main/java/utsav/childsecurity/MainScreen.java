package utsav.childsecurity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainScreen extends AppCompatActivity {

    EditText email, password;
    Button login, forget, register;
    ParseUser user;
    Integer i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
        forget = (Button) findViewById(R.id.forget);
        register = (Button) findViewById(R.id.register);

        user = new ParseUser();
        user = ParseUser.getCurrentUser();

        new NetCheck1().execute();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetAsync(v);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Register.class));
                finish();
            }
        });

        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ForgetPassword.class));
                finish();
            }
        });
    }

    private class NetCheck extends AsyncTask<String, String, Boolean> {
        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            nDialog = new ProgressDialog(MainScreen.this);
            nDialog.setTitle("Checking Network");
            nDialog.setMessage("Loading...");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();
        }

        /**
         * Gets current device state and checks for working internet connection by trying Google.
         */
        @Override
        protected Boolean doInBackground(String... args) {

            ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
            return cd.isConnectingToInternet();

        }

        @Override
        protected void onPostExecute(Boolean th) {

            if (th) {
                nDialog.dismiss();
                new ProcessLogin().execute();
            } else {
                nDialog.dismiss();
                new AlertDialog.Builder(MainScreen.this)
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

    private class NetCheck1 extends AsyncTask<String, String, Boolean> {
        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            nDialog = new ProgressDialog(MainScreen.this);
            nDialog.setTitle("Checking Network");
            nDialog.setMessage("Loading...");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();
        }

        /**
         * Gets current device state and checks for working internet connection by trying Google.
         */
        @Override
        protected Boolean doInBackground(String... args) {
            ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
            return cd.isConnectingToInternet();

        }

        @Override
        protected void onPostExecute(Boolean th) {

            if (th) {
                nDialog.dismiss();
                new ProcessLogin1().execute();
            } else {
                nDialog.dismiss();
                new AlertDialog.Builder(MainScreen.this)
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

    private class ProcessLogin extends AsyncTask<String, String, Integer> {

        private ProgressDialog pDialog;

        String emailid, userpassword;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            emailid = email.getText().toString();
            userpassword = password.getText().toString();
            pDialog = new ProgressDialog(MainScreen.this);
            pDialog.setTitle("Contacting Servers");
            pDialog.setMessage("Logging in ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected Integer doInBackground(String... args) {

            try {
                ParseUser.logIn(emailid, userpassword);
                i = 0;
            } catch (ParseException e) {
                e.printStackTrace();
                i = e.getCode();
            }
            return i;
        }

        @Override
        protected void onPostExecute(Integer i) {
            pDialog.dismiss();
            if (i == 0) {
                user = ParseUser.getCurrentUser();
                if (user != null) {
                    Boolean bool = user.getBoolean("emailVerified");
                    if (bool) {
                        startActivity(new Intent(getApplicationContext(), LoggedIn.class));
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Please verify your email before logging in.", Toast.LENGTH_LONG).show();
                    }
                }
            } else {
                Toast.makeText(getApplicationContext(), "Error code: " + i, Toast.LENGTH_LONG).show();
            }
        }
    }

    private class ProcessLogin1 extends AsyncTask<String, String, Integer> {

        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainScreen.this);
            pDialog.setTitle("Contacting Servers");
            pDialog.setMessage("Logging in ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected Integer doInBackground(String... args) {
            Boolean bool = null;
            if (user != null) {
                try {
                    user = user.fetch();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (user.getObjectId() != null) {
                    bool = user.getBoolean("emailVerified");
                }
            }
            if (bool == null) {
                return 0;
            } else if (bool) {
                return 1;
            } else {
                return 2;
            }
        }

        @Override
        protected void onPostExecute(Integer i) {
            pDialog.dismiss();
            if (i == 1) {
                startActivity(new Intent(getApplicationContext(), LoggedIn.class));
                finish();
            } else if (i == 2) {
                Toast.makeText(getApplicationContext(), "Please verify your email before logging in.", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void NetAsync(View view) {
        new NetCheck().execute();
    }
}

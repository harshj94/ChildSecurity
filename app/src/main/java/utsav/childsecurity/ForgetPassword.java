package utsav.childsecurity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseUser;

public class ForgetPassword extends AppCompatActivity {

    EditText email;
    Button forget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgotpassword);

        email = (EditText) findViewById(R.id.email);
        forget = (Button) findViewById(R.id.forget);

        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ParseUser.requestPasswordReset(email.getText().toString());
                    startActivity(new Intent(getApplicationContext(), MainScreen.class));
                    finish();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

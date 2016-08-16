package com.example.sujit.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    Button button, buttonLogin;
    AppCompatButton buttonConfirm;
    TextView linkSignup,linkForgotPassword;
    EditText editTextPhone,editTextPassword;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        session = new SessionManager(getApplicationContext());
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        linkSignup=(TextView)findViewById(R.id.linkSignup);
        linkForgotPassword=(TextView)findViewById(R.id.linkForgotPassword);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
        linkSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, Register.class));
            }
        });

        linkForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forgotPassword();
            }
        });

        button=(Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, MainActivity.class));
            }
        });
    }

    private void login() {

               //final String phoneNo=editTextPhone.getText().toString().trim();
               //final String password=editTextPassword.getText().toString().trim();

        RequestQueue requestQueue = Volley.newRequestQueue(Login.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://192.168.0.108/loginsuccess.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //if the server response is success
                        if(response.contains("success")){
                            Log.d("LoginResponse", response);
                          //  storeUserData(phoneNo,password);

                            session.setLogin(true);
                            startActivity(new Intent(Login.this, MainActivity.class));
                            finish();
                        }
                       // startActivity(new Intent(Login.this, Login.class));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(Login.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        requestQueue.add(stringRequest);
    }
/*
    private void storeUserData(String phoneNo, String password) {
        SharedPreferences sharedPref=getSharedPreferences("MyPref",MODE_PRIVATE);
        SharedPreferences.Editor spEditor= sharedPref.edit();
        spEditor.putString("phone",phoneNo);
        spEditor.putString("password",password);
        spEditor.apply();
    }
*/
    private void forgotPassword() {

        LayoutInflater li = LayoutInflater.from(this);
        //Creating a view to get the dialog box
        View confirmDialog = li.inflate(R.layout.dialog_forgotpass, null);

        //Initizliaing confirm button fo dialog box and edit text of dialog box
        buttonConfirm = (AppCompatButton) confirmDialog.findViewById(R.id.buttonConfirm);
        editTextPhone = (EditText) confirmDialog.findViewById(R.id.editTextPhone);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        //Adding our dialog box to the view of alert dialog
        alert.setView(confirmDialog);

        //Creating an alert dialog
        final AlertDialog alertDialog = alert.create();

        //Displaying the alert dialog
        alertDialog.show();

        //On the click of the confirm button from alert dialog
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Hiding the alert dialog
                alertDialog.dismiss();
                //Displaying a progressbar
                //pDialog.show();

                //Getting the user entered otp from edittext
                final String number = editTextPhone.getText().toString().trim();
                RequestQueue requestQueue = Volley.newRequestQueue(Login.this);
                // String confirmUrl = "http://e4729191.ngrok.io/registeracustomer";
                //Creating an string request
                StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://3b9d70af.ngrok.io/forgotpassword",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                //if the server response is success
                                    Toast.makeText(Login.this, response, Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(Login.this, Login.class));
                                }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                alertDialog.dismiss();
                                Toast.makeText(Login.this, error.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        //Adding the parameters otp and username
                        params.put("mobile_number", number);
                        return params;
                    }
                };

                //Adding the request to the queue
                requestQueue.add(stringRequest);
            }
        });
    }
}

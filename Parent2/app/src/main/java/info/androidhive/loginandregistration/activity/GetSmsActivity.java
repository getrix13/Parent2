package info.androidhive.loginandregistration.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import info.androidhive.loginandregistration.R;
import info.androidhive.loginandregistration.app.AppConfig;
import info.androidhive.loginandregistration.app.AppController;
import info.androidhive.loginandregistration.helper.SessionManager;

import static info.androidhive.loginandregistration.activity.MainActivity.EXTRA_EMAIL;
import static info.androidhive.loginandregistration.activity.MainActivity.EXTRA_PASS;


public class GetSmsActivity extends Activity {




    StringBuffer strBuf=new StringBuffer();
    TextView tex;
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private ProgressDialog pDialog;


    private SessionManager session;

    String email;
    String password;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infos);



        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);


        // session manager
        session = new SessionManager(getApplicationContext());

        tex = (TextView) findViewById(R.id.textView);
        tex.setMovementMethod(new ScrollingMovementMethod());

        Intent intent=getIntent();
        password = intent.getStringExtra(EXTRA_PASS);
        email = intent.getStringExtra(EXTRA_EMAIL);


        if (!email.isEmpty() && !password.isEmpty() ) {
            // login user
            checkLogin(email.trim(), password.trim());
        } else {
            // Prompt user to enter credentials
            Toast.makeText(getApplicationContext(),
                    "Please enter the credentials!", Toast.LENGTH_LONG)
                    .show();
        }



    }





    //fonction importee
    private void checkLogin(final String email, final String password) {
        this.email = email;
        this.password = password;
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Logging in ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_GETSMS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {

                        // user successfully logged in
                        // Create login session
                        session.setLogin(true);



                        JSONObject user = jObj.getJSONObject("user");


                        String key;
                        String value;

                        /*
                        for(int i = 0; i<sms.names().length(); i++){
                            Log.v(TAG, "key = " + sms.names().getString(i) + " value = " + sms.get(sms.names().getString(i)));
                        }
                        */

                        String s = user.getString("sms");

                    /*

                        String numero = sms.getString("numero");
                        String date = sms.getString("date1");
                        String type = sms.getString("type");
                        String message = sms.getString("message");


                        strBuf.append("\nContact : "+numero+" "+"\ndate : "+date+"\nmessagef : "+message);
                        strBuf.append("\n-----------------------");


                        sb.append("\nNumero de tel : " +numero +
                                "\nType SMS : " + type +
                                "\nDate SMS : " + date +
                                "\nmessage : " + message);
                        sb.append("\n-----------------------------------------------");
                     */
                        tex.setText(s);




                        ///finit
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);


                return params;
            }


        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }



    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }


    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}







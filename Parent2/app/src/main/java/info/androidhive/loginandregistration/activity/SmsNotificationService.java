package info.androidhive.loginandregistration.activity;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import info.androidhive.loginandregistration.app.AppConfig;
import info.androidhive.loginandregistration.app.AppController;
import info.androidhive.loginandregistration.helper.SessionManager;

public class SmsNotificationService extends Service {


    private static String TAG = "Service";

    String sms;
    String email;
    String password;
    private ProgressDialog pDialog;
    private SessionManager session;
    String numero;
    String date;
    String message;


    private CountDownTimer timer;
    private int cmpt = 0;
    private int act = 0;
    boolean b = false;


    public SmsNotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        super.onStart(intent, startId);
        Log.d(TAG, "AppelsDetails started");


        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);


        // Session manager
        session = new SessionManager(getApplicationContext());


        SharedPreferences prefs = getSharedPreferences("MyApp", MODE_PRIVATE);
        email = prefs.getString("email", "UNKNOWN");
        password = prefs.getString("password", "UNKNOWN2");


        Timer timer = new Timer();
        TimerTask t = new TimerTask() {
            int sec = 0;

            @Override
            public void run() {


                checkLogin(email.trim(), password.trim());
            }
        };
        timer.scheduleAtFixedRate(t, 10000, 10000);


    }








    private void checkLogin(final String email, final String password) {
        this.email = email;
        this.password = password;
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Logging in ...");


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_GETSMS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());

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




                        String res;
                        //String s = "eee&bbbb";
                        String s = user.getString("sms");
                        String[] result = s.split("&");


                        cmpt=0;
                        for(int x = 0; x<result.length;x++)

                        {
                            res = result[x];
                            cmpt++;
                        }

                            System.out.println("-----------+"+cmpt);


                            if ((act < cmpt) && (b == false)) {
                                act = cmpt;


                            } else if ((act < cmpt) && (b == true)) {
                                System.out.println("-----------+"+cmpt);
                                System.out.println("-----------+"+act);
                                act = cmpt;

                                //envNotification();
                                System.out.println("----------------------------Base de donnée a changé !-----------------------");
                            }
                            b=true;




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






}



















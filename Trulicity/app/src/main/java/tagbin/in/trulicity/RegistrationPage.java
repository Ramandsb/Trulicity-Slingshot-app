package tagbin.in.trulicity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationPage extends AppCompatActivity {

    public static String id;
    public  static String  url="http://192.168.0.101/trulicity-app/webapp/action.php";
    EditText name, email,number;
    String myname,myemail,mynumber;
    boolean ret =false;
    public static String regResponse;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    String MobilePattern = "[0-9]{10}";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_activity);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        name= (EditText) findViewById(R.id.name);
        email= (EditText) findViewById(R.id.email);
        number= (EditText) findViewById(R.id.phoneNumber);
//        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
//        wl.acquire();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        name.setHintTextColor(getResources().getColor(R.color.white));
        email.setHintTextColor(getResources().getColor(R.color.white));
        number.setHintTextColor(getResources().getColor(R.color.white));
//        Log.d("url",R.string.serverUrl)

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               if(myshow()) {
                   mynumber= number.getText().toString();
                   makeRequest(myname, myemail, mynumber);




               }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_registration_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public boolean myshow() {


        if (name.getText().toString().equals("")) {
            name.setError("Empty Field");
            return false;

        } else {
            myname = name.getText().toString();

        }

        if (email.getText().toString().equals("")) {
            email.setError("Empty field");
            return false;

        }

        else {
            myemail = email.getText().toString();
           ret= isValidMail(myemail);

        }








        return ret;
    }




    private boolean isValidMail(String eemail) {
        boolean check;
        Pattern p;
        Matcher m;

        String EMAIL_STRING = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        p = Pattern.compile(EMAIL_STRING);

        m = p.matcher(eemail);
        check = m.matches();

        if (!check) {
            email.setError("Email Invalid");

        }
        return check;
    }

    private boolean isValidMobile(String mobile) {
        boolean check;
        if (mobile.length() < 10 || mobile.length() > 13) {
            check = false;
            number.setError("Mobile Number Invalid");
//            Toast.makeText(getApplicationContext(), "Please enter valid 10 digit phone number", Toast.LENGTH_SHORT).show();
        } else {
            check = true;
        }
        return check;
    }




    private void makeRequest(final String name,final String email,final String num) {


        RequestQueue requestQueue = Volley.newRequestQueue(RegistrationPage.this);

        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            id=     response.getString("id");
                            if(response.getString("response").equals("1")) {
                                Intent i = new Intent(RegistrationPage.this, LetsClickSelfiePage.class);
                                startActivity(i);
                            }
                            else Toast.makeText(RegistrationPage.this,"Network Error",Toast.LENGTH_LONG);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        Log.v("writtem:%n %s", response.toString());


                        Toast.makeText(RegistrationPage.this, response.toString(), Toast.LENGTH_LONG);


                    }

                },

                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v("error:%n %s", error.toString());
                        Toast.makeText(RegistrationPage.this, error.toString(), Toast.LENGTH_LONG);


                    }
                }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("action","_REGISTRATION");
                params.put("name", name);
                params.put("email", email);
                params.put("mobile", num);

                return params;
            }


        };
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsObjRequest);
//        requestQueue.cancelAll(tag);

    }

}


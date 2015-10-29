package tagbin.in.trulicity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
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

public class DisclaimerActivity extends AppCompatActivity {

    String  url="http://192.168.0.101/trulicity-app/webapp/action.php";
    FloatingActionButton fab;
    TextView yourTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disclaimer);
//        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
//        wl.acquire();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        CheckBox cb= (CheckBox) findViewById(R.id.iagree);
        yourTextView= (TextView) findViewById(R.id.textView7);
        yourTextView.setMovementMethod(new ScrollingMovementMethod());
        yourTextView.setText("The content & the rights to this Application together with the rights of underlying software code for this application is owned by Lilly & licensed by third party.All logos, names, designs & marks contained herein are trademarks owned or used under license by Lilly.\n" +
                        "By signing this form, I hereby give permission to Lilly to use my name, email id & personal image in whatever medium deemed appropriate by Lilly for any of the following purpose:\n"+"(i) public relations\n"+"(ii) training & education\n"+"(iii) advertising\n"+"(iv) research & \n"+"(v) sales & marketing activities.\n"+"Lilly will not use this information for any other purposes." +
                        "Lilly shall employ means to keep Personal Information reasonably accurate, complete, & ecure in accordance with the purposes for which it was collected.\n"
                        );



         fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DisclaimerActivity.this,RegistrationPage.class);
                startActivity(i);
            }
        });

        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    makeRequest();

                }else fab.setVisibility(View.INVISIBLE);

            }
        });
    }

    private void makeRequest() {


        RequestQueue requestQueue = Volley.newRequestQueue(DisclaimerActivity.this);

        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            response.getString("id");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        fab.setVisibility(View.VISIBLE);

                        Log.v("writtem:%n %s", response.toString());


                        Toast.makeText(DisclaimerActivity.this, response.toString(), Toast.LENGTH_LONG);


                    }

                },

                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v("error:%n %s", error.toString());
                        Toast.makeText(DisclaimerActivity.this, error.toString(), Toast.LENGTH_LONG);


                    }
                }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("action","_AGREE");
                return params;
            }


        };
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsObjRequest);
//        requestQueue.cancelAll(tag);

    }
}

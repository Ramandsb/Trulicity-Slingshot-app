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

public class AskQuestion extends AppCompatActivity {
    EditText ed;
    String answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_question);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
//        wl.acquire();
        ed= (EditText) findViewById(R.id.ans);
        ed.setHintTextColor(getResources().getColor(R.color.white));
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answer= ed.getText().toString();
                makeRequest(answer);
                Intent i = new Intent(AskQuestion.this,ShakeActivity.class);
                startActivity(i);

            }
        });
    }
    private void makeRequest(final String answer1) {


        RequestQueue requestQueue = Volley.newRequestQueue(AskQuestion.this);

        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, RegistrationPage.url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                                 response.getString("id");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        Log.v("writtem:%n %s", response.toString());


                        Toast.makeText(AskQuestion.this, response.toString(), Toast.LENGTH_LONG);


                    }

                },

                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v("error:%n %s", error.toString());
                        Toast.makeText(AskQuestion.this, error.toString(), Toast.LENGTH_LONG);


                    }
                }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("action","_QUESTION");
                params.put("id",RegistrationPage.id);
                params.put("answer", answer1);

                return params;
            }


        };
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsObjRequest);
//        requestQueue.cancelAll(tag);

    }

}

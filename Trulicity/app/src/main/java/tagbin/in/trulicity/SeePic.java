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
import android.widget.ImageView;
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

public class SeePic extends AppCompatActivity {
    public static ImageView imageView;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_pic);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
//        wl.acquire();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        imageView = (ImageView) findViewById(R.id.myImageview);
        makeRequest();

         fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                    Intent i = new Intent(SeePic.this, AskQuestion.class);
                    startActivity(i);

            }
        });


    }

    private void makeRequest() {


        RequestQueue requestQueue = Volley.newRequestQueue(SeePic.this);

        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, RegistrationPage.url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                           if( response.getString("response").equals("1")){
                               fab.setVisibility(View.VISIBLE);
                           }
                            else {
                               makeRequest();
                           }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        fab.setVisibility(View.VISIBLE);

                        Log.v("writtem:%n %s", response.toString());


                        Toast.makeText(SeePic.this, response.toString(), Toast.LENGTH_LONG);


                    }

                },

                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        makeRequest();
                        Log.v("error:%n %s", error.toString());
                        Toast.makeText(SeePic.this, error.toString(), Toast.LENGTH_LONG);


                    }
                }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("action","_CHECK_IMAGE");
                params.put("id",RegistrationPage.id);
                return params;
            }


        };
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsObjRequest);
//        requestQueue.cancelAll(tag);

    }

}

package tagbin.in.trulicity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.PowerManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ShakeActivity extends AppCompatActivity implements ShakeDetector.OnShakeListener, SeekBar.OnSeekBarChangeListener, View.OnTouchListener {

    private static final String TAG1 = "TestingLog";
    SensorManager mSensorManager;
    Sensor mSensoracc, mSensormag, mSensorgrav;
    //SensorEventListener myListener;
    static float[] magval, accval, ResVec, accval1 = new float[4];
    static float[] Orival = new float[4];
    static float[] Ri, Ii, Ro = new float[16];
    static double[] Angles = new double[3];
    ImageButton setVal;
    long currtime = 0;
    TextView orx,ory,orz;
    //View tv;
    public  String x,y,z;
    FloatingActionButton fab;
    public static final String TAG = ShakeActivity.class.getSimpleName();

    private static final String STATUS = "status";
    private static final String SENSIBILITY = "sensibility";
    private static final String SHAKE_NUMBER = "shake_number";
    public static int shakenum= 2;
    public static int shakeval=4;
//    protected TextView mStatus;



    protected LinearLayout mRightDrawer;

    protected SeekBar mSensibility;

    protected SeekBar mShakeNumber;
    //    @InjectView(R.id.sensibility_label)
    protected TextView mSensibilityLabel;
    //        @InjectView(R.id.shake_number_label)
    protected  TextView mShakeNumberLabel;
    Bundle mysaved;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        RegisterListeners();
//        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
//        wl.acquire();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //orx= (TextView) findViewById(R.id.orx);
//        ory= (TextView) findViewById(R.id.ory);
//        orz= (TextView) findViewById(R.id.orz);

        ShakeDetector.create(this, new ShakeDetector.OnShakeListener() {
            @Override
            public void OnShake() {
                Log.d("shaked", "");
                makeRequest();

            }
        });
//       ShakeDetector.updateConfiguration(4, 2);


         fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent i = new Intent(ShakeActivity.this, DisclaimerActivity.class);
                startActivity(i);
                ShakeActivity.this.finish();

            }
        });
        fab.setVisibility(View.INVISIBLE);

        setVal= (ImageButton) findViewById(R.id.setVals);
        setVal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setVals();
            }
        });


//        new Timer().scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                makeRequest(x,y,z);
//                Log.d("SENSOR","Timer called");
//            }
//        }, 0,200);//put here time 1000 milliseconds=1 second
    }

    private void setVals() {

        final Dialog sliderDialog = new Dialog(ShakeActivity.this);
        sliderDialog.setTitle("Set Values");
        sliderDialog.setContentView(R.layout.slider);
        mSensibility = (SeekBar) sliderDialog.findViewById(R.id.sensibility);
        mShakeNumber = (SeekBar) sliderDialog.findViewById(R.id.shake_number);
        mSensibilityLabel= (TextView) sliderDialog.findViewById(R.id.sensibility_label);
        mShakeNumberLabel= (TextView) sliderDialog.findViewById(R.id.shake_number_label);
        Button button= (Button) sliderDialog.findViewById(R.id.set);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sliderDialog.dismiss();
            }
        });

//            mStatus.setText(savedInstanceState.getString(STATUS));
        mSensibility.setProgress(shakeval);
        mShakeNumber.setProgress(shakenum);


        mSensibility.setOnSeekBarChangeListener(ShakeActivity.this);
        mSensibility.setOnTouchListener(ShakeActivity.this);
        mShakeNumber.setOnSeekBarChangeListener(ShakeActivity.this);
        mShakeNumber.setOnTouchListener(ShakeActivity.this);
        sliderDialog.show();

    }


    @Override
    public void OnShake() {
        // This callback is triggered by the ShakeDetector. In a real implementation, you should
        // do here a real action.
        Log.d("shake", "detected");
        makeRequest();

//        Toast.makeText(this, getString(R.string.device_shaken), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            if (seekBar.getId() == R.id.sensibility) {
                float sensibility = (float) (progress + 10) / 10;
                shakeval=progress;
                Log.d("myprogess",""+sensibility+" "+progress+" "+mShakeNumber.getProgress());
                ShakeDetector.updateConfiguration(sensibility, mShakeNumber.getProgress());
                updateSeekBarLabel(mSensibilityLabel, String.format("%.1f", sensibility));
                addStatusMessage(getString(R.string.update_sensibility, sensibility));
            } else if (seekBar.getId() == R.id.shake_number) {
                ShakeDetector.updateConfiguration((mSensibility.getProgress() + 10) / 10, progress);
                updateSeekBarLabel(mShakeNumberLabel, String.valueOf(progress));
                shakenum=progress;
                addStatusMessage(getString(R.string.update_shake_number, progress));
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // Nothing to see here
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // Nothing to see here
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // Disallow Drawer to intercept touch events.
                view.getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_UP:
                // Allow Drawer to intercept touch events.
                view.getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }

        // Handle seekbar touch events.
        view.onTouchEvent(motionEvent);
        return true;
    }

    private void addStatusMessage(String message) {
        String date = new SimpleDateFormat("HH:mm:ss-SSS").format(new Date());
        String status = String.format("\n[%s] %s", date, message);

//        mStatus.append(status);
        Log.d(TAG, status);
    }

    private void updateSeekBarLabel(TextView view, String textToAppend) {
        String label = "";
        if (view.getId() == R.id.sensibility_label) {
            label = getString(R.string.label_sensibility, textToAppend);
        }
        if (view.getId() == R.id.shake_number_label) {
            label = getString(R.string.label_shake_number, textToAppend);
        }
        view.setText(label);
    }


    @Override
    protected void onPause() {
        super.onPause();
        ShakeDetector.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ShakeDetector.start();
    }

    private void makeRequest() {

        Log.d("shaked","list");

        RequestQueue requestQueue = Volley.newRequestQueue(ShakeActivity.this);

        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, RegistrationPage.url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {



                        Log.v("Response:%n %s", response.toString());
                        fab.setVisibility(View.VISIBLE);


                        Toast.makeText(ShakeActivity.this, response.toString(), Toast.LENGTH_LONG);


                    }

                },

                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v("error:%n %s", error.toString());
                        Toast.makeText(ShakeActivity.this, error.toString(), Toast.LENGTH_LONG);


                    }
                }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("action", "_SHAKE");
                params.put("id", RegistrationPage.id);
                params.put("status", "1");
                return params;
            }


        };
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsObjRequest);
//        requestQueue.cancelAll(tag);

    }

//    public void shareText(String subject, String body) {
//        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
//        sharingIntent.setType("text/plain");
//        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
//        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, body);
//        startActivity(Intent.createChooser(sharingIntent, "Share via"));
//    }
//
//    public void RegisterListeners() {
//        //Initialize the Arrays
//        Ri = new float[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1};
//        Ro = new float[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1};
//        Ii = new float[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1};
//
//        magval = new float[]{0, 0, 0};
//        accval = new float[]{0, 0, 0, 0};
//        accval1 = new float[]{0, 0, 0, 0};
//        ResVec = new float[]{0, 0, 0, 0};
//
//
//        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//
//
//        if (mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) != null) {
//            mSensorgrav = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
//            mSensorManager.registerListener(myListener, mSensorgrav, SensorManager.SENSOR_DELAY_NORMAL);
//            Log.d(TAG, "GRAVPASS");
//        } else {
//            Log.d(TAG, "GRAVFAIL");
//            // Sorry, there are no accelerometers on your device.
//            // You can't play this game.
//            if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
//                mSensoracc = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//                mSensorManager.registerListener(myListener, mSensoracc, SensorManager.SENSOR_DELAY_FASTEST);
//                Log.d(TAG, "ACCEREG");
//            } else {
//                Log.d(TAG, "ACCFAIL");
//                // Sorry, there are no accelerometers on your device.
//                // You can't play this game.
//            }
//        }
//
//        if (mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
//            mSensormag = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
//            mSensorManager.registerListener(myListener, mSensormag, SensorManager.SENSOR_DELAY_NORMAL);
//            Log.d(TAG, "GRAVPASS");
//        } else {
//            Log.d(TAG, "GRAVFAIL");
//            // Sorry, there are no accelerometers on your device.
//            // You can't play this game.
//        }
//    }
//
//    public SensorEventListener myListener = new SensorEventListener() {
//
//        @Override
//        public void onSensorChanged(SensorEvent event) {
//            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
//                //Log.d(TAG, "AccelFail");
//                //accx.setText(""+String.format("%.3f\n",event.values[0]));
//                //accy.setText(""+String.format("%.3f\n",event.values[1]));
//                //accz.setText(""+String.format("%.3f\n",event.values[2]));
//                double a = Math.sqrt(Math.pow(event.values[0], 2) + Math.pow(event.values[1], 2) + Math.pow(event.values[2], 2));
//                //accful.setText(String.format("%.3f\n", a));
//                System.arraycopy(event.values, 0, accval, 0, 3);
//
//            } else if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
//
//                //Log.d(TAG, "MagFail");
//                //magx.setText(""+String.format("%.3f\n",event.values[0]));
//                //magy.setText(""+String.format("%.3f\n",event.values[1]));
//                //magz.setText(""+String.format("%.3f\n",event.values[2]));
//                double b = Math.sqrt(Math.pow(event.values[0], 2) + Math.pow(event.values[1], 2) + Math.pow(event.values[2], 2));
//                //magful.setText(String.format("%.3f\n", b));
//                //Angles[0] = Math.acos(event.values[0]/b);
//                //Angles[1] = Math.acos(event.values[1]/b);
//                //Angles[2] = Math.acos(event.values[2]/b);
//                System.arraycopy(event.values, 0, accval1, 0, 3);
//            }
//            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
//                //Log.d(TAG, "MagFail");
//                //magx.setText(""+String.format("%.3f\n",event.values[0]));
//                //magy.setText(""+String.format("%.3f\n",event.values[1]));
//                //magz.setText(""+String.format("%.3f\n",event.values[2]));
//                double b = Math.sqrt(Math.pow(event.values[0], 2) + Math.pow(event.values[1], 2) + Math.pow(event.values[2], 2));
//                //magful.setText(String.format("%.3f\n", b));
//
//                System.arraycopy(event.values, 0, magval, 0, 3);
//            }
//            boolean succ = SensorManager.getRotationMatrix(Ri, null, accval1, magval);
//
//            if (magval != null && accval != null && Ri != null) {
//                //Log.d(TAG, "Rotation"+Ri[9]);
//                //Log.d(TAG, ""+Ri.length);
//                //android.opengl.Matrix.invertM(Ii,0,Ri,0);
//                //android.opengl.Matrix.multiplyMV(ResVec, 0, Ii, 0, accval1, 0);
//                /*
//                if (currtime>100) {
//                    float timeDelta =(System.currentTimeMillis() - currtime)/1000f;
//
//                    //veloc[0] += timeDelta*accval1[0];
//                    //veloc[1] += timeDelta*accval1[1];
//                    //veloc[2] += timeDelta*accval1[2];
//                    //Log.d(TAG, ""+veloc[0]+" "+veloc[1]+" "+veloc[2]);
//                    //Log.d(TAG, ""+ResVec[0]);
//                    //distTrav[0] += (veloc[0]*timeDelta);// + ((Math.pow(timeDelta, 2) / 2) * accval1[0]);
//                    //distTrav[1] += (veloc[1]*timeDelta);// + ((Math.pow(timeDelta, 2) / 2) * accval1[1]);
//                    //distTrav[2] += (veloc[2]*timeDelta);// + ((Math.pow(timeDelta, 2) / 2) * accval1[2]);
//
//
//
//                    //magx.setText("" + String.format("%.8f\n", distTrav[0]));
//                    //magy.setText("" + String.format("%.8f\n", distTrav[1]));
//                    //magz.setText("" + String.format("%.8f\n", distTrav[2]));
//                }
//                */
//                currtime = System.currentTimeMillis();
//                SensorManager.remapCoordinateSystem(Ri, SensorManager.AXIS_X, SensorManager.AXIS_Z, Ro);
//
//                Orival = SensorManager.getOrientation(Ro, Orival);
//                //Incl = SensorManager.getInclination(Ii);
//                //magful.setText(String.format("%.3f\n", Math.toDegrees(Incl)));
//
//                orx.setText("" + String.format("%.3f\n", Math.toDegrees(Orival[0])));
//                ory.setText("" + String.format("%.3f\n", Math.toDegrees(Orival[1])));
//                orz.setText("" + String.format("%.3f\n", Math.toDegrees(Orival[2])));
//
//                 x = Double.toString(Math.toDegrees(Orival[0]));
//                 y = Double.toString(Math.toDegrees(Orival[1]));
//                 z = Double.toString(Math.toDegrees(Orival[2]));
//                Log.d("SENSOR",x+"-"+y+"-"+z);
//
//
//            }
//        }
//
//        @Override
//        public void onAccuracyChanged(Sensor sensor, int i) {
//
//        }
//    };
//
//    public float orX(){
//        return Orival[0];
//    }
//
//    public float orY(){
//        return Orival[1];
//    }
//
//    public float orZ(){
//        return Orival[2];
//    }
//

//    private void makeRequest(final String x,final String y,final String z) {
//        Log.d("vals",x+y+z);
//
//
//        RequestQueue requestQueue = Volley.newRequestQueue(ShakeActivity.this);
//
//        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, RegistrationPage.url, null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//
////                        try {
////                           // response.getString("id");
////                        } catch (JSONException e) {
////                            e.printStackTrace();
////                        }
//
//
//                        Log.v("writtem:%n %s", response.toString());
//
//
//                        Toast.makeText(ShakeActivity.this, response.toString(), Toast.LENGTH_LONG);
//
//
//                    }
//
//                },
//
//                new Response.ErrorListener() {
//
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.v("error:%n %s", error.toString());
//                        Toast.makeText(ShakeActivity.this, error.toString(), Toast.LENGTH_LONG);
//
//
//                    }
//                }) {
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("action","_ANGLE");
//                params.put("id",RegistrationPage.id);
//                params.put("x", x);
//                params.put("y", y);
//                params.put("z", z);
//
//                return params;
//            }
//
//
//        };
//        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        requestQueue.add(jsObjRequest);
////        requestQueue.cancelAll(tag);
//
//    }



}

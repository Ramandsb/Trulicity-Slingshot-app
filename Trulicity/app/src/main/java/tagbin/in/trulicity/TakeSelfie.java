package tagbin.in.trulicity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TakeSelfie extends AppCompatActivity implements SurfaceHolder.Callback {
    private static final String TAG = "CameraTest";
    Camera mCamera;
    boolean mPreviewRunning = false;
    TextView tv;
    Bitmap resizedBitmap;
    public static String imageResponse="";
//    String url="http://192.168.1.13/trulicity-webapp/action/action.php";
String url;
    @SuppressWarnings("deprecation")
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);
        Log.e(TAG, "onCreate");
//        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
//        wl.acquire();

        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_take_selfie);
        tv = (TextView) findViewById(R.id.textView);
        mSurfaceView = (SurfaceView) findViewById(R.id.surface_camera);
//        mSurfaceView.setOnClickListener(this);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captureImage();
            }
        });
         url= RegistrationPage.url;



        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
    }


    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {

        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
            if (data != null){
                //Intent mIntent = new Intent();
                //mIntent.putExtra("image",imageData);

                mCamera.stopPreview();
                mPreviewRunning = false;
                mCamera.release();

                try{
                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    Bitmap bitmap= BitmapFactory.decodeByteArray(data, 0, data.length,opts);
                    bitmap = Bitmap.createScaledBitmap(bitmap, 880, 500, false);
                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();
                    int newWidth = 880;
                    int newHeight = 500;



                    // calculate the scale - in this case = 0.4f
                    float scaleWidth = ((float) newWidth) / width;
                    float scaleHeight = ((float) newHeight) / height;

                    Log.d("values","width  "+width+" height :"+height+"scaleWidth  : "+scaleWidth+"scaleHeight  :"+scaleHeight);

                    // createa matrix for the manipulation
                    Matrix matrix = new Matrix();
                    // resize the bit map
//                    matrix.postScale(scaleWidth, scaleHeight);
                    // rotate the Bitmap
                    matrix.postRotate(-90);
                     resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                            width, height, matrix, true);
//                    	 CaptureCameraImage.image.setImageBitmap(resizedBitmap);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    SeePic.imageView.setImageBitmap(resizedBitmap);
                     //bm is the bitmap object
                    byte[] b = baos.toByteArray();
                    String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
                    Log.d("encoded Image", encodedImage);

                    makeRequest(encodedImage);


                }catch(Exception e){
                    e.printStackTrace();
                }

                setResult(585);
                finish();
            }
        }
    };
    private void makeRequest(final String encodedImage) {

        Log.v("makeRequest", url);

        RequestQueue requestQueue = Volley.newRequestQueue(TakeSelfie.this);

        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {


                        try {

                           imageResponse= response.getString("response");


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.v("ImageSent", response.toString());
                        Log.v("id", RegistrationPage.id);


//                        Toast.makeText(TakeSelfie.this, response.toString(), Toast.LENGTH_LONG);


                    }

                },

                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v("error:%n %s", error.toString());
//                        Toast.makeText(TakeSelfie.this, error.toString(), Toast.LENGTH_LONG);


                    }
                }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("action","_SAVE_IMAGE");
                params.put("id", RegistrationPage.id);
                params.put("image", encodedImage);

                return params;
            }


        };
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsObjRequest);
//        requestQueue.cancelAll(tag);

    }


    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
    }

    protected void onStop(){
        Log.e(TAG, "onStop");
        super.onStop();
    }

    @TargetApi(9)
    public void surfaceCreated(SurfaceHolder holder){
        Log.e(TAG, "surfaceCreated");
        mCamera = Camera.open(1);

        try {
            mCamera.setPreviewDisplay(holder);

        } catch (IOException e) {
            Log.d("error",e.toString());
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        Log.e(TAG, "surfaceChanged");

        // XXX stopPreview() will crash if preview is not running
        if (mPreviewRunning){
            mCamera.stopPreview();
        }

        Camera.Parameters p = mCamera.getParameters();
        p.setPreviewSize(100, 100);




//            mCamera.setParameters(p);
        try{
            mCamera.setPreviewDisplay(holder);
            mCamera.setDisplayOrientation(90);
        }catch (Exception e){
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
//            mCamera.setDisplayOrientation(-90);
        mCamera.startPreview();
        mPreviewRunning = true;
//            mCamera.takePicture(null, mPictureCallback, mPictureCallback);
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e(TAG, "surfaceDestroyed");
        //mCamera.stopPreview();
        //mPreviewRunning = false;
        //mCamera.release();
    }

    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;

//    public void onClick(View v) {
        // TcODO Auto-generated method stub
public void captureImage() {
        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean b, Camera camera) {
                new CountDownTimer(3000, 700) {

                    public void onTick(long millisUntilFinished) {
//                    tv.setText("" + millisUntilFinished / 1000);
                        performTick(millisUntilFinished);
                    }

                    public void onFinish() {
                        tv.setText("Smile");
                        mCamera.takePicture(null, mPictureCallback, mPictureCallback);
                        Intent i = new Intent(TakeSelfie.this, SeePic.class);
                        startActivity(i);

                    }
                }.start();



            }
        });



    }

    private void performTick(long millisUntilFinished) {
        tv.setText("" + String.valueOf(Math.round(millisUntilFinished * 0.001f)));
    }

}
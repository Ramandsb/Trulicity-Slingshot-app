package tagbin.in.trulicity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

/**
 * Created by admin pc on 24-10-2015.
 */
public class LetsClickSelfiePage extends Activity {
    public static int cameraID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_selfie);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
//        wl.acquire();
        ImageButton fab = (ImageButton) findViewById(R.id.clickselfie);
        final String regRes=RegistrationPage.regResponse;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraID = 1;
                Intent i = new Intent(LetsClickSelfiePage.this,TakeSelfie.class);
                startActivityForResult(i, 999);
            }
        });

    }

}

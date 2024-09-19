package com.app.tensquare.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.app.tensquare.R;
import com.app.tensquare.utils.AppUtills;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;

import org.jsoup.Jsoup;

public class Splash_trailActivity extends AppCompatActivity {

    String currentVersion = "";
    AppUpdateManager manager;
    Task<AppUpdateInfo> task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_trail);

        try {
            this.currentVersion = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        manager = AppUpdateManagerFactory.create(this);
        task = manager.getAppUpdateInfo();

        task.addOnSuccessListener(appUpdateInfo -> {
           if(appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE){
               Toast.makeText(this, "Update", Toast.LENGTH_SHORT).show();
           }else{
               Toast.makeText(this, "NO", Toast.LENGTH_SHORT).show();
           }
        });


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                new RetrieveFeedTask().execute("dfs");
//                goTo();

            }
        },1000);

    }



    class RetrieveFeedTask extends AsyncTask<String, Void, String> {

        protected String doInBackground(final String... urls) {
            try {
                final String newVersion =
                        Jsoup.connect("https://play.google.com/store/apps/details?id=" +
                                        Splash_trailActivity.this.getPackageName() + "&hl=en")
                                .timeout(30000)
                                .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) " +
                                        "Gecko/20070725 Firefox/2.0.0.6")
                                .referrer("http://www.google.com")
                                .get()
                                .select("div.hAyfc:nth-child(4) > span:nth-child(2) > div:nth-child(1) > " +
                                        "span:nth-child(1)")
                                .first()
                                .ownText();

                return newVersion;

            } catch (final Exception e) {

//                if (AppUtills.INSTANCE.isDebug())
//                    Log.e("VersionCodeAps","ex "+e.getMessage());

                return null;
            }
        }

        protected void onPostExecute(final String newVersion) {
            if(newVersion!=null) {
                if(Splash_trailActivity.this.currentVersion.equalsIgnoreCase(newVersion)) {
//                    if (Utils.isDebug())
//                        Log.e("VersionCodeAps","latest version");

                    Log.e("Check" , " go to");

                } else {
                    try{
                        final String currentMainLine = Splash_trailActivity.this.currentVersion.split("\\.")[0];
                        final String  newMainLine = newVersion.split("\\.")[0];
                        if(currentMainLine.equalsIgnoreCase(newMainLine)) {
//                            if (Utils.isDebug())
//                                Log.e("VersionCodeAps","old version with not require");
//                            showUpdatePopup();
                            Log.e("Check" , " showUpdatePopup");
                        } else {
//                            if (Utils.isDebug())
//                                Log.e("VersionCodeAps","old version with require");
//                            showUpdatePopup();
                            Log.e("Check" , " showUpdatePopup");
                        }

                    } catch (final Exception e) {
                        e.printStackTrace();

                        Log.e("Check" , " GO To");
                    }

                }
            } else {
                Log.e("Check" , " GO To");
            }
//            if (Utils.isDebug())
//                Log.e("VersionCodeAps","cureent post "+newVersion);
        }
    }

}
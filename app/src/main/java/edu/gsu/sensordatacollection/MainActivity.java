package edu.gsu.sensordatacollection;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.WindowCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import edu.gsu.sensordatacollection.adapter.GridViewAdapter;
import edu.gsu.sensordatacollection.databinding.ActivityMainBinding;
import edu.gsu.sensordatacollection.model.Item;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String ACTION_LOCATION_BROADCAST = SensorDataService.class.getName() + "sensorDataBroadCast";
    public static final String ZONE = "zone_id";
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    private GridView zones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        zones = findViewById(R.id.grid_view);
        ArrayList<Item> items = new ArrayList<Item>();
        for(int i = 1; i < 10; i++){
            items.add(new Item(""+i));
        }
        GridViewAdapter adapter = new GridViewAdapter(this, items);
        zones.setAdapter(adapter);

        zones.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                sendBroadcastMessage(position);
            }
        });

        final ToggleButton tB = (ToggleButton) findViewById(R.id.record_button);
        tB.setOnClickListener(arg0 -> {
            if(tB.isChecked()){
                startService(new Intent(MainActivity.this, SensorDataService.class));
            } else{
                stopService(new Intent(MainActivity.this, SensorDataService.class));
            }
        });

        ActivityCompat.requestPermissions( this,
                new String[]{
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, 1
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()){


            }else{
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        }

        final FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(arg0 -> {
            try {
                // define Intent object with action attribute as ACTION_SEND
                Intent intent = new Intent(Intent.ACTION_SEND);

                // add three fields to intent using putExtra function
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"toy041126@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "sensorData");
                intent.putExtra(Intent.EXTRA_TEXT, "emailbody");

                File root = Environment.getExternalStorageDirectory();
                File file = new File(root, SensorDataService.fileName);
                if (!file.exists() || !file.canRead()) {
                    Toast.makeText(this, "Attachment Error", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

               // Uri uri = Uri.parse("file://" + file);
                Uri uri = FileProvider.getUriForFile(
                        this,
                        "edu.gsu.sensordatacollection.MainActivity.provider",
                        file);


                intent.putExtra(Intent.EXTRA_STREAM, uri);

                // set type of intent
                intent.setType("message/rfc822");
                startActivity(Intent.createChooser(intent, "Choose an Email client :"));

            } catch (Exception e) {
                Log.e("SendMail", e.getMessage(), e);
            }
        });
    }

    private void sendBroadcastMessage(int zone_id) {
        Intent intent = new Intent(ACTION_LOCATION_BROADCAST);
        intent.putExtra(ZONE, new Integer(zone_id));
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, SensorDataService.class));
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopService(new Intent(this, SensorDataService.class));
    }

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // feature requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            });
}
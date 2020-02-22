package br.com.transescolar.Activies;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;
import androidx.core.app.NavUtils;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import br.com.transescolar.Conexao.SessionManager;
import br.com.transescolar.R;

public class DriverMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    FusedLocationProviderClient mFusedLocationClient;

    private GoogleApiClient mGoogleApiClient;

    private LatLng pickupLocation;
    private Button mLogout;

    SessionManager sessionManager;
    String getId, getCpf, getNome;
    String getStatus;

    private DatabaseReference databaseR;

    private String customerId = "";

    private SupportMapFragment mapFragment;

    Boolean isLogginOut = false;

    LocationManager locationManager;

    private LocationListener mLocationListener;

    private Switch mWorkingSwitch;
    private final Handler handler = new Handler();

    public static final String SWITCH_PREFS = "Switch";
    public static final String SWITCH_CHECKED = "isChecked";
    public static final String SWITCH_NOT_CHECKED = "isNotChecked";
    public static final String SWITCH_STATUS = "Status";
    public static final int MY_PERMISSION_CODE = 1;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    //teste
    private Boolean mLocationPermissionsGranted = false;
    private static final float DEFAULT_ZOOM = 15f;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        //Criando o tollbar para custumizar
        getActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getActionBar().setTitle("Mapa");

        //iniciando a sessão
        sessionManager = new SessionManager(this);

        //pegando dados da sessão
        HashMap<String, String> user = sessionManager.getUserDetail();
        getId = user.get(sessionManager.ID);
        getCpf = user.get(sessionManager.CPF);
        getNome = user.get(sessionManager.APELIDO);

        HashMap<String, String> status = sessionManager.getStatus();
        getStatus = status.get(sessionManager.STATUS);

        mWorkingSwitch = findViewById(R.id.workingSwitch);

        sharedpreferences = getSharedPreferences("MY PREFS", Context.MODE_PRIVATE);
        mWorkingSwitch.setChecked(sharedpreferences.getBoolean(SWITCH_STATUS, true));
        mWorkingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    connectDriver();
                    doTheAutoRefresh();
                    sessionManager.createSessionStatus("isChecked");
                    Toast.makeText(DriverMapsActivity.this, "Você está ON LINE", Toast.LENGTH_SHORT).show();

//                    SharedPreferences.Editor editor = sharedpreferences.edit();
//                    editor.putBoolean(SWITCH_STATUS, true);
//                    editor.commit();
                    Log.d("Chamada if", "isChecked");

                } else {
                    sessionManager.createSessionStatus("notChecked");
                    stopLocationUpdates();
//                    SharedPreferences.Editor editor = sharedpreferences.edit();
//                    editor.putBoolean(SWITCH_STATUS, false);
//                    editor.commit();
                    Toast.makeText(DriverMapsActivity.this, "Você está OFF LINE", Toast.LENGTH_SHORT).show();
                    Log.d("Chamada if", "isNotChecked");
                    finish();
                }
            }
        });

        getLocationPermission();

        Toast.makeText(this, getStatus, Toast.LENGTH_SHORT).show();
    } //Oncreate

    //Teste

    private void getDeviceLocation() {
        Log.d("Chamada", "getDeviceLocation: pegando a localização");
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionsGranted) {

                final Task location = mFusedLocationClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d("Chamada", "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM);

                        } else {
                            Log.d("Chamada", "onComplete: current location is null");
                            Toast.makeText(DriverMapsActivity.this, "Localização indisponível!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Chamada", "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    private void saveDeviceLocation() {
        Log.d("Chamada", "getDeviceLocation: pegando a localização");
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionsGranted) {

                final Task location = mFusedLocationClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d("Chamada", "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM);

                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference refAvailable = database.getReference("driversAvailable");
                            DatabaseReference refWorking = database.getReference("driversWorking");
                            GeoFire geoFireAvailable = new GeoFire(refAvailable);
                            GeoFire geoFireWorking = new GeoFire(refWorking);

                            geoFireAvailable.setLocation(getCpf, new GeoLocation(currentLocation.getLatitude(), currentLocation.getLongitude()), new GeoFire.CompletionListener() {
                                @Override
                                public void onComplete(String key, DatabaseError error) {
                                    if (error != null) {
                                        Log.d("Sucesso", "Localização Salva ");
                                    }
                                }
                            });

                        } else {
                            Log.d("Chamada", "onComplete: current location is null");
                            Toast.makeText(DriverMapsActivity.this, "Localização indisponível!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Chamada", "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, float zoom) {
        Log.d("Chamada", "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void initMap() {
        Log.d("Chamada", "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(DriverMapsActivity.this);
    }

    private void getLocationPermission() {
        Log.d("Chamada", "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    //Teste final

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (mLocationPermissionsGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            //mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }


    }// onMapReady

    private void turnGPSOff() {
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if (provider.contains("gps")) { //if gps is enabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            sendBroadcast(poke);
        }
    }

    private void setUplocation() {
        if (androidx.core.app.ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && androidx.core.app.ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestRuntimePermission();
        }
    }

    private void requestRuntimePermission() {
        androidx.core.app.ActivityCompat.requestPermissions(this, new String[]
                {
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                }, MY_PERMISSION_CODE);
    }

    private void myPermissionLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("De permissão para localização!")
                        .setMessage("Para funcionmento precisa ter permição")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(DriverMapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                new android.app.AlertDialog.Builder(this)
                        .setTitle("give permission")
                        .setMessage("give permission message")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(DriverMapsActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(DriverMapsActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    private void myAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Localização");
        builder.setMessage("Para funcionmento precisa ter permição")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ActivityCompat.requestPermissions(DriverMapsActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                if (getApplicationContext() != null) {
                    mLastLocation = location;

                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference refAvailable = database.getReference("driversAvailable");
                    DatabaseReference refWorking = database.getReference("driversWorking");
                    GeoFire geoFireAvailable = new GeoFire(refAvailable);
                    GeoFire geoFireWorking = new GeoFire(refWorking);

                    geoFireAvailable.setLocation(getNome, new GeoLocation(location.getLatitude(), location.getLongitude()), new GeoFire.CompletionListener() {
                        @Override
                        public void onComplete(String key, DatabaseError error) {
                            if (error != null) {
                                Log.d("Sucesso", "Localização Salva ");
                            }
                        }
                    });

                    Log.d("MSG", "Ainda pegando a localização");
                }
            }

        }

    };


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        //mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
                    }
                    mapFragment.getMapAsync(this);
                } else {
                    Toast.makeText(this, "Precisa permitir o acesso a localização!", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
        Log.d("Call", "onRequestPermissionsResult");

    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        }
        Log.d("Chamada", "Location update started ..............: ");
        requestLocationUpdates();
    }

    public void requestLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        Log.d("Chamada", "requestLocationUpdates");

    }

    protected void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        Log.d("Chamada", "Location update stopped .......................");
    }

    private void connectDriver() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        //mMap.setMyLocationEnabled(true);
        Log.d("Chamada", "connectDriver");
    }

    private void disconectarDriver() {

        mFusedLocationClient.removeLocationUpdates(mLocationCallback);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("driversAvailable");

        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(getNome, new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                Log.d("Chamada", "disconectarDriver");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mWorkingSwitch.isChecked()){
            connectDriver();
            Toast.makeText(this, "Você está ONLINE", Toast.LENGTH_SHORT).show();
            Log.d("Chamada mWorkingSwitch", "onCreate isChecked");
        }else {
            stopLocationUpdates();
            Toast.makeText(this, "Você está OFLINE", Toast.LENGTH_SHORT).show();
            Log.d("Chamada mWorkingSwitch", "onCreate isNotChecked");
        }
        Log.d("Chamada", "onStart..............");
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.d("Chamada", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mWorkingSwitch.isChecked()){
            sessionManager.createSessionStatus("isChecked");
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean(SWITCH_STATUS, true);
            editor.commit();
        }else {
            stopLocationUpdates();
            sessionManager.createSessionStatus("notChecked");
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean(SWITCH_STATUS, false);
            editor.commit();
        }

        Log.d("Chamada", "onDestroy");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    private void doTheAutoRefresh() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getStatus.equals("isChecked")){
                    getDeviceLocation();
                }else {

                }
                getDeviceLocation();
                doTheAutoRefresh();
            }
        }, 3000);
    }

}// Map Activity

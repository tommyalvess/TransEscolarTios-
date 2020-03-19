package br.com.transescolar.Activies;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;

import com.google.android.material.snackbar.Snackbar;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.onesignal.OneSignal;

import android.widget.ToggleButton;


import java.util.HashMap;

import br.com.transescolar.Conexao.NetworkChangeReceiver;
import br.com.transescolar.Conexao.SessionManager;
import br.com.transescolar.R;
import de.hdodenhof.circleimageview.CircleImageView;

import static br.com.transescolar.controler.HomeControler.showSnackbarC;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback {


    SessionManager sessionManager;
    static CircleImageView imgPass,imgEscola,imgIntinerario,imgUsuario,imgPais;
    static String LoggedIn_User_Email;
    String getCpf, getStatus, getNome, getApelido;
    Location mLastLocation;
    FusedLocationProviderClient mFusedLocationClient;
    LocationRequest mLocationRequest;
    private GoogleMap mMap;
    private Boolean mLocationPermissionsGranted = false;

    static Snackbar snackbar;
    private NetworkChangeReceiver mNetworkReceiver;

    ToggleButton locationI;

    static ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();

        //TODO: OneSignalll, inicinado
        OneSignal.startInit(this).init();

        // pegar infs da session
        HashMap<String, String> user = sessionManager.getUserDetail();
        getCpf = user.get(sessionManager.CPF);
        getNome = user.get(sessionManager.APELIDO);


        HashMap<String, String> status = sessionManager.getStatus();
        getStatus = status.get(sessionManager.STATUS);

        //TODO: OneSignalll, pegando os dado e enviando
        LoggedIn_User_Email = getCpf;
        OneSignal.sendTag("User_ID", LoggedIn_User_Email);

        imgPass = findViewById(R.id.passageiros);
        imgEscola = findViewById(R.id.escolas);
        imgIntinerario = findViewById(R.id.itinerario);
        imgUsuario = findViewById(R.id.user);
        imgPais = findViewById(R.id.pais);

        //TODO: Recebendo informação sobre conexão com a net.
        mNetworkReceiver = new NetworkChangeReceiver();
        registerNetworkBroadcastForNougat();

        constraintLayout = findViewById(R.id.constraintLayoutH);
        locationI = findViewById(R.id.locationI);

        locationI.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled, change the heart color to red
                    locationI.setBackgroundResource(R.drawable.location);
                    connectDriver();

                } else {
                    // The toggle is disabled, change to the default heart
                    disconnectDriver();
                    locationI.setBackgroundResource(R.drawable.locationoff);
                }
            }
        });

        imgPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("PassageirosActivity","passageiro");
                Intent intent = new Intent(HomeActivity.this, PassageirosActivity.class);
                startActivity(intent);
            }
        }); //Fim do imgPass

        imgEscola.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("EscolasActivity","escola");
                Intent intent = new Intent(HomeActivity.this, EscolasActivity.class);
                startActivity(intent);

            }
        });// Fim imgEscola

        imgIntinerario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ItinerarioActivity","intinerario");
                Intent intent = new Intent(HomeActivity.this, RotaActivity
                        .class);
                startActivity(intent);
            }
        }); // Fim do imgIntinerario

        imgUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("PerfilActivity","PerfilActivity");
                //disconnectDriver();
                Intent intent = new Intent(HomeActivity.this, PerfilActivity.class);
                startActivity(intent);
            }
        }); // Fim do imgUsuario

        imgPais.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("PaisActivity","PaisActivity");
                Intent intent = new Intent(HomeActivity.this, PaisActivity.class);
                startActivity(intent);
            }
        }); // Fim do imgPais

        checkLocationPermission();
    }//OnCreate

    public  boolean verificaConexao() {
        boolean conectado;
        ConnectivityManager conectivtyManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conectivtyManager.getActiveNetworkInfo() != null
                && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected()) {
            conectado = true;
        } else {
            conectado = false;
        }
        return conectado;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (verificaConexao() == true){
            sessionManager.checkLogin();

        }else {
            //Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            //startActivity(intent);
        }
    }

    private void checkLocationPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("give permission")
                        .setMessage("give permission message")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            }
                        })
                        .create()
                        .show();
            }
            else{
                ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    };

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                if (getApplicationContext() != null) {
                    mLastLocation = location;

                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

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

    private void disconnectDriver(){
        if(mFusedLocationClient != null){
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    private void connectDriver(){
        checkLocationPermission();
        startLocationUpdates();
        getDeviceLocation();
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            }else{
                checkLocationPermission();
            }
        }
    }

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

                        } else {
                            Log.d("Chamada", "onComplete: current location is null");
                            snackbar = showSnackbarC(constraintLayout, Snackbar.LENGTH_INDEFINITE, HomeActivity.this);
                            snackbar.show();
                            View view = snackbar.getView();
                            TextView tv = (TextView) view.findViewById(R.id.textSnack);
                            tv.setText("Localização indisponível!");
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Chamada", "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    public void requestLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        Log.d("Chamada", "requestLocationUpdates");
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        //mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                    }
                } else {
                    snackbar = showSnackbarC(constraintLayout, Snackbar.LENGTH_INDEFINITE, HomeActivity.this);
                    snackbar.show();
                    View view = snackbar.getView();
                    TextView tv = (TextView) view.findViewById(R.id.textSnack);
                    tv.setText("Precisa permitir o acesso a localização!");
                }
                break;
            }
        }
        Log.d("Call", "onRequestPermissionsResult");

    }

    @Override
    protected void onPause() {
        super.onPause();
        locationI.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled, change the heart color to red
                    connectDriver();
                    locationI.setBackgroundResource(R.drawable.location);
                  } else {
                    // The toggle is disabled, change to the default heart
                    disconnectDriver();
                    locationI.setBackgroundResource(R.drawable.locationoff);
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationI.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled, change the heart color to red
                    connectDriver();
                    locationI.setBackgroundResource(R.drawable.location);
                } else {
                    // The toggle is disabled, change to the default heart
                    disconnectDriver();
                    locationI.setBackgroundResource(R.drawable.locationoff);
                }
            }
        });
    }

    private void registerNetworkBroadcastForNougat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    protected void unregisterNetworkChanges() {
        try {
            unregisterReceiver(mNetworkReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterNetworkChanges();
    }

    private void logoffDriver(){
        if(mFusedLocationClient != null){
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }

        HashMap<String, String> user = sessionManager.getUserDetail();
        getApelido = user.get(sessionManager.APELIDO);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query applesQuery = ref.child("driversAvailable").child(getNome);

        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Chamada", "onCancelled", databaseError.toException());
            }
        });

    }

    public void notificar(MenuItem item) {
//        snackbar = showSnackbar(constraintLayout, Snackbar.LENGTH_LONG, this);
//        snackbar.show();
//        View view = snackbar.getView();
//        TextView tv = (TextView) view.findViewById(R.id.snackbar_action);
//        tv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                snackbar.dismiss();
//            }
//        });

        }

    public void logout(View view) {
        dialogExit();
    }

    private void dialogExit(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_text, null);
        final TextView nomeE = mView.findViewById(R.id.nomeD);
        nomeE.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        Button mSim = mView.findViewById(R.id.btnSim);
        Button mNao = mView.findViewById(R.id.btnNao);

        alertDialog.setView(mView);
        final AlertDialog dialog = alertDialog.create();

        nomeE.setText("Você deseja realmente sair?");
        mSim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.logout();
                logoffDriver();
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                dialog.dismiss();
            }
        });
        mNao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}//Class

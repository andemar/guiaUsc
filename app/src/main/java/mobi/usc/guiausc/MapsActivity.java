package mobi.usc.guiausc;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;
import com.google.android.gms.tasks.OnSuccessListener;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private Ruteador ruteador;

    private static final LatLng USC = new LatLng(3.403026785954639, -76.54848105758441);
    private static final int TRANSPARENCY_MAX = 100;

    /** Formato para las URL de las imagenes en servidor */
    private static final String MOON_MAP_URL_FORMAT =
            "https://mw1.google.com/mw-planetary/lunar/lunarmaps_v1/clem_bw/%d/%d/%d.jpg";

    private LatLngBounds boundsCampusUSCPampalinda;
    private LatLngBounds boundsBloque2;

    private TileOverlay mMoonTiles;
    private SeekBar mTransparencyBar;

    private Polyline ruta;
    private Marker marcador;
    private static MapsActivity instance;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;

    private GroundOverlay overlayCapusPampalinda;
    private GroundOverlay[] bloqueActual;
    private GroundOverlay[] overlayPisosBloque2;

    private Button btnUp;
    private Button btnDown;
    private Button btnDestino;

    private TextView txtlat;
    private TextView txtlong;

    Polyline linea;
    List<LatLng> listaPuntos;
    ArrayList<Salon> listaPiso0;
    ArrayList<Salon> listaPiso1;
    ArrayList<Salon> listaPiso2;
    ArrayList<Salon> listaPiso3;


    private static final int PISOS_BLOQUE2 = 1;

    private int pisoActual;
    private int limiteActual;
    private String destino;
    private String origen;
    private static final String TAG = "MapsActivity";
    private boolean rutaexiste;
    private boolean lineaLanzamiento;
    private boolean requestingLocationUpdates;
    private static final String REQUESTING_LOCATION_UPDATES_KEY = "0";


    private boolean focusBloque;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        if(savedInstanceState == null){
            mapFragment.getMapAsync(this);
        }


        lineaLanzamiento = false;
        rutaexiste = false;

        Log.d(TAG, "onCreate: se creo");


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        btnUp = (Button) findViewById(R.id.buttonSubir);
        btnUp.setVisibility(View.INVISIBLE);
        btnDown = (Button) findViewById(R.id.buttonBajar);
        btnDown.setVisibility(View.INVISIBLE);
        btnDestino = (Button) findViewById(R.id.buttonDestino);

        destino = getIntent().getStringExtra("Salon");

        txtlat = (TextView) findViewById(R.id.textolat);
        txtlat.setText("0");
        txtlat.setVisibility(View.INVISIBLE);
        txtlong = (TextView) findViewById(R.id.textolong);
        txtlong.setText("0");
        txtlong.setVisibility(View.INVISIBLE);

        instance = this;

        focusBloque = false;

        listaPiso0 = new ArrayList<Salon>();
        listaPiso1 = new ArrayList<Salon>();
        listaPiso2 = new ArrayList<Salon>();
        listaPiso3 = new ArrayList<Salon>();

        //TODO
        limiteActual = 2;

        btnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(bloqueActual[pisoActual]!=null){
                    bloqueActual[pisoActual].setTransparency(1);
                }

                pisoActual++;

                if(pisoActual > 1 &&   bloqueActual[pisoActual]!=null){
                    overlayCapusPampalinda.setTransparency(0.5f);
                    Log.d(TAG, "Piso up: " + pisoActual);
                    bloqueActual[pisoActual].setTransparency(0);
                    setPuntosPiso(linea, pisoActual);
                    buttonVisible();

                }else if(pisoActual==1){

                    overlayCapusPampalinda.setTransparency(0);
                    buttonVisible();
                }else{
                    //TODO Cuando manejemos sotanos
                }
            }
        });

        btnDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(bloqueActual[pisoActual]!=null){
                    bloqueActual[pisoActual].setTransparency(1);
                }
                pisoActual--;

                if(pisoActual > 1 &&   bloqueActual[pisoActual]!=null){
                    overlayCapusPampalinda.setTransparency(0.5f);
                    Log.d(TAG, "Piso down: " + pisoActual);
                    bloqueActual[pisoActual].setTransparency(0);
                    setPuntosPiso(linea, pisoActual);
                    buttonVisible();
                }else if(pisoActual==1){

                    overlayCapusPampalinda.setTransparency(0);
                    buttonVisible();
                }else{
                    //TODO Cuando manejemos sotanos
                }


            }
        });

        btnDestino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                buttonVisible();

                getUltimaPosicion();


                if (txtlat.getText().equals("0") && txtlong.getText().equals("0") && rutaexiste == false) {

                } else {
                    double latorigne = Double.parseDouble(txtlat.getText().toString());

                    double longorigen = Double.parseDouble(txtlong.getText().toString());

                    if (latorigne != 0 || longorigen != 0) {

                        Log.d(TAG, "onMapClick: lat orgien" + latorigne + " long " + longorigen);
                    }

                    LatLng inicio = new LatLng(latorigne, longorigen);

                    if (inicio != null) {

                        Log.d(TAG, "onMapClick: inicio existe " + inicio.latitude + " -long " + inicio.longitude);
                    }

                    //Enrutamiento y pintada de primera linea
                    if (lineaLanzamiento == false) {


                        Salon origen = ruteador.masCercano(inicio);

                        Log.d(TAG, "onMapClick: nodo mas cercano" + ruteador.masCercano(inicio).getCodigo());

                        Log.d(TAG, "btnDestino: " + destino);

                        String CodigoDestino = destino;

                        Salon destino = ruteador.buscarSalon(CodigoDestino);

                        ArrayList<Salon> listafinal = ruteador.enrutar(destino, origen);

                        LatLng coord;

                        linea = mMap.addPolyline(new PolylineOptions().color(Color.BLUE));

                        listaPuntos = linea.getPoints();

                        //metodo para separar los pisos

                        setListasPisos(listafinal);

                        //for para llenar los puntos del piso 0
                    /*
                    for (int i = 1; i < listaPiso0.size(); i++) {

                        coord = new LatLng(listaPiso0.get(i).getLatitud(), listaPiso0.get(i).getLongitud());

                        listaPuntos.add(coord);

                        coord = null;

                    }
                    */
                        setPuntosPiso(linea, 1);

                        linea.setPoints(listaPuntos);



                        lineaLanzamiento = true;

                    }
                }

                //Log.d(TAG, "onMapClick: lat"+inicio.latitude+"long"+inicio.longitude);


                //Cuando hacen tap en el bloque 2 transparentar el plano general y mostrar el plano del bloque
                //en el piso 0

                /*
                if (boundsBloque2.contains(mapClick)) {

                   // overlayCapusPampalinda.setTransparency(0.5f);
                    //overlayBloque2P1.setTransparency(0);
                    btnUp.setVisibility(View.VISIBLE);
                    focusBloque = true;
                    pisoActual = 0;
                    limiteActual = PISOS_BLOQUE2;
                    bloqueActual = overlayPisosBloque2;

                } else {


                }
                */


            }
        });


        locationCallback = new LocationCallback(){

            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    // ... TODO

                    Toast.makeText(MapsActivity.this, "acutaliza", Toast.LENGTH_SHORT).show();

                    if(marcador != null){
                        marcador.remove();
                    }

                    LatLng coordenadasUsuario = new LatLng(location.getLatitude(), location.getLongitude());
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(coordenadasUsuario);
                    markerOptions.title("Usuario");
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                    marcador = mMap.addMarker(markerOptions);

                    Toast.makeText(MapsActivity.this, "Deberia crear marcador", Toast.LENGTH_SHORT).show();



                }
            }
        };
        updateValuesFromBundle(savedInstanceState);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (requestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        stopLocationUpdates();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        ruteador = new Ruteador();

        //Toast.makeText(instance, "Coordenadas de inicio, lat"+inicio.latitude + " long "+inicio.longitude, Toast.LENGTH_SHORT).show();

        mMap = googleMap;

        //Establece el tipo de mapa como NORMAL
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //Se crean un objeto LatLngBounds con las coordenadas de los limites del mapa
        // que se muestran en la aplicacion.
        // coordenadas originales  new LatLng(3.401541193971563, -76.5496220406365)
        boundsCampusUSCPampalinda = new LatLngBounds(
                 //new LatLng(3.4013, -76.54978), new LatLng(3.4044, -76.5467) //Coordenadas del plano general de la universidad
                //new LatLng(3.403801921103504, -76.54802872296392), new LatLng(3.403036777475968, -76.54698391208632)
                new LatLng(3.4030856467031017, -76.54809219727076), new LatLng(3.4038092580688097, -76.54673464999632)
        );

        //Coordenadas originales
        boundsBloque2 = new LatLngBounds(
                new LatLng(3.403173716809185, -76.54806048100147), new LatLng(3.40376, -76.54735)

        );


        //
        mMap.setLatLngBoundsForCameraTarget(boundsCampusUSCPampalinda);

        //Se establecen los limites minimos y maximos del zoom al mapa
        mMap.setMinZoomPreference(21.0f);
        mMap.setMaxZoomPreference(21.0f);


        TileProvider tileProvider = new UrlTileProvider(256, 256) {
            @Override
            public synchronized URL getTileUrl(int x, int y, int zoom) {
                // The moon tile coordinate system is reversed.  This is not normal.
                //String s = String.format(Locale.US, MOON_MAP_URL_FORMAT, zoom, x, y);
                String s = "http://guiausc.000webhostapp.com/images/test/planocampusbloque2.jpg";
                URL url = null;
                try {
                    url = new URL(s);
                } catch (MalformedURLException e) {
                    throw new AssertionError(e);
                }

                Log.d("a", "getTileUrl: "+url);
                return url;
            }
        };

        mMoonTiles = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider));



        //Crea el nuevo GroundOverlay con las coordenadas de la universidad y agrega la imagen
        //del plano desde la carpeta res/drawable
        /*
        GroundOverlayOptions USCMapa = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.planocampus))
                .positionFromBounds(boundsCampusUSCPampalinda);


        //.position(USC, 8600f, 6500f);

        //Se agrega el GroundOverlay al mapa
        final GroundOverlay overlayCapus = mMap.addGroundOverlay(USCMapa);

        overlayCapusPampalinda = overlayCapus;
        */

        //Crea el nuevo GroundOverlay con las coordenadas del bloque 2 y agrega la imagen
        //del plano del piso 1 desde la carpeta res/drawable

        /*
        GroundOverlayOptions bloque2P1Mapa = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.bloque2piso1))
                .positionFromBounds(boundsBloque2);


        final GroundOverlay overlayBloque2P1 = mMap.addGroundOverlay(bloque2P1Mapa);
        overlayBloque2P1.setTransparency(1);
        */


        //Crea el nuevo GroundOverlay con las coordenadas del bloque 2 y agrega la imagen
        //del plano del piso 2 desde la carpeta res/drawable

        /*
        GroundOverlayOptions bloque2P2Mapa = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.bloque2piso2))
                .positionFromBounds(boundsBloque2);

        final GroundOverlay overlayBloque2P2 = mMap.addGroundOverlay(bloque2P2Mapa);
        overlayBloque2P2.setTransparency(1);

        //overlayPisosBloque2 = new GroundOverlay[]{overlayBloque2P1, overlayBloque2P2}; // TODO Cuando se vuelva a diferenciar cuando entre a los bloques
        overlayPisosBloque2 = new GroundOverlay[]{null, null, overlayBloque2P2};
        bloqueActual = overlayPisosBloque2;
        */

        // Add a marker in Sydney and move the camera
        //LatLng USC = new LatLng(3.4034734809095464, -76.54695657064843);
        LatLng USC = new LatLng(3.4034453993340605, -76.54781796958935);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(USC, 20.0f));

        pisoActual = 1;

        createLocationRequest();

        startLocationUpdates();

    }


    private void setListasPisos(ArrayList<Salon> listafinal) {

        for (int i = 0; i < listafinal.size(); i++) {

            Log.d(TAG, "setListasPisoslistaenrutada: " + listafinal.get(i).getCodigo());

            if (listafinal.get(i).getPiso() == 0) {


                listaPiso0.add(listafinal.get(i));
                Log.d(TAG, "setListasPisos0: objeto" + i);

            } else if (listafinal.get(i).getPiso() == 1) {

                listaPiso1.add(listafinal.get(i));
                Log.d(TAG, "setListasPisos1: objeto" + i);

            } else if (listafinal.get(i).getPiso() == 2) {

                listaPiso2.add(listafinal.get(i));
                Log.d(TAG, "setListasPisos2: objeto" + i);

            } else if (listafinal.get(i).getPiso() == 3) {

                listaPiso3.add(listafinal.get(i));
                Log.d(TAG, "setListasPisos3: objeto" + i);

            }

        }

    }


    private void setPuntosPiso(Polyline linea, int pisoActual) {

        LatLng coord;

        listaPuntos.clear();

        if (pisoActual == 0) {

            for (int i = 0; i < listaPiso0.size(); i++) {

                coord = new LatLng(listaPiso0.get(i).getLatitud(), listaPiso0.get(i).getLongitud());

                listaPuntos.add(coord);

                //Log.d(TAG, "setPuntosPiso0: objeto:"+i);

                coord = null;

            }

            linea.setPoints(listaPuntos);

        } else if (pisoActual == 1) {

            for (int i = 0; i < listaPiso1.size(); i++) {

                coord = new LatLng(listaPiso1.get(i).getLatitud(), listaPiso1.get(i).getLongitud());

                listaPuntos.add(coord);

                Log.d(TAG, "setPuntosPiso1: objeto:" + i);

                coord = null;

            }

            linea.setPoints(listaPuntos);

        } else if (pisoActual == 2) {

            for (int i = 0; i < listaPiso2.size(); i++) {

                coord = new LatLng(listaPiso2.get(i).getLatitud(), listaPiso2.get(i).getLongitud());

                listaPuntos.add(coord);

                Log.d(TAG, "setPuntosPiso2: objeto:" + i + "Salon" + listaPiso2.get(i).getCodigo());

                coord = null;

            }

            linea.setPoints(listaPuntos);

        } else if (pisoActual == 3) {

            for (int i = 0; i < listaPiso3.size(); i++) {

                coord = new LatLng(listaPiso3.get(i).getLatitud(), listaPiso3.get(i).getLongitud());

                listaPuntos.add(coord);

                //Log.d(TAG, "setPuntosPiso3: objeto:"+i);

                coord = null;

            }

            linea.setPoints(listaPuntos);
        }

    }

    private void buttonVisible() {

        if (pisoActual == limiteActual) {
            btnUp.setVisibility(View.INVISIBLE);
            btnDown.setVisibility(View.VISIBLE);
        } else if (pisoActual == 1) {
            btnDown.setVisibility(View.INVISIBLE);
            btnUp.setVisibility(View.VISIBLE);
        } else {
            btnDown.setVisibility(View.VISIBLE);
            btnUp.setVisibility(View.VISIBLE);
        }

    }


    private void getUltimaPosicion() {


        /*
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if(task.isSuccessful()){
                    Location location = task.getResult();
                    Toast.makeText(MapsActivity.this, "Entro al coordendas", Toast.LENGTH_SHORT).show();

                    //Log.d(TAG, "onComplete: completo coordenadas"+coord.latitude+"-"+coord.longitude);
                    //inicio= coord;
                    ruteador.setCoordInicio(location.getLatitude(), location.getLongitude());

                    guardarCoord(location.getLatitude(), location.getLongitude());
                    Log.d(TAG, "onComplete: completo inicio1"+location.getLatitude()+"-"+location.getLongitude());

                }
            }
        });
        */

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
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {

                    txtlat.setText(location.getLatitude() + "");

                    txtlong.setText(location.getLongitude() + "");

                }
            }
        });
        {

        }

    }


    private void startLocationUpdates() {

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
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                locationCallback,
                null /* Looper */);
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    protected void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY,
                requestingLocationUpdates);
        // ...
        super.onSaveInstanceState(outState);
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }

        // Update the value of requestingLocationUpdates from the Bundle.
        if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
            requestingLocationUpdates = savedInstanceState.getBoolean(
                    REQUESTING_LOCATION_UPDATES_KEY);
        }

        // ...

        // Update UI to match restored state

    }


    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
        if(location!=null){

            Log.d(TAG, "onLocationChanged: cambio a "+location.getLatitude()+" - "+location.getLongitude());

        }
        }
    };

    public void setFadeIn(View v) {
        if (mMoonTiles == null) {
            return;
        }
        mMoonTiles.setFadeIn(((CheckBox) v).isChecked());
    }

}

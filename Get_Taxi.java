package com.example.sujit.myapplication;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;

public class Get_Taxi extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnInfoWindowClickListener {

    GoogleMap mMap;
    GoogleApiClient googleApiClient;
    double currentLatitude;
    double currentLongitude;

    AppCompatButton buttonSelect, buttonAssign, buttonCall;
    TextView textView1, textView2, textViewName1, textViewLicenseNo1,textViewAddress1,textViewMobile1,textViewTaxiNo1;


    double destnLatitude;
    double destnLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get__taxi);


        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .enableAutoManage(this, this)
                    .build();
        }
       SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.autocompleteFragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                destnLatitude = place.getLatLng().latitude;
                destnLongitude = place.getLatLng().longitude;

                mMap.clear();
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                LatLng latLng = new LatLng(currentLatitude, currentLongitude);
                mMap.addMarker(new MarkerOptions().position(latLng));
                mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                        .position(place.getLatLng())
                        .title(place.getName().toString()).snippet(place.getId()));

                getDirection();
            }
            @Override
            public void onError(Status status) {
                Log.d("Place Error", "An error occurred: " + status);
            }
        });
    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getCurrentLocation();
    }

    public void getCurrentLocation() {
        // mMap.clear();
        //Creating a location object
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null) {
            //Getting longitude and latitude
            currentLongitude = location.getLongitude();
            currentLatitude = location.getLatitude();
            //moving the map to location
            moveMap(currentLatitude, currentLongitude);
        }
    }

    public void moveMap(double currentLatitude, double currentLongitude) {
        //String to display current latitude and longitude
        String msg = currentLatitude + ", " + currentLongitude;
        //Creating a LatLng Object to store Coordinates
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);
        //Adding marker to map
        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                .position(latLng) //setting position
                .title("Current Location")); //Adding a title

        Toast.makeText(Get_Taxi.this, "Your Location : " + msg, Toast.LENGTH_LONG).show();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        //   Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("Connection failed", String.valueOf(connectionResult.getErrorCode() + connectionResult.getErrorCode()));
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

     //   mMap.addMarker(new MarkerOptions().position());
        // mMap.setOnMapLongClickListener(this);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    public String makeURL (double sourcelat, double sourcelog, double destlat, double destlog ){
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString
                .append(Double.toString( sourcelog));
        urlString.append("&destination=");// to
        urlString
                .append(Double.toString( destlat));
        urlString.append(",");
        urlString.append(Double.toString(destlog));
        urlString.append("&sensor=false&mode=driving&alternatives=true");
        urlString.append("&key=AIzaSyCdVpU-8Ccf8hvSnyHE2h2armbBRXyr6JM");
        return urlString.toString();
    }

    private void getDirection(){
        //Getting the URL
        String url = makeURL(currentLatitude, currentLongitude, destnLatitude, destnLongitude);

        //Showing a dialog till we get the route
        final ProgressDialog loading = ProgressDialog.show(this, "Getting Route", "Please wait...", false, false);

        //Creating a string request
        StringRequest stringRequest = new StringRequest(url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        //Calling the method drawPath to draw the path
                        drawPath(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        Log.d("Direction APi error", error.toString());
                    }
                });
        //Adding the request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    //The parameter is the server response
    public void drawPath(String  result) {
        //Getting both the coordinates
        LatLng from = new LatLng(currentLatitude,currentLongitude);
        LatLng to = new LatLng(destnLatitude,destnLongitude);

        //Calculating the distance in meters
        Double distance = (SphericalUtil.computeDistanceBetween(from, to));
        //Displaying the distance
        Toast.makeText(this,String.valueOf(distance +" Meters"),Toast.LENGTH_LONG).show();
       showDialog(distance);

        try {
            //Parsing json
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);
            Polyline line = mMap.addPolyline(new PolylineOptions()
                    .addAll(list)
                    .width(8)
                    .color(Color.YELLOW)
                    .geodesic(true)
            );
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showDialog(double distance) {

        String dis = String.valueOf((int)distance);

        LayoutInflater li = LayoutInflater.from(this);
        //Creating a view to get the dialog box
        View confirmDialog = li.inflate(R.layout.dialog_distance, null);

        //Initizliaing confirm button fo dialog box and edit text of dialog box
        buttonSelect = (AppCompatButton) confirmDialog.findViewById(R.id.buttonSelect);
        buttonAssign = (AppCompatButton) confirmDialog.findViewById(R.id.buttonAssign);

        textView1 = (TextView) confirmDialog.findViewById(R.id.textView1);
        textView2 = (TextView) confirmDialog.findViewById(R.id.textView2);

        textView1.setText(dis);
        //textView2.setText(time);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        //Adding our dialog box to the view of alert dialog
        alert.setView(confirmDialog);

        //Creating an alert dialog
        final AlertDialog alertDialog = alert.create();

        //Displaying the alert dialog
        alertDialog.show();

        buttonSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                getDrivers();
            }
        });
        buttonAssign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                showProfile();
            }
        });
    }
    private void showProfile() {
        LayoutInflater li = LayoutInflater.from(this);
        //Creating a view to get the dialog box
        View confirmDialog = li.inflate(R.layout.dialog_profile, null);
        buttonCall = (AppCompatButton) confirmDialog.findViewById(R.id.buttonCall);
        textViewName1 = (TextView) confirmDialog.findViewById(R.id.textViewName1);
        textViewLicenseNo1 = (TextView) confirmDialog.findViewById(R.id.textViewLicenseNo1);
        textViewAddress1 = (TextView) confirmDialog.findViewById(R.id.textViewAddress1);
        textViewMobile1 = (TextView) confirmDialog.findViewById(R.id.textViewMobile1);
        textViewTaxiNo1 = (TextView) confirmDialog.findViewById(R.id.textViewTaxiNo1);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        //Adding our dialog box to the view of alert dialog
        alert.setView(confirmDialog);

        //Creating an alert dialog
        final AlertDialog alertDialog = alert.create();

        //Displaying the alert dialog

        RequestQueue requestQueue = Volley.newRequestQueue(Get_Taxi.this);
        final ProgressDialog loading = ProgressDialog.show(this, "Getting Route", "Please wait...", false, false);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("drivers");
                    JSONObject jsonObject1 = jsonArray.getJSONObject(0);

                    textViewName1.setText(jsonObject1.getString("full_name"));
                    textViewLicenseNo1.setText(jsonObject1.getString("license_number"));
                    textViewAddress1.setText(jsonObject1.getString("address"));
                    textViewMobile1.setText(jsonObject1.getString("mobile_number"));
                    textViewTaxiNo1.setText(jsonObject1.getString("taxi_number"));
                    alertDialog.show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params= new HashMap<>();
                params.put("latitude",String.valueOf(currentLatitude));
                params.put("longitude",String.valueOf(currentLongitude));

                return params;

            }

        };requestQueue.add(stringRequest);
        buttonCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
}
    private void getDrivers() {
        final ProgressDialog loading = ProgressDialog.show(this, "Fetching Drivers", "Please wait...", false, false);

        final String SERVICE_URL = "http://192.168.0.101/retrieve.php";
        RequestQueue requestQueue = Volley.newRequestQueue(Get_Taxi.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, SERVICE_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mMap.clear();
                LatLng latLng1=new LatLng(currentLatitude,currentLongitude);
                mMap.addMarker(new MarkerOptions().position(latLng1).title("Your Location"));
                Log.d("Response Error", response.toString());
                try {
                    JSONArray jsonArray = response.getJSONArray("markers");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObj = jsonArray.getJSONObject(i);

                        double lang = jsonObj.getDouble("longitude");
                        double lat = jsonObj.getDouble("latitude");

                        LatLng latLng = new LatLng(lat,lang);

                        //move CameraPosition on first result
                        if (i == 0) {
                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(latLng).zoom(15).build();

                            mMap.animateCamera(CameraUpdateFactory
                                    .newCameraPosition(cameraPosition));
                        }

                        // Create a marker for each city in the JSON data.
                        mMap.addMarker(new MarkerOptions()
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_car))
                                .title(jsonObj.getString("username"))
                                // .snippet(Integer.toString(jsonObj.getInt("id")))
                                .position(latLng));
                    }
                    loading.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("Error Json Exception", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Toast.makeText(Get_Taxi.this, error.toString(), Toast.LENGTH_LONG).show();
                Log.d("abd", "Error: " + error
                        + ">>" + error.networkResponse.statusCode
                        + ">>" + error.getCause()
                        + ">>" + error.getMessage());
            }
        });
        requestQueue.add(jsonObjectRequest);
    }


    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng( (((double) lat / 1E5)),
                    (((double) lng / 1E5) ));
            poly.add(p);
        }
        return poly;
    }
}

package com.android.banck;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by anteneh on 9/2/2016.
 */
public class AtmMap extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener, OnMapReadyCallback
        , GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleMap.OnMarkerClickListener {

    DataSource dataSource;
    int updateOrg;
    private Toolbar mtoolbar;
    private FragmentDrawer drawerFragment;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private ProgressDialog progressDialog;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private Location mLastLocation;
    LatLng dest;
    private List<Branch> orgLocation;
    int count=0;
    Marker current,prevuos;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DisplayImageOptions defaultOptions=new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration configa=new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .build();


        ImageLoader.getInstance().init(configa);
        mtoolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        dataSource = new DataSource(this);
        dataSource.open();
        //Log.i("bbb val", dataSource.findFilteredBranch(null, null, null, null, null).size() + " " + dataSource.findFilteredBanck(null, null, null, null, null).size());
        new JSONTaskBank().execute("http://192.168.42.126/ome/fileupload.php");
        new JSONTaskBranch().execute("http://192.168.42.126/ome/fileuploadbranch.php");
        orgLocation = dataSource.findFilteredBranch(DatabaseOpenHelper.atm+" = 'T'", null, null, null, null);
        Log.i("bbb val", dataSource.findFilteredBranch(null, null, null, null, null).size() + "");
        if (dataSource.findFilteredBranch(null, null, null, null, null) != null)
            /*for (int i = 0; i < dataSource.findFilteredBranch(null, null, null, null, null).size(); i++) {

                //rep=new MarkerOptions();
                LatLng lat = new LatLng(parse_to_double(dataSource.findFilteredBranch(null, null, null, null, null).get(i).getLongtiude()), parse_to_double(dataSource.findFilteredBranch(null, null, null, null, null).get(i).getLatitude()));
                Log.i("bbb location", lat.latitude + " , " + lat.longitude);
                orgLocation.add(lat);
            }*/

            drawerFragment = (FragmentDrawer)
                    getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mtoolbar);
        drawerFragment.setDrawerListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    public double parse_to_double(String a) {
        return Double.parseDouble(a);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (mMap!=null){
            View marker = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);


            // Add a marker in Sydney and move the camera
            // LatLng sydney = new LatLng(-34, 151);
            //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));

            Log.i("bbb value", orgLocation.size() + "");
            if (orgLocation.size() > 0)
                //mMap.moveCamera(CameraUpdateFactory.newLatLng(orgLocation.get(0).getPosition()));
                Log.i("bbb orgLocation size ", orgLocation.size() + "");
            for (int i = 0; i < orgLocation.size(); i++) {
                //drawMarker(orgLocation.get(i), orgLocation.get(i).latitude + "");
                drawMarker(orgLocation.get(i),marker);
                Log.i("bbb orgLocation(", i + ")");

            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    buildGoogleApiClient();
                    mMap.setMyLocationEnabled(true);
                }
            } else {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
            mMap.setOnMarkerClickListener(this);
        }
    }

    private void drawMarker(Branch org,View marker) {

        ImageView imageView=(ImageView)marker.findViewById(R.id.image_org);
        ImageLoader.getInstance().displayImage(org.getLogo(),imageView);

        LatLng point=new LatLng(parse_to_double(org.getLongtiude()),parse_to_double(org.getLatitude()));
        MarkerOptions markerOptions = new MarkerOptions();
        if (org.getLogo()!=null)
            markerOptions.position(point)
                    .title(org.getBranch_name())
                    .snippet(org.getTel())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_atm));
        Log.i("bbb put on map",org.getBranch_name()+" "+org.getLongtiude()+" "+org.getLogo());
    }

    public static Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        count++;
        current=marker;
        if (prevuos.equals(marker))
            count++;
        else
            count=0;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        Log.i("bbb count",count+"");
        if (mLastLocation!=null&&count==2){
            double lat=mLastLocation.getLatitude();
            double lng=mLastLocation.getLongitude();
            LatLng latLng=new LatLng(lat,lng);
            LatLng origin=latLng;


//            Log.i("bbbdestnation",dest.latitude+","+dest.longitude);
            Log.i("bbb onMapClick",": reach");
            String url=getUrl(origin,marker.getPosition());
            Log.d("bbb onMapClick",url.toString());
            FetchUrl fetchUrl=new FetchUrl();
            fetchUrl.execute(url);
            Log.i("bbb","finished u must get the path");
            mMap.moveCamera(CameraUpdateFactory.newLatLng(origin));

            mMap.animateCamera(CameraUpdateFactory.zoomTo(16));



        }
        prevuos=marker;

        return false;
    }

    public class JSONTaskBank extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection urlConnection=null;
            BufferedReader reader=null;
            try {
                URL url=new URL(params[0]);
                urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.connect();
                InputStream stream=urlConnection.getInputStream();
                reader=new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer=new StringBuffer();
                String line="";
                while((line=reader.readLine())!=null){
                    buffer.append(line);

                }

                String linebuffer= buffer.toString();
                JSONObject parentObject=new JSONObject(linebuffer);

                JSONArray parentbra=parentObject.getJSONArray("banks");
                JSONObject finalobject=null;

                for (int i=0;i<parentbra.length();i++){
                    finalobject=parentbra.getJSONObject(i);
                    Bank org=new Bank();
                    org.setBank_id(finalobject.getString("bank_id"));
                    org.setBank_name(finalobject.getString("bank_name"));
                    org.setLogo("http://192.168.42.126/ome/images/"+finalobject.getString("logo"));
                    ImageLoader.getInstance().loadImageSync(org.getLogo());
                    org.setVisibility(finalobject.getString("visibility"));
                    int org_id=finalobject.getInt("bank_id");
                    if (dataSource.findFilteredBanck(null,null,null,null,null).size()==0||dataSource.findFilteredBranch(null,null,null,null,null)==null) {
                        dataSource.create_bank(org);
                    }

                    List<Bank> check=dataSource.findFilteredBanck(null,null,null,null,null);
                    for(int ch=0;ch<check.size();ch++){
                        if(check.get(ch).getBank_id().equals(org_id))
                            dataSource.create_bank(org);
                        if(check.get(ch).getBank_id().equals(org_id));
                        //updateOrg=dataSource.updateOrg(org_id+"",visibility);
                    }


                }

                return finalobject.getString("bank_name");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if(urlConnection!=null)
                    urlConnection.disconnect();
                try {
                    if (reader!=null)
                        reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            return  null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //
        }
    }

    public class JSONTaskBranch extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection urlConnection=null;
            BufferedReader reader=null;
            try {
                URL url=new URL(params[0]);
                urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.connect();
                InputStream stream=urlConnection.getInputStream();
                reader=new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer=new StringBuffer();
                String line="";
                while((line=reader.readLine())!=null){
                    buffer.append(line);

                }

                String linebuffer= buffer.toString();
                JSONObject parentObject=new JSONObject(linebuffer);

                JSONArray parentbra=parentObject.getJSONArray("branch");
                JSONObject finalobject=null;

                for (int i=0;i<parentbra.length();i++){
                    finalobject=parentbra.getJSONObject(i);
                    Branch org=new Branch();
                    org.setBranch_id(finalobject.getString("branch_id"));
                    org.setBranch_name(finalobject.getString("branch_name"));
                    org.setAtm(finalobject.getString("atm"));
                    org.setVisibility(finalobject.getString("visibility"));
                    org.setLatitude(finalobject.getString("lat"));
                    org.setLongtiude(finalobject.getString("lng"));
                    org.setTel(finalobject.getString("tel"));
                    org.setBank_id(finalobject.getString("bank_id"));
                    String bank_id=finalobject.getString("bank_id");
                    List<Bank> data=dataSource.findOrgLogo(bank_id);
                    if (data.size()>0)
                        org.setLogo(data.get(0).getLogo());
                    int org_id=finalobject.getInt("branch_id");

                    /*List<Branch> check=dataSource.findFilteredBranch(null,null,null,null,null);
                    for(int ch=0;ch<check.size();ch++){
                        if(check.get(ch).getBank_id().equals(org_id))
                            dataSource.create_branch(org);
                        if(check.get(ch).getBank_id().equals(org_id));
                        //updateOrg=dataSource.updateOrg(org_id+"",visibility);
                    }

*/  //                  if (dataSource.findFilteredBranch(null,null,null,null,null).size()==0) {
                    dataSource.create_branch(org);
                    //                  }
                }

                return finalobject.getString("branch_name");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if(urlConnection!=null)
                    urlConnection.disconnect();
                try {
                    if (reader!=null)
                        reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            return  null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        dataSource.open();
    }

    private String getUrl(LatLng origin, LatLng dest){
        String str_orgin="origin="+origin.latitude+","+origin.longitude;
        String str_dest="destination="+dest.latitude+","+dest.longitude;

        String sensor="sensor=false&mode=driving&alternatives=true";
        String parameters=str_orgin+"&"+str_dest+"&"+sensor;

        String output="json";
        String url="https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
        return url;
    }

    private class FetchUrl extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... url) {
            String data="";

            try {
                data=downloadUrl(url[0]);
                Log.d("Background TAsk data",data.toString());
            } catch (IOException e) {
                Log.d("Background Task",e.toString());
            }
            return data;
        }
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progressDialog = new ProgressDialog(AtmMap.this);
            progressDialog.setMessage("Fetching route, Please wait...");
            progressDialog.setIndeterminate(true);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.hide();
            ParserTask parserTask=new ParserTask();
            parserTask.execute(result);
        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data="";
        InputStream istream=null;
        HttpURLConnection urlConnection=null;
        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();

            istream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(istream));

            StringBuffer sb=new StringBuffer();
            String line="";
            while ((line=br.readLine())!=null){
                sb.append(line);
            }
            data=sb.toString();
            Log.d("downloadUrl",data.toString());
            br.close();
        }catch (Exception e){
            Log.d("Exception",e.toString());
        }finally {
            istream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class ParserTask extends AsyncTask<String,Integer,List<List<HashMap<String,String>>>>{

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes=null;

            try{
                jObject=new JSONObject(jsonData[0]);
                Log.d("Parser Task",jsonData[0].toString());
                DataParser parser=new DataParser();
                Log.d("Parser Task",parser.toString());

                routes=parser.parse(jObject);
                Log.d("ParserTask","Executing routes");
                Log.d("ParserTask",routes.toString());
            }catch (Exception e){
                Log.d("Parser Task",e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            super.onPostExecute(result);
            ArrayList<LatLng> points;
            PolylineOptions lineOptions=null;
            String distance="",duration="";

            for (int i=0;i<result.size();i++){
                points=new ArrayList<>();
                lineOptions=new PolylineOptions();
                List<HashMap<String, String>> path=result.get(i);

                for (int j=0;j<path.size();j++){
                    HashMap<String,String> point=path.get(j);

                    if (j==0){
                        distance=(String)point.get("distances");
                        continue;
                    }else if(j==1){
                        duration=(String)point.get("duration");
                        continue;
                    }//
                    double lat=Double.parseDouble(point.get("lat"));
                    double lng=Double.parseDouble(point.get("lng"));

                    LatLng position=new LatLng(lat,lng);
                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.parseColor("#05b1fb"));
                lineOptions.geodesic(true);
                Log.d("onPostExecute","onPostExecute lineoption");

            }
            if (distance!=null&duration!=null)
//                distance_duration.setText("Distance: "+distance+"  Duration: "+duration);

                if (lineOptions!=null){
                    mMap.addPolyline(lineOptions);
                }
                else {
                    Log.d("onPostExecute","without Polylines drawn");
                }
        }
    }

    public class DataParser{

        public List<List<HashMap<String, String>>> parse(JSONObject jObject){
            List<List<HashMap<String, String>>> routes=new ArrayList<>();
            JSONArray jRoutes;
            JSONArray jLegs;
            JSONArray jSteps;
            // JSONObject jDuration;
            //JSONObject jDistance;

            try{
                jRoutes=jObject.getJSONArray("routes");
                for (int i=0;i<jRoutes.length();i++){
                    jLegs=((JSONObject)jRoutes.get(i)).getJSONArray("legs");
                    List path=new ArrayList<>();

                    for (int j=0;j<jLegs.length();j++){
                        /*jDistance=((JSONObject)jLegs.get(j)).getJSONObject("distance");
                        HashMap<String,String> hmDistance=new HashMap<>();
                        hmDistance.put("distance",jDistance.getString("text"));
                        path.add(jDistance);

                        jDuration=((JSONObject)jLegs.get(j)).getJSONObject("duration");
                        HashMap<String,String> hmDuration=new HashMap<>();
                        hmDistance.put("duration",jDuration.getString("text"));
                        path.add(jDuration);*/

                        jSteps=((JSONObject)jLegs.get(j)).getJSONArray("steps");

                        for (int k=0;k<jSteps.length();k++){
                            String polyline="";
                            polyline=(String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                            List<LatLng> list=decodePoly(polyline);

                            for (int l=0;l<list.size();l++){
                                HashMap<String,String> hm=new HashMap<>();
                                hm.put("lat",Double.toString((list.get(l)).latitude));
                                hm.put("lng",Double.toString((list.get(l)).longitude));
                                path.add(hm);
                            }
                        }
                        routes.add(path);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        private List<LatLng> decodePoly(String encoded){
            List<LatLng> poly=new ArrayList<>();
            int index=0,len=encoded.length();
            int lat=0,lng=0;

            while (index<len){
                int b,shift=0,result=0;
                do {
                    b=encoded.charAt(index++)-63;
                    result |=(b&0x1f)<<shift;
                    shift+=5;
                }while (b>=0x20);
                int dlat=((result &1)!= 0 ? ~(result>>1):(result >> 1));

                lat+=dlat;

                shift=0;
                result=0;

                do {
                    b=encoded.charAt(index++)-63;
                    result |=(b&0x1f)<<shift;
                    shift+=5;
                }while (b>=0x20);
                int dlng=((result&1)!=0 ? ~(result>>1):(result >> 1));
                lng+=dlng;

                LatLng p=new LatLng((((double) lat / 1E5)),(((double) lng / 1E5)));
                poly.add(p);
            }
            return poly;
        }
    }

    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient=new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest=new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation=location;
        LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
        LatLng origin=latLng;


//        Log.i("destnation",dest.latitude+","+dest.longitude);
        Log.i("onMapClick",": reach");
        String url=getUrl(origin,dest);
        Log.d("onMapClick",url.toString());
        FetchUrl fetchUrl=new FetchUrl();
        fetchUrl.execute(url);
        Log.i("bbb","finished u must get the path");
        mMap.moveCamera(CameraUpdateFactory.newLatLng(origin));

        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));



        if (mGoogleApiClient!=null){
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
        }



    }


    public boolean checkLocationPermisssion(){
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSIONS_REQUEST_LOCATION);

            }
            else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSIONS_REQUEST_LOCATION);

            }
            return false;
        }else{
            return true;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSIONS_REQUEST_LOCATION:{
                if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                        if (mGoogleApiClient==null)
                            buildGoogleApiClient();

                        mMap.setMyLocationEnabled(true);
                    }
                }else{
                    Toast.makeText(this,"permission denied",Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}

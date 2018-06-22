package com.android.banck;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;

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
import java.util.List;

/**
 * Created by anteneh on 6/1/2016.
 */
public class fragment_business extends AppCompatActivity {
    Dialog dialog;
    DataSource dataSource;
    private RecyclerView recyclerView;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private orgRecycleAdapter adapter;
     FloatingActionButton search_action;
    public fragment_business(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        DisplayImageOptions defaultOptions=new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration configa=new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .build();


        ImageLoader.getInstance().init(configa);

        dataSource=new DataSource(this);
        dataSource.open();
        final List<Bank> list=dataSource.findFilteredBanck(null,null,null,null,null);
        if(list.size()==0) {
            Bank orga=new Bank();
            orga.setBank_name("No result");

            list.add(orga);


        }
        final List<Bank> list1=new ArrayList<Bank>();
        for(int i=0;i<list.size();i++){
            list1.add(list.get(i));
        }
        Log.i("bbb",""+list1.size());
        Log.i("bbb",""+list.size());
        recyclerView=(RecyclerView)findViewById(R.id.recycler_view);
        staggeredGridLayoutManager=new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        adapter=new orgRecycleAdapter(this,list);
        recyclerView.setAdapter(adapter);
        /*recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy>0||dy<0&&search_action.isShown()){
                    search_action.hide();
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                if(newState== AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                    search_action.show();
                super.onScrollStateChanged(recyclerView, newState);

            }
        });*/
        new JSONTaskBank().execute("http://192.168.42.126/ome/fileupload.php");
        new JSONTaskBranch().execute("http://192.168.42.126/ome/fileuploadbranch.php");

        orgRecycleAdapter.OnItemClickListener onItemClickListener=new orgRecycleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
/*
                Intent i=new Intent(getActivity(),ScrollingActivity.class);
                i.putExtra(DatabaseOpenHelper.org_id,list1.get(position).getOrg_id()+"");
                Log.i("bbb id",list1.get(position).getOrg_id()+"");
                i.putExtra(DatabaseOpenHelper.logo,list1.get(position).getLogo());
                i.putExtra(DatabaseOpenHelper.org_name,list1.get(position).getOrg_name());
                i.putExtra(DatabaseOpenHelper.tell,list1.get(position).getTell());
                i.putExtra(DatabaseOpenHelper.fax,list1.get(position).getFax());
                i.putExtra(DatabaseOpenHelper.po_box,list1.get(position).getPo_box());
                i.putExtra(DatabaseOpenHelper.email,list1.get(position).getEmail());
                i.putExtra(DatabaseOpenHelper.street_name,list1.get(position).getStreet_name());
                i.putExtra(DatabaseOpenHelper.address,list1.get(position).getAddress());
                i.putExtra(DatabaseOpenHelper.phone,list1.get(position).getPhone());
                i.putExtra(DatabaseOpenHelper.city,list1.get(position).getCity());
                i.putExtra(DatabaseOpenHelper.country,list1.get(position).getCountry());
                i.putExtra(DatabaseOpenHelper.logo,list1.get(position).getLogo());
                i.putExtra(DatabaseOpenHelper.latitude,list1.get(position).getLatitude());
                i.putExtra(DatabaseOpenHelper.longitude,list1.get(position).getLongitude());
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
*/
            }
        };
        adapter.setOnItemClickListener(onItemClickListener);
    }



    public void onResume() {
        super.onResume();
        dataSource.open();
    }

    @Override
    public void onPause() {
        super.onPause();
        dataSource.close();
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
                    //if (dataSource.findFilteredBanck(null,null,null,null,null).size()==0||dataSource.findFilteredBranch(null,null,null,null,null)==null) {
                       dataSource.create_bank(org);
                    //}

//                    List<Bank> check=dataSource.findFilteredBanck(null,null,null,null,null);
  //                  for(int ch=0;ch<check.size();ch++){
    //                    if(check.get(ch).getBank_id().equals(org_id))
        //                    dataSource.create_bank(org);
      //                  if(check.get(ch).getBank_id().equals(org_id));
                        //updateOrg=dataSource.updateOrg(org_id+"",visibility);
          //          }


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


}

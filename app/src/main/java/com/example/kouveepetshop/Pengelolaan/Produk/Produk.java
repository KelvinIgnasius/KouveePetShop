package com.example.kouveepetshop.Pengelolaan.Produk;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.baoyz.widget.PullRefreshLayout;
import com.example.kouveepetshop.API.Rest_API;
import com.example.kouveepetshop.MainActivity;
import com.example.kouveepetshop.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Produk extends AppCompatActivity {

    private RecyclerView.Adapter mAdapter;
    private ArrayList<ProdukDAO> mItems;
    private ProgressDialog pd;
    private String ip = MainActivity.getIp();

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mManager;
    ImageView tambah;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.produk);
        final PullRefreshLayout layout = findViewById(R.id.swipeRefreshLayout);

        init();

        tambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Produk.this, Produk_Tambah.class);
                startActivityForResult(i,1);
            }
        });

        //Recycle View kalau dipaksa tarik kebawah sampai pol bakal ngerefresh viewnya
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mItems.clear();
                loadjson();
                layout.setRefreshing(false);
            }
        });

        loadjson();
    }

    //Inisialisasi
    private void init(){
        pd = new ProgressDialog(this);
        mRecyclerView = findViewById(R.id.recycle_produk);
        mItems = new ArrayList<>();
        mManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mManager);
        mAdapter = new Produk_Adapter(this, mItems);
        mRecyclerView.setAdapter(mAdapter);
        tambah = findViewById(R.id.produk_add);
    }

//  Ngambil Data untuk ditampilin
    private void loadjson(){
        mItems.clear();
        pd.setMessage("Mengambil Data");
        pd.setCancelable(false);
        pd.show();
        String url = "http://"+ip+"/rest_api-kouvee-pet-shop-master/index.php/Produk/";

        JsonObjectRequest arrayRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("volley", "response : " + response.toString());

                try {
                    JSONArray massage = response.getJSONArray("message");
                    for (int i = massage.length()-1; i > 0 ; i--){
                        JSONObject massageDetail = massage.getJSONObject(i);
                        ProdukDAO produk = new ProdukDAO();
                        produk.setId(massageDetail.getInt("id"));
                        produk.setNama(massageDetail.getString("nama"));
                        produk.setKategori(massageDetail.getString("id_kategori_produk"));
                        produk.setHarga(massageDetail.getInt("harga"));
                        produk.setSatuan(massageDetail.getString("satuan"));
                        produk.setJmlh(massageDetail.getInt("jmlh"));
                        produk.setJmlh_min(massageDetail.getInt("jmlh_min"));
                        mItems.add(produk);
                    }
                    pd.cancel();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                pd.cancel();
                Log.d("volley", "error : " + error.getMessage());
            }
        });
        Rest_API.getInstance(this).addToRequestQueue(arrayRequest);
    }

    // Supaya kalau dia balik ke halaman produk, bakal di refresh recycle viewnya. Baru bisa berfungsi waktu selesai Produk_Tambah, Produk Edit belom bisa
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loadjson();
        mAdapter.notifyDataSetChanged();
    }
}

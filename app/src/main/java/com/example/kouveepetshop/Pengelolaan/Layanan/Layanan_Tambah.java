package com.example.kouveepetshop.Pengelolaan.Layanan;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.kouveepetshop.API.Rest_API;
import com.example.kouveepetshop.MainActivity;
import com.example.kouveepetshop.Pengelolaan.KeteranganDAO;
import com.example.kouveepetshop.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Layanan_Tambah extends AppCompatActivity {


    private Integer harga;
    private final String id_pegawai = "KelvinAja";
    private ArrayList<String> mItems = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private ProgressDialog pd;
    private String ip = MainActivity.getIp();
    private Spinner kategori_spinner,ukuran_spinner;
    private ArrayList<LayananDAO> kategori_layanan;
    private ArrayList<LayananDAO> ukuran_layanan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layanan_add);
        pd = new ProgressDialog(this);
        mItems = new ArrayList<>();
        kategori_layanan = new ArrayList<>();
        ukuran_layanan = new ArrayList<>();


        Button tambah = findViewById(R.id.layanan_tambah_add);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,mItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        kategori_spinner = findViewById(R.id.layanan_add_spinner);
        kategori_spinner.setAdapter(adapter);
        ukuran_spinner = findViewById(R.id.layanan_spinner_ukuran);
        ukuran_spinner.setAdapter(adapter);


        loadjson();
        loadUkuran();
        tambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProduk();
                Intent returnIntent = new Intent();
                setResult(RESULT_OK,returnIntent);
                finish();
            }
        });

    }

    private void addProduk(){
        getValue();

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://" + ip + "/rest_api-kouvee-pet-shop-master/index.php/Produk";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  request = new HashMap<String, String>();
                request.put("harga", String.valueOf(harga));
                request.put("created_by", id_pegawai);
                return request;
            }
        };
        queue.add(postRequest);
    }

    private void loadjson() {
        pd.setMessage("Mengambil Data");
        pd.setCancelable(false);
        pd.show();
        String url = "http://" + ip + "/rest_api-kouvee-pet-shop-master/index.php/jenislayanan";

        JsonObjectRequest arrayRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("volley", "response : " + response.toString());

                try {
                    JSONArray massage = response.getJSONArray("message");

                    for (int i = 0; i < massage.length(); i++) {
                        JSONObject massageDetail = massage.getJSONObject(i);
                        mItems.add(massageDetail.getString("nama"));

                        LayananDAO layanan = new LayananDAO();
                        layanan.setId(massageDetail.getInt("id"));
                        layanan.setKeterangan(massageDetail.getString("nama"));
                        kategori_layanan.add(layanan);

                        adapter.notifyDataSetChanged();
                    }
                    pd.cancel();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                pd.cancel();
                Log.d("volley", "error : " + error.getMessage());
            }
        });
        Rest_API.getInstance(this).addToRequestQueue(arrayRequest);
    }

    private void loadUkuran() {
        pd.setMessage("Mengambil Data");
        pd.setCancelable(false);
        pd.show();
        String url = "http://" + ip + "/rest_api-kouvee-pet-shop-master/index.php/Ukuranhewan";

        JsonObjectRequest arrayRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("volley", "response : " + response.toString());

                try {
                    JSONArray massage = response.getJSONArray("message");

                    for (int i = 0; i < massage.length(); i++) {
                        JSONObject massageDetail = massage.getJSONObject(i);
                        mItems.add(massageDetail.getString("nama"));

                        LayananDAO ukuran = new LayananDAO();
                        ukuran.setId(massageDetail.getInt("id"));
                        ukuran.setKeterangan(massageDetail.getString("nama"));
                        ukuran_layanan.add(ukuran);

                        adapter1.notifyDataSetChanged();
                    }
                    pd.cancel();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                pd.cancel();
                Log.d("volley", "error : " + error.getMessage());
            }
        });
        Rest_API.getInstance(this).addToRequestQueue(arrayRequest);
    }

    private void getValue(){
        EditText harga_text = findViewById(R.id.produk_tambah_harga);



        String jenis = kategori_spinner.getSelectedItem().toString();
        KeteranganDAO keterangan = new KeteranganDAO();



        harga = Integer.parseInt(harga_text.getText().toString());

    }
}

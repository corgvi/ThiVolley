package com.example.thivolley.screen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.thivolley.BaseURL;
import com.example.thivolley.R;
import com.example.thivolley.adapter.MotoAdapter;
import com.example.thivolley.model.Moto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Moto> listMoto = new ArrayList<Moto>();
    private EditText edName, edPrice, edImg, edColor;
    private Button btnAdd, btnLoad, btnBackup;
    private MotoAdapter adapter;
    private RecyclerView rcvMoto;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edName = findViewById(R.id.ed_name);
        edColor = findViewById(R.id.ed_color);
        edImg = findViewById(R.id.ed_img);
        edColor = findViewById(R.id.ed_color);
        edPrice = findViewById(R.id.ed_price);
        btnAdd = findViewById(R.id.btn_add);
        btnLoad = findViewById(R.id.btn_load);
        btnBackup = findViewById(R.id.btn_backup);
        getListMoto();
        rcvMoto = findViewById(R.id.rcv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rcvMoto.setLayoutManager(layoutManager);
        adapter = new MotoAdapter(listMoto, this);
        rcvMoto.setAdapter(adapter);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postMoto();
            }
        });
        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getListMoto();
            }
        });
        btnBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, BackupActivity.class));
            }
        });
    }

    private void getListMoto() {
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, BaseURL.BASE_URL_GET, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                String name = jsonObject.getString("name");
                                int price = Integer.parseInt(jsonObject.getString("price"));
                                String img = jsonObject.getString("image");
                                String color = jsonObject.getString("color");
                                String id = jsonObject.getString("id");
                                String createdAt = jsonObject.getString("createdAt");
                                listMoto.add(new Moto(createdAt, name, img, color, price, id));
                                adapter.setData(listMoto);
                                rcvMoto.setAdapter(adapter);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: " + error.toString());
            }
        });

// Add the request to the RequestQueue.
        queue.add(jsonArrayRequest);
    }

    private void postMoto() {

        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);


            JSONObject jsonBody = new JSONObject();
            jsonBody.put("name", edName.getText().toString());
            jsonBody.put("image", edImg.getText().toString());
            jsonBody.put("price", edPrice.getText().toString());
            jsonBody.put("color", edColor.getText().toString());

            final String requestBody = jsonBody.toString();

            //Cú pháp: StringRequest stringRequest = new StringRequest(METHOD, URL, respone) {request_option};

            StringRequest stringRequest = new StringRequest(Request.Method.POST, BaseURL.BASE_URL_GET, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VOLLEY  response post ", response);
                    if (response != null) {
                        edColor.setText("");
                        edPrice.setText("");
                        edName.setText("");
                        edImg.setText("");
                        Toast.makeText(MainActivity.this, "Them thanh cong", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                    Toast.makeText(MainActivity.this, "Them that bai", Toast.LENGTH_SHORT).show();
                }
            }) {// phần gửi đi
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        responseString = String.valueOf(response.statusCode);
                        // can get more details such as response.headers
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };
            // thực hiện kết nối server và gửi dữ liệu (volley thực thi)
            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
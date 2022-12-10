package com.example.thivolley.screen;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.thivolley.BaseURL;
import com.example.thivolley.R;
import com.example.thivolley.model.Moto;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SuaActivity extends AppCompatActivity {

    private Button btnSua;
    private EditText edName, edPrice, edColor;
    private ImageView img;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sua);

        btnSua = findViewById(R.id.btn_sua);
        edColor = findViewById(R.id.ed_color);
        edName = findViewById(R.id.ed_name);
        edPrice = findViewById(R.id.ed_price);
        img = findViewById(R.id.img);

        Moto moto = (Moto) getIntent().getSerializableExtra("moto");
        Log.d("TAG", "onCreate: " + moto.toString());
        edColor.setText(moto.getColor());
        edName.setText(moto.getName());
        edPrice.setText(moto.getPrice()+"");

        Glide.with(this)
                .load(moto.getImage())
                .centerCrop()
                .placeholder(R.drawable.ic_baseline_file_download_off_24)
                .into(img);

        btnSua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestQueue queue = Volley.newRequestQueue(SuaActivity.this);
                StringRequest request = new StringRequest(Request.Method.PUT, BaseURL.BASE_URL_PUT + moto.getId(), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(SuaActivity.this, "Update success", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SuaActivity.this, MainActivity.class));
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SuaActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
                    }
                }){
                    @Nullable
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();

                        // on below line we are passing our key
                        // and value pair to our parameters.
                        params.put("name", edName.getText().toString());
                        params.put("color", edColor.getText().toString());
                        params.put("price", edPrice.getText().toString());

                        // at last we are
                        // returning our params.
                        return params;
                    }
                };
                queue.add(request);
            }
        });
    }
}

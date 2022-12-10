package com.example.thivolley.screen;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.thivolley.BaseURL;
import com.example.thivolley.R;
import com.example.thivolley.adapter.BackupAdapter;
import com.example.thivolley.model.Moto;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BackupActivity extends AppCompatActivity {

    private Button btnBackup;
    private RecyclerView rcv;
    private BackupAdapter adapter;
    private List<Moto> listMoto = new ArrayList<Moto>();
    FirebaseFirestore db ; ;
    String TAG = "zzzzzzz";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);

        FirebaseApp.initializeApp(this);
        db  = FirebaseFirestore.getInstance();
        btnBackup = findViewById(R.id.btn_backup);
        rcv = findViewById(R.id.rcv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rcv.setLayoutManager(layoutManager);
        adapter = new BackupAdapter(listMoto, this);
        rcv.setAdapter(adapter);
        getListMoto();
        btnBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToFirestore(listMoto);
                Log.d(TAG, "onClick: " + listMoto.size());
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
                                rcv.setAdapter(adapter);
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

    private void addToFirestore(List<Moto> list){
        for (Moto m : list){
            db.collection("MotoVolley")
                    .add( m )
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d("zzzz", "onSuccess: Thêm moto thành công");
                            Log.d(TAG, "onSuccess: " + documentReference.get());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("zzzz", "onFailure: lỗi thêm moto");
                            e.printStackTrace();
                        }
                    });
        }

    }
}

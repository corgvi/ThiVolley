package com.example.thivolley.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.thivolley.BaseURL;
import com.example.thivolley.R;
import com.example.thivolley.model.Moto;
import com.example.thivolley.screen.SuaActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MotoAdapter extends RecyclerView.Adapter<MotoAdapter.MotoViewHolder> {

    private List<Moto> listMoto;
    private Context mContext;

    public MotoAdapter(List<Moto> listMoto, Context mContext) {
        this.listMoto = listMoto;
        this.mContext = mContext;
    }

    public void setData(List<Moto> listMoto) {
        this.listMoto = listMoto;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MotoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_moto, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MotoViewHolder holder, int position) {
        Moto moto = listMoto.get(position);
        holder.tvcolor.setText(moto.getColor());
        holder.tvName.setText(moto.getName());
        holder.tvPrice.setText(moto.getPrice() + "");
        Glide.with(mContext)
                .load(moto.getImage())
                .centerCrop()
                .placeholder(R.drawable.ic_baseline_file_download_off_24)
                .into(holder.img);

        holder.btnSua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SuaActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("moto", moto);
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });

        holder.btnXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog alertDialog = new AlertDialog.Builder(mContext)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Are you sure to Exit")
                        .setMessage("Do you want delete this moto?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                RequestQueue queue = Volley.newRequestQueue(mContext);
                                StringRequest request = new StringRequest(Request.Method.DELETE, BaseURL.BASE_URL_PUT + moto.getId(), new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        getListMoto();
                                        Toast.makeText(mContext, "Delete success", Toast.LENGTH_SHORT).show();
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(mContext, "Delete failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                queue.add(request);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //set what should happen when negative button is clicked
                                Toast.makeText(mContext.getApplicationContext(), "OK", Toast.LENGTH_LONG).show();
                            }
                        })
                        .show();


            }
        });
    }

    @Override
    public int getItemCount() {
        return listMoto.size();
    }

    class MotoViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvcolor;
        ImageView img, btnSua, btnXoa;

        public MotoViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvcolor = itemView.findViewById(R.id.tv_color);
            img = itemView.findViewById(R.id.img);
            btnSua = itemView.findViewById(R.id.btn_sua);
            btnXoa = itemView.findViewById(R.id.btn_xoa);
        }
    }
    private void getListMoto() {
        RequestQueue queue = Volley.newRequestQueue(mContext);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, BaseURL.BASE_URL_GET, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        listMoto.clear();
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
                                notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TAG", "onErrorResponse: " + error.toString());
            }
        });

// Add the request to the RequestQueue.
        queue.add(jsonArrayRequest);
    }
}

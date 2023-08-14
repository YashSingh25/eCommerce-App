package com.example.ecommercepracticeapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ecommercepracticeapp.adapters.CartAdapter;
import com.example.ecommercepracticeapp.databinding.ActivityCheckoutBinding;
import com.example.ecommercepracticeapp.models.ProductModel;
import com.example.ecommercepracticeapp.utils.Constants;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.model.Item;
import com.hishd.tinycart.util.TinyCartHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CheckoutActivity extends AppCompatActivity {
    ActivityCheckoutBinding binding;
    double ttlPrice=0;
    final int tax=11;
    ProgressDialog progressDialog;
    Cart cart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityCheckoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        CartAdapter cartAdapter;
        ArrayList<ProductModel> productModels;
        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Processing");

        cart= TinyCartHelper.getCart();
        productModels=new ArrayList<>();

        for(Map.Entry<Item, Integer> item: cart.getAllItemsWithQty().entrySet())
        {
            ProductModel product= (ProductModel) item.getKey();
            int quantity=item.getValue();
            product.setQuantity(quantity);
            productModels.add(product);
        }

        cartAdapter=new CartAdapter(this, productModels, new CartAdapter.CartListener() {
            @Override
            public void onQuantityChanged() {
                binding.tvSubTotalValue.setText(String.format("₹ %.2f" , cart.getTotalPrice()));
            }
        });
        LinearLayoutManager manager=new LinearLayoutManager(this );
        DividerItemDecoration decoration=new DividerItemDecoration(this , manager.getOrientation());
        binding.rvCartList.setLayoutManager(manager);
        binding.rvCartList.addItemDecoration(decoration);
        binding.rvCartList.setAdapter(cartAdapter);

        binding.tvSubTotalValue.setText(String.format("₹ %.2f" , cart.getTotalPrice()));

        binding.tvTax.setText(tax+"%");

        ttlPrice=(cart.getTotalPrice().doubleValue() * tax /100) + cart.getTotalPrice().doubleValue();
        binding.tvTotalValue.setText("₹ "+ttlPrice);

        binding.btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processOrder();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    void processOrder(){
        progressDialog.show();
        RequestQueue queue= Volley.newRequestQueue(this);

        JSONObject productOrder=new JSONObject();
        JSONObject dataObject=new JSONObject();
        try {

            productOrder.put("address",binding.etAddress.getText().toString());
            productOrder.put("buyer",binding.etName.getText().toString());
            productOrder.put("comment", binding.etAdditionalComments.getText().toString());
            productOrder.put("created_at", Calendar.getInstance().getTimeInMillis());
            productOrder.put("last_update", Calendar.getInstance().getTimeInMillis());
            productOrder.put("date_ship", Calendar.getInstance().getTimeInMillis());
            productOrder.put("email", binding.etEmailAddress.getText().toString());
            productOrder.put("phone", binding.etPhoneNumber.getText().toString());
            productOrder.put("serial", "cab8c1a4e4421a3b");
            productOrder.put("shipping", "");
            productOrder.put("shipping_location", "");
            productOrder.put("shipping_rate", "0.0");
            productOrder.put("status", "WAITING");
            productOrder.put("tax", tax);
            productOrder.put("total_fees", ttlPrice);

            JSONArray product_order_detail=new JSONArray();
            for(Map.Entry<Item, Integer> item: cart.getAllItemsWithQty().entrySet())
            {
                ProductModel product= (ProductModel) item.getKey();
                int quantity=item.getValue();
                product.setQuantity(quantity);

                JSONObject prdctObj=new JSONObject();
                prdctObj.put("product_id",product.getId());
                prdctObj.put("product_name",product.getName());
                prdctObj.put("amount", quantity);
                prdctObj.put("price_item",product.getPrice());
                product_order_detail.put(prdctObj);
            }

                dataObject.put("product_order",productOrder);
                dataObject.put("product_order_detail",product_order_detail);

            Log.e("err", dataObject.toString() );

        } catch (JSONException e) {}


        JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST, Constants.POST_ORDER_URL, dataObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getString("status").equals("success")){
                        Toast.makeText(CheckoutActivity.this, "Order Success", Toast.LENGTH_SHORT).show();
                        String orderNumber=response.getJSONObject("data").getString("code");
                        new AlertDialog.Builder(CheckoutActivity.this)
                                .setTitle("Order Successful")
                                .setMessage("Your Oder Number is: "+orderNumber)
                                .setCancelable(false)
                                .setPositiveButton("Pay Now", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent=new Intent(CheckoutActivity.this , PaymentActivity.class);
                                        intent.putExtra("orderNumber" ,orderNumber);
                                        startActivity(intent);
                                    }
                                }).show();

                    }else{
                        Toast.makeText(CheckoutActivity.this, "Order Failed", Toast.LENGTH_SHORT).show();
                        String orderNumber=response.getJSONObject("data").getString("code");
                        new AlertDialog.Builder(CheckoutActivity.this)
                                .setTitle("Order Failed")
                                .setMessage("Something Went Wrong , Please Try Again")
                                .setCancelable(false)
                                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                }).show();
                    }
                    progressDialog.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String , String> headers=new HashMap<>();
                headers.put("Security" , "secure_code");
                return headers;
            }
        };
        queue.add(request);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
package com.example.ecommercepracticeapp.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ecommercepracticeapp.R;
import com.example.ecommercepracticeapp.databinding.DialogQuantityBinding;
import com.example.ecommercepracticeapp.databinding.SampleCartItemsBinding;
import com.example.ecommercepracticeapp.models.ProductModel;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.util.TinyCartHelper;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    Context context;
    ArrayList<ProductModel> productModels;
    CartListener cartListener;
    Cart cart;

    public interface CartListener {
        public void onQuantityChanged();
    }

    public CartAdapter(Context context, ArrayList<ProductModel> productModels , CartListener cartListener) {
        this.context = context;
        this.productModels = productModels;
        this.cartListener= cartListener;
        cart= TinyCartHelper.getCart();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CartViewHolder(LayoutInflater.from(context).inflate(R.layout.sample_cart_items , parent , false) );
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        ProductModel productModel=productModels.get(position);
        holder.binding.tvCartPrdctName.setText(productModel.getName());
        Glide.with(context).load(productModel.getImage()).into(holder.binding.ivCartPrdctImg);
        holder.binding.tvCartPrdctPrice.setText("₹ "+productModel.getPrice());
        holder.binding.tvCartPrdctQuantity.setText(productModel.getQuantity()+" item(s)");


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogQuantityBinding dialogQuantityBinding= DialogQuantityBinding.inflate(LayoutInflater.from(context));
                AlertDialog dialog=new AlertDialog.Builder(context).
                        setView(dialogQuantityBinding.getRoot()).
                        create();

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));

                dialogQuantityBinding.tvQuantityPrdctNme.setText(productModel.getName());
                dialogQuantityBinding.tvStockQuantity.setText("Stock :"+productModel.getStock());
                dialogQuantityBinding.tvQuantity.setText(String.valueOf(productModel.getQuantity()));
                int stock=productModel.getStock();

                dialogQuantityBinding.btnPlus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int quantity= productModel.getQuantity();
                        quantity++;

                        if(quantity> productModel.getStock()){
                            Toast.makeText(context, "Max Stock Available: "+productModel.getStock(), Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            productModel.setQuantity(quantity);
                            dialogQuantityBinding.tvQuantity.setText(String.valueOf(quantity));
                        }
                        notifyDataSetChanged();
                        cart.updateItem(productModel , productModel.getQuantity());
                        cartListener.onQuantityChanged();
                    }
                });

                dialogQuantityBinding.btnMinus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int quantity= productModel.getQuantity();
                        if(quantity>1){
                            quantity--;
                            productModel.setQuantity(quantity);
                            dialogQuantityBinding.tvQuantity.setText(String.valueOf(quantity));
                            notifyDataSetChanged();
                            cart.updateItem(productModel , productModel.getQuantity());
                            cartListener.onQuantityChanged();
                        }
                    }
                });

                dialogQuantityBinding.btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        notifyDataSetChanged();
                        cart.updateItem(productModel , productModel.getQuantity());
                        cartListener.onQuantityChanged();
                    }
                });
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return productModels.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder{
            SampleCartItemsBinding binding;
        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            binding=SampleCartItemsBinding.bind(itemView);
        }
    }
}

package com.example.ecommercepracticeapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ecommercepracticeapp.R;
import com.example.ecommercepracticeapp.activities.ProductDetailsActivity;
import com.example.ecommercepracticeapp.databinding.SampleCategoryItemsBinding;
import com.example.ecommercepracticeapp.databinding.SampleProductItemsBinding;
import com.example.ecommercepracticeapp.models.ProductModel;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    Context context;
    ArrayList<ProductModel> productModels;

    public ProductAdapter(Context context, ArrayList<ProductModel> productModels) {
        this.context = context;
        this.productModels = productModels;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductViewHolder(LayoutInflater.from(context).inflate(R.layout.sample_product_items , parent , false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ProductModel productModel=productModels.get(position);
        Glide.with(context).load(productModel.getImage()).into(holder.binding.ivProductImage);
        holder.binding.tvProductName.setText(productModel.getName());
        holder.binding.tvProductPrice.setText("₹"+productModel.getPrice());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context , ProductDetailsActivity.class);
                intent.putExtra("name" , productModel.getName());
                intent.putExtra("image" , productModel.getImage());
                intent.putExtra("id" , productModel.getId());
                intent.putExtra("price" , productModel.getPrice());
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return productModels.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        SampleProductItemsBinding binding;
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            binding=SampleProductItemsBinding.bind(itemView);
        }
    }
}

package com.example.ecommercepracticeapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ecommercepracticeapp.R;
import com.example.ecommercepracticeapp.activities.CategoryActivity;
import com.example.ecommercepracticeapp.databinding.SampleCategoryItemsBinding;
import com.example.ecommercepracticeapp.models.CategoryModel;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    Context context;
    ArrayList<CategoryModel> categoryModels;

    public CategoryAdapter(Context context, ArrayList<CategoryModel> categoryModels) {
        this.context = context;
        this.categoryModels = categoryModels;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoryViewHolder(LayoutInflater.from(context).inflate(R.layout.sample_category_items , parent , false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        CategoryModel categoryModel= categoryModels.get(position);
        holder.binding.tvCategoryName.setText(Html.fromHtml(categoryModel.getName()));
        Glide.with(context).load(categoryModel.getIcon()).into(holder.binding.ivCategoryImage);
        holder.binding.ivCategoryImage.setBackgroundColor(Color.parseColor(categoryModel.getColor()));

       holder.itemView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent intent=new Intent(context , CategoryActivity.class);
               intent.putExtra("catId" , categoryModel.getId());
               intent.putExtra("categoryName" , categoryModel.getName());
               context.startActivity(intent);
           }
       });


    }

    @Override
    public int getItemCount() {
        return categoryModels.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        SampleCategoryItemsBinding binding;
        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            binding=SampleCategoryItemsBinding.bind(itemView);

        }
    }
}

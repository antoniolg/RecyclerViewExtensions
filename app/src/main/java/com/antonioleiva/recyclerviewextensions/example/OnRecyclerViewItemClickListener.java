package com.antonioleiva.recyclerviewextensions.example;

import android.view.View;

public interface OnRecyclerViewItemClickListener<Model> {
    public void onItemClick(View view, Model model);
}
package com.antonioleiva.recyclerviewextensions.example;

import android.view.View;

public interface OnRecyclerViewItemClickListener<Model> {
    void onItemClick(View view, Model model);
}
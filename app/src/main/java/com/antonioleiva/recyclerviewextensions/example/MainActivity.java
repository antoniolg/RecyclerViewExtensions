package com.antonioleiva.recyclerviewextensions.example;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    private static final String MOCK_URL = "http://lorempixel.com/800/400/nightlife/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);
        final MyRecyclerAdapter adapter;
        recyclerView.setAdapter(adapter = new MyRecyclerAdapter(createMockList(), R.layout.item));
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter.setOnItemClickListener(new OnRecyclerViewItemClickListener<ViewModel>() {
            @Override
            public void onItemClick(View view, ViewModel viewModel) {
                adapter.remove(viewModel);
            }
        });
    }

    private List<ViewModel> createMockList() {
        List<ViewModel> items = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            items.add(new ViewModel(i, "Item " + (i + 1), MOCK_URL + (i % 10 + 1)));
        }
        return items;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }
}

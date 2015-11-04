package com.smartinnovationtechnology.ishopping;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.twotoasters.jazzylistview.recyclerview.JazzyRecyclerViewScrollListener;

import java.util.ArrayList;
import java.util.List;

import adappters.ProductsAdapter;
import datamodels.Cache;
import datamodels.Category;
import datamodels.Constants;
import datamodels.Product;
import json.JsonReader;
import json.ProductsHandler;
import utils.InternetUtil;
import views.ProgressActivity;

/**
 * Created by Dahman on 7/26/2015.
 */
public class ProductsActivity extends ProgressActivity {
    private Category category;
    private ImageButton buttonIcon;
    private TextView textTile;
    private ProgressBar progressActionBar;
    private RecyclerView recyclerView;
    private List<AsyncTask> tasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initComponents();
    }

    /**
     * method, used to initialize components
     */
    private void initComponents() {
        category = (Category) getIntent().getSerializableExtra(Constants.KEY_CATEGORY);
        buttonIcon = (ImageButton) findViewById(R.id.button_icon);
        textTile = (TextView) findViewById(R.id.text_title);
        progressActionBar = (ProgressBar) findViewById(R.id.progress_actionBar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        tasks = new ArrayList<>();

        // customize actionbar
        buttonIcon.setImageResource(R.drawable.ab_back);
        textTile.setText(category.getTitle() + " " + getString(R.string.products));
        buttonIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // customize recycler view
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        JazzyRecyclerViewScrollListener scrollListener = new JazzyRecyclerViewScrollListener();
        scrollListener.setTransitionEffect(14);
        recyclerView.setOnScrollListener(scrollListener);

        // get cached content
        String response = Cache.getCategoryProductsResponse(this, category.getId());
        // check it
        if (response != null) {
            // valid >> handle it
            ProductsHandler handler = new ProductsHandler(response);
            List<Product> products = handler.handle();

            // check data length
            if (products.size() == 0) {
                // show empty msg
                showEmpty(R.string.no_products_here);
            } else {
                // set recycler adapter
                setProductsAdapter(products);

                showMain();
            }

            // update content from server if possible
            new ProductsTask(true).execute();
        } else {
            // load it from server
            new ProductsTask(false).execute();
        }
    }

    /**
     * method, used to set recycler adapter
     */
    private void setProductsAdapter(final List<Product> products) {
        ProductsAdapter adapter = new ProductsAdapter(this, products, R.layout.recycler_products_item);
        recyclerView.setAdapter(adapter);

        // add listeners
        adapter.setOnItemClickListener(new ProductsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Product product = products.get(position);
                // open product activity
                Intent intent = new Intent(ProductsActivity.this, ProductDetailsActivity.class);
                intent.putExtra(Constants.KEY_PRODUCT, product);
                startActivity(intent);
                overridePendingTransition(R.anim.child_enter, R.anim.parent_exit);
            }
        });
    }

    /**
     * sub class, used to load products from server
     */
    private class ProductsTask extends AsyncTask<Void, Void, Void> {
        private ProductsActivity activity;
        private boolean updateInBackground;
        private String response;

        private ProductsTask(boolean updateInBackground) {
            this.updateInBackground = updateInBackground;
            activity = ProductsActivity.this;

            tasks.add(this); // save reference to this task to destroy it if required
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // check internet connection
            if (!InternetUtil.isConnected(activity)) {
                if (!updateInBackground)
                    // show error
                    showError(R.string.no_internet_connection);

                cancel(true);
                return;
            }

            showProgress();
        }

        @Override
        protected Void doInBackground(Void... params) {
            String url = AppController.END_POINT + "/m_products/" + category.getId();
            JsonReader jsonReader = new JsonReader(url);
            response = jsonReader.sendGetRequest();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // validate response
            if (response == null) {
                if (!updateInBackground)
                    // show error msg
                    showError(R.string.connection_error);

                return;
            }

            // ---response is valid---
            // handle it
            ProductsHandler handler = new ProductsHandler(response);
            List<Product> products = handler.handle();

            // check handling operation result
            if (products == null) {
                if (!updateInBackground)
                    // show error msg
                    showError(R.string.connection_error);

                return;
            }

            // cache it
            Cache.updateCategoryProductsResponse(activity, response, category.getId());

            // check data length
            if (products.size() == 0) {
                // show empty msg
                showEmpty(R.string.no_products_here);

                return;
            }

            // set recycler adapter
            setProductsAdapter(products);

            showMain();
        }
    }

    /**
     * overridden method, used to return layout res id
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.activity_products;
    }

    /**
     * method, used to refresh content
     */
    @Override
    protected void onRefresh() {
        new ProductsTask(false).execute();
    }

    /**
     * overridden method
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.parent_enter, R.anim.child_exit);
    }
}

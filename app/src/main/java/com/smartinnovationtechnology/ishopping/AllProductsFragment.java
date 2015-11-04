package com.smartinnovationtechnology.ishopping;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.twotoasters.jazzylistview.recyclerview.JazzyRecyclerViewScrollListener;

import java.util.ArrayList;
import java.util.List;

import adappters.ProductsAdapter;
import datamodels.Cache;
import datamodels.Constants;
import datamodels.Product;
import json.JsonReader;
import json.ProductsHandler;
import utils.InternetUtil;
import views.ProgressFragment;

/**
 * Created by Shamyyoun on 7/22/2015.
 */
public class AllProductsFragment extends ProgressFragment {
    private MainActivity activity;
    private RecyclerView recyclerView;
    private List<AsyncTask> tasks;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        initComponents(rootView);

        return rootView;
    }

    /**
     * method, used to initialize components
     */
    private void initComponents(ViewGroup rootView) {
        activity = (MainActivity) getActivity();
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        setActionBarProgress(activity.getActionBarProgress());
        tasks = new ArrayList<>();

        // customize recycler view
        GridLayoutManager layoutManager = new GridLayoutManager(activity, 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        JazzyRecyclerViewScrollListener scrollListener = new JazzyRecyclerViewScrollListener();
        scrollListener.setTransitionEffect(14);
        recyclerView.setOnScrollListener(scrollListener);

        // get cached content
        String response = Cache.getAllProductsResponse(activity);
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
            new AllProductsTask(true).execute();
        } else {
            // load it from server
            new AllProductsTask(false).execute();
        }
    }

    /**
     * method, used to set recycler adapter
     */
    private void setProductsAdapter(final List<Product> products) {
        ProductsAdapter adapter = new ProductsAdapter(activity, products, R.layout.recycler_products_item);
        recyclerView.setAdapter(adapter);

        // add listeners
        adapter.setOnItemClickListener(new ProductsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Product product = products.get(position);
                // open product activity
                Intent intent = new Intent(activity, ProductDetailsActivity.class);
                intent.putExtra(Constants.KEY_PRODUCT, product);
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.child_enter, R.anim.parent_exit);
            }
        });
    }

    /**
     * sub class, used to load all products from server
     */
    private class AllProductsTask extends AsyncTask<Void, Void, Void> {
        private boolean updateInBackground;
        private String response;

        private AllProductsTask(boolean updateInBackground) {
            this.updateInBackground = updateInBackground;
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
            String url = AppController.END_POINT + "/m_all_products";
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
            Cache.updateAllProductsResponse(activity, response);

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
        return R.layout.fragment_all_products;
    }

    /**
     * method, used to refresh content
     */
    @Override
    protected void onRefresh() {
        new AllProductsTask(false).execute();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // stop running tasks
        for (AsyncTask task : tasks) {
            task.cancel(true);
        }

        // hide actionbar progress
        activity.getActionBarProgress().setVisibility(View.INVISIBLE);
    }
}

package com.smartinnovationtechnology.ishopping;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ProgressBar;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import datamodels.Cache;
import datamodels.Category;
import datamodels.Product;
import json.CategoriesHandler;
import json.JsonReader;
import json.ProductsHandler;
import utils.InternetUtil;
import utils.ViewUtil;


public class SplashActivity extends ActionBarActivity {
    private ProgressBar progressBar;
    private Runnable runnable;
    private List<Product> topProducts;
    private List<Category> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        // load cached data if exists
        String cachedTopProductsResponse = Cache.getTopProductsResponse(this);
        String cachedCategoriesResponse = Cache.getCategoriesResponse(this);
        topProducts = new ProductsHandler(cachedTopProductsResponse).handle();
        categories = new CategoriesHandler(cachedCategoriesResponse).handle();

        // check internet connection
        if (!InternetUtil.isConnected(this)) {
            endLoading(R.string.no_internet_connection);
        }

        // init runnable class
        runnable = new Runnable() {
            @Override
            public void run() {
                // --get top products from server--
                // create and execute json reader
                String topProductsUrl = AppController.END_POINT + "/m_top_products";
                JsonReader topProductsReader = new JsonReader(topProductsUrl);
                String topProductsResponse = topProductsReader.sendGetRequest();

                // validate and handle response in products list
                if (topProductsResponse == null) {
                    endLoading(R.string.connection_error);
                    return;
                }

                ProductsHandler topProductsHandler = new ProductsHandler(topProductsResponse);
                topProducts = topProductsHandler.handle();

                if (topProducts == null) {
                    endLoading(R.string.connection_error);
                    return;
                }

                // cache top products
                Cache.updateTopProductsResponse(SplashActivity.this, topProductsResponse);

                // --get categories from server--
                // create and execute json reader
                String categoriesUrl = AppController.END_POINT + "/m_all_categories";
                JsonReader categoriesReader = new JsonReader(categoriesUrl);
                String categoriesResponse = categoriesReader.sendGetRequest();

                // validate and handle response in categories list
                if (categoriesResponse == null) {
                    endLoading(R.string.connection_error);
                    return;
                }

                CategoriesHandler categoriesHandler = new CategoriesHandler(categoriesResponse);
                categories = categoriesHandler.handle();

                if (categories == null) {
                    endLoading(R.string.connection_error);
                    return;
                }

                // cache categories
                Cache.updateCategoriesResponse(SplashActivity.this, categoriesResponse);

                // start main activity
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        };

        // start loading thread
        new Thread(runnable).start();
    }

    /**
     * method, used to show error as alert dialog or goto main activity
     */
    private void endLoading(final int errorMsgResId) {
        // check data
        if (topProducts == null || categories == null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // hide progressBar
                    ViewUtil.showView(progressBar, false);

                    // show error msg
                    SweetAlertDialog errorDialog = new SweetAlertDialog(SplashActivity.this, SweetAlertDialog.ERROR_TYPE);
                    errorDialog.setCancelable(false);
                    errorDialog.setTitleText(getString(R.string.connection_error_c));
                    errorDialog.setConfirmText(getString(R.string.close));
                    errorDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismiss();
                            finish();
                        }
                    });

                    errorDialog.show();
                }
            });
        } else {
            // just goto main activity
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }
    }
}

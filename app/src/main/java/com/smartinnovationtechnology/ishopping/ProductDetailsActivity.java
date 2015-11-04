package com.smartinnovationtechnology.ishopping;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.mrengineer13.snackbar.SnackBar;
import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import database.WishListDAO;
import datamodels.Constants;
import datamodels.Product;
import json.JsonReader;
import utils.InternetUtil;

/**
 * Created by Dahman on 7/26/2015.
 */
public class ProductDetailsActivity extends ActionBarActivity implements View.OnClickListener {
    private Product product;
    private int productPosition;
    private WishListDAO wishListDAO;

    // actionbar objects
    private ImageButton buttonIcon;
    private TextView textTitle;

    private ScrollView scrollView;
    private ImageView imageImage;
    private TextView textDesc;
    private TextView textPrice;
    private View viewAddToWishList;
    private ImageView imageHeart;
    private View viewBuy;
    private ImageView imageArrow;
    private View viewBuyContent;
    private EditText textName;
    private EditText textPhone;
    private EditText textEmail;
    private EditText textAddress;
    private Button buttonSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        initComponents();
    }

    /**
     * method, used to initialize components
     */
    private void initComponents() {
        product = (Product) getIntent().getSerializableExtra(Constants.KEY_PRODUCT);
        productPosition = getIntent().getIntExtra(Constants.KEY_PRODUCT_POSITION, 0);
        wishListDAO = new WishListDAO(this);
        buttonIcon = (ImageButton) findViewById(R.id.button_icon);
        textTitle = (TextView) findViewById(R.id.text_title);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        imageImage = (ImageView) findViewById(R.id.image_image);
        textDesc = (TextView) findViewById(R.id.text_desc);
        textPrice = (TextView) findViewById(R.id.text_price);
        viewAddToWishList = findViewById(R.id.view_addToWishList);
        imageHeart = (ImageView) findViewById(R.id.image_heart);
        viewBuy = findViewById(R.id.view_buy);
        imageArrow = (ImageView) findViewById(R.id.image_arrow);
        viewBuyContent = findViewById(R.id.view_buyContent);
        textName = (EditText) findViewById(R.id.text_name);
        textPhone = (EditText) findViewById(R.id.text_phone);
        textEmail = (EditText) findViewById(R.id.text_email);
        textAddress = (EditText) findViewById(R.id.text_address);
        buttonSend = (Button) findViewById(R.id.button_send);

        // customize actionbar
        buttonIcon.setImageResource(R.drawable.ab_back);
        textTitle.setText(R.string.product);

        // set data
        textTitle.setText(product.getTitle());
        textDesc.setText(product.getDesc());
        textPrice.setText(product.getPrice() + " L.E.");
        if (!product.getImage().isEmpty()) {
            Picasso.with(this).load(product.getImage()).into(imageImage);
        }

        // check if item is in wish list
        wishListDAO.open();
        if (wishListDAO.existsInDB(product.getId())) {
            imageHeart.setImageResource(R.drawable.red_heart_icon);
        }
        wishListDAO.close();

        // add listeners
        buttonIcon.setOnClickListener(this);
        buttonSend.setOnClickListener(this);
        viewAddToWishList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check if product is already in wish list
                Intent resultIntent = new Intent();
                wishListDAO.open();
                if (wishListDAO.existsInDB(product.getId())) {
                    // exists >> remove it
                    wishListDAO.delete(product.getId());

                    // show msg
                    new SnackBar.Builder(ProductDetailsActivity.this)
                            .withMessageId(R.string.removed_successfully)
                            .withDuration(SnackBar.MED_SNACK)
                            .show();
                    imageHeart.setImageResource(R.drawable.gray_heart_icon);

                    // set result data
                    resultIntent.putExtra(Constants.KEY_PRODUCT_POSITION, productPosition);
                    resultIntent.putExtra(Constants.KEY_REMOVED_FROM_WISH_LIST, true);
                } else {
                    // not exists >> add it
                    wishListDAO.add(product);

                    // show msg
                    new SnackBar.Builder(ProductDetailsActivity.this)
                            .withMessageId(R.string.added_successfully)
                            .withDuration(SnackBar.MED_SNACK)
                            .show();
                    imageHeart.setImageResource(R.drawable.red_heart_icon);

                    // set result data
                    resultIntent.putExtra(Constants.KEY_PRODUCT_POSITION, productPosition);
                    resultIntent.putExtra(Constants.KEY_REMOVED_FROM_WISH_LIST, false);
                }
                wishListDAO.close();
                setResult(RESULT_OK, resultIntent);
            }
        });
        viewBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewBuyContent.getVisibility() == View.VISIBLE) {
                    viewBuyContent.setVisibility(View.GONE);
                    imageArrow.setImageResource(R.drawable.right_arrow);
                } else {
                    viewBuyContent.setVisibility(View.VISIBLE);
                    imageArrow.setImageResource(R.drawable.bottom_arrow);
                    scrollView.fullScroll(View.FOCUS_DOWN);
                }
            }
        });
    }

    /**
     * method, used to handle click listeners
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_icon:
                onBackPressed();
                break;

            case R.id.button_send:
                new BuyTask().execute();
                break;
        }
    }

    /**
     * overridden method
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.parent_enter, R.anim.child_exit);
    }

    /**
     * async task, used to send buy request to server
     */
    private class BuyTask extends AsyncTask<Void, Void, Void> {
        private ProductDetailsActivity activity;
        SweetAlertDialog dialog;

        private String name;
        private String phone;
        private String email;
        private String address;

        private String response;

        public BuyTask() {
            activity = ProductDetailsActivity.this;
            dialog = new SweetAlertDialog(activity, SweetAlertDialog.PROGRESS_TYPE);
            dialog.getProgressHelper().setBarColor(getResources().getColor(R.color.primary));
            dialog.setCancelable(false);
            dialog.setTitleText(getString(R.string.loading));

            name = textName.getText().toString().trim();
            phone = textPhone.getText().toString().trim();
            email = textEmail.getText().toString().trim();
            address = textAddress.getText().toString().trim();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // validate inputs
            if (name.isEmpty()) {
                textName.setError(getString(R.string.required));
                cancel(true);
                return;
            }
            if (phone.isEmpty()) {
                textPhone.setError(getString(R.string.required));
                cancel(true);
                return;
            }
            if (email.isEmpty()) {
                textEmail.setError(getString(R.string.required));
                cancel(true);
                return;
            }
            if (address.isEmpty()) {
                textAddress.setError(getString(R.string.required));
                cancel(true);
                return;
            }

            // check internet connection
            if (!InternetUtil.isConnected(activity)) {
                // show error
                showError(R.string.no_internet_connection);
                cancel(true);
                return;
            }

            // show progress
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            String url = AppController.END_POINT + "/m_buy_product";
            JsonReader jsonReader = new JsonReader(url);

            // prepare parameters
            List<NameValuePair> parameters = new ArrayList<>(5);
            parameters.add(new BasicNameValuePair("name", name));
            parameters.add(new BasicNameValuePair("phone", phone));
            parameters.add(new BasicNameValuePair("email", email));
            parameters.add(new BasicNameValuePair("address", address));
            parameters.add(new BasicNameValuePair("product_id", "" + product.getId()));
            response = jsonReader.sendPostRequest(parameters);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // check response
            if (Constants.JSON_MSG_SUCCESS.equals(response)) {
                // show success msg
                dialog.setTitleText(getString(R.string.successful_request))
                        .setConfirmText(getString(R.string.close))
                        .setConfirmClickListener(null)
                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                dialog.setCancelable(true);

                // hide buy content layout
                viewBuyContent.setVisibility(View.GONE);
                imageArrow.setImageResource(R.drawable.right_arrow);

                // remove inputs
                textName.setText("");
                textPhone.setText("");
                textEmail.setText("");
                textAddress.setText("");
            } else {
                showError(R.string.connection_error_c);
            }
        }

        /**
         * method, used to show error as snack msg
         */
        private void showError(int msgResId) {
            dialog.setTitleText(getString(msgResId))
                    .setConfirmText(getString(R.string.close))
                    .setConfirmClickListener(null)
                    .changeAlertType(SweetAlertDialog.ERROR_TYPE);
            dialog.setCancelable(true);
        }
    }
}

package com.smartinnovationtechnology.ishopping;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.twotoasters.jazzylistview.recyclerview.JazzyRecyclerViewScrollListener;

import java.util.List;

import adappters.ProductsAdapter;
import database.WishListDAO;
import datamodels.Constants;
import datamodels.Product;
import utils.ViewUtil;

/**
 * Created by Shamyyoun on 7/22/2015.
 */
public class WishListFragment extends Fragment {
    private Activity activity;
    private WishListDAO wishListDAO;
    private List<Product> products;

    private View emptyView;
    private TextView textEmpty;
    private RecyclerView recyclerView;
    private ProductsAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_wish_list, container, false);
        initComponents(rootView);

        return rootView;
    }

    /**
     * method, used to initialize components
     */
    private void initComponents(ViewGroup rootView) {
        activity = getActivity();
        wishListDAO = new WishListDAO(activity);
        emptyView = rootView.findViewById(R.id.view_empty);
        textEmpty = (TextView) emptyView.findViewById(R.id.text_empty);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);

        // get favorite products
        wishListDAO.open();
        products = wishListDAO.getAll();
        wishListDAO.close();

        // check data length
        if (products.size() == 0) {
            // show empty view
            textEmpty.setText(R.string.no_favorites_in_your_wish_list);
            ViewUtil.showView(emptyView, true);
            ViewUtil.showView(recyclerView, false);
        }

        // set recycler adapter
        GridLayoutManager layoutManager = new GridLayoutManager(activity, 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new ProductsAdapter(activity, products, R.layout.recycler_products_item);
        recyclerView.setAdapter(adapter);
        JazzyRecyclerViewScrollListener scrollListener = new JazzyRecyclerViewScrollListener();
        scrollListener.setTransitionEffect(14);
        recyclerView.setOnScrollListener(scrollListener);

        // add listeners
        adapter.setOnItemClickListener(new ProductsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Product product = products.get(position);
                // open product activity
                Intent intent = new Intent(activity, ProductDetailsActivity.class);
                intent.putExtra(Constants.KEY_PRODUCT, product);
                intent.putExtra(Constants.KEY_PRODUCT_POSITION, position);
                startActivityForResult(intent, Constants.REQ_PRODUCT_DETAILS);
                activity.overridePendingTransition(R.anim.child_enter, R.anim.parent_exit);
            }
        });
    }

    /**
     * overridden method
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // check request code
        if (requestCode == Constants.REQ_PRODUCT_DETAILS) {
            // check result code
            if (resultCode == Activity.RESULT_OK) {
                // check if removed
                boolean removed = data.getBooleanExtra(Constants.KEY_REMOVED_FROM_WISH_LIST, false);
                if (removed) {
                    // remove product from grid view adapter
                    int productPosition = data.getIntExtra(Constants.KEY_PRODUCT_POSITION, 0);
                    products.remove(productPosition);
                    adapter.notifyDataSetChanged();

                    // check data length
                    if (products.size() == 0) {
                        // show empty view
                        textEmpty.setText(R.string.no_favorites_in_your_wish_list);
                        ViewUtil.showView(emptyView, true);
                        ViewUtil.showView(recyclerView, false);
                    } else {
                        // show recycler view
                        ViewUtil.showView(emptyView, false);
                        ViewUtil.showView(recyclerView, true);
                    }
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}

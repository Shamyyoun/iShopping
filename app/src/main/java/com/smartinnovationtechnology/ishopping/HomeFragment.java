package com.smartinnovationtechnology.ishopping;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ScrollView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;

import java.util.List;

import adappters.CategoriesAdapter;
import datamodels.Cache;
import datamodels.Category;
import datamodels.Constants;
import datamodels.Product;
import json.CategoriesHandler;
import json.ProductsHandler;
import views.ExpandableHeightListView;

/**
 * Created by Shamyyoun on 7/22/2015.
 */
public class HomeFragment extends Fragment {
    private static final int SLIDER_DURATION = 4000;

    private Activity activity;
    private ScrollView scrollView;
    private SliderLayout slider;
    private ExpandableHeightListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);
        initComponents(rootView);

        return rootView;
    }

    /**
     * method, used to initialize components
     */
    private void initComponents(View rootView) {
        activity = getActivity();
        scrollView = (ScrollView) rootView.findViewById(R.id.scrollView);
        slider = (SliderLayout) rootView.findViewById(R.id.slider);
        listView = (ExpandableHeightListView) rootView.findViewById(R.id.listView);

        // load cached data
        String topProductsResponse = Cache.getTopProductsResponse(activity);
        String categoriesResponse = Cache.getCategoriesResponse(activity);
        List<Product> topProducts = new ProductsHandler(topProductsResponse).handle();
        final List<Category> categories = new CategoriesHandler(categoriesResponse).handle();

        // customize slider
        for (final Product product : topProducts) {
            TextSliderView textSliderView = new TextSliderView(activity);
            textSliderView
                    .description(product.getTitle())
                    .image(product.getImage())
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                        @Override
                        public void onSliderClick(BaseSliderView baseSliderView) {
                            // open product details activity
                            Intent intent = new Intent(activity, ProductDetailsActivity.class);
                            intent.putExtra(Constants.KEY_PRODUCT, product);
                            activity.startActivity(intent);
                            activity.overridePendingTransition(R.anim.child_enter, R.anim.parent_exit);
                        }
                    });

            textSliderView.bundle(new Bundle());
            textSliderView.getBundle().putSerializable(Constants.KEY_PRODUCT, product);

            slider.addSlider(textSliderView);
        }

        slider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        slider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        slider.setCustomAnimation(new DescriptionAnimation());
        slider.setDuration(SLIDER_DURATION);

        // customize listview
        CategoriesAdapter categoriesAdapter = new CategoriesAdapter(activity, R.layout.list_categories_item, categories);
        listView.setAdapter(categoriesAdapter);
        listView.setExpanded(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Category category = categories.get(position);

                // open products activity
                Intent intent = new Intent(activity, ProductsActivity.class);
                intent.putExtra(Constants.KEY_CATEGORY, category);
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.child_enter, R.anim.parent_exit);
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0, 0);
            }
        }, 50);
    }
}

package com.smartinnovationtechnology.ishopping;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {
    public static final int MENU_DRAWER_GRAVITY = Gravity.START;

    private ImageButton buttonIcon;
    private TextView textTitle;
    private ProgressBar progressActionBar;
    private DrawerLayout menuDrawer;
    private View viewRegister;
    private View viewHome;
    private View viewProducts;
    private View viewWishList;
    private View viewContact;

    private int selectedMenuItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();
    }

    /**
     * method, used to initialize components
     */
    private void initComponents() {
        buttonIcon = (ImageButton) findViewById(R.id.button_icon);
        textTitle = (TextView) findViewById(R.id.text_title);
        progressActionBar = (ProgressBar) findViewById(R.id.progress_actionBar);
        menuDrawer = (DrawerLayout) findViewById(R.id.menuDrawer);
        viewRegister = findViewById(R.id.view_register);
        viewHome = findViewById(R.id.view_home);
        viewProducts = findViewById(R.id.view_products);
        viewWishList = findViewById(R.id.view_wishList);
        viewContact = findViewById(R.id.view_contact);

        // customize actionbar
        textTitle.setText(R.string.home);

        // add listeners
        buttonIcon.setOnClickListener(this);
        viewRegister.setOnClickListener(this);
        viewHome.setOnClickListener(this);
        viewProducts.setOnClickListener(this);
        viewWishList.setOnClickListener(this);
        viewContact.setOnClickListener(this);

        // load home fragment
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.container_main, new HomeFragment());
        ft.commit();
        selectedMenuItemId = R.id.view_home;
    }

    /**
     * overridden method
     */
    @Override
    public void onBackPressed() {
        if (menuDrawer.isDrawerOpen(MENU_DRAWER_GRAVITY)) {
            menuDrawer.closeDrawer(MENU_DRAWER_GRAVITY);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * overridden method, used to handle click events
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_icon) {
            if (menuDrawer.isDrawerOpen(MENU_DRAWER_GRAVITY))
                menuDrawer.closeDrawer(MENU_DRAWER_GRAVITY);
            else
                menuDrawer.openDrawer(MENU_DRAWER_GRAVITY);
        } else if (v.getId() != selectedMenuItemId) { // ensure not same choice
            // check selected item
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            switch (v.getId()) {
                case R.id.view_register:
                    textTitle.setText(R.string.register);
                    ft.replace(R.id.container_main, new RegisterFragment());
                    break;

                case R.id.view_home:
                    textTitle.setText(R.string.home);
                    ft.replace(R.id.container_main, new HomeFragment());
                    break;

                case R.id.view_products:
                    textTitle.setText(R.string.all_products);
                    ft.replace(R.id.container_main, new AllProductsFragment());
                    break;

                case R.id.view_wishList:
                    textTitle.setText(R.string.wish_list);
                    ft.replace(R.id.container_main, new WishListFragment());
                    break;

                case R.id.view_contact:
                    textTitle.setText(R.string.contact);
                    ft.replace(R.id.container_main, new ContactFragment());
                    break;
            }
            ft.commit();
            selectedMenuItemId = v.getId();
            menuDrawer.closeDrawer(MENU_DRAWER_GRAVITY);
        } else {
            // just close menu drawer
            menuDrawer.closeDrawer(MENU_DRAWER_GRAVITY);
        }
    }

    /**
     * method, used to set selected menuItem id
     */
    public void setSelectedMenuItemId(int selectedMenuItemId) {
        this.selectedMenuItemId = selectedMenuItemId;
    }

    /**
     * method, used to show or hide progress bar in action bar
     */
    public void showActionBarProgress(boolean show) {
        progressActionBar.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * getter
     */
    public ProgressBar getActionBarProgress() {
        return progressActionBar;
    }
}

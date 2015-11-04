package views;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.devspark.appmsg.AppMsg;
import com.smartinnovationtechnology.ishopping.R;

import utils.ViewUtil;

public abstract class ProgressActivity extends ActionBarActivity {
    // constants for view states
    private static final int VIEW_STATE_MAIN = 1;
    private static final int VIEW_STATE_PROGRESS = 2;
    private static final int VIEW_STATE_ERROR = 3;
    private static final int VIEW_STATE_EMPTY = 4;

    private int viewState; // used to save current visible view state

    // main views
    private ViewGroup mainView;
    private View progressView;
    private View errorView;
    private View emptyView;
    private ProgressBar progressActionBar;

    // error view components
    private ImageButton buttonRefresh;
    private TextView textError;
    private String errorMsg;

    // empty view components
    private TextView textEmpty;
    private String emptyMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());

        // init components
        mainView = (ViewGroup) findViewById(R.id.view_main);
        progressView = findViewById(R.id.view_progress);
        errorView = findViewById(R.id.view_error);
        emptyView = findViewById(R.id.view_empty);
        progressActionBar = (ProgressBar) findViewById(R.id.progress_actionBar);
        buttonRefresh = (ImageButton) errorView.findViewById(R.id.button_refresh);
        textError = (TextView) errorView.findViewById(R.id.text_error);
        textEmpty = (TextView) emptyView.findViewById(R.id.text_empty);

        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRefresh();
            }
        });
    }

    /**
     * abstract method to pass layout resource id from children
     */
    protected abstract int getLayoutResource();

    /*
    * method used to show main viewState
    */
    protected void showMain() {
        // hide error view if it is visible
        ViewUtil.showView(errorView, false);
        // hide all AppMsgs
        AppMsg.cancelAll(this);

        // hide progress view if it is visible
        ViewUtil.showView(progressView, false);
        ViewUtil.showView(progressActionBar, false, View.INVISIBLE);

        // hide empty view if visible
        ViewUtil.showView(emptyView, false);

        // show main view
        ViewUtil.showView(mainView, true);
        // update view state
        viewState = VIEW_STATE_MAIN;
    }

    /*
     * method used to show progress if it is possible
     */
    protected void showProgress() {
        // check to ensure main view is not visible
        if (viewState != VIEW_STATE_MAIN) {
            // not visible, so hide all views
            ViewUtil.showView(mainView, false);
            ViewUtil.showView(errorView, false);
            ViewUtil.showView(emptyView, false);
            ViewUtil.showView(progressActionBar, false, View.INVISIBLE);
            // and show progress view
            ViewUtil.showView(progressView, true);

            // update view state
            viewState = VIEW_STATE_PROGRESS;
        } else {
            // main view is visible >> hide all AppMsgs and swipe layout will show its progress
            AppMsg.cancelAll(this);
            // show actionbar progress
            ViewUtil.showView(progressActionBar, true);
        }
    }

    /*
     * overloaded method used to show error with default msg
     */
    protected void showError() {
        errorMsg = getString(R.string.error_loading_data);
        // set error text
        textError.setText(errorMsg);

        // show the suitable error style
        showTheError();
    }

    /*
     * overloaded method used to show error with String msg
     */
    protected void showError(String errorMsg) {
        this.errorMsg = errorMsg;
        // set error text
        textError.setText(errorMsg);

        // show the suitable error style
        showTheError();
    }

    /*
     * overloaded method used to show error with msg resource id
     */
    protected void showError(int errorMsgResource) {
        errorMsg = getString(errorMsgResource);
        // set error text
        textError.setText(errorMsgResource);

        // show the suitable error style
        showTheError();
    }

    /*
     * method used to show the suitable error msg
     */
    private void showTheError() {
        // check if main view is visible
        if (viewState == VIEW_STATE_MAIN) {
            // and hide all other AppMsgs
            AppMsg.cancelAll(this);
            // and hide actionbar progress
            ViewUtil.showView(progressActionBar, false, View.INVISIBLE);
            // and just show error in AppMsg
            AppMsg appMsg = AppMsg.makeText(this, errorMsg, AppMsg.STYLE_CONFIRM);
            appMsg.setParent(mainView);
            appMsg.show();
        } else {
            // main view is not visible, so hide all views
            ViewUtil.showView(mainView, false);
            ViewUtil.showView(progressView, false);
            ViewUtil.showView(emptyView, false);
            ViewUtil.showView(progressActionBar, false, View.INVISIBLE);

            // and show error view
            ViewUtil.showView(errorView, true);

            // update view state
            viewState = VIEW_STATE_ERROR;
        }
    }

    /*
     * overloaded method used to show empty view with default msg
     */
    protected void showEmpty() {
        emptyMsg = getString(R.string.no_data);
        // set empty text
        textEmpty.setText(emptyMsg);

        // show the empty view
        showTheEmpty();
    }

    /*
     * overloaded method used to show empty view with String msg
     */
    protected void showEmpty(String emptyMsg) {
        this.emptyMsg = emptyMsg;
        // set empty text
        textEmpty.setText(emptyMsg);

        // show the empty view
        showTheEmpty();
    }

    /*
     * overloaded method used to show empty view with msg resource id
     */
    protected void showEmpty(int emptyMsgResource) {
        emptyMsg = getString(emptyMsgResource);
        // set empty text
        textEmpty.setText(emptyMsgResource);

        // show the empty view
        showTheEmpty();
    }

    /*
     * method used to show the empty view
     */
    private void showTheEmpty() {
        // hide all views
        ViewUtil.showView(mainView, false);
        ViewUtil.showView(progressView, false);
        ViewUtil.showView(errorView, false);
        ViewUtil.showView(progressActionBar, false, View.INVISIBLE);

        // and show empty view
        ViewUtil.showView(emptyView, true);

        // update view state
        viewState = VIEW_STATE_EMPTY;
    }


    /**
     * abstract method to override in children to do refresh operation
     */
    protected abstract void onRefresh();
}

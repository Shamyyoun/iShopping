package datamodels;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Shamyyoun on 3/29/2015.
 */
public class Cache {
    /**
     * method used to update top products response in SP
     */
    public static void updateTopProductsResponse(Context context, String value) {
        updateCachedString(context, Constants.SP_RESPONSES, Constants.SP_KEY_TOP_PRODUCTS_RESPONSE, value);
    }

    /**
     * method used to get top products response from SP
     */
    public static String getTopProductsResponse(Context context) {
        return getCachedString(context, Constants.SP_RESPONSES, Constants.SP_KEY_TOP_PRODUCTS_RESPONSE);
    }

    /**
     * method used to update categories response in SP
     */
    public static void updateCategoriesResponse(Context context, String value) {
        updateCachedString(context, Constants.SP_RESPONSES, Constants.SP_KEY_CATEGORIES_RESPONSE, value);
    }

    /**
     * method used to get categories response from SP
     */
    public static String getCategoriesResponse(Context context) {
        return getCachedString(context, Constants.SP_RESPONSES, Constants.SP_KEY_CATEGORIES_RESPONSE);
    }

    /**
     * method used to update all products response in SP
     */
    public static void updateAllProductsResponse(Context context, String value) {
        updateCachedString(context, Constants.SP_RESPONSES, Constants.SP_KEY_ALL_PRODUCTS_RESPONSE, value);
    }

    /**
     * method used to get all products response from SP
     */
    public static String getAllProductsResponse(Context context) {
        return getCachedString(context, Constants.SP_RESPONSES, Constants.SP_KEY_ALL_PRODUCTS_RESPONSE);
    }

    /**
     * method used to update category products response in SP
     */
    public static void updateCategoryProductsResponse(Context context, String value, int categoryId) {
        updateCachedString(context, Constants.SP_RESPONSES, categoryId + Constants.SP_KEY_PRODUCTS_RESPONSE, value);
    }

    /**
     * method used to get category products response from SP
     */
    public static String getCategoryProductsResponse(Context context, int categoryId) {
        return getCachedString(context, Constants.SP_RESPONSES, categoryId + Constants.SP_KEY_PRODUCTS_RESPONSE);
    }


    /*
     * method, used to update string value in SP
     */
    private static void updateCachedString(Context context, String spName, String valueName, String value) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(valueName, value);
        editor.commit();
    }

    /*
     * method, used to get cached String from SP
     */
    private static String getCachedString(Context context, String spName, String valueName) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        String value = sp.getString(valueName, null);

        return value;
    }
}

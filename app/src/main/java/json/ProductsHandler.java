package json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import datamodels.Product;

public class ProductsHandler {
    private String response;

    public ProductsHandler(String response) {
        this.response = response;
    }

    public List<Product> handle() {
        try {
            JSONArray jsonArray = new JSONArray(response);
            List<Product> products = new ArrayList<Product>(jsonArray.length());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Product product = handleProduct(jsonObject);

                products.add(product);
            }
            return products;
        } catch (Exception e) {
            return null;
        }

    }

    private Product handleProduct(JSONObject jsonObject) {
        Product product;
        try {
            int id = jsonObject.getInt("id");
            String title = jsonObject.getString("name");
            String desc = jsonObject.getString("disc");
            int price = jsonObject.getInt("price");
            int discount = jsonObject.getInt("discount");
            String image = jsonObject.getString("logo");
            int categoryId = jsonObject.getInt("categories_id");

            product = new Product(id);
            product.setTitle(title);
            product.setDesc(desc);
            product.setPrice(price);
            product.setDiscount(discount);
            product.setImage(image);
            product.setCategoryId(categoryId);

        } catch (JSONException e) {
            product = null;
            e.printStackTrace();
        }

        return product;
    }
}

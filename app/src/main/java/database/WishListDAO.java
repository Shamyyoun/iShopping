package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import datamodels.Product;

public class WishListDAO {

    private SQLiteDatabase database;
    private DatabaseSQLiteHelper dbHelper;

    public WishListDAO(Context context) {
        dbHelper = new DatabaseSQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    /**
     * method, used to add item to database
     */
    public void add(Product item) {
        ContentValues values = new ContentValues();
        values.put(DatabaseSQLiteHelper.WISH_LIST_ID, item.getId());
        values.put(DatabaseSQLiteHelper.WISH_LIST_TITLE, item.getTitle());
        values.put(DatabaseSQLiteHelper.WISH_LIST_DESC, item.getDesc());
        values.put(DatabaseSQLiteHelper.WISH_LIST_PRICE, item.getPrice());
        values.put(DatabaseSQLiteHelper.WISH_LIST_DISCOUNT, item.getDiscount());
        values.put(DatabaseSQLiteHelper.WISH_LIST_IMAGE, item.getImage());
        values.put(DatabaseSQLiteHelper.WISH_LIST_CATEGORY_ID, item.getCategoryId());

        database.insert(DatabaseSQLiteHelper.TABLE_WISH_LIST, null, values);
    }

    /**
     * method, used to delete item from database filtered by id
     */
    public void delete(int id) {
        database.delete(DatabaseSQLiteHelper.TABLE_WISH_LIST, DatabaseSQLiteHelper.WISH_LIST_ID + " = " + id, null);
    }

    /**
     * method, used to delete all items from database
     */
    public void deleteAll() {
        database.delete(DatabaseSQLiteHelper.TABLE_WISH_LIST, null, null);
    }

    /**
     * method, used to get all items from db
     */
    public List<Product> getAll() {
        List<Product> products = new ArrayList<Product>();

        Cursor cursor = database.query(DatabaseSQLiteHelper.TABLE_WISH_LIST, null, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Product product = cursorToVehicle(cursor);
            products.add(product);

            cursor.moveToNext();
        }
        cursor.close();
        return products;
    }

    /**
     * method, used to get item values from cursor row
     */
    private Product cursorToVehicle(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(DatabaseSQLiteHelper.WISH_LIST_ID));
        String title = cursor.getString(cursor.getColumnIndex(DatabaseSQLiteHelper.WISH_LIST_TITLE));
        String desc = cursor.getString(cursor.getColumnIndex(DatabaseSQLiteHelper.WISH_LIST_DESC));
        int price = cursor.getInt(cursor.getColumnIndex(DatabaseSQLiteHelper.WISH_LIST_PRICE));
        int discount = cursor.getInt(cursor.getColumnIndex(DatabaseSQLiteHelper.WISH_LIST_DISCOUNT));
        String image = cursor.getString(cursor.getColumnIndex(DatabaseSQLiteHelper.WISH_LIST_IMAGE));
        int categoryId = cursor.getInt(cursor.getColumnIndex(DatabaseSQLiteHelper.WISH_LIST_CATEGORY_ID));

        Product product = new Product(id);
        product.setTitle(title);
        product.setDesc(desc);
        product.setPrice(price);
        product.setDiscount(discount);
        product.setImage(image);
        product.setCategoryId(categoryId);

        return product;
    }

    /**
     * method, used to check if database has items or not based on count
     */
    public boolean hasItems() {
        Cursor mCount = database.rawQuery("SELECT COUNT(" + DatabaseSQLiteHelper.WISH_LIST_ID +
                ") FROM " + DatabaseSQLiteHelper.TABLE_WISH_LIST, null);
        mCount.moveToFirst();
        int count = mCount.getInt(0);
        mCount.close();

        if (count == 0)
            return false;
        else
            return true;
    }

    /**
     * method, used to check if item exists in database or not
     */
    public boolean existsInDB(int id) {
        Cursor mCount = database.rawQuery("SELECT COUNT(" + DatabaseSQLiteHelper.WISH_LIST_ID
                + ") FROM " + DatabaseSQLiteHelper.TABLE_WISH_LIST
                + " WHERE " + DatabaseSQLiteHelper.WISH_LIST_ID + " = " + id, null);
        mCount.moveToFirst();
        int count = mCount.getInt(0);
        mCount.close();

        if (count == 0)
            return false;
        else
            return true;
    }
}
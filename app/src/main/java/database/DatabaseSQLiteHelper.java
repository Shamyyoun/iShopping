package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseSQLiteHelper extends SQLiteOpenHelper {
    private Context context;

    // database info
    private static final String DATABASE_NAME = "ishopping.db";
    private static final int DATABASE_VERSION = 3;

    // table wish list
    public static final String TABLE_WISH_LIST = "wish_list";
    public static final String WISH_LIST_ID = "_id";
    public static final String WISH_LIST_TITLE = "title";
    public static final String WISH_LIST_DESC = "_desc";
    public static final String WISH_LIST_PRICE = "price";
    public static final String WISH_LIST_DISCOUNT = "discount";
    public static final String WISH_LIST_IMAGE = "image";
    public static final String WISH_LIST_CATEGORY_ID = "category_id";

    // tables creation
    private static final String VEHICLES_CREATE = "CREATE TABLE " + TABLE_WISH_LIST
            + "("
            + WISH_LIST_ID + " INTEGER PRIMARY KEY, "
            + WISH_LIST_TITLE + " TEXT NOT NULL, "
            + WISH_LIST_DESC + " TEXT, "
            + WISH_LIST_PRICE + " INTEGER NOT NULL, "
            + WISH_LIST_DISCOUNT + " INTEGER NOT NULL, "
            + WISH_LIST_IMAGE + " TEXT, "
            + WISH_LIST_CATEGORY_ID + " INTEGER NOT NULL"
            + ");";

    public DatabaseSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        // create tables
        database.execSQL(VEHICLES_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WISH_LIST);
        onCreate(db);
    }

}
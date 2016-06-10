package database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by parijathar on 6/3/2016.
 */
public class DatabaseProvider extends ContentProvider {

    private static final String DB_NAME = "findots.db";
    private static final int DB_VERSION = 2;
    private DBHelper mDBHelper = null;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        //sUriMatcher.addURI(Constants.AUTHORITY, Constants.TABLENAME_LOGIN, Constants.Login);

    }

    public class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context mContext) {
            super(mContext, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            createTable(db);
            long avilablesize = db.getMaximumSize();
            db.setMaximumSize(avilablesize);
        }

        public void createTable(SQLiteDatabase db) {
            long avilablesize = db.getMaximumSize();
            db.setMaximumSize(avilablesize);

            /**
             *  create table query
             */

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            if (newVersion > oldVersion) {

                /**
                 *   delete old tables
                 */
                //db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLENAME_LOGIN);

                onCreate(db);
            }
        }
    }

    @Override
    public boolean onCreate() {
        mDBHelper = new DBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int urimatch = sUriMatcher.match(uri);

        switch (urimatch) {
            default:break;
        }

        return 0;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int urimatch = sUriMatcher.match(uri);

        switch (urimatch) {
            default:
                break;
        }

        return null;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        int urimatch = sUriMatcher.match(uri);

        switch (urimatch) {
            default:
                break;
        }

        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        int urimatch = sUriMatcher.match(uri);

        switch (urimatch) {
            default:
                break;
        }
        return 0;
    }
}

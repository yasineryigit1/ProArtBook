package com.ossovita.proartbook;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQuery;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.proto.ProtoOutputStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.net.URL;
import java.util.HashMap;

public class ArtContentProvider extends ContentProvider {

    static final String PROVIDER_NAME="com.ossovita.proartbook.ArtContentProvier";
    static final String URL = "content://" + PROVIDER_NAME + "/arts";
    //Uri yi oluşturduk
    static final Uri CONTENT_URI = Uri.parse(URL);

    static final String NAME = "name";
    static final String IMAGE = "image";

    static final int ARTS = 1;
    static final UriMatcher uriMatcher;

    //ArtContentProvider sınıfından bir değişkene ulaştığımızda bu static okunur
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME,"arts",ARTS);
    }
    private static HashMap<String,String> ART_PROJECTION_MAP;
     //---------------------DATABASE--------------------------------
    //db static değil yazmış
    private static SQLiteDatabase db;
    static final String DATABASE_NAME = "Arts";
    static final String ARTS_TABLE_NAME = "arts";
    static final int DATABASE_VERSION = 1;
    static final String CREATE_DATABASE_TABLE = "CREATE TABLE " +
            ARTS_TABLE_NAME + "(name TEXT NOT NULL," +
            "image BLOB NOT NULL)";

    private static class DatabaseHelper extends SQLiteOpenHelper{


        public DatabaseHelper(@Nullable Context context) {
            super(context, DATABASE_NAME,null,DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_DATABASE_TABLE);//tablo açma komutunu gitdik

        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            //upgrade edeceksek önce mevcut tabloyu ortadan kaldırırız
            db.execSQL("DROP TABLE IF EXISTS " + ARTS_TABLE_NAME);
            //yeni db database oluştur
            onCreate(sqLiteDatabase);
        }
    }

    //---------------------DATABASE--------------------------------



    @Override
    public boolean onCreate() {
        //DatabaseHelper objesi oluşturmak için constructor'ı context istiyordu
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        db   = dbHelper.getWritableDatabase();
        //database oluşturulduysa true döndürür
        return db!=null;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {

        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();
        sqLiteQueryBuilder.setTables(ARTS_TABLE_NAME);

        switch (uriMatcher.match(uri)){
            case ARTS:
                sqLiteQueryBuilder.setProjectionMap(ART_PROJECTION_MAP);
                break;

            default://
        }
        if(s1==null || s1.matches("")){
            s1=NAME;
        }
        Cursor cursor = sqLiteQueryBuilder.query(db,strings,s,strings1,null,null,s1);
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        long rowId = db.insert(ARTS_TABLE_NAME,"",contentValues);
        if(rowId>0){
            Uri newUri = ContentUris.withAppendedId(CONTENT_URI,rowId);
            getContext().getContentResolver().notifyChange(newUri,null);
            return newUri;
        }
        throw new SQLException("Error!");
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}

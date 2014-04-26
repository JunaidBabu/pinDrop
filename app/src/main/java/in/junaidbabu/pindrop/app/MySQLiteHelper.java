package in.junaidbabu.pindrop.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by neo on 5/3/14.
 */

public class MySQLiteHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "LocationDB";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create location table
        String CREATE_location_TABLE = "CREATE TABLE locations ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, "+
                "location TEXT, "+
                "url TEXT )";
        String CREATE_tags_TABLE = "CREATE TABLE tags ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, "+
                "UNIQUE(title))";

        String CREATE_item_tags_TABLE = "CREATE TABLE location_tags ( " +
                "location_id INTEGER, "+
                "tag TEXT )";

        // create locations table
        db.execSQL(CREATE_location_TABLE);
        db.execSQL(CREATE_tags_TABLE);
        db.execSQL(CREATE_item_tags_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older locations table if existed
        db.execSQL("DROP TABLE IF EXISTS locations");

        // create fresh locations table
        this.onCreate(db);
    }

    //---------------------------------------------------------------------

    /**
     * CRUD operations (create "add", read "get", update, delete) book + get all books + delete all books
     */

    // Books table name
    private static final String TABLE_LOCATIONS = "locations";

    // Books Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_URL = "url";

    private static final String[] COLUMNS = {KEY_ID,KEY_TITLE,KEY_LOCATION, KEY_URL};
    private static final String[] COLUMNS_tags = {"location_id", "tag"};

    public String getTags(int location_id){
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query("location_tags", // a. table
                        COLUMNS_tags , // b. column names
                        " location_id = ?", // c. selections
                        new String[] { String.valueOf(location_id) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        String ar="";
        if (cursor.moveToFirst()) {
            do {
                ar+=cursor.getString(1)+",";

            } while (cursor.moveToNext());
        }
        return ar;
    }
    public String[] getTags(){
        String query = "SELECT  * FROM " + "tags";

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        List<String> myList = new ArrayList<String>();
        if (cursor.moveToFirst()) {
            do {
                myList.add(cursor.getString(1));

            } while (cursor.moveToNext());
        }
        String[] arr = myList.toArray(new String[myList.size()]);
        return arr;
    }
    public void createTag(String[] tag, int location_id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("location_tags",
                "location_id = ?",
                new String[] { String.valueOf(location_id) });
        for(int i=0;i<tag.length;i++){
            ContentValues values = new ContentValues();
            values.put(KEY_TITLE, tag[i]);
            ContentValues tags = new ContentValues();
            if(tag[i].length()>0){
            tags.put("location_id", location_id);
            tags.put("tag", tag[i]);
            }

            try{

            db.insert("tags", // table
                    null, //nullColumnHack
                    values); // key/value -> keys = column names/ values = column values

            }catch (Exception e){
                Log.w("SQL Error", "Mostly not unique stuff, lite only!");
            }
            try{

                db.insert("location_tags", // table
                        null, //nullColumnHack
                        tags); // key/value -> keys = column names/ values = column values

            }catch (Exception e){
                Log.w("SQL Error", "Mostly not unique stuff, lite only!");
            }
        }


        db.close();
    }
    public void addLocation(DataClass location){
        //Log.d("addBook", location.toString());
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, location.getTitle());
        values.put(KEY_LOCATION, location.getLocation());
        values.put(KEY_URL, location.getUrl());

        // 3. insert
        db.insert(TABLE_LOCATIONS, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    public List<DataClass> getLocation(String tag){

        List<DataClass> books = new LinkedList<DataClass>();
        Log.i("tag",tag);
        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_LOCATIONS;
        SQLiteDatabase db = this.getWritableDatabase();
        //assert db != null;
        Cursor cursor =
                db.query("location_tags", // a. table
                        COLUMNS_tags, // b. column names
                        "tag=?", // c. selections
                        new String[] { tag }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null);
        // 2. get reference to writable DB

        Cursor cursor_loc = db.rawQuery(query, null);

        ArrayList<Integer> loc_ids = new ArrayList<Integer>();
        // 3. go over each row, build book and add it to list
        DataClass book = new DataClass();
        if (cursor.moveToFirst()) {
            do {
                loc_ids.add(Integer.parseInt(cursor.getString(0)));
            } while (cursor.moveToNext());
        }
        if(loc_ids.size()>0)
            Log.i("loc_ids",loc_ids.get(0).toString());
        if (cursor_loc.moveToFirst()) {
            do {
                try{
                    if(Arrays.asList(loc_ids).contains(Integer.parseInt(cursor.getString(0)))){

                    book.setId(Integer.parseInt(cursor.getString(0)));
                    book.setTitle(cursor.getString(1));
                    book.setLocation(cursor.getString(2));
                    book.setUrl(cursor.getString(3));

                    // Add book to books
                    books.add(book);

                    }
                }catch (Exception e){

                }
            } while (cursor.moveToNext());
        }

        //Log.d("getAllBooks()", books.toString());

        return books;
    }

    public DataClass getLocation(int id){

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(TABLE_LOCATIONS, // a. table
                        COLUMNS, // b. column names
                        " id = ?", // c. selections
                        new String[] { String.valueOf(id) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        // 4. build book object
        DataClass book = new DataClass();
        book.setId(Integer.parseInt(cursor.getString(0)));
        book.setTitle(cursor.getString(1));
        book.setLocation(cursor.getString(2));
        book.setUrl(cursor.getString(3));

        Log.d("getBook("+id+")", book.toString());

        // 5. return book
        return book;
    }

    // Get All Books
    public List<DataClass> getAllLocations() {
        List<DataClass> books = new LinkedList<DataClass>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_LOCATIONS;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        DataClass book = null;
        if (cursor.moveToFirst()) {
            do {
                book = new DataClass();
                book.setId(Integer.parseInt(cursor.getString(0)));
                book.setTitle(cursor.getString(1));
                book.setLocation(cursor.getString(2));
                book.setUrl(cursor.getString(3));

                // Add book to books
                books.add(book);
            } while (cursor.moveToNext());
        }

        //Log.d("getAllBooks()", books.toString());

        // return books

        return books;
    }

    // Updating single book
    public int updateLocation(DataClass book) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, book.getTitle());
        values.put(KEY_LOCATION, book.getLocation());
        values.put(KEY_URL, book.getUrl());

        // 3. updating row
        int i = db.update(TABLE_LOCATIONS, //table
                values, // column/value
                KEY_ID+" = ?", // selections
                new String[] { String.valueOf(book.getId()) }); //selection args

        // 4. close
        db.close();

        return i;

    }

    // Deleting single book
    public void deleteLocation(int id) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(TABLE_LOCATIONS,
                KEY_ID+" = ?",
                new String[] { String.valueOf(id) });

        // 3. close
        db.close();

        //Log.d("deleteBook", book.toString());

    }
}
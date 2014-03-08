package in.junaidbabu.pindrop.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;

import android.view.ContextMenu;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    TextView t;
    MySQLiteHelper db;
    List<DataClass> list;
    ListView myListView;

    public void RefreshList(){

        list = db.getAllLocations();
        if(list.size()==0){
            t.setText("Share Pins from Google Maps to pinDrop");
            return;
        }
        t.setText("");

        List<RowItem> rowItems;
        rowItems = new ArrayList<RowItem>();

        for(int i = 0; i<list.size();i++){
            RowItem item = new RowItem(Integer.toString(i+1),
                    list.get(i).getTitle().toString(),
                    list.get(i).getLocation().toString(),
                    list.get(i).getUrl().toString());
            rowItems.add(item);
            //myStringArray1.add(list.get(i).getTitle().toString()+"\n"+list.get(i).getLocation().toString()+"\n"+list.get(i).getUrl().toString());
        }
        CustomListViewAdapter adapter = new CustomListViewAdapter(this,
                R.layout.list_item, rowItems);

        //myStringArray1.add("something");
        //ArrayAdapter<String> myarrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myStringArray1);
        myListView.setAdapter(adapter);
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(list.get(position).getUrl().toString()));

                 try{
                    startActivity(i);
                 }catch(Exception e){
                     Toast.makeText(MainActivity.this,"Something went wrong! :(", Toast.LENGTH_SHORT).show();
                 }

                //Toast.makeText(MainActivity.this, list.get(position).getUrl().toString(), Toast.LENGTH_SHORT).show();
            }
        });
//<<<<<<< Updated upstream
        registerForContextMenu(myListView);
        /*myListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
=======
        final ActionMode.Callback modeCallBack = new ActionMode.Callback() {

            public boolean onPrepareActionMode(ActionMode mode, Menu menu){
                return false;
            }


            public void onDestroyActionMode(ActionMode mode) {
                mode = null;
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return true;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

                int id = item.getItemId();
                switch (id) {
                    case R.id.action_delete: {
                        //aAdpt.remove( aAdpt.getItem(aAdpt.currentSelection) );
                        //mode.finish();
                        Toast.makeText(MainActivity.this, "something", Toast.LENGTH_SHORT).show();

                        break;
                    }
                    default:
                        return false;

                }
                return false;
            }
        };
        myListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick (AdapterView parent, View view, int position, long id) {
                //System.out.println("Long click");
                startActionMode(modeCallBack);
                view.setSelected(true);
                return true;
            }
        });


       /* myListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
>>>>>>> Stashed changes
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Delete entry")
                        .setMessage("Are you sure you want to delete this entry?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                db.deleteLocation(list.get(position).getId());
                                RefreshList();
                                // continue with delete
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        }).show();
                return true;
            }
        });
        */
    }
//<<<<<<< Updated upstream

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);


        menu.setHeaderTitle(v.getId());
        menu.add(0, v.getId(), 0, "Edit");
        menu.add(0, v.getId(), 0, "Copy URL");
        menu.add(0, v.getId(), 0, "Delete");
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        if(item.getTitle().equals("Edit")){editItem(info.position);}
        else if(item.getTitle().equals("Delete")){deleteItem(info.position);}
        else if(item.getTitle().equals("Copy URL")){copyItem(item.getItemId());}
        else {return false;}
        return true;
    }

    private void copyItem(int itemId) {

    }

    private void deleteItem(final int position) {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Delete entry")
                .setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        db.deleteLocation(list.get(position).getId());
                        RefreshList();
                        // continue with delete
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                }).show();
    }

    void editItem(int position) {

    }

    public void function1(int id){
        Toast.makeText(this, "function 1 called"+Integer.toString(id), Toast.LENGTH_SHORT).show();
    }
    public void function2(int id){
        Toast.makeText(this, "function 2 called", Toast.LENGTH_SHORT).show();
    }

    private AdView adView;
    private static final String AD_UNIT_ID = "a15318ef8b15a1f";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        myListView = (ListView) findViewById(R.id.listView);
        db = new MySQLiteHelper(this);

        adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(AD_UNIT_ID);
        LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
        layout.addView(adView);
        AdRequest adRequest = new AdRequest.Builder()
               // .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
               // .addTestDevice("INSERT_YOUR_HASHED_DEVICE_ID_HERE")
                .build();
        adView.loadAd(adRequest);

        t = (TextView)findViewById(R.id.text1);
        RefreshList();
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                if (sharedText != null) {
                    String[] line;
                    String title="", location="", url="";
                    line = sharedText.split("[\\r\\n]+");
                    Log.i("line to string", line.toString());

                    for(int i=0;i<line.length-1;i++){
                        //Toast.makeText(this, Integer.toString(i)+" "+line[i], Toast.LENGTH_LONG).show();
                        //Log.i("text",Integer.toString(i)+" "+line[i]);
                        location+=line[i];
                    }
                    location=location.replace("Dropped Pin", "");
                    url = line[line.length-1];
                    //final MySQLiteHelper db = new MySQLiteHelper(this);
                    //db.addLocation(new DataClass("some title", "some location","some url"));

                    //List<DataClass> list = db.getAllLocations();
                    //t.setText(Integer.toString(list.size()));
                    //list.get(0).getLocation().toString()
                    Toast.makeText(this, "selected settings", Toast.LENGTH_LONG).show();
                    LayoutInflater li = LayoutInflater.from(this);
                    View promptsView = li.inflate(R.layout.prompt, null);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            this);
                    alertDialogBuilder.setView(promptsView);
                    final EditText userInput = (EditText) promptsView
                            .findViewById(R.id.editTextDialogUserInput);
                    final String finalLocation = location;
                    final String finalUrl = url;
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int id) {
                                            MySQLiteHelper db = new MySQLiteHelper(MainActivity.this);
                                            db.addLocation(new DataClass(userInput.getText().toString(), finalLocation, finalUrl));
                                            RefreshList();
                                        }
                                    })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int id) {
                                            dialog.cancel();
                                        }
                                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                    //t.setText(sharedText.toString());
                }
            }
        }


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {

        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }

    @Override
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    /** Called before the activity is destroyed. */
    @Override
    public void onDestroy() {
        // Destroy the AdView.
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

}

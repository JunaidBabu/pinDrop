package in.junaidbabu.pindrop.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

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
            }
        });
        registerForContextMenu(myListView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle(list.get(info.position).getTitle());
        menu.add(0, v.getId(), 0, "Edit");
        menu.add(0, v.getId(), 0, "Delete");
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        if(item.getTitle().equals("Edit")){editItem(info.position);}
        else if(item.getTitle().equals("Delete")){deleteItem(info.position);}
        else {return false;}
        return true;
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

    ChipsMultiAutoCompleteTextview mu;

    void editItem(final int position) {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.prompt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        alertDialogBuilder.setView(promptsView);
        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextTitle);
        userInput.setText(list.get(position).getTitle());


        mu = (ChipsMultiAutoCompleteTextview) promptsView.findViewById(R.id.editTextTags);
        String[] item = db.getTags();
        //Log.i("", "Country Count : " + item.length);
        mu.setText(db.getTags(list.get(position).getId()));
        mu.setChips();
        mu.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, item));
        mu.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());



        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                final String[] ar=mu.getText().toString().split(",");
                                for(int i =0; i<ar.length;i++)
                                    ar[i]=toTitle(ar[i]);
                                list.get(position).setTitle(userInput.getText().toString());
                                db.updateLocation(list.get(position));
                                db.createTag(ar, list.get(position).getId());
                                //MySQLiteHelper db = new MySQLiteHelper(MainActivity.this);
                                //db.addLocation(new DataClass(userInput.getText().toString(), finalLocation, finalUrl));
                                RefreshList();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }



    private AdView adView;
    private static final String AD_UNIT_ID = "a15318ef8b15a1f";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        myListView = (ListView) findViewById(R.id.listView);
        db = new MySQLiteHelper(this);

//        adView = new AdView(this);
//        adView.setAdSize(AdSize.BANNER);
//        adView.setAdUnitId(AD_UNIT_ID);
//        LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
//        layout.addView(adView);
//        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice("90E3B0F34FB84C76925A056D1E16292A")
//                .addTestDevice("358001041154770")
//                .build();
//        adView.loadAd(adRequest);

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
                    String location="", url="";
                    line = sharedText.split("[\\r\\n]+");
                    Log.i("line to string", line.toString());

                    for(int i=0;i<line.length-1;i++)
                        location+=line[i]+" ";
                    location=location.replace("Dropped Pin", "");
                    url = line[line.length-1];
                    LayoutInflater li = LayoutInflater.from(this);
                    View promptsView = li.inflate(R.layout.prompt, null);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            this);
                    alertDialogBuilder.setView(promptsView);
                    final EditText userInput = (EditText) promptsView
                            .findViewById(R.id.editTextTitle);
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
            Intent i = new Intent(this, Home.class);
            startActivity(i);

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

    public String toTitle(String input) {

        StringBuilder titleCase = new StringBuilder();
        boolean nextTitleCase = true;

        for (char c : input.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                nextTitleCase = true;
            } else if (nextTitleCase) {
                c = Character.toTitleCase(c);
                nextTitleCase = false;
            }

            titleCase.append(c);
        }

        return titleCase.toString();
    }


}

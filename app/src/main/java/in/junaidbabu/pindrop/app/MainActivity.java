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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Ref;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    TextView t;

    public void RefreshList(){
        ListView myListView = (ListView) findViewById(R.id.listView);
        //ArrayList<String> myStringArray1 =  new ArrayList<String>();
        final MySQLiteHelper db = new MySQLiteHelper(this);
        final List<DataClass> list = db.getAllLocations();
        if(list.size()==0){
            t.setText("Share Dropped Pins to pinDrop");
            return;
        }

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
                startActivity(i);
                //Toast.makeText(MainActivity.this, list.get(position).getUrl().toString(), Toast.LENGTH_SHORT).show();
            }
        });
        registerForContextMenu(myListView);
        /*myListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);


        menu.setHeaderTitle(v.getId());
        menu.add(0, v.getId(), 0, "Action 1");
        menu.add(0, v.getId(), 0, "Action 2");
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        if(item.getTitle()=="Action 1"){function1(item.getItemId());}
        else if(item.getTitle()=="Action 2"){function2(item.getItemId());}
        else {return false;}
        return true;
    }

    public void function1(int id){
        Toast.makeText(this, "function 1 called", Toast.LENGTH_SHORT).show();
    }
    public void function2(int id){
        Toast.makeText(this, "function 2 called", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

}

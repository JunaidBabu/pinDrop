package in.junaidbabu.pindrop.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Home extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    static MySQLiteHelper db;
    static List<DataClass> list;
    static ListView myListView;
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new MySQLiteHelper(this);
        setContentView(R.layout.activity_home);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

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
                                            MySQLiteHelper db = new MySQLiteHelper(getApplicationContext());
                                            db.addLocation(new DataClass(userInput.getText().toString(), finalLocation, finalUrl));
                                            //db.createTag(ar, list.get(position).getId());
                                            //RefreshList();
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
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        if(db.getTags().length>0)
            mTitle = db.getTags()[number-1];
        else
            mTitle = "Home";
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.home, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Log.i("Settings Clicked", "True that");
            List<DataClass> a = db.getLocation("IIT");
            List<DataClass> all = db.getAllLocations();
            Log.i("Location 1 of IIT", a.toString());
            Log.i("first of all",all.get(0).getLocation());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }
        public void RefreshList(int pos){

            if(pos==-1)
                list = db.getAllLocations();
            else
                list=db.getLocation(db.getTags()[pos]);

            //list = db.getAllLocations();
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
            CustomListViewAdapter adapter = new CustomListViewAdapter(getActivity(),
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
                        Toast.makeText(getActivity(), "Something went wrong! :(", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            registerForContextMenu(myListView);
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
            new AlertDialog.Builder(getActivity())
                    .setTitle("Delete entry")
                    .setMessage("Are you sure you want to delete this entry?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            db.deleteLocation(list.get(position).getId());
                            RefreshList(-1);
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
            LayoutInflater li = LayoutInflater.from(getActivity());
            View promptsView = li.inflate(R.layout.prompt, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setView(promptsView);
            final EditText userInput = (EditText) promptsView
                    .findViewById(R.id.editTextTitle);
            userInput.setText(list.get(position).getTitle());


            mu = (ChipsMultiAutoCompleteTextview) promptsView.findViewById(R.id.editTextTags);
            String[] item = db.getTags();
            //Log.i("", "Country Count : " + item.length);
            mu.setText(db.getTags(list.get(position).getId()));
            mu.setChips();
            mu.setAdapter(new ArrayAdapter<String>(getActivity(),
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
                                    RefreshList(-1);
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
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_home, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            myListView = (ListView) rootView.findViewById(R.id.listViewHome);
            textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
            RefreshList(getArguments().getInt(ARG_SECTION_NUMBER)-1);

            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((Home) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}

package org.projects.shoppinglist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    ArrayAdapter<String> adapter;
    ListView listView;
    ArrayList<String> bag = new ArrayList<String>();

    public ArrayAdapter getMyAdapter() {
        return adapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //getting our listiew - you can check the ID in the xml to see that it
        //is indeed specified as "list"
        listView = (ListView) findViewById(R.id.list);
        //here we create a new adapter linking the bag and the
        //listview
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_checked, bag);

        //setting the adapter on the listview
        listView.setAdapter(adapter);
        //here we set the choice mode - meaning in this case we can
        //only select one item at a time.
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        Button addButton = (Button) findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText textField = (EditText) findViewById(R.id.name);
                EditText textField2 = (EditText) findViewById(R.id.quantity);
                String text = textField2.getText().toString() + " " + textField.getText().toString();
                //   bag.add(text);
                //The next line is needed in order to say to the ListView
                //that the data has changed - we have added stuff now!
                bag.add(text);
                getMyAdapter().notifyDataSetChanged();
            }
        });

        Button removeButton = (Button) findViewById(R.id.removeButton);
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SparseBooleanArray checked = listView.getCheckedItemPositions();
                List<Integer> toDelete = new ArrayList<>();
                for (int i = 0; i < checked.size(); i++) {
                    int key = checked.keyAt(i);
                    if (checked.get(key)) {
                        toDelete.add(key);
                    }
                }
                for (int i = 0; i < toDelete.size(); i++) {
                    bag.remove(toDelete.get(i) - i);
                }
                listView.setAdapter(adapter);
                getMyAdapter().notifyDataSetChanged();
            }
        });

        Button clearButton = (Button) findViewById(R.id.clearButton);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.clear();
                listView.setAdapter(adapter);
                getMyAdapter().notifyDataSetChanged();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //This method is called before our activity is destoryed
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //ALWAYS CALL THE SUPER METHOD - To be nice!
        super.onSaveInstanceState(outState);
        /* Here we put code now to save the state */
        outState.putStringArrayList("savedBag", bag);

        SparseBooleanArray checked = listView.getCheckedItemPositions();

        outState.putParcelable("checkedItems", new SparseBooleanArrayParcelable(checked));
    }

    //this is called when our activity is recreated, but
    //AFTER our onCreate method has been called
    //EXTREMELY IMPORTANT DETAIL
    @Override
    protected void onRestoreInstanceState(Bundle savedState) {
        //MOST UI elements will automatically store the information
        //if we call the super.onRestoreInstaceState
        //but other data will be lost.
        super.onRestoreInstanceState(savedState);
		/*Here we restore any state */
        //in the line below, notice key value matches the key from onSaved
        //this is of course EXTREMELY IMPORTANT
        this.bag = savedState.getStringArrayList("savedBag");
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_checked, bag);
        listView.setAdapter(adapter);
        SparseBooleanArray checked = (SparseBooleanArray) savedState.getParcelable("checkedItems");
        for (int i = 0; i < checked.size(); i++) {
            int key = checked.keyAt(i);
            if (checked.get(key)) {
                listView.setItemChecked(key, true);
            }
        }
        adapter.notifyDataSetChanged();
    }
}

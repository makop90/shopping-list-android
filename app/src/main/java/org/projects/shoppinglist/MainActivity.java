package org.projects.shoppinglist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MyDialogFragment.OnPositiveListener {

    public ArrayAdapter<Product> adapter;
    public ListView listView;
    private static final int INTENT_SETTINGS_ID = 1;
    public ArrayList<Product> bag = new ArrayList<Product>();
    public static MyDialogFragment dialog;
    public static Context context;
    public String name = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = MainActivity.this;
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listView = (ListView) findViewById(R.id.list);
        adapter = new ArrayAdapter<Product>(this,
                android.R.layout.simple_list_item_checked, bag);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        Button addButton = (Button) findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText textField = (EditText) findViewById(R.id.name);
                EditText textField2 = (EditText) findViewById(R.id.quantity);
                Product p = new Product(textField.getText().toString(), Integer.parseInt(textField2.getText().toString()));
                bag.add(p);
                adapter.notifyDataSetChanged();
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
                adapter.notifyDataSetChanged();
            }
        });

        name = PreferencesFragment.getName(this);
        String message = "Welcome back "+name;
        Toast toast = Toast.makeText(this,message,Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings: {
                Intent intent = new Intent(this,SettingsActivity.class);
                startActivityForResult(intent,INTENT_SETTINGS_ID);
                break;
            }
            case R.id.item_delete: {
                dialog = new MyDialog();
                dialog.show(getFragmentManager(), "MyFragment");
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("savedBag", bag);
        SparseBooleanArray checked = listView.getCheckedItemPositions();
        outState.putParcelable("checkedItems", new SparseBooleanArrayParcelable(checked));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedState) {
        super.onRestoreInstanceState(savedState);
        this.bag = savedState.getParcelableArrayList("savedBag");
        adapter = new ArrayAdapter<Product>(this,
                android.R.layout.simple_list_item_checked, bag);
        listView.setAdapter(adapter);
        SparseBooleanArray checked = savedState.getParcelable("checkedItems");
        for (int i = 0; i < checked.size(); i++) {
            int key = checked.keyAt(i);
            if (checked.get(key)) {
                listView.setItemChecked(key, true);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onPositiveClicked() {
        adapter.clear();
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public static class MyDialog extends MyDialogFragment {
        @Override
        protected void negativeClick() {
            Toast toast = Toast.makeText(context,
                    "Canceled", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==INTENT_SETTINGS_ID) //the code means we came back from settings
        {
            String name = PreferencesFragment.getName(this);
            String message = "Welcome, "+name;
            Toast toast = Toast.makeText(this,message,Toast.LENGTH_LONG);
            toast.show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}

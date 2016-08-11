package com.knowall.findots.activities;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.knowall.findots.R;
import com.knowall.findots.adapters.Serach_UserListAdapter;
import com.knowall.findots.fragments.InitialFragment;
import com.knowall.findots.restcalls.getUser.GetUserData;

import java.util.ArrayList;

/**
 * Created by jpaulose on 8/10/2016.
 */
public class SearchUserActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private RecyclerView mRecyclerView;
    private Serach_UserListAdapter mAdapter;
    private ArrayList<GetUserData> mModels;
    int userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

//        if(savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.container, MainFragment.newInstance())
//                    .commit();
//        }

//        setHasOptionsMenu(true);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(SearchUserActivity.this));
        mModels = new ArrayList<GetUserData>();
        mModels.addAll(InitialFragment.getUserDatasList);

        if(getIntent().getExtras()!=null)
        userId=getIntent().getExtras().getInt("userId");

        if(userId==0)
            userId=-1;
        mAdapter = new Serach_UserListAdapter(SearchUserActivity.this, mModels,userId);
        mRecyclerView.setAdapter(mAdapter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_searchuser, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setIconifiedByDefault(true);
        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();
        searchView.setOnQueryTextListener(SearchUserActivity.this);
//        int id = searchView.getContext()
//                .getResources()
//                .getIdentifier("android:id/search_src_text", null, null);
//        TextView textView = (TextView) searchView.findViewById(id);
//        textView.setTextColor(Color.CYAN);
        // Get the search close button image view
        ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text)).setHintTextColor(getResources().getColor(R.color.black_25));
        ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text)).setTextColor(getResources().getColor(R.color.black_70));

        ImageView closeButton = (ImageView)searchView.findViewById(R.id.search_close_btn);

        // Set on click listener
        closeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("jomy", "Search close button clicked");
                searchView.setQuery("", false);
//                //Find EditText view
//                EditText et = (EditText) findViewById(R.id.search_src_text);
//
//                //Clear the text from EditText view
//                et.setText("");
//
//                //Clear query
//                mSearchView.setQuery("", false);
//                //Collapse the action view
//                mSearchView.onActionViewCollapsed();
//                //Collapse the search widget
//                mSearchMenu.collapseActionView();
            }
        });
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onQueryTextChange(String query) {
        final ArrayList<GetUserData> filteredModelList = filter(mModels, query);
        mAdapter.animateTo(filteredModelList);
        mRecyclerView.scrollToPosition(0);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private ArrayList<GetUserData> filter(ArrayList<GetUserData> models, String query) {
        query = query.toLowerCase();

        final ArrayList<GetUserData> filteredModelList = new ArrayList<>();
        for (GetUserData model : models) {
            final String text = model.getName().toLowerCase();
//            Log.d("jomy", "text  : "+text);
            if (text.contains(query)) {
                filteredModelList.add(model);
                Log.d("jomy", "text Added : "+filteredModelList.size());
            }
        }
        return filteredModelList;
    }
}

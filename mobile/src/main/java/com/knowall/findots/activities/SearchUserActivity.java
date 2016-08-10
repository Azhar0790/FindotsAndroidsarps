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
import android.widget.ImageView;

import com.knowall.findots.R;
import com.knowall.findots.adapters.Serach_UserListAdapter;
import com.knowall.findots.fragments.InitialFragment;
import com.knowall.findots.restcalls.getUser.GetUserData;
import com.knowall.findots.restcalls.getUser.GetUserModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jpaulose on 8/10/2016.
 */
public class SearchUserActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private RecyclerView mRecyclerView;
    private Serach_UserListAdapter mAdapter;
    private ArrayList<GetUserData> mModels;
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

        mRecyclerView.setLayoutManager(new LinearLayoutManager(SearchUserActivity.this));

        mModels = new ArrayList<GetUserData>();
        mModels.addAll(InitialFragment.getUserDatasList);


        mAdapter = new Serach_UserListAdapter(SearchUserActivity.this, mModels);
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
        // Get the search close button image view
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
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }
}

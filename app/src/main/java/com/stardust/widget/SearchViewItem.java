package com.stardust.widget;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.MenuItem;

import com.stardust.scriptdroid.ui.main.MainActivity;

/**
 * Created by Stardust on 2017/10/25.
 */

public class SearchViewItem implements MenuItemCompat.OnActionExpandListener, SearchView.OnQueryTextListener {

    public interface QueryCallback {
        void summitQuery(String query);
    }

    private QueryCallback mQueryCallback;
    private MenuItem mSearchMenuItem;

    public SearchViewItem(Activity activity, MenuItem searchMenuItem) {
        mSearchMenuItem = searchMenuItem;
        SearchManager searchManager = (SearchManager) activity.getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();
        if (searchView == null) {
            return;
        }
        searchView.setSearchableInfo(searchManager.getSearchableInfo(activity.getComponentName()));
        MenuItemCompat.setOnActionExpandListener(searchMenuItem, this);
        searchView.setOnQueryTextListener(this);
    }

    public void setQueryCallback(QueryCallback queryCallback) {
        mQueryCallback = queryCallback;
    }

    public void setVisible(boolean visible) {
        mSearchMenuItem.setVisible(visible);
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        if (mQueryCallback == null) {
            return true;
        }
        mQueryCallback.summitQuery(null);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (mQueryCallback == null) {
            return true;
        }
        mQueryCallback.summitQuery(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }


    public void collapse() {
        mSearchMenuItem.collapseActionView();
    }

}

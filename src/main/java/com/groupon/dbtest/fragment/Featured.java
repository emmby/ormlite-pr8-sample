package com.groupon.dbtest.fragment;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.app.ListFragment;
import android.view.*;
import android.widget.TextView;
import com.groupon.dbtest.R;
import com.groupon.dbtest.db.DbTestOrmLiteOpenHelper;
import com.groupon.dbtest.model.BugCount;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteCursorAdapter;
import com.j256.ormlite.android.apptools.support.OrmLiteCursorLoader;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;

import java.sql.SQLException;

public class Featured extends ListFragment {

    protected Dao<BugCount,Long> bugCountDao;

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final DbTestOrmLiteOpenHelper helper = OpenHelperManager.getHelper(getActivity(), DbTestOrmLiteOpenHelper.class);
        final PreparedQuery<BugCount> preparedQuery;
        try {
            bugCountDao = helper.getDao(BugCount.class);
            preparedQuery = bugCountDao.queryBuilder().orderBy("count", false).prepare();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Enable the reload menu
        setHasOptionsMenu(true);

        // Create the list adapter that will map our entities to views in a listview
        final OrmLiteCursorAdapter<BugCount,TextView> adapter = new OrmLiteCursorAdapter<BugCount,TextView>(getActivity()) {
            @Override
            public void bindView(TextView card, Context context, BugCount item) {
                card.setText(item.getTitle());
            }

            @Override
            public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
                return new TextView(context);
            }
        };

        // Set the adapter
        setListAdapter(adapter);

        // Start the asynchronous loader to load the entities from the database

        getLoaderManager().initLoader(0, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
                return new OrmLiteCursorLoader<BugCount>(getActivity(), bugCountDao, preparedQuery);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
                adapter.changeCursor(cursor, ((OrmLiteCursorLoader<BugCount>) loader).getQuery());
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                adapter.changeCursor(null,null);
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.featured, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.add(0,1,0,"Add More").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if( item.getItemId()==1) {
            DbTestOrmLiteOpenHelper.addSomeBugCounts(bugCountDao, 5);
            return true;
        }

        return false;
    }
}

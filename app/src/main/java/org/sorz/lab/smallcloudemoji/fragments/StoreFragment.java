package org.sorz.lab.smallcloudemoji.fragments;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import org.sorz.lab.smallcloudemoji.R;
import org.sorz.lab.smallcloudemoji.activites.AddRepositoryActivity;
import org.sorz.lab.smallcloudemoji.activites.SettingsActivity;
import org.sorz.lab.smallcloudemoji.adapters.StoreSourceAdapter;
import org.sorz.lab.smallcloudemoji.db.DaoSession;
import org.sorz.lab.smallcloudemoji.db.DatabaseHelper;
import org.sorz.lab.smallcloudemoji.tasks.RefreshStoreAsyncTask;


/**
 * A fragment representing a list of source.
 */
public class StoreFragment extends ListFragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String STORE_URL = "storeUrl";
    private Context context;
    private DaoSession daoSession;
    private String storeUrl;
    private OnSourceClickListener mListener;

    private SwipeRefreshLayout swipeLayout;
    private StoreSourceAdapter adapter;

    public static StoreFragment newInstance(String storeUrl) {
        StoreFragment fragment = new StoreFragment();
        Bundle args = new Bundle();
        args.putString(STORE_URL, storeUrl);
        fragment.setArguments(args);
        return fragment;
    }

    public StoreFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        mListener = (OnSourceClickListener) context;
        daoSession = DatabaseHelper.getInstance(context).getDaoSession();

        if (getArguments() != null) {
            storeUrl = getArguments().getString(STORE_URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store, container, false);
        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);

        ListView listView = (ListView) view.findViewById(android.R.id.list);
        adapter = new StoreSourceAdapter(context, daoSession.getSourceDao());
        listView.setAdapter(adapter);

        if (adapter.isEmpty()) {
            swipeLayout.setRefreshing(true);
            onRefresh();
        }
        return view;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (!swipeLayout.isRefreshing())
            mListener.onSourceClick(id);
    }

    @Override
    public void onRefresh() {
        new RefreshStoreAsyncTask(context) {
            @Override
            protected void onPostExecute(Integer result) {
                super.onPostExecute(result);
                swipeLayout.setRefreshing(false);
                adapter.notifyDataSetChanged();
                if (result == RESULT_SUCCESS)
                    Toast.makeText(context,
                            R.string.download_success, Toast.LENGTH_SHORT).show();
                else if (result == RESULT_ERROR_IO)
                    Toast.makeText(context,
                            R.string.download_io_exception, Toast.LENGTH_SHORT).show();
                else if (result == RESULT_ERROR_SERVER_FAIL)
                    Toast.makeText(context,
                            R.string.store_server_fail, Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(context,
                            R.string.download_unknown_error, Toast.LENGTH_SHORT).show();
            }
        }.execute(storeUrl);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SettingsActivity.REQUEST_FOR_ADDING_REPOSITORY) {
            if (resultCode == AddRepositoryActivity.RESULT_SUCCESS_ADDED) {
                adapter.notifyDataSetChanged();
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public interface OnSourceClickListener {
        public void onSourceClick(long sourceId);
    }
}

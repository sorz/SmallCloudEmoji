package org.sorz.lab.smallcloudemoji.fragments;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import org.sorz.lab.smallcloudemoji.R;
import org.sorz.lab.smallcloudemoji.tasks.RefreshStoreAsyncTask;


/**
 * A fragment representing a list of source.
 */
public class StoreFragment extends ListFragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String STORE_URL = "storeUrl";
    private Context context;
    private String storeUrl;
    private SwipeRefreshLayout swipeLayout;

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

        return view;

    }


    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }

    @Override
    public void onRefresh() {
        System.out.println("refreshing");
        new RefreshStoreAsyncTask(context) {
            @Override
            protected void onPostExecute(Integer result) {
                super.onPostExecute(result);
                swipeLayout.setRefreshing(false);
                if (result == RESULT_SUCCESS)
                    Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
                else if (result == RESULT_ERROR_IO)
                    Toast.makeText(context, "io error", Toast.LENGTH_SHORT).show();
                else if (result == RESULT_ERROR_SERVER_FAIL)
                    Toast.makeText(context, "server fail", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(context, "unknown error", Toast.LENGTH_SHORT).show();
            }
        }.execute(storeUrl);
    }
}

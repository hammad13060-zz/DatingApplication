package com.hammad13060.datingapplication;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.hammad13060.datingapplication.helper.SwipeListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DisplayMatchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DisplayMatchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DisplayMatchFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "DisplayMatchFragment";
    private static final String WEB_URL = Constants.WEB_SERVER_URL + "/get_matches.php";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private SwipeListAdapter adapter;
    private List<Person> matchList;

    private View myView;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DisplayMatchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DisplayMatchFragment newInstance(String param1, String param2) {
        DisplayMatchFragment fragment = new DisplayMatchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public DisplayMatchFragment() {
        // Required empty public constructor
    }
    


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_display_match, container, false);
        return myView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView = (ListView) myView.findViewById(R.id.listView);
        swipeRefreshLayout = (SwipeRefreshLayout) myView.findViewById(R.id.swipe_refresh_layout);

        matchList = new ArrayList<>();
        adapter = new SwipeListAdapter(getActivity(), matchList);

        listView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(this);

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        fetchMatches();
                                    }
                                }
        );

    }

    @Override
    public void onRefresh() {
        fetchMatches();
    }


    private void fetchMatches() {
        // showing refresh animation before making http call
        swipeRefreshLayout.setRefreshing(true);

        //volley request object
        RequestQueue volleyRequest = Volley.newRequestQueue(getActivity());

        //success/error response listeners
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                Log.e(TAG, "response successful from get_matches");
                boolean has_matches = false;

                try {
                    has_matches = response.getBoolean("has_matches");
                    Log.d(TAG, "response pretty json: " + response.toString());
                    if (has_matches) {
                        Log.d(TAG, "user have matches");

                        JSONArray matches = response.getJSONArray("matches");
                        for (int i = 0; i < matches.length(); i++) {
                            JSONObject match_object = (JSONObject) matches.get(i);
                            Person match = new Person(
                                    match_object.getString("user_id"),
                                    match_object.getString("name"),
                                    (match_object.getInt("gender") == 0) ? false : true,
                                    match_object.getInt("age"),
                                    match_object.getString("url")
                            );

                            matchList.add(match);


                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.d(TAG, "no matches for user");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "couldn't fetch matches");
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();

                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);
            }
        };

        //json for fetching matches
        final JSONObject object = new JSONObject();

        try {
            object.put("fetch_matches", true);
            //user_id being sent to server
            Log.e(TAG, "user_id: " + AccessToken.getCurrentAccessToken().getUserId());
            object.put("user_id", AccessToken.getCurrentAccessToken().getUserId());
            Log.d(TAG, "request pretty json: " + object.toString());
            JSONRequest request = new JSONRequest(
                    Request.Method.POST, WEB_URL, null,
                    responseListener, errorListener, object
            );
            volleyRequest.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /*@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }*/

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}

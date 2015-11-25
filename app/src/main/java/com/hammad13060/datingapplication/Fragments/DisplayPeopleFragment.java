package com.hammad13060.datingapplication.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.hammad13060.datingapplication.DBEntity.Person;
import com.hammad13060.datingapplication.DBHandlers.LikedUserDBHandler;
import com.hammad13060.datingapplication.DBHandlers.PeopleDBHandler;
import com.hammad13060.datingapplication.R;
import com.hammad13060.datingapplication.helper.Constants;
import com.hammad13060.datingapplication.helper.JSONRequest;
import com.hammad13060.datingapplication.helper.NSDHelper;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DisplayPeopleFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DisplayPeopleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DisplayPeopleFragment extends Fragment {

    private static final String WEB_URL = Constants.WEB_SERVER_URL + "/register_user_like.php";

    private static final String TAG = "DisplayPeopleFragment";

    private List<Person> peopleAround = null;

    Person currentPerson = null;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private View myView = null;

    private boolean status = false;
    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DisplayPeopleFragment.
     */

    // TODO: Rename and change types and number of parameters
    public static DisplayPeopleFragment newInstance(String param1, String param2) {
        DisplayPeopleFragment fragment = new DisplayPeopleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public DisplayPeopleFragment() {
        // Required empty public constructor
    }

    private NSDHelper mNSDHelper = null;
    private PeopleAroundReceiver peopleAroundReceiver = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        status = false;


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_display_people, container, false);
        ((Button)myView.findViewById(R.id.like_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerLike();
                deleteCurrentUser();
            }
        });

        ((Button)myView.findViewById(R.id.ignore_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCurrentUser();
            }
        });

        return myView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {

        super.onResume();
        registerPeopleAroundReceiver();
        getPeople();
        updateLayout();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterPeopleAroundReceiver();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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

    private void registerPeopleAroundReceiver() {
        IntentFilter filter = new IntentFilter("com.hammad13060.datingapplication.PEOPLE_AROUND_RECEIVER");
        peopleAroundReceiver = new PeopleAroundReceiver();
        getActivity().registerReceiver(peopleAroundReceiver, filter);
    }

    private void unregisterPeopleAroundReceiver() {
        getActivity().unregisterReceiver(peopleAroundReceiver);
    }

    //updating layout at runtime
    private void updateLayout() {
        if (peopleAround ==null || peopleAround.size() <= 0){
            getPeople();
        }
        LinearLayout no_people_around = (LinearLayout)myView.findViewById(R.id.no_people_around);
        LinearLayout people_around = (LinearLayout)myView.findViewById(R.id.people_around);
        if (peopleAround.size() <= 0) {
            people_around.setVisibility(View.GONE);
            no_people_around.setVisibility(View.VISIBLE);
            status = false;
        } else {
            no_people_around.setVisibility(View.GONE);
            people_around.setVisibility(View.VISIBLE);
            status = true;
            setCurrentUser();
        }
    }

    private class PeopleAroundReceiver extends BroadcastReceiver {
        @Override
        public void
        onReceive(Context context, Intent intent) {
            if (status == false) {
                updateLayout();
            }
        }
    }

    private void getPeople() {
        PeopleDBHandler handler = new PeopleDBHandler(getActivity(), null, null, 1);
        peopleAround = handler.getAllUser();
    }

    private void setCurrentUser() {
        currentPerson = peopleAround.get(0);

        if (hasLiked(currentPerson) || isMe(currentPerson)) {
            deleteCurrentUser();
        } else {

            ImageView profile_image_view = (ImageView) myView.findViewById(R.id.profile_image_view);

            Picasso
                    .with(getActivity())
                    .load(currentPerson.get_url())
                    .into(profile_image_view);
        }
    }

    private void deleteCurrentUser() {
        peopleAround.remove(0);
        PeopleDBHandler handler = new PeopleDBHandler(getActivity(), null, null, 1);
        handler.deleteUser(currentPerson.get_user_id());
        updateLayout();
    }

    private void registerLike() {
        //logged in user id
        String user_id_1 = AccessToken.getCurrentAccessToken().getUserId();

        //user id of person liked
        String user_id_2 = currentPerson.get_user_id();

        JSONObject user_request = new JSONObject();
        try {
            user_request.put("user_id_1", user_id_1);
            user_request.put("user_id_2", user_id_2);
            sendRequest(user_request, user_id_2);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void sendRequest(JSONObject object, final String user_id) {

        //volley request object
        RequestQueue volleyRequest = Volley.newRequestQueue(getActivity());

        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                try {
                    boolean success = response.getBoolean("success");
                    Log.d(TAG, "like registration: " + success);
                    LikedUserDBHandler handler = new LikedUserDBHandler(getActivity(), null, null, 1);
                    handler.addLikedUserId(user_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "LIKE REGISTRATION FAILED");
            }
        };

        JSONRequest request = new JSONRequest(
                Request.Method.POST, WEB_URL, null,
                responseListener, errorListener, object
        );

        volleyRequest.add(request);

    }

    private boolean hasLiked(Person person) {
        LikedUserDBHandler likedUserDBHandler = new LikedUserDBHandler(getActivity(), null, null, 1);
        return likedUserDBHandler.hasLiked(person.get_user_id());
    }

    private boolean isMe(Person person) {
        return AccessToken.getCurrentAccessToken().getUserId().equals(person.get_user_id());
    }

}
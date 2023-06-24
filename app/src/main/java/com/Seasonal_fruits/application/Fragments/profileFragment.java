package com.Seasonal_fruits.application.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.Seasonal_fruits.application.Activities.ChangeProfile;
import com.Seasonal_fruits.application.Activities.LoginActivity;
import com.Seasonal_fruits.application.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.Objects;


public class profileFragment extends Fragment {

    public profileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    DatabaseReference ref;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    private static final String TAG="User Profile";
    TextView userName,userPhoneNumber,userEmail;
    ImageView profilePic;
    TextView tv_changeProfile;
    Button logoutBtn;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() != null) {
            getActivity().setTitle("Profile");
        }
        tv_changeProfile=view.findViewById(R.id.tv_changeProfile);
        logoutBtn=view.findViewById(R.id.logoutbtn);
        if (auth.getCurrentUser() != null) {
            profilePic=view.findViewById(R.id.profilePic);
            userName=view.findViewById(R.id.userName);
            userPhoneNumber=view.findViewById(R.id.userPhoneNumber);
            userEmail=view.findViewById(R.id.userEmail);
            ref= FirebaseDatabase.getInstance().getReference("Registered Users");
            ref.child(Objects.requireNonNull(auth.getUid())).get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                        DataSnapshot ds= task.getResult();
                        String fullName=String.valueOf(ds.child("fullName").getValue());
                        String phoneNumber=String.valueOf(ds.child("phoneNumber").getValue());
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser(); // Initialize firebaseUser
                        if (firebaseUser != null) { // Check if firebaseUser is not null
                            String Email = firebaseUser.getEmail();
                            userName.setText(fullName);
                            userPhoneNumber.setText(phoneNumber);
                            userEmail.setText(Email);
                            Uri uri =firebaseUser.getPhotoUrl();
                            Picasso.get().load(uri).into(profilePic);
                        }
                    }
                }
                try {
                    Exception exception = task.getException();
                    if (exception != null) {
                        throw exception;
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Exception occurred: " + Log.getStackTraceString(e));
                }
            });
        } else {
            Log.e(TAG, "FirebaseAuth instance is null or user is not signed in");
        }

        tv_changeProfile.setOnClickListener(v-> startActivity(new Intent(getActivity(), ChangeProfile.class)));

        logoutBtn.setOnClickListener(v -> {
            Intent intent=new Intent(getContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}
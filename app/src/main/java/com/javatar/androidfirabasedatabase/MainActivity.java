package com.javatar.androidfirabasedatabase;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.javatar.androidfirabasedatabase.model.User;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private TextView nameTextView;
    private EditText nameEditText;
    private EditText emailEditText;
    private Button saveButton;
    private DatabaseReference mFirebaseDatabaseUser;
    private FirebaseDatabase mFirebaseInstance;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initFirabase();
        initUI();
    }

    private void initFirabase() {
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabaseUser = mFirebaseInstance.getReference("users");
    }

    private void initUI() {
        nameTextView = (TextView) findViewById(R.id.user_textview);
        nameEditText = (EditText) findViewById(R.id.name_edittext);
        emailEditText = (EditText) findViewById(R.id.email_edittext);
        saveButton = (Button) findViewById(R.id.save_button);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameEditText.getText().toString();
                String email = emailEditText.getText().toString();

                // Check for already existed userId
                if (TextUtils.isEmpty(userId)) {
                    createUser(name, email);
                } else {
                    updateUser(name, email);
                }
            }
        });
    }

    private void createUser(String name, String email) {

        if (TextUtils.isEmpty(userId)) {
            userId = mFirebaseDatabaseUser.push().getKey();
        }

        User user = new User(name, email);

        mFirebaseDatabaseUser.child(userId).setValue(user);

        addUserChangeListener();
    }

    private void updateUser(String name, String email) {

        if (!TextUtils.isEmpty(name)) {
            mFirebaseDatabaseUser.child(userId).child("name").setValue(name);
        }

        if (!TextUtils.isEmpty(email)) {
            mFirebaseDatabaseUser.child(userId).child("email").setValue(email);
        }
    }


    private void addUserChangeListener() {
        mFirebaseDatabaseUser.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                if (user == null) {
                    Log.e(TAG, "Data is null!");
                    return;
                }
                nameTextView.setText(user.name + ", " + user.email);
                emailEditText.setText("");
                nameEditText.setText("");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Failed to read user", error.toException());
            }
        });
    }
}
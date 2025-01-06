package com.example.brainpath.ui.interaction;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class InteractionViewModel extends ViewModel {
    private final MutableLiveData<List<Friend>> friends = new MutableLiveData<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public LiveData<List<Friend>> getFriends() {
        if (friends.getValue() == null) {
            fetchFriendsFromFirebase();
        }
        return friends;
    }

    private void fetchFriendsFromFirebase() {
        db.collection("users")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Friend> friendList = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        friendList.add(doc.toObject(Friend.class));
                    }
                    friends.setValue(friendList);
                })
                .addOnFailureListener(e -> Log.e("InteractionViewModel", "Error fetching friends", e));
    }
}

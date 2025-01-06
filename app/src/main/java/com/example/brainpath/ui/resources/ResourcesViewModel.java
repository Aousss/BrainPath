package com.example.brainpath.ui.resources;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class ResourcesViewModel extends ViewModel {
    private final MutableLiveData<List<Resources>> resources = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> details = new MutableLiveData<>();


    // Setter for the resource list
    public void setResources(List<Resources> resourcesList) {
        resources.setValue(resourcesList);
    }

    // Getter for the resource list
    public LiveData<List<Resources>> getResources() {
        return resources;
    }

    // Setter for the selected resource
    public void setDetails(String resource) {
        details.setValue(resource);
    }

    // Getter for the selected resource
    public LiveData<String> getDetails() {
        return details;
    }

}
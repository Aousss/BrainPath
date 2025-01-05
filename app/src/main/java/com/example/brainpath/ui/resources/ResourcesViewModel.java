package com.example.brainpath.ui.resources;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class ResourcesViewModel extends ViewModel {
    private final MutableLiveData<List<Resources>> resources = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<Resources>> getResources() {
        return resources;
    }

    public void setResources(List<Resources> resourcesList) {
        resources.setValue(resourcesList);
    }
}

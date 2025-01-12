package com.example.brainpath.ui.interaction;
import androidx.recyclerview.widget.DiffUtil;
import java.util.List;

public class DiffCallback extends DiffUtil.Callback {

    private final List<ForumPost> oldList;
    private final List<ForumPost> newList;

    public DiffCallback(List<ForumPost> oldList, List<ForumPost> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        // Compare unique identifiers (like fileDocId) to determine if items are the same
        return oldList.get(oldItemPosition).getFileDocId()
                .equals(newList.get(newItemPosition).getFileDocId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        // Compare the entire content of the objects for equality
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }
}

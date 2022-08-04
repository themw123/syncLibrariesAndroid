package com.example.synclibraries2.Download;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import syncLibraries.Download;

public class SeitenAdapter extends FragmentStateAdapter {

    private Download download;
    private ArrayAdapter arrayAdapter;

    public SeitenAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, Download download) {
        super(fragmentManager, lifecycle);
        this.download = download;

    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = new RecycleFragment(position, download);
        Bundle args = new Bundle();
        args.putString(RecycleFragment.TITLE, "Tab "+(position+1));
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public int getItemCount() {
        return 3;
    }

}

package com.example.musify;

public interface SongChangeListener {
    void onChanged(int position);
    void onPlay();
    void onPrev();
    void onPaused();
    void onNext();
    void showBottomSheetDialog();
}

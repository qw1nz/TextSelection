package com.dwdm.textselection.selection;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class SelectableLayoutManager extends LinearLayoutManager {

    private static final String TAG = ">>>>>";
    private SelectionController sh;

    public SelectableLayoutManager(Context context) {
        super(context);
    }

    public SelectableLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public SelectableLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setSelectionController(SelectionController selectionController) {
        sh = selectionController;
    }
    @Override
    public void offsetChildrenVertical(int dy) {
        super.offsetChildrenVertical(dy);
        if (sh != null) sh.checkHandlesPosition();
    }

    @Override
    public void attachView(View child) {
        Log.d(TAG, "attachView() called with " + "child = [" + child + "]");
        super.attachView(child);

    }

    @Override
    public void addDisappearingView(View child) {
        super.addDisappearingView(child);
        Log.d(TAG, "addDisappearingView() called with " + "child = [" + child + "]");
        if (sh != null) sh.addViewToSelectable(child);
    }


    @Override
    public void addDisappearingView(View child, int index) {
        Log.d(TAG, "addDisappearingView() called with " + "child = [" + child + "], index = [" + index + "]");
        super.addDisappearingView(child, index);
    }

    @Override
    public void attachView(View child, int index, RecyclerView.LayoutParams lp) {
        super.attachView(child, index, lp);
        Log.d(TAG, "attachView() called with " + "child = [" + child + "], index = [" + index + "], lp = [" + lp + "]");
    }

    /*@Override
    public void addView(View child) {
        super.addView(child);
        if (sh != null) sh.addViewToSelectable(child);
        Log.d(TAG, "addView() called with " + "child = [" + child + "]");
    }*/

    @Override
    public void addView(View child, int index) {
        super.addView(child, index);
        if (sh != null) sh.addViewToSelectable(child);
        Log.d(TAG, "addView() called with " + "child = [" + child + "], index = [" + index + "]");
    }

    @Override
    public void detachAndScrapAttachedViews(RecyclerView.Recycler recycler) {
        Log.d(TAG, "detachAndScrapAttachedViews() called with " + "recycler = [" + recycler + "]");
        super.detachAndScrapAttachedViews(recycler);
      //  if (sh != null) sh.checkSelectableList();
    }


    @Override
    public void attachView(View child, int index) {
        super.attachView(child, index);
        Log.d(TAG, "attachView() called with " + "child = [" + child + "], index = [" + index + "]");
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        if (sh != null) sh.checkHandlesPosition();
    }

}

package com.dwdm.textselection.selection;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by anton on 26.09.2015.
 */
public class SelectableRecyclerView extends RecyclerView{
    private SelectionController sh;

    public SelectableRecyclerView(Context context) {
        super(context);
    }

    public SelectableRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SelectableRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        sh = new SelectionController(this);
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
        if (layout instanceof SelectableLayoutManager) {
            ((SelectableLayoutManager)layout).setSelectionController(sh);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (sh != null) sh.checkHandlesPosition();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (sh != null) sh.drawHandles(canvas);
    }

    public void setSelectionCallback(SelectionCallback selectionCallback) {
        if (sh != null) {
            sh.setSelectionCallback(selectionCallback);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (sh != null) {
            if (sh.onTouchEvent(ev)) {
                return true;
            }
        }

        return super.dispatchTouchEvent(ev);
    }

    public void copyTextToClipboard() {
        sh.copyTextToClipboard();
    }

    public String getSelectedText() {
        return sh.getSelectedText();
    }

    public void resetSelection() {
        sh.resetSelection();
    }
}

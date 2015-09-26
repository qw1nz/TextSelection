package com.dwdm.textselection.selection;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;


/**
 * Created by a.kapitonov on 11.06.2015.
 */
public class SelectableListView extends ListView {

    private SelectionController sh;


    public SelectableListView(Context context) {
        this(context, null);
    }

    public SelectableListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelectableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
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

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        sh = new SelectionController(this);
        /*sh.setSelectionEnableListener(new SelectionController.SelectionEnableListener() {
            @Override
            public boolean isEnabled() {
                return PrefUtils.isTextSelectionEnabled(getContext());
            }
        });
        setSetupChildListener(new SetupChildListener() {
            @Override
            public void setupChild(View view) {
                if (sh != null) sh.addViewToSelectable(view);
            }
        });

        setRecyclerListener(new RecyclerListener() {
            @Override
            public void onMovedToScrapHeap(View view) {
                if (sh != null) sh.checkSelectableList();
            }
        });

        setOffsetListener(new OffsetListener() {
            @Override
            public void offsetChildren(int offset) {
                if (sh != null) sh.checkHandlesPosition();
            }
        });*/
    }

    public void onEventMainThread(SelectionResetEvent event){
        resetSelection();
    }

    @Override
    protected void onAttachedToWindow() {
        //EventBus.getDefault().register(this);
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        //EventBus.getDefault().unregister(this);
        super.onDetachedFromWindow();
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

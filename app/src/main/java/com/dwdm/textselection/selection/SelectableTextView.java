package com.dwdm.textselection.selection;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.util.AttributeSet;
import android.widget.TextView;

import com.dwdm.textselection.R;


/**
 * Created by a.kapitonov on 25.12.2014.
 */
public class SelectableTextView extends TextView implements Selectable {

    private final Rect rectTop = new Rect(0, 0, 0, 0);
    private final Rect rectMiddle = new Rect(0, 0, 0, 0);
    private final Rect rectBottom = new Rect(0, 0, 0, 0);
    private int endSelection = 0;
    private int startSelection = 0;
    private final Paint paint = new Paint();
    private Rect bounds;
    private int selectionColor = R.color.text_selection;
    private String key;

    public SelectableTextView(Context context) {
        super(context);
    }

    public SelectableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SelectableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public int getOffsetForPosition(int x, int y, boolean correctY) {
        if (getLayout() == null) return -1;

        final int line = getLineAtCoordinate(y);
        return getOffsetAtCoordinate(line, x);

    }

    @Override
    public float[] getPositionForOffset(int offset, final float[] position){
        Layout layout = getLayout();
        if (layout == null){
            position[0] = -1;
            position[1] = -1;
            return position;
        }

        int[] location = new int[2];
        int line = getLayout().getLineForOffset(offset);
        getLocationOnScreen(location);
        position[0] = getLayout().getPrimaryHorizontal(offset) + location[0] + getCompoundPaddingRight();
        position[1] = getLayout().getLineBottom(line) + location[1];
        return position;
    }

    private int getOffsetAtCoordinate(int line, float x) {
        x = convertToLocalHorizontalCoordinate(x);
        return getLayout().getOffsetForHorizontal(line, x);
    }

    private float convertToLocalHorizontalCoordinate(float x) {
        x -= getTotalPaddingLeft();
        // Clamp the position to inside of the view.
        x = Math.max(0.0f, x);
        x = Math.min(getWidth() - getTotalPaddingRight() - 1, x);
        x += getScrollX();
        return x;
    }

    private int getLineAtCoordinate(float y) {
        y -= getTotalPaddingTop();
        // Clamp the position to inside of the view.
        y = Math.max(0.0f, y);
        y = Math.min(getHeight() - getTotalPaddingBottom() - 1, y);
        y += getScrollY();
        return getLayout().getLineForVertical((int) y);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (startSelection == 0 && endSelection == 0) {
            super.onDraw(canvas);
            return;
        }

        paint.setColor(getResources().getColor(selectionColor));

        final int save = canvas.save();
        canvas.translate(getPaddingLeft(), getPaddingTop());

        canvas.drawRect(rectTop, paint);
        canvas.drawRect(rectMiddle, paint);
        canvas.drawRect(rectBottom, paint);

        canvas.restoreToCount(save);

        super.onDraw(canvas);
    }

    public void selectText(int start, int end){
        if (startSelection == start && endSelection == end) {
            return;
        }

        startSelection = start;
        endSelection = end;
        if (start == 0 && end == 0){
            rectTop.left = 0;
            rectTop.top = 0;
            rectTop.right = 0;
            rectTop.bottom = 0;

            rectMiddle.left = 0;
            rectMiddle.top = 0;
            rectMiddle.right = 0;
            rectMiddle.bottom = 0;

            rectBottom.left = 0;
            rectBottom.top = 0;
            rectBottom.right = 0;
            rectBottom.bottom = 0;
            invalidate();
            return;
        }

        if (start == 0 && end == getText().length()) {
            rectTop.left = 0;
            rectTop.top = 0;
            rectTop.right = 0;
            rectTop.bottom = 0;

            rectMiddle.left = 0;
            rectMiddle.top = 0;
            rectMiddle.right = getLayout().getWidth();
            rectMiddle.bottom = getLayout().getHeight();

            rectBottom.left = 0;
            rectBottom.top = 0;
            rectBottom.right = 0;
            rectBottom.bottom = 0;
            invalidate();
            return;
        }
        generateRects();
        invalidate();
    }

    @Override
    public CharSequence getSelectedText() {
        if (startSelection >= 0 && startSelection < endSelection)
            return getText().toString().substring(startSelection, endSelection);
        else
            return "";
    }

    @Override
    public boolean isInside(int evX, int evY) {
        int[] location = new int[2];
        getLocationOnScreen(location);
        int left = location[0];
        int right = left + getWidth();
        int top = location[1];
        int bottom = top + getHeight();
        return left <= evX && right >= evX && top <= evY && bottom >= evY;
    }

    @Override
    public void setColor(int selectionColor) {
        if (this.selectionColor == selectionColor)
            return;

        this.selectionColor = selectionColor;
        invalidate();
    }

    @Override
    public int getStartSelection() {
        return startSelection;
    }

    @Override
    public int getEndSelection() {
        return endSelection;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void setKey(String key) {
        this.key = key;
    }

    private void generateRects() {
        Layout layout = getLayout();
        if (layout == null){
            return ;
        }

        int lineS = getLayout().getLineForOffset(startSelection);
        float xS = getLayout().getPrimaryHorizontal(startSelection);
        float ySTop = getLayout().getLineTop(lineS);
        float ySBottom = getLayout().getLineBottom(lineS);

        int lineE = getLayout().getLineForOffset(endSelection);
        float xE = getLayout().getPrimaryHorizontal(endSelection);
        float yETop = getLayout().getLineTop(lineE);
        float yEBottom = getLayout().getLineBottom(lineE);

        if (lineS == lineE){
            rectTop.left = 0;
            rectTop.top = 0;
            rectTop.right = 0;
            rectTop.bottom = 0;

            rectMiddle.left = (int)xS;
            rectMiddle.top = (int)ySTop;
            rectMiddle.right = (int)xE;
            rectMiddle.bottom = (int)yEBottom;

            rectBottom.left = 0;
            rectBottom.top = 0;
            rectBottom.right = 0;
            rectBottom.bottom = 0;

            return;
        }

        if (lineE - lineS == 1){
            rectTop.left = (int)xS;
            rectTop.top = (int)ySTop;
            rectTop.right = getLayout().getWidth();
            rectTop.bottom = (int)ySBottom;

            rectMiddle.left = 0;
            rectMiddle.top = 0;
            rectMiddle.right = 0;
            rectMiddle.bottom = 0;

            rectBottom.left = 0;
            rectBottom.top = (int)yETop;
            rectBottom.right = (int)xE;
            rectBottom.bottom = (int)yEBottom;

            return;
        }
        rectTop.left = (int)xS;
        rectTop.top = (int)ySTop;
        rectTop.right = getLayout().getWidth();
        rectTop.bottom = (int)ySBottom;

        rectMiddle.left = 0;
        rectMiddle.top = (int)ySBottom;
        rectMiddle.right =  getLayout().getWidth();
        rectMiddle.bottom = (int)yETop;

        rectBottom.left = 0;
        rectBottom.top = (int)yETop;
        rectBottom.right = (int)xE;
        rectBottom.bottom = (int)yEBottom;
    }
}

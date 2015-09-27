package com.dwdm.textselection.selection;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.*;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.*;
import android.widget.Toast;

import com.dwdm.textselection.R;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class SelectionController {

    private ViewGroup selectableViewGroup;

    private static final int DEFAULT_CURSOR_HEIGTH = 70;
    private static final int DEFAULT_CURSOR_WIDTH = 50;
    public static final int LAST_SYMBOL = -2;
    public static final int FIRST_SYMBOL = -3;
    private static final int DEFAULT_RIGHT_HANDLE_DRAWABLE_RES = R.drawable.text_select_handle_right;
    private static final int DEFAULT_LEFT_HANDLE_DRAWABLE_RES = R.drawable.text_select_handle_left;
    private static final int DEFAULT_SELECTION_COLOR_RES = R.color.text_selection;

    private static final Pattern LetterDigitPattern = Pattern.compile("[A-Za-z0-9]");

    private ArrayList<SelectableInfo> selectableInfos = new ArrayList<>();
    private Handle rightHandle;

    private Handle leftHandle;
    private boolean selectInProcess = false;
    private boolean needReplace = true;
    private ActionMode.Callback actionModeCallback;

    private ActionMode actionMode;
    private float rightHandlePadding = 0.25f;
    private float leftHandlePadding = 0.25f;
    private GestureDetector gestureDetector;
    private HandleTouchEvent rightHandleListener;

    private HandleTouchEvent leftHandleListener;
    private SelectionCallback selectionCallback;
    private SelectionEnableListener selectionEnableListener;

    public SelectionController(ViewGroup selectableViewGroup) {
        this.selectableViewGroup = selectableViewGroup;
        initHandles();
        initHandlesEvents();
        initGesture();
    }

    private void setHandlersValues() {
        rightHandle.setDefaultValues();
        leftHandle.setDefaultValues();

        rightHandle.setHandleImage(BitmapFactory.decodeResource(selectableViewGroup.getResources(), DEFAULT_RIGHT_HANDLE_DRAWABLE_RES));
        leftHandle.setHandleImage(BitmapFactory.decodeResource(selectableViewGroup.getResources(), DEFAULT_LEFT_HANDLE_DRAWABLE_RES));
    }

    private void initHandlesEvents() {
        rightHandleListener = new HandleTouchEvent(rightHandle);
        leftHandleListener = new HandleTouchEvent(leftHandle);
    }

    private int getCursorPosition(float x, float y, int currCursorPosition) {
        int cursorPos = getCursorPosition(x, y);
        if (cursorPos == -1)
            return currCursorPosition;

        return cursorPos;
    }

    private int getCursorPosition(float x, float y) {
        int totalPos = 0;
        int pos = 0;
        int cursorPos = -1;

        for (SelectableInfo selectableInfo : selectableInfos) {
            final Selectable stv = selectableInfo.getSelectable();
            if (stv != null && stv.getVisibility() == View.VISIBLE) {
                int[] locationSTV = new int[2];
                stv.getLocationOnScreen(locationSTV);
                int left = locationSTV[0];
                int right = left + stv.getWidth();
                int top = locationSTV[1];
                int bottom = top + stv.getHeight();
                int[] location = new int[2];
                selectableViewGroup.getLocationOnScreen(location);
                if (top <= y && bottom >= y) {
                    if (left <= x && right >= x) {
                        pos = stv.getOffsetForPosition((int) (x - left), (int) (y - top));
                        cursorPos = totalPos + pos;
                        break;
                    } else if (x < left) {
                        pos = stv.getOffsetForPosition(FIRST_SYMBOL, (int) (y - top));
                        cursorPos = totalPos + pos;
                        break;
                    } else if (x > right) {
                        pos = stv.getOffsetForPosition(LAST_SYMBOL, (int) (y - top));
                        cursorPos = totalPos + pos;
                        break;
                    }
                }
            }
            totalPos += selectableInfo.getText().length();
        }

        if (pos == -1)
            return pos;
        else
            return cursorPos;
    }

    private void initHandles() {
        rightHandle = new Handle();
        leftHandle = new Handle();
    }

    private void initGesture() {
        gestureDetector = Build.VERSION.SDK_INT < 11 ? null : new GestureDetector(selectableViewGroup.getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                if (!selectInProcess && isEnabled()) {
                    startSelection(e);
                }
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if (selectInProcess) {
                    copyToClipBoard(stopSelection().toString());
                  //  stopSelection();
                }
                return super.onSingleTapUp(e);
            }
        });
    }

    private boolean isEnabled() {
        return selectionEnableListener == null || selectionEnableListener.isEnabled();
    }

    public void addViewToSelectable(View view) {
        checkSelectableList();
        if (view instanceof Selectable){
            addSelectableToSelectableInfos((Selectable) view);
        } else if (view instanceof ViewGroup){
            findSelectableTextView((ViewGroup) view);
        }
    }

    public void findSelectableTextView(ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++){
            View view = viewGroup.getChildAt(i);
            if (view instanceof Selectable){
                addSelectableToSelectableInfos((Selectable) view);
                continue;
            }
            if (view instanceof ViewGroup){
                findSelectableTextView((ViewGroup) view);
            }
        }
    }

    private void addSelectableToSelectableInfos(Selectable selectable) {
        boolean found = false;
        for (SelectableInfo selectableInfo : selectableInfos) {
            if (selectableInfo.getKey().equals(selectable.getKey())) {
                selectableInfo.setSelectable(selectable);
                found = true;
                Log.d(">>>>>", "exist selectable = " + selectableInfo.getSelectable());
                break;
            }
        }
        if (!found) {
            final SelectableInfo selectableInfo = new SelectableInfo(selectable);
            selectableInfos.add(selectableInfo);
            Log.d(">>>>>", "new selectable = " + selectableInfo.getSelectable());
        }
    }

    public void checkSelectableList() {
        for (SelectableInfo selectableInfo : selectableInfos) {
            if (selectableInfo.getSelectable() != null) {
                if (!selectableInfo.getSelectable().getKey().equals(selectableInfo.getKey())) {
                    Log.d(">>>>>", "removeSelectable getKey "+selectableInfo.getSelectable());
                    selectableInfo.removeSelectable();
                    continue;
                }

                if (!isSvgParent((View) selectableInfo.getSelectable())) {
                    Log.d(">>>>>", "removeSelectable isSvgParent "+selectableInfo.getSelectable());
                    selectableInfo.removeSelectable();
                }
            }
        }

    }

    private boolean isSvgParent(View view) {
        ViewParent parent = view.getParent();
        while (parent != null && !parent.equals(selectableViewGroup)) {
            parent = parent.getParent();
        }
        return parent != null;
    }

    public void drawHandles(Canvas canvas) {
        if (!selectInProcess)
            return;

        rightHandle.draw(canvas);
        leftHandle.draw(canvas);
    }

    public boolean onTouchEvent(MotionEvent ev) {
        if (gestureDetector != null)
            gestureDetector.onTouchEvent(ev);
        boolean dispatched = false;
        if (selectInProcess) {
            boolean right = rightHandleListener.onTouchHandle(ev);
            boolean left = leftHandleListener.onTouchHandle(ev);
            dispatched = right || left;
        }
        return dispatched;
    }

    private void startSelection(MotionEvent e) {
        selectInProcess = setFirstCursorsPosition(e);
        if (selectInProcess && selectionCallback != null) {
            selectionCallback.startSelection();
        }
        if (selectInProcess && actionModeCallback != null)
            actionMode = selectableViewGroup.startActionMode(actionModeCallback);
    }

    private boolean setFirstCursorsPosition(MotionEvent e) {
        setHandlersValues();
        int totalPos = 0;
        int pos = -1;
        int[] location = new int[2];
        String text = "";
        selectableViewGroup.getLocationOnScreen(location);
        for (SelectableInfo selectableInfo : selectableInfos) {
            final Selectable selectable = selectableInfo.getSelectable();
            if (selectable != null && selectable.getVisibility() == View.VISIBLE) {
                int[] locationSelectable = new int[2];
                selectable.getLocationOnScreen(locationSelectable);
                int left = locationSelectable[0];
                int top = locationSelectable[1];
                int evX = (int) (e.getX() + location[0]);
                int evY = (int) (e.getY() + location[1]);
                if (selectable.isInside(evX, evY)) {
                    pos = selectable.getOffsetForPosition(evX - left, evY - top);
                    text = selectable.getText().toString();
                    break;
                }
            }
            totalPos += selectableInfo.getText().length();
        }
        if (pos == -1){ //view not found
            return false;
        }

        int[] handlesPosition = getHandlesPosition(text, pos);

        leftHandle.position = handlesPosition[0] + totalPos;
        rightHandle.position = handlesPosition[1] + totalPos;
        needReplace = true;
        rightHandle.correctX = 0 - rightHandle.width * rightHandlePadding;
        leftHandle.correctX = 0 -  leftHandle.width + leftHandle.width * leftHandlePadding;
        setHandleCoordinate(rightHandle);
        setHandleCoordinate(leftHandle);
        setSelectionText();
        selectableViewGroup.invalidate();
        return true;
    }

    private int[] getHandlesPosition(final String text, final int pos) {
        final int[] handlesPosition = new int[2];
        final int textLength = text.length();
        handlesPosition[0] = 0;
        for (int i = pos; i >= 0; i--) {
            if (!LetterDigitPattern.matcher(String.valueOf(text.charAt(i))).matches()){
                handlesPosition[0] = i + 1;
                break;
            }
        }

        handlesPosition[1] = textLength - 1;
        for (int i = pos; i < textLength; i++) {
            if (!LetterDigitPattern.matcher(String.valueOf(text.charAt(i))).matches()){
                handlesPosition[1] = i;
                break;
            }
        }
        return handlesPosition;
    }


    private void setSelectionText() {
        int totalPos = 0;
        int start;
        int end;
        int leftCursorPos = leftHandle.position;
        int rightCursorPos = rightHandle.position;
        if (leftCursorPos > rightCursorPos){
            rightCursorPos = leftHandle.position;
            leftCursorPos = rightHandle.position;
        }
        for (SelectableInfo selectableInfo : selectableInfos) {

            String text = selectableInfo.getText().toString();
            int length = text.length();
            start = 0;
            end = 0;
            if (totalPos <= leftCursorPos && leftCursorPos < totalPos + length) {
                if (totalPos < rightCursorPos && rightCursorPos <= totalPos + length) {
                    start = leftCursorPos - totalPos;
                    end = rightCursorPos - totalPos;
                } else {
                    start = leftCursorPos - totalPos;
                    end = length;
                }
            }
            if (totalPos > leftCursorPos && totalPos + length <= rightCursorPos) {
                start = 0;
                end = length;
            }
            if (totalPos > leftCursorPos && totalPos + length > rightCursorPos && totalPos < rightCursorPos) {
                start = 0;
                end = rightCursorPos - totalPos;
            }
            selectableInfo.selectText(start, end);
            totalPos += length;
        }
    }

    private CharSequence stopSelection() {
        if (!selectInProcess) {
            return "";
        }
        if (actionMode != null)
            actionMode.finish();

        CharSequence selection = getSelection(true);

        //TODO: clean current selection
        //selectionInfoMap = new HashMap<>();
       // selectables = null; //TODO create selectables only if selection in progress

        selectInProcess = false;
        selectableViewGroup.invalidate();

        if (selectionCallback != null) {
            selectionCallback.stopSelection();
        }

        return selection;
    }

    private void copyToClipBoard(String s) {
        ClipboardManager clipboard = (ClipboardManager) selectableViewGroup.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Article", s);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(selectableViewGroup.getContext(), "Text was copied to clipboard", Toast.LENGTH_LONG).show();
    }

    private CharSequence getSelection(boolean reset) {
        SpannableStringBuilder strBuilder = new SpannableStringBuilder();
        for (SelectableInfo selectableInfo : selectableInfos) {
            final CharSequence selectedText = selectableInfo.getSelectedText();

            strBuilder.append(selectedText);

            if (!selectedText.toString().isEmpty()) {
                strBuilder.append("\n");
            }

            if (reset) {
                selectableInfo.resetSelection();
            }
        }
        if (strBuilder.length() > 0) {
            strBuilder.delete(strBuilder.length() - 1, strBuilder.length());
        }
        return strBuilder;
    }

    private void setHandleCoordinate(Handle handle) {
        Selectable textView = null;
        int totalPos = 0;
        for (SelectableInfo selectableInfo : selectableInfos) {
            String text = selectableInfo.getText().toString();
            int length = text.length();
            if (handle.position >= totalPos && handle.position < totalPos + length) {
                textView = selectableInfo.getSelectable();
                break;
            }
            totalPos += length;
        }
        if (textView == null) {
            handle.visible = false;
            return;
        }

        if (!isSvgParent((View)textView)) {
            handle.visible = false;
            checkSelectableList();
            return;
        }

        handle.visible = true;

        float[] coordinate = new float[2];
        coordinate = textView.getPositionForOffset(handle.position - totalPos, coordinate);
        int[] location = new int[2];
        selectableViewGroup.getLocationOnScreen(location);
        if (coordinate[0] == -1 || coordinate[1] == -1)
            return;

        handle.x = coordinate[0] - location[0] + handle.correctX;
        handle.y = coordinate[1] - location[1];

    }

    private void checkBackground() {
        if (leftHandle.position > rightHandle.position && needReplace){
            Bitmap temp = rightHandle.handleImage;
            rightHandle.setHandleImage(leftHandle.handleImage);
            leftHandle.setHandleImage(temp);
            rightHandle.correctX = (rightHandle.correctX - rightHandle.width + rightHandle.width * rightHandlePadding * 2);
            leftHandle.correctX = (leftHandle.correctX + leftHandle.width - leftHandle.width * leftHandlePadding * 2);
            needReplace = ! needReplace;
            setHandleCoordinate(rightHandle);
            setHandleCoordinate(leftHandle);
            return;
        }
        if (leftHandle.position <= rightHandle.position && !needReplace){
            Bitmap temp = rightHandle.handleImage;
            rightHandle.setHandleImage(leftHandle.handleImage);
            leftHandle.setHandleImage(temp);
            rightHandle.correctX = (rightHandle.correctX + rightHandle.width - rightHandle.width * rightHandlePadding * 2);
            leftHandle.correctX = (leftHandle.correctX - leftHandle.width + leftHandle.width * leftHandlePadding * 2);
            needReplace = !needReplace;
            setHandleCoordinate(rightHandle);
            setHandleCoordinate(leftHandle);
            return;
        }
    }

    public void checkHandlesPosition() {
        if (!selectInProcess)
            return;

        setHandleCoordinate(rightHandle);
        setHandleCoordinate(leftHandle);
        selectableViewGroup.postInvalidate();
    }

    public void setSelectionCallback(SelectionCallback selectionCallback) {
        this.selectionCallback = selectionCallback;
    }

    public void copyTextToClipboard() {
        copyToClipBoard(stopSelection().toString());
    }

    public String getSelectedText() {
        return stopSelection().toString();
    }

    public void resetSelection() {
        stopSelection();
    }

    public void setSelectionEnableListener(SelectionEnableListener selectionEnableListener) {
        this.selectionEnableListener = selectionEnableListener;
    }

    private class Handle {
        private float x;
        private float y;
        private float width;
        private float height;
        private int position;
        private float correctX;
        private boolean isMoving;
        private boolean visible = true;
        public Bitmap handleImage;
        private Paint paint = new Paint();

        Handle() {
            setDefaultValues();
        }

        public void setDefaultValues() {
            paint.setColor(Color.RED);
            x = 0;
            y = 0;
            correctX = 0;
            position = -1;
            width = DEFAULT_CURSOR_WIDTH;
            height = DEFAULT_CURSOR_HEIGTH;
            isMoving = false;
        }

        public boolean contains(float x, float y) {
            return this.x <= x
                    && this.y <= y
                    && (this.x + width) >= x
                    && (this.y + height) >= y;
        }

        public void draw(Canvas canvas) {
            if (!visible)
                return;

            if (handleImage == null) {
                canvas.drawRect(x, y, x + width, y + height, paint);
            } else {
                canvas.drawBitmap(handleImage, x, y, paint);
            }
        }

        public void setHandleImage(Bitmap bitmap) {
            handleImage = bitmap;
            width = bitmap.getWidth();
            height = bitmap.getHeight();
        }
    }

    private class HandleTouchEvent{
        private int x;
        private int y;
        private int yDelta;
        private int xDelta;
        private Handle handle;

        HandleTouchEvent(Handle handle){
            this.handle = handle;
        }

        public boolean onTouchHandle(MotionEvent event) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    handle.isMoving = handle.contains(event.getX(), event.getY());
                    if (handle.isMoving) {
                        yDelta = (int) (event.getY() - handle.y + 1);
                        xDelta = (int) (event.getX() - handle.x + handle.correctX);
                        selectableViewGroup.getParent().requestDisallowInterceptTouchEvent(true);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    handle.isMoving = false;
                    selectableViewGroup.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (handle.isMoving) {
                        x = (int) (event.getRawX() - xDelta);
                        y = (int) (event.getRawY() - yDelta);
                        int oldHandlePos = handle.position;
                        handle.position = getCursorPosition(x, y, handle.position);

                        if (handle.position != oldHandlePos) {
                            setHandleCoordinate(handle);
                            setSelectionText();
                            checkBackground();
                            selectableViewGroup.invalidate();
                        }
                    }
                    break;
            }
            return handle.isMoving;
        }
    }


    public interface SelectionEnableListener {
        boolean isEnabled();
    }

}

package com.dwdm.textselection.selection;

/**
 * Created by a.kapitonov on 22.01.2015.
 */
public interface Selectable {
    int getOffsetForPosition(int x, int y);
    int getVisibility();
    CharSequence getText();
    void setText(CharSequence text);
    void getLocationOnScreen(int[] location);
    int getHeight();
    int getWidth();
    float[] getPositionForOffset(int offset, float[] position);
    void selectText(int start, int end);
    CharSequence getSelectedText();
    boolean isInside(int evX, int evY);
    void setColor(int selectionColor);
    int getStartSelection();
    int getEndSelection();
    String getKey();
    void setKey(String key);
}

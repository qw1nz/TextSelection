package com.dwdm.textselection.selection;

/**
 * Created by a.kapitonov on 16.06.2015.
 */
public class SelectableInfo {

    private int start;
    private int end;
    private CharSequence selectedText;
    private CharSequence text;
    private String key;
    private Selectable selectable;

    public SelectableInfo(Selectable selectable) {
        this.start = 0;
        this.end = 0;
        this.selectedText = "";
        this.selectable = selectable;
        this.text = selectable.getText();
        this.key = selectable.getKey();
    }

    public void removeSelectable() {
        this.selectable.selectText(0, 0);
        this.selectable = null;
    }

    public void selectText(int start, int end) {
        this.start = start;
        this.end = end;
        if (selectable != null) {
            selectable.selectText(start, end);
            selectedText = selectable.getSelectedText();
        } else {
            selectedText = text.toString().substring(start, end);
        }
    }

    public void resetSelection() {
        selectText(0, 0);
    }


    public Selectable getSelectable() {
        return selectable;
    }

    public void setSelectable(Selectable selectable) {
        this.selectable = selectable;
        selectText(start, end);
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public CharSequence getSelectedText() {
        return selectedText;
    }


    public void setStart(int start) {
        this.start = start;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public void setSelectedText(CharSequence selectedText) {
        this.selectedText = selectedText;
    }

    public CharSequence getText() {
        return text;
    }

    public void setText(CharSequence text) {
        this.text = text;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}

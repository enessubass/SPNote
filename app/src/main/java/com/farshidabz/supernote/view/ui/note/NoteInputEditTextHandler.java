package com.farshidabz.supernote.view.ui.note;

import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.widget.EditText;

import com.farshidabz.supernote.R;
import com.farshidabz.supernote.util.widgets.DrawingView;
import com.farshidabz.supernote.view.ui.note.textstyle.TextStyle;

/**
 * Created by FarshidAbz.
 * Since 4/14/2017.
 */

public class NoteInputEditTextHandler {
    private EditText inputEditText;
    private int paperStyleId;
    private int paperId;
    private int textStyle;

    private int textColorId;
    private int startPos;
    private int endPos;

    public NoteInputEditTextHandler(EditText inputEditText) {
        this.inputEditText = inputEditText;
    }

    public void setPaperStyle(int paperStyleId, int paperId) {
        this.paperStyleId = paperStyleId;
        this.paperId = paperId;

        inputEditText.setBackgroundResource(paperId);
    }

    public void setTextStyle(@TextStyle int textStyle, int textColorId) {
        this.textStyle = textStyle;
        this.textColorId = textColorId;

        Spannable spannable = inputEditText.getText();

        spannable.setSpan(new StyleSpan(textStyle), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(inputEditText.getContext().getResources().getColor(textColorId)), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        inputEditText.setText(spannable);
        inputEditText.setSelection(inputEditText.getText().length());
    }

    public int getBackgroundResId() {
        if (paperStyleId == 0) {
            return R.drawable.line_edit_text;
        }

        return paperStyleId;
    }

    public int getPaperStyleId() {
        if (paperStyleId == 0) {
            return R.drawable.line_style;
        }

        return paperStyleId;
    }

    public int getTextStyle() {
        if (textStyle == 0)
            return TextStyle.REGULAR;

        return textStyle;
    }

    public int getTextColorId() {
        if (textColorId == 0)
            return R.color.black;

        return textColorId;
    }

    public void setInputTypeMode(boolean writingMode, DrawingView drawingView) {
        if (writingMode)
            drawingView.canDraw(false);
        else
            drawingView.canDraw(true);

        inputEditText.setOnTouchListener((v, event) -> {
            if (!writingMode) {
                drawingView.onTouchEvent(event);
                return true;
            } else {
                return false;
            }
        });
    }

    public void setSelectionPos(int startPos, int endPos) {
        this.startPos = startPos;
        this.endPos = endPos;
    }
}

package com.farshidabz.supernote.util.widgets.popupwindow;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.farshidabz.supernote.R;
import com.farshidabz.supernote.model.PopupModel;

import java.util.ArrayList;

/**
 * Created by FarshidAbz.
 * Since 4/15/2017.
 */

public class CustomPopupWindow {
    private Context context;

    private PopupWindow popupWindow;

    private RecyclerView popupWindowRecycler;
    private ArrayList<PopupModel> inputList;
    private PopupWindowAdapter popupWindowAdapter;

    private OnPopupItemClickListener onPopupItemClickListener;

    private View anchorView;

    private boolean isPopupWindowShowing;

    private static final String TAG = "CustomPopupWindow";

    public CustomPopupWindow(Context context) {
        this.context = context;
    }

    public CustomPopupWindow(Context context, ArrayList<PopupModel> inputList) {
        this.context = context;
        this.inputList = inputList;
    }

    public CustomPopupWindow(Context context, ArrayList<PopupModel> inputList, View anchorView) {
        this.context = context;
        this.inputList = inputList;
        this.anchorView = anchorView;
    }

    public CustomPopupWindow(Context context, ArrayList<PopupModel> inputList, View anchorView, PopupWindow popupWindow) {
        this.context = context;
        this.inputList = inputList;
        this.anchorView = anchorView;
        this.popupWindow = popupWindow;
    }

    public void setAnchorView(View anchorView) {
        this.anchorView = anchorView;
    }

    public void setOnPopupItemClickListener(OnPopupItemClickListener onPopupItemClickListener) {
        this.onPopupItemClickListener = onPopupItemClickListener;
    }

    public void setInputList(ArrayList<PopupModel> inputList) {
        this.inputList = inputList;
    }

    public void show(View anchorView) {
        this.anchorView = anchorView;
        show();
    }

    public void show() {
        new Handler().post(() -> {
            if (anchorView == null) {
                Log.e(TAG, "show: anchor view can not be null");
                return;
            }

            if (inputList == null || inputList.size() <= 0) {
                Log.e(TAG, "show: filter list size is 0");
                popupWindowAdapter.removeAll();
                return;
            }

            initRecyclerView();
            initPopupWindow();

            popupWindow.setContentView(popupWindowRecycler);
            showPopupWindow();
        });
    }

    private void showPopupWindow() {
        if (!isPopupWindowShowing) {
            popupWindow.dismiss();
            popupWindow.showAsDropDown(anchorView);
            isPopupWindowShowing = true;
        }
    }

    private void initPopupWindow() {
        if (popupWindow != null) {
            return;
        }

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        popupWindow = new PopupWindow(inflater.inflate(R.layout.content_popupwindow, null),
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        popupWindow.setFocusable(true);
        popupWindow.setOnDismissListener(() -> {
            popupWindow.dismiss();
            isPopupWindowShowing = false;
        });
    }

    private void initRecyclerView() {
        popupWindowRecycler = new RecyclerView(context);
        popupWindowAdapter = new PopupWindowAdapter();
        popupWindowAdapter.setInputAdapter(inputList);
        popupWindowAdapter.setOnClickListener((position, popupModel) -> {
            onPopupItemClickListener.onItemClickListener(position, popupModel);
            popupWindow.dismiss();
            isPopupWindowShowing = false;
        });

        popupWindowRecycler.setAdapter(popupWindowAdapter);
        popupWindowRecycler.setLayoutManager(new LinearLayoutManager(context));
    }
}

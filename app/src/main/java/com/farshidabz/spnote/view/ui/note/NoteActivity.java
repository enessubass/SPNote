package com.farshidabz.spnote.view.ui.note;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.farshidabz.spnote.R;
import com.farshidabz.spnote.model.NoteModel;
import com.farshidabz.spnote.util.widgets.DrawingView;
import com.farshidabz.spnote.view.ui.base.BaseActivity;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NoteActivity extends BaseActivity implements NoteMvpView {

    private static final int REQ_CODE_SPEECH_INPUT = 75;
    @BindView(R.id.NoteActivityToolbar)
    Toolbar noteActivityToolbar;
    @BindView(R.id.imgTextStyle)
    ImageView imgTextStyle;
    @BindView(R.id.etContentField)
    EditText etContentField;
    @BindView(R.id.imgEraser)
    ImageView imgEraser;
    @BindView(R.id.imgDrawingPen)
    ImageView imgDrawingPen;
    @BindView(R.id.drawingView)
    DrawingView drawingView;
    @BindView(R.id.imgDraw)
    ImageView imgDraw;
    @BindView(R.id.tvInputType)
    TextView tvInputType;
    @BindView(R.id.llInputTypeSwitcher)
    LinearLayout llInputTypeSwitcher;

    @BindView(R.id.adView)
    AdView adView;

    NoteMvpPresenter noteMvpPresenter;

    private FirebaseAnalytics firebaseAnalytics;

    private int noteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_note);

        ButterKnife.bind(this);

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        initAdView();

        noteMvpPresenter = new NotePresenter(this,
                getSupportFragmentManager(),
                drawingView,
                etContentField);

        noteMvpPresenter.onAttach(this);
        iniToolbar();

        getData();
    }

    private void initAdView() {
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                Bundle bundle = new Bundle();
                bundle.putInt("ad_mob_failed", i);
                firebaseAnalytics.logEvent("ad_mob", bundle);
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                Bundle bundle = new Bundle();
                bundle.putBoolean("ad_mob_opened", true);
                firebaseAnalytics.logEvent("ad_mob", bundle);
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Bundle bundle = new Bundle();
                bundle.putBoolean("ad_mob_loaded", true);
                firebaseAnalytics.logEvent("ad_mob", bundle);
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
                Bundle bundle = new Bundle();
                bundle.putBoolean("ad_mob_clicked", true);
                firebaseAnalytics.logEvent("ad_mob", bundle);
            }
        });
    }

    private void getData() {
        noteId = getIntent().getIntExtra("noteId", -1);
        int folderId = getIntent().getIntExtra("folderId", -1);

        noteMvpPresenter.getNote(noteId, folderId);
    }

    private void iniToolbar() {
        setSupportActionBar(noteActivityToolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speak));

        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException ignored) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.text_note_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnuDiscard:
                noteMvpPresenter.onBackClicked(false, noteId);
                break;
            case R.id.mnuPaperStyle:
                noteMvpPresenter.onPaperStyleClicked();
                break;
            case android.R.id.home:
                noteMvpPresenter.onBackClicked(true, noteId);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        noteMvpPresenter.onBackClicked(true, noteId);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                etContentField.setText(etContentField.getText().append(result.get(0)) + " ");
                etContentField.setSelection(etContentField.getText().length());
            }
        }
    }

    @OnClick(R.id.imgTextStyle)
    public void onImgTextStyleClicked() {
        noteMvpPresenter.onTextStyleClicked();
    }

    @OnClick(R.id.llInputTypeSwitcher)
    public void onSwitchModeClicked() {
        noteMvpPresenter.onInputTypeSwitcherClicked(llInputTypeSwitcher);
        noteMvpPresenter.hideKeyboard();
    }

    @OnClick(R.id.imgEraser)
    public void onEraserClicked() {
        imgEraser.setVisibility(View.INVISIBLE);
        imgDraw.setVisibility(View.VISIBLE);
        noteMvpPresenter.onEraserClicked();
    }

    @OnClick(R.id.imgDraw)
    public void onImgDrawClicked() {
        imgDraw.setVisibility(View.INVISIBLE);
        imgEraser.setVisibility(View.VISIBLE);
        noteMvpPresenter.onDrawClicked();
    }

    @OnClick(R.id.imgDrawingPen)
    public void onImgDrawingPenClicked() {
        noteMvpPresenter.onDrawingStyleClicked();
    }

    @OnClick(R.id.fabVoiceToText)
    public void onVoiceToTextClicked() {
        startVoiceInput();
    }

    @Override
    public void onNoteReceived(NoteModel noteModel) {
    }

    @Override
    public void setEraserVisibility(int visibility) {
        imgEraser.setVisibility(visibility);
    }

    @Override
    public void setTextStyleVisibility(int visibility) {
        imgTextStyle.setVisibility(visibility);
    }

    @Override
    public void setDrawingPenVisibility(int visibility) {
        imgDrawingPen.setVisibility(visibility);
    }

    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    public void setInputTypeModeText(String text) {
        tvInputType.setText(text);
    }

    @Override
    public void setTextStyleColorBackground(@DrawableRes int drawable) {
        imgTextStyle.setImageResource(drawable);
    }

    @Override
    public void setDrawingPenColorBackground(@DrawableRes int drawable) {
        imgDrawingPen.setImageResource(drawable);
    }

    @Override
    public void showLoading() {
    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void onError(@StringRes int resId) {
    }

    @Override
    protected void setUp() {

    }

    @Override
    public void onError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
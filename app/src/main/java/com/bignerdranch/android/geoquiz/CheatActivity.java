package com.bignerdranch.android.geoquiz;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends AppCompatActivity {

    private final static String TAG = "CheatActivity";
    private static final String EXTRA_ANSWER_IS_TRUE = "com.bignerdranch.android.geoquiz.answer_is_true";
    private static final String EXTRA_ANSWER_SHOWN = "com.bignerdranch.android.geoquiz.answer_shown";
    private static final String EXTRA_CHEATS_COUNT = "com.bignerdranch.android.geoquiz.cheats_count";
    private static final String KEY_ANSWER_SHOWN = "answer_shown";
    private static final String KEY_CHEATS_COUNT = "cheats_count";
    private boolean mAnswerIsTrue;
    private boolean mAnswerIsShown;
    private TextView mAnswerTextView;
    private TextView mCheatsCountTextView;
    private Button mShowAnswerButton;
    private int mCheatsCount;

    public static Intent newIntent(Context packageContext, boolean answerIsTrue, int cheatsCount) {
        Intent intent = new Intent(packageContext, CheatActivity.class);
        intent.putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue);
        intent.putExtra(EXTRA_CHEATS_COUNT, cheatsCount);
        return intent;
    }

    public static boolean wasAnswerShown(Intent result) {
        return result.getBooleanExtra(EXTRA_ANSWER_SHOWN, false);
    }

    public static int getCheatsLeftCount(Intent result) {
        return result.getIntExtra(EXTRA_CHEATS_COUNT, 3);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);
        mCheatsCount = getIntent().getIntExtra(EXTRA_CHEATS_COUNT, 3);
        mAnswerTextView = findViewById(R.id.answer_text_view);
        mShowAnswerButton = findViewById(R.id.show_answer_button);
        mCheatsCountTextView = findViewById(R.id.cheats_count_text_view);

        if (savedInstanceState != null) {
            if (mAnswerIsTrue) {
                mAnswerTextView.setText(R.string.true_button);
            } else {
                mAnswerTextView.setText(R.string.false_button);
            }
            mAnswerIsShown = savedInstanceState.getBoolean(KEY_ANSWER_SHOWN, false);
            mCheatsCount = savedInstanceState.getInt(KEY_CHEATS_COUNT, 3);
            setAnswerShownResult(mAnswerIsShown, mCheatsCount);
        }

        if (mAnswerIsShown || mCheatsCount == 0) mShowAnswerButton.setEnabled(false);
        mShowAnswerButton.setOnClickListener(view -> {
            if (mAnswerIsTrue) {
                mAnswerTextView.setText(R.string.true_button);
            } else {
                mAnswerTextView.setText(R.string.false_button);
            }
            mAnswerIsShown = true;
            mCheatsCount--;
            mCheatsCountTextView.setText(String.format(getString(R.string.cheats_count_text), mCheatsCount));
            mShowAnswerButton.setEnabled(false);
            setAnswerShownResult(mAnswerIsShown, mCheatsCount);
        });

        mCheatsCountTextView.setText(String.format(getString(R.string.cheats_count_text), mCheatsCount));
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        savedInstanceState.putBoolean(KEY_ANSWER_SHOWN, mAnswerIsShown);
        savedInstanceState.putInt(KEY_CHEATS_COUNT, mCheatsCount);
    }

    private void setAnswerShownResult(boolean isAnswerShown, int cheatsCount) {
        Intent data = new Intent();
        data.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown);
        data.putExtra(EXTRA_CHEATS_COUNT, cheatsCount);
        setResult(RESULT_OK, data);
    }
}

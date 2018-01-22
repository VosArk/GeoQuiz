package com.bignerdranch.android.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class QuizActivity extends AppCompatActivity {

    private final static String TAG = "QuizActivity";
    private final static String KEY_INDEX = "index";
    private final static String KEY_ANSWERED_QUESTIONS = "questions";
    private final static String KEY_CORRECT_ANSWERS = "correct";
    private final static String KEY_INCORRECT_ANSWERS = "incorrect";
    private final static String KEY_CHEATS_ANSWERS = "cheats";
    private final static String KEY_IS_CHEATER = "is_cheater";
    private final static int REQUEST_CODE_CHEAT = 0;
    private Button mTrueButton;
    private Button mFalseButton;
    private ImageButton mCheatButton;
    private TextView mQuestionTextView;

    private Question[] mQuestionBank = new Question[]{
            new Question(R.string.question_africa, false),
            new Question(R.string.question_america, true),
            new Question(R.string.question_asia, true),
            new Question(R.string.question_australia, true),
            new Question(R.string.question_antarctica, true),
            new Question(R.string.question_oceans, false),
            new Question(R.string.question_mideast, false)
    };

    private int mCurrentIndex = 0;
    private boolean mIsCheater;
    private int mCorrectAnswersCount = 0;
    private int mIncorrectAnswersCount = 0;
    private int mCheatsCount = 0;
    private ArrayList<Integer> mAnsweredQuestions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);

        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mCorrectAnswersCount = savedInstanceState.getInt(KEY_CORRECT_ANSWERS, 0);
            mIncorrectAnswersCount = savedInstanceState.getInt(KEY_INCORRECT_ANSWERS, 0);
            mCheatsCount = savedInstanceState.getInt(KEY_CHEATS_ANSWERS, 0);
            mAnsweredQuestions = savedInstanceState.getIntegerArrayList(KEY_ANSWERED_QUESTIONS);
            mIsCheater = savedInstanceState.getBoolean(KEY_IS_CHEATER, false);
        }

        mQuestionTextView = findViewById(R.id.question_text_view);
        updateQuestion();

        mTrueButton = findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(view -> {
            checkAnswer(true);
            mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
            mIsCheater = false;
            updateQuestion();
        });

        mFalseButton = findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(view -> {
            checkAnswer(false);
            mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
            mIsCheater = false;
            updateQuestion();
        });

        mCheatButton = findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener((view) -> {
            boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
            Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue);
            startActivityForResult(intent, REQUEST_CODE_CHEAT);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_CHEAT) {
            if (data == null) {
                return;
            }
            mIsCheater = CheatActivity.wasAnswerShown(data);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
        savedInstanceState.putInt(KEY_CORRECT_ANSWERS, mCorrectAnswersCount);
        savedInstanceState.putInt(KEY_INCORRECT_ANSWERS, mIncorrectAnswersCount);
        savedInstanceState.putInt(KEY_CHEATS_ANSWERS, mCheatsCount);
        savedInstanceState.putIntegerArrayList(KEY_ANSWERED_QUESTIONS, mAnsweredQuestions);
        savedInstanceState.putBoolean(KEY_IS_CHEATER, mIsCheater);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }

    private void updateQuestion() {
        if (mCorrectAnswersCount + mIncorrectAnswersCount + mCheatsCount != mQuestionBank.length) {
            int question = mQuestionBank[mCurrentIndex].getTextResId();
            mQuestionTextView.setText(question);
        } else {
            mQuestionTextView.setText(String.format(getString(R.string.correct_results_text), mCorrectAnswersCount, mQuestionBank.length));
            mTrueButton.setEnabled(false);
            mFalseButton.setEnabled(false);
            mCheatButton.setImageResource(R.drawable.ic_cached_black_24dp);
            mCheatButton.setOnClickListener((view) -> {
                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getBaseContext().getPackageName());
                if (i != null) {
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                }
                finish();
                startActivity(i);
            });
        }
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        int messageResId;

        if (!mAnsweredQuestions.contains(mCurrentIndex)) {
            if (mIsCheater) {
                messageResId = R.string.judgment_toast;
                mCheatsCount++;
            } else {

                if (userPressedTrue == answerIsTrue) {
                    messageResId = R.string.correct_toast;
                    mCorrectAnswersCount++;
                } else {
                    messageResId = R.string.incorrect_toast;
                    mIncorrectAnswersCount++;
                }
            }
            Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
            mAnsweredQuestions.add(mCurrentIndex);
        }
    }
}

package com.greent.geoquiz;


import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class QuizActivity extends AppCompatActivity {

    private Button trueBtn;
    private Button falseBtn;
    private ImageButton next;
    private ImageButton previous;
    private TextView mQuestionTextView;
    private int turn;
    private int score;
    private boolean isAnswered;
    private static QuizViewModel qvm;
    private static final String saved_score = "score";
    private static final String saved_turn = "turn";
    private static final String key = "key";
    private Button cheatBtn;
    private static final int REQUEST_CODE_CHEAT = 0;
    private boolean mIsCheater;
    private static final String isCheater = "cheater";
    private int savedCheatIndex;
    private static final String cheat_index_key = "cheaterIndex";
    private TextView tokenTextView;
    private String tokenStr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        trueBtn = findViewById(R.id.true_button);
        falseBtn = findViewById(R.id.false_button);
        next = findViewById(R.id.next_button);
        previous = findViewById(R.id.previous_button);
        tokenTextView = findViewById(R.id.token_text_view);
        cheatBtn = findViewById(R.id.cheat_button);

        qvm = new ViewModelProvider(this).get(QuizViewModel.class);

        if (savedInstanceState != null) {
            turn = savedInstanceState.getInt(saved_turn);
            score = savedInstanceState.getInt(saved_score);
            isAnswered = savedInstanceState.getBoolean(key);
            enable(isAnswered);
            qvm.isCheater = savedInstanceState.getBoolean(isCheater, false);
            qvm.cheatedIndex = savedInstanceState.getInt(cheat_index_key, -1);
            setCheatInfo();

        } else {
            turn = 0;
            score = 0;
        }

        mQuestionTextView = findViewById(R.id.question_text_view);

        setCheatInfo();
        mQuestionTextView.setText(qvm.getQuestionText());

        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateQuestion();
            }
        });

        trueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkAnswer(true);
            }
        });

        falseBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                checkAnswer(false);

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qvm.moveToNext();
                updateQuestion();
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

        cheatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tokenTextView.setText(tokenStr);

                boolean answerIsTrue = qvm.getQuestionAnswer();
                Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    ActivityOptions options = ActivityOptions.makeClipRevealAnimation(v, 0, 0,
                            v.getWidth(), v.getHeight());

                    startActivityForResult(intent, REQUEST_CODE_CHEAT, options.toBundle());
                } else {
                    startActivityForResult(intent, REQUEST_CODE_CHEAT);
                }

            }
        });

    }

    private void updateQuestion() {
        setCheatInfo();

        int questionTextResId = qvm.getQuestionText();
        mQuestionTextView.setText(questionTextResId);

        enable(true);

    }

    private void goBack() {
        setCheatInfo();
        qvm.moveBack();
        int questionTextResId = qvm.getQuestionText();
        mQuestionTextView.setText(questionTextResId);
        enable(true);

    }

    private void checkAnswer(boolean userPressedTrue) {
        Question q = qvm.getQuestion();
        boolean answerIsTrue =
                qvm.getQuestionAnswer();
        int messageResId = 0;

        setCheatInfo();

        if (q.isAnswered()) {
            messageResId = R.string.error;
            enable(false);
        } else if (userPressedTrue == answerIsTrue) {
            messageResId = R.string.correct_toast;
            score++;
            turn++;
        } else {
            messageResId = R.string.incorrect_toast;
            turn++;
        }

        Toast t = Toast.makeText(QuizActivity.this, messageResId, Toast.LENGTH_SHORT);
        t.setGravity(Gravity.BOTTOM, 0, 50);
        t.show();
        //cheated or not if answered label already answered
        qvm.setQuestionAnswered();

        if (turn == 7) {
            String scr = "Your score is " + score + "/7";
            Toast result = Toast.makeText(QuizActivity.this, scr, Toast.LENGTH_LONG);
            result.setGravity(Gravity.TOP, 0, 50);
            result.show();
            enable(false);
        }
    }

    //controls button accessibility
    private void enable(boolean b) {
        trueBtn.setEnabled(b);
        falseBtn.setEnabled(b);
        if (turn == 7) {
            next.setEnabled(b);
            previous.setEnabled(b);
        }
    }

    private boolean isActive() {
        return trueBtn.isEnabled() && falseBtn.isEnabled();
    }

    private void setCheatInfo() {
        String tokenLeft = (getResources().getString(R.string.token_text));
        tokenStr = String.format("%s %d", tokenLeft, qvm.tokens);
        tokenTextView.setText(tokenStr);

        if (qvm.tokens < 1) {
            cheatBtn.setEnabled(false);
        }
    }

    @Override
    protected void onSaveInstanceState(@Nullable Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt(saved_score, score);
        savedInstanceState.putInt(saved_turn, turn);
        savedInstanceState.putBoolean(key, isActive());
        savedInstanceState.putBoolean(isCheater, mIsCheater);
        savedInstanceState.putInt(cheat_index_key, savedCheatIndex);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        qvm.isCheater = CheatActivity.wasAnswerShown(data);
        mIsCheater = CheatActivity.wasAnswerShown(data);
        qvm.cheatedIndex = qvm.mCurrentIndex;
        savedCheatIndex = qvm.cheatedIndex;
        qvm.setTokens();
        setCheatInfo();
    }
}

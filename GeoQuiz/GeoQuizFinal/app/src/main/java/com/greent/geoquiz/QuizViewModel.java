package com.greent.geoquiz;

import androidx.lifecycle.ViewModel;

public class QuizViewModel extends ViewModel {


    private Question[] mQuestionBank = {
            new Question(R.string.question_turkey, false),
            new Question(R.string.question_beijing, true),
            new Question(R.string.question_vatican, true),
            new Question(R.string.question_maldives, false),
            new Question(R.string.question_nile, true),
            new Question(R.string.question_sahara, true),
            new Question(R.string.question_iceland, false),
    };
    int mCurrentIndex = 0;
    boolean isCheater = false;
    int cheatedIndex = -1;
    int tokens = 3;

    int getQuestionText() {
        return mQuestionBank[mCurrentIndex].getTextResId();
    }

    void moveToNext() {
        mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
        //isCheater = false;
    }

    void moveBack() {
        if (mCurrentIndex == 0) {
            mCurrentIndex = 6;
        } else {
            mCurrentIndex = (mCurrentIndex - 1) % mQuestionBank.length;
        }

    }

    boolean getQuestionAnswer() {
        return mQuestionBank[mCurrentIndex].isAnswerTrue();

    }

    Question getQuestion() {
        return mQuestionBank[mCurrentIndex];
    }

    void setQuestionAnswered() {
        mQuestionBank[mCurrentIndex].setAnswered(true);
    }

    int getByIndex(int index) {
        return mQuestionBank[index].getTextResId();
    }

    void setTokens() {
        if (tokens > 0) {
            tokens--;
        }
    }
}

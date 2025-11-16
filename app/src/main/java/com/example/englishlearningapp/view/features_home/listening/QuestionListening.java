package com.example.englishlearningapp.view.features_home.listening;

import java.io.Serializable;

public class QuestionListening implements Serializable {
    private int questionNo;
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String correctAnswer;
    private String selectedOption;
    private boolean showAnswer;

    public QuestionListening() {
    }

    public QuestionListening(int questionNo, String questionText, String optionA, String optionB,
                             String optionC, String correctAnswer) {
        this.questionNo = questionNo;
        this.questionText = questionText;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.correctAnswer = correctAnswer;
        this.showAnswer = false;
    }

    // Getters and setters
    public int getQuestionNo() {
        return questionNo;
    }

    public void setQuestionNo(int questionNo) {
        this.questionNo = questionNo;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getOptionA() {
        return optionA;
    }

    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(String selectedOption) {
        this.selectedOption = selectedOption;
    }

    public boolean isShowAnswer() {
        return showAnswer;
    }

    public void setShowAnswer(boolean showAnswer) {
        this.showAnswer = showAnswer;
    }

    // Helper methods
    public boolean isAnswered() {
        return selectedOption != null && !selectedOption.isEmpty();
    }

    public boolean isCorrect() {
        return selectedOption != null && selectedOption.equals(correctAnswer);
    }

    public String getAnswerText() {
        switch (correctAnswer) {
            case "A":
                return "Correct answer: " + optionA;
            case "B":
                return "Correct answer: " + optionB;
            case "C":
                return "Correct answer: " + optionC;
            default:
                return "Correct answer: " + correctAnswer;
        }
    }
}
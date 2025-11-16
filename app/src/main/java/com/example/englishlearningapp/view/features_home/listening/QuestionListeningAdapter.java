package com.example.englishlearningapp.view.features_home.listening;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishlearningapp.R;

import java.util.List;

public class QuestionListeningAdapter extends RecyclerView.Adapter<QuestionListeningAdapter.QuestionViewHolder> {

    private List<QuestionListening> questions;
    private OnOptionClickListener onOptionClickListener;
    private boolean showAllAnswers = false;

    public interface OnOptionClickListener {
        void onOptionClick(int questionPosition, String selectedOption);
    }

    public QuestionListeningAdapter(List<QuestionListening> questions) {
        this.questions = questions;
    }

    public void setOnOptionClickListener(OnOptionClickListener listener) {
        this.onOptionClickListener = listener;
    }

    public void setShowAllAnswers(boolean showAllAnswers) {
        this.showAllAnswers = showAllAnswers;
        notifyDataSetChanged();
    }

    public void updateQuestion(int position, QuestionListening question) {
        if (position >= 0 && position < questions.size()) {
            questions.set(position, question);
            notifyItemChanged(position);
        }
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rv_question_listening, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        QuestionListening question = questions.get(position);
        holder.bind(question, position);
    }

    @Override
    public int getItemCount() {
        return questions != null ? questions.size() : 0;
    }

    public class QuestionViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNoQuestion;
        private TextView tvQuestionText;
        private LinearLayout optionsContainer;

        private LinearLayout optionAContainer, optionBContainer, optionCContainer;
        private TextView tvOptionALabel, tvOptionBLabel, tvOptionCLabel;
        private TextView tvOptionAText, tvOptionBText, tvOptionCText;
        private ImageView ivOptionAStatus, ivOptionBStatus, ivOptionCStatus;

        private LinearLayout answerContainer;
        private TextView tvAnswer;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);

            initViews(itemView);
            setupClickListeners();
        }

        private void initViews(View itemView) {
            tvNoQuestion = itemView.findViewById(R.id.tv_no_question);
            tvQuestionText = itemView.findViewById(R.id.tv_question_text);
            optionsContainer = itemView.findViewById(R.id.options_container);

            // Option containers
            optionAContainer = itemView.findViewById(R.id.option_a_container);
            optionBContainer = itemView.findViewById(R.id.option_b_container);
            optionCContainer = itemView.findViewById(R.id.option_c_container);

            // Option labels
            tvOptionALabel = itemView.findViewById(R.id.tv_option_a_label);
            tvOptionBLabel = itemView.findViewById(R.id.tv_option_b_label);
            tvOptionCLabel = itemView.findViewById(R.id.tv_option_c_label);

            // Option texts
            tvOptionAText = itemView.findViewById(R.id.tv_option_a_text);
            tvOptionBText = itemView.findViewById(R.id.tv_option_b_text);
            tvOptionCText = itemView.findViewById(R.id.tv_option_c_text);

            // Option status icons
            ivOptionAStatus = itemView.findViewById(R.id.iv_option_a_status);
            ivOptionBStatus = itemView.findViewById(R.id.iv_option_b_status);
            ivOptionCStatus = itemView.findViewById(R.id.iv_option_c_status);

            // Answer container
            answerContainer = itemView.findViewById(R.id.answer_container);
            tvAnswer = itemView.findViewById(R.id.tv_answer);
        }

        private void setupClickListeners() {
            optionAContainer.setOnClickListener(v -> onOptionClick("A"));
            optionBContainer.setOnClickListener(v -> onOptionClick("B"));
            optionCContainer.setOnClickListener(v -> onOptionClick("C"));
        }

        private void onOptionClick(String selectedOption) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && onOptionClickListener != null) {
                onOptionClickListener.onOptionClick(position, selectedOption);
            }
        }

        public void bind(QuestionListening question, int position) {
            // Set question number and text
            tvNoQuestion.setText(String.valueOf(question.getQuestionNo()));
            tvQuestionText.setText(question.getQuestionText());

            // Set option texts
            tvOptionAText.setText(question.getOptionA());
            tvOptionBText.setText(question.getOptionB());
            tvOptionCText.setText(question.getOptionC());

            // Set answer text
            tvAnswer.setText(question.getAnswerText());

            // Update UI based on selection and answer state
            updateOptionAppearance(question);

            // Show/hide answer container
            updateAnswerVisibility(question);
        }

        private void updateOptionAppearance(QuestionListening question) {
            resetAllOptions();

            String selectedOption = question.getSelectedOption();
            String correctAnswer = question.getCorrectAnswer();
            boolean shouldShowAnswer = showAllAnswers || question.isShowAnswer();

            if (selectedOption != null) {
                // Highlight selected option
                highlightSelectedOption(selectedOption);

                if (shouldShowAnswer) {
                    // Show correct/incorrect states
                    showAnswerStates(correctAnswer, selectedOption);
                }
            } else if (shouldShowAnswer) {
                // Only show correct answer if no option was selected
                showCorrectAnswerOnly(correctAnswer);
            }
        }

        private void resetAllOptions() {
            resetOption(optionAContainer, tvOptionALabel, ivOptionAStatus, "A");
            resetOption(optionBContainer, tvOptionBLabel, ivOptionBStatus, "B");
            resetOption(optionCContainer, tvOptionCLabel, ivOptionCStatus, "C");
        }

        private void resetOption(LinearLayout container, TextView label, ImageView statusIcon, String option) {
            container.setBackgroundResource(R.drawable.bg_option_unselected);
            label.setBackgroundResource(R.drawable.bg_option_label_unselected);
            label.setTextColor(Color.parseColor("#666666"));
            statusIcon.setVisibility(View.GONE);
        }

        private void highlightSelectedOption(String selectedOption) {
            switch (selectedOption) {
                case "A":
                    setOptionSelected(optionAContainer, tvOptionALabel, ivOptionAStatus);
                    break;
                case "B":
                    setOptionSelected(optionBContainer, tvOptionBLabel, ivOptionBStatus);
                    break;
                case "C":
                    setOptionSelected(optionCContainer, tvOptionCLabel, ivOptionCStatus);
                    break;
            }
        }

        private void setOptionSelected(LinearLayout container, TextView label, ImageView statusIcon) {
            container.setBackgroundColor(Color.parseColor("#E3F2FD"));
            label.setBackgroundColor(Color.parseColor("#2196F3"));
            label.setTextColor(Color.WHITE);

            // Show check icon for selected option when not showing answers
            if (!showAllAnswers && !isAnyQuestionShowingAnswer()) {
                statusIcon.setImageResource(R.drawable.ic_check_circle);
                statusIcon.setColorFilter(Color.parseColor("#2196F3"));
                statusIcon.setVisibility(View.VISIBLE);
            }
        }

        private void showAnswerStates(String correctAnswer, String selectedOption) {
            // Show correct answer
            showCorrectAnswer(correctAnswer);

            // Show incorrect selection if any
            if (!selectedOption.equals(correctAnswer)) {
                showIncorrectAnswer(selectedOption);
            }
        }

        private void showCorrectAnswerOnly(String correctAnswer) {
            showCorrectAnswer(correctAnswer);
        }

        private void showCorrectAnswer(String correctAnswer) {
            switch (correctAnswer) {
                case "A":
                    setOptionCorrect(optionAContainer, tvOptionALabel, ivOptionAStatus);
                    break;
                case "B":
                    setOptionCorrect(optionBContainer, tvOptionBLabel, ivOptionBStatus);
                    break;
                case "C":
                    setOptionCorrect(optionCContainer, tvOptionCLabel, ivOptionCStatus);
                    break;
            }
        }

        private void setOptionCorrect(LinearLayout container, TextView label, ImageView statusIcon) {
            container.setBackgroundColor(Color.parseColor("#E8F5E8"));
            label.setBackgroundColor(Color.parseColor("#4CAF50"));
            label.setTextColor(Color.WHITE);
            statusIcon.setImageResource(R.drawable.ic_check_circle);
            statusIcon.setColorFilter(Color.parseColor("#4CAF50"));
            statusIcon.setVisibility(View.VISIBLE);
        }

        private void showIncorrectAnswer(String incorrectOption) {
            switch (incorrectOption) {
                case "A":
                    setOptionIncorrect(optionAContainer, tvOptionALabel, ivOptionAStatus);
                    break;
                case "B":
                    setOptionIncorrect(optionBContainer, tvOptionBLabel, ivOptionBStatus);
                    break;
                case "C":
                    setOptionIncorrect(optionCContainer, tvOptionCLabel, ivOptionCStatus);
                    break;
            }
        }

        private void setOptionIncorrect(LinearLayout container, TextView label, ImageView statusIcon) {
            container.setBackgroundColor(Color.parseColor("#FFEBEE"));
            label.setBackgroundColor(Color.parseColor("#F44336"));
            label.setTextColor(Color.WHITE);
            statusIcon.setImageResource(R.drawable.ic_close);
            statusIcon.setColorFilter(Color.parseColor("#F44336"));
            statusIcon.setVisibility(View.VISIBLE);
        }

        private void updateAnswerVisibility(QuestionListening question) {
            boolean shouldShowAnswer = showAllAnswers || question.isShowAnswer();

            if (shouldShowAnswer) {
                answerContainer.setVisibility(View.VISIBLE);
            } else {
                answerContainer.setVisibility(View.GONE);
            }
        }

        private boolean isAnyQuestionShowingAnswer() {
            for (QuestionListening q : questions) {
                if (q.isShowAnswer()) {
                    return true;
                }
            }
            return showAllAnswers;
        }
    }
}
package tr.edu.yildiz.berkayyapici;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;

public class CreateExamAdapter extends RecyclerView.Adapter<CreateExamAdapter.MyViewHolder> {
    private final ArrayList<Question> questions;
    private final ArrayList<Boolean> checkedList;
    private final LayoutInflater mInflater;
    private final Context context;
    private final int difficulty;

    public CreateExamAdapter(Context context, ArrayList<Question> questions, ArrayList<Boolean> checkedList, int difficulty) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.questions = questions;
        this.checkedList = checkedList;
        this.difficulty = difficulty;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.my_question_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CreateExamAdapter.MyViewHolder holder, int position) {
        ArrayList<String> choices = new ArrayList<>();
        ArrayList<String> choicesList = new ArrayList<>();
        String question, answer;
        int trueIndex = 0;

        String number = (position + 1) + " -";
        holder.questionNo.setText(number);

        question = questions.get(position).getQuestion();
        holder.questionText.setText(question);

        answer = questions.get(position).getAnswer();

        choices.add(questions.get(position).getChoice1());
        choices.add(questions.get(position).getChoice2());
        choices.add(questions.get(position).getChoice3());
        choices.add(questions.get(position).getChoice4());

        switch (difficulty) {
            case 2:
                Collections.shuffle(choices);

                choicesList.add(answer);
                choicesList.add(choices.get(0));

                Collections.shuffle(choicesList);
                trueIndex = choicesList.indexOf(answer);

                holder.radioButton1.setText(choicesList.get(0));
                holder.radioButton2.setText(choicesList.get(1));
                holder.radioButton3.setVisibility(View.GONE);
                holder.radioButton4.setVisibility(View.GONE);
                holder.radioButton5.setVisibility(View.GONE);

                break;
            case 3:
                Collections.shuffle(choices);

                choicesList.add(answer);
                choicesList.add(choices.get(0));
                choicesList.add(choices.get(1));

                Collections.shuffle(choicesList);
                trueIndex = choicesList.indexOf(answer);

                holder.radioButton1.setText(choicesList.get(0));
                holder.radioButton2.setText(choicesList.get(1));
                holder.radioButton3.setText(choicesList.get(2));
                holder.radioButton4.setVisibility(View.GONE);
                holder.radioButton5.setVisibility(View.GONE);

                break;
            case 4:
                Collections.shuffle(choices);

                choicesList.add(answer);
                choicesList.add(choices.get(0));
                choicesList.add(choices.get(1));
                choicesList.add(choices.get(2));

                Collections.shuffle(choicesList);
                trueIndex = choicesList.indexOf(answer);

                holder.radioButton1.setText(choicesList.get(0));
                holder.radioButton2.setText(choicesList.get(1));
                holder.radioButton3.setText(choicesList.get(2));
                holder.radioButton4.setText(choicesList.get(3));
                holder.radioButton5.setVisibility(View.GONE);

                break;
            case 5:
                choices.add(answer);

                Collections.shuffle(choices);
                trueIndex = choices.indexOf(answer);

                holder.radioButton1.setText(choices.get(0));
                holder.radioButton2.setText(choices.get(1));
                holder.radioButton3.setText(choices.get(2));
                holder.radioButton4.setText(choices.get(3));
                holder.radioButton5.setText(choices.get(4));

                break;
        }

        RadioButton trueButton = (RadioButton)holder.radioGroup.getChildAt(trueIndex);
        trueButton.setTextColor(Color.GREEN);
        trueButton.setChecked(true);

        holder.selectQuestion.setVisibility(View.VISIBLE);
        holder.selectQuestion.setChecked(checkedList.get(position));
        holder.selectQuestion.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                checkedList.set(position, true);
            }
            else {
                checkedList.set(position, false);
            }
            try {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("checkedList.txt", Context.MODE_PRIVATE));
                for(int i = 0; i < questions.size(); i++)  {
                    outputStreamWriter.append(String.valueOf(checkedList.get(i))).append("\n");
                }
                outputStreamWriter.close();
            } catch (IOException e) {
                Log.e("Exception", "File write failed: " + e.toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView questionNo, questionText;
        RadioButton radioButton1, radioButton2, radioButton3, radioButton4, radioButton5;
        RadioGroup radioGroup;
        CheckBox selectQuestion;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            questionNo = itemView.findViewById(R.id.questionNo);
            questionText = itemView.findViewById(R.id.questionText);
            radioGroup = itemView.findViewById(R.id.radioGroup);
            radioButton1 = itemView.findViewById(R.id.radioButton1);
            radioButton2 = itemView.findViewById(R.id.radioButton2);
            radioButton3 = itemView.findViewById(R.id.radioButton3);
            radioButton4 = itemView.findViewById(R.id.radioButton4);
            radioButton5 = itemView.findViewById(R.id.radioButton5);
            selectQuestion = itemView.findViewById(R.id.selectQuestion);
        }
    }
}

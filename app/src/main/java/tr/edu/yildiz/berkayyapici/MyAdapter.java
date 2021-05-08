package tr.edu.yildiz.berkayyapici;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private final List<String> questions;
    private final String[][] choices;
    private final String[] answers;
    private final LayoutInflater mInflater;
    private final Context context;

    public MyAdapter(Context context, List<String> questions, String[][] choices, String[] answers) {
        this.mInflater = LayoutInflater.from(context);
        this.questions = questions;
        this.choices = choices;
        this.answers = answers;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.my_row, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.questionNo.setText((position + 1) + " -");
        holder.questionText.setText(questions.get(position));
        holder.radioButton1.setText(choices[position][0]);
        holder.radioButton2.setText(choices[position][1]);
        holder.radioButton3.setText(choices[position][2]);
        holder.radioButton4.setText(choices[position][3]);

        holder.showAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int radioButtonID = holder.radioGroup.getCheckedRadioButtonId();
                int trueIndex = Integer.parseInt(answers[position]);
                if(radioButtonID != -1) {
                    RadioButton temp = holder.radioGroup.findViewById(radioButtonID);
                    int index = holder.radioGroup.indexOfChild(temp);
                    if(index == trueIndex) {
                        temp.setTextColor(Color.GREEN);
                    }
                    else {
                        temp.setTextColor(Color.RED);
                    }
                }
                RadioButton trueButton = (RadioButton)holder.radioGroup.getChildAt(trueIndex);
                trueButton.setTextColor(Color.GREEN);
                trueButton.setChecked(true);
                holder.showAnswer.setClickable(false);
            }
        });
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView questionNo, questionText;
        RadioGroup radioGroup;
        RadioButton radioButton1, radioButton2, radioButton3, radioButton4;
        Button showAnswer;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            questionNo = itemView.findViewById(R.id.questionNo);
            questionText = itemView.findViewById(R.id.questionText);
            radioGroup = itemView.findViewById(R.id.radioGroup);
            radioButton1 = itemView.findViewById(R.id.radioButton1);
            radioButton2 = itemView.findViewById(R.id.radioButton2);
            radioButton3 = itemView.findViewById(R.id.radioButton3);
            radioButton4 = itemView.findViewById(R.id.radioButton4);
            showAnswer = itemView.findViewById(R.id.showAnswer);
        }
    }
    
}

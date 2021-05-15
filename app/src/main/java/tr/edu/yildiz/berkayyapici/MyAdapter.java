package tr.edu.yildiz.berkayyapici;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomMenuButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private final ArrayList<Question> questions;
    private final LayoutInflater mInflater;
    private final Context context;

    public MyAdapter(Context context, ArrayList<Question> questions) {
        this.mInflater = LayoutInflater.from(context);
        this.questions = questions;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.my_question_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ArrayList<String> choices = new ArrayList<>();
        String question, answer;

        String number = (position + 1) + " -";
        holder.questionNo.setText(number);

        question = questions.get(position).getQuestion();
        holder.questionText.setText(question);

        String uri = questions.get(position).getUri();
        String[] uris = uri.split("#");
        String[] uriNames = new String[3];

        for(int i=0; i<uris.length; i++) {
            String result = uris[i];
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
                uriNames[i] = result;
            }
            else {
                uriNames[i] = "none";
            }
        }

        holder.bmbUri.setVisibility(View.VISIBLE);

        MediaPlayer mediaPlayer = new MediaPlayer();
        if(!uris[0].equals("none")) {
            try {
                mediaPlayer.setAudioAttributes(
                        new AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .build()
                );
                mediaPlayer.setDataSource(context, Uri.parse(uris[0]));
            } catch (IOException e) {
                Log.d("myTAG", e.getMessage());
            }
        }
        HamButton.Builder builderSound = new HamButton.Builder()
                .listener(index -> {
                    // When the boom-button corresponding this builder is clicked.
                    if(!uris[0].equals("none")) {
                        if (mediaPlayer.isPlaying()) {
                            Log.d("myTAG", "yes");
                            mediaPlayer.stop();
                        } else {
                            Log.d("myTAG", "no");
                            try {
                                mediaPlayer.prepare();
                                mediaPlayer.start();
                            } catch (IOException e) {
                                Log.d("myTAG", e.getMessage());
                            }
                        }
                    }
                    else {
                        Toast.makeText(context, "There is no sound", Toast.LENGTH_SHORT).show();
                    }
                })
                .normalText(uriNames[0])
                .normalImageRes(R.drawable.ic_music);
        holder.bmbUri.addBuilder(builderSound);

        HamButton.Builder builderImage = new HamButton.Builder()
                .listener(index -> {
                    // When the boom-button corresponding this builder is clicked.
                    if(!uris[1].equals("none")) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse(uris[1]), "image/*");
                        context.startActivity(Intent.createChooser(intent,"Show image with"));
                    }
                    else {
                        Toast.makeText(context, "There is no image", Toast.LENGTH_SHORT).show();
                    }
                })
                .normalText(uriNames[1])
                .normalImageRes(R.drawable.ic_image);
        holder.bmbUri.addBuilder(builderImage);

        HamButton.Builder builderVideo = new HamButton.Builder()
                .listener(index -> {
                    // When the boom-button corresponding this builder is clicked.
                    if(!uris[2].equals("none")) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse(uris[2]), "video/*");
                        context.startActivity(Intent.createChooser(intent,"Play video with"));
                    }
                    else {
                        Toast.makeText(context, "There is no video", Toast.LENGTH_SHORT).show();
                    }
                })
                .normalText(uriNames[2])
                .normalImageRes(R.drawable.ic_video);
        holder.bmbUri.addBuilder(builderVideo);

        answer = questions.get(position).getAnswer();
        choices.add(answer);
        choices.add(questions.get(position).getChoice1());
        choices.add(questions.get(position).getChoice2());
        choices.add(questions.get(position).getChoice3());
        choices.add(questions.get(position).getChoice4());

        Collections.shuffle(choices);

        int trueIndex = choices.indexOf(answer);
        holder.radioButton1.setText(choices.get(0));
        holder.radioButton2.setText(choices.get(1));
        holder.radioButton3.setText(choices.get(2));
        holder.radioButton4.setText(choices.get(3));
        holder.radioButton5.setText(choices.get(4));

        RadioButton trueButton = (RadioButton)holder.radioGroup.getChildAt(trueIndex);
        trueButton.setTextColor(Color.GREEN);
        trueButton.setChecked(true);

        holder.bmb.setVisibility(View.VISIBLE);
        holder.attachImage.setVisibility(View.VISIBLE);

        HamButton.Builder builderUpdate = new HamButton.Builder()
                .listener(index -> {
                    // When the boom-button corresponding this builder is clicked.
                    Intent intent = new Intent(context, AddUpdateQuestionActivity.class);
                    intent.putExtra("questionID", questions.get(position).getId());

                    context.startActivity(intent);
                    ((Activity) context).finish();
                })
                .normalText("Update Question")
                .normalImageRes(R.drawable.ic_edit);
        holder.bmb.addBuilder(builderUpdate);

        HamButton.Builder builderDelete = new HamButton.Builder()
                .listener(index -> {
                    // When the boom-button corresponding this builder is clicked.
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Delete Question?");
                    builder.setMessage("Are you sure you want to delete this question?");
                    builder.setPositiveButton("Yes", (dialog, which) -> {
                        DatabaseHelper databaseHelper = new DatabaseHelper(context);
                        databaseHelper.deleteQuestion(questions.get(position).getId());
                        Toast.makeText(context, "Question deleted.", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(context, QuestionsActivity.class);
                        context.startActivity(intent);
                        ((Activity) context).finish();
                    });
                    builder.setNegativeButton("No", null);
                    builder.show();
                })
                .normalText("Delete Question")
                .normalImageRes(R.drawable.ic_delete);
        holder.bmb.addBuilder(builderDelete);
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView questionNo, questionText;
        ImageView attachImage;
        RadioGroup radioGroup;
        RadioButton radioButton1, radioButton2, radioButton3, radioButton4, radioButton5;
        BoomMenuButton bmb, bmbUri;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            questionNo = itemView.findViewById(R.id.questionNo);
            questionText = itemView.findViewById(R.id.questionText);
            attachImage = itemView.findViewById(R.id.attachImage);
            radioGroup = itemView.findViewById(R.id.radioGroup);
            radioButton1 = itemView.findViewById(R.id.radioButton1);
            radioButton2 = itemView.findViewById(R.id.radioButton2);
            radioButton3 = itemView.findViewById(R.id.radioButton3);
            radioButton4 = itemView.findViewById(R.id.radioButton4);
            radioButton5 = itemView.findViewById(R.id.radioButton5);
            bmb = itemView.findViewById(R.id.bmb);
            bmbUri = itemView.findViewById(R.id.bmbUri);
        }
    }
    
}

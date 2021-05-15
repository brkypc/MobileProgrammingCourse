package tr.edu.yildiz.berkayyapici;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.hitomi.cmlibrary.CircleMenu;
import com.hitomi.cmlibrary.OnMenuStatusChangeListener;

import java.util.Objects;

public class AddUpdateQuestionActivity extends AppCompatActivity {
    private static final int MUSIC_CODE = 10;
    private static final int IMAGE_CODE = 11;
    private static final int VIDEO_CODE = 12;

    TextInputLayout enteredQuestion, answer, choice1, choice2, choice3, choice4;
    TextView text;
    Button saveQuestion;
    CircleMenu circleMenu;

    String question, textAnswer, textChoice1, textChoice2, textChoice3, textChoice4;
    int questionID;

    Animation scaleUp, scaleDown;

    DatabaseHelper databaseHelper;
    Uri uriSound = null, uriImage = null, uriVideo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_update_question);
        defineVariables();

        if(checkStatus()) {
            // add
            defineAddListener();
        }
        else {
            //update
            text.setText(R.string.update_question);
            saveQuestion.setText(R.string.update_question);
            defineUpdateListener();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case MUSIC_CODE:
                    uriSound = data.getData();
                    Log.d("myTAG", getRealPathFromSound(uriSound));
                    break;
                case IMAGE_CODE:
                    uriImage = data.getData();
                    Log.d("myTAG", getRealPathFromImage(uriImage));
                    break;
                case VIDEO_CODE:
                    uriVideo = data.getData();
                    Log.d("myTAG", getRealPathFromVideo(uriVideo));
                    break;
            }
        }
    }

    private Boolean checkStatus() {
        Intent intent = getIntent();
        if(intent.hasExtra("questionID")) {
            questionID = intent.getIntExtra("questionID", -1);
            Question question = databaseHelper.getQuestion(questionID);

            Objects.requireNonNull(enteredQuestion.getEditText()).setText(question.getQuestion());
            Objects.requireNonNull(answer.getEditText()).setText(question.getAnswer());
            Objects.requireNonNull(choice1.getEditText()).setText(question.getChoice1());
            Objects.requireNonNull(choice2.getEditText()).setText(question.getChoice2());
            Objects.requireNonNull(choice3.getEditText()).setText(question.getChoice3());
            Objects.requireNonNull(choice4.getEditText()).setText(question.getChoice4());

            String uri = question.getUri();
            String[] uris = uri.split("#");
            Log.d("myTAG",uris[0]);
            Log.d("myTAG",uris[1]);
            Log.d("myTAG",uris[2]);

            if(!uris[0].equals("none"))
                uriSound = Uri.parse(uris[0]);
            if(!uris[1].equals("none"))
                uriImage = Uri.parse(uris[1]);
            if(!uris[2].equals("none"))
                uriVideo = Uri.parse(uris[2]);

            return false;
        }
        else return true;
    }

    private void defineAddListener() {
        saveQuestion.setOnClickListener(v -> {
            saveQuestion.startAnimation(scaleUp);
            saveQuestion.startAnimation(scaleDown);
            if(validateFields()) {
                StringBuilder stringBuilder = new StringBuilder();
                if(uriSound != null) stringBuilder.append(getRealPathFromSound(uriSound)).append("#");
                else stringBuilder.append("none#");

                if(uriImage != null) stringBuilder.append(getRealPathFromImage(uriImage)).append("#");
                else stringBuilder.append("none#");

                if(uriVideo != null) stringBuilder.append(getRealPathFromVideo(uriVideo));
                else stringBuilder.append("none");

                databaseHelper.addQuestion(new Question(question, textAnswer, textChoice1, textChoice2, textChoice3, textChoice4, stringBuilder.toString()));
                Toast.makeText(AddUpdateQuestionActivity.this, "Question added to the database.", Toast.LENGTH_SHORT).show();

                clearFields();
                enteredQuestion.requestFocus();
            }
            else {
                Toast.makeText(AddUpdateQuestionActivity.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void defineUpdateListener() {
        saveQuestion.setOnClickListener(v -> {
            saveQuestion.startAnimation(scaleUp);
            saveQuestion.startAnimation(scaleDown);
            if(validateFields()) {
                StringBuilder stringBuilder = new StringBuilder();
                if(uriSound != null) stringBuilder.append(getRealPathFromSound(uriSound)).append("#");
                else stringBuilder.append("none#");

                if(uriImage != null) stringBuilder.append(getRealPathFromImage(uriImage)).append("#");
                else stringBuilder.append("none#");

                if(uriVideo != null) stringBuilder.append(getRealPathFromVideo(uriVideo));
                else stringBuilder.append("none");

                databaseHelper.updateQuestion(new Question(question, textAnswer, textChoice1, textChoice2, textChoice3, textChoice4, stringBuilder.toString()), questionID);
                Toast.makeText(AddUpdateQuestionActivity.this, "Question updated.", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(AddUpdateQuestionActivity.this, QuestionsActivity.class);
                startActivity(intent);
                finish();
            }
            else {
                Toast.makeText(AddUpdateQuestionActivity.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setCircleMenu() {
        LinearLayout.LayoutParams paramsDown = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsDown.setMargins(0, 0, 0, 0); paramsDown.gravity = Gravity.CENTER;

        LinearLayout.LayoutParams paramsUp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsUp.setMargins(0, -120, 0, -140); paramsUp.gravity = Gravity.CENTER;

        Animation down = AnimationUtils.loadAnimation(this, R.anim.menu_down);
        Animation up = AnimationUtils.loadAnimation(this, R.anim.menu_up);

        circleMenu.setMainMenu(Color.parseColor("#258CFF"), R.drawable.ic_attach, R.mipmap.icon_cancel)
                .addSubMenu(Color.parseColor("#FF4B32"), R.drawable.ic_music)
                .addSubMenu(Color.parseColor("#30A400"), R.drawable.ic_image)
                .addSubMenu(Color.parseColor("#8A39FF"), R.drawable.ic_video)
                .setOnMenuSelectedListener(index -> {
                    switch (index) {
                        case 0:
                            new Handler().postDelayed(() -> {
                                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(intent, MUSIC_CODE);
                            }, 500);
                            break;
                        case 1:
                            new Handler().postDelayed(() -> {
                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType("image/*");
                                startActivityForResult(Intent.createChooser(intent,"Select image with"), IMAGE_CODE);
                            }, 500);
                            break;
                        case 2:
                            new Handler().postDelayed(() -> {
                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType("video/*");
                                startActivityForResult(Intent.createChooser(intent,"Select video with"),VIDEO_CODE);
                            }, 500);
                            break;
                    }
                })
                .setOnMenuStatusChangeListener(new OnMenuStatusChangeListener() {
                    @Override
                    public void onMenuOpened() {
                        if (ContextCompat.checkSelfPermission(AddUpdateQuestionActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(AddUpdateQuestionActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 765);
                        }
                        circleMenu.startAnimation(down);
                        circleMenu.setLayoutParams(paramsDown);
                        saveQuestion.setVisibility(View.GONE);
                    }
                    @Override
                    public void onMenuClosed() {
                        circleMenu.startAnimation(up);
                        circleMenu.setLayoutParams(paramsUp);
                        saveQuestion.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void defineVariables() {
        databaseHelper = new DatabaseHelper(AddUpdateQuestionActivity.this);
        text = findViewById(R.id.addUpdateText);
        enteredQuestion = findViewById(R.id.enteredQuestion);
        answer = findViewById(R.id.answer);
        Objects.requireNonNull(answer.getEditText()).setTextColor(Color.GREEN);
        choice1 = findViewById(R.id.choice1);
        choice2 = findViewById(R.id.choice2);
        choice3 = findViewById(R.id.choice3);
        choice4 = findViewById(R.id.choice4);
        saveQuestion = findViewById(R.id.saveQuestion);
        circleMenu = findViewById(R.id.circle_menu);
        setCircleMenu();

        scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up);
        scaleDown = AnimationUtils.loadAnimation(this, R.anim.scale_down);
        scaleDown.setStartOffset(100);
    }

    private String getRealPathFromSound(Uri soundUri) {
        String[] proj = { MediaStore.Audio.Media.DATA };

        @SuppressLint("Recycle")
        Cursor ringtoneCursor = getApplicationContext().getContentResolver().query(soundUri, proj, null, null, null);
        if(ringtoneCursor != null ) {
           ringtoneCursor.moveToFirst();
           return ringtoneCursor.getString(ringtoneCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
        }
        else
            return null;
    }

    private String getRealPathFromImage(Uri imageUri) {
        String[] projection = { MediaStore.Images.Media.DATA };

        @SuppressLint("Recycle")
        Cursor cursor = getContentResolver().query(imageUri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        else
            return null;
    }

    private String getRealPathFromVideo(Uri videoUri) {
        String[] projection = { MediaStore.Video.Media.DATA };

        @SuppressLint("Recycle")
        Cursor cursor = getContentResolver().query(videoUri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        else
            return null;
    }

    private void clearFields() {
        Objects.requireNonNull(enteredQuestion.getEditText()).setText("");
        Objects.requireNonNull(answer.getEditText()).setText("");
        Objects.requireNonNull(choice1.getEditText()).setText("");
        Objects.requireNonNull(choice2.getEditText()).setText("");
        Objects.requireNonNull(choice3.getEditText()).setText("");
        Objects.requireNonNull(choice4.getEditText()).setText("");
    }

    private boolean validateFields() {
        getTextFromFields();

        if(question.isEmpty()) {
            return false;
        }
        else if(textAnswer.isEmpty()) {
            return false;
        }
        else if(textChoice1.isEmpty()) {
            return false;
        }
        else if(textChoice2.isEmpty()) {
            return false;
        }
        else if(textChoice3.isEmpty()) {
            return false;
        }
        else return !textChoice4.isEmpty();
    }

    private void getTextFromFields() {
        question = Objects.requireNonNull(enteredQuestion.getEditText()).getText().toString();
        textAnswer = Objects.requireNonNull(answer.getEditText()).getText().toString();
        textChoice1 = Objects.requireNonNull(choice1.getEditText()).getText().toString();
        textChoice2 = Objects.requireNonNull(choice2.getEditText()).getText().toString();
        textChoice3 = Objects.requireNonNull(choice3.getEditText()).getText().toString();
        textChoice4 = Objects.requireNonNull(choice4.getEditText()).getText().toString();
    }
}
package tr.edu.yildiz.berkayyapici;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "berkayyapici.db";
    private static final int DATABASE_VERSION = 1;

    private static final String USERS_TABLE = "users";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_DATE = "birthdate";

    private static final String QUESTIONS_TABLE = "questions";
    private static final String COLUMN_ID = "ID";
    private static final String COLUMN_QUESTION = "question_text";
    private static final String COLUMN_ANSWER = "answer";
    private static final String COLUMN_CHOICE1 = "choice1";
    private static final String COLUMN_CHOICE2 = "choice2";
    private static final String COLUMN_CHOICE3 = "choice3";
    private static final String COLUMN_CHOICE4 = "choice4";
    private static final String COLUMN_URI = "uri";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE " + USERS_TABLE + " (" +
                COLUMN_USERNAME + " TEXT PRIMARY KEY, " +
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_EMAIL + " TEXT, " +
                COLUMN_PHONE + " TEXT, " +
                COLUMN_DATE + " TEXT);";

        String createQuestionsTable = "CREATE TABLE " + QUESTIONS_TABLE + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_QUESTION + " TEXT, " +
                COLUMN_ANSWER + " TEXT, " +
                COLUMN_CHOICE1 + " TEXT, " +
                COLUMN_CHOICE2 + " TEXT, " +
                COLUMN_CHOICE3 + " TEXT, " +
                COLUMN_CHOICE4 + " TEXT, " +
                COLUMN_URI + " TEXT);";

        db.execSQL(createUsersTable);
        db.execSQL(createQuestionsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addUser(User user) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USERNAME, user.getUsername());
        contentValues.put(COLUMN_PASSWORD, user.getPassword());
        contentValues.put(COLUMN_NAME, user.getFullName());
        contentValues.put(COLUMN_EMAIL, user.getEmail());
        contentValues.put(COLUMN_PHONE, user.getPhoneNo());
        contentValues.put(COLUMN_DATE, user.getBirthDate());

        database.insert(USERS_TABLE, null, contentValues);
    }

    public String getUserLoginInfo(String userEnteredUsername) {
        SQLiteDatabase database = this.getReadableDatabase();
        String rawQuery = "SELECT password FROM " + USERS_TABLE +
                " WHERE username = '" + userEnteredUsername + "';";

        @SuppressLint("Recycle")
        Cursor cursor = database.rawQuery(rawQuery, null);

        if(cursor.moveToFirst()) {
            return cursor.getString(0);
        }else {
            return "";
        }

    }

    public Cursor getUser(String username) {
        SQLiteDatabase database = this.getReadableDatabase();
        String rawQuery = "SELECT * FROM " + USERS_TABLE +
                " WHERE username = '" + username + "';";
        return database.rawQuery(rawQuery, null);
    }

    public void addQuestion(Question question) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_QUESTION, question.getQuestion());
        contentValues.put(COLUMN_ANSWER, question.getAnswer());
        contentValues.put(COLUMN_CHOICE1, question.getChoice1());
        contentValues.put(COLUMN_CHOICE2, question.getChoice2());
        contentValues.put(COLUMN_CHOICE3, question.getChoice3());
        contentValues.put(COLUMN_CHOICE4, question.getChoice4());
        contentValues.put(COLUMN_URI, question.getUri());

        database.insert(QUESTIONS_TABLE, null, contentValues);
    }

    public void updateQuestion(Question question, int Id) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_QUESTION, question.getQuestion());
        contentValues.put(COLUMN_ANSWER, question.getAnswer());
        contentValues.put(COLUMN_CHOICE1, question.getChoice1());
        contentValues.put(COLUMN_CHOICE2, question.getChoice2());
        contentValues.put(COLUMN_CHOICE3, question.getChoice3());
        contentValues.put(COLUMN_CHOICE4, question.getChoice4());
        contentValues.put(COLUMN_URI, question.getUri());

        database.update(QUESTIONS_TABLE, contentValues, "ID = ?", new String[]{String.valueOf(Id)});
    }

    public void deleteQuestion(int Id) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(QUESTIONS_TABLE, "ID = ?", new String[]{String.valueOf(Id)});
    }

    public Question getQuestion(int questionID) {
        String question, answer, choice1, choice2 ,choice3, choice4, uri;
        int id;

        SQLiteDatabase database = this.getReadableDatabase();
        String rawQuery = "SELECT * FROM " + QUESTIONS_TABLE +
                " WHERE ID = '" + questionID + "';";

        @SuppressLint("Recycle")
        Cursor cursor = database.rawQuery(rawQuery, null);

        if(cursor.moveToFirst()) {
            id = cursor.getInt(0);          // id
            question = cursor.getString(1);  // question
            answer = cursor.getString(2);  // answer
            choice1 = cursor.getString(3);  // choice1
            choice2 = cursor.getString(4);  // choice2
            choice3 = cursor.getString(5);  // choice3
            choice4 = cursor.getString(6);  // choice4
            uri = cursor.getString(7);  // uri

           return new Question(question, answer, choice1, choice2, choice3, choice4, uri, id);
        }
        return null;
    }

    public ArrayList<Question> getQuestions() {
        ArrayList<Question> questions = new ArrayList<>();
        String question, answer, choice1, choice2 ,choice3, choice4, uri;
        int id;

        SQLiteDatabase database = this.getReadableDatabase();
        String rawQuery = "SELECT * FROM " + QUESTIONS_TABLE;

        @SuppressLint("Recycle")
        Cursor cursor = database.rawQuery(rawQuery, null);
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(0);            // id
                question = cursor.getString(1);  // question
                answer = cursor.getString(2);  // answer
                choice1 = cursor.getString(3);  // choice1
                choice2 = cursor.getString(4);  // choice2
                choice3 = cursor.getString(5);  // choice3
                choice4 = cursor.getString(6);  // choice4
                uri = cursor.getString(7);  // uri

                questions.add(new Question(question, answer, choice1, choice2, choice3, choice4, uri, id));
            }while (cursor.moveToNext());

            return questions;
        }
        return null;
    }
}

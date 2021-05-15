package tr.edu.yildiz.berkayyapici;

import java.util.ArrayList;

public class Question {
    private String question, answer, choice1, choice2, choice3, choice4, uri;
    private int id;

    public Question() {

    }
    public Question(String question, String answer, String choice1, String choice2, String choice3, String choice4, String uri) {
        this.question = question;
        this.answer = answer;
        this.choice1 = choice1;
        this.choice2 = choice2;
        this.choice3 = choice3;
        this.choice4 = choice4;
        this.uri = uri;
    }

    public Question(String question, String answer, String choice1, String choice2, String choice3, String choice4, String uri, int id) {
        this.question = question;
        this.answer = answer;
        this.choice1 = choice1;
        this.choice2 = choice2;
        this.choice3 = choice3;
        this.choice4 = choice4;
        this.uri = uri;
        this.id = id;
    }

    public int getId() { return id; }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public String getChoice1() {
        return choice1;
    }

    public String getChoice2() {
        return choice2;
    }

    public String getChoice3() {
        return choice3;
    }

    public String getChoice4() {
        return choice4;
    }

    public String getUri() { return uri; }

    public ArrayList<Question> defaultQuestions() {
        ArrayList<Question> questionsList = new ArrayList<>();

        questionsList.add(new Question("What is a correct syntax to output \"Hello World\" in Java?","System.out.println(\"Hello World\");",
                "print(\"Hello World\");", "printf(\"Hello World\")", "echo(\"Hello World\")", "Console.WriteLine(\"Hello World\")", "none#none#none"));
        questionsList.add(new Question("Which method can be used to find the length of a string in Java?","length()",
                "strlen()", "size()", "getSize()", "len()", "none#none#none"));
        questionsList.add(new Question("How to stop the services in android?","stopSelf()",
                "finish()", "System.exit()", "kill()", "delete()", "none#none#none"));
        return questionsList;
    }
}

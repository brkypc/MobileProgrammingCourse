package tr.edu.yildiz.berkayyapici;

public class Question {
    private final String question;
    private final String answer;
    private final String choice1;
    private final String choice2;
    private final String choice3;
    private final String choice4;
    private final String uri;
    private int id;

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

}

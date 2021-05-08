package tr.edu.yildiz.berkayyapici;

import java.util.ArrayList;

public class Questions {
    public Questions() {
    }

    public ArrayList<String> getQuestions() {
        ArrayList<String> questionsList = new ArrayList<>();
        questionsList.add("What planet comes first in the solar system?");
        questionsList.add("What planet comes second in the solar system?");
        questionsList.add("What planet comes third in the solar system?");
        questionsList.add("What planet comes fourth in the solar system?");
        questionsList.add("What planet comes fifth in the solar system?");
        questionsList.add("What planet comes sixth in the solar system?");
        questionsList.add("What planet comes seventh in the solar system?");
        questionsList.add("What planet comes eighth in the solar system?");
        questionsList.add("What planet comes ninth in the solar system?");
        return questionsList;
    }

    public String[][] getChoices() {
        return new String[][]{
                {"Mercury", "Venus", "Uranus", "Jupiter"},
                {"Saturn", "Earth", "Venus", "Neptune"},
                {"Uranus", "Venus", "Earth", "Pluto"},
                {"Neptune", "Mars", "Saturn", "Earth"},
                {"Pluto", "Neptune", "Saturn", "Jupiter"},
                {"Venus", "Mars", "Jupiter", "Saturn"},
                {"Earth", "Pluto", "Uranus", "Jupiter"},
                {"Mars", "Neptune", "Uranus", "Venus"},
                {"Jupiter", "Saturn", "Mars", "Pluto"}
        };
    }

   /* public String[] getAnswers() {
        return new String[]{"Mercury", "Venus", "Earth", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune", "Pluto"};
    }*/

    public String[] getAnswers() {
        return new String[]{"0", "2", "2", "1", "3", "3", "2", "1", "3"};
    }
}

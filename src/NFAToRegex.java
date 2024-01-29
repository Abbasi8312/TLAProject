import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NFAToRegex {
    public static void main(String[] args) {
        GNFA gnfa = new GNFA();
    }
}

class GNFA {
    private final List<Transition> transitions;
    private final Set<String> states;
    private final Set<Character> alphabet;
    private final String startState;
    private final String finalState;

    public GNFA() {
        states = new HashSet<>();
        alphabet = new HashSet<>();
        transitions = new ArrayList<>();
        startState = "qs";
        finalState = "qf";
    }

    public void addState(String state) {
        states.add(state);
    }

    public void addAlphabet(char c) {
        alphabet.add(c);
    }

    public void addTransition(String from, String to, String regex) {
        transitions.add(new Transition(from, to, regex));
    }

    public void addTransition(String from, String to, char ch) throws Exception {
        if (!states.contains(from) || !states.contains(to)) {
            throw new StateException();
        } else if (!alphabet.contains(ch)) {
            throw new AlphabetException();
        }
        transitions.add(new Transition(from, to, ch + ""));
    }

    record Transition(String from, String to, String regex) {
    }

    static class StateException extends Exception {
    }

    static class AlphabetException extends Exception {
    }
}

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NFAToRegex {
    public static void main(String[] args) {
        GNFA gnfa = new GNFA();
    }

    private void setStartState(GNFA gnfa, String startState) throws Exception {
        if (!gnfa.hasState(startState)) {
            throw new StateException();
        }
        gnfa.addTransition(gnfa.startState(), startState, "");
    }

    private void setFinalStates(GNFA gnfa, String[] finalStates) throws Exception {
        for (String finalState : finalStates) {
            if (!gnfa.hasState(finalState)) {
                throw new StateException();
            }
        }
        for (String finalState : finalStates) {
            gnfa.addTransition(finalState, gnfa.finalState(), "");
        }
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

    public String startState() {
        return startState;
    }

    public String finalState() {
        return finalState;
    }

    public boolean hasState(String state) {
        return states.contains(state);
    }

    public boolean hasAlphabet(char c) {
        return alphabet.contains(c);
    }

    record Transition(String from, String to, String regex) {
    }
}

class StateException extends Exception {
}

class AlphabetException extends Exception {
}
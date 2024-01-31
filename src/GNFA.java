import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GNFA {
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
        boolean newTransition = true;
        for (int i = 0, transitionsSize = transitions.size(); i < transitionsSize; i++) {
            if (transitions.get(i).from.equals(from) && transitions.get(i).to.equals(to)) {
                newTransition = false;
                String tmp;
                if (transitions.get(i).regex().matches("\\(.*\\)")) {
                    tmp = transitions.get(i).regex().substring(1, transitions.get(i).regex().length() - 1);
                } else {
                    tmp = transitions.get(i).regex();
                }
                transitions.set(i, new Transition(from, to, "(" + tmp + "+" + regex + ")"));
            }
        }
        if (newTransition) {
            transitions.add(new Transition(from, to, regex));
        }
    }

    public void addTransition(String from, String to, char ch) {
        addTransition(from, to, String.valueOf(ch));
    }

    public void removeState(String state) {
        states.remove(state);
        transitions.removeIf(transition -> transition.from.equals(state) || transition.to.equals(state));
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

    public List<Transition> transitionsFrom(String from) {
        List<Transition> transitions = new ArrayList<>();
        for (Transition transition : this.transitions) {
            if (transition.from.equals(from)) {
                transitions.add(transition);
            }
        }
        return transitions;
    }

    public List<Transition> transitionsTo(String to) {
        List<Transition> transitions = new ArrayList<>();
        for (Transition transition : this.transitions) {
            if (transition.to.equals(to)) {
                transitions.add(transition);
            }
        }
        return transitions;
    }

    public void setStartState(String startState) {
        addTransition(this.startState, startState, "");
    }

    public void addFinalState(String finalState) {
        addTransition(finalState, this.finalState, "");
    }

    @Override
    protected GNFA clone() {
        GNFA gnfa = new GNFA();
        gnfa.states.addAll(states);
        gnfa.alphabet.addAll(alphabet);
        gnfa.transitions.addAll(transitions);
        return gnfa;
    }

    record Transition(String from, String to, String regex) {
    }
}
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NFAToRegex {
    public static void main(String[] args) {
        GNFA gnfa = new GNFA();
        gnfa.addState("0");
        gnfa.addState("1");
        gnfa.addState("2");
        gnfa.addState("3");
        gnfa.addState("4");
        gnfa.addState("t");
        gnfa.addAlphabet('a');
        gnfa.addAlphabet('b');
        try {
            gnfa.setStartState("0");
            gnfa.setFinalStates(new String[]{"4"});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            gnfa.addTransition("0", "1", 'a');
            gnfa.addTransition("0", "t", 'b');
            gnfa.addTransition("t", "t", 'a');
            gnfa.addTransition("t", "t", 'b');
            gnfa.addTransition("1", "t", 'a');
            gnfa.addTransition("1", "2", 'b');
            gnfa.addTransition("2", "3", 'a');
            gnfa.addTransition("2", "2", 'b');
            gnfa.addTransition("3", "3", 'a');
            gnfa.addTransition("3", "4", 'b');
            gnfa.addTransition("4", "4", 'a');
            gnfa.addTransition("4", "4", 'b');
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println(generateRegex(gnfa));
    }

    protected static String generateRegex(GNFA originalGNFA) {
        GNFA gnfa = originalGNFA.clone();
        String current = gnfa.transitionsFrom(gnfa.startState()).get(0).to();
        while (!current.equals(gnfa.finalState())) {
            List<GNFA.Transition> transitions = gnfa.transitionsTo(current);
            List<GNFA.Transition> newTransitions = new ArrayList<>();
            String repeat = "";
            for (GNFA.Transition transition : transitions) {
                if (transition.from().equals(current)) {

                    // Parentheses not needed only if length=1 or if it already contains parentheses at the start and end
                    // But needed if it contains (not nested) parentheses in the middle or no parentheses at all
                    if (transition.regex().matches("^(\\(([^()]*(\\([^()]*\\))?[^()]*)*\\)|.)$")) {
                        repeat = transition.regex() + "*";
                    } else {
                        repeat = "(" + transition.regex() + ")*";
                    }
                    break;
                }
            }
            for (GNFA.Transition transition : transitions) {
                if (transition.from().equals(current)) {
                    continue;
                }
                for (GNFA.Transition transition1 : gnfa.transitionsFrom(current)) {
                    if (transition1.to().equals(current)) {
                        continue;
                    }
                    newTransitions.add(new GNFA.Transition(transition.from(), transition1.to(),
                            transition.regex() + repeat + transition1.regex()));
                }
            }
            gnfa.removeState(current);
            for (GNFA.Transition transition : newTransitions) {
                gnfa.addTransition(transition.from(), transition.to(), transition.regex());
            }

            current = gnfa.transitionsFrom(gnfa.startState()).get(0).to();
        }

        return gnfa.transitionsFrom(gnfa.startState()).get(0).regex();
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

    public void addTransition(String from, String to, char ch) throws Exception {
        if (!hasState(from) || !hasState(to)) {
            throw new StateException();
        } else if (!alphabet.contains(ch)) {
            throw new AlphabetException();
        }
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

    public void setStartState(String startState) throws Exception {
        if (!hasState(startState)) {
            throw new StateException();
        }
        addTransition(this.startState, startState, "");
    }

    public void setFinalStates(String[] finalStates) throws Exception {
        for (String finalState : finalStates) {
            if (!hasState(finalState)) {
                throw new StateException();
            }
        }
        for (String finalState : finalStates) {
            addTransition(finalState, this.finalState, "");
        }
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

class StateException extends Exception {
}

class AlphabetException extends Exception {
}
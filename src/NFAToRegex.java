import java.util.*;

public class NFAToRegex {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        GNFA gnfa = new GNFA();
        String input;
        int n = getNumber("Enter the number of states: ");
        for (int i = 0; i < n; i++) {
            String state = getString("Enter the name of state " + (i + 1) + ": ");
            if (state.equals("qs") || state.equals("qf") || state.matches("^\\s*$")) {
                System.out.println("Invalid state name. qs, qf and \" \" are reserved. Please enter another name.");
                i--;
            } else if (gnfa.hasState(state)) {
                System.out.println("Duplicate state name. Please enter another name.");
                i--;
            } else {
                gnfa.addState(state);
            }
        }
        int m = getNumber("Enter the number of alphabets: ");
        for (int i = 0; i < m; i++) {
            input = getString("Enter the name of alphabet " + (i + 1) + ": ");
            char alphabet = input.charAt(0);
            if (!input.matches("^\\S$")) {
                System.out.println("Invalid input. Please enter a character.");
                i--;
            } else if (gnfa.hasAlphabet(alphabet)) {
                System.out.println("Duplicate alphabet name. Please enter another name.");
                i--;
            } else {
                gnfa.addAlphabet(alphabet);
            }
        }
        while (true) {
            String start = getString("Enter the name of start state: ");
            if (!gnfa.hasState(start)) {
                System.out.println("State not found. Please enter a valid state name.");
            } else {
                gnfa.setStartState(start);
                break;
            }
        }
        int k = getNumber("Enter the number of final states: ");
        for (int i = 0; i < k; i++) {
            String finalState = getString("Enter the name of final state " + (i + 1) + ": ");
            if (!gnfa.hasState(finalState)) {
                System.out.println("State not found. Please enter a valid state name.");
                i--;
            } else {
                gnfa.addFinalState(finalState);
            }
        }
        int t = getNumber("Enter the number of transitions: ");
        for (int i = 0; i < t; i++) {
            input = getString(
                    "Enter the source state, destination state and alphabet of transition " + (i + 1) + " seperated by space: ");
            String[] inputs = input.split(" ");
            if (inputs.length != 3) {
                System.out.println("Invalid input.");
                i--;
                continue;
            }
            String source = inputs[0];
            String destination = inputs[1];
            char symbol = inputs[2].charAt(0);
            if (!gnfa.hasState(source) || !gnfa.hasState(destination)) {
                System.out.println("State not found. Please enter valid state names.");
                i--;
            } else if (!inputs[2].matches("^\\S$")) {
                System.out.println("Invalid Alphabet. Please enter a character.");
                i--;
            } else if (!gnfa.hasAlphabet(symbol)) {
                System.out.println("Alphabet not found. Please enter a valid alphabet name.");
                i--;
            } else {
                gnfa.addTransition(source, destination, symbol);
            }
        }
        System.out.println(generateRegex(gnfa));
        scanner.close();
    }

    private static String getString(String prompt) {
        String input;
        System.out.print(prompt);
        input = scanner.nextLine();
        return input;
    }

    private static int getNumber(String prompt) {
        String input;
        System.out.print(prompt);
        input = scanner.nextLine();
        while (!input.matches("^[0-9]+$")) {
            System.out.println("Invalid input. Please enter a number.");
            System.out.print(prompt);
            input = scanner.nextLine();
        }
        return Integer.parseInt(input);
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
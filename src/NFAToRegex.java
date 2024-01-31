import java.util.ArrayList;
import java.util.List;

public class NFAToRegex {
    public static void main(String[] args) {
        GNFA gnfa = new GNFA();
        UI ui = new UI();
        ui.constructMachine(gnfa);
        String regex = generateRegex(gnfa);
        ui.displayRegex(regex);
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
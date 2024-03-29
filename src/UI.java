import java.util.Scanner;

public class UI {
    private final Scanner scanner;

    public UI() {
        scanner = new Scanner(System.in);
    }

    private static void separator() {
        System.out.println(
                "|---------------------------------------------------------------------------------------------------");
    }

    private static void printLine(String x) {
        System.out.println("| " + x);
    }

    public void constructMachine(GNFA gnfa) {
        System.out.println(
                "----------------------------------------------------------------------------------------------------");
        getStates(gnfa);
        separator();
        getAlphabet(gnfa);
        separator();
        getStartState(gnfa);
        separator();
        getFinalStates(gnfa);
        separator();
        getTransitions(gnfa);
        separator();
    }

    public void displayRegex(String regex) {
        printLine("Regex: " + regex);
        System.out.println(
                "----------------------------------------------------------------------------------------------------");
    }

    private void getStates(GNFA gnfa) {
        int n = getNumber("Enter the number of states: ");
        for (int i = 0; i < n; i++) {
            String state = getString("Enter the name of state " + (i + 1) + ": ");
            if (state.equals("qs") || state.equals("qf") || state.matches("^\\s*$")) {
                printLine("|Invalid state name. qs, qf and \" \" are reserved. Please enter another name.");
                i--;
            } else if (gnfa.hasState(state)) {
                printLine("|Duplicate state name. Please enter another name.");
                i--;
            } else {
                gnfa.addState(state);
            }
        }
    }

    private void getAlphabet(GNFA gnfa) {
        String input;
        int m = getNumber("Enter the number of alphabets: ");
        for (int i = 0; i < m; i++) {
            input = getString("Enter the name of alphabet " + (i + 1) + ": ");
            char alphabet = input.charAt(0);
            if (!input.matches("^\\S$")) {
                printLine("Invalid input. Please enter a character.");
                i--;
            } else if (gnfa.hasAlphabet(alphabet)) {
                printLine("Duplicate alphabet name. Please enter another name.");
                i--;
            } else {
                gnfa.addAlphabet(alphabet);
            }
        }
    }

    private void getStartState(GNFA gnfa) {
        while (true) {
            String start = getString("Enter the name of start state: ");
            if (!gnfa.hasState(start)) {
                printLine("State not found. Please enter a valid state name.");
            } else {
                gnfa.setStartState(start);
                break;
            }
        }
    }

    private void getFinalStates(GNFA gnfa) {
        int k = getNumber("Enter the number of final states: ");
        for (int i = 0; i < k; i++) {
            String finalState = getString("Enter the name of final state " + (i + 1) + ": ");
            if (!gnfa.hasState(finalState)) {
                printLine("State not found. Please enter a valid state name.");
                i--;
            } else {
                gnfa.addFinalState(finalState);
            }
        }
    }

    private void getTransitions(GNFA gnfa) {
        String input;
        int t = getNumber("Enter the number of transitions: ");
        printLine("*Use bb for epsilon transition.*");
        for (int i = 0; i < t; i++) {
            input = getString(
                    "Enter the source state, destination state and alphabet of transition " + (i + 1) + " seperated by space: ");
            String[] inputs = input.split(" ");
            if (inputs.length != 3) {
                printLine("Invalid input.");
                i--;
                continue;
            }
            String source = inputs[0];
            String destination = inputs[1];
            char symbol = inputs[2].charAt(0);
            if (!gnfa.hasState(source) || !gnfa.hasState(destination)) {
                printLine("State not found. Please enter valid state names.");
                i--;
            } else if (inputs[2].equals("bb")) {
                gnfa.addTransition(source, destination, "");
            } else if (!inputs[2].matches("^\\S$")) {
                printLine("Invalid Alphabet. Please enter a character.");
                i--;
            } else if (!gnfa.hasAlphabet(symbol)) {
                printLine("Alphabet not found. Please enter a valid alphabet name.");
                i--;
            } else {
                gnfa.addTransition(source, destination, symbol);
            }
        }
    }

    private String getString(String prompt) {
        String input;
        System.out.print("| " + prompt);
        input = scanner.nextLine();
        return input;
    }

    private int getNumber(String prompt) {
        String input;
        System.out.print("| " + prompt);
        input = scanner.nextLine();
        while (!input.matches("^[0-9]+$")) {
            printLine("Invalid input. Please enter a number.");
            System.out.print(prompt);
            input = scanner.nextLine();
        }
        return Integer.parseInt(input);
    }
}

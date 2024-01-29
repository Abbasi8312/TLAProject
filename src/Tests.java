public class Tests {
    public static void main(String[] args) {
        test1();
        System.out.println("--------------------");
        test2();
        System.out.println("--------------------");
        test3();
        System.out.println("--------------------");
    }

    private static void test1() {
        GNFA gnfa = new GNFA();
        gnfa.addState("0");
        gnfa.addState("1");
        gnfa.addState("2");
        gnfa.addState("3");
        gnfa.addState("4");
        gnfa.addState("trap");
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
            gnfa.addTransition("0", "trap", 'b');
            gnfa.addTransition("trap", "trap", 'a');
            gnfa.addTransition("trap", "trap", 'b');
            gnfa.addTransition("1", "trap", 'a');
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
        System.out.println(NFAToRegex.generateRegex(gnfa));
    }

    private static void test2() {
        GNFA gnfa = new GNFA();
        gnfa.addState("0");
        gnfa.addState("1");
        gnfa.addState("2");
        gnfa.addState("3");
        gnfa.addState("4");
        gnfa.addState("5");
        gnfa.addAlphabet('a');
        gnfa.addAlphabet('b');
        try {
            gnfa.setStartState("0");
            gnfa.setFinalStates(new String[]{"5"});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            gnfa.addTransition("0", "1", 'a');
            gnfa.addTransition("0", "2", 'b');
            gnfa.addTransition("1", "3", 'a');
            gnfa.addTransition("1", "3", 'b');
            gnfa.addTransition("2", "1", 'a');
            gnfa.addTransition("2", "4", 'b');
            gnfa.addTransition("3", "5", 'a');
            gnfa.addTransition("3", "4", 'b');
            gnfa.addTransition("4", "5", 'a');
            gnfa.addTransition("5", "3", 'a');
            gnfa.addTransition("5", "4", 'b');
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println(NFAToRegex.generateRegex(gnfa));
    }
    private static void test3() {
        GNFA gnfa = new GNFA();
        gnfa.addState("0");
        gnfa.addState("1");
        gnfa.addState("2");
        gnfa.addState("3");
        gnfa.addAlphabet('a');
        gnfa.addAlphabet('b');
        try {
            gnfa.setStartState("0");
            gnfa.setFinalStates(new String[]{"3"});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            gnfa.addTransition("0", "1", 'a');
            gnfa.addTransition("0", "0", 'b');
            gnfa.addTransition("1", "1", 'a');
            gnfa.addTransition("1", "2", 'b');
            gnfa.addTransition("2", "2", 'a');
            gnfa.addTransition("2", "3", 'b');
            gnfa.addTransition("3", "2", 'a');
            gnfa.addTransition("3", "1", 'b');
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println(NFAToRegex.generateRegex(gnfa));
    }

}
public class UnexpectedSituation {

    public static void exit(String s, Exception e) {
        System.err.println("Unexpected situation: "+ s +"\nExiting program immediately.");
        if (e != null) {
            e.printStackTrace(System.err);
        }
        System.exit(-1);
    }

}
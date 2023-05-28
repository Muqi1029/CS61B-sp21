package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Muqi
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
//        System.out.println("firstArg: " + firstArg);
        switch(firstArg) {
            case "init":
                validateArgs(args, 1);
                Repository.init();
                break;
            case "add":
                /** java gitlet.Main add [file name] */
                validateArgs(args, 2);
                Repository.add(args[1]);
                break;
            case "commit":
                /** java gitlet.Main commit [message] */
                validateArgs(args, 2);
                Repository.commit(args[1]);
                break;
            case "rm":
                /** java gitlet.Main rm [file name] */
                validateArgs(args, 2);
                Repository.rm(args[1]);
                break;
            case "log":
                validateArgs(args, 1);
                Repository.log();
                break;
            default:
                System.out.println("Incorrect operands.");
            // TODO: FILL THE REST IN
        }
    }
    private static void validateArgs(String[] args, int num) {
        if (args.length != num) {
            System.out.println("the format of your input is wrong!");
            System.exit(0);
        }

    }

}

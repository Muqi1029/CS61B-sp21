package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Muqi
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            /** If a user doesn't input any arguments */
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
                /** java gitlet.Main log */
                validateArgs(args, 1);
                Repository.log();
                break;
            case "global-log": // TODO
                break;
            case "find":
                /** java gitlet.Main find [file name] */
                validateArgs(args, 2);
                Repository.find(args[1]);
                break;
            case "status":
                /** java gitlet.Main status */
                validateArgs(args, 1);
                Repository.status();
                break;
            case "checkout":
                validateArgs(args, 2, 4);
                Repository.checkout(args);
                break;
            case "branch":
                validateArgs(args, 2);
                Repository.branch(args[1]);
                break;
            default:
                /** If a user inputs a command that doesn't exist */
                System.out.println("No command with that name exist.");
        }
    }
    private static void validateArgs(String[] args, int num) {
        if (args.length != num) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }
    private static void validateArgs(String[] args, int begin, int end) {
        if (args.length < begin || args.length > end) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }

}

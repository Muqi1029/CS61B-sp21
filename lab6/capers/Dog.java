package capers;


import java.io.File;
import java.io.Serializable;

/** Represents a dog that can be serialized.
 * @author Muqi
*/
public class Dog implements Serializable {

    /** Folder that dogs live in. */
    static final File DOG_FOLDER = Utils.join(CapersRepository.CAPERS_FOLDER, ".dogs");

    /** Age of dog. */
    private int age;
    /** Breed of dog. */
    private String breed;
    /** Name of dog. */
    private String name;

    /**
     * Creates a dog object with the specified parameters.
     * @param name Name of dog
     * @param breed Breed of dog
     * @param age Age of dog
     */
    public Dog(String name, String breed, int age) {
        this.age = age;
        this.breed = breed;
        this.name = name;
    }

    /**
     * Reads in and deserializes a dog from a file with name NAME in DOG_FOLDER.
     *
     * @param name Name of dog to load
     * @return Dog read from file
     */
    public static Dog fromFile(String name) {
        File file = Utils.join(DOG_FOLDER, name);
        if (!file.exists()) {
            Utils.exitWithError("the dog with the name doesn't exist.");
        }
        return Utils.readObject(file, Dog.class);
    }

    /**
     * Increases a dog's age and celebrates!
     */
    public void haveBirthday() {
        age += 1;
        System.out.println(toString());
        System.out.println("Happy birthday! Woof! Woof!");
        saveDog();
    }

    /**
     * Saves a dog to a file for future use.
     */
    public void saveDog() {
        if (!DOG_FOLDER.exists()) {
            boolean mkdir = DOG_FOLDER.mkdir();
            if (!mkdir) {
                Utils.exitWithError("fail to create dog dir.");
            }
        }
        File dog = Utils.join(DOG_FOLDER, name);
        Utils.writeObject(dog, this);
    }

    @Override
    public String toString() {
        return String.format(
            "Woof! My name is %s and I am a %s! I am %d years old! Woof!",
            name, breed, age);
    }

}

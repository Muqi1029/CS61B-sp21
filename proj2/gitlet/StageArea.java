package gitlet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * stage area in gitlet
 * @Author muqi
 */
public class StageArea implements Serializable {

    public static Map<String, String> stageMap = new HashMap<>();

    public static List<String> removalList = new ArrayList<>();


}

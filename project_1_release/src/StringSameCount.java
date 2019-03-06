import java.util.*;

public class StringSameCount {
    private HashMap map;
    private int counter;

    public StringSameCount() {
        map = new HashMap<>();
    }
    @SuppressWarnings("unchecked")
    public void hashInsert(String string) {
        if (map.containsKey(string)) {   //判断指定的Key是否存在
            counter = (Integer) map.get(string);  //根据key取得value
            map.put(string, ++counter);
        } else {
            map.put(string, 1);
        }
    }

    public HashMap getHashMap() {
        return map;
    }
}
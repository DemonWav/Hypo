package scenario07;

import java.util.List;

public class ChildClass extends ParentClass {

    public ChildClass(int right, int left, String down, long up, Object back, List<String> forward) {
        super(left, right, up, down, forward, back);
    }
}

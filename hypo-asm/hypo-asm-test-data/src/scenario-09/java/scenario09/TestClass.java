package scenario09;

// Compiled with JDK 21
public enum TestClass {
    ONE {
        @Override
        void doThing() {
        }
    },
    TWO {
        @Override
        void doThing() {
        }
    },
    THREE {
        @Override
        void doThing() {
        }
    },
    ;

    abstract void doThing();
}

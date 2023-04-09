package test;

public class TestExtend {
    public static void main(String[] args) {
        B b = new B();
        b.say1(2);
    }
}

class A {
    public int ah = 1;

    public void say1() {
        System.out.println(1);
    }
}

class B extends A {

    @Override
    public void say1() {
        System.out.println(2);
    }

    public void say1(int i) {
        System.out.println(i);
    }
}

import java.util.Arrays;

public class Test {
    public static void main(String[] args) {
        ExtendedState s = new ExtendedState(new State());
        s.makeMove(0);
        s.makeMove(2);
        s.makeMove(1);
        s.makeMove(4);
        s.makeMove(3);
        s.makeMove(5);
        s.makeMove(7);
        for (int i = s.clonedField.length - 1 ; i >= 0; i--){
	        System.out.println(Arrays.toString(s.clonedField[i]));
	    }
    }
}
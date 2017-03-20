public class PlayerSkeleton {

    // Implement this function to have a working system
    // Legal move, 2D array: [Orientation, Slot]
    public int pickMove(State s, int[][] legalMoves) {
        return 0;
    }

    public int run() {
        State s = new State();
        new TFrame(s);
        PlayerSkeleton p = new PlayerSkeleton();
        while(!s.hasLost()) {
            s.makeMove(p.pickMove(s,s.legalMoves()));
            s.draw();
            s.drawNext(0,0);
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("You have completed "+s.getRowsCleared()+" rows.");
        return s.getRowsCleared();
    }

    public static void main(String[] args) {
        State s = new State();
        new TFrame(s);
        PlayerSkeleton p = new PlayerSkeleton();
        while(!s.hasLost()) {
            s.makeMove(p.pickMove(s,s.legalMoves()));
            s.draw();
            s.drawNext(0,0);
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("You have completed "+s.getRowsCleared()+" rows.");
    }

}


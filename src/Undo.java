import java.awt.*;


public class Undo {
    //石を打つ場所
    public int x;
    public int y;
    //ひっくり返った石の数
    public int count;
    //ひっくり返った石の場所
    public Point[] pos;


    public Undo(int x, int y){
        this.x = x;
        this.y = y;
        count = 0;
        pos = new Point[64];
    }

    
}

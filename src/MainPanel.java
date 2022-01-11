import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import javax.swing.*;

//参考URL https://aidiary.hatenablog.com/entry/20040918/1251373370



public class MainPanel extends JPanel implements MouseListener{
    //マスのサイズ
    private static final int GS = 64;
    //マスの数 
    public static final int MASU = 8;
    //盤面の大きさ＝メインパネルの大きさ  
    private static final int WIDTH = GS * MASU;
    private static final int HEIGHT = WIDTH;
    //空白    
    private static final int BLANK = 0;
    //黒石
    private static final int BLACK_STONE = 1;
    //白石
    private static final int WHITE_STONE = -1;
    //休止時間
    private static final int SLEEP_TIME = 500;
    //終了時の石の数
    private static final int END_NUMBER = 60;
    //ゲームの状態
    private static final int START = 0;
    private static final int PLAY = 1;
    private static final int YOU_WIN = 2;
    private static final int YOU_LOSE = 3;
    private static final int DRAW = 4;

    //盤面
    public int[][] board = new int[MASU][MASU];
    //白の番か
    private boolean flagForWhite;
    //打たれた石の数
    private int putNumber;
    //石を打つ音
    private AudioClip kachi;
    //ゲームの状態
    private int gameState;
    //AI
    private AI ai;                  
    
    //情報パネルへの参照
    private InfoPanel infoPanel;

    public MainPanel(InfoPanel infoPanel){
        //othelloでpack()するときに必要
        setPreferredSize(new Dimension(WIDTH,HEIGHT));
        this.infoPanel = infoPanel;

        //盤面を初期化する
        initBoard();
        //サウンドをロードする
        kachi = Applet.newAudioClip(getClass().getResource("kachi.wav"));
        //
        ai = new AI(this);
        //マウス操作を受け付ける
        addMouseListener(this);
        //START状態（タイトル表示）
        gameState = START;
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
       
        //盤面を描く   
        drawBoard(g);
        switch(gameState){
            case START:
                drawTextCentering(g,"OTHELLO");
                break;
            case PLAY:
                //石を描く
                drawStone(g);
                //盤面の石の数を数える
                Counter counter = countStone();
                //ラベルに表示
                infoPanel.setBlackLabel(counter.blackCount);
                infoPanel.setWhiteLabel(counter.whiteCount);
                break;
            case YOU_WIN:
                drawTextCentering(g,"YOU WIN");
                break;
            case YOU_LOSE:
                drawTextCentering(g,"YOU LOSE");    
                break;
            case DRAW:
                drawTextCentering(g,"DRAW");
                break;
        }
    }

    //マウスをクリックしたとき石を打つ
    public void mouseClicked(MouseEvent e){
        switch(gameState){
                case START:
                    //START画面でクリックされたらゲーム開始
                    gameState = PLAY;
                    break;
                case PLAY:
                    //どこのマスか調べる
                    int x = e.getX() / GS;
                    int y = e.getY() / GS;
                    //(x,y)に石が打てる場合だけ打つ
                    if(canPutDown(x, y)){
                        //戻せるように記録しておく
                        Undo undo =new Undo(x, y);
                        //その場所に石を打つ
                        putDownStone(x, y, false);
                        //ひっくり返す
                        reverse(undo, false);
                        //
                        endGame();
                        //
                        nextTurn();
                        //
                        if(countCanPutDownStone() == 0){
                            System.out.println("AI PASS!");
                            nextTurn();
                            return;
                        }else{
                            //
                            ai.compute();
                        }
                    }
                    break;
                case YOU_WIN:
                case YOU_LOSE:
                case DRAW:
                    //ゲーム終了時にクリックされたらSTARTへと戻る
                    gameState = START;
                    //盤面初期化
                    initBoard();
                    break;
        }
        
        //再描画する
        repaint();
    }

    //盤面を初期化する
    public void initBoard(){
        for(int y = 0; y < MASU; y++){
            for(int x = 0; x < MASU; x++){
                board[y][x] = BLANK;
            }
        }
        //初期配置
        board[3][3] = board[4][4] = WHITE_STONE;
        board[3][4] = board[4][3] = BLACK_STONE;
        //黒番から始める
        flagForWhite = false;
        putNumber = 0;
    }


    //盤面を描く
    private void drawBoard(Graphics g){
        //マスを塗りつぶす
        g.setColor(new Color(0,128,128));
        g.fillRect(0,0,WIDTH,HEIGHT);
            for(int y = 0; y < MASU; y++){
                for(int x = 0; x < MASU; x++){
                   //マス枠を描画する
                    g.setColor(Color.BLACK);
                    g.drawRect(x * GS,y * GS,GS,GS);
                }
           }

    }

    //石を描く
    private void drawStone(Graphics g){
        for(int y = 0; y < MASU; y++){
            for(int x = 0; x < MASU; x++){
                if(board[y][x] == BLANK){
                    continue;
                }else if(board[y][x] == BLACK_STONE){
                    g.setColor(Color.BLACK);
                }else{
                    g.setColor(Color.WHITE);
                }
                g.fillOval(x * GS + 3, y * GS + 3, GS - 6, GS -6);
            }
        }
    }



    //盤面に石を打つ
    public void putDownStone(int x,int y, boolean tryAndError){
        int stone;

        //どっちの手番か調べて石の色を決める
        if(flagForWhite){
            stone = WHITE_STONE;
        }else{
            stone = BLACK_STONE;
        }
        //石を打つ
        board[y][x] = stone;
        //
        if(!tryAndError){
            putNumber++;
            //
            kachi.play();
            //
            update(getGraphics());
            //
            sleep();
        }   
    }

    //
    public boolean canPutDown(int x,int y) {
        //
        if(x >= MASU || y >= MASU)
            return false;
        //
        if(board[y][x] != BLANK)
          return false;
        //
        //
        if(canPutDown(x, y, 1, 0))
            return true;
        //
        if(canPutDown(x, y, 0, 1))
            return true;
        //
        if(canPutDown(x, y, -1, 0))
            return true;
        //
        if(canPutDown(x, y, 0, -1))
            return true;
        //
        if(canPutDown(x, y, 1, 1)) 
            return true;
        //   
        if(canPutDown(x, y, -1, -1))
            return true;
        //
        if(canPutDown(x, y, 1, -1))
            return true;
        //
        if(canPutDown(x, y, -1, 1))
            return true;

        //
        return false;
    }
    

    //
    private boolean canPutDown(int x, int y, int vecX, int vecY){
        int putStone;

        //
        if(flagForWhite){
            putStone = WHITE_STONE;
        }else{
            putStone = BLACK_STONE;
        }

        //
        x += vecX;
        y += vecY;
        //
        if(x < 0 || x >= MASU || y < 0 || y >= MASU)
            return false;
        //
        if(board[y][x] == putStone)
            return false;
        //
        if(board[y][x] == BLANK)
            return false;

        //
        x += vecX;
        y += vecY;
        //
        while(x >=0 && x < MASU && y >= 0 && y < MASU){
            //
            if(board[y][x] == BLANK)
                return false;
            //
            if(board[y][x] == putStone)
                return true;
            x += vecX;
            y += vecY;
        }
        //
        return false;
    }

    //
    public void reverse(Undo undo, boolean tryAndError){
        //
        if(canPutDown(undo.x, undo.y, 1, 0))      reverse(undo, 1, 0, tryAndError);
        if(canPutDown(undo.x, undo.y, 0, 1))      reverse(undo, 0, 1, tryAndError);
        if(canPutDown(undo.x, undo.y, -1, 0))     reverse(undo, -1, 0, tryAndError);
        if(canPutDown(undo.x, undo.y, 0, -1))     reverse(undo, 0, -1, tryAndError);
        if(canPutDown(undo.x, undo.y, 1, 1))      reverse(undo, 1, 1,  tryAndError);
        if(canPutDown(undo.x, undo.y, -1, -1))    reverse(undo, -1, -1, tryAndError);
        if(canPutDown(undo.x, undo.y, 1, -1))     reverse(undo, 1, -1, tryAndError);
        if(canPutDown(undo.x, undo.y, -1, 1))     reverse(undo, -1, 1, tryAndError);
    }
    
    //
    private void reverse(Undo undo, int vecX, int vecY, boolean tryAndError){
        int putStone;
        int x = undo.x;
        int y = undo.y;

        if(flagForWhite){
            putStone = WHITE_STONE;
        }else{
            putStone = BLACK_STONE;
        }


        //
        //
        x += vecX;
        y += vecY;
        while(board[y][x] != putStone){
            //
            board[y][x] = putStone;
            //
            undo.pos[undo.count++] = new Point(x, y);
            if(!tryAndError){
                //
                kachi.play();
                //
                update(getGraphics());
                //
                sleep();
            }
            x += vecX;
            y += vecY;
        }
    }


    //
    public void undoBoard(Undo undo){
        int c = 0;

        while(undo.pos[c] != null){
            //
            int x = undo.pos[c].x;
            int y = undo.pos[c].y;
            //
            //
            board[y][x] *= -1;
            c++;
        }
        //
        board[undo.y][undo.x] = BLANK;
        //
        nextTurn();
    }
    
    //
    public void nextTurn(){
        //
        flagForWhite = !flagForWhite;
    }

    //
    public int countCanPutDownStone(){
        int count = 0;

        for(int y = 0; y < MainPanel.MASU; y++){
            for(int x = 0; x < MainPanel.MASU; x++){
                if(canPutDown(x, y)){
                    count++;
                }
            }
        }
        return count;
    }

    //
    private void sleep(){
        try{
            Thread.sleep(SLEEP_TIME);
        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }


    //
    private void drawTextCentering(Graphics g, String s){
        Font f = new Font("SansSerif", Font.BOLD, 20);
        g.setFont(f);
        FontMetrics fm = g.getFontMetrics();
        g.setColor(Color.YELLOW);
        g.drawString(s,WIDTH / 2 - fm.stringWidth(s) / 2, HEIGHT / 2 + fm.getDescent());
    }

    //
    public boolean endGame(){
        //
        if(putNumber == END_NUMBER){
            //
            Counter counter;
            counter = countStone();
            //
            //
            //
            if(counter.blackCount > 32){
                gameState = YOU_WIN;
            }else if(counter.blackCount < 32){
                gameState = YOU_LOSE;
            }else{
                gameState = DRAW;
            }
            repaint();
            return true;
        }
        return false;
    }

    //
    public Counter countStone(){
        Counter counter = new Counter();

        for(int y = 0; y < MASU; y++){
            for(int x  = 0; x < MASU; x++){
                if(board[y][x] == BLACK_STONE)
                    counter.blackCount++;
                if(board[y][x] == WHITE_STONE)
                    counter.whiteCount++;
            }
        }
        return counter;
    }


    //
    public int getBoard(int x, int y){
        return board[y][x];
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public void mousePressed(MouseEvent e){
    }

    public void mouseEntered(MouseEvent e){
    }

    public void mouseExited(MouseEvent e){
    }

    public void mouseReleased(MouseEvent e){
    }

    public class Counter{
        public int blackCount;
        public int whiteCount;

        public Counter(){
            blackCount = 0;
            whiteCount = 0;
        }
    }















}

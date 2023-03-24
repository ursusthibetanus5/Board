//awtを引き込む
// Abustract Window Toolkit
import java.awt.*;
import java.awt.event.*;
//今は非推奨
import java.applet.*;
//GUIの為のもの
import javax.swing.*;


//JPanelを継承しMouseListenerを実装したMainPanelを宣言
public class MainPanel extends JPanel implements MouseListener{
    //マスのサイズ
    //変更できない変数GSに64を代入
    private static final int GS = 64;
    //マスの数
    //変更できない変数MASUに８を代入 
    public static final int MASU = 8;
    //盤面の大きさ＝メインパネルの大きさ
    //変更できない変数WIDTHにGS*MASUを代入  
    private static final int WIDTH = GS * MASU;
    //変更出来ない変数HEIGHTにWIDTHを代入
    private static final int HEIGHT = WIDTH;
    //空白
    //変更できない変数BLANKに0を代入    
    private static final int BLANK = 0;
    //黒石
    //変更できない変数BLACK_STONEに1を代入
    private static final int BLACK_STONE = 1;
    //白石
    //変更できない変数WHITE_STONEに-1を代入
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
    //2次元配列boardを宣言して配列の中にMASUを入れて配列を作成
    public int[][] board = new int[MASU][MASU];
    //白の番か
    //boolean変数flagForWhiteを宣言
    private boolean flagForWhite;
    //打たれた石の数
    private int putNumber;
    //石を打つ音
    private AudioClip kachi;
    //ゲームの状態
    private int gameState;
    //AIを参照
    //AI型の変数aiを宣言
    private AI ai;                  
    
    //情報パネルへの参照
    // InfoPanel型変数infoPanel宣言
    private InfoPanel infoPanel;

    public MainPanel(InfoPanel infoPanel){
        //テキストフィールドのサイズをピクセル単位で設定
        //othelloでpack()するときに必要
        setPreferredSize(new Dimension(WIDTH,HEIGHT));
        // このクラスのinfoPanelにinfoPanel代入
        this.infoPanel = infoPanel;

        //盤面を初期化する
        initBoard();
        //サウンドをロードする
        kachi = Applet.newAudioClip(getClass().getResource("kachi.wav"));
        //AIを作成
        //AIというオブジェクト
        ai = new AI(this);
        //マウス操作を受け付ける
        addMouseListener(this);
        //START状態（タイトル表示）
        gameState = START;
    }


    //
    public void paintComponent(Graphics g){
        //親クラスの値を参照
        super.paintComponent(g);
       
        //盤面を描く   
        drawBoard(g);
        //ゲームの状態が
        switch(gameState){
            //STARTの場合
            case START:
                drawTextCentering(g,"OTHELLO");
                break;
            //PLAYの場合
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
                    int x = e.getX() / GS; //座標　/ マス
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
                        //もし石が置けるところが0ならば
                        if(countCanPutDownStone() == 0){
                            //AI PASS!と表示
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
        //yがMASUより小さいとき
        for(int y = 0; y < MASU; y++){
            //ｘがMASUより小さいとき
            for(int x = 0; x < MASU; x++){
                board[y][x] = BLANK;
            }
        }
        //初期配置
        board[3][3] = board[4][4] = WHITE_STONE;
        board[3][4] = board[4][3] = BLACK_STONE;
        //黒番から始める
        flagForWhite = false;
        //打たれた石の数は0個
        putNumber = 0;
    }


    //盤面を描く
    private void drawBoard(Graphics g){
        //盤面の色
        g.setColor(new Color(148,0,211));
        //塗りつぶされた長方形
        g.fillRect(0,0,WIDTH,HEIGHT);
            //ｙがMASUより小さいとき
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
        //もし白の番なら
        if(flagForWhite){
            //石は白色です
            stone = WHITE_STONE;
        //そうでないなら
        }else{
            //石は黒色です
            stone = BLACK_STONE;
        }
        //石を打つ
        board[y][x] = stone;
        //コンピュータの思考中でなければ実際に打って再描画する
        if(!tryAndError){
            putNumber++;
            //音が鳴る
            kachi.play();
            //盤面が更新されたので再描画
            update(getGraphics());
            //小休止
            sleep();
        }   
    }

    //石を置くことが出来るか判別する
    public boolean canPutDown(int x,int y) {
        //もしxがMASU以上かつyがMASU以上なら(盤面外)
            if(x >= MASU || y >= MASU)
            //置けない
            return false;
        //もしそこに石があったなら
        if(board[y][x] != BLANK)
            //置けない
          return false;
        //8方向のうち一か所でもひっくり返せればこの場所に打てる
        //ひっくり返せるかどうかはもう1つのcanPutDownで調べる

        // 右
        if(canPutDown(x, y, 1, 0))
            return true;
        //上
        if(canPutDown(x, y, 0, 1))
            return true;
        //左
        if(canPutDown(x, y, -1, 0))
            return true;
        //下
        if(canPutDown(x, y, 0, -1))
            return true;
        //右斜め上
        if(canPutDown(x, y, 1, 1)) 
            return true;
        //左斜め下
        if(canPutDown(x, y, -1, -1))
            return true;
        //右斜め下
        if(canPutDown(x, y, 1, -1))
            return true;
        //左斜め上
        if(canPutDown(x, y, -1, 1))
            return true;

        //どれもだめなら置けない
        return false;
    }
    

    //vecX、vecYの方向にひっくり返せる石があるか調べる
    private boolean canPutDown(int x, int y, int vecX, int vecY){
        int putStone;

        //もし白の番なら
        if(flagForWhite){
            //置く石は白
            putStone = WHITE_STONE;
         //そうでないなら
        }else{
            //置く石は黒
            putStone = BLACK_STONE;
        }

        //xにxのベクトルを足してxに代入
        x += vecX;
        y += vecY;
        //盤面の範囲外
        if(x < 0 || x >= MASU || y < 0 || y >= MASU)
            return false;
        //隣が自分の石
        if(board[y][x] == putStone)
            return false;
        //隣が空白
        if(board[y][x] == BLANK)
            return false;

        //さらに隣
        x += vecX;
        y += vecY;
        //隣に石がある間はループする
        while(x >=0 && x < MASU && y >= 0 && y < MASU){
            //もし空白なら打てない
            if(board[y][x] == BLANK)
                return false;
            //自分の石があれば打てる
            if(board[y][x] == putStone)
                return true;
            x += vecX;
            y += vecY;
        }
        //相手の石しかない場合はいずれ盤面外
        return false;
    }

    //石をひっくり返す場所
    public void reverse(Undo undo, boolean tryAndError){
        //8方向
        if(canPutDown(undo.x, undo.y, 1, 0))      reverse(undo, 1, 0, tryAndError);
        if(canPutDown(undo.x, undo.y, 0, 1))      reverse(undo, 0, 1, tryAndError);
        if(canPutDown(undo.x, undo.y, -1, 0))     reverse(undo, -1, 0, tryAndError);
        if(canPutDown(undo.x, undo.y, 0, -1))     reverse(undo, 0, -1, tryAndError);
        if(canPutDown(undo.x, undo.y, 1, 1))      reverse(undo, 1, 1,  tryAndError);
        if(canPutDown(undo.x, undo.y, -1, -1))    reverse(undo, -1, -1, tryAndError);
        if(canPutDown(undo.x, undo.y, 1, -1))     reverse(undo, 1, -1, tryAndError);
        if(canPutDown(undo.x, undo.y, -1, 1))     reverse(undo, -1, 1, tryAndError);
    }
    
    //石をひっくり返す
    private void reverse(Undo undo, int vecX, int vecY, boolean tryAndError){
        int putStone;
        int x = undo.x;
        int y = undo.y;

        //今どっちの番？
        if(flagForWhite){
            putStone = WHITE_STONE;
        }else{
            putStone = BLACK_STONE;
        }


        //相手の石がある間はひっくり返し続ける
        //(x,y)に打てるのは確認済みなので相手の石は必ずある
        x += vecX;
        y += vecY;
        //相手の石がある間
        while(board[y][x] != putStone){
            //ひっくり返す
            board[y][x] = putStone;
            //ひっくり返した場所を記憶しておく
            undo.pos[undo.count++] = new Point(x, y);
            if(!tryAndError){
                //音が鳴る
                kachi.play();
                //盤面が更新されたので再描画
                update(getGraphics());
                //小休止
                sleep();
            }
            x += vecX;
            y += vecY;
        }
    }


    //オセロ盤を1手手前の状態にもどす
    public void undoBoard(Undo undo){
        int c = 0;

        //Point[c]がnullじゃない間
        while(undo.pos[c] != null){
            //ひっくり返した位置を取得
            int x = undo.pos[c].x;
            int y = undo.pos[c].y;
            //元に戻すには-1をかければよい            
            //黒(1)は白(-1)に白は黒になる
            board[y][x] *= -1;
            c++;
        }
        //石を打つ前に戻す
        board[undo.y][undo.x] = BLANK;
        //手番も元に戻す
        nextTurn();
    }
    
    //手番を変える
    public void nextTurn(){
        //手番を変える
        //白の番に逆の値を入れる
        flagForWhite = !flagForWhite;
    }

    //石が打てる場所の数を数える
    public int countCanPutDownStone(){
        int count = 0;

        //MASUの範囲内なら
        for(int y = 0; y < MainPanel.MASU; y++){
            //MASUの範囲内なら
            for(int x = 0; x < MainPanel.MASU; x++){
                //もし置けるなら
                if(canPutDown(x, y)){
                    //カウントを足します
                    count++;
                }
            }
        }
        //カウントを返す
        return count;
    }

    //SLEEP_TIMEだけ休止
    private void sleep(){
        //例外処理
        try{
            //500ミリ秒休止
            Thread.sleep(SLEEP_TIME);
        //割り込みが発生した場合の例外
        }catch(InterruptedException e){
            //スタックトレースを出力します
            e.printStackTrace();
        }
    }


    //画面中央に文字列を出力します
    private void drawTextCentering(Graphics g, String s){
        //
        Font f = new Font("SansSerif", Font.BOLD, 20);
        g.setFont(f);
        FontMetrics fm = g.getFontMetrics();
        g.setColor(Color.YELLOW);
        g.drawString(s,WIDTH / 2 - fm.stringWidth(s) / 2, HEIGHT / 2 + fm.getDescent());
    }

    //ゲームが終了したか調べる
    public boolean endGame(){
        //もし打たれた石が60なら
        if(putNumber == END_NUMBER){
            //白黒両方の石を数える
            Counter counter;
            counter = countStone();
            //黒が過半数32を取っていたら勝ち
            //過半数以下なら負け
            //同じ数なら引き分け
            if(counter.blackCount > 32){
                gameState = YOU_WIN;
            }else if(counter.blackCount < 32){
                gameState = YOU_LOSE;
            }else{
                gameState = DRAW;
            }
            //再描画
            repaint();
            //呼び出し元にtrueを返す
            return true;
        }
        return false;
    }

    //盤上の石の数を数える
    public Counter countStone(){
        Counter counter = new Counter();

        for(int y = 0; y < MASU; y++){
            for(int x  = 0; x < MASU; x++){
                //もしボードの座標(x,y)が黒石なら黒のカウント1つ足す 
                if(board[y][x] == BLACK_STONE)
                    counter.blackCount++;
                if(board[y][x] == WHITE_STONE)
                    counter.whiteCount++;
            }
        }
        return counter;
    }


    //(x,y)のボードの石の種類を返す
    public int getBoard(int x, int y){
        return board[y][x];
    }

    //マウスを押す
    public void mousePressed(MouseEvent e){
    }
    //マウスが入る
    public void mouseEntered(MouseEvent e){
    }
    //マウスが出る
    public void mouseExited(MouseEvent e){
    }
    //マウスを離す
    public void mouseReleased(MouseEvent e){
    }
}

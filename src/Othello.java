import java.awt.*;
import javax.swing.*;



//OthelloクラスにJFrameクラスを継承
public class Othello extends JFrame{
    public Othello(){
        //タイトル
        setTitle("othllo");
        //サイズ変更
        setResizable(false);
        //Container型変数　contentPaneを宣言
        Container contentPane = getContentPane();

        //情報パネル作成
        InfoPanel infoPanel = new InfoPanel();
        contentPane.add(infoPanel, BorderLayout.NORTH);

        //メインパネル作成してフレームに追加
        MainPanel mainPanel = new MainPanel(infoPanel);
        contentPane.add(mainPanel, BorderLayout.CENTER);
        //パネルサイズに合わせてフレームサイズを自動設定
        pack();
    }

    public static void main(String[] args){
        Othello frame = new Othello();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
            
    }
}
import javax.swing.*;



public class InfoPanel extends JPanel {
    //swingにおいてラベル作成するクラス
    private JLabel blackLabel;
    private JLabel whiteLabel;

    public InfoPanel(){
        add(new JLabel("BLACK:"));
        blackLabel = new JLabel("0");
        add(blackLabel);
        add(new JLabel("WHITE:"));
        whiteLabel = new JLabel("0");
        add(whiteLabel);
    }



    //BLACKラベルに値をセットする
    public void setBlackLabel(int count){
        blackLabel.setText(count + "");
    }

    //WHITEラベルに値をセットする
    public void setWhiteLabel(int count){
        whiteLabel.setText(count + "");
    }
    
}

public class AI {
    //
    private static final int SEARCH_LEVEL = 5;
    //
    private MainPanel panel;


    public AI(MainPanel panel){
        this.panel = panel;
    }
    
    public void compute(){
        //
        //
        int temp = minMax(true, SEARCH_LEVEL);

        //
        int x = temp % MainPanel.MASU;
        int y = temp / MainPanel.MASU;

        //
        Undo undo = new Undo(x, y);
        //
        panel.putDownStone(x, y, false);
        //
        panel.reverse(undo, false);
        //
        panel.nextTurn();
    }



    //
    private int minMax(boolean flag, int level){
        //
        int value;
        //
        int childValue;
        //
        int bestX = 0;
        int bestY = 0;

        //
        //
        if(level == 0){
            return valueBoard();   //
        }

        if(flag){
            //
            value = Integer.MIN_VALUE;
        }else{
            //
            value = Integer.MAX_VALUE;
        }
        
        //
        for(int y = 0; y < MainPanel.MASU; y++){
            for(int x = 0; x < MainPanel.MASU; x++){
                if(panel.canPutDown(x, y)){
                    Undo undo  = new Undo(x, y);
                    //
                    panel.putDownStone(x, y, true);
                    //
                    panel.reverse(undo, true);
                    //
                    panel.nextTurn();
                    //
                    //
                    childValue = minMax(!flag, level - 1);
                    //
                    if(flag){
                        //
                        if(childValue > value){
                            value = childValue;
                            bestX = x;
                            bestY = y;
                        }
                    }else{
                        //
                        if(childValue < value){
                            value = childValue;
                            bestX = x;
                            bestY = y;
                        }
                    }
                    //
                    panel.undoBoard(undo);
                }
            }
        }

    
        if(level == SEARCH_LEVEL){
            //
            return bestX + bestY * MainPanel.MASU;
        }else{
            //
            return value;
        }
    }

        //
        private int valueBoard() {
            MainPanel.Counter counter = panel.countStone();
            // 白石の数が評価値になる
            // 白石が多い盤面の方が評価が高いとする
            return counter.whiteCount;
        }    








}

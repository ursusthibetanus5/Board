public class AI {
    //
    private static final int SEARCH_LEVEL = 7;
    //
    private MainPanel panel;
    //
    private static final int valueOfPlace[][] = {
        {120, -20, 20,  5,  5, 20, -20, 120},
        {-20, -40, -5, -5, -5, -5, -40, -20},
        { 20,  -5, 15,  3,  3, 15,  -5,  20},
        {  5,  -5,  3,  3,  3,  3,  -5,   5},
        {  5,  -5,  3,  3,  3,  3,  -5,   5},
        { 20,  -5, 15,  3,  3, 15,  -5,  20},
        {-20, -40, -5, -5, -5, -5, -40, -20},
        {120, -20, 20,  5,  5, 20, -20, 120}
    };

    public AI(MainPanel panel){
        this.panel = panel;
    }
    
    public void compute(){
        //
        //
        int temp = alphaBeta(true, SEARCH_LEVEL, Integer.MIN_VALUE, Integer.MAX_VALUE);

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
        if(panel.endGame()) return;
        //
        panel.nextTurn();
        //
        if(panel.countCanPutDownStone() == 0){
            System.out.println("PlayerPASS!");
            panel.nextTurn();
            compute();
        }
    }


    //
    private int alphaBeta(boolean flag, int level, int alpha, int beta){
        //
        int value;
        //
        int childValue;
        //
        int bestX = 0;
        int bestY = 0;

        //
        if(level == 0){
            return valueBoard();
        }
        if(flag){
            //
            value = Integer.MIN_VALUE;
        }else{
            //
            value = Integer.MAX_VALUE;
        }

        //
        if(panel.countCanPutDownStone() == 0){
            return valueBoard();
        }

        //
        for(int y = 0; y < MainPanel.MASU; y++){
            for(int x = 0; x < MainPanel.MASU; x++){
                if(panel.canPutDown(x, y)){
                    Undo undo = new Undo(x, y);
                    //
                    panel.putDownStone(x, y, true);
                    //
                    panel.reverse(undo, true);
                    //
                    panel.nextTurn();
                    //
                    //
                    childValue = alphaBeta(!flag, level - 1, alpha, beta);
                    //
                    if(flag){
                        //
                        if(childValue > value){
                            value = childValue;
                            //
                            alpha = value;
                            bestX = x;
                            bestY = y;
                        }
                        //
                        //
                        //
                        if(value > beta){
                            panel.undoBoard(undo);
                            return value;
                        }
                    }else{
                        //
                        if(childValue < value){
                            value = childValue;
                            //
                            beta = value;
                            bestX = x;
                            bestY = y;
                        }
                        //
                        //
                        //
                        if(value < alpha){
                            panel.undoBoard(undo);
                            return value;
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
    private int valueBoard(){
        int value = 0;

        for (int y = 0; y < MainPanel.MASU; y++){
            for(int x = 0; x < MainPanel.MASU; x++){
                //
                value += panel.getBoard(x, y) * valueOfPlace[y][x];
            }
        }

        //
        return - value;
    }    
}

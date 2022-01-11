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
            return 0;   //
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








}

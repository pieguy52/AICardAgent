package CardPickup;

import java.util.ArrayList;

public class initTestAgent extends Player {

    protected final String newName = "initTestAgent";
    HandEvaluator hE;

    public initTestAgent() {
        super();
        playerName = newName;
    }

    @Override
    public void initialize() {
        hE = new HandEvaluator();
    }

    protected void opponentAction(int opponentNode, boolean opponentPickedUp, Card c){
        oppNode = opponentNode;
        if(opponentPickedUp) {
            oppLastCard = c;
            nodes[oppNode].clearCard();
        }else
            oppLastCard = null;
    }

    @Override
    public Action makeAction() {
        if(hand.size()==5)
            return new Action();
        int neighbor = 0;
        ArrayList<Node> neighbors = nodes[currentNode].getNeighborList();
        if (neighbors.size()==1)
            neighbor = neighbors.get(0).getNodeID();
        else if(!neighbors.isEmpty()) {
            for(Node list : neighbors){
                Card bestCard = new Card();
                for(Card c: list.getPossibleCards())
                    if(hE.rankHand(c) > hE.rankHand(bestCard)) {
                        neighbor = list.getNodeID();
                        bestCard = c;
                    }
            }
        }
        if(!nodes[neighbor].getPossibleCards().isEmpty())
            return new Action(ActionType.PICKUP, neighbor);
        else
            return new Action(ActionType.MOVE, neighbor);
    }

    protected void actionResult(int currentNode, Card c){
        this.currentNode = currentNode;
        if(c!=null) {
            addCardToHand(c);
            nodes[currentNode].clearPossibleCards();
        }
    }
}

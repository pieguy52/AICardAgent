package CardPickup;

import java.util.ArrayList;
import java.util.Random;

public class Jotaro extends Player {

    protected final String newName = "Jotaro";
    HandEvaluator hE;
    Random rand;

    public Jotaro() {
        super();
        playerName = newName;
    }

    @Override
    public void initialize() {
        hE = new HandEvaluator();
        rand = new Random();
    }


    @Override
    public Action makeAction() {
        if(hand.getNumHole() == 5)
            return new Action();//test for burning
        int dest = nodes[currentNode].getNodeID();
        ArrayList<Node> neighbors = nodes[currentNode].getNeighborList();
        Hand temp = new Hand();//temp hand to hold onto cards for hand eval
        for(int i = 0; i < hand.size(); i++)
            temp.addHoleCard(hand.getHoleCard(i));
        if(neighbors.size() == 1)
            dest = neighbors.get(0).getNodeID();
        else if(!neighbors.isEmpty()){
            ArrayList<Float> avgVals = new ArrayList<>();
            float holder;
            for(int i = 0; i < neighbors.size(); i++){
                avgVals.add(i, 0f);
                holder = 0;
                for(Card c: neighbors.get(i).getPossibleCards()){
                    temp.addHoleCard(c);
                    holder += hE.rankHand(temp) + avgVals.get(i);
                    temp.remove(c);
                }
                holder /= neighbors.size();
                avgVals.add(i, holder);
            }
            holder = 0;
            for(int i = 0; i < avgVals.size(); i++)
                if(avgVals.get(i) > holder)
                    dest = neighbors.get(i).getNodeID();
        }
        if(!nodes[dest].getPossibleCards().isEmpty()) {
            return new Action(ActionType.PICKUP, dest);
        }else {
            int bestNeighbor = 0;
            int numNeighbors = 0;
            for(Node neighbor: neighbors){
                if(neighbor.getNeighborAmount() > numNeighbors){
                       numNeighbors = neighbor.getNeighborAmount();
                       bestNeighbor = neighbor.getNodeID();
                }
            }
            dest = bestNeighbor;
            return new Action(ActionType.MOVE, dest);
        }
    }

    @Override
    protected void actionResult(int currentNode, Card c){
        this.currentNode = currentNode;
        if(c!=null) {
            addCardToHand(c);
            nodes[currentNode].clearPossibleCards();
        }
    }

    @Override
    protected void opponentAction(int opponentNode, boolean opponentPickedUp, Card c){
        oppNode = opponentNode;
        if(opponentPickedUp) {
            oppLastCard = c;
            nodes[opponentNode].clearPossibleCards();
        }
        else
            oppLastCard = null;
    }
}

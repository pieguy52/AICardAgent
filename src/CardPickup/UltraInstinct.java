package CardPickup;

import java.util.ArrayList;
import java.util.Random;

public class UltraInstinct extends Player {

    protected final String newName = "UltraInstinct";
    HandEvaluator hE;
    int numNodes = 0;
    int uncertainty;
    Random rand;

    public UltraInstinct() {
        super();
        playerName = newName;
    }

    @Override
    public void initialize() {
        hE = new HandEvaluator();
        numNodes = nodes.length;
        uncertainty = nodes[0].getPossibleCards().size();
        rand = new Random();
    }

    @Override
    public Action makeAction() {
        if(uncertainty == 1)
            return Agent3Action();
        else
            return aStarAction();
    }

    public Action Agent3Action() {
        if(hand.size()==5)
            return new Action();//end
        Card max_card = hand.getHoleCard(0);
        for(int i = 1; i < hand.size(); i++) {
            if(max_card.compareTo(hand.getHoleCard(i)) < 0)
                max_card = hand.getHoleCard(i);
        }
        int[] sums = new int[nodes[currentNode].getNeighborAmount()];
        for(int i = 0; i < sums.length; i++){
            ArrayList<Card> possible = nodes[currentNode].getNeighbor(i).getPossibleCards();
            if(possible != null){
                for(int j = 0; j < possible.size(); j++) {
                    if(max_card.getSuit() == possible.get(j).getSuit() &&
                            ((max_card.getRank() == (possible.get(j).getRank()+1) ||
                                    max_card.getRank() == (possible.get(j).getRank()-1)))) {
                        return new Action(ActionType.PICKUP, nodes[currentNode].getNeighbor(i).getNodeID());}
                    else if(max_card.getRank() == possible.get(j).getRank()) {
                        return new Action(ActionType.PICKUP, nodes[currentNode].getNeighbor(i).getNodeID());}
                    else if(max_card.getSuit() == possible.get(j).getSuit()){
                        return new Action(ActionType.PICKUP, nodes[currentNode].getNeighbor(i).getNodeID());}
                    else if(max_card.getSuit() != possible.get(j).getSuit() &&
                            ((max_card.getRank() == (possible.get(j).getRank()+1) ||
                                    max_card.getRank() == (possible.get(j).getRank()-1)))){
                        return new Action(ActionType.PICKUP, nodes[currentNode].getNeighbor(i).getNodeID());
                    }
                    else {
                        sums[i] += possible.get(j).getRank();

                    }
                }
            }
        }
        int maxIndex = 0;
        for(int k = 1; k < sums.length; k++)
            if(sums[maxIndex] < sums[k])
                maxIndex = k;
        int neighbor = nodes[currentNode].getNeighbor(maxIndex).getNodeID();
        return new Action(ActionType.PICKUP, neighbor);
    }


    public Action aStarAction() {
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
    protected void opponentAction(int opponentNode, boolean opponentPickedUp, Card c) {
        oppNode = opponentNode;
        if (opponentPickedUp) {
            oppLastCard = c;
            nodes[opponentNode].clearPossibleCards();
        } else
            oppLastCard = null;
    }

}

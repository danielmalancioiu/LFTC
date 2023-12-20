
import java.util.*;

public class CanonicalCollection {

    private List<State> states;
    private Map<Pair<Integer, String>, Integer> adjacencyList;

    public CanonicalCollection(){
        this.states = new ArrayList<>();
        this.adjacencyList = new LinkedHashMap<>();
    }

    public CanonicalCollection(List<State> states, Map<Pair<Integer, String>, Integer> adjacencyList) {
        this.states = states;
        this.adjacencyList = adjacencyList;
    }


    public void connectStates(Integer indexFirstState, String symbol, Integer indexSecondState){
        this.adjacencyList.put(new Pair<>(indexFirstState, symbol), indexSecondState);
    }

    public void addState(State state){
        this.states.add(states.size(), state);
    }

    public List<State> getStates(){
        return this.states;
    }

    public Map<Pair<Integer, String>, Integer> getAdjacencyList(){
        return this.adjacencyList;
    }

}

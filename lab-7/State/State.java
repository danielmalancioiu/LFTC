import java.util.*;

public class State {

    private StateActionType stateActionType;
    private final Set<Item> items;

    public State(Set<Item> states){
        this.items = states;
        this.setActionForState();
    }

    public Set<Item> getItems(){
        return items;
    }

    public StateActionType getStateActionType(){
        return this.stateActionType;
    }

    public List<String> getSymbolsSucceedingTheDot(){
        Set<String> symbols = new LinkedHashSet<>();

        for(Item i: items){
            if(i.getPositionForDot() < i.getRightHandSide().size())
                symbols.add(i.getRightHandSide().get(i.getPositionForDot()));
        }

        return new ArrayList<>(symbols);
    }

    public void setActionForState(){
        if(items.size() == 1 && ((Item)items.toArray()[0]).getRightHandSide().size() == ((Item)items.toArray()[0]).getPositionForDot() && ((Item)this.items.toArray()[0]).getLeftHandSide() == Grammar.enrichedStartingGrammarSymbol){
            this.stateActionType = StateActionType.ACCEPT;
        } else if(items.size() == 1 && ((Item) items.toArray()[0]).getRightHandSide().size() == ((Item) items.toArray()[0]).getPositionForDot())
        {
            this.stateActionType = StateActionType.REDUCE;
        } else if(items.size() >= 1 && this.items.stream().allMatch(i -> i.getRightHandSide().size() > i.getPositionForDot())){
            this.stateActionType = StateActionType.SHIFT;
        } else if(items.size() > 1 && this.items.stream().allMatch(i -> i.getRightHandSide().size() == i.getPositionForDot())){
            this.stateActionType = StateActionType.REDUCE_REDUCE_CONFLICT;
        } else {
            this.stateActionType = StateActionType.SHIFT_REDUCE_CONFLICT;
        }
    }

    @Override
    public int hashCode(){
        return Objects.hash(items);
    }

    @Override
    public boolean equals(Object item){
        if(item instanceof  State){
            return ((State) item).getItems().equals(this.getItems());
        }

        return false;
    }

    @Override
    public String toString(){
        return stateActionType + " - " + items;
    }

}

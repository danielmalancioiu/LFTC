import java.util.*;

public class State {

	private final Set<Item> items;


	public State(Set<Item> states){
		this.items = states;
	}

	public Set<Item> getItems(){
		return items;
	}

	@Override
	public String toString(){
		return items.toString();
	}

	public List<String> getSymbolsSucceedingTheDot(){
		Set<String> symbols = new LinkedHashSet<>();

		for(Item i: items){
			if(i.getPositionForDot() < i.getRightHandSide().size())
				symbols.add(i.getRightHandSide().get(i.getPositionForDot()));
		}

		return new ArrayList<>(symbols);
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

}
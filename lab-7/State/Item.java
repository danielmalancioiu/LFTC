import java.util.List;
import java.util.Objects;


public class Item {

    private String leftHandSide;
    private List<String> rightHandSide;
    private Integer positionForDot;

    public String getLeftHandSide(){
        return this.leftHandSide;
    }

    public Item(String leftHandSide, List<String> rightHandSide, Integer positionForDot){
        this.leftHandSide = leftHandSide;
        this.rightHandSide = rightHandSide;
        this.positionForDot = positionForDot;
    }
    public List<String> getRightHandSide(){
        return this.rightHandSide;
    }

    public Integer getPositionForDot(){
        return this.positionForDot;
    }

    public boolean dotIsLast(){
        return this.positionForDot == this.rightHandSide.size();
    }

    @Override
    public String toString(){
        List<String> rightHandSide1 = this.rightHandSide.subList(0, positionForDot);

        String stringRightHandSide1 = String.join("", rightHandSide1);

        List<String> rightHandSide2 = this.rightHandSide.subList(positionForDot, this.rightHandSide.size());

        String stringLeftHandSide2 = String.join("", rightHandSide2);

        return leftHandSide.toString() + "->" + stringRightHandSide1 + "." + stringLeftHandSide2;
    }

    @Override
    public int hashCode(){
        return Objects.hash(leftHandSide, rightHandSide, positionForDot);
    }

    @Override
    public boolean equals(Object item) {
        return item instanceof Item && Objects.equals(((Item)item).leftHandSide, this.leftHandSide) && ((Item)item).rightHandSide == rightHandSide && Objects.equals(((Item)item).positionForDot, this.positionForDot);
    }

}

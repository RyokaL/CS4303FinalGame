package helpers;

public class EnemyMove {

	public int currEquipIndex;
	public Pair posToAttack;
	public Pair posToMove;
	
	public EnemyMove(int currEquipIndex, Pair posToAttack, Pair posToMove) {
		this.currEquipIndex = currEquipIndex;
		this.posToAttack = posToAttack;
		this.posToMove = posToMove;
	}
}

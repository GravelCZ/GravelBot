package cz.GravelCZ.Bot.Utils;

public class Pair<L, R> {

	public R right;
	public L left;
	
	public Pair(L left, R right) {
		this.right = right;
		this.left = left;
	}
	
	public static <L, R> Pair<L, R> create(L left, R right) {
		return new Pair<L, R>(left, right);
		
	}

	public static <L, R> Pair<L, R> of(L left, R right) {
		return new Pair<L, R>(left, right);
		
	}

	public R getValue() {
		return right;
	}

	public L getKey() {
		return left;
	}
	
}

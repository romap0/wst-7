package wst.client;


public class Pair<L, R> {
    private final L left;

    public L getLeft() {
        return left;
    }

    public R getRight() {
        return right;
    }

    private final R right;

    Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }
}
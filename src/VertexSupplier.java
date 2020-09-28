import java.util.function.Supplier;

/**
 * basic class of vertex supplier in order to create selfmade graphs (key increases and starts with 1)
 */
class VertexSupplier implements Supplier {
    Integer key;
    VertexSupplier(){
        key = 1;
    }

    @Override
    public Object get() {
        return key++;
    }
}
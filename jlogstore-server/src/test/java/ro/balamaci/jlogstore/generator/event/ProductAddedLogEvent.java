package ro.balamaci.jlogstore.generator.event;

public class ProductAddedLogEvent extends UserLogEvent {

    private Integer productId;

    public ProductAddedLogEvent() {
    }

    public ProductAddedLogEvent(String username, Integer productId) {
        super(String.format("ProductId=%d added to Cart by user=%s", productId, username), username);
        this.productId = productId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }
}

public class FairBillingException extends RuntimeException {

    public FairBillingException(String message, Exception e) {
        super(message, e);
    }
}
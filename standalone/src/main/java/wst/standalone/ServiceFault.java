package wst.standalone;

public class ServiceFault {
    private static final String DEFAULT_MESSAGE = "Something went wrong. Please, try again.";
    protected String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static ServiceFault defaultInstance() {
        ServiceFault fault = new ServiceFault();
        fault.message = DEFAULT_MESSAGE;
        return fault;
    }
}
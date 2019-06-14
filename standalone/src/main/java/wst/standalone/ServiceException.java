package wst.standalone;

import javax.xml.ws.WebFault;

@WebFault(faultBean = "wst.standalone.ServiceFault")
public class ServiceException extends Exception {
    private static final long serialVersionUID = -6647544772732631047L;
    private final ServiceFault fault;

    public ServiceException(String message) {
        super(message);
        this.fault = ServiceFault.defaultInstance();
    }

    public ServiceException(String message, ServiceFault fault) {
        super(message);
        this.fault = fault;
    }

    public ServiceException(String message, ServiceFault fault, Throwable cause) {
        super(message, cause);
        this.fault = fault;
    }

    public ServiceFault getFaultInfo() {
        return fault;
    }
}


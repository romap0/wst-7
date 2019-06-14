package wst.client;

import wst.generated.Shop;
import wst.generated.ShopService;
import wst.generated.ShopWebService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.ws.BindingProvider;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.api_v3.AccessPointType;
import org.apache.juddi.v3.client.transport.TransportException;
import org.uddi.api_v3.*;

public class WebServiceClient {
    private static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    private static ShopService shopService;
    private static ShopWebService service;
    private static JuddiClient juddiClient;

    public static void main(String[] args) throws Exception {
        shopService = new ShopService(new URL("http://localhost:8080/app/ShopService?wsdl"));
        service = shopService.getShopWebServicePort();

        System.out.println("Enter JUDDI username");
        String username = in.readLine().trim();
        System.out.println("Enter JUDDI user password");
        String password = in.readLine().trim();

        juddiClient = new JuddiClient("META-INF/uddi.xml");
        juddiClient.authenticate(username, password);

        while (true) {
            System.out.println("Enter command (1 - find/2 - update/3 - delete/4 - insert/5 - businesses):");

            int command = 0;
            try {
                command = Integer.parseInt(in.readLine());
            } catch (IOException e) {
                e.printStackTrace();
            }

            switch (command) {
                case 1:
                    read();
                    break;
                case 2:
                    update();
                    break;
                case 3:
                    delete();
                    break;
                case 4:
                    create();
                    break;
                case 5:
                    command = 0;
                    boolean br = false;
                    while (!br) {
                        switch (command) {
                            case 0:
                                System.out.println("1. List all businesses");
                                System.out.println("2. Register business");
                                System.out.println("3. Register servcice");
                                System.out.println("4. Find and use service");
                                System.out.println("5. Cancel");

                                command = Integer.parseInt(in.readLine());
                                break;
                            case 1:
                                listBusinesses(null);
                                command=0;
                                break;
                            case 2:
                                System.out.println("Введите имя бизнеса");
                                String bnn = readString(in);
                                if (bnn != null) {
                                    createBusiness(bnn);
                                }
                                command = 0;
                                break;
                            case 3:
                                listBusinesses(null);

                                String bbk;
                                do {
                                    System.out.println("Введите ключ бизнеса");
                                    bbk = readString(in);
                                } while (bbk == null);

                                String ssn;
                                do {
                                    System.out.println("Введите имя сервиса");
                                    ssn = readString(in);
                                } while (ssn == null);

                                String ssurl;
                                do {
                                    System.out.println("Введите ссылку на wsdl");
                                    ssurl = readString(in);
                                } while (ssurl == null);
                                
                                createService(bbk, ssn, ssurl);
                                command = 0;
                                break;
                            case 4:
                                System.out.println("Введите имя сервиса для поиска");
                                String ffsn = readString(in);
                                filterServices(ffsn);
                                System.out.println("Введите ключ сервиса");
                                
                                String kkey = readString(in);
                                if (kkey != null) {
                                    useService(kkey);
                                }

                                command = 0;
                                br=true;
                                break;
                            case 5:
                                return;
                            default:
                                command = 0;
                                break;
                        }
                    }
                    break;
                case 6:
                    break;
                default:
                    break;
            }
        }
    }

    public static void read() {
        String name = getColumn("Name: ");
        String city = getColumn("City: ");
        String address = getColumn("Address: ");
        Boolean isActive = getBooaleanColumn("Active (y/n): ");
        String type = getColumn("Type: ");

        List<Shop> shops = shopService.getShopWebServicePort().read(
                name, city, address, isActive, type);

        if (shops.isEmpty()) {
            System.out.println("Did not found any shops");
        } else {
            for (Shop shop : shops) {
                System.out.println(
                        "Shop{" +
                                "id='" +shop.getId()+'\''+
                                ", name='" + shop.getName() + '\'' +
                                ", isActive=" + shop.isIsActive() +
                                ", city='" + shop.getCity() + '\'' +
                                ", address='" + shop.getAddress() + '\'' +
                                ", type='" + shop.getType() + '\'' +
                                '}');
            }

            System.out.println("Total shops: " + shops.size());
        }
    }

    public static void update() {
        int id = getIntColumn("ID: ");
        String name = getColumn("Name: ");
        String city = getColumn("City: ");
        String address = getColumn("Address: ");
        Boolean isActive = getBooaleanColumn("Active (y/n): ");
        String type = getColumn("Type: ");

        try {
            int status = shopService.getShopWebServicePort().update(id, name, city, address, isActive, type);
            System.out.println(status);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void delete() {
        int id = getIntColumn("ID: ");

        try {
            int status = shopService.getShopWebServicePort().delete(id);
            System.out.println(status);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void create() {
        String name = getColumn("Name: ");
        String city = getColumn("City: ");
        String address = getColumn("Address: ");
        Boolean isActive = getBooaleanColumn("Active (y/n): ");
        String type = getColumn("Type: ");

        try {
            long id = shopService.getShopWebServicePort().create(name, city, address, isActive, type);
            System.out.println(id);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void useService(String serviceKey) throws RemoteException {
        ServiceDetail serviceDetail = juddiClient.getService(serviceKey.trim());
        if (serviceDetail == null || serviceDetail.getBusinessService() == null || serviceDetail.getBusinessService().isEmpty()) {
            System.out.printf("Can not find service by key '%s'\b", serviceKey);
            return;
        }
        List<BusinessService> services = serviceDetail.getBusinessService();
        BusinessService businessService = services.get(0);
        BindingTemplates bindingTemplates = businessService.getBindingTemplates();
        if (bindingTemplates == null || bindingTemplates.getBindingTemplate().isEmpty()) {
            System.out.printf("No binding template found for service '%s' '%s'\n", serviceKey, businessService.getBusinessKey());
            return;
        }
        for (BindingTemplate bindingTemplate : bindingTemplates.getBindingTemplate()) {
            AccessPoint accessPoint = bindingTemplate.getAccessPoint();
            if (accessPoint.getUseType().equals(AccessPointType.END_POINT.toString())) {
                String value = accessPoint.getValue();
                System.out.printf("Use endpoint '%s'\n", value);
                changeEndpointUrl(value);
                return;
            }
        }
        System.out.printf("No endpoint found for service '%s'\n", serviceKey);
    }

    private static void createService(String businessKey, String serviceName, String wsdlUrl) throws Exception {
        List<ServiceDetail> serviceDetails = juddiClient.publishUrl(businessKey.trim(), serviceName.trim(), wsdlUrl.trim());
        System.out.printf("Services published from wsdl %s\n", wsdlUrl);
        JuddiUtil.printServicesInfo(serviceDetails.stream()
                .map(ServiceDetail::getBusinessService)
                .flatMap(List::stream)
                .collect(Collectors.toList())
        );
    }

    public static void createBusiness(String businessName) throws RemoteException {
        businessName = businessName.trim();
        BusinessDetail business = juddiClient.createBusiness(businessName);
        System.out.println("New business was created");
        for (BusinessEntity businessEntity : business.getBusinessEntity()) {
            System.out.printf("Key: '%s'\n", businessEntity.getBusinessKey());
            System.out.printf("Name: '%s'\n", businessEntity.getName().stream().map(Name::getValue).collect(Collectors.joining(" ")));
        }
    }

    public static void changeEndpointUrl(String endpointUrl) {
        ((BindingProvider) service).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointUrl.trim());
    }

    private static void filterServices(String filterArg) throws RemoteException {
        List<BusinessService> services = juddiClient.getServices(filterArg);
        JuddiUtil.printServicesInfo(services);
    }

    private static void listBusinesses(Void ignored) throws RemoteException {
        JuddiUtil.printBusinessInfo(juddiClient.getBusinessList().getBusinessInfos());
    }

    private static String readString(BufferedReader reader) throws IOException {
        String trim = reader.readLine().trim();
        if (trim.isEmpty()) {
            return null;
        }
        return trim;
    }

    public static String checkNull (String s) {
        return s.length() == 0 ? null : s;
    }

    public static Boolean checkBoolean(String s) {
        if (s.length() == 0) return null;

        return s.equals("y") ? Boolean.TRUE : Boolean.FALSE;
    }

    public static String getColumn(String msg) {
        System.out.print(msg);
        try {
            return checkNull(in.readLine());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Boolean getBooaleanColumn(String msg) {
        System.out.print(msg);
        try {
            return checkBoolean(in.readLine());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int getIntColumn(String msg) {
        System.out.print(msg);
        try {
            return Integer.parseInt(in.readLine());
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }
};
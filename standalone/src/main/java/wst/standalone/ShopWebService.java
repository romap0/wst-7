package wst.standalone;

import wst.dao.ShopDAO;
import wst.entity.Shop;

import javax.inject.Inject;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.sql.SQLException;
import java.util.List;

@WebService(serviceName = "ShopService", targetNamespace = "http://wst.com")
public class ShopWebService {
    @Inject
    private ShopDAO shopDAO;

    @WebMethod(operationName = "findAll")
    public List<Shop> findAll() {
        try {
            return shopDAO.findAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @WebMethod(operationName = "read")
    public List<Shop> filter(
        @WebParam(name = "name") String name,
        @WebParam(name = "city") String city,
        @WebParam(name = "address") String address,
        @WebParam(name = "isActive") Boolean isActive,
        @WebParam(name = "type") String type
        ) {
        
        System.out.println("name: " + name + " city: " + city + " address: " + address + " isActive: " + isActive + " type: " + type);

        try {
            return shopDAO.read(name, city, address, isActive, type);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return  null;
    }

    @WebMethod(operationName = "create")
    public long create(
        @WebParam(name = "name") String name,
        @WebParam(name = "city") String city,
        @WebParam(name = "address") String address,
        @WebParam(name = "isActive") Boolean isActive,
        @WebParam(name = "type") String type
    ) throws ServiceException {
        if (name == null || !name.matches("[a-zA-Zа-яА-Я]+")) {
            throw new ServiceException("Name should contain only letters.");
        }

        if (isActive == null) {
            throw new ServiceException("isActive should not be null.");
        }

        return shopDAO.create(name, city, address, isActive, type);
    }

    @WebMethod(operationName = "delete")
    public int delete(@WebParam(name = "id") int id
    ) throws ServiceException {
        if (shopDAO.findById(id) == null) {
            throw new ServiceException("Not found.");
        }

        return shopDAO.delete(id);
    }

    @WebMethod(operationName = "update")
    public int update(
        @WebParam(name = "id") int id,
        @WebParam(name = "name") String name,
        @WebParam(name = "city") String city,
        @WebParam(name = "address") String address,
        @WebParam(name = "isActive") Boolean isActive,
        @WebParam(name = "type") String type
    ) throws ServiceException {
        if (shopDAO.findById(id) == null) {
            throw new ServiceException("Not found.");
        }
        
        if (name == null || !name.matches("[a-zA-Zа-яА-Я]+")) {
            throw new ServiceException("Name should contain only letters.");
        }

        if (isActive == null) {
            throw new ServiceException("isActive should not be null.");
        }

        return shopDAO.update(id, name, city, address, isActive, type);
    }

    public ShopWebService(ShopDAO ShopDAO) {
        this.shopDAO = ShopDAO;
    }

    public ShopWebService() {
        this.shopDAO = new ShopDAO();
    }
}

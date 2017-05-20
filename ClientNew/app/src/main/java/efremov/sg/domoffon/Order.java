package efremov.sg.domoffon;

/**
 * Created by Aleksey on 20.05.2017.
 */

public class Order {
    private int id;
    private String name;
    private String addr;
    private int count;
    private int price;
    private String status;

    public Order(int id, String name, String addr, int count, int price, String status) {
        this.id = id;
        this.name = name;
        this.addr = addr;
        this.count = count;
        this.price = price;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String des) {
        this.addr = des;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

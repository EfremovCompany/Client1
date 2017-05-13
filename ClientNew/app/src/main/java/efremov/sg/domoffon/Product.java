package efremov.sg.domoffon;

/**
 * Created by Aleksey on 01.05.2017.
 */

public class Product {
    private int id;
    private String name;
    private String des;
    private int count;
    private int price;
    private String src;

    public Product(int id, String name, String des, int count, int price, String src) {
        this.id = id;
        this.name = name;
        this.des = des;
        this.count = count;
        this.price = price;
        this.src = src;
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

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
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

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }
}

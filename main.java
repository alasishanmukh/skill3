import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import jakarta.persistence.*;

@Entity
@Table(name="products")

class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    private String name;
    private Double price;
    private Integer quantity;
    private String description;

    public Product(){}

    public Product(String name, Double price, Integer quantity, String description){
        this.name=name;
        this.price=price;
        this.quantity=quantity;
        this.description=description;
    }

    public Long getId(){ return id; }
    public String getName(){ return name; }
    public Double getPrice(){ return price; }
    public Integer getQuantity(){ return quantity; }
    public String getDescription(){ return description; }

    public String toString(){
        return name+" - $"+price+" - Qty:"+quantity+" - "+description;
    }
}

public class HQLDemo {

    public static void main(String[] args) {

        SessionFactory factory=new Configuration().configure().addAnnotatedClass(Product.class).buildSessionFactory();
        Session session=factory.openSession();

        try{

            Transaction tx=session.beginTransaction();

            session.save(new Product("Laptop",899.99,15,"Electronics"));
            session.save(new Product("Mouse",25.50,50,"Electronics"));
            session.save(new Product("Keyboard",45.00,30,"Electronics"));
            session.save(new Product("Monitor",299.99,20,"Electronics"));
            session.save(new Product("Desk Chair",150.00,0,"Furniture"));
            session.save(new Product("Desk Lamp",35.75,25,"Furniture"));
            session.save(new Product("Notebook",5.99,100,"Stationery"));
            session.save(new Product("Pen Set",12.50,75,"Stationery"));

            tx.commit();

            System.out.println("Sample products inserted");

            // Sort by price ascending
            Query<Product> q1=session.createQuery("FROM Product p ORDER BY p.price ASC",Product.class);
            List<Product> list1=q1.list();

            System.out.println("\nProducts Sorted by Price ASC");
            for(Product p:list1){
                System.out.println(p);
            }

            // Sort by quantity
            Query<Product> q2=session.createQuery("FROM Product p ORDER BY p.quantity DESC",Product.class);
            List<Product> list2=q2.list();

            System.out.println("\nProducts Sorted by Quantity");
            for(Product p:list2){
                System.out.println(p.getName()+" - "+p.getQuantity());
            }

            // Pagination
            Query<Product> q3=session.createQuery("FROM Product",Product.class);
            q3.setFirstResult(0);
            q3.setMaxResults(3);

            System.out.println("\nFirst 3 Products");
            for(Product p:q3.list()){
                System.out.println(p);
            }

            // Count products
            Query<Long> q4=session.createQuery("SELECT COUNT(p) FROM Product p",Long.class);
            System.out.println("\nTotal Products: "+q4.uniqueResult());

            // Min Max price
            Query<Object[]> q5=session.createQuery("SELECT MIN(p.price), MAX(p.price) FROM Product p",Object[].class);
            Object[] result=q5.uniqueResult();

            System.out.println("Minimum Price: "+result[0]);
            System.out.println("Maximum Price: "+result[1]);

            // Filter price range
            Query<Product> q6=session.createQuery("FROM Product p WHERE p.price BETWEEN :min AND :max",Product.class);
            q6.setParameter("min",20.0);
            q6.setParameter("max",100.0);

            System.out.println("\nProducts between $20 and $100");
            for(Product p:q6.list()){
                System.out.println(p.getName()+" - "+p.getPrice());
            }

        }
        finally{
            session.close();
            factory.close();
        }
    }
}
package lk.developersstack.lms.util;

import com.sun.nio.file.ExtendedWatchEventModifier;
import lk.developersstack.lms.entity.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import java.io.FileInputStream;
import java.util.Properties;

public class HibernateUtil {
    private static HibernateUtil hibernateUtil;
    private final static SessionFactory sessionFactory = buildsSessionFactory();
    private HibernateUtil(){}
    public static HibernateUtil getInstance(){
        return (hibernateUtil == null ? hibernateUtil = new HibernateUtil() : hibernateUtil);
    }

    private static SessionFactory buildsSessionFactory(){

        //============ Second one (With hibernate properties file) ============

        Properties properties = new Properties();
        try{
            properties.load(new FileInputStream("hibernate.properties"));
        }catch(Exception e){
            e.printStackTrace();
        }

        Configuration configuration = new Configuration();
        configuration.addProperties(properties);
        configuration.addAnnotatedClass(Student.class)
                .addAnnotatedClass(Program.class)
                .addAnnotatedClass(Laptop.class)
                .addAnnotatedClass(Book.class)
                .addAnnotatedClass(Registration.class);
        return configuration.buildSessionFactory();


        //======================= first one ============

        /*StandardServiceRegistry standardRegistry = new StandardServiceRegistryBuilder()
                .configure("hibernate.cfg.xml")
                .build();

        Metadata metadata = new MetadataSources(standardRegistry)
                .addAnnotatedClass(Student.class)
                .addAnnotatedClass(Program.class)
                .addAnnotatedClass(Laptop.class)
                .addAnnotatedClass(Book.class)
                .addAnnotatedClass(Registration.class)
                .getMetadataBuilder()
                .applyImplicitNamingStrategy(ImplicitNamingStrategyJpaCompliantImpl.INSTANCE)
                .build();

                return metadata.getSessionFactoryBuilder()
                .build();*/
    }

    public Session openSession(){
        return sessionFactory.openSession();
    }

}

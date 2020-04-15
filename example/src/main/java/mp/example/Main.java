package mp.example;

import mp.persistence.PersistenceManager;
import mp.persistence.ReflectivePersistenceManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:sqlite:test.db");
        PersistenceManager manager = new ReflectivePersistenceManager(conn);

        manager.createTables(Department.class, Person.class);
        Department development = new Department("Development", "DVLP");
        Department marketing = new Department("Marketing", "MARK");
        Person hrasko = new Person("Janko", "Hrasko", 30);
        hrasko.setDepartment(development);
        Person mrkvicka = new Person("Jozko", "Mrkvicka", 25);
        mrkvicka.setDepartment(development);
        Person novak = new Person("Jan", "Novak", 45);
        novak.setDepartment(marketing);

        Person novakNullCrash = new Person(null, "Novak", 45);
        novak.setDepartment(marketing);

        manager.save(hrasko);
        manager.save(mrkvicka);
        manager.save(novak);


        //nullFailed might failed
//        manager.save(novakNullCrash);

        Person proxyDummy = manager.get(Person.class, 1);
        DepartmentRepo dep = (DepartmentRepo) proxyDummy.getDepartment();
        System.out.println(dep);
        Person proxyDummyTwo = manager.get(Person.class, 2);
        DepartmentRepo depTwo = (DepartmentRepo) proxyDummyTwo.getDepartment();

        List<Person> persons = manager.getAll(Person.class);
        for (Person person : persons) {
            System.out.println(person);
        }
        Person proxyPerson = persons.get(0);
        proxyPerson.setSurname("PROXY");
        manager.save(proxyPerson);

        List<Person> personsBy = manager.getBy(Person.class, "MENO", "Janko");
        for (Person person : personsBy) {
            System.out.println(person);
            System.out.println("  " + person.getDepartment());
        }
        Thread.sleep(500);
        marketing.setName("AutoSaved name");
        conn.close();
    }
}



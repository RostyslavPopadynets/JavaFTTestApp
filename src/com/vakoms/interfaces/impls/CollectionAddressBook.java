package com.vakoms.interfaces.impls;

import com.vakoms.interfaces.AddressBook;
import com.vakoms.objects.Person;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CollectionAddressBook implements AddressBook {

    private ObservableList<Person> personList = FXCollections.observableArrayList();

    @Override
    public void add(Person person) {
        personList.add(person);
    }

    @Override
    public void delete(Person person) {
        personList.remove(person);
    }

    public ObservableList<Person> getPersonList() {
        return personList;
    }

    public void fillTestData(){
        personList.add(new Person("Иван Печкин", "23948723948"));
        personList.add(new Person("Роман Романов", "345345345"));
        personList.add(new Person("Антон Иванов", "345345345"));
        personList.add(new Person("Джон Маклейн", "23423423"));
        personList.add(new Person("Джек Воробей", "234234"));
        personList.add(new Person("Алиса Ивановна", "456456"));
        personList.add(new Person("Боб Марли", "34534345"));
    }

}
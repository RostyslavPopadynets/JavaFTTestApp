package com.vakoms.controllers;

import com.vakoms.interfaces.impls.CollectionAddressBook;
import com.vakoms.objects.Lang;
import com.vakoms.objects.Person;
import com.vakoms.start.Main;
import com.vakoms.utils.DialogManager;
import com.vakoms.utils.LocaleManager;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.CustomTextField;
import org.controlsfx.control.textfield.TextFields;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Observable;
import java.util.ResourceBundle;

public class MainController extends Observable implements Initializable {

    private static final String FXML_EDIT = "../fxml/edit.fxml";
    private CollectionAddressBook addressBookImpl = new CollectionAddressBook();

    @FXML
    private CustomTextField txtSearch;

    @FXML
    private TableView<Person> tableAddressBook;

    @FXML
    private TableColumn<Person, String> columnFIO;

    @FXML
    private TableColumn<Person, String> columnPhone;

    @FXML
    private Label labelCount;

    @FXML
    private ComboBox<Lang> comboLocales;

    private Parent fxmlEdit;

    private FXMLLoader fxmlLoader = new FXMLLoader();

    private EditDialogController editDialogController;

    private Stage editDialogStage;

    private ResourceBundle resourceBundle;

    private ObservableList<Person> backupList;

    private static final String RU_CODE = "ru";
    private static final String EN_CODE = "en";
    private static final String UA_CODE = "ua";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resourceBundle = resources;
        columnFIO.setCellValueFactory(new PropertyValueFactory<>("fio"));
        columnPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        setupClearButtonField(txtSearch);
        initListeners();
        fillData();
        initLoader();
    }

    private void setupClearButtonField(CustomTextField customTextField) {
        try {
            Method m = TextFields.class.getDeclaredMethod("setupClearButtonField", TextField.class, ObjectProperty.class);
            m.setAccessible(true);
            m.invoke(null, customTextField, customTextField.rightProperty());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fillData() {
        fillTable();
        fillLangComboBox();
    }

    private void fillTable() {
        addressBookImpl.fillTestData();
        backupList = FXCollections.observableArrayList();
        backupList.addAll(addressBookImpl.getPersonList());
        tableAddressBook.setItems(addressBookImpl.getPersonList());
    }

    private void fillLangComboBox() {
        Lang langRU = new Lang(0, RU_CODE, resourceBundle.getString("ru"), LocaleManager.RU_LOCALE);
        Lang langEN = new Lang(1, EN_CODE, resourceBundle.getString("en"), LocaleManager.EN_LOCALE);
        Lang langUA = new Lang(2, UA_CODE, resourceBundle.getString("ua"), LocaleManager.UA_LOCALE);

        comboLocales.getItems().add(langRU);
        comboLocales.getItems().add(langEN);
        comboLocales.getItems().add(langUA);

        if (LocaleManager.getCurrentLang() == null) {
            comboLocales.getSelectionModel().select(2);
        } else {
            comboLocales.getSelectionModel().select(LocaleManager.getCurrentLang().getIndex());
        }
    }

    private void initListeners() {

        // слухає зміни в колекції для обновлення надписі "Кількість записів"
        addressBookImpl.getPersonList().addListener((ListChangeListener<Person>) c -> updateCountLabel());

        // слухає подвійне натиснення для редагування запису
        tableAddressBook.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                editDialogController.setPerson(tableAddressBook.getSelectionModel().getSelectedItem());
                showDialog();
            }
        });

        // слухає зміну мови
        comboLocales.setOnAction(event -> {
            Lang selectedLang = comboLocales.getSelectionModel().getSelectedItem();
            LocaleManager.setCurrentLang(selectedLang);

            // повідомить всіх слухачів, що відбулася зміна мови
            setChanged();
            notifyObservers(selectedLang);
        });
    }

    private void initLoader() {
        try {
            fxmlLoader.setLocation(getClass().getResource(FXML_EDIT));
            fxmlLoader.setResources(ResourceBundle.getBundle(Main.BUNDLES_FOLDER, LocaleManager.getCurrentLang().getLocale()));
            fxmlEdit = fxmlLoader.load();
            editDialogController = fxmlLoader.getController();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateCountLabel() {
        labelCount.setText(resourceBundle.getString("count") + ": " + addressBookImpl.getPersonList().size());
    }

    public void actionButtonPressed(ActionEvent actionEvent) {

        Object source = actionEvent.getSource();

        // якщо нажата не кнопка - виходимо з метода
        if (!(source instanceof Button)) {
            return;
        }

        Person selectedPerson = tableAddressBook.getSelectionModel().getSelectedItem();

        Button clickedButton = (Button) source;

        switch (clickedButton.getId()) {
            case "btnAdd":
                editDialogController.setPerson(new Person());
                showDialog();
                addressBookImpl.add(editDialogController.getPerson());
                break;
            case "btnEdit":
                if (!personIsSelected(selectedPerson)) {
                    return;
                }
                editDialogController.setPerson(selectedPerson);
                showDialog();
                break;
            case "btnDelete":
                if (!personIsSelected(selectedPerson)) {
                    return;
                }
                addressBookImpl.delete(selectedPerson);
                break;
        }

    }

    private boolean personIsSelected(Person selectedPerson) {
        if (selectedPerson == null) {
            DialogManager.showInfoDialog(resourceBundle.getString("error"), resourceBundle.getString("select_person"));
            return false;
        }
        return true;
    }

    private void showDialog() {

        if (editDialogStage == null) {
            editDialogStage = new Stage();
            editDialogStage.setTitle(resourceBundle.getString("edit"));
            editDialogStage.setMinHeight(150);
            editDialogStage.setMinWidth(300);
            editDialogStage.setResizable(false);
            editDialogStage.setScene(new Scene(fxmlEdit));
            editDialogStage.initModality(Modality.WINDOW_MODAL);
            editDialogStage.initOwner(new Stage());
        }

        editDialogStage.showAndWait(); // для очікування закриття вікна
    }

    public void actionSearch() {
        addressBookImpl.getPersonList().clear();

        for (Person person : backupList) {
            if (person.getFio().toLowerCase().contains(txtSearch.getText().toLowerCase()) ||
                    person.getPhone().toLowerCase().contains(txtSearch.getText().toLowerCase())) {
                addressBookImpl.getPersonList().add(person);
            }
        }

    }

}
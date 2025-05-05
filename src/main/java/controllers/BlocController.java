package controllers;

import entities.Blocs;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import services.BlocService;

public class BlocController {

    @FXML
    private TableView<Blocs> tableviewBloc;

    @FXML
    private TableColumn<Blocs, String> nomCol;

    @FXML
    private TableColumn<Blocs, String> descriptionCol;

    @FXML
    private TableColumn<Blocs, String> statutCol;

    @FXML
    private TextField nomField;

    @FXML
    private TextField descriptionField;

    @FXML
    private ComboBox<String> statutComboBox;

    @FXML
    private Button updateButton;
    @FXML
    private TableColumn<Blocs, Void> actionCol;

    @FXML
    private Button addButton;

    private final BlocService blocService = new BlocService();
    private ObservableList<Blocs> blocsList;
    private Blocs selectedBloc = null;

    @FXML
    public void initialize() {
        statutComboBox.setItems(FXCollections.observableArrayList("Actif", "Inactif"));

        nomCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNom()));
        descriptionCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDescription()));
        statutCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStatut()));

        loadBlocs();
        addActionButtonsToTable();

        tableviewBloc.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedBloc = newSelection;
                fillForm(selectedBloc);
            }
        });

        updateButton.setDisable(true);
    }
    private void addActionButtonsToTable() {
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");
            private final Button updateButton = new Button("Update");
            private final HBox hBox = new HBox(10, updateButton, deleteButton);

            {
                hBox.setPadding(new Insets(5));
                deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 2 10 2 10; -fx-font-size: 12px;");
                updateButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 2 10 2 10; -fx-font-size: 12px;");


                deleteButton.setOnAction(event -> {
                    Blocs selectedBloc = getTableView().getItems().get(getIndex());
                    System.out.println("Delete clicked for: " + selectedBloc.getNom());
                    blocService.DeleteByID(selectedBloc.getId());
                    initialize();
                });

                updateButton.setOnAction(event -> {
                    Blocs selectedBloc = getTableView().getItems().get(getIndex());
                    System.out.println("Update clicked for: " + selectedBloc.getNom());
                    handleUpdate(selectedBloc);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(hBox);
                }
            }
        });
    }
    private void handleUpdate(Blocs Bloc) {
        selectedBloc = Bloc;
        nomField.setText(Bloc.getNom());
        descriptionField.setText(Bloc.getDescription());
        statutComboBox.setItems(FXCollections.observableArrayList("Actif", "Inactif"));

        updateButton.setDisable(false);
        addButton.setDisable(true);
    }
    private void loadBlocs() {
        blocsList = FXCollections.observableArrayList(blocService.readAll());
        tableviewBloc.setItems(blocsList);
    }

    private void fillForm(Blocs bloc) {
        nomField.setText(bloc.getNom());
        descriptionField.setText(bloc.getDescription());
        statutComboBox.setValue(bloc.getStatut());
        updateButton.setDisable(false);
    }

    @FXML
    public void handleAdd() {
        String nom = nomField.getText();
        String desc = descriptionField.getText();
        String statut = statutComboBox.getValue();

        if (nom.isEmpty() || desc.isEmpty() || statut == null) return;

        Blocs bloc = new Blocs();
        bloc.setNom(nom);
        bloc.setDescription(desc);
        bloc.setStatut(statut);

        blocService.Add(bloc);
        loadBlocs();
        clearForm();
    }

    @FXML
    public void handleUpdateAction() {
        if (selectedBloc == null) return;

        selectedBloc.setNom(nomField.getText());
        selectedBloc.setDescription(descriptionField.getText());
        selectedBloc.setStatut(statutComboBox.getValue());

        blocService.Update(selectedBloc);
        loadBlocs();
        clearForm();
    }

    private void clearForm() {
        nomField.clear();
        descriptionField.clear();
        statutComboBox.getSelectionModel().clearSelection();
        selectedBloc = null;
        updateButton.setDisable(true);
        tableviewBloc.getSelectionModel().clearSelection();
    }

    @FXML
    public void handleDelete() {
        Blocs toDelete = tableviewBloc.getSelectionModel().getSelectedItem();
        if (toDelete != null) {
            blocService.DeleteByID(toDelete.getId());
            loadBlocs();
            clearForm();
        }
    }
}

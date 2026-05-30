package controller;

import dao.AidDistributionDAO;
import dao.FamilyDAO;
import dao.OrganizationDAO;
import dao.UserDAO;
import model.AidDistribution;
import model.Family;
import model.User;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class CoordinatorDashboardController implements Initializable {

    @FXML private Label orgNameLabel, totalFamiliesLabel, servedByOrgLabel, notServedLabel;
    @FXML private Label selectedFamilyLabel, aidMsgLabel, profileMsgLabel, passwordMsgLabel;

    @FXML private VBox dashboardPane, familiesPane, aidPane, profilePane, changePasswordPane;

    @FXML private TextField familyNameField, familyPhoneField, familyLocationField, familySizeField, familyNationalIdField;
    @FXML private ComboBox<String> familyVulnerabilityCombo;
    @FXML private DatePicker familyRegDatePicker;
    @FXML private TableView<Family> familyTable;
    @FXML private TableColumn<Family, Integer> familyIdCol, familySizeCol;
    @FXML private TableColumn<Family, String> familyNameCol, familyPhoneCol, familyLocationCol, familyNationalIdCol, familyVulnerabilityCol;
    @FXML private TableColumn<Family, LocalDate> familyLastAidCol;

    @FXML private TableView<Family> aidFamilyTable;
    @FXML private TableColumn<Family, Integer> aidFamilyIdCol, aidFamilySizeCol;
    @FXML private TableColumn<Family, String> aidFamilyNameCol, aidFamilyLocationCol, aidFamilyVulnerabilityCol;
    @FXML private TableColumn<Family, LocalDate> aidFamilyLastAidCol;
    @FXML private DatePicker aidDatePicker;
    @FXML private ComboBox<String> aidTypeCombo;

    @FXML private TextField profileFullNameField, profileEmailField, profileUsernameField;

    @FXML private PasswordField currentPasswordField, newPasswordField, confirmPasswordField;

    private FamilyDAO familyDAO = new FamilyDAO();
    private AidDistributionDAO aidDAO = new AidDistributionDAO();
    private UserDAO userDAO = new UserDAO();
    private OrganizationDAO orgDAO = new OrganizationDAO();

    private User currentUser;
    private Family selectedAidFamily;

    public void setUser(User user) {
        this.currentUser = user;

        if (orgNameLabel != null) {
            orgNameLabel.setText(user.getFullName());
        }

        loadDashboardStats();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        setupFamilyTable();
        setupAidFamilyTable();

        familyVulnerabilityCombo.setItems(
                FXCollections.observableArrayList(
                        "HIGH", "MEDIUM", "LOW"
                )
        );

        aidTypeCombo.setItems(
                FXCollections.observableArrayList(
                        "Food", "Cash", "Medical", "Clothes"
                )
        );
    }

    private void showOnly(VBox pane) {
        dashboardPane.setVisible(false);
        dashboardPane.setManaged(false);

        familiesPane.setVisible(false);
        familiesPane.setManaged(false);

        aidPane.setVisible(false);
        aidPane.setManaged(false);

        profilePane.setVisible(false);
        profilePane.setManaged(false);

        changePasswordPane.setVisible(false);
        changePasswordPane.setManaged(false);

        pane.setVisible(true);
        pane.setManaged(true);
    }

    @FXML
    private void showDashboard() {
        showOnly(dashboardPane);
        loadDashboardStats();
    }

    @FXML
    private void showFamilies() {
        showOnly(familiesPane);
        loadFamilies();
    }

    @FXML
    private void showAidDistribution() {
        showOnly(aidPane);
        loadAllFamiliesForAid();
    }

    @FXML
    private void showProfile() {
        showOnly(profilePane);
        loadProfile();
    }

    @FXML
    private void showChangePassword() {
        showOnly(changePasswordPane);
    }

    private void loadDashboardStats() {
        if (currentUser == null) {
            return;
        }

        int totalFamilies = familyDAO.getAllFamilies().size();
        int served = aidDAO.getServedFamiliesByOrg(currentUser.getOrgId());
        int notServed = totalFamilies - aidDAO.getTotalServedFamilies();

        totalFamiliesLabel.setText(String.valueOf(totalFamilies));
        servedByOrgLabel.setText(String.valueOf(served));
        notServedLabel.setText(String.valueOf(notServed));
    }

    private void setupFamilyTable() {
        familyIdCol.setCellValueFactory(new PropertyValueFactory<>("familyId"));
        familyNameCol.setCellValueFactory(new PropertyValueFactory<>("householdName"));
        familyPhoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        familyLocationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        familySizeCol.setCellValueFactory(new PropertyValueFactory<>("familySize"));
        familyNationalIdCol.setCellValueFactory(new PropertyValueFactory<>("nationalId"));
        familyVulnerabilityCol.setCellValueFactory(new PropertyValueFactory<>("vulnerabilityLevel"));
        familyLastAidCol.setCellValueFactory(new PropertyValueFactory<>("lastAidDate"));
    }

    private void loadFamilies() {
        familyTable.setItems(
                FXCollections.observableArrayList(
                        familyDAO.getAllFamilies()
                )
        );
    }

    @FXML
    private void handleAddFamily() {

        if (familyNameField.getText().isEmpty()
                || familyNationalIdField.getText().isEmpty()
                || familySizeField.getText().isEmpty()
                || familyVulnerabilityCombo.getValue() == null
                || familyRegDatePicker.getValue() == null) {

            showAlert(
                    "Error",
                    "All required fields must be filled.",
                    Alert.AlertType.ERROR
            );
            return;
        }

        if (!familyDAO.isNationalIdUnique(
                familyNationalIdField.getText(), 0)) {

            showAlert(
                    "Error",
                    "National ID already exists.",
                    Alert.AlertType.ERROR
            );
            return;
        }

        Family family =
                new Family(
                        0,
                        familyNameField.getText(),
                        familyPhoneField.getText(),
                        familyLocationField.getText(),
                        Integer.parseInt(familySizeField.getText()),
                        familyNationalIdField.getText(),
                        familyVulnerabilityCombo.getValue(),
                        familyRegDatePicker.getValue(),
                        null
                );

        if (familyDAO.addFamily(family)) {

            showAlert(
                    "Success",
                    "Family registered.",
                    Alert.AlertType.INFORMATION
            );

            loadFamilies();
            handleResetFamily();

        } else {

            showAlert(
                    "Error",
                    "Failed to register family.",
                    Alert.AlertType.ERROR
            );
        }
    }

    @FXML
    private void handleResetFamily() {
        familyNameField.clear();
        familyPhoneField.clear();
        familyLocationField.clear();
        familySizeField.clear();
        familyNationalIdField.clear();
        familyVulnerabilityCombo.setValue(null);
        familyRegDatePicker.setValue(null);
    }

    private void setupAidFamilyTable() {
        aidFamilyIdCol.setCellValueFactory(new PropertyValueFactory<>("familyId"));
        aidFamilyNameCol.setCellValueFactory(new PropertyValueFactory<>("householdName"));
        aidFamilyLocationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        aidFamilySizeCol.setCellValueFactory(new PropertyValueFactory<>("familySize"));
        aidFamilyVulnerabilityCol.setCellValueFactory(new PropertyValueFactory<>("vulnerabilityLevel"));
        aidFamilyLastAidCol.setCellValueFactory(new PropertyValueFactory<>("lastAidDate"));

        aidFamilyTable
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, old, selected) -> {

                    if (selected != null) {

                        selectedAidFamily = selected;

                        selectedFamilyLabel.setText(
                                selected.getHouseholdName()
                                + " ("
                                + selected.getVulnerabilityLevel()
                                + ")"
                        );
                    }
                });
    }

    private void loadAllFamiliesForAid() {
        aidFamilyTable.setItems(
                FXCollections.observableArrayList(
                        familyDAO.getAllFamilies()
                )
        );
    }

    @FXML
    private void handleShowVulnerable() {
        aidFamilyTable.setItems(
                FXCollections.observableArrayList(
                        familyDAO.getFamiliesByVulnerability()
                )
        );
    }

    @FXML
    private void handleShowUnserved() {
        aidFamilyTable.setItems(
                FXCollections.observableArrayList(
                        familyDAO.getUnservedFamilies()
                )
        );
    }

    @FXML
    private void handleShowAllFamilies() {
        loadAllFamiliesForAid();
    }

    @FXML
    private void handleRecordAid() {

        if (selectedAidFamily == null) {
            showAidMsg("Please select a family first.", true);
            return;
        }

        if (aidTypeCombo.getValue() == null) {
            showAidMsg("Please select aid type.", true);
            return;
        }

        if (aidDatePicker.getValue() == null) {
            showAidMsg("Please select a distribution date.", true);
            return;
        }

        if (currentUser == null) {
            showAidMsg("No logged-in coordinator found.", true);
            return;
        }

        String vulnerability =
                selectedAidFamily.getVulnerabilityLevel();

        String aidType =
                aidTypeCombo.getValue();

        if (!vulnerability.equals("HIGH")) {

            AidDistribution duplicate =
        aidDAO.checkDuplicate(
                selectedAidFamily.getFamilyId()
        );

            if (duplicate != null) {

                showAidMsg(
                        "REJECTED! Family: "
                        + duplicate.getFamilyName()
                        + " | Aid Type: "
                        + duplicate.getAidType()
                        + " | Vulnerability: "
                        + vulnerability
                        + " | Last aid by: "
                        + duplicate.getOrgName()
                        + " on "
                        + duplicate.getDistributionDate(),
                        true
                );

                return;
            }
        }

        AidDistribution dist =
                new AidDistribution(
                        0,
                        selectedAidFamily.getFamilyId(),
                        currentUser.getOrgId(),
                        currentUser.getUserId(),
                        aidType,
                        aidDatePicker.getValue()
                );

        if (aidDAO.addDistribution(dist)) {

            showAidMsg(
                    "Aid recorded successfully!",
                    false
            );

            loadAllFamiliesForAid();

            selectedAidFamily = null;
            selectedFamilyLabel.setText("None");
            aidDatePicker.setValue(null);
            aidTypeCombo.setValue(null);

            loadDashboardStats();

        } else {

            showAidMsg(
                    "Failed to record aid.",
                    true
            );
        }
    }

    private void showAidMsg(String msg, boolean isError) {
        aidMsgLabel.setText(msg);
        aidMsgLabel.setStyle(
                isError
                        ? "-fx-text-fill: #e94560;"
                        : "-fx-text-fill: green;"
        );
        aidMsgLabel.setVisible(true);
    }

    private void loadProfile() {
        if (currentUser == null) {
            return;
        }

        profileFullNameField.setText(
                currentUser.getFullName()
        );

        profileEmailField.setText(
                currentUser.getEmail()
        );

        profileUsernameField.setText(
                currentUser.getUsername()
        );
    }

    @FXML
    private void handleUpdateProfile() {

        if (currentUser == null) {
            return;
        }

        currentUser.setFullName(
                profileFullNameField.getText()
        );

        currentUser.setEmail(
                profileEmailField.getText()
        );

        if (userDAO.updateUser(currentUser)) {

            profileMsgLabel.setText(
                    "Profile updated successfully!"
            );

            profileMsgLabel.setStyle(
                    "-fx-text-fill: green;"
            );

        } else {

            profileMsgLabel.setText(
                    "Failed to update profile."
            );

            profileMsgLabel.setStyle(
                    "-fx-text-fill: #e94560;"
            );
        }

        profileMsgLabel.setVisible(true);
    }

    @FXML
    private void handleChangePassword() {

        String current =
                currentPasswordField.getText();

        String newPass =
                newPasswordField.getText();

        String confirm =
                confirmPasswordField.getText();

        if (current.isEmpty()
                || newPass.isEmpty()
                || confirm.isEmpty()) {

            showPasswordMsg(
                    "All fields are required.",
                    true
            );

            return;
        }

        if (!current.equals(
                currentUser.getPassword())) {

            showPasswordMsg(
                    "Current password is incorrect.",
                    true
            );

            return;
        }

        if (newPass.length() < 8) {

            showPasswordMsg(
                    "Password must be at least 8 characters.",
                    true
            );

            return;
        }

        if (!newPass.equals(confirm)) {

            showPasswordMsg(
                    "Passwords do not match.",
                    true
            );

            return;
        }

        if (userDAO.changePassword(
                currentUser.getUserId(),
                newPass)) {

            currentUser.setPassword(newPass);

            showPasswordMsg(
                    "Password changed successfully!",
                    false
            );

            currentPasswordField.clear();
            newPasswordField.clear();
            confirmPasswordField.clear();

        } else {

            showPasswordMsg(
                    "Failed to change password.",
                    true
            );
        }
    }

    private void showPasswordMsg(
            String msg,
            boolean isError
    ) {

        passwordMsgLabel.setText(msg);

        passwordMsgLabel.setStyle(
                isError
                        ? "-fx-text-fill: #e94560;"
                        : "-fx-text-fill: green;"
        );

        passwordMsgLabel.setVisible(true);
    }

    @FXML
    private void handleLogout() {

        try {

            Parent root =
                    FXMLLoader.load(
                            getClass()
                                    .getResource(
                                            "/view/Login.fxml"
                                    )
                    );

            Stage stage =
                    (Stage) totalFamiliesLabel
                            .getScene()
                            .getWindow();

            stage.setScene(
                    new Scene(root)
            );

            stage.setTitle(
                    "GHADS - Login"
            );

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    @FXML
    private void handleExit() {
        System.exit(0);
    }

    @FXML
    private void handleFontSize() {

        TextInputDialog dialog =
                new TextInputDialog("14");

        dialog.setTitle("Font Size");
        dialog.setHeaderText("Enter font size:");

        dialog.showAndWait().ifPresent(size -> {

            try {

                totalFamiliesLabel
                        .getScene()
                        .getRoot()
                        .setStyle(
                                "-fx-font-size: "
                                + size
                                + "px;"
                        );

            } catch (Exception ignored) {
            }
        });
    }

    @FXML
    private void handleFontFamily() {

        TextInputDialog dialog =
                new TextInputDialog("Segoe UI");

        dialog.setTitle("Font Family");
        dialog.setHeaderText("Enter font family:");

        dialog.showAndWait().ifPresent(font -> {

            try {

                totalFamiliesLabel
                        .getScene()
                        .getRoot()
                        .setStyle(
                                "-fx-font-family: '"
                                + font
                                + "';"
                        );

            } catch (Exception ignored) {
            }
        });
    }

    @FXML
    private void handleTheme() {

        javafx.scene.Parent root =
                totalFamiliesLabel
                        .getScene()
                        .getRoot();

        String current =
                root.getStyle();

        root.setStyle(
                current.contains("#1a1a2e")
                        ? "-fx-background-color: #f0f2f5;"
                        : "-fx-background-color: #1a1a2e;"
        );
    }

    @FXML
    private void handleAbout() {

        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        alert.setTitle("About GHADS");

        alert.setHeaderText(
                "GHADS - Gaza Humanitarian Aid Distribution System"
        );

        alert.setContentText(
                "GHADS is a desktop application developed to help\n"
                + "humanitarian organizations in Gaza coordinate\n"
                + "and manage aid distribution fairly and efficiently.\n\n"
                + "Developed By:\n"
                + "Manar Abu Arab\n"
                + "Areej Kuhail\n\n"
                + "Programming III Project\n"
                + "Islamic University of Gaza\n\n"
                + "Version 1.0 - 2026"
        );

        alert.showAndWait();
    }

    private void showAlert(
            String title,
            String msg,
            Alert.AlertType type
    ) {

        Alert alert =
                new Alert(type);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
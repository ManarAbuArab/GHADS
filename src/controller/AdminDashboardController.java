package controller;

import dao.AidDistributionDAO;
import dao.FamilyDAO;
import dao.OrganizationDAO;
import dao.UserDAO;
import model.AidDistribution;
import model.Family;
import model.Organization;
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
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class AdminDashboardController implements Initializable {

    // Dashboard
    @FXML private Label totalOrgsLabel, totalUsersLabel, totalFamiliesLabel, servedFamiliesLabel, notServedLabel;

    // Panes
    @FXML private VBox dashboardPane, organizationsPane, usersPane, familiesPane, aidPane, changePasswordPane;

    // Organizations
    @FXML private TextField orgNameField, orgTypeField, orgContactField;
    @FXML private TableView<Organization> orgTable;
    @FXML private TableColumn<Organization, Integer> orgIdCol;
    @FXML private TableColumn<Organization, String> orgNameCol, orgTypeCol, orgContactCol;

    // Users
    @FXML private TextField userFullNameField, userUsernameField, userPasswordField, userEmailField;
    @FXML private ComboBox<Organization> userOrgCombo;
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Integer> userIdCol;
    @FXML private TableColumn<User, String> userFullNameCol, userUsernameCol, userEmailCol, userOrgCol;

    // Families
    @FXML private TextField familyNameField, familyPhoneField, familyLocationField, familySizeField, familyNationalIdField;
    @FXML private ComboBox<String> familyVulnerabilityCombo;
    @FXML private DatePicker familyRegDatePicker;
    @FXML private TableView<Family> familyTable;
    @FXML private TableColumn<Family, Integer> familyIdCol, familySizeCol;
    @FXML private TableColumn<Family, String> familyNameCol, familyPhoneCol, familyLocationCol, familyNationalIdCol, familyVulnerabilityCol;
    @FXML private TableColumn<Family, LocalDate> familyLastAidCol;

    
    @FXML private ComboBox<Organization> aidOrgFilterCombo;
    @FXML private TableView<AidDistribution> aidTable;
    @FXML private TableColumn<AidDistribution, Integer> aidIdCol;
    @FXML private TableColumn<AidDistribution, String> aidFamilyCol;
    @FXML private TableColumn<AidDistribution, String> aidOrgCol;
    @FXML private TableColumn<AidDistribution, String> aidCoordinatorCol;
    @FXML private TableColumn<AidDistribution, String> aidTypeCol;
    @FXML private TableColumn<AidDistribution, LocalDate> aidDateCol;

    
    @FXML private PasswordField currentPasswordField, newPasswordField, confirmPasswordField;
    @FXML private Label passwordMsgLabel;

    private OrganizationDAO orgDAO = new OrganizationDAO();
    private UserDAO userDAO = new UserDAO();
    private FamilyDAO familyDAO = new FamilyDAO();
    private AidDistributionDAO aidDAO = new AidDistributionDAO();

    private Organization selectedOrg;
    private User selectedUser;
    private Family selectedFamily;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupOrgTable();
        setupUserTable();
        setupFamilyTable();
        setupAidTable();
        familyVulnerabilityCombo.setItems(FXCollections.observableArrayList("HIGH", "MEDIUM", "LOW"));
        loadDashboardStats();
    }

    // ==================== NAVIGATION ====================
    private void showOnly(VBox pane) {
        dashboardPane.setVisible(false); dashboardPane.setManaged(false);
        organizationsPane.setVisible(false); organizationsPane.setManaged(false);
        usersPane.setVisible(false); usersPane.setManaged(false);
        familiesPane.setVisible(false); familiesPane.setManaged(false);
        aidPane.setVisible(false); aidPane.setManaged(false);
        changePasswordPane.setVisible(false); changePasswordPane.setManaged(false);
        pane.setVisible(true); pane.setManaged(true);
    }

    @FXML private void showDashboard() { showOnly(dashboardPane); loadDashboardStats(); }
    @FXML private void showOrganizations() { showOnly(organizationsPane); loadOrgs(); }
    @FXML private void showUsers() { showOnly(usersPane); loadUsers(); loadOrgCombo(); }
    @FXML private void showFamilies() { showOnly(familiesPane); loadFamilies(); }
    @FXML private void showAidDistributions() { showOnly(aidPane); loadAid(); loadAidOrgFilter(); }
    @FXML private void showChangePassword() { showOnly(changePasswordPane); }

    
    private void loadDashboardStats() {
        int totalFamilies = familyDAO.getAllFamilies().size();
        int served = aidDAO.getTotalServedFamilies();
        totalOrgsLabel.setText(String.valueOf(orgDAO.getAllOrganizations().size()));
        totalUsersLabel.setText(String.valueOf(userDAO.getAllUsers().size()));
        totalFamiliesLabel.setText(String.valueOf(totalFamilies));
        servedFamiliesLabel.setText(String.valueOf(served));
        notServedLabel.setText(String.valueOf(totalFamilies - served));
    }

    
    private void setupOrgTable() {
        orgIdCol.setCellValueFactory(new PropertyValueFactory<>("orgId"));
        orgNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        orgTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        orgContactCol.setCellValueFactory(new PropertyValueFactory<>("contactInfo"));
        orgTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                selectedOrg = selected;
                orgNameField.setText(selected.getName());
                orgTypeField.setText(selected.getType());
                orgContactField.setText(selected.getContactInfo());
            }
        });
    }

    private void loadOrgs() {
        orgTable.setItems(FXCollections.observableArrayList(orgDAO.getAllOrganizations()));
    }

    @FXML private void handleAddOrg() {
        if (orgNameField.getText().isEmpty() || orgTypeField.getText().isEmpty()) {
            showAlert("Error", "Name and Type are required.", Alert.AlertType.ERROR); return;
        }
        Organization org = new Organization(0, orgNameField.getText(), orgTypeField.getText(), orgContactField.getText());
        if (orgDAO.addOrganization(org)) { showAlert("Success", "Organization added.", Alert.AlertType.INFORMATION); loadOrgs(); handleResetOrg(); }
        else showAlert("Error", "Failed to add organization.", Alert.AlertType.ERROR);
    }

    @FXML private void handleUpdateOrg() {
        if (selectedOrg == null) { showAlert("Error", "Select an organization first.", Alert.AlertType.ERROR); return; }
        selectedOrg.setName(orgNameField.getText());
        selectedOrg.setType(orgTypeField.getText());
        selectedOrg.setContactInfo(orgContactField.getText());
        if (orgDAO.updateOrganization(selectedOrg)) { showAlert("Success", "Organization updated.", Alert.AlertType.INFORMATION); loadOrgs(); handleResetOrg(); }
        else showAlert("Error", "Failed to update.", Alert.AlertType.ERROR);
    }

    @FXML private void handleDeleteOrg() {
        if (selectedOrg == null) { showAlert("Error", "Select an organization first.", Alert.AlertType.ERROR); return; }
        if (confirmDelete()) {
            if (orgDAO.deleteOrganization(selectedOrg.getOrgId())) { showAlert("Success", "Organization deleted.", Alert.AlertType.INFORMATION); loadOrgs(); handleResetOrg(); }
            else showAlert("Error", "Failed to delete.", Alert.AlertType.ERROR);
        }
    }

    @FXML private void handleResetOrg() {
        orgNameField.clear(); orgTypeField.clear(); orgContactField.clear(); selectedOrg = null;
    }

    // ==================== USERS ====================
    private void setupUserTable() {
    userIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
    userFullNameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
    userUsernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
    userEmailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
    userOrgCol.setCellValueFactory(cellData -> {
        int orgId = cellData.getValue().getOrgId();
        List<Organization> orgs = orgDAO.getAllOrganizations();
        String orgName = orgs.stream()
                .filter(o -> o.getOrgId() == orgId)
                .map(Organization::getName)
                .findFirst()
                .orElse("N/A");
        return new javafx.beans.property.SimpleStringProperty(orgName);
    });
    userTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
        if (selected != null) {
            selectedUser = selected;
            userFullNameField.setText(selected.getFullName());
            userUsernameField.setText(selected.getUsername());
            userPasswordField.setText(selected.getPassword());
            userEmailField.setText(selected.getEmail());
        }
    });
}

    private void loadUsers() {
        userTable.setItems(FXCollections.observableArrayList(userDAO.getAllUsers()));
    }

    private void loadOrgCombo() {
        userOrgCombo.setItems(FXCollections.observableArrayList(orgDAO.getAllOrganizations()));
    }

    @FXML private void handleAddUser() {
        if (userFullNameField.getText().isEmpty() || userUsernameField.getText().isEmpty() ||
            userPasswordField.getText().isEmpty() || userEmailField.getText().isEmpty() || userOrgCombo.getValue() == null) {
            showAlert("Error", "All fields are required.", Alert.AlertType.ERROR); return;
        }
        if (userPasswordField.getText().length() < 8) {
            showAlert("Error", "Password must be at least 8 characters.", Alert.AlertType.ERROR); return;
        }
        if (!userDAO.isUsernameUnique(userUsernameField.getText(), 0)) {
            showAlert("Error", "Username already exists.", Alert.AlertType.ERROR); return;
        }
        if (!userDAO.isEmailUnique(userEmailField.getText(), 0)) {
            showAlert("Error", "Email already exists.", Alert.AlertType.ERROR); return;
        }
        User user = new User(0, userUsernameField.getText(), userPasswordField.getText(),
                userFullNameField.getText(), userEmailField.getText(), "COORDINATOR", userOrgCombo.getValue().getOrgId());
        if (userDAO.addUser(user)) { showAlert("Success", "User added.", Alert.AlertType.INFORMATION); loadUsers(); handleResetUser(); }
        else showAlert("Error", "Failed to add user.", Alert.AlertType.ERROR);
    }

    @FXML private void handleUpdateUser() {
        if (selectedUser == null) { showAlert("Error", "Select a user first.", Alert.AlertType.ERROR); return; }
        selectedUser.setFullName(userFullNameField.getText());
        selectedUser.setUsername(userUsernameField.getText());
        selectedUser.setPassword(userPasswordField.getText());
        selectedUser.setEmail(userEmailField.getText());
        if (userOrgCombo.getValue() != null) selectedUser.setOrgId(userOrgCombo.getValue().getOrgId());
        if (userDAO.updateUser(selectedUser)) { showAlert("Success", "User updated.", Alert.AlertType.INFORMATION); loadUsers(); handleResetUser(); }
        else showAlert("Error", "Failed to update.", Alert.AlertType.ERROR);
    }

    @FXML private void handleDeleteUser() {
        if (selectedUser == null) { showAlert("Error", "Select a user first.", Alert.AlertType.ERROR); return; }
        if (confirmDelete()) {
            if (userDAO.deleteUser(selectedUser.getUserId())) { showAlert("Success", "User deleted.", Alert.AlertType.INFORMATION); loadUsers(); handleResetUser(); }
            else showAlert("Error", "Failed to delete.", Alert.AlertType.ERROR);
        }
    }

    @FXML private void handleResetUser() {
        userFullNameField.clear(); userUsernameField.clear(); userPasswordField.clear();
        userEmailField.clear(); userOrgCombo.setValue(null); selectedUser = null;
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
        familyTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                selectedFamily = selected;
                familyNameField.setText(selected.getHouseholdName());
                familyPhoneField.setText(selected.getPhone());
                familyLocationField.setText(selected.getLocation());
                familySizeField.setText(String.valueOf(selected.getFamilySize()));
                familyNationalIdField.setText(selected.getNationalId());
                familyVulnerabilityCombo.setValue(selected.getVulnerabilityLevel());
                familyRegDatePicker.setValue(selected.getRegistrationDate());
            }
        });
    }

    private void loadFamilies() {
        familyTable.setItems(FXCollections.observableArrayList(familyDAO.getAllFamilies()));
    }

    @FXML private void handleAddFamily() {
        if (familyNameField.getText().isEmpty() || familyNationalIdField.getText().isEmpty() ||
            familySizeField.getText().isEmpty() || familyVulnerabilityCombo.getValue() == null || familyRegDatePicker.getValue() == null) {
            showAlert("Error", "All required fields must be filled.", Alert.AlertType.ERROR); return;
        }
        if (!familyDAO.isNationalIdUnique(familyNationalIdField.getText(), 0)) {
            showAlert("Error", "National ID already exists.", Alert.AlertType.ERROR); return;
        }
        Family family = new Family(0, familyNameField.getText(), familyPhoneField.getText(),
                familyLocationField.getText(), Integer.parseInt(familySizeField.getText()),
                familyNationalIdField.getText(), familyVulnerabilityCombo.getValue(),
                familyRegDatePicker.getValue(), null);
        if (familyDAO.addFamily(family)) { showAlert("Success", "Family added.", Alert.AlertType.INFORMATION); loadFamilies(); handleResetFamily(); }
        else showAlert("Error", "Failed to add family.", Alert.AlertType.ERROR);
    }

    @FXML private void handleUpdateFamily() {
        if (selectedFamily == null) { showAlert("Error", "Select a family first.", Alert.AlertType.ERROR); return; }
        selectedFamily.setHouseholdName(familyNameField.getText());
        selectedFamily.setPhone(familyPhoneField.getText());
        selectedFamily.setLocation(familyLocationField.getText());
        selectedFamily.setFamilySize(Integer.parseInt(familySizeField.getText()));
        selectedFamily.setNationalId(familyNationalIdField.getText());
        selectedFamily.setVulnerabilityLevel(familyVulnerabilityCombo.getValue());
        if (familyDAO.updateFamily(selectedFamily)) { showAlert("Success", "Family updated.", Alert.AlertType.INFORMATION); loadFamilies(); handleResetFamily(); }
        else showAlert("Error", "Failed to update.", Alert.AlertType.ERROR);
    }

    @FXML private void handleDeleteFamily() {
        if (selectedFamily == null) { showAlert("Error", "Select a family first.", Alert.AlertType.ERROR); return; }
        if (confirmDelete()) {
            if (familyDAO.deleteFamily(selectedFamily.getFamilyId())) { showAlert("Success", "Family deleted.", Alert.AlertType.INFORMATION); loadFamilies(); handleResetFamily(); }
            else showAlert("Error", "Failed to delete.", Alert.AlertType.ERROR);
        }
    }

    @FXML private void handleResetFamily() {
        familyNameField.clear(); familyPhoneField.clear(); familyLocationField.clear();
        familySizeField.clear(); familyNationalIdField.clear();
        familyVulnerabilityCombo.setValue(null); familyRegDatePicker.setValue(null); selectedFamily = null;
    }

     private void setupAidTable() {
    aidIdCol.setCellValueFactory(new PropertyValueFactory<>("distributionId"));
    aidFamilyCol.setCellValueFactory(new PropertyValueFactory<>("familyName"));
    aidOrgCol.setCellValueFactory(new PropertyValueFactory<>("orgName"));
    aidCoordinatorCol.setCellValueFactory(new PropertyValueFactory<>("coordinatorName"));
    aidTypeCol.setCellValueFactory(new PropertyValueFactory<>("aidType"));
    aidDateCol.setCellValueFactory(new PropertyValueFactory<>("distributionDate"));
}

    private void loadAid() {
        aidTable.setItems(FXCollections.observableArrayList(aidDAO.getAllDistributions()));
    }

    private void loadAidOrgFilter() {
        aidOrgFilterCombo.setItems(FXCollections.observableArrayList(orgDAO.getAllOrganizations()));
    }

    @FXML private void handleSearchAid() {
        if (aidOrgFilterCombo.getValue() == null) return;
        aidTable.setItems(FXCollections.observableArrayList(aidDAO.getDistributionsByOrg(aidOrgFilterCombo.getValue().getOrgId())));
    }

    @FXML private void handleShowAllAid() {
        aidOrgFilterCombo.setValue(null);
        loadAid();
    }

    
 @FXML private void handleChangePassword() {
    String current = currentPasswordField.getText();
    String newPass = newPasswordField.getText();
    String confirm = confirmPasswordField.getText();

    if (current.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
        showPasswordMsg("All fields are required.", true); return;
    }
    if (newPass.length() < 8) {
        showPasswordMsg("Password must be at least 8 characters.", true); return;
    }
    if (!newPass.equals(confirm)) {
        showPasswordMsg("Passwords do not match.", true); return;
    }

    
    User admin = userDAO.login("admin", current);
    if (admin == null) {
        showPasswordMsg("Current password is incorrect.", true); return;
    }

    if (userDAO.changePassword(admin.getUserId(), newPass)) {
        showPasswordMsg("Password changed successfully!", false);
        currentPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();
    } else {
        showPasswordMsg("Failed to change password.", true);
    }
}

    private void showPasswordMsg(String msg, boolean isError) {
        passwordMsgLabel.setText(msg);
        passwordMsgLabel.setStyle(isError ? "-fx-text-fill: #e94560;" : "-fx-text-fill: green;");
        passwordMsgLabel.setVisible(true);
    }

    
    @FXML private void handleLogout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
            Stage stage = (Stage) totalOrgsLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("GHADS - Login");
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML private void handleExit() { System.exit(0); }

    @FXML private void handleFontSize() {
        TextInputDialog dialog = new TextInputDialog("14");
        dialog.setTitle("Font Size");
        dialog.setHeaderText("Enter font size:");
        dialog.showAndWait().ifPresent(size -> {
            try {
                totalOrgsLabel.getScene().getRoot().setStyle("-fx-font-size: " + size + "px;");
            } catch (Exception ignored) {}
        });
    }

    @FXML private void handleFontFamily() {
        TextInputDialog dialog = new TextInputDialog("Segoe UI");
        dialog.setTitle("Font Family");
        dialog.setHeaderText("Enter font family:");
        dialog.showAndWait().ifPresent(font -> {
            try {
                totalOrgsLabel.getScene().getRoot().setStyle("-fx-font-family: '" + font + "';");
            } catch (Exception ignored) {}
        });
    }

    @FXML private void handleTheme() {
        javafx.scene.Parent root = totalOrgsLabel.getScene().getRoot();
        String dark = "-fx-background-color: #1a1a2e;";
        String light = "-fx-background-color: #f0f2f5;";
        String current = root.getStyle();
        root.setStyle(current.contains("#1a1a2e") ? light : dark);
    }

    @FXML private void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About GHADS");
        alert.setContentText(
        "GHADS - Gaza Humanitarian Aid Distribution System\n\n" +
        "Developed By:" +
        "Manar Abu Arab\n" +
        "Areej Kuhail\n" +
        "Programming III Project\n" +
        "Islamic University of Gaza"
);
        alert.showAndWait();
    }

    
    private void showAlert(String title, String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private boolean confirmDelete() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Are you sure you want to delete?");
        alert.setContentText("This action cannot be undone.");
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}
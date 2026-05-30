package controller;

import dao.UserDAO;
import model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private UserDAO userDAO = new UserDAO();

    @FXML
    private void handleLogin() {

        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter username and password.");
            return;
        }

        User user = userDAO.login(username, password);

        if (user == null) {
            showError("Invalid username or password.");
            return;
        }

        try {

            String fxml;

            if (user.getRole().equals("ADMIN")) {
                fxml = "/view/AdminDashboard.fxml";
            } else {
                fxml = "/view/CoordinatorDashboard.fxml";
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));

            Parent root = loader.load();

            if (user.getRole().equals("COORDINATOR")) {
                CoordinatorDashboardController ctrl = loader.getController();
                ctrl.setUser(user);
            }

            Scene scene = new Scene(root);

            scene.getStylesheets().add(
                    new java.io.File("src/css/admin-style.css")
                            .toURI()
                            .toURL()
                            .toExternalForm()
            );

            Stage stage = (Stage) usernameField.getScene().getWindow();

            stage.setScene(scene);
            stage.setTitle("GHADS - " + user.getRole());
            stage.setResizable(true);
            stage.show();

        } catch (Exception e) {

            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Dashboard Error");
            alert.setHeaderText("Real Error:");
            alert.setContentText(e.toString());
            alert.showAndWait();
        }
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
    }
}
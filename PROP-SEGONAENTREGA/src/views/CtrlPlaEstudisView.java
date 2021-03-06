package views;

import com.google.gson.Gson;
import controllers.CtrlDomini;
import exceptions.NotFoundException;
import exceptions.RestriccioIntegritatException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import utils.FormValidation;

import java.io.IOException;
import java.util.Map;

public class CtrlPlaEstudisView {
    @FXML
    TextField text_titulacio = new TextField();
    @FXML
    TextField text_any = new TextField();
    @FXML
    TextArea text_descripcio = new TextArea();
    @FXML
    CheckBox checkbox_obsolet = new CheckBox();
    @FXML
    ListView<String> list_assignatures = new ListView<>();
    @FXML
    ChoiceBox<String> choice_assignatures = new ChoiceBox<>();
    @FXML
    Button plaest_elim_assig = new Button();
    @FXML
    Button plaest_afegeix_assig = new Button();

    @FXML
    Label label_nom = new Label();
    @FXML
    Label label_any = new Label();
    @FXML
    Label label_descripcio = new Label();
    @FXML
    CheckBox checkbox_obsolet_consulta = new CheckBox();
    @FXML
    ListView<String> list_assignatures_consulta = new ListView<>();


    @FXML
    Button cancel_button = new Button();
    @FXML
    Button save_button = new Button();

    /***** OTHER CONTROLLER VARIABLES *****/
    private CtrlMainView ctrlMainView;
    private CtrlDomini ctrlDomini = CtrlDomini.getInstance();
    private boolean editmode = false;

    private ObservableList<String> assignatures = FXCollections.observableArrayList();
    private ObservableList<String> assignatures_pos = FXCollections.observableArrayList();

    /**
     * Init function
     */
    public void initialize() {

        loadAssignaturesPossibles();

        plaest_elim_assig.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (list_assignatures.getSelectionModel().getSelectedIndex() != -1) {
                    assignatures_pos.add(list_assignatures.getSelectionModel().getSelectedItem());
                    assignatures.remove(list_assignatures.getSelectionModel().getSelectedItem());
                }
            }
        });

        plaest_afegeix_assig.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (choice_assignatures.getSelectionModel().getSelectedIndex() != -1) {
                    assignatures.add(choice_assignatures.getSelectionModel().getSelectedItem());
                    assignatures_pos.remove(choice_assignatures.getSelectionModel().getSelectedItem());
                }
            }
        });


    }

    /**
     * Tanca la finestra actual i n'obre una per modificar el Pla d'Estudis
     * @throws IOException Si no es troba el fxml
     */
    @FXML
    private void handleModifica() throws IOException {
        ctrlMainView.modificarPlaEstudis(label_nom.getText());
        Stage stage = (Stage) label_nom.getScene().getWindow();
        stage.close();
    }

    /**
     * Carrega la informació complerta d'un pla d'estudis per mostrar-lo en la interficie
     *
     * @param nomPla nom de l'aula que hem seleccionat per consultar
     */
    void displayPlaEstudis(String nomPla) {
        String json = null;
        try {
            json = ctrlDomini.consultarPlaEsudis(nomPla);
            Map<String, Object> plaEst = new Gson().fromJson(json, Map.class);
            label_nom.setText(nomPla);
            text_descripcio.setText((String) plaEst.get("descripcio"));
            text_descripcio.setWrapText(true);
            label_any.setText(String.valueOf(((Double) plaEst.get("any")).intValue()));
            checkbox_obsolet_consulta.setSelected((boolean) plaEst.get("obsolet"));
            assignatures = FXCollections.observableArrayList(ctrlDomini.consultarAssignaturesPlaEstudis(nomPla));
            list_assignatures_consulta.setItems(assignatures);
        } catch (NotFoundException e) {
            alert(e.getMessage());
            exit();
        }
    }

    /**
     * Carrega la informació complerta d'un pla d'estudis per mostrar-lo en la interficie i dona accés al mode d'edició dels paràmetres permesos
     *
     * @param plaEst nom del pla d'estudis que hem seleccionat per consultar
     */
    void loadPlaEstudis(String plaEst) {
        editmode = true;
        String json = null;
        try {
            json = ctrlDomini.consultarPlaEsudis(plaEst);
            Map<String, Object> plaEstudis = new Gson().fromJson(json, Map.class);

            String nom = (String) plaEstudis.get("nomTitulacio");
            if (nom != null && !nom.isEmpty()) {
                text_titulacio.setText(nom);
            } else {
                alert("No es pot mostrar un pla d'estudis no identificat");
            }

            Double any = (Double) plaEstudis.get("any");
            if (any != null) {
                text_any.setText(String.valueOf(any.intValue()));
            }

            String descripcio = (String) plaEstudis.get("descripcio");
            if (descripcio != null && !descripcio.isEmpty()) {
                text_descripcio.setText(descripcio);
            }

            boolean obsolet = (boolean) plaEstudis.get("obsolet");
            checkbox_obsolet.setSelected(obsolet);
            loadAssignaturesPla(plaEst);
            text_descripcio.setWrapText(true);
        } catch (NotFoundException e) {
            alert(e.getMessage());
            exit();
        }
    }

    /**
     * Carrega les assignatures que formen part inicialment del pla
     *
     * @param nomPla nom del pla d'estudis que volem carregar-ne les assignatures
     * @throws NotFoundException si no existeix el pla d'estudis especificat
     */
    private void loadAssignaturesPla(String nomPla) throws NotFoundException {
        assignatures = FXCollections.observableArrayList(CtrlDomini.getInstance().consultarAssignaturesPlaEstudis(nomPla));
        list_assignatures.setItems(assignatures);
    }

    /**
     * Carrega les assignatures que poden formar part del pla inicialment, es a dir, que no estan assignades encara a cap pla
     */
    private void loadAssignaturesPossibles() {
        assignatures_pos = FXCollections.observableArrayList(CtrlDomini.getInstance().consultarAssignaturesLliures());
        choice_assignatures.setItems(assignatures_pos);
    }

    /**
     * Binding amb el controlador principal
     *
     * @param c main controller
     */
    void setMainController(CtrlMainView c) {
        this.ctrlMainView = c;
    }

    /**
     * Guarda totes les modificacions fetes en un pla d'estudis o en guarda un de nou
     */
    public void saveChanges() {
        int numerrors = verifyFields();
        if (numerrors > 0) {
            if (numerrors == 1) alert("Hi ha " + numerrors + " errors en el formulari.");
            else alert("Hi han " + numerrors + " errors en el formulari.");
            return;
        }

        String titulacio = text_titulacio.getText();
        String descripcio = text_descripcio.getText();
        int any = Integer.parseInt(text_any.getText());
        boolean obsolet = checkbox_obsolet.isSelected();

        try {
            if (editmode) {
                /* Delete it */
                ctrlDomini.setObsolet(titulacio, true);
                ctrlDomini.esborrarPlaEstudis(titulacio);
                /* Add it again */
                ctrlDomini.crearPlaEstudis(titulacio, any, descripcio);
                afegeixAssignaturesPla(titulacio);
                ctrlDomini.setObsolet(titulacio, obsolet);
            } else {
                ctrlDomini.crearPlaEstudis(titulacio, any, descripcio);
                afegeixAssignaturesPla(titulacio);
            }

            exit();
        } catch (NotFoundException | RestriccioIntegritatException | IOException e) {
            alert(e.getMessage());
        }
    }

    /**
     * @param titulacio Afegeix al pla d'estudis les assignatures indicaddes
     * @throws NotFoundException             si no existeix el pla d'estudis
     * @throws RestriccioIntegritatException si l'assignatura ja esta afegida al pla
     */
    private void afegeixAssignaturesPla(String titulacio) throws NotFoundException, RestriccioIntegritatException, IOException {
        for (String assignature : assignatures) {
            ctrlDomini.afegirAssignaturaPla(titulacio, assignature);
        }
    }

    /**
     * Tanca la vista i propaga els canvis fets a la vista principal
     */
    public void exit() {
        ctrlMainView.reloadList();
        Stage stage = (Stage) cancel_button.getScene().getWindow();
        stage.close();
    }

    /**
     * Mostra un pop-up amb un missatge d'error si s'en dona un
     *
     * @param s missatge d'error a mostrar
     */
    private void alert(String s) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setContentText(s);
        a.setHeaderText("Hi ha hagut un error");
        a.show();
    }

    /**
     * Deshabilita els camps restringits en la creació d'un pla d'estudis
     */
    void disableCreateFields() {
        checkbox_obsolet.setSelected(false);
        checkbox_obsolet.setDisable(true);
    }

    /**
     * Bloqueja l'edició dels paràmetres no modificables en plans d'estudi ja creats
     */
    void disableEditFields() {
        text_titulacio.setDisable(true);
    }

    /**
     * Verifica els camps del formulari i notifica del nombre d'errors que conté
     *
     * @return nombre d'errors
     */
    private int verifyFields() {
        FormValidation formvalidator = new FormValidation();
        int errorcount = 0;

        if (!formvalidator.validateStringSpace(text_titulacio.getText())) {
            errorcount++;
            text_titulacio.setBorder(formvalidator.errorBorder);
        } else {
            text_titulacio.setBorder(formvalidator.okBorder);
        }

        if (!formvalidator.validateStringSpaceAllowEmpty(text_descripcio.getText())) {
            errorcount++;
            text_descripcio.setBorder(formvalidator.errorBorder);
        } else {
            text_descripcio.setBorder(formvalidator.okBorder);
        }

        if (!formvalidator.validateNumber(text_any.getText())) {
            errorcount++;
            text_any.setBorder(formvalidator.errorBorder);
        } else {
            text_any.setBorder(formvalidator.okBorder);
        }
        return errorcount;
    }
}

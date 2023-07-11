package lk.developersstack.lms.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lk.developersstack.lms.bo.BoFactory;
import lk.developersstack.lms.bo.custom.LaptopBo;
import lk.developersstack.lms.bo.custom.ProgramBo;
import lk.developersstack.lms.bo.custom.StudentBo;
import lk.developersstack.lms.dto.CreateLaptopDto;
import lk.developersstack.lms.dto.CustomRegistrationData;
import lk.developersstack.lms.dto.ProgramDto;
import lk.developersstack.lms.dto.StudentDto;
import lk.developersstack.lms.view.tm.StudentTM;

import java.sql.SQLException;
import java.util.Observable;
import java.util.Optional;

public class MainFormController {
    public TextField txtName;
    public TextField txtContact;
    public TableView<StudentTM> tblStudents;
    public TableColumn colStudentName;
    public TableColumn colContactNumber;
    public TableColumn colSeeMore;
    public TableColumn ColDelete;
    public TableColumn colStudentId;
    public Button btnStudentSave;
    public TextField txtLapBrand;
    public TextField txtLapSearch;
    public TableView tblLaptop;
    public TableColumn colLapId;
    public TableColumn colBrand;
    public TableColumn ColLapDelete;
    public ComboBox<Long> cmbStudent;

    private final StudentBo studentBo = BoFactory.getInstance().getBo(BoFactory.BoType.STUDENT);
    private final LaptopBo laptopBo = BoFactory.getInstance().getBo(BoFactory.BoType.LAPTOP);
    private final ProgramBo programBo = BoFactory.getInstance().getBo(BoFactory.BoType.PROGRAM);
    public Button btnLapSave;
    public TextField txtStudentSearch;
    public TextField txtProgramTitle;
    public TextField txtProgramCredit;
    public Button btnProgramSave;
    public TextField txtProgramSearch;
    public TableView tblProgram;
    public TableColumn colProgramId;
    public TableColumn colProgramTitle;
    public TableColumn ColProgramDelete;
    public TableColumn colCredit;
    public TextField txtRegistrationSearch;
    public TableView<CustomRegistrationData> tblRegistration;
    public TableColumn colId;
    public TableColumn colDate;
    public TableColumn colStudent;
    public TableColumn colProgram;
    public ComboBox<Long> cmbPrograms;
    public ComboBox<Long> cmbStudentForProgram;

    public void initialize() throws SQLException, ClassNotFoundException {

        colStudentId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colStudentName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colContactNumber.setCellValueFactory(new PropertyValueFactory<>("contact"));
        colSeeMore.setCellValueFactory(new PropertyValueFactory<>("seeMoreBtn"));
        ColDelete.setCellValueFactory(new PropertyValueFactory<>("deleteBtn"));


        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colStudent.setCellValueFactory(new PropertyValueFactory<>("student"));
        colProgram.setCellValueFactory(new PropertyValueFactory<>("title"));

        loadAllStudents();
        loadAllStudentsForLaptopSection();
        loadProgramsForRegistrationSection();
        loadAllRegistrations();

        //-------------listner--------------
        tblStudents.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if(newValue!=null){
                        selectedStudentTm = newValue;
                        txtName.setText(newValue.getName());
                        txtContact.setText(newValue.getContact());
                        btnStudentSave.setText("Update Student");
                    }
        });
        //-------------listner--------------
    }

    private void loadAllStudentsForLaptopSection() throws SQLException, ClassNotFoundException {
        ObservableList<Long> oblist = FXCollections.observableArrayList();
        for (StudentDto dto : studentBo.findAllStudents()) {
            oblist.add(dto.getId());
        }
        cmbStudent.setItems(oblist);
        cmbStudentForProgram.setItems(oblist);
    }

    private void loadProgramsForRegistrationSection() throws SQLException, ClassNotFoundException {
        ObservableList<Long> oblist = FXCollections.observableArrayList(
                programBo.findAllStudentIds()
        );

        cmbPrograms.setItems(oblist);
    }

    private StudentTM selectedStudentTm = null;

    private void loadAllStudents() throws SQLException, ClassNotFoundException {
        ObservableList<StudentTM> tmList = FXCollections.observableArrayList();
        for(StudentDto dto :studentBo.findAllStudents()){
            Button deleteButton = new Button("Delete");
            deleteButton.setStyle("-fx-background-color: #c0392b");
            Button seeMoreButton = new Button("See More");
            deleteButton.setStyle("-fx-background-color: #2980b9");

            StudentTM tm = new StudentTM(dto.getId(),dto.getName(),
                    dto.getContact(), deleteButton,seeMoreButton);
            tmList.add(tm);

            deleteButton.setOnAction(e->{

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                        "Are you Sure",ButtonType.YES, ButtonType.NO);
                Optional<ButtonType> selectedButtonData = alert.showAndWait();

                if(selectedButtonData.get().equals(ButtonType.YES)){
                    try{
                        studentBo.deleteStudentById(tm.getId());
                        new Alert(Alert.AlertType.INFORMATION, "Student Deleted").show();
                        loadAllStudents();
                    }catch (Exception e1){
                        new Alert(Alert.AlertType.ERROR, "Try Again").show();
                    }
                }
            });
        }
        tblStudents.setItems(tmList);

    }

    public void btnSaveStudentOnAction(ActionEvent actionEvent) {
        StudentDto dto = new StudentDto();
        dto.setName(txtName.getText());
        dto.setContact(txtContact.getText());

        if(btnStudentSave.getText().equals("Update Student")){
            if(selectedStudentTm == null){
                new Alert(Alert.AlertType.ERROR, "Try Again").show();
                return;
            }
            try{
                dto.setId(selectedStudentTm.getId());
                studentBo.updateStudent(dto);
                new Alert(Alert.AlertType.INFORMATION, "Student Updated").show();
                selectedStudentTm = null;
                btnStudentSave.setText("Save Student");
                loadAllStudents();
            }catch (Exception e){
                new Alert(Alert.AlertType.ERROR, "Try Again").show();
            }
        }else{
            try{
                studentBo.saveStudent(dto);
                new Alert(Alert.AlertType.INFORMATION, "Student Save").show();
                loadAllStudents();
                loadAllStudentsForLaptopSection();
            }catch (Exception e){
                new Alert(Alert.AlertType.ERROR, "Try Again").show();
            }
        }
    }

    public void newStudentOnAction(ActionEvent actionEvent) {
        btnStudentSave.setText("Save Student");
        selectedStudentTm = null;
    }

    public void newLaptopOnAction(ActionEvent actionEvent) {
    }

    public void btnSaveLaptopOnAction(ActionEvent actionEvent) {
        try{
            laptopBo.saveLaptop(
                    new CreateLaptopDto(
                            cmbStudent.getValue(),
                            txtLapBrand.getText()
                    )
            );
            new Alert(Alert.AlertType.INFORMATION, "Laptop Save").show();
            loadAllLaptops();
        }catch (Exception e){
            new Alert(Alert.AlertType.ERROR, "Try Again").show();
        }
    }

    private void loadAllLaptops() {
        // to be implemented, same as students
    }

    public void newProgramOnAction(ActionEvent actionEvent) {
    }

    public void btnSaveProgramOnAction(ActionEvent actionEvent) {
        try{
            programBo.saveProgram(
                    new ProgramDto(
                            txtProgramTitle.getText(),
                            Integer.parseInt(txtProgramCredit.getText())
                    )
            );
            new Alert(Alert.AlertType.INFORMATION, "Program Saved").show();
            loadAllPrograms();
            loadProgramsForRegistrationSection();
        }catch (Exception e){
            new Alert(Alert.AlertType.ERROR, "Try Again").show();
        }
    }

    private void loadAllPrograms() {
        // to be implemented, same as students
    }

    public void btnRegisterOnAction(ActionEvent actionEvent) {
        try{
            programBo.register(
                    cmbStudentForProgram.getValue(),
                    cmbPrograms.getValue()
            );
            new Alert(Alert.AlertType.INFORMATION, "Regitered").show();
            loadAllRegistrations();
        }catch (Exception e){
            new Alert(Alert.AlertType.ERROR, "Try Again").show();
        }
    }

    private void loadAllRegistrations() {
        ObservableList<CustomRegistrationData> list = FXCollections
                .observableArrayList(programBo.findAllRegistrations());
        tblRegistration.setItems(list);
    }
}

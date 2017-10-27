package postojna;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;


public class Controller implements Initializable{

    File destinationFolder;
    File sourceFolder;

    final static String mainpath = "C:\\Users\\Antonio\\Desktop\\";


//    Copy file from @source to @dest
//
    private static void copyFileUsingStream(File source, File dest) throws IOException {
        try (InputStream is = new FileInputStream(source); OutputStream os = new FileOutputStream(dest)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        }
    }

//    Create files condition.txt and end.txt for the printing machine
//
    private static void createPrintConditions(File f, String pix) {

        File[] listOfFiles = f.listFiles();
        FileWriter fw = null;
        BufferedWriter bw = null;
        PrintWriter printWriter = null;


        boolean allreadyConditionFile = false;

        for (File file : listOfFiles) {
            if (file.isDirectory()) {

                File[] pictures = file.listFiles();


                try {
//                    System.out.println(file.getCanonicalPath() + "\\" + "condition.txt");
                    File condition = new File(file.getCanonicalPath() + "\\" + "condition.txt");
                    File end = new File(file.getCanonicalPath() + "\\" + "end.txt");
                    end.createNewFile();
                    fw = new FileWriter(condition);
                    bw = new BufferedWriter(fw);
                    printWriter = new PrintWriter(bw);

                    printWriter.println("[OutDevice]");
                    printWriter.println("DeviceName=PICsRGB");
                    printWriter.println("[ImageList]");
                    int picCounter = 0;
                    for (File picture : pictures) {
                        if (picture.getName().endsWith("jpg")) {
                            picCounter++;
                        }
                    }

                    printWriter.println("ImageCnt=" + picCounter);

//                        File[] pictures = file1.listFiles();
                    int counter = 1;
                    for (File picture : pictures) {
                        if (picture.getName().endsWith("jpg")) {
                            printWriter.println(counter + "=" + picture.getName());
                            counter++;
                        }
                    }

                    for (File picture : pictures) {
                        if (picture.getName().endsWith("jpg")) {
                            printWriter.println("[" + picture.getName() + "]");
                            printWriter.println("SizeName=" + pix);
                            printWriter.println("PrintCnt=1");
                            printWriter.println("BackPrint=FILE");
                            printWriter.println("BackPrintLine1=");
                            printWriter.println("BackPrintLine2=");
                            printWriter.println("Resize=FILLIN");
                            printWriter.println("DSC_Chk=FALSE");
                        }
                    }

                } catch (Exception e) {
                    System.out.println(e);
                } finally {
                    try {
                        if (printWriter != null)
                            printWriter.close();
                    } catch (Exception e) {
                        //exception handling left as an exercise for the reader
                    }
                    try {
                        if (bw != null)
                            bw.close();
                    } catch (IOException e) {
                        //exception handling left as an exercise for the reader
                    }
                    try {
                        if (fw != null)
                            fw.close();
                    } catch (IOException e) {
                        //exception handling left as an exercise for the reader
                    }
                }

            }

        }
    }

//    Check on  preset interval  if there are @max files allready in @srcFile to
//    move then into the @destFile, using the @pix to determine the format which will be
//    used in the .txt files required for the printing machine
//
//    In the case of PostojnaCave application the @srcFile is the folder where all of the
//    photoshop ready files will be in.
//
    private static void checkForNewPic(File srcFile, String destFile, int max, String pix) {

        String logFolderPath = mainpath + "logs";
        int MAX_FILES = max;
        File[] listOfFiles = srcFile.listFiles();
        boolean allreadyLogFile = false;
        PrintWriter printWriter = null;
        BufferedWriter bw = null;
        FileWriter fw = null;
        boolean makeConditions = false;
        int howManyUntilBreak = 0;
        String timeStamp = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
        String logTimeStap = new SimpleDateFormat("MM.dd.yyyy").format(Calendar.getInstance().getTime());
//        String folderTimeStamp = new SimpleDateFormat("MM.dd.yyyy-HH-mm-ss").format(Calendar.getInstance().getTime());
        String folderTimeStamp = new SimpleDateFormat("MMddHHmmss").format(Calendar.getInstance().getTime());

        try {
            File progFile = new File(logFolderPath);
            File[] listOfFiles1 = progFile.listFiles();
            for (int i = 0; i < listOfFiles1.length; i++) {
                if (listOfFiles1[i].isFile()) {
//                    System.out.println("File " + listOfFiles[i].getName());
                    if (listOfFiles1[i].getName().equals(logTimeStap + "-log.txt")) {
                        allreadyLogFile = true;
                    }
                }
            }

            File log = new File(logFolderPath + "/" + logTimeStap + "-log.txt");
            if (allreadyLogFile) {

                fw = new FileWriter(log, true);
                bw = new BufferedWriter(fw);
                printWriter = new PrintWriter(bw);

            } else {
//                System.out.println(allreadyLogFile);
                fw = new FileWriter(log);
                bw = new BufferedWriter(fw);
                printWriter = new PrintWriter(bw);
            }

//            String newPics = "newPics";
            int counter = 1;
            printWriter.println("-----------------------------------"); //35 crti
            printWriter.print("Program started at " + timeStamp);
            printWriter.println();
            printWriter.println();
            boolean flag = true;

            int k = 0;
            int start = 0;
            if (checkIfThereAreMaxNew(listOfFiles, max)) {
                for (int i = start; i < listOfFiles.length; i++) {
//                boolean firstTime = true;
                    String dat = destFile + "/" + folderTimeStamp;
//                    String dat = destFile + "/" + folderTimeStamp + "-" + counter;
//                System.out.println(dat);
                    File dir = new File(dat);

                    while (k < MAX_FILES) {
                        int c = 0;
                        if (listOfFiles[i].isFile() && (listOfFiles[i].getName().endsWith(".jpg") || listOfFiles[i].getName().endsWith(".JPG"))) {

                            if ((listOfFiles[i].getName().endsWith(".jpg") && !listOfFiles[i].getName().endsWith("DONE.jpg")) || (listOfFiles[i].getName().endsWith(".JPG") && !listOfFiles[i].getName().endsWith("DONE.JPG"))) {
                                makeConditions = true;
                                while (flag) {
                                    if (!dir.exists()) {
                                        dir.mkdir();

                                        printWriter.print("New folder created : " + folderTimeStamp + "folderNum" + counter);
                                        printWriter.println();
                                        flag = false;
                                    } else if (dir.listFiles().length < MAX_FILES) {
                                        int razlika = MAX_FILES - dir.listFiles().length;

                                        flag = false;
                                        while (c < razlika) {
//                                        if (listOfFiles[i].isFile() && listOfFiles[i].getName().endsWith(".jpg")) {
                                            if (!listOfFiles[i].getName().endsWith("DONE.jpg") || !listOfFiles[i].getName().endsWith("DONE.JPG")) {
//                                            System.out.println("File being moved : " + listOfFiles[i].getName());
                                                printWriter.print("Picture being moved : " + listOfFiles[i].getName());
                                                printWriter.println();
                                                howManyUntilBreak++;
//                                            Write done on every source pic
                                                File newFile = new File(srcFile + "\\" + listOfFiles[i].getName().replace(".JPG", "DONE.JPG"));
//                            Write done on every pic
                                                if (listOfFiles[i].getName().endsWith(".jpg")) {
                                                    newFile = new File(srcFile + "\\" + listOfFiles[i].getName().replace(".jpg", "DONE.jpg"));
                                                }

                                                copyFileUsingStream(listOfFiles[i], newFile);
//                                            ================================================

                                                String path = dir + "\\" + listOfFiles[i].getName();

                                                listOfFiles[i].renameTo(new File(path));
                                                c++;
                                                i++;
                                            } else {

                                                i++;
                                            }
//                                        }
                                        }
//                                i = start;
//                                flag1 = false;
                                    } else {
                                        counter++;
//                                        dat = destFile + "/" + folderTimeStamp + "-" + counter;
                                        dat = destFile + "/" + folderTimeStamp;
//                                    System.out.println("--=-=-=-=-=-==-");
//                                    System.out.println(dat);
                                        dir = new File(dat);
//                                    System.out.println(dat);
                                    }

                                }
                                if (c > 0) {
                                    break;
                                }


                                printWriter.println("File being moved : " + listOfFiles[i].getName());
                                howManyUntilBreak++;
                                File newFile = new File(srcFile + "\\" + listOfFiles[i].getName().replace(".JPG", "DONE.JPG"));
//                            Write done on every pic
                                if (listOfFiles[i].getName().endsWith(".jpg")) {
                                    newFile = new File(srcFile + "\\" + listOfFiles[i].getName().replace(".jpg", "DONE.jpg"));
                                }
                                copyFileUsingStream(listOfFiles[i], newFile);
//                          ================================================
//                            newFile.renameTo(new File(dir + "/" + newFile.getName()));
                                String path1 = dir + "\\" + listOfFiles[i].getName();
                                System.out.println("The path is : " + path1);

                                listOfFiles[i].renameTo(new File(path1));

//                            if(firstTime) {
//                                try {
//                                    System.out.println(dir);
//                                    Runtime r = Runtime.getRuntime();
//                                    r.exec("C:\\Users\\Antonio\\Desktop\\OBDELAJ.exe" + " " + dir);
//                                    firstTime = false;
//                                } catch (IOException err) {
//                                    err.printStackTrace();
//
//                                }
//                            }

                                k++;
                                i++;
                            } else {
                                i++;
                            }

                        } else {
//                        if (i < listOfFiles.length) {
                            i++;
//                        }
                        }

                    }
                    if (howManyUntilBreak == max) {
                        break;
                    }
                    i = start;
                    flag = true;
                    k = 0;
                    counter++;

                }

            }
        } catch (Exception e) {

            System.out.println(e);
//            printWriter.println(e);
        } finally {
//            System.out.println(makeConditions);
            if (makeConditions) {
//                System.out.println("We are in!");
                createPrintConditions(new File(destFile), pix);
            }

            try {
                if (printWriter != null)
                    printWriter.close();
            } catch (Exception e) {
                //exception handling left as an exercise for the reader
            }
            try {
                if (bw != null)
                    bw.close();
            } catch (IOException e) {
                //exception handling left as an exercise for the reader
            }
            try {
                if (fw != null)
                    fw.close();
            } catch (IOException e) {
                //exception handling left as an exercise for the reader
            }
        }

//        System.out.println("Pictures renamed and moved: " + howManyPrint);

    }

//    Helper function to check if we reached the @max limit of DONE pictures in the folder
//
    private static boolean checkIfThereAreMaxNew(File[] folder, int max) {
        boolean out = false;
        int counter = 0;
//        File[] listOfFiles = folder.listFiles();
        for (File file : folder) {

            if (file.isFile() && ((file.getName().endsWith(".jpg") && !file.getName().endsWith("DONE.jpg")) || (file.getName().endsWith(".JPG") && !file.getName().endsWith("DONE.JPG")))) {
                counter++;
            }
        }
        System.out.println("There are " + counter + " files.");
        return counter >= max;
    }

//    Checks for new pictures which will be then formatted in a folder of @max pictures,
//    and then calls OBDELAJ.exe on the folders on every 10 sec
//
    private static void checkForNewPicFirst(File srcFile, String destFile, int max) {

        int openedInPs = 0;
        int MAX_FILES = max;
        File[] listOfFiles = srcFile.listFiles();
        boolean makeConditions = false;
        String timeStamp = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
//        String folderTimeStamp = new SimpleDateFormat("MM.dd.yyyy-HH-mm-ss").format(Calendar.getInstance().getTime());
        String folderTimeStamp = new SimpleDateFormat("MMddHHmmss").format(Calendar.getInstance().getTime());

        try {

            boolean flag = true;

            int k = 0;
            int start = 0;
            for (int i = start; i < listOfFiles.length; i++) {
                boolean firstTime = true;
                String dat = destFile + "/" + folderTimeStamp;
//                String dat = destFile + "/" + folderTimeStamp + "-" + counter;
//                System.out.println(dat);
                File dir = new File(dat);

                while (k < MAX_FILES) {
                    int c = 0;
                    if (listOfFiles[i].isFile() && (listOfFiles[i].getName().endsWith(".jpg") || listOfFiles[i].getName().endsWith(".JPG"))) {

                        if ((listOfFiles[i].getName().endsWith(".jpg") && !listOfFiles[i].getName().endsWith("DONE.jpg")) || (listOfFiles[i].getName().endsWith(".JPG") && !listOfFiles[i].getName().endsWith("DONE.JPG"))) {
//                            makeConditions = true;
                            while (flag) {
                                if (!dir.exists()) {
                                    dir.mkdir();

//                                    printWriter.print("New folder created : " + folderTimeStamp + "folderNum" + counter);
//                                    printWriter.println();
                                    flag = false;
                                } else if (dir.listFiles().length < MAX_FILES) {
                                    int razlika = MAX_FILES - dir.listFiles().length;

                                    flag = false;
                                    while (c < razlika) {
//                                        if (listOfFiles[i].isFile() && listOfFiles[i].getName().endsWith(".jpg")) {
                                        if (!listOfFiles[i].getName().endsWith("DONE.jpg") || !listOfFiles[i].getName().endsWith("DONE.JPG")) {
//                                            System.out.println("File being moved : " + listOfFiles[i].getName());
//                                            printWriter.print("Picture being moved : " + listOfFiles[i].getName());
//                                            printWriter.println();
//                                            Write done on every source pic


                                            try {
                                                System.out.println(listOfFiles[i].getCanonicalPath());
                                                Runtime r = Runtime.getRuntime();
                                                r.exec(mainpath + "OBDELAJ.exe" + " " + listOfFiles[i].getCanonicalPath());
                                                openedInPs++;
                                            } catch (IOException err) {
                                                err.printStackTrace();
                                            }

                                            File newFile = new File(srcFile + "\\" + listOfFiles[i].getName().replace(".JPG", "DONE.JPG"));
//                            Write done on every pic
                                            if (listOfFiles[i].getName().endsWith(".jpg")) {
                                                newFile = new File(srcFile + "\\" + listOfFiles[i].getName().replace(".jpg", "DONE.jpg"));
                                            }
                                            copyFileUsingStream(listOfFiles[i], newFile);
//                                            ================================================

                                            String path = dir + "\\" + listOfFiles[i].getName();

                                            listOfFiles[i].renameTo(new File(path));
                                            c++;
                                            i++;
                                        } else {

                                            i++;
                                        }
//                                        }
                                    }

                                } else {
                                    dat = destFile + "/" + folderTimeStamp;
                                    dir = new File(dat);
//                                    System.out.println(dat);
                                }

                            }
                            if (c > 0) {
                                break;
                            }

                            File newFile = new File(srcFile + "\\" + listOfFiles[i].getName().replace(".JPG", "DONE.JPG"));
//                            Write done on every pic
                            if (listOfFiles[i].getName().endsWith(".jpg")) {
                                newFile = new File(srcFile + "\\" + listOfFiles[i].getName().replace(".jpg", "DONE.jpg"));
                            }
                            copyFileUsingStream(listOfFiles[i], newFile);
//                          ================================================
//                            newFile.renameTo(new File(dir + "/" + newFile.getName()));
                            String path1 = dir + "\\" + listOfFiles[i].getName();
                            System.out.println("The path is : " + path1);

                            listOfFiles[i].renameTo(new File(path1));

                            if (firstTime) {
                                try {
                                    System.out.println(dir);
                                    Runtime r = Runtime.getRuntime();
                                    r.exec(mainpath + "OBDELAJ.exe" + " " + dir);
                                    firstTime = false;
                                    openedInPs++;
                                } catch (IOException err) {
                                    err.printStackTrace();

                                }
                            }

                            k++;
                            i++;
                        } else {
                            i++;
                        }

                    } else {
//                        if (i < listOfFiles.length) {
                        i++;
//                        }
                    }

                }
                System.out.println(openedInPs);
                if (openedInPs == 1) {
                    break;
                }
                i = start;
                flag = true;
                k = 0;
//                counter++;

            }


        } catch (Exception e) {
//       Prints ArrayOutOfBounds Exception between the intervals
//            System.out.println(e);
        }

    }

//    Helper function to handle the temporary folder /temp
//    to not go over 10 folders at a time.
//    The function is used in a TimerTask on a preset interval
//
    public static void emptyFolder(File folder) {

        if (folder.listFiles().length > 10) {

            System.out.println("Deleting temp folders.");
            File[] files = folder.listFiles();
            for (int i = 0; i < 10; i++) {
                System.out.println(files[i].getName());
                if (files[i].isDirectory()) {
                    for (File file1 : files[i].listFiles()) {
                        file1.delete();
                    }
                    files[i].delete();
                } else {
                    files[i].delete();
                }
            }
        }
    }

    public static class checkForNewPicsTimer extends TimerTask {

        File srcFolder;
        int maxFiles;
        String dest;
        String pix;
        String temp = mainpath + "temp";

        checkForNewPicsTimer(File f, String dest, int maxFiles, String pix) {
            srcFolder = f;
            this.maxFiles = maxFiles;
            this.dest = dest;
            this.pix = pix;
        }

        public void run() {

            checkForNewPic(srcFolder, dest, maxFiles, pix);
        }
    }

    public static class checkForNewPicsFirstTimer extends TimerTask {

        File srcFolder;
        int maxFiles;
        String dest;


        checkForNewPicsFirstTimer(File f, String dest, int maxFiles) {
            srcFolder = f;
            this.maxFiles = maxFiles;
            this.dest = dest;
        }

        public void run() {
//            File folder = new File("../sliki");
            checkForNewPicFirst(srcFolder, dest, maxFiles);

        }
    }

    public static class emptyTempFolderTimer extends TimerTask {

        String temp = mainpath + "temp";


        public void run() {
            emptyFolder(new File(temp));
        }
    }


    @FXML
    private ComboBox<Integer>  intervalChooser;

    @FXML
    private ComboBox<Integer> maxFilesChooser;

    @FXML
    private ComboBox<String> formatChooser;

    @FXML
    private Label sourceLabel;

    @FXML
    private Label destLabel;

    @FXML
    private Label statusLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        intervalChooser.getItems().addAll(15, 20, 30, 50, 60);
        intervalChooser.getSelectionModel().selectFirst();
        maxFilesChooser.getItems().addAll(10, 30, 50);
        maxFilesChooser.getSelectionModel().selectFirst();
        formatChooser.getItems().addAll("13x18", "8x10", "10x12", "10x15", "11x14", "12x16");
        formatChooser.getSelectionModel().selectFirst();
    }

    @FXML
    public void exitApplication(ActionEvent event) {
        Platform.exit();

    }

    public void showAbout(){
        Stage about = new Stage();
        final GridPane inputGridPane = new GridPane();

        Label version = new Label("Version 1.0.0");
        Label copyright = new Label("All rights reserved. Angelis");


        GridPane.setConstraints(version, 0, 0);
        GridPane.setConstraints(copyright, 1, 0);
        inputGridPane.setHgap(6);
        inputGridPane.setVgap(6);
        inputGridPane.getChildren().addAll(version, copyright);

        final Pane rootGroup = new VBox(12);
        rootGroup.getChildren().addAll(inputGridPane);
        rootGroup.setPadding(new Insets(12, 12, 12, 12));
        about.setTitle("About");
        about.setScene(new Scene(rootGroup));
        about.show();
    }

    public void startTheProgram(){
        System.out.println("Program is started ...");
        System.out.println("Interval : " + intervalChooser.getValue());
        System.out.println("Max files in folder: " + maxFilesChooser.getValue());
        System.out.println("Format :" + formatChooser.getValue());
        statusLabel.setText("Running...");

        String temp = "temp";
        String output = "OUTPUT FOLDER DROP";

        Timer photoshopTimer = new Timer();
        Timer sortFilesIntoDestFolderTImer = new Timer();

        photoshopTimer.schedule(new checkForNewPicsFirstTimer(new File(sourceLabel.getText()),mainpath + temp,10), 0,15000);
        photoshopTimer.schedule(new emptyTempFolderTimer(), 0, 15000);

        sortFilesIntoDestFolderTImer.schedule(new checkForNewPicsTimer(new File(mainpath + output), destLabel.getText(), maxFilesChooser.getValue(), formatChooser.getValue()), 0, intervalChooser.getValue() * 1000);

    }

    public void browseSource(){
        System.out.println("Choosing source folder ...");
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose the source folder");
        Stage dialog = new Stage(); // new stage
        dialog.initModality(Modality.APPLICATION_MODAL);
        // Defines a modal window that blocks events from being
        // delivered to any other application window.

        sourceFolder = directoryChooser.showDialog(dialog);
        try {
            sourceLabel.setText(sourceFolder.getCanonicalPath());
            System.out.println(sourceFolder.getCanonicalPath());
        }catch (Exception e){
            System.out.println(e);
        }


    }

    public void browseDestination(){
        System.out.println("Choosing destination folder ...");
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose the destination folder");
        Stage dialog = new Stage(); // new stage
        dialog.initModality(Modality.APPLICATION_MODAL);
        // Defines a modal window that blocks events from being
        // delivered to any other application window.
        destinationFolder = directoryChooser.showDialog(dialog);
        try {
            destLabel.setText(destinationFolder.getCanonicalPath());
            System.out.println(destinationFolder.getCanonicalPath());
        }catch (Exception e){
            System.out.println(e);
        }
    }
}

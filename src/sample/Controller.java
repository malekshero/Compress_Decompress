package sample;


import RLE.RLE;
import RLE.HuffmanComp;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {


    public Button RLEButton;
    public Button Huffman;

    @Override
    public void initialize(URL location, ResourceBundle resources) {


    }

    public void RLEButton(ActionEvent actionEvent) {
        RLE rc=new RLE("");
        //rc.tester("test/s.so", "test/2s.so", true);

        rc.testerTout(true);
    }

    public void HuffmanButton(ActionEvent actionEvent) {
         HuffmanComp h = new HuffmanComp("");
         h.testerTout(true);
    }
}

package sample;


import RLE.RLE;
import RLE.HuffmanComp;
import ZipCompress.ZipDir;
import ZipCompress.ZipFile;
import ZipCompress.ZipFiles;
import adaptiveHuffman.decoder.Decoder;
import adaptiveHuffman.encoder.Encoder;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {


    public Button RLEButton;
    public Button Huffman;
    public Button AdaptiveHuffmanDecode;
    public Button AdaptiveHuffmanEncode;
    public Button ZipFileCompress;
    public Button ZipMultiFiles;
    public Button ZipDirectory;

    @Override
    public void initialize(URL location, ResourceBundle resources) {


    }

    public void RLEButton(ActionEvent actionEvent) {
        RLE rc=new RLE("");
        rc.testerTout(true);
    }

    public void HuffmanButton(ActionEvent actionEvent) {
         HuffmanComp h = new HuffmanComp("");
         h.testerTout(true);
    }

    public void AdaptiveHuffmanDecodeButton(ActionEvent actionEvent) {
        Decoder De = new Decoder();
        De.main();


    }

    public void AdaptiveHuffmanEncodeButton(ActionEvent actionEvent) {
        Encoder encode= new Encoder();
        encode.main();
    }

    public void ZipFileCompressButton(ActionEvent actionEvent) {
        ZipFile zipFile = new ZipFile();
        zipFile.main();
    }

    public void ZipMultiFilesButton(ActionEvent actionEvent) {
        ZipFiles zf = new ZipFiles();
        zf.main();
    }

    public void ZipDirectoryButton(ActionEvent actionEvent) {
        ZipDir zd = new ZipDir();
        zd.main();
    }
}

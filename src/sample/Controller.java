package sample;
import Arithmatic.AdaptiveArithmeticCompress;
import Arithmatic.AdaptiveArithmeticDecompress;
import Arithmatic.ArithmeticCompress;
import Arithmatic.ArithmeticDecompress;
import RLE.RLE;
import RLE.HuffmanComp;
import RLE.blockSortingAlgorithm;
import ZipCompress.ZipDir;
import ZipCompress.ZipFile;
import ZipCompress.ZipFiles;
import adaptiveHuffman.decoder.Decoder;
import adaptiveHuffman.encoder.Encoder;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.io.IOException;
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
    public Button BlockSort;
    public Button AdaptiveEncodeFiles;
    public Button AdaptiveDecodeFiles;
    public Button AdaptiveArithmaticCompress;
    public Button AdaptiveArithmaticDecompress;
    public Button ArithmaticDecompress;
    public Button ArithmaticCompress;

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
        De.decodeFile();


    }
    public void BlockSortButton(ActionEvent actionEvent) {
        blockSortingAlgorithm bsa = new blockSortingAlgorithm();
        bsa.testerTout(true);
    }

    public void AdaptiveHuffmanEncodeButton(ActionEvent actionEvent) {
        Encoder encode= new Encoder();
        encode.encodeFile();
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

    public void AdaptiveEncodeFilesButton(ActionEvent actionEvent) {
        Encoder encode= new Encoder();
        encode.encodeFiles();
    }

    public void AdaptiveDecodeFilesButton(ActionEvent actionEvent) {
        Decoder De = new Decoder();
        De.decodeFiles();
    }

    public void AdaptiveArithmaticCompressButton(ActionEvent actionEvent) throws IOException {
        AdaptiveArithmeticCompress a = new AdaptiveArithmeticCompress();
        a.mains();

    }

    public void AdaptiveArithmaticDecompressButton(ActionEvent actionEvent) throws IOException {
        AdaptiveArithmeticDecompress a = new AdaptiveArithmeticDecompress();
        a.mains();
    }

    public void ArithmaticCompressButton(ActionEvent actionEvent) throws IOException {
        ArithmeticCompress ac = new ArithmeticCompress();
        ac.mains();
    }

    public void ArithmaticDecompressButton(ActionEvent actionEvent) throws IOException {
        ArithmeticDecompress ac = new ArithmeticDecompress();
        ac.mains();
    }
}
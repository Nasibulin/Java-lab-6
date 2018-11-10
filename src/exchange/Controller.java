package exchange;

import com.jfoenix.controls.JFXComboBox;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private static final String CBR_API_URL = "https://www.cbr.ru/scripts/XML_daily.asp";
    private static LocalDate dt;
    @FXML
    JFXComboBox<Currency> jfxComboBox1;
    @FXML
    JFXComboBox<Currency> jfxComboBox2;
    @FXML
    Label rate;
    @FXML
    Label charCode1;
    @FXML
    Label charCode2;
    @FXML
    Label title;
    @FXML
    Label date;
    @FXML
    Label nominal;

    public static List<Currency> getCurrencyList() {
        List<Currency> currencyList = new ArrayList<>();

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new URL(CBR_API_URL).openStream());

            doc.getDocumentElement().normalize();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.MM.yyyy");

            dt = LocalDate.parse(doc.getDocumentElement().getAttribute(
                    "Date"), formatter);

            NodeList nList = doc.getElementsByTagName("Valute");

            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;
                    currencyList.add(new Currency(
                            eElement.getAttribute("ID"),
                            eElement.getElementsByTagName("NumCode").item(0).getTextContent(),
                            eElement.getElementsByTagName("CharCode").item(0).getTextContent(),
                            Double.parseDouble(eElement.getElementsByTagName("Nominal").item(0).getTextContent()),
                            eElement.getElementsByTagName("Name").item(0).getTextContent(),
                            NumberFormat.getInstance(Locale.FRANCE).parse(
                                    eElement.getElementsByTagName("Value").item(0).getTextContent()).doubleValue()
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return currencyList;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        jfxComboBox1.setItems(FXCollections.observableArrayList(getCurrencyList()));
        jfxComboBox1.getSelectionModel().select(2);
        jfxComboBox2.setItems(FXCollections.observableArrayList(getCurrencyList()));
        jfxComboBox2.getSelectionModel().select(10);
        charCode1.setText(jfxComboBox1.getSelectionModel().getSelectedItem().getCharCode());
        charCode2.setText(jfxComboBox2.getSelectionModel().getSelectedItem().getCharCode());

        double arate = jfxComboBox1.getSelectionModel().getSelectedItem().getValue();
        double brate = jfxComboBox2.getSelectionModel().getSelectedItem().getValue();

        double anom = jfxComboBox1.getSelectionModel().getSelectedItem().getValue();
        double bnom = jfxComboBox2.getSelectionModel().getSelectedItem().getValue();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.MM.yyyy");
        date.setText(String.valueOf(dt.format(formatter)));

        rate.setText(String.format("%.4f", arate / brate * anom / bnom));
    }

    public void rate() {

        double arate = jfxComboBox1.getSelectionModel().getSelectedItem().getValue();
        double brate = jfxComboBox2.getSelectionModel().getSelectedItem().getValue();

        double anom = jfxComboBox1.getSelectionModel().getSelectedItem().getValue();
        double bnom = jfxComboBox2.getSelectionModel().getSelectedItem().getValue();

        double tmp = arate / brate * anom / bnom;

        charCode1.setText(jfxComboBox1.getSelectionModel().getSelectedItem().getCharCode());
        charCode2.setText(jfxComboBox2.getSelectionModel().getSelectedItem().getCharCode());

        rate.setText(String.format("%.4f", tmp * normalize(tmp)));
        nominal.setText(String.format("%d", normalize(tmp)));
    }

    public int normalize(double rate) {
        int normalize;
        normalize = (rate == 1) ? 1 : (int) Math.pow(10, (int) Math.log10(10 / rate));
        return normalize;
    }


}

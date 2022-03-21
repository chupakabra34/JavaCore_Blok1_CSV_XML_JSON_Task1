import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * \* Created with IntelliJ IDEA.
 * \* Author: Prekrasnov Sergei
 * \* Date: 19.03.2022
 * \*  ----- group JAVA-27 -----
 * \* Description: Работа с файлами CSV, XML, JSON
 * \
 */
public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileNameCsv = "data.csv";
        String fileNameXml = "data.xml";
        List<Employee> listCsv = parseCSV(columnMapping, fileNameCsv);
        String jsonCsv = listToJson(listCsv);
        writeString(jsonCsv, "data.json");
        List<Employee> listXml = parseXML(fileNameXml);
        String jsonXml = listToJson(listXml);
        writeString(jsonXml, "data2.json");
    }

    /**
     * Парсим CSV -> JSON
     *
     * @param columnMapping
     * @param nameFile
     */
    public static List<Employee> parseCSV(String[] columnMapping, String nameFile) {
        List<Employee> staffCSV = null;
        try (CSVReader csvReader = new CSVReader(new FileReader(nameFile))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader).withMappingStrategy(strategy).build();
            staffCSV = csv.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return staffCSV;
    }

    /**
     * Преобразуем из входящего List в JSON, на выходе готовый JSON для записи
     *
     * @param list
     * @return json
     */
    public static String listToJson(List<Employee> list) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        String json = gson.toJson(list, listType);
        return json;
    }

    /**
     * Записываем готовый результат в файл JSON
     *
     * @param data     - данные для записи
     * @param fileName - имя файла
     */
    public static void writeString(String data, String fileName) {
        try (FileWriter fileOutput = new FileWriter(fileName)) {
            fileOutput.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Парсим XML -> JSON
     *
     * @param fileData - XML file
     * @return staffXML - List с данными для дальнейшей работы
     */
    public static List<Employee> parseXML(String fileData) throws ParserConfigurationException, IOException, SAXException {
        List<Employee> staffXML = new ArrayList<>();
        List<String> elementsStaffXML = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document dataFile = builder.parse(new File(fileData));
        NodeList employeeElements = dataFile.getDocumentElement().getChildNodes();
        for (int i = 0; i < employeeElements.getLength(); i++) {
            Node employeeNode = employeeElements.item(i);
            if (employeeNode.getNodeName().equals("employee")) {
                NodeList nodeEmployee = employeeNode.getChildNodes();
                for (int j = 0; j < nodeEmployee.getLength(); j++) {
                    Node anyEmployee = nodeEmployee.item(j);
                    if (Node.ELEMENT_NODE == anyEmployee.getNodeType()) {
                        elementsStaffXML.add(anyEmployee.getTextContent());
                    }
                }
                staffXML.add(new Employee(Long.parseLong(elementsStaffXML.get(0)), elementsStaffXML.get(1),
                        elementsStaffXML.get(2),
                        elementsStaffXML.get(3),
                        Integer.parseInt(elementsStaffXML.get(4))));
                elementsStaffXML.clear();
            }
        }
        return staffXML;
    }
}
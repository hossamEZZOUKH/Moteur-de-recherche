/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package m2i.mr.indexation;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 *
 * @author hunter
 */
public class Html {
    
    public static void buildXMLFile() {
        SAXBuilder sxb = new SAXBuilder();
        try {
            Element racine = sxb.build("file").getRootElement();
            List<Element> liste0 = racine.getChildren("nom de la balise");//body or head
            for (Element talk : liste0) {
                List<Element> liste1 = talk.getChildren("nom de la balise");
                for (Element element : liste1) {
                        String data = element.getText();
                }
            }
        } catch (IOException | JDOMException e) {
            e.printStackTrace();
        }
    }

}

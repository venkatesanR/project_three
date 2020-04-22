package com.techmania.spboot.validator;

import com.sun.org.apache.xerces.internal.dom.DOMInputImpl;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CustomValidator {
    private Validator validator;
    private JAXBContext jaxbContext;

    public CustomValidator() throws SAXException, JAXBException, IOException {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        schemaFactory.setResourceResolver((new LSResourceResolver() {
            @Override
            public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
                LSInput input = new DOMInputImpl();
                String filePath = "sources/" + systemId;
                System.out.println(filePath);
                InputStream stream = getClass().getClassLoader().getResourceAsStream(filePath);
                input.setPublicId(publicId);
                input.setSystemId(systemId);
                input.setBaseURI(baseURI);
                input.setCharacterStream(new InputStreamReader(stream));
                return input;
            }

        }));
        Schema schema = schemaFactory.newSchema(new StreamSource(getClass()
                .getClassLoader().getResourceAsStream("sources/employees.xsd")));
        validator = schema.newValidator();
        jaxbContext = JAXBContext.newInstance(Employee.class);
    }

    public void validate() throws JAXBException, IOException, SAXException {
        JAXBSource source = new JAXBSource(jaxbContext, new Employee());
        validator.validate(source);
    }

    public static void main(String[] args) throws JAXBException, SAXException, IOException {
        CustomValidator validator = new CustomValidator();
        validator.validate();
    }
}

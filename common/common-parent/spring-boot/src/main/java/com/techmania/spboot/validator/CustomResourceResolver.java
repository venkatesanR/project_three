package com.techmania.spboot.validator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

import java.io.InputStream;

@AllArgsConstructor
@Getter
public class CustomResourceResolver implements LSResourceResolver {
    private final String path;

    public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
        InputStream is = null;
        String resourceName = systemId;
        int index = systemId.lastIndexOf(47);
        if (index != -1) {
            resourceName = systemId.substring(index + 1);
        }
        String resourcePath = path + resourceName;
        is = this.getClass().getClassLoader().getResourceAsStream(resourcePath);
        CustomLSInput ls = new CustomLSInput();
        ls.setByteStream(is);
        ls.setSystemId(systemId);
        return ls;
    }
}

package com.bogovich.xmlparser;
import com.bogovich.groovy.xmlparser.*;

public class XmlParseJava {
    public static void main(String[] args) throws Exception {
        String filePath = "ПФР_7707492166_000_УЗР_20160804_95af1d6c-b629-4c04-b633-acb65e3be7f3.XML";
        ReorganisationXmlParserV2 reorganisationXmlParser = new ReorganisationXmlParserV2(ClassLoader.getSystemClassLoader().getResource(filePath).toURI());
        reorganisationXmlParser.parseReorganisationInfo();
    }
}

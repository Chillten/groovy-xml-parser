package com.bogovich.xmlparser;

public class XmlParseJava {
    public static void main(String[] args) {
        String filePath = "C:/Users/aleksandr.bogovich/Desktop/my staff/practice/groovy-xml-parser/src/main/groovy/com/bogovich/xmlparser/ПФР_7707492166_000_УЗР_20160804_95af1d6c-b629-4c04-b633-acb65e3be7f3.XML";

        XmlPaser xmlPaser = new XmlPaser(filePath);
        xmlPaser.printAllRorg();
    }
}

package com.oye.common.smartkit;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class ReadXml {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        read("E:\\.tp\\test.xml");
    }

    private static void read(String xmlPath) throws ParserConfigurationException, SAXException, IOException {
        DefaultHandler handler = new DefaultHandler() {
            private final StringBuilder elementContent = new StringBuilder();

            @Override
            public void startDocument() {
                log.info("startDocument()");
            }

            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) {
                log.info("startElement({}, {}, {}, {})", uri, localName, qName, attributes);
            }

            @Override
            public void characters(char[] ch, int start, int length) {
                log.error("characters()");
            }

            @Override
            public void endElement(String uri, String localName, String qName) {
                log.info("endElement({}, {}, {})", uri, localName, qName);
            }

            @Override
            public void endDocument() throws SAXException {
                super.endDocument();
            }
        };
        SAXParserFactory.newInstance().newSAXParser().parse(new File(xmlPath), handler);
    }

    @Getter
    @RequiredArgsConstructor
    enum ParseState {
        IDLE(""),
        FAULT_TREES("Fault-Trees"),
        PERF_FAULT("性能故障"),
        FAULT_TREE("Fault-Tree"),
        BASIC_EVENT("Basic-Event"),
        BASIC_EVENT_ID("ID"),
        BASIC_EVENT_NAME_DESC("NameDesc"),
        ;

        public static final List<String> ELEMENT_NAMES = Arrays.stream(values())
            .filter(value -> !value.equals(IDLE))
            .map(ParseState::getElementName)
            .collect(Collectors.toList());

        private final String elementName;
    }
}

package com.bogovich.groovy.xmlparser

import groovy.util.slurpersupport.GPathResult

class ReorganisationXmlParserV2 {
    def root

    ReorganisationXmlParserV2(filePath) {
        this.root = new XmlSlurper(namespaceAware: false).parse(new File(filePath))
    }

    void parseReorganisationInfo() {
        Map result = [:]

        args.each { k, v -> result.put(k, null) }
        args.findAll { it.value['const'] }.each { k, v ->
            result.put(k, parseType(v, injectPath(root?.УЗР, getPath(v))))
        }
        root?.УЗР?.Реорганизация?.Результат?.'*'?.each {
            result.put('I_NPF_TYPE', getNpfType(it?.name()))
            it?.НПФ?.each {
                args.findAll { k, v -> !v['const'] && k != 'I_NPF_TYPE' }.each { k, v ->
                    result.put(k, parseType(v, injectPath(it, getPath(v))))
                }
                println result
            }
        }
    }

    GPathResult injectPath(def root, String path) {
        return path.split(/\./).inject(root) { obj, node -> obj?."$node" } as GPathResult
    }

    def getNpfType(name) {
        switch (name) {
            case 'Созданные': return 'CREATE_NPF'
            case 'ПродолжающиеДеятельность': return 'CONTINUE_NPF'
            case 'ПрекратившиеДеятельность': return 'STOP_NPF'
        }
        return null
    }

    String getPath(list) {
        return list['path'].toString()
    }

    def parseType(type, node) {
        if (type['type'] && node) {
            switch (type['type']) {
                case 'date': return Date.parse("yyyy-MM-dd", node?.text())
                case 'int': return Integer.parseInt(node?.text())
            }
        }
        return node?.text() != '' ? node?.text() : null
    }

    def args = [
            'I_NPF_TYPE'           : [],
            'I_NPF_NAME'           : [path: 'Наименование'],
            'I_NPF_INN'            : [path: 'ИНН', setting: ['body']],
            'I_NPF_OGRN'           : [path: 'ОГРН'],
            'I_NPF_LICENCE_DATE'   : [path: 'НПФ.Лицензия.Дата', type: 'date', const: true],
            'I_NPF_LICENCE_NUM'    : [path: 'НПФ.Лицензия.Номер', const: true],
            'I_RORG_DECISION_DATE' : [path: 'Реорганизация.РешениеБанка.Дата', type: 'date', const: true], //root
            'I_RORG_DECISION_NUM'  : [path: 'Реорганизация.РешениеБанка.Номер', const: true], //root
            'I_RORG_FORM'          : [path: 'Реорганизация.Форма', type: 'int', const: true], //root
            'I_NPF_ADDR_REGION'    : [path: 'Адрес.РоссийскийАдрес.Регион.Название'],
            'I_NPF_ADDR_REGION_S'  : [path: 'Адрес.РоссийскийАдрес.Регион.Сокращение'],
            'I_NPF_ADDR_AREA'      : [path: 'Адрес.РоссийскийАдрес.Район.Название'],
            'I_NPF_ADDR_AREA_S'    : [path: 'Адрес.РоссийскийАдрес.Район.Сокращение'],
            'I_NPF_ADDR_CITY'      : [path: 'Адрес.РоссийскийАдрес.Город.Название'],
            'I_NPF_ADDR_CITY_S'    : [path: 'Адрес.РоссийскийАдрес.Город.Сокращение'],
            'I_NPF_ADDR_LOCALITY'  : [path: 'Адрес.РоссийскийАдрес.НаселенныйПункт.Название'],
            'I_NPF_ADDR_LOCALITY_S': [path: 'Адрес.РоссийскийАдрес.НаселенныйПункт.Сокращение'],
            'I_NPF_ADDR_STREET'    : [path: 'Адрес.РоссийскийАдрес.Улица.Название'],
            'I_NPF_ADDR_STREET_S'  : [path: 'Адрес.РоссийскийАдрес.Улица.Сокращение'],
            'I_NPF_ADDR_HOUSE'     : [path: 'Адрес.РоссийскийАдрес.Дом.Номер'],
            'I_NPF_ADDR_BUILDG'    : [path: 'Адрес.РоссийскийАдрес.Строение.Номер'],
            'I_NPF_ADDR_BLOCK'     : [path: 'Адрес.РоссийскийАдрес.Корпус.Номер'],
            'I_NPF_ADDR_FLAT'      : [path: 'Адрес.РоссийскийАдрес.Квартира.Номер'],
            'I_EIO_NPF_FIRST_NAME' : [path: 'ЕиоНПФ.ФИО.Имя', const: true], //root
            'I_EIO_NPF_MIDDLE_NAME': [path: 'ЕиоНПФ.ФИО.Отчество', const: true], //root
            'I_EIO_NPF_LAST_NAME'  : [path: 'ЕиоНПФ.ФИО.Фамилия', const: true], //root
            'I_EIO_NPF_POSITION'   : [path: 'ЕиоНПФ.Должность', const: true], //root
            'I_EIO_NPF_PHONE_NUM'  : [path: 'ЕиоНПФ.Телефон', const: true], //root
    ]
}

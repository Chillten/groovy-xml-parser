package com.bogovich.xmlparser

import groovy.util.slurpersupport.GPathResult

class XmlPaser {
    def filePath = 'C:/Users/aleksandr.bogovich/Desktop/my staff/practice/groovy-xml-parser/src/main/groovy/com/bogovich/xmlparser/ПФР_7707492166_000_УЗР_20160804_95af1d6c-b629-4c04-b633-acb65e3be7f3.XML'
    def root

    GPathResult injectPath(def root, String path) {
        return path.split(/\./).inject(root) { obj, node -> obj?."$node" } as GPathResult
    }

    XmlPaser() {
        this.root = new XmlSlurper(namespaceAware: false).parse(new File(filePath))
    }

    void printAllRorg() {


        rorg_info.each { k, v ->
            injectPath(root?.УЗР, getPath(v))?.each {
                print "${k} = "
                printNode(it, v)
                if (args.containsKey(k))
                    args.put(k, parseType(v, it))
            }
        }

//        def main_org = args.get('I_NPF_TYPE')

        def needToClean = []

        root?.УЗР?.Реорганизация?.Результат?.'*'?.each {
            needToClean.each {
                args.put(it, null)
            }
            needToClean = []

            println("============")
            println(it?.name())
            args.put('I_NPF_TYPE', it?.name())
            it?.НПФ?.each {
                def node = it
                npfFields.each { k, v ->
                    injectPath(node, getPath(v))?.each {
                        print "${k} = "
                        printNode(it, v)
                        if (args.containsKey(k)) {
                            args.put(k, parseType(v, it))
                            needToClean.add(k)
                        }
                    }
                }
            }
            // Обработка
            print args
        }
    }

    void printNode(node) {
        println String.format('%s - %s', node?.name(), node?.text())
    }

    void printNode(node, row) {
        println String.format('%s - %s', node?.name(), parseType(row, node) )
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
        return node?.text()
    }

    def npfFields = [
            'I_NPF_NAME'           : [path: 'Наименование'],
            'I_NPF_OGRN'           : [path: 'ОГРН'],
            'I_NPF_ADDR_DEX'       : [path: 'Адрес.Индекс'],
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
    ]

    def rorg_info = [
            'I_DOC_SOURCE_INN'        : [path: 'НПФ.ИНН'],
            'I_DOC_SOURCE_OGRN'       : [path: 'НПФ.ОГРН'],
//            'I_DOC_SOURCE_NPF' : [path: 'НПФ.Наименование'],
            'I_DOC_SOURCE_FORMAL_NAME': [path: 'НПФ.НаименованиеФормализованное'],
            'I_NPF_LICENCE_DATE'      : [path: 'НПФ.Лицензия.Дата', type: 'date'],
            'I_NPF_LICENCE_NUM'       : [path: 'НПФ.Лицензия.Номер'],

            'I_RORG_DECISION_DATE'    : [path: 'Реорганизация.РешениеБанка.Дата', type: 'date'],
            'I_RORG_DECISION_NUM'     : [path: 'Реорганизация.РешениеБанка.Номер'],
            'I_RORG_FORM'             : [path: 'Реорганизация.Форма', type: 'int'],

            'I_EIO_NPF_FIRST_NAME'    : [path: 'ЕиоНПФ.ФИО.Имя'],
            'I_EIO_NPF_MIDDLE_NAME'   : [path: 'ЕиоНПФ.ФИО.Отчество'],
            'I_EIO_NPF_LAST_NAME'     : [path: 'ЕиоНПФ.ФИО.Фамилия'],
            'I_EIO_NPF_POSITION'      : [path: 'ЕиоНПФ.Должность'],
            'I_EIO_NPF_PHONE_NUM'     : [path: 'ЕиоНПФ.Телефон'],
    ]

    def args = [
//            'I_DOC_HEAD_ID': null,
'I_DOC_SOURCE_INN'        : null,
'I_DOC_SOURCE_NPF'        : null,
'I_DOC_SOURCE_FORMAL_NAME': null,
//            'I_DOC_TYPE': null,
//            'I_DOC_NUMBER': null,
//            'I_DOC_DATE': null,
//            'I_DOC_NUMBER_ORG': null,
//            'I_DOC_NAME': null,
//            'I_DOC_YEAR': null,
//            'I_DOC_PROGRAM_NAME': null,
//            'I_DOC_PROGRAM_VER': null,
//            'I_DOC_FORMAT_VER': null,
//            'I_DOC_FILE_TYPE': null,
//            'I_DOC_DATA_SOURCE': null,
//            'I_DOC_ENVIRONMENT': null,
//            'I_DOC_REGISTER_TYPE': null,
//            'I_DOC_STATUS': null,
//            'I_DOC_BASE_NAME': null,
//            'I_DOC_BASE_VER': null,
//            'I_DOC_BASE_NUMBER': null,
//            'I_DOC_BASE_DATE': null,
//            'I_DOC_PART_DATE': null,
//            'I_DOC_PEOPLE_QUANTITY': null,
//            'I_DOC_PART_QUANTITY': null,
//            'I_DOC_VEST_METHOD': null,
//            'I_DOC_SUM_SPN': null,
//            'I_DOC_PAYSHEET_NUMBER': null,
//            'I_DOC_PAYSHEET_DATE': null,
//            'I_DOC_OPERATION_TYPE': null,
//            'I_DOC_OPERATION_DATE': null,
//            'I_NPF_DAT_START': null,
//            'I_NPF_DAT_EXPIRE': null,
//            'I_NPF_ACT_OLD': null,
//            'I_NPF_DAT_OLD': null,
//            'I_NPF_COD_NEW': null,
//            'I_CMG_PRF_NAM': null,
'I_NPF_TYPE'              : String,
'I_NPF_NAME'              : null,
'I_NPF_OGRN'              : null,
'I_NPF_LICENCE_DATE'      : null,
'I_NPF_LICENCE_NUM'       : null,
'I_RORG_DECISION_DATE'    : null,
'I_RORG_DECISION_NUM'     : null,
'I_RORG_FORM'             : null,
'I_NPF_ADDR_DEX'          : null,
'I_NPF_ADDR_REGION'       : null,
'I_NPF_ADDR_REGION_S'     : null,
'I_NPF_ADDR_AREA'         : null,
'I_NPF_ADDR_AREA_S'       : null,
'I_NPF_ADDR_CITY'         : null,
'I_NPF_ADDR_CITY_S'       : null,
'I_NPF_ADDR_LOCALITY'     : null,
'I_NPF_ADDR_LOCALITY_S'   : null,
'I_NPF_ADDR_STREET'       : null,
'I_NPF_ADDR_STREET_S'     : null,
'I_NPF_ADDR_HOUSE'        : null,
'I_NPF_ADDR_BUILDG'       : null,
'I_NPF_ADDR_BLOCK'        : null,
'I_NPF_ADDR_FLAT'         : null,
'I_EIO_NPF_FIRST_NAME'    : null,
'I_EIO_NPF_MIDDLE_NAME'   : null,
'I_EIO_NPF_LAST_NAME'     : null,
'I_EIO_NPF_POSITION'      : null,
'I_EIO_NPF_PHONE_NUM'     : null,
    ]
}
package com.angkorteam.mbaas.server.function;
//
//import com.angkorteam.mbaas.plain.enums.TypeEnum;
//import com.angkorteam.mbaas.server.Jdbc;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
//
//import java.util.*;
//

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.EavBooleanTable;
import com.angkorteam.mbaas.model.entity.tables.EavCharacterTable;
import com.angkorteam.mbaas.model.entity.tables.EavDateTable;
import com.angkorteam.mbaas.model.entity.tables.EavDateTimeTable;
import com.angkorteam.mbaas.model.entity.tables.EavDecimalTable;
import com.angkorteam.mbaas.model.entity.tables.EavIntegerTable;
import com.angkorteam.mbaas.model.entity.tables.EavTextTable;
import com.angkorteam.mbaas.model.entity.tables.EavTimeTable;
import com.angkorteam.mbaas.model.entity.tables.EavVarcharTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.AttributePojo;
import com.angkorteam.mbaas.model.entity.tables.records.EavBooleanRecord;
import com.angkorteam.mbaas.model.entity.tables.records.EavCharacterRecord;
import com.angkorteam.mbaas.model.entity.tables.records.EavDateRecord;
import com.angkorteam.mbaas.model.entity.tables.records.EavDateTimeRecord;
import com.angkorteam.mbaas.model.entity.tables.records.EavDecimalRecord;
import com.angkorteam.mbaas.model.entity.tables.records.EavIntegerRecord;
import com.angkorteam.mbaas.model.entity.tables.records.EavTextRecord;
import com.angkorteam.mbaas.model.entity.tables.records.EavTimeRecord;
import com.angkorteam.mbaas.model.entity.tables.records.EavVarcharRecord;
import com.angkorteam.mbaas.plain.enums.TypeEnum;
import com.angkorteam.mbaas.server.Spring;
import com.angkorteam.mbaas.server.bean.System;
import org.jooq.DSLContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Date;
import java.util.Map;

/**
 * Created by Khauv Socheat on 4/17/2016.
 */
public class CommonFunction {
//
////    /**
////     * return true if valid, and resultDocument is updated to right data type
////     *
////     * @param attributeRecords
////     * @param externalDocument
////     * @param resultDocument
////     * @return
////     */
////    public static boolean checkDataTypes(Map<String, Map<String, Object>> attributeRecords, Map<String, Object> externalDocument, Map<String, Object> resultDocument) {
////        if (externalDocument != null && !externalDocument.isEmpty()) {
////            XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
////            DateFormat patternDatetime = new SimpleDateFormat(configuration.getString(Constants.PATTERN_DATETIME));
////            DateFormat patternDate = new SimpleDateFormat(configuration.getString(Constants.PATTERN_DATE));
////            DateFormat patternTime = new SimpleDateFormat(configuration.getString(Constants.PATTERN_TIME));
////            for (Map.Entry<String, Object> item : externalDocument.entrySet()) {
////                if (attributeRecords.containsKey(item.getKey())) {
////                    Object value = item.getValue();
////                    Map<String, Object> attributeRecord = attributeRecords.get(item.getKey());
////                    AttributeTypeEnum externalType = AttributeTypeEnum.parseExternalAttributeType(item.getValue());
////                    AttributeTypeEnum internalType = AttributeTypeEnum.valueOf((String) attributeRecord.get(Jdbc.Attribute.ATTRIBUTE_TYPE));
////                    if (internalType == AttributeTypeEnum.Boolean) {
////                        if (externalType != AttributeTypeEnum.Boolean) {
////                            return false;
////                        }
////                        boolean v = (boolean) item.getValue();
////                        resultDocument.put(item.getKey(), v);
////                    } else if (internalType == AttributeTypeEnum.Byte) {
////                        if (externalType == AttributeTypeEnum.Byte
////                                || externalType == AttributeTypeEnum.Short
////                                || externalType == AttributeTypeEnum.Integer
////                                || externalType == AttributeTypeEnum.Long) {
////                            try {
////                                Number number = (Number) value;
////                                byte v = Byte.valueOf(String.valueOf(number));
////                                resultDocument.put(item.getKey(), v);
////                            } catch (NumberFormatException e) {
////                                return false;
////                            }
////                        } else {
////                            return false;
////                        }
////                    } else if (internalType == AttributeTypeEnum.Short) {
////                        if (externalType == AttributeTypeEnum.Byte
////                                || externalType == AttributeTypeEnum.Short
////                                || externalType == AttributeTypeEnum.Integer
////                                || externalType == AttributeTypeEnum.Long) {
////                            try {
////                                Number number = (Number) value;
////                                short v = Short.valueOf(String.valueOf(number));
////                                resultDocument.put(item.getKey(), v);
////                            } catch (NumberFormatException e) {
////                                return false;
////                            }
////                        } else {
////                            return false;
////                        }
////                    } else if (internalType == AttributeTypeEnum.Integer) {
////                        if (externalType == AttributeTypeEnum.Byte
////                                || externalType == AttributeTypeEnum.Short
////                                || externalType == AttributeTypeEnum.Integer
////                                || externalType == AttributeTypeEnum.Long) {
////                            try {
////                                Number number = (Number) value;
////                                int v = Integer.valueOf(String.valueOf(number));
////                                resultDocument.put(item.getKey(), v);
////                            } catch (NumberFormatException e) {
////                                return false;
////                            }
////                        } else if (externalType == AttributeTypeEnum.Float) {
////                            try {
////                                Float number = (Float) value;
////                                int v = number.intValue();
////                                resultDocument.put(item.getKey(), v);
////                            } catch (NumberFormatException e) {
////                                return false;
////                            }
////                        } else if (externalType == AttributeTypeEnum.Double) {
////                            try {
////                                Double number = (Double) value;
////                                int v = number.intValue();
////                                resultDocument.put(item.getKey(), v);
////                            } catch (NumberFormatException e) {
////                                return false;
////                            }
////                        } else {
////                            return false;
////                        }
////                    } else if (internalType == AttributeTypeEnum.Long) {
////                        if (externalType == AttributeTypeEnum.Byte
////                                || externalType == AttributeTypeEnum.Short
////                                || externalType == AttributeTypeEnum.Integer
////                                || externalType == AttributeTypeEnum.Long) {
////                            try {
////                                Number number = (Number) value;
////                                long v = Long.valueOf(String.valueOf(number));
////                                resultDocument.put(item.getKey(), v);
////                            } catch (NumberFormatException e) {
////                                return false;
////                            }
////                        } else {
////                            return false;
////                        }
////                    } else if (internalType == AttributeTypeEnum.Float) {
////                        if (externalType == AttributeTypeEnum.Float
////                                || externalType == AttributeTypeEnum.Double) {
////                            try {
////                                Number number = (Number) value;
////                                float v = Float.valueOf(String.valueOf(number));
////                                resultDocument.put(item.getKey(), v);
////                            } catch (NumberFormatException e) {
////                                return false;
////                            }
////                        } else {
////                            return false;
////                        }
////                    } else if (internalType == AttributeTypeEnum.Double) {
////                        if (externalType == AttributeTypeEnum.Float
////                                || externalType == AttributeTypeEnum.Double) {
////                            try {
////                                Number number = (Number) value;
////                                double v = Double.valueOf(String.valueOf(number));
////                                resultDocument.put(item.getKey(), v);
////                            } catch (NumberFormatException e) {
////                                return false;
////                            }
////                        } else {
////                            return false;
////                        }
////                    } else if (internalType == AttributeTypeEnum.Character) {
////                        if (externalType == AttributeTypeEnum.String) {
////                            if (((String) value).length() > 1) {
////                                return false;
////                            }
////                            resultDocument.put(item.getKey(), ((String) value).charAt(0));
////                        } else {
////                            if (externalType != AttributeTypeEnum.Character) {
////                                return false;
////                            }
////                            resultDocument.put(item.getKey(), (Character) value);
////                        }
////                    } else if (internalType == AttributeTypeEnum.String) {
////                        if (externalType == AttributeTypeEnum.String) {
////                            if (((String) value).length() > 255) {
////                                return false;
////                            }
////                            resultDocument.put(item.getKey(), (String) value);
////                        } else {
////                            return false;
////                        }
////                    } else if (internalType == AttributeTypeEnum.Text) {
////                        if (externalType != AttributeTypeEnum.String) {
////                            return false;
////                        }
////                        resultDocument.put(item.getKey(), (String) value);
////                    } else if (internalType == AttributeTypeEnum.Time) {
////                        if (item.getValue() instanceof String) {
////                            try {
////                                resultDocument.put(item.getKey(), patternTime.parse((String) value));
////                            } catch (ParseException e) {
////                                return false;
////                            }
////                        } else if (item.getValue() instanceof Date
////                                || item.getValue() instanceof LocalDate
////                                || item.getValue() instanceof LocalTime
////                                || item.getValue() instanceof LocalDateTime
////                                || item.getValue() instanceof org.joda.time.LocalDate
////                                || item.getValue() instanceof org.joda.time.LocalDateTime
////                                || item.getValue() instanceof org.joda.time.LocalTime) {
////                            resultDocument.put(item.getKey(), value);
////                        } else {
////                            return false;
////                        }
////                    } else if (internalType == AttributeTypeEnum.Date) {
////                        if (externalType == AttributeTypeEnum.String) {
////                            try {
////                                resultDocument.put(item.getKey(), patternDate.parse((String) value));
////                            } catch (ParseException e) {
////                                return false;
////                            }
////                        } else if (item.getValue() instanceof Date
////                                || item.getValue() instanceof LocalDate
////                                || item.getValue() instanceof LocalTime
////                                || item.getValue() instanceof LocalDateTime
////                                || item.getValue() instanceof org.joda.time.LocalDate
////                                || item.getValue() instanceof org.joda.time.LocalDateTime
////                                || item.getValue() instanceof org.joda.time.LocalTime) {
////                            resultDocument.put(item.getKey(), value);
////                        } else {
////                            return false;
////                        }
////                    } else if (internalType == AttributeTypeEnum.DateTime) {
////                        if (externalType == AttributeTypeEnum.String) {
////                            try {
////                                resultDocument.put(item.getKey(), patternDatetime.parse((String) value));
////                            } catch (ParseException e) {
////                                return false;
////                            }
////                        } else if (item.getValue() instanceof Date
////                                || item.getValue() instanceof LocalDate
////                                || item.getValue() instanceof LocalTime
////                                || item.getValue() instanceof LocalDateTime
////                                || item.getValue() instanceof org.joda.time.LocalDate
////                                || item.getValue() instanceof org.joda.time.LocalDateTime
////                                || item.getValue() instanceof org.joda.time.LocalTime) {
////                            resultDocument.put(item.getKey(), value);
////                        } else {
////                            return false;
////                        }
////                    }
////                } else {
////                    return false;
////                }
////            }
////        }
////        return true;
////    }
//

    /**
     * return true if valid
     *
     * @param attributeRecords
     * @param externalAttributes
     * @return
     */
    public static boolean ensureAttributes(Map<String, AttributePojo> attributeRecords, Map<String, Object> externalAttributes) {
        if (externalAttributes != null && !externalAttributes.isEmpty()) {
            for (Map.Entry<String, Object> item : externalAttributes.entrySet()) {
                if (!attributeRecords.containsKey(item.getKey())) {
                    return false;
                }
            }
        }
        return true;
    }

    //
//    public static boolean checkDuplication(List<String> name, Map<String, Object> attributes) {
//        if (attributes != null && !attributes.isEmpty()) {
//            for (Map.Entry<String, Object> field : attributes.entrySet()) {
//                if (name.contains(field.getKey())) {
//                    return false;
//                } else {
//                    name.add(field.getKey());
//                }
//            }
//        }
//        return true;
//    }
//
//    public static void cleanEmpty(Map<String, Object> externalAttribute) {
//        if (externalAttribute != null && !externalAttribute.isEmpty()) {
//            List<String> names = new ArrayList<>();
//            externalAttribute.entrySet().stream().filter(item -> item.getValue() == null || "".equals(item.getValue())).forEach(item -> {
//                names.add(item.getKey());
//            });
//            if (!names.isEmpty()) {
//                names.forEach(externalAttribute::remove);
//            }
//        }
//    }
//
    public static void saveEavAttributes(String collectionId, String documentId, Map<String, AttributePojo> attributeRecords, Map<String, Object> eavAttributes) {
        System system = Spring.getBean(System.class);
        DSLContext context = Spring.getBean(DSLContext.class);
        EavTimeTable eavTimeTable = Tables.EAV_TIME.as("eavTimeTable");
        EavDateTable eavDateTable = Tables.EAV_DATE.as("eavDateTable");
        EavDateTimeTable eavDateTimeTable = Tables.EAV_DATE_TIME.as("eavDateTimeTable");
        EavBooleanTable eavBooleanTable = Tables.EAV_BOOLEAN.as("eavBooleanTable");
        EavVarcharTable eavVarcharTable = Tables.EAV_VARCHAR.as("eavVarcharTable");
        EavTextTable eavTextTable = Tables.EAV_TEXT.as("eavTextTable");
        EavIntegerTable eavIntegerTable = Tables.EAV_INTEGER.as("eavIntegerTable");
        EavCharacterTable eavCharacterTable = Tables.EAV_CHARACTER.as("eavCharacterTable");
        EavDecimalTable eavDecimalTable = Tables.EAV_DECIMAL.as("eavDecimalTable");
        if (eavAttributes != null && !eavAttributes.isEmpty()) {
            for (Map.Entry<String, Object> item : eavAttributes.entrySet()) {
                AttributePojo attributeRecord = attributeRecords.get(item.getKey());
                TypeEnum attributeType = TypeEnum.valueOf(attributeRecord.getType());
                String uuid = system.randomUUID();
                // eav time
                if (attributeType == TypeEnum.Time) {
                    EavTimeRecord record = context.newRecord(eavTimeTable);
                    record.setEavTimeId(uuid);
                    record.setAttributeId(attributeRecord.getAttributeId());
                    record.setCollectionId(collectionId);
                    record.setDocumentId(documentId);
                    record.setAttributeType(attributeType.getLiteral());
                    record.setEavValue((Date) item.getValue());
                    record.store();
                    // TODO : SKH Create new document
                }
                // eav date
                if (attributeType == TypeEnum.Date) {
                    EavDateRecord record = context.newRecord(eavDateTable);
                    record.setEavDateId(uuid);
                    record.setAttributeId(attributeRecord.getAttributeId());
                    record.setCollectionId(collectionId);
                    record.setDocumentId(documentId);
                    record.setAttributeType(attributeType.getLiteral());
                    record.setEavValue((Date) item.getValue());
                    record.store();
                }
                // eav datetime
                if (attributeType == TypeEnum.DateTime) {
                    EavDateTimeRecord record = context.newRecord(eavDateTimeTable);
                    record.setEavDateTimeId(uuid);
                    record.setAttributeId(attributeRecord.getAttributeId());
                    record.setCollectionId(collectionId);
                    record.setDocumentId(documentId);
                    record.setAttributeType(attributeType.getLiteral());
                    record.setEavValue((Date) item.getValue());
                    record.store();
                }
                // eav varchar
                if (attributeType == TypeEnum.String) {
                    EavVarcharRecord record = context.newRecord(eavVarcharTable);
                    record.setEavVarcharId(uuid);
                    record.setAttributeId(attributeRecord.getAttributeId());
                    record.setCollectionId(collectionId);
                    record.setDocumentId(documentId);
                    record.setAttributeType(attributeType.getLiteral());
                    record.setEavValue((String) item.getValue());
                    record.store();
                }
                // eav character
                if (attributeType == TypeEnum.Character) {
                    EavCharacterRecord record = context.newRecord(eavCharacterTable);
                    record.setEavCharacterId(uuid);
                    record.setAttributeId(attributeRecord.getAttributeId());
                    record.setCollectionId(collectionId);
                    record.setDocumentId(documentId);
                    record.setAttributeType(attributeType.getLiteral());
                    record.setEavValue((String) item.getValue());
                    record.store();
                }
                // eav decimal
                if (attributeType == TypeEnum.Double) {
                    EavDecimalRecord record = context.newRecord(eavDecimalTable);
                    record.setEavDecimalId(uuid);
                    record.setAttributeId(attributeRecord.getAttributeId());
                    record.setCollectionId(collectionId);
                    record.setDocumentId(documentId);
                    record.setAttributeType(attributeType.getLiteral());
                    record.setEavValue(((Number) item.getValue()).doubleValue());
                    record.store();
                }
                // eav boolean
                if (attributeType == TypeEnum.Boolean) {
                    EavBooleanRecord record = context.newRecord(eavBooleanTable);
                    record.setEavBooleanId(uuid);
                    record.setAttributeId(attributeRecord.getAttributeId());
                    record.setCollectionId(collectionId);
                    record.setDocumentId(documentId);
                    record.setAttributeType(attributeType.getLiteral());
                    record.setEavValue((Boolean) item.getValue());
                    record.store();
                }
                // eav integer
                if (attributeType == TypeEnum.Long) {
                    EavIntegerRecord record = context.newRecord(eavIntegerTable);
                    record.setEavIntegerId(uuid);
                    record.setAttributeId(attributeRecord.getAttributeId());
                    record.setCollectionId(collectionId);
                    record.setDocumentId(documentId);
                    record.setAttributeType(attributeType.getLiteral());
                    record.setEavValue(((Number) item.getValue()).intValue());
                    record.store();
                }
                // eav text
                if (attributeType == TypeEnum.Text) {
                    EavTextRecord record = context.newRecord(eavTextTable);
                    record.setEavTextId(uuid);
                    record.setAttributeId(attributeRecord.getAttributeId());
                    record.setCollectionId(collectionId);
                    record.setDocumentId(documentId);
                    record.setAttributeType(attributeType.getLiteral());
                    record.setEavValue((String) item.getValue());
                    record.store();
                }
            }
        }
    }
//
//    public static List<String> splitNoneWhite(String string) {
//        List<String> split = new ArrayList<>();
//        if (string != null && !"".equals(string)) {
//            for (String item : StringUtils.split(string, ',')) {
//                if (item != null && !"".equals(item)) {
//                    String trimmed = item.trim();
//                    if (!"".equals(trimmed)) {
//                        if (!split.contains(trimmed)) {
//                            split.add(trimmed);
//                        }
//                    }
//                }
//            }
//        }
//        return split;
//    }
}
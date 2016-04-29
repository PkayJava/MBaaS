package com.angkorteam.mbaas.server.function;

import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.records.*;
import com.angkorteam.mbaas.plain.enums.AttributeTypeEnum;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Khauv Socheat on 4/17/2016.
 */
public class CommonFunction {

    /**
     * return true if valid, and resultDocument is updated to right data type
     *
     * @param attributeRecords
     * @param externalDocument
     * @param resultDocument
     * @return
     */
    public static boolean checkDataTypes(Map<String, AttributeRecord> attributeRecords, Map<String, Object> externalDocument, Map<String, Object> resultDocument) {
        if (externalDocument != null && !externalDocument.isEmpty()) {
            XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
            DateFormat patternDatetime = new SimpleDateFormat(configuration.getString(Constants.PATTERN_DATETIME));
            DateFormat patternDate = new SimpleDateFormat(configuration.getString(Constants.PATTERN_DATE));
            DateFormat patternTime = new SimpleDateFormat(configuration.getString(Constants.PATTERN_TIME));
            for (Map.Entry<String, Object> item : externalDocument.entrySet()) {
                if (attributeRecords.containsKey(item.getKey())) {
                    Object value = item.getValue();
                    AttributeRecord attributeRecord = attributeRecords.get(item.getKey());
                    AttributeTypeEnum externalType = AttributeTypeEnum.parseExternalAttributeType(item.getValue());
                    AttributeTypeEnum internalType = AttributeTypeEnum.valueOf(attributeRecord.getAttributeType());
                    if (internalType == AttributeTypeEnum.Boolean) {
                        if (externalType != AttributeTypeEnum.Boolean) {
                            return false;
                        }
                        boolean v = (boolean) item.getValue();
                        resultDocument.put(item.getKey(), v);
                    } else if (internalType == AttributeTypeEnum.Byte) {
                        if (externalType == AttributeTypeEnum.Byte
                                || externalType == AttributeTypeEnum.Short
                                || externalType == AttributeTypeEnum.Integer
                                || externalType == AttributeTypeEnum.Long) {
                            try {
                                Number number = (Number) value;
                                byte v = Byte.valueOf(String.valueOf(number));
                                resultDocument.put(item.getKey(), v);
                            } catch (NumberFormatException e) {
                                return false;
                            }
                        } else {
                            return false;
                        }
                    } else if (internalType == AttributeTypeEnum.Short) {
                        if (externalType == AttributeTypeEnum.Byte
                                || externalType == AttributeTypeEnum.Short
                                || externalType == AttributeTypeEnum.Integer
                                || externalType == AttributeTypeEnum.Long) {
                            try {
                                Number number = (Number) value;
                                short v = Short.valueOf(String.valueOf(number));
                                resultDocument.put(item.getKey(), v);
                            } catch (NumberFormatException e) {
                                return false;
                            }
                        } else {
                            return false;
                        }
                    } else if (internalType == AttributeTypeEnum.Integer) {
                        if (externalType == AttributeTypeEnum.Byte
                                || externalType == AttributeTypeEnum.Short
                                || externalType == AttributeTypeEnum.Integer
                                || externalType == AttributeTypeEnum.Long) {
                            try {
                                Number number = (Number) value;
                                int v = Integer.valueOf(String.valueOf(number));
                                resultDocument.put(item.getKey(), v);
                            } catch (NumberFormatException e) {
                                return false;
                            }
                        } else if (externalType == AttributeTypeEnum.Float) {
                            try {
                                Float number = (Float) value;
                                int v = Integer.valueOf(String.valueOf(number));
                                resultDocument.put(item.getKey(), v);
                            } catch (NumberFormatException e) {
                                return false;
                            }
                        } else if (externalType == AttributeTypeEnum.Double) {
                            try {
                                Double number = (Double) value;
                                int v = Integer.valueOf(String.valueOf(number));
                                resultDocument.put(item.getKey(), v);
                            } catch (NumberFormatException e) {
                                return false;
                            }
                        } else {
                            return false;
                        }
                    } else if (internalType == AttributeTypeEnum.Long) {
                        if (externalType == AttributeTypeEnum.Byte
                                || externalType == AttributeTypeEnum.Short
                                || externalType == AttributeTypeEnum.Integer
                                || externalType == AttributeTypeEnum.Long) {
                            try {
                                Number number = (Number) value;
                                long v = Long.valueOf(String.valueOf(number));
                                resultDocument.put(item.getKey(), v);
                            } catch (NumberFormatException e) {
                                return false;
                            }
                        } else {
                            return false;
                        }
                    } else if (internalType == AttributeTypeEnum.Float) {
                        if (externalType == AttributeTypeEnum.Float
                                || externalType == AttributeTypeEnum.Double) {
                            try {
                                Number number = (Number) value;
                                float v = Float.valueOf(String.valueOf(number));
                                resultDocument.put(item.getKey(), v);
                            } catch (NumberFormatException e) {
                                return false;
                            }
                        } else {
                            return false;
                        }
                    } else if (internalType == AttributeTypeEnum.Double) {
                        if (externalType == AttributeTypeEnum.Float
                                || externalType == AttributeTypeEnum.Double) {
                            try {
                                Number number = (Number) value;
                                double v = Double.valueOf(String.valueOf(number));
                                resultDocument.put(item.getKey(), v);
                            } catch (NumberFormatException e) {
                                return false;
                            }
                        } else {
                            return false;
                        }
                    } else if (internalType == AttributeTypeEnum.Character) {
                        if (externalType == AttributeTypeEnum.String) {
                            if (((String) value).length() > 1) {
                                return false;
                            }
                            resultDocument.put(item.getKey(), ((String) value).charAt(0));
                        } else {
                            if (externalType != AttributeTypeEnum.Character) {
                                return false;
                            }
                            resultDocument.put(item.getKey(), (Character) value);
                        }
                    } else if (internalType == AttributeTypeEnum.String) {
                        if (externalType == AttributeTypeEnum.String) {
                            if (((String) value).length() > 255) {
                                return false;
                            }
                            resultDocument.put(item.getKey(), (String) value);
                        } else {
                            return false;
                        }
                    } else if (internalType == AttributeTypeEnum.Text) {
                        if (externalType != AttributeTypeEnum.String) {
                            return false;
                        }
                        resultDocument.put(item.getKey(), (String) value);
                    } else if (internalType == AttributeTypeEnum.Time) {
                        if (externalType == AttributeTypeEnum.String) {
                            try {
                                resultDocument.put(item.getKey(), patternTime.parse((String) value));
                            } catch (ParseException e) {
                                try {
                                    resultDocument.put(item.getKey(), patternTime.parse((String) value));
                                } catch (ParseException e1) {
                                    return false;
                                }
                            }
                        } else {
                            return false;
                        }
                    } else if (internalType == AttributeTypeEnum.Date) {
                        if (externalType == AttributeTypeEnum.String) {
                            try {
                                resultDocument.put(item.getKey(), patternDate.parse((String) value));
                            } catch (ParseException e) {
                                try {
                                    resultDocument.put(item.getKey(), patternDate.parse((String) value));
                                } catch (ParseException e1) {
                                    return false;
                                }
                            }
                        } else {
                            return false;
                        }
                    } else if (internalType == AttributeTypeEnum.DateTime) {
                        if (externalType == AttributeTypeEnum.String) {
                            try {
                                resultDocument.put(item.getKey(), patternDatetime.parse((String) value));
                            } catch (ParseException e) {
                                return false;
                            }
                        } else {
                            return false;
                        }
                    }
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * return true if valid
     *
     * @param attributeRecords
     * @param externalAttributes
     * @return
     */
    public static boolean ensureAttributes(Map<String, AttributeRecord> attributeRecords, Map<String, Object> externalAttributes) {
        if (externalAttributes != null && !externalAttributes.isEmpty()) {
            for (Map.Entry<String, Object> item : externalAttributes.entrySet()) {
                if (!attributeRecords.containsKey(item.getKey())) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean checkDuplication(List<String> name, Map<String, Object> attributes) {
        if (attributes != null && !attributes.isEmpty()) {
            for (Map.Entry<String, Object> field : attributes.entrySet()) {
                if (name.contains(field.getKey())) {
                    return false;
                } else {
                    name.add(field.getKey());
                }
            }
        }
        return true;
    }

    public static void cleanEmpty(Map<String, Object> externalAttribute) {
        if (externalAttribute != null && !externalAttribute.isEmpty()) {
            List<String> names = new ArrayList<>();
            externalAttribute.entrySet().stream().filter(item -> item.getValue() == null || "".equals(item.getValue())).forEach(item -> {
                names.add(item.getKey());
            });
            if (!names.isEmpty()) {
                names.forEach(externalAttribute::remove);
            }
        }
    }

    public static void saveEavAttributes(String collectionId, String documentId, DSLContext context, Map<String, AttributeRecord> attributeRecords, Map<String, Object> eavAttributes) {
        if (eavAttributes != null && !eavAttributes.isEmpty()) {
            for (Map.Entry<String, Object> item : eavAttributes.entrySet()) {
                AttributeRecord attributeRecord = attributeRecords.get(item.getKey());
                AttributeTypeEnum attributeType = AttributeTypeEnum.valueOf(attributeRecord.getAttributeType());
                String uuid = UUID.randomUUID().toString();
                // eav time
                if (attributeType == AttributeTypeEnum.Time) {
                    EavTimeRecord record = context.newRecord(Tables.EAV_TIME);
                    record.setEavTimeId(uuid);
                    record.setAttributeId(attributeRecord.getAttributeId());
                    record.setCollectionId(collectionId);
                    record.setDocumentId(documentId);
                    record.setAttributeType(attributeType.getLiteral());
                    record.setEavValue((Date) item.getValue());
                    record.store();
                }
                // eav date
                if (attributeType == AttributeTypeEnum.Date) {
                    EavDateRecord record = context.newRecord(Tables.EAV_DATE);
                    record.setEavDateId(uuid);
                    record.setAttributeId(attributeRecord.getAttributeId());
                    record.setCollectionId(collectionId);
                    record.setDocumentId(documentId);
                    record.setAttributeType(attributeType.getLiteral());
                    record.setEavValue((Date) item.getValue());
                    record.store();
                }
                // eav datetime
                if (attributeType == AttributeTypeEnum.DateTime) {
                    EavDateTimeRecord record = context.newRecord(Tables.EAV_DATE_TIME);
                    record.setEavDateTimeId(uuid);
                    record.setAttributeId(attributeRecord.getAttributeId());
                    record.setCollectionId(collectionId);
                    record.setDocumentId(documentId);
                    record.setAttributeType(attributeType.getLiteral());
                    record.setEavValue((Date) item.getValue());
                    record.store();
                }
                // eav varchar
                if (attributeType == AttributeTypeEnum.String) {
                    EavVarcharRecord record = context.newRecord(Tables.EAV_VARCHAR);
                    record.setEavVarcharId(uuid);
                    record.setAttributeId(attributeRecord.getAttributeId());
                    record.setAttributeType(attributeType.getLiteral());
                    record.setCollectionId(collectionId);
                    record.setDocumentId(documentId);
                    record.setEavValue(String.valueOf(item.getValue()));
                    record.store();
                }
                // eav character
                if (attributeType == AttributeTypeEnum.Character) {
                    EavCharacterRecord record = context.newRecord(Tables.EAV_CHARACTER);
                    record.setEavCharacterId(uuid);
                    record.setAttributeId(attributeRecord.getAttributeId());
                    record.setAttributeType(attributeType.getLiteral());
                    record.setCollectionId(collectionId);
                    record.setDocumentId(documentId);
                    record.setEavValue(String.valueOf(item.getValue()));
                    record.store();
                }
                // eav decimal
                if (attributeType == AttributeTypeEnum.Float
                        || attributeType == AttributeTypeEnum.Double) {
                    EavDecimalRecord record = context.newRecord(Tables.EAV_DECIMAL);
                    record.setEavDecimalId(uuid);
                    record.setAttributeId(attributeRecord.getAttributeId());
                    record.setCollectionId(collectionId);
                    record.setDocumentId(documentId);
                    record.setAttributeType(attributeType.getLiteral());
                    record.setEavValue(((Number) item.getValue()).doubleValue());
                    record.store();
                }
                // eav boolean
                if (attributeType == AttributeTypeEnum.Boolean) {
                    EavBooleanRecord record = context.newRecord(Tables.EAV_BOOLEAN);
                    record.setEavBooleanId(uuid);
                    record.setAttributeId(attributeRecord.getAttributeId());
                    record.setAttributeType(attributeType.getLiteral());
                    record.setCollectionId(collectionId);
                    record.setDocumentId(documentId);
                    record.setEavValue((Boolean) item.getValue());
                    record.store();
                }
                // eav integer
                if (attributeType == AttributeTypeEnum.Byte
                        || attributeType == AttributeTypeEnum.Short
                        || attributeType == AttributeTypeEnum.Integer
                        || attributeType == AttributeTypeEnum.Long) {
                    EavIntegerRecord record = context.newRecord(Tables.EAV_INTEGER);
                    record.setEavIntegerId(uuid);
                    record.setAttributeId(attributeRecord.getAttributeId());
                    record.setAttributeType(attributeType.getLiteral());
                    record.setCollectionId(collectionId);
                    record.setDocumentId(documentId);
                    record.setEavValue(((Number) item.getValue()).intValue());
                    record.store();
                }
                // eav text
                if (attributeType == AttributeTypeEnum.Text) {
                    EavTextRecord record = context.newRecord(Tables.EAV_TEXT);
                    record.setEavTextId(uuid);
                    record.setAttributeId(attributeRecord.getAttributeId());
                    record.setAttributeType(attributeType.getLiteral());
                    record.setCollectionId(collectionId);
                    record.setDocumentId(documentId);
                    record.setEavValue((String) item.getValue());
                    record.store();
                }
            }
        }
    }

    public static List<String> splitNoneWhite(String string) {
        List<String> split = new ArrayList<>();
        for (String item : StringUtils.split(string, ',')) {
            if (item != null && !"".equals(item)) {
                String trimmed = item.trim();
                if (!"".equals(trimmed)) {
                    if (!split.contains(trimmed)) {
                        split.add(trimmed);
                    }
                }
            }
        }
        return split;
    }
}
package com.angkorteam.mbaas.server.function;

import com.angkorteam.mbaas.plain.enums.TypeEnum;
import com.angkorteam.mbaas.server.Jdbc;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.util.*;

/**
 * Created by Khauv Socheat on 4/17/2016.
 */
public class CommonFunction {

//    /**
//     * return true if valid, and resultDocument is updated to right data type
//     *
//     * @param attributeRecords
//     * @param externalDocument
//     * @param resultDocument
//     * @return
//     */
//    public static boolean checkDataTypes(Map<String, Map<String, Object>> attributeRecords, Map<String, Object> externalDocument, Map<String, Object> resultDocument) {
//        if (externalDocument != null && !externalDocument.isEmpty()) {
//            XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
//            DateFormat patternDatetime = new SimpleDateFormat(configuration.getString(Constants.PATTERN_DATETIME));
//            DateFormat patternDate = new SimpleDateFormat(configuration.getString(Constants.PATTERN_DATE));
//            DateFormat patternTime = new SimpleDateFormat(configuration.getString(Constants.PATTERN_TIME));
//            for (Map.Entry<String, Object> item : externalDocument.entrySet()) {
//                if (attributeRecords.containsKey(item.getKey())) {
//                    Object value = item.getValue();
//                    Map<String, Object> attributeRecord = attributeRecords.get(item.getKey());
//                    AttributeTypeEnum externalType = AttributeTypeEnum.parseExternalAttributeType(item.getValue());
//                    AttributeTypeEnum internalType = AttributeTypeEnum.valueOf((String) attributeRecord.get(Jdbc.Attribute.ATTRIBUTE_TYPE));
//                    if (internalType == AttributeTypeEnum.Boolean) {
//                        if (externalType != AttributeTypeEnum.Boolean) {
//                            return false;
//                        }
//                        boolean v = (boolean) item.getValue();
//                        resultDocument.put(item.getKey(), v);
//                    } else if (internalType == AttributeTypeEnum.Byte) {
//                        if (externalType == AttributeTypeEnum.Byte
//                                || externalType == AttributeTypeEnum.Short
//                                || externalType == AttributeTypeEnum.Integer
//                                || externalType == AttributeTypeEnum.Long) {
//                            try {
//                                Number number = (Number) value;
//                                byte v = Byte.valueOf(String.valueOf(number));
//                                resultDocument.put(item.getKey(), v);
//                            } catch (NumberFormatException e) {
//                                return false;
//                            }
//                        } else {
//                            return false;
//                        }
//                    } else if (internalType == AttributeTypeEnum.Short) {
//                        if (externalType == AttributeTypeEnum.Byte
//                                || externalType == AttributeTypeEnum.Short
//                                || externalType == AttributeTypeEnum.Integer
//                                || externalType == AttributeTypeEnum.Long) {
//                            try {
//                                Number number = (Number) value;
//                                short v = Short.valueOf(String.valueOf(number));
//                                resultDocument.put(item.getKey(), v);
//                            } catch (NumberFormatException e) {
//                                return false;
//                            }
//                        } else {
//                            return false;
//                        }
//                    } else if (internalType == AttributeTypeEnum.Integer) {
//                        if (externalType == AttributeTypeEnum.Byte
//                                || externalType == AttributeTypeEnum.Short
//                                || externalType == AttributeTypeEnum.Integer
//                                || externalType == AttributeTypeEnum.Long) {
//                            try {
//                                Number number = (Number) value;
//                                int v = Integer.valueOf(String.valueOf(number));
//                                resultDocument.put(item.getKey(), v);
//                            } catch (NumberFormatException e) {
//                                return false;
//                            }
//                        } else if (externalType == AttributeTypeEnum.Float) {
//                            try {
//                                Float number = (Float) value;
//                                int v = number.intValue();
//                                resultDocument.put(item.getKey(), v);
//                            } catch (NumberFormatException e) {
//                                return false;
//                            }
//                        } else if (externalType == AttributeTypeEnum.Double) {
//                            try {
//                                Double number = (Double) value;
//                                int v = number.intValue();
//                                resultDocument.put(item.getKey(), v);
//                            } catch (NumberFormatException e) {
//                                return false;
//                            }
//                        } else {
//                            return false;
//                        }
//                    } else if (internalType == AttributeTypeEnum.Long) {
//                        if (externalType == AttributeTypeEnum.Byte
//                                || externalType == AttributeTypeEnum.Short
//                                || externalType == AttributeTypeEnum.Integer
//                                || externalType == AttributeTypeEnum.Long) {
//                            try {
//                                Number number = (Number) value;
//                                long v = Long.valueOf(String.valueOf(number));
//                                resultDocument.put(item.getKey(), v);
//                            } catch (NumberFormatException e) {
//                                return false;
//                            }
//                        } else {
//                            return false;
//                        }
//                    } else if (internalType == AttributeTypeEnum.Float) {
//                        if (externalType == AttributeTypeEnum.Float
//                                || externalType == AttributeTypeEnum.Double) {
//                            try {
//                                Number number = (Number) value;
//                                float v = Float.valueOf(String.valueOf(number));
//                                resultDocument.put(item.getKey(), v);
//                            } catch (NumberFormatException e) {
//                                return false;
//                            }
//                        } else {
//                            return false;
//                        }
//                    } else if (internalType == AttributeTypeEnum.Double) {
//                        if (externalType == AttributeTypeEnum.Float
//                                || externalType == AttributeTypeEnum.Double) {
//                            try {
//                                Number number = (Number) value;
//                                double v = Double.valueOf(String.valueOf(number));
//                                resultDocument.put(item.getKey(), v);
//                            } catch (NumberFormatException e) {
//                                return false;
//                            }
//                        } else {
//                            return false;
//                        }
//                    } else if (internalType == AttributeTypeEnum.Character) {
//                        if (externalType == AttributeTypeEnum.String) {
//                            if (((String) value).length() > 1) {
//                                return false;
//                            }
//                            resultDocument.put(item.getKey(), ((String) value).charAt(0));
//                        } else {
//                            if (externalType != AttributeTypeEnum.Character) {
//                                return false;
//                            }
//                            resultDocument.put(item.getKey(), (Character) value);
//                        }
//                    } else if (internalType == AttributeTypeEnum.String) {
//                        if (externalType == AttributeTypeEnum.String) {
//                            if (((String) value).length() > 255) {
//                                return false;
//                            }
//                            resultDocument.put(item.getKey(), (String) value);
//                        } else {
//                            return false;
//                        }
//                    } else if (internalType == AttributeTypeEnum.Text) {
//                        if (externalType != AttributeTypeEnum.String) {
//                            return false;
//                        }
//                        resultDocument.put(item.getKey(), (String) value);
//                    } else if (internalType == AttributeTypeEnum.Time) {
//                        if (item.getValue() instanceof String) {
//                            try {
//                                resultDocument.put(item.getKey(), patternTime.parse((String) value));
//                            } catch (ParseException e) {
//                                return false;
//                            }
//                        } else if (item.getValue() instanceof Date
//                                || item.getValue() instanceof LocalDate
//                                || item.getValue() instanceof LocalTime
//                                || item.getValue() instanceof LocalDateTime
//                                || item.getValue() instanceof org.joda.time.LocalDate
//                                || item.getValue() instanceof org.joda.time.LocalDateTime
//                                || item.getValue() instanceof org.joda.time.LocalTime) {
//                            resultDocument.put(item.getKey(), value);
//                        } else {
//                            return false;
//                        }
//                    } else if (internalType == AttributeTypeEnum.Date) {
//                        if (externalType == AttributeTypeEnum.String) {
//                            try {
//                                resultDocument.put(item.getKey(), patternDate.parse((String) value));
//                            } catch (ParseException e) {
//                                return false;
//                            }
//                        } else if (item.getValue() instanceof Date
//                                || item.getValue() instanceof LocalDate
//                                || item.getValue() instanceof LocalTime
//                                || item.getValue() instanceof LocalDateTime
//                                || item.getValue() instanceof org.joda.time.LocalDate
//                                || item.getValue() instanceof org.joda.time.LocalDateTime
//                                || item.getValue() instanceof org.joda.time.LocalTime) {
//                            resultDocument.put(item.getKey(), value);
//                        } else {
//                            return false;
//                        }
//                    } else if (internalType == AttributeTypeEnum.DateTime) {
//                        if (externalType == AttributeTypeEnum.String) {
//                            try {
//                                resultDocument.put(item.getKey(), patternDatetime.parse((String) value));
//                            } catch (ParseException e) {
//                                return false;
//                            }
//                        } else if (item.getValue() instanceof Date
//                                || item.getValue() instanceof LocalDate
//                                || item.getValue() instanceof LocalTime
//                                || item.getValue() instanceof LocalDateTime
//                                || item.getValue() instanceof org.joda.time.LocalDate
//                                || item.getValue() instanceof org.joda.time.LocalDateTime
//                                || item.getValue() instanceof org.joda.time.LocalTime) {
//                            resultDocument.put(item.getKey(), value);
//                        } else {
//                            return false;
//                        }
//                    }
//                } else {
//                    return false;
//                }
//            }
//        }
//        return true;
//    }

    /**
     * return true if valid
     *
     * @param attributeRecords
     * @param externalAttributes
     * @return
     */
    public static boolean ensureAttributes(Map<String, Map<String, Object>> attributeRecords, Map<String, Object> externalAttributes) {
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

    public static void saveEavAttributes(JdbcTemplate jdbcTemplate, String collectionId, String documentId, Map<String, Map<String, Object>> attributeRecords, Map<String, Object> eavAttributes) {
        if (eavAttributes != null && !eavAttributes.isEmpty()) {
            for (Map.Entry<String, Object> item : eavAttributes.entrySet()) {
                Map<String, Object> attributeRecord = attributeRecords.get(item.getKey());
                TypeEnum attributeType = TypeEnum.valueOf((String) attributeRecord.get(Jdbc.Attribute.ATTRIBUTE_TYPE));
                String uuid = UUID.randomUUID().toString();
                // eav time
                if (attributeType == TypeEnum.Time) {
                    Map<String, Object> fields = new HashMap<>();
                    fields.put(Jdbc.EavTime.EAV_TIME_ID, uuid);
                    fields.put(Jdbc.EavTime.ATTRIBUTE_ID, attributeRecord.get(Jdbc.Attribute.ATTRIBUTE_ID));
                    fields.put(Jdbc.EavTime.COLLECTION_ID, collectionId);
                    fields.put(Jdbc.EavTime.DOCUMENT_ID, documentId);
                    fields.put(Jdbc.EavTime.ATTRIBUTE_TYPE, attributeType.getLiteral());
                    fields.put(Jdbc.EavTime.EAV_VALUE, item.getValue());
                    SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
                    jdbcInsert.withTableName(Jdbc.EAV_TIME);
                    jdbcInsert.execute(fields);
                }
                // eav date
                if (attributeType == TypeEnum.Date) {
                    Map<String, Object> fields = new HashMap<>();
                    fields.put(Jdbc.EavDate.EAV_DATE_ID, uuid);
                    fields.put(Jdbc.EavDate.ATTRIBUTE_ID, attributeRecord.get(Jdbc.Attribute.ATTRIBUTE_ID));
                    fields.put(Jdbc.EavDate.COLLECTION_ID, collectionId);
                    fields.put(Jdbc.EavDate.DOCUMENT_ID, documentId);
                    fields.put(Jdbc.EavDate.ATTRIBUTE_TYPE, attributeType.getLiteral());
                    fields.put(Jdbc.EavDate.EAV_VALUE, item.getValue());
                    SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
                    jdbcInsert.withTableName(Jdbc.EAV_DATE);
                    jdbcInsert.execute(fields);
                }
                // eav datetime
                if (attributeType == TypeEnum.DateTime) {
                    Map<String, Object> fields = new HashMap<>();
                    fields.put(Jdbc.EavDateTime.EAV_DATE_TIME_ID, uuid);
                    fields.put(Jdbc.EavDateTime.ATTRIBUTE_ID, attributeRecord.get(Jdbc.Attribute.ATTRIBUTE_ID));
                    fields.put(Jdbc.EavDateTime.COLLECTION_ID, collectionId);
                    fields.put(Jdbc.EavDateTime.DOCUMENT_ID, documentId);
                    fields.put(Jdbc.EavDateTime.ATTRIBUTE_TYPE, attributeType.getLiteral());
                    fields.put(Jdbc.EavDateTime.EAV_VALUE, item.getValue());
                    SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
                    jdbcInsert.withTableName(Jdbc.EAV_DATE_TIME);
                    jdbcInsert.execute(fields);
                }
                // eav varchar
                if (attributeType == TypeEnum.String) {
                    Map<String, Object> fields = new HashMap<>();
                    fields.put(Jdbc.EavVarchar.EAV_VARCHAR_ID, uuid);
                    fields.put(Jdbc.EavVarchar.ATTRIBUTE_ID, attributeRecord.get(Jdbc.Attribute.ATTRIBUTE_ID));
                    fields.put(Jdbc.EavVarchar.COLLECTION_ID, collectionId);
                    fields.put(Jdbc.EavVarchar.DOCUMENT_ID, documentId);
                    fields.put(Jdbc.EavVarchar.ATTRIBUTE_TYPE, attributeType.getLiteral());
                    fields.put(Jdbc.EavVarchar.EAV_VALUE, item.getValue());
                    SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
                    jdbcInsert.withTableName(Jdbc.EAV_VARCHAR);
                    jdbcInsert.execute(fields);
                }
                // eav character
                if (attributeType == TypeEnum.Character) {
                    Map<String, Object> fields = new HashMap<>();
                    fields.put(Jdbc.EavCharacter.EAV_CHARACTER_ID, uuid);
                    fields.put(Jdbc.EavCharacter.ATTRIBUTE_ID, attributeRecord.get(Jdbc.Attribute.ATTRIBUTE_ID));
                    fields.put(Jdbc.EavCharacter.COLLECTION_ID, collectionId);
                    fields.put(Jdbc.EavCharacter.DOCUMENT_ID, documentId);
                    fields.put(Jdbc.EavCharacter.ATTRIBUTE_TYPE, attributeType.getLiteral());
                    fields.put(Jdbc.EavCharacter.EAV_VALUE, item.getValue());
                    SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
                    jdbcInsert.withTableName(Jdbc.EAV_CHARACTER);
                    jdbcInsert.execute(fields);
                }
                // eav decimal
                if (attributeType == TypeEnum.Double) {
                    Map<String, Object> fields = new HashMap<>();
                    fields.put(Jdbc.EavDecimal.EAV_DECIMAL_ID, uuid);
                    fields.put(Jdbc.EavDecimal.ATTRIBUTE_ID, attributeRecord.get(Jdbc.Attribute.ATTRIBUTE_ID));
                    fields.put(Jdbc.EavDecimal.COLLECTION_ID, collectionId);
                    fields.put(Jdbc.EavDecimal.DOCUMENT_ID, documentId);
                    fields.put(Jdbc.EavDecimal.ATTRIBUTE_TYPE, attributeType.getLiteral());
                    fields.put(Jdbc.EavDecimal.EAV_VALUE, ((Number) item.getValue()).doubleValue());
                    SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
                    jdbcInsert.withTableName(Jdbc.EAV_DECIMAL);
                    jdbcInsert.execute(fields);
                }
                // eav boolean
                if (attributeType == TypeEnum.Boolean) {
                    Map<String, Object> fields = new HashMap<>();
                    fields.put(Jdbc.EavBoolean.EAV_BOOLEAN_ID, uuid);
                    fields.put(Jdbc.EavBoolean.ATTRIBUTE_ID, attributeRecord.get(Jdbc.Attribute.ATTRIBUTE_ID));
                    fields.put(Jdbc.EavBoolean.COLLECTION_ID, collectionId);
                    fields.put(Jdbc.EavBoolean.DOCUMENT_ID, documentId);
                    fields.put(Jdbc.EavBoolean.ATTRIBUTE_TYPE, attributeType.getLiteral());
                    fields.put(Jdbc.EavBoolean.EAV_VALUE, item.getValue());
                    SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
                    jdbcInsert.withTableName(Jdbc.EAV_BOOLEAN);
                    jdbcInsert.execute(fields);
                }
                // eav integer
                if (attributeType == TypeEnum.Long) {
                    Map<String, Object> fields = new HashMap<>();
                    fields.put(Jdbc.EavInteger.EAV_INTEGER_ID, uuid);
                    fields.put(Jdbc.EavInteger.ATTRIBUTE_ID, attributeRecord.get(Jdbc.Attribute.ATTRIBUTE_ID));
                    fields.put(Jdbc.EavInteger.COLLECTION_ID, collectionId);
                    fields.put(Jdbc.EavInteger.DOCUMENT_ID, documentId);
                    fields.put(Jdbc.EavInteger.ATTRIBUTE_TYPE, attributeType.getLiteral());
                    fields.put(Jdbc.EavInteger.EAV_VALUE, ((Number) item.getValue()).intValue());
                    SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
                    jdbcInsert.withTableName(Jdbc.EAV_INTEGER);
                    jdbcInsert.execute(fields);
                }
                // eav text
                if (attributeType == TypeEnum.Text) {
                    Map<String, Object> fields = new HashMap<>();
                    fields.put(Jdbc.EavText.EAV_TEXT_ID, uuid);
                    fields.put(Jdbc.EavText.ATTRIBUTE_ID, attributeRecord.get(Jdbc.Attribute.ATTRIBUTE_ID));
                    fields.put(Jdbc.EavText.COLLECTION_ID, collectionId);
                    fields.put(Jdbc.EavText.DOCUMENT_ID, documentId);
                    fields.put(Jdbc.EavText.ATTRIBUTE_TYPE, attributeType.getLiteral());
                    fields.put(Jdbc.EavText.EAV_VALUE, item.getValue());
                    SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
                    jdbcInsert.withTableName(Jdbc.EAV_TEXT);
                    jdbcInsert.execute(fields);
                }
            }
        }
    }

    public static List<String> splitNoneWhite(String string) {
        List<String> split = new ArrayList<>();
        if (string != null && !"".equals(string)) {
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
        }
        return split;
    }
}
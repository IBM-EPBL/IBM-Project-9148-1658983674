import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
public class ObjectInfo {
    public static String toString(Object obj) throws Exception{
        Class cls=obj.getClass();
        //Class name
        String className=cls.getSimpleName();
        //Non-Static fields
        Field[] fieldsArr=cls.getDeclaredFields();
        Set<Field> fields=new HashSet<>();
        storeNonStaticFields(fields,fieldsArr);
        Set<String> fieldNames=new HashSet<>();
        storeFieldNames(fieldNames,fields);
        //fieldName-field Type
        Set<String> fieldToTypeSet=new HashSet<>();
        storeFieldTypes(fieldToTypeSet,fields);
        //FieldName - value
        Map<String,String> fieldTypeToValueSet=new HashMap<>();
        storeFieldValues(obj,fieldTypeToValueSet,fields);
        //Final info
        StringBuilder info=new StringBuilder();
        info.append(className).append("\n");
        info.append(fieldNames).append("\n");
        info.append(fieldToTypeSet).append("\n");
        info.append(fieldTypeToValueSet).append("\n");
        return info.toString();
    }
    private static void storeFieldValues(Object obj,Map<String,String> fieldToValueSet,Set<Field> fields) throws Exception {
        for(Field field:fields){
            Class fieldType=field.getType();
            String fieldName=field.getName();
            field.setAccessible(true);
            Object fieldValue;
            try {
                fieldValue = field.get(obj);
            }
            catch (IllegalAccessException e){
                throw new RuntimeException(e);
            }
            if(fieldType.isPrimitive() || fieldType.getName().equals("java.lang.String")){
                fieldToValueSet.put(fieldName,fieldValue+"");
            }
            else if(fieldType.isArray()){
                Map<String,String> arrayElementsToValueSet=new HashMap<>();
                Object[] arr=(Object[])fieldValue;
                if(arr.length>0) {
                    Class arrFieldClass = arr[0].getClass();
                    Field[] arrFields = arrFieldClass.getDeclaredFields();
                    Set<Field> arrFieldsSet = new HashSet<>();
                    storeNonStaticFields(arrFieldsSet, arrFields);
                    for (int i = 0; i < 15 && i < arr.length; i++) {
                        Map<String, String> currElementToValueSet = new HashMap<>();
                        storeFieldValues(arr[i], currElementToValueSet, arrFieldsSet);
                        arrayElementsToValueSet.put((i + 1)+"", currElementToValueSet.toString());
                    }
                }
                fieldToValueSet.put(fieldName,arrayElementsToValueSet.toString());
            }
            else{
                Map<String,String> subFieldsToValueSet=new HashMap<>();
                Class subFieldClass=Class.forName(field.getType().getName());
                Field[] subFields=subFieldClass.getDeclaredFields();
                Set<Field> subFieldsSet=new HashSet<>();
                storeNonStaticFields(subFieldsSet,subFields);
                storeFieldValues(fieldValue,subFieldsToValueSet,subFieldsSet);
                fieldToValueSet.put(fieldName,subFieldsToValueSet.toString());
            }
        }
    }
    private static void storeFieldTypes(Set<String> fieldToTypeSet,Set<Field> fields) throws Exception {
        for(Field field:fields){
            String fieldName=field.getName();
            String fieldClassName=field.getType().getName();
            if (field.getType().isPrimitive()) {
                fieldToTypeSet.add(fieldName+":"+field.getType().getSimpleName());
            }
            else {
                //to avoid package name
                Class cls = Class.forName(fieldClassName);
                fieldToTypeSet.add(fieldName + ":" + cls.getSimpleName());
            }
        }
    }
    private static void storeFieldNames(Set<String> fieldNames,Set<Field> fields){
        for(Field field:fields){
            fieldNames.add(field.getName());
        }
    }
    private static void storeNonStaticFields(Set<Field> nonStaticFields,Field[] fields){
        for(Field field:fields){
            if(!isStaticField(field)){
                nonStaticFields.add(field);
            }
        }
    }
    private static boolean isStaticField(Field field){
        return Modifier.isStatic(field.getModifiers());
    }
}
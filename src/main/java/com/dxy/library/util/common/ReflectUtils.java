package com.dxy.library.util.common;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 反射工具类
 * @author duanxinyuan
 * 2015-01-16 20:43
 */
@Slf4j
public class ReflectUtils {

    //类和属性的缓存
    private static final Map<Class<?>, List<Field>> declaredFieldsCache = new ConcurrentHashMap<>(256);

    /**
     * 设置属性可见
     */
    public static void setAccessible(Field field) {
        if ((!Modifier.isPublic(field.getModifiers())
                || !Modifier.isPublic(field.getDeclaringClass().getModifiers())
                || Modifier.isFinal(field.getModifiers()))
                && !field.isAccessible()) {
            field.setAccessible(true);
        }
    }

    /**
     * 设置方法可见
     */
    public static void setAccessible(Method method) {
        if ((!Modifier.isPublic(method.getModifiers()) ||
                !Modifier.isPublic(method.getDeclaringClass().getModifiers()))
                && !method.isAccessible()) {
            method.setAccessible(true);
        }
    }

    /**
     * 设置构造方法可见
     */
    public static void setAccessible(Constructor<?> ctor) {
        if ((!Modifier.isPublic(ctor.getModifiers())
                || !Modifier.isPublic(ctor.getDeclaringClass().getModifiers()))
                && !ctor.isAccessible()) {
            ctor.setAccessible(true);
        }
    }

    /**
     * 合并一个对象的属性到另一个对象（不为空就合并，集合类型会自动合并）
     */
    public static <P, T> T union(P source, T target) {
        Objects.requireNonNull(source);
        Objects.requireNonNull(target);
        List<Field> sourceFields = getFields(source.getClass());
        List<String> sourceFieldNames = sourceFields.stream().map(Field::getName).collect(Collectors.toList());
        sourceFieldNames.forEach(name -> {
            Object fieldValue = null;
            try {
                fieldValue = getFieldValue(source, name);
            } catch (Exception e) {
                log.warn("the field of source is not exists, source: {}, field: {}", source.getClass().getName(), name);
            }
            if (fieldValue != null) {
                try {
                    setFieldValue(target, name, fieldValue);
                } catch (Exception e) {
                    log.warn("the field of target is not exists, target: {}, field: {}", target.getClass().getName(), name);
                }
            }
        });
        return target;
    }

    /**
     * 将某个数组里面的对象转换为另一个对象的list返回
     */
    public static <P, T> List<T> copy(P[] ps, Class<T> cls) {
        Objects.requireNonNull(ps);
        List<T> newList = new ArrayList<>();
        for (P p : ps) {
            newList.add(ReflectUtils.copy(p, cls));
        }
        return newList;
    }

    /**
     * 将某个List里面的对象转换为另一个对象的list返回
     */
    public static <P, T> List<T> copy(List<P> ps, Class<T> cls) {
        Objects.requireNonNull(ps);
        List<T> newList = new ArrayList<>();
        for (P p : ps) {
            newList.add(ReflectUtils.copy(p, cls));
        }
        return newList;
    }

    /**
     * 将某个对象转换为另外一个类型的对象
     * @param source 要转换的对象
     * @param cls 转换成为的类型
     */
    public static <P, T> T copy(P source, Class<T> cls) {
        Objects.requireNonNull(source);
        //创建一个对象
        T t = ClassUtils.instantiateClass(cls);
        return copy(source, t);
    }

    /**
     * 复制一个对象到另一个对象（所有属性覆盖）
     */
    public static <P, T> T copy(P source, T target) {
        Objects.requireNonNull(source);
        Objects.requireNonNull(target);
        List<String> sourceFieldNames = getKeyNames(source);
        sourceFieldNames.forEach(name -> setFieldValue(target, name, getFieldValue(source, name)));
        return target;
    }

    public static List<String> getKeyNames(Object obj) {
        if (obj instanceof Map) {
            Map<Object, Object> map = (Map) obj;
            return map.keySet().stream()
                    .filter(item -> item instanceof String)
                    .map(String::valueOf)
                    .collect(Collectors.toList());
        } else {
            // 排查 static、transient 字段
            return getFields(obj.getClass())
                    .stream()
                    .filter(field -> {
                        int modifier = field.getModifiers();
                        return !Modifier.isStatic(modifier) && !Modifier.isTransient(modifier);
                    })
                    .map(Field::getName)
                    .collect(Collectors.toList());
        }
    }

    /**
     * 获取元素值
     */
    public static <P> Object getFieldValue(P obj, String name) {
        try {
            if (obj == null) {
                return null;
            }
            if (obj instanceof Map) {
                Map map = (Map) obj;
                return map.get(name);
            }
            Field field = getField(obj.getClass(), name);
            if (field == null) {
                return invokeGet(obj, name);
            } else {
                return field.get(obj);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据deep路径获取参数值
     * 1. {"a": "b"}, "a"
     * 2. {"person": {"name": "JK"}} ==> person.name
     * @param obj 对象
     * @param deep field路径
     * @return 相应field值
     */
    public static Object deepField(Object obj, String deep) {
        if (StringUtils.isEmpty(deep)) {
            return obj;
        }
        Object res = obj;
        String[] items = deep.split("\\.");
        for (String item : items) {
            res = getFieldValue(res, item);
            if (res == null) {
                return null;
            }
        }
        return res;
    }

    /**
     * 设置某个对象的某个值
     * @param obj 被设置的对象
     * @param name 对象对应的属性名称
     * @param value 设置对应的值
     */
    public static <P> void setFieldValue(P obj, String name, Object value) {
        if (obj != null && obj instanceof Map) {
            Map map = (Map) obj;
            map.put(name, value);
            return;
        }
        Field field = getField(obj.getClass(), name);
        if (null != field) {
            try {
                Class<?> type = field.getType();
                if (null == value && !type.isPrimitive()) {
                    field.set(obj, null);
                } else {
                    if (type == Short.class && !(value instanceof Short)) {
                        value = Short.parseShort(String.valueOf(value));
                    } else if (type == Integer.class && !(value instanceof Integer)) {
                        value = Integer.parseInt(String.valueOf(value));
                    } else if (type == Long.class && !(value instanceof Long)) {
                        value = Long.parseLong(String.valueOf(value));
                    } else if (type == Float.class && !(value instanceof Float)) {
                        value = Float.parseFloat(String.valueOf(value));
                    } else if (type == Double.class && !(value instanceof Double)) {
                        value = Double.parseDouble(String.valueOf(value));
                    } else if (type == Boolean.class && !(value instanceof Boolean)) {
                        value = BooleanUtils.toBoolean(String.valueOf(value));
                    } else if (type == BigDecimal.class && !(value instanceof BigDecimal)) {
                        value = new BigDecimal(String.valueOf(value));
                    } else if (type == String.class && !(value instanceof String)) {
                        value = String.valueOf(value);
                    }
                    field.set(obj, value);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        } else {
            invokeSet(obj, name, value);
        }
    }

    /**
     * 获取元素值
     */
    private static Field getField(Class cls, String name) {
        if (cls == null) {
            return null;
        }
        try {
            Field field = cls.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            return getField(cls.getSuperclass(), name);
        }
    }

    /**
     * 获取类的全部属性（包括所有父类的）
     */
    public static List<Field> getFields(Class<?> type) {
        List<Field> cacheFields = declaredFieldsCache.get(type);
        if (cacheFields != null) {
            return cacheFields;
        } else {
            List<Field> fields = Lists.newArrayList();
            Collections.addAll(fields, type.getDeclaredFields());
            if (null != type.getSuperclass()) {
                fields.addAll(getFields(type.getSuperclass()));
            }
            List<Field> result = fields.stream().filter(field -> !"serialVersionUID".equals(field.getName())).collect(Collectors.toList());
            declaredFieldsCache.put(type, result);
            return result;
        }
    }

    /**
     * 执行set方法
     * @param obj 执行对象
     * @param fieldName 属性
     * @param value 值
     */
    public static <P> void invokeSet(P obj, String fieldName, Object value) {
        try {
            Class<?>[] parameterTypes = new Class<?>[1];
            parameterTypes[0] = obj.getClass().getDeclaredField(fieldName).getType();
            String sb = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            Method method = obj.getClass().getMethod(sb, parameterTypes);
            method.invoke(obj, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 执行get方法
     * @param obj 执行对象
     * @param fieldName 属性
     */
    public static <P> Object invokeGet(P obj, String fieldName) {
        try {
            String sb = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            Method method = obj.getClass().getMethod(sb);
            return method.invoke(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Method findMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) {
        try {
            return clazz.getMethod(methodName, paramTypes);
        } catch (NoSuchMethodException ex) {
            return findDeclaredMethod(clazz, methodName, paramTypes);
        }
    }

    public static Method findDeclaredMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) {
        try {
            return clazz.getDeclaredMethod(methodName, paramTypes);
        } catch (NoSuchMethodException ex) {
            if (clazz.getSuperclass() != null) {
                return findDeclaredMethod(clazz.getSuperclass(), methodName, paramTypes);
            }
            return null;
        }
    }


    /**
     * 解析 getMethodName -> propertyName
     * @param getMethodName 需要解析的
     * @return 返回解析后的字段名称
     */
    public static String resolveFieldName(String getMethodName) {
        if (getMethodName.startsWith("get")) {
            getMethodName = getMethodName.substring(3);
        } else if (getMethodName.startsWith("is")) {
            getMethodName = getMethodName.substring(2);
        }
        // 小写第一个字母
        return StringUtils.firstToLowerCase(getMethodName);
    }

}

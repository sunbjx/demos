package com.sunbjx.demos.framework.tools.reflect;

import com.esotericsoftware.reflectasm.MethodAccess;
import net.sf.cglib.beans.BeanCopier;
import net.sf.cglib.beans.BeanMap;
import net.sf.cglib.core.Converter;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 字节码操作JavaBean，属性复制及Bean和Map转换。
 *
 * @author sunbjx
 * @since 2018/6/12 16:43
 */
public class BeanCopyUtils {

    /**
     * 缓存BeanCopier
     */
    private static Map<String, BeanCopier> copierCache = new ConcurrentHashMap<String, BeanCopier>();
    private static Map<String, BeanMap> beanMapCache = new ConcurrentHashMap<String, BeanMap>();

    private static final BeanCopyUtils instance = new BeanCopyUtils();

    private BeanCopyUtils() {

    }

    /**
     * 获得BeanCopierFactory单例
     */
    public static BeanCopyUtils get() {
        return instance;
    }

    /**
     * 创建JavaBean属性复制器，使用CGLIB实现
     *
     * @param sourceClass 源类
     * @param targetClass 目标类
     * @return net.sf.cglib.beans.BeanCopier 实例
     */
    public BeanCopier createBeanCopier(Class<?> sourceClass, Class<?> targetClass) {
        return createBeanCopier(sourceClass, targetClass, false);
    }

    /**
     * 创建JavaBean属性复制器，使用CGLIB实现
     *
     * @param sourceClass 源类
     * @param targetClass 目标类
     * @return net.sf.cglib.beans.BeanCopier 实例
     */
    public BeanCopier createBeanCopier(Class<?> sourceClass, Class<?> targetClass, boolean useConverter) {
        StringBuilder sb = new StringBuilder(sourceClass.getName()).append("To").append(targetClass.getName()).append(useConverter);
        final String key = sb.toString();
        BeanCopier beanCopier = copierCache.get(key);
        if (beanCopier == null) {
            beanCopier = BeanCopier.create(sourceClass, targetClass, false);
            copierCache.put(key, beanCopier);
        }
        return beanCopier;
    }

    /**
     * 依据属性名相等，将JavaBean fromObject的属性复制到目标JavaBean toObject。性能超好，4W nanos。
     *
     * @param fromObject 源JavaBean
     * @param toObject   目标JavaBean
     */
    public void beanCopy(Object fromObject, Object toObject) {
        BeanCopier beanCopier = createBeanCopier(fromObject.getClass(), toObject.getClass());
        beanCopier.copy(fromObject, toObject, null);
    }

    /**
     * 依据属性名相等，将JavaBean fromObject的属性复制到目标JavaBean toObject。性能超好，4W nanos。
     *
     * @param fromObject  源JavaBean
     * @param toObjectClz 目标JavaBean的clazz
     */
    public <T> T beanCopy(Object fromObject, Class<T> toObjectClz) throws IllegalAccessException, InstantiationException {
        T toObject = toObjectClz.newInstance();
        BeanCopier beanCopier = createBeanCopier(fromObject.getClass(), toObject.getClass());
        beanCopier.copy(fromObject, toObject, null);
        return toObject;
    }

    /**
     * 依据属性名相等，将JavaBean fromObject的属性复制到目标JavaBean toObject。性能超好，4W nanos。
     *
     * @param fromObject 源JavaBean
     * @param toObject   目标JavaBean
     */
    public void beanCopy(Object fromObject, Object toObject, Converter converter) {
        BeanCopier beanCopier = createBeanCopier(fromObject.getClass(), toObject.getClass());
        beanCopier.copy(fromObject, toObject, converter);
    }

    /**
     * 依据属性名相等，将JavaBean fromObject的属性复制到目标JavaBean toObject。性能超好，4W nanos。
     *
     * @param fromObject  源JavaBean
     * @param toObjectClz 目标JavaBean的clazz
     */
    public <T> T beanCopy(Object fromObject, Class<T> toObjectClz, Converter converter) throws IllegalAccessException, InstantiationException {
        T toObject = toObjectClz.newInstance();
        BeanCopier beanCopier = createBeanCopier(fromObject.getClass(), toObject.getClass());
        beanCopier.copy(fromObject, toObject, converter);
        return toObject;
    }

    /**
     * 将Bean转换为Map，使用MethodAccess实现。性能最好。
     *
     * @param fromBean 源JavaBean
     * @param toMap    目标Map
     */
    public void beanToMap(Object fromBean, Map<String, Object> toMap) {
        MethodAccess methodAccess = ASMUtils.get().createMethodAccess(fromBean.getClass());
        String[] methodNames = methodAccess.getMethodNames();
        for (String methodName : methodNames) {
            if (methodName.startsWith("get")) {
                Object value = methodAccess.invoke(fromBean, methodName, (Object[]) null);
                if (value != null) {
                    toMap.put(StringUtils.uncapitalize(methodName.substring(3)), value);
                }
            }
        }
    }

    /**
     * 将Bean转换为Map，Map key为getter方法名，使用MethodAccess实现。性能最好。
     *
     * @param fromBean 源JavaBean
     * @param toMap    目标Map
     */
    public void beanToMapPrefix(Object fromBean, Map<String, Object> toMap) {
        MethodAccess methodAccess = ASMUtils.get().createMethodAccess(fromBean.getClass());
        String[] methodNames = methodAccess.getMethodNames();
        for (String methodName : methodNames) {
            if (methodName.startsWith("get")) {
                Object value = methodAccess.invoke(fromBean, methodName, (Object[]) null);
                if (value != null) {
                    toMap.put(methodName, value);
                }
            }
        }
    }

    /**
     * 将Bean转换为Map，使用MethodAccess实现，key为属性名。性能最好。
     *
     * @param fromBean 源JavaBean
     */
    public Map<String, Object> beanToMaps(Object fromBean) {
        MethodAccess methodAccess = ASMUtils.get().createMethodAccess(fromBean.getClass());
        String[] methodNames = methodAccess.getMethodNames();
        Map<String, Object> toMap = new HashMap<String, Object>();
        for (String methodName : methodNames) {
            if (methodName.startsWith("get")) {
                Object value = methodAccess.invoke(fromBean, methodName, (Object[]) null);
                if (value != null) {
                    toMap.put(StringUtils.uncapitalize(methodName.substring(3)), value);
                }
            }
        }
        return toMap;
    }

    /**
     * 将Bean转换为Map，map key使用getter方法名，同时将值存到params中。性能最好。
     *
     * @param fromBean 源JavaBean
     * @param params   目标Map，fromBean转换成的Map同时放到这个Map中
     * @return fromBean转化成的Map
     */
    public Map<String, Object> toMap(Object fromBean, Map<String, Object> params) {
        MethodAccess methodAccess = ASMUtils.get().createMethodAccess(fromBean.getClass());
        String[] methodNames = methodAccess.getMethodNames();
        Map<String, Object> toMap = new HashMap<String, Object>();
        for (String methodName : methodNames) {
            if (methodName.startsWith("get")) {
                Object value = methodAccess.invoke(fromBean, methodName, (Object[]) null);
                if (value != null) {
                    toMap.put(methodName, value);
                    params.put(methodName, value);
                }
            }
        }
        return toMap;
    }


    /**
     * 将Map转换为JavaBean，使用MethodAccess实现。
     *
     * @param toBean  目标JavaBean
     * @param fromMap 源Map
     */
    public void mapToBean(Object toBean, Map<String, Object> fromMap) {
        MethodAccess methodAccess = ASMUtils.get().createMethodAccess(toBean.getClass());
        for (Map.Entry<String, Object> entry : fromMap.entrySet()) {
            String methodName = "set" + StringUtils.capitalize(entry.getKey());
            methodAccess.invoke(toBean, methodName, entry.getValue());
        }
    }

//    @SuppressWarnings("unused")
//	public static void main(String[] a) {
//    	ConstructorAccess<EmpUser> constructorAccess = BeanCopyUtils.getInstance().createConstructorAccess(EmpUser.class);
//    	System.out.println(constructorAccess);
//    	MethodAccess access = BeanCopyUtils.getInstance().createMethodAccess(EmpUser.class);
//    	System.out.println(access);
//
//    	EmpUser target = new EmpUser();
//    	target.setUsername("asdf");
////    	try {
////			target.getClass().getDeclaredField("username").setAccessible(true);
////		} catch (SecurityException e) {
////			e.printStackTrace();
////		} catch (NoSuchFieldException e) {
////			e.printStackTrace();
////		}
//    	//String usern = (String)BeanCopierFactory.getInstance().getFieldByIndex(target, "username");
//    	String usern2 = (String)BeanCopyUtils.getInstance().getField(target, "username");
//    	long d = System.currentTimeMillis();
//    	for (int i = 0; i < 10000000; i++) {
//    		//String username = (String)BeanCopierFactory.getInstance().invokeMethod(target, "getUsername", (Object[])null);
//    	}
//    	System.out.println(System.currentTimeMillis() - d);
//    	long d2 = System.currentTimeMillis();
//    	for (int i = 0; i < 10000000; i++) {
//    		//String username2 = (String)BeanCopierFactory.getInstance().invokeMethodByIndex(target, "getUsername", (Object[])null);
//    	}
//
//    	System.out.println(System.currentTimeMillis() - d2);
//    }

    /**
     * 将对象转化为Map，JSONObject就是个Map，它实现了Map接口. 使用fastjson实现的。含null值。<br>
     * 性能稍差，建议使用{@link #beanToMap(Object, Map)}
     * @param object 要被转化的对象
     * @return 对象Map，key是属性名，value是属性值
     * @author yinlei
     * @date 2013年10月18日 下午11:19:50
     */
//	public Map<String, Object> toMap(Object object) {
//		JSONObject jsonObject = (JSONObject) JSON.toJSON(object);
//		return jsonObject;
//	}

    /**
     * 将对象转化位Map，使用fastjson实现，去掉null值了。 性能稍差，建议使用{@link #beanToMap(Object, Map)}
     * @param object 要被转化的对象
     * @return 对象Map，key是属性名，value是属性值
     * @author yinlei
     * @date 2013年10月18日 下午11:20:51
     */
//	public Map<String, Object> toMaps(Object object) {
//		Map<String, Object> map = new HashMap<String, Object>();
//		JSONObject jsonObject = (JSONObject) JSON.toJSON(object);
//		for (Entry<String, Object> entry : jsonObject.entrySet()) {
//			if (entry.getValue() != null) {
//				map.put(entry.getKey(), entry.getValue());
//			}
//		}
//		return map;
//	}


    /**
     * 获得对象object的CGLIB BeanMap
     *
     * @param object 要被转换的Bean
     * @return BeanMap
     * @author yinlei
     * @date 2013年10月18日 下午11:29:07
     */
    public BeanMap getBeanMap(Object object) {
        BeanMap beanMap = beanMapCache.get(object.getClass().getName());
        if (beanMap == null) {
            beanMap = BeanMap.create(object);
            beanMapCache.put(object.getClass().getName(), beanMap);
        }
        return beanMap;
    }

    /**
     * 使用CGLIB BeanMap将Bean转换成Map，性能次之。{@link #beanToMap(Object, Map)}
     *
     * @param object 要被转换的Bean
     * @return 对象Map，key是属性名，value是属性值
     * @author yinlei
     * @date 2013年10月18日 下午11:30:28
     */
    public Map<String, Object> beanToMap(Object object) {
        BeanMap beanMap = getBeanMap(object);
        beanMap.setBean(object);

        @SuppressWarnings("unchecked") Map<String, Object> toMap = beanMap;
        Map<String, Object> returnMap = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : toMap.entrySet()) {
            if (entry.getValue() != null) {
                returnMap.put(entry.getKey(), entry.getValue());
            }
        }
        return returnMap;
    }
}

package org.common.reflector;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.common.reflector.data.ClassAnnotated;
import org.common.reflector.data.ClassAnnotation;
import org.common.reflector.data.CustomAnnotationForTest;
import org.common.reflector.data.CustomMethodAnnotation;
import org.common.reflector.data.CustomTestClassForType;
import org.common.reflector.data.CustomTestInvokeClass;
import org.common.reflector.data.MethodAnnotatedClass;
import org.common.reflector.data.SimpleAnnotatedEntry;
import org.common.reflector.data.SimpleEntryClass;
import org.junit.jupiter.api.Test;
import org.reflector.ReflectionUtils;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReflectionUtilsTest {
    @Test
    public void getAllClassNamesTest() {
        Object obj = new CustomTestInvokeClass("SimpleClassSimpleValue");
        assertAll("classNames",
                  () ->  assertEquals("CustomTestInvokeClass", ReflectionUtils.getClassSimpleName(obj)),
                  () ->  assertEquals("org.common.reflector.data.CustomTestInvokeClass", ReflectionUtils.getClassFullName(obj)),
                  () ->  assertEquals("org.common.reflector.data.CustomTestInvokeClass", ReflectionUtils.getClassCanonicalName(obj))
        );
    }

    @Test
    public void getPackageNameTest() {
        Object obj = new CustomTestInvokeClass("SimpleClassSimpleValue");
        assertEquals("org.common.reflector.data", ReflectionUtils.getPackage(obj));
    }

    @Test
    public void getSuperClassNameTest() {
        CustomTestInvokeClass obj = new CustomTestInvokeClass("SimpleClassSimpleValue");
        assertEquals("java.lang.Object", ReflectionUtils.getSuperClassName(obj));
    }

    @Test
    public void getSuperClassTest() {
        CustomTestInvokeClass obj = new CustomTestInvokeClass("SimpleClassSimpleValue");
        assertEquals(Object.class, ReflectionUtils.getSuperClass(obj));
    }

    @Test
    public void getAllClassNamesByClassTest() {
        assertAll("classNames",
                  () ->  assertEquals("CustomTestInvokeClass", ReflectionUtils.getClassSimpleNameByClass(CustomTestInvokeClass.class)),
                  () ->  assertEquals("org.common.reflector.data.CustomTestInvokeClass", ReflectionUtils.getClassFullNameByClass(CustomTestInvokeClass.class)),
                  () ->  assertEquals("org.common.reflector.data.CustomTestInvokeClass", ReflectionUtils.getClassCanonicalNameByClass(CustomTestInvokeClass.class))
        );
    }

    @Test
    public void getPackageNameByClassTest() {
        assertEquals("org.common.reflector.data", ReflectionUtils.getPackageByClass(CustomTestInvokeClass.class));
    }

    @Test
    public void getSuperClassNameByClassTest() {
        assertEquals("java.lang.Object", ReflectionUtils.getSuperClassNameByClass(CustomTestInvokeClass.class));
    }

    @Test
    public void getSuperClassFoClassTest() {
        assertEquals(Object.class, ReflectionUtils.getSuperClass(CustomTestInvokeClass.class));
    }

    @Test
    void getAllPrivateFieldsTest() {

        List<Field> fields = ReflectionUtils.getAllPrivateFields(CustomTestClassForType.class);

        assertAll("privateFields",
                () -> assertEquals(fields.get(0).getName(), "stringField"),
                () -> assertEquals(fields.get(1).getName(), "objectField"),
                () -> assertEquals(fields.get(2).getName(), "floatField"),
                () -> assertEquals(fields.size(), 3)
        );
    }

    @Test
    void invokeClassMethodTest() {
        String classFullNameWithPackage = "org.common.reflector.data.CustomTestInvokeClass";

        String classValue = "SimpleValue";
        String setMethodName = "setValue";
        String getMethodName = "getValue";

        CustomTestInvokeClass instance = (CustomTestInvokeClass) ReflectionUtils.invokeInstance(classFullNameWithPackage);
        Object ret1 = ReflectionUtils.invokeMethod(instance, setMethodName,
                new Class[]{String.class}, new String[]{classValue});
        String ret2 = (String) ReflectionUtils.invokeMethod(instance, getMethodName, null, null);

        assertAll("invokedMethodValues",
                () -> assertEquals(ret1, null),
                () -> assertEquals(ret2, classValue)
        );
    }

    @Test
    void invokeClassInstanceTest() {
        String classFullNameWithPackage = "org.common.reflector.data.CustomTestInvokeClass";
        CustomTestInvokeClass instance = (CustomTestInvokeClass) ReflectionUtils.invokeInstance(classFullNameWithPackage);
        assertAll("classInstance",
                () -> assertNotEquals(instance, null)
        );

    }

    @Test
    void readSingleField() {
        CustomTestClassForType customClass = new CustomTestClassForType();

        Object oneConstant = ReflectionUtils.readField(customClass, "oneConstant");
        System.out.println(oneConstant);

        assertAll("readedSingleField",
                () -> assertEquals(oneConstant, 1),
                () -> assertEquals(oneConstant.getClass(), Integer.class)
        );
    }

    @Test
    void invokeSingleClassMethodTest() {
        String classFullNameWithPackage = "org.common.reflector.data.CustomTestInvokeClass";

        String classValue = "SimpleValue";
        String setMethodName = "setValue";
        String getMethodName = "getValue";

        CustomTestInvokeClass instance = (CustomTestInvokeClass) ReflectionUtils.invokeInstance(classFullNameWithPackage);
        Object ret1 = ReflectionUtils.invokeSingleMethod(instance, setMethodName, String.class, classValue);
        String ret2 = (String) ReflectionUtils.invokeMethod(instance, getMethodName, null, null);

        assertAll("singleClassMethodValue",
                () -> assertEquals(ret1, null),
                () -> assertEquals(ret2, classValue)
        );
    }

    @Test
    void invokeClassInstanceWithParametersTest() {
        Object[] obj = {"SomeValue"};

        String classFullNameWithPackage = "org.common.reflector.data.CustomTestInvokeClass";
        CustomTestInvokeClass instance = (CustomTestInvokeClass) ReflectionUtils.invokeInstance(
                classFullNameWithPackage, obj);
        assertAll("classMultipleParametersInstance",
                () -> assertNotEquals(instance, null),
                () -> assertEquals(instance.getValue(), obj[0])
        );
    }

    @Test
    void getAllAnnotatedFieldsTest() {
        List<Field> fields = ReflectionUtils.getAllAnnotatedFields(SimpleAnnotatedEntry.class, CustomAnnotationForTest.class);
        assertAll("classMultipleParametersInstance",
                () -> assertEquals(fields.size(), 2),
                () -> assertEquals(fields.get(0).getName(), "key"),
                () -> assertEquals(fields.get(1).getName(), "value")
        );
    }

    @Test
    void clearUnselectedFieldsTest() {
        SimpleAnnotatedEntry entry = new SimpleAnnotatedEntry();
        entry.setKey("entryKey");
        entry.setValue("entryValue");
        entry.setInfo("entryInfo");

        List<String> valuesList = new ArrayList<>();
        valuesList.add("key");
        valuesList.add("value");

        ReflectionUtils.clearUnselectedFields(entry, valuesList);

        assertAll("classMultipleParametersInstance",
                () -> assertEquals(entry.getInfo(), null),
                () -> assertNotEquals(entry.getKey(), null),
                () -> assertNotEquals(entry.getValue(), null)
        );
    }

    @Test
    public void getAllPublicMethodsTest() {
        List<Method> allPublicProtectedMethods = ReflectionUtils.getAllPublicProtectedMethods(SimpleAnnotatedEntry.class);
        assertAll("publicProtectedMethods",
                () -> assertEquals(allPublicProtectedMethods.size(), 17)
        );
    }

    @Test
    public void getAllPrivateMethodsTest() {
        List<Method> allPublicProtectedMethods = ReflectionUtils.getAllPrivateMethods(SimpleAnnotatedEntry.class);
        assertEquals(allPublicProtectedMethods.get(0).getName(), "doSomething");
    }

    @Test
    public void getAllFieldsMap() {
        Map<String, Field> fields = ReflectionUtils.getAllFieldsMap(CustomTestClassForType.class);

        assertAll("allFields",
                  () -> assertEquals(fields.get("stringField").getName(), "stringField"),
                  () -> assertEquals(fields.get("objectField").getName(), "objectField"),
                  () -> assertEquals(fields.get("floatField").getName(), "floatField"),
                  () -> assertEquals(fields.get("notPrivateField").getName(), "notPrivateField"),
                  () -> assertEquals(fields.get("oneConstant").getName(), "oneConstant"),
                  () -> assertEquals(fields.size(), 5)
        );
    }

    @Test
    public void getAllPrivateFieldsMap() {
        Map<String, Field> fields = ReflectionUtils.getAllPrivateFieldsMap(CustomTestClassForType.class);

        assertAll("privateFields",
                  () -> assertEquals(fields.get("stringField").getName(), "stringField"),
                  () -> assertEquals(fields.get("objectField").getName(), "objectField"),
                  () -> assertEquals(fields.get("floatField").getName(), "floatField"),
                  () -> assertEquals(fields.size(), 3)
        );
    }

    @Test
    public void getConstructors() {
        Constructor<?>[] constructors = ReflectionUtils.getConstructors(SimpleEntryClass.class);
        assertEquals(constructors.length, 3);
    }

    @Test
    public void getDeclaredConstructors() {
        Constructor<?>[] constructors = ReflectionUtils.getDeclaredConstructors(SimpleEntryClass.class);
        assertEquals(constructors.length, 4);
    }

    @Test
    public void copyObjectTest() {
        SimpleEntryClass simpleEntryClass = new SimpleEntryClass("K", "V");
        SimpleEntryClass simpleEntryClassCopy = (SimpleEntryClass) ReflectionUtils.copy(simpleEntryClass);
        assertEquals(simpleEntryClass, simpleEntryClassCopy);
    }

    @Test
    public void findAllClassesByPackageTest() throws IOException, URISyntaxException, ClassNotFoundException {
        List<Class<?>> classesByPackage = ReflectionUtils.getClassesByPackage("org.common.reflector");
        assertTrue(classesByPackage.size() >= 7);
    }

    @Test
    public void methodAnnotationTest() {
        List<Method> allPublicProtectedMethods = ReflectionUtils.getAllPublicProtectedMethods(MethodAnnotatedClass.class);
        Method[] methods = new Method[allPublicProtectedMethods.size()];
        for (int i = 0; i < allPublicProtectedMethods.size(); i++) {
            methods[i] = allPublicProtectedMethods.get(i);
        }
        Map<Method, Annotation[]> methodDeclaredAnnotations = ReflectionUtils.getMethodDeclaredAnnotations(methods);
        String expectedMethodName = "annotatedMethod";
        Annotation actualAnnotation = null;
        for (Map.Entry<Method, Annotation[]> methodEntry : methodDeclaredAnnotations.entrySet()) {
            if (methodEntry.getKey().getName().equals(expectedMethodName)) {
                actualAnnotation = methodEntry.getValue()[0];
                break;
            }
        }
        assertEquals(actualAnnotation.annotationType(), CustomMethodAnnotation.class);
    }

    @Test
    public void doesHasAnnotations() {
        List<Method> allPublicProtectedMethods = ReflectionUtils.getAllPublicMethods(MethodAnnotatedClass.class);
        List<String> expected = new ArrayList<>(Arrays.asList("getClass",
                                                              "annotatedMethod",
                                                              "wait",
                                                              "hashCode",
                                                              "equals",
                                                              "notifyAll",
                                                              "toString",
                                                              "notify"));
        List<String> actual = allPublicProtectedMethods.stream().map(Method::getName).collect(Collectors.toList());
        assertFalse(expected.size() == actual.size() && expected.containsAll(actual) && actual.containsAll(expected));
    }

    @Test
    public void getAnnotatedClassesTest() throws IOException, URISyntaxException, ClassNotFoundException {
        List<Class<?>> classes = ReflectionUtils.getAllAnnotatedClassesByPackage("org.common.reflector.data", ClassAnnotation.class);
        int expectedAnnotationClassesQuantity = 2;
        assertEquals(expectedAnnotationClassesQuantity, classes.size());
    }

    @Test
    public void getNotAnnotatedClassesTest() throws IOException, URISyntaxException, ClassNotFoundException {
        List<Class<?>> classes = ReflectionUtils.getAllAnnotatedClassesByPackage("org.common.reflector.data", CustomMethodAnnotation.class);
        int expectedAnnotationClassesQuantity = 0;
        assertEquals(expectedAnnotationClassesQuantity, classes.size());
    }
}

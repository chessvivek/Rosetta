package com.hubspot.rosetta;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.common.base.Optional;
import com.hubspot.rosetta.RosettaBinder.Callback;
import com.hubspot.rosetta.beans.InnerBean;
import com.hubspot.rosetta.beans.ListBean;
import com.hubspot.rosetta.beans.NestedBean;
import com.hubspot.rosetta.beans.RosettaCreatorConstructorBean;
import com.hubspot.rosetta.beans.RosettaCreatorMethodBean;
import com.hubspot.rosetta.beans.RosettaNamingBean;
import com.hubspot.rosetta.beans.ServiceLoaderBean;
import com.hubspot.rosetta.beans.StoredAsJsonBean;
import com.hubspot.rosetta.beans.StoredAsJsonTypeInfoBean.ConcreteStoredAsJsonTypeInfo;

public class RosettaBinderTest {

  @Test
  public void itBindsRosettaCreatorConstructorBeanCorrectly() {
    RosettaCreatorConstructorBean bean = new RosettaCreatorConstructorBean("value");

    assertThat(bind(bean)).isEqualTo(map("stringProperty", "value"));
    assertThat(bindWithPrefix("prefix", bean)).isEqualTo(map("prefix.stringProperty", "value"));
  }

  @Test
  public void itBindsRosettaCreatorMethodBeanCorrectly() {
    RosettaCreatorMethodBean bean = RosettaCreatorMethodBean.fromString("value");

    assertThat(bind(bean)).isEqualTo(map("stringProperty", "value"));
    assertThat(bindWithPrefix("prefix", bean)).isEqualTo(map("prefix.stringProperty", "value"));
  }

  @Test
  public void itBindsRosettaNamingBeanCorrectly() {
    RosettaNamingBean bean = new RosettaNamingBean();
    bean.setStringProperty("value");

    assertThat(bind(bean)).isEqualTo(map("string_property", "value"));
    assertThat(bindWithPrefix("prefix", bean)).isEqualTo(map("prefix.string_property", "value"));
  }

  @Test
  public void itBindsStoredAsJsonBeanCorrectly() throws Exception {
    InnerBean inner = new InnerBean();
    inner.setStringProperty("value");

    ConcreteStoredAsJsonTypeInfo concrete = new ConcreteStoredAsJsonTypeInfo();
    concrete.setGeneralValue("general");
    concrete.setConcreteValue("concrete");

    JsonNode innerJsonNode = Rosetta.getMapper().createObjectNode().set("stringProperty", TextNode.valueOf("value"));

    StoredAsJsonBean bean = new StoredAsJsonBean();
    bean.setAnnotatedField(inner);
    bean.setAnnotatedGetter(inner);
    bean.setAnnotatedSetter(inner);
    bean.setAnnotatedFieldWithDefault(inner);
    bean.setAnnotatedGetterWithDefault(inner);
    bean.setAnnotatedSetterWithDefault(inner);
    bean.setOptionalField(Optional.of(inner));
    bean.setOptionalGetter(Optional.of(inner));
    bean.setOptionalSetter(Optional.of(inner));
    bean.setBinaryField(inner);
    bean.setBinaryFieldWithDefault(inner);
    bean.setJsonNodeField(innerJsonNode);
    bean.setOptionalTypeInfoField(Optional.of(concrete));
    bean.setOptionalTypeInfoGetter(Optional.of(concrete));
    bean.setOptionalTypeInfoSetter(Optional.of(concrete));
    bean.setTypeInfoField(concrete);
    bean.setTypeInfoGetter(concrete);
    bean.setTypeInfoSetter(concrete);

    String json = "{\"stringProperty\":\"value\"}";
    String typedJson = "{\"generalValue\":\"general\",\"concreteValue\":\"concrete\",\"type\":\"concrete\"}";
    List<Byte> bytes = toList(json.getBytes(StandardCharsets.UTF_8));

    Map<String, Object> BeanMap = bind(bean);
    JSONAssert.assertEquals(BeanMap.get("annotatedField").toString(), json, false);
    JSONAssert.assertEquals(BeanMap.get("annotatedGetter").toString(), json, false);
    JSONAssert.assertEquals(BeanMap.get("annotatedSetter").toString(), json, false);
    JSONAssert.assertEquals(BeanMap.get("annotatedFieldWithDefault").toString(), json, false);
    JSONAssert.assertEquals(BeanMap.get("annotatedGetterWithDefault").toString(), json, false);
    JSONAssert.assertEquals(BeanMap.get("annotatedSetterWithDefault").toString(), json, false);
    JSONAssert.assertEquals(BeanMap.get("optionalField").toString(), json, false);
    JSONAssert.assertEquals(BeanMap.get("optionalGetter").toString(), json, false);
    JSONAssert.assertEquals(BeanMap.get("optionalSetter").toString(), json, false);
    assertThat(BeanMap.get("binaryField")).isEqualTo(bytes);
    assertThat(BeanMap.get("binaryFieldWithDefault")).isEqualTo(bytes);
    JSONAssert.assertEquals(BeanMap.get("jsonNodeField").toString(), json, false);
    JSONAssert.assertEquals(BeanMap.get("optionalTypeInfoField").toString(), typedJson, false);
    JSONAssert.assertEquals(BeanMap.get("optionalTypeInfoGetter").toString(), typedJson, false);
    JSONAssert.assertEquals(BeanMap.get("optionalTypeInfoSetter").toString(), typedJson, false);
    JSONAssert.assertEquals(BeanMap.get("typeInfoField").toString(), typedJson, false);
    JSONAssert.assertEquals(BeanMap.get("typeInfoGetter").toString(), typedJson, false);
    JSONAssert.assertEquals(BeanMap.get("typeInfoSetter").toString(), typedJson, false);
    assertThat(BeanMap.keySet().size()).isEqualTo(18);

    BeanMap = bindWithPrefix("prefix", bean);
    JSONAssert.assertEquals(BeanMap.get("prefix.annotatedField").toString(), json, false);
    JSONAssert.assertEquals(BeanMap.get("prefix.annotatedGetter").toString(), json, false);
    JSONAssert.assertEquals(BeanMap.get("prefix.annotatedSetter").toString(), json, false);
    JSONAssert.assertEquals(BeanMap.get("prefix.annotatedFieldWithDefault").toString(), json, false);
    JSONAssert.assertEquals(BeanMap.get("prefix.annotatedGetterWithDefault").toString(), json, false);
    JSONAssert.assertEquals(BeanMap.get("prefix.annotatedSetterWithDefault").toString(), json, false);
    JSONAssert.assertEquals(BeanMap.get("prefix.optionalField").toString(), json, false);
    JSONAssert.assertEquals(BeanMap.get("prefix.optionalGetter").toString(), json, false);
    JSONAssert.assertEquals(BeanMap.get("prefix.optionalSetter").toString(), json, false);
    assertThat(BeanMap.get("prefix.binaryField")).isEqualTo(bytes);
    assertThat(BeanMap.get("prefix.binaryFieldWithDefault")).isEqualTo(bytes);
    JSONAssert.assertEquals(BeanMap.get("prefix.jsonNodeField").toString(), json, false);
    JSONAssert.assertEquals(BeanMap.get("prefix.optionalTypeInfoField").toString(), typedJson, false);
    JSONAssert.assertEquals(BeanMap.get("prefix.optionalTypeInfoGetter").toString(), typedJson, false);
    JSONAssert.assertEquals(BeanMap.get("prefix.optionalTypeInfoSetter").toString(), typedJson, false);
    JSONAssert.assertEquals(BeanMap.get("prefix.typeInfoField").toString(), typedJson, false);
    JSONAssert.assertEquals(BeanMap.get("prefix.typeInfoGetter").toString(), typedJson, false);
    JSONAssert.assertEquals(BeanMap.get("prefix.typeInfoSetter").toString(), typedJson, false);
    assertThat(BeanMap.keySet().size()).isEqualTo(18);
  }

  @Test
  public void itBindsNullStoredAsJsonBeanCorrectly() {
    StoredAsJsonBean bean = new StoredAsJsonBean();

    String json = "{\"stringProperty\":\"value\"}";
    List<Byte> bytes = toList(json.getBytes(StandardCharsets.UTF_8));

    assertThat(bind(bean)).isEqualTo(map(
            "annotatedField", null,
            "annotatedGetter", null,
            "annotatedSetter", null,
            "annotatedFieldWithDefault", json,
            "annotatedGetterWithDefault", json,
            "annotatedSetterWithDefault", json,
            "optionalField", null,
            "optionalGetter", null,
            "optionalSetter", null,
            "binaryField", null,
            "binaryFieldWithDefault", bytes,
            "jsonNodeField", null,
            "optionalTypeInfoField", null,
            "optionalTypeInfoGetter", null,
            "optionalTypeInfoSetter", null,
            "typeInfoField", null,
            "typeInfoGetter", null,
            "typeInfoSetter", null
    ));
    assertThat(bindWithPrefix("prefix", bean)).isEqualTo(map(
            "prefix.annotatedField", null,
            "prefix.annotatedGetter", null,
            "prefix.annotatedSetter", null,
            "prefix.annotatedFieldWithDefault", json,
            "prefix.annotatedGetterWithDefault", json,
            "prefix.annotatedSetterWithDefault", json,
            "prefix.optionalField", null,
            "prefix.optionalGetter", null,
            "prefix.optionalSetter", null,
            "prefix.binaryField", null,
            "prefix.binaryFieldWithDefault", bytes,
            "prefix.jsonNodeField", null,
            "prefix.optionalTypeInfoField", null,
            "prefix.optionalTypeInfoGetter", null,
            "prefix.optionalTypeInfoSetter", null,
            "prefix.typeInfoField", null,
            "prefix.typeInfoGetter", null,
            "prefix.typeInfoSetter", null
    ));
  }

  @Test
  public void itBindsNestedBeanCorrectly() {
    InnerBean inner = new InnerBean();
    inner.setStringProperty("value");

    NestedBean bean = new NestedBean();
    bean.setInner(inner);

    assertThat(bind(bean)).isEqualTo(map("inner.stringProperty", "value"));
    assertThat(bindWithPrefix("prefix", bean)).isEqualTo(map("prefix.inner.stringProperty", "value"));
  }

  @Test
  public void itBindsListFieldsCorrectly() {
    ListBean bean = new ListBean();
    List<Integer> values = Arrays.asList(1, 2, 3);
    bean.setValues(values);

    assertThat(bind(bean)).isEqualTo(map("values", values));
    assertThat(bindWithPrefix("prefix", bean)).isEqualTo(map("prefix.values", values));
  }

  @Test
  public void itUsesServiceLoaderToDiscoverModules() {
    ServiceLoaderBean bean = new ServiceLoaderBean();
    bean.setId(50);
    bean.setName("test");

    assertThat(bind(bean)).isEqualTo(map("id_value", 50, "name_value", "test"));
    assertThat(bindWithPrefix("prefix", bean)).isEqualTo(map("prefix.id_value", 50, "prefix.name_value", "test"));
  }

  private static Map<String, Object> map(Object... strings) {
    Map<String, Object> map = new HashMap<String, Object>();
    for (int i = 0; i < strings.length; i += 2) {
      map.put((String) strings[i], strings[i + 1]);
    }

    return map;
  }

  private Map<String, Object> bind(Object o) {
    return bindWithPrefix("", o);
  }

  private Map<String, Object> bindWithPrefix(String prefix, Object o) {
    JsonNode node = Rosetta.getMapper().valueToTree(o);
    MockCallback callback = new MockCallback();
    RosettaBinder.INSTANCE.bind(prefix, node, callback);
    return callback.getBindings();
  }

  private static class MockCallback implements Callback {
    private final Map<String, Object> bindings = new HashMap<String, Object>();

    @Override
    public void bind(String key, Object value) {
      if (value instanceof byte[]) {
        value = toList((byte[]) value);
      }

      bindings.put(key, value);
    }

    public Map<String, Object> getBindings() {
      return bindings;
    }
  }

  private static List<Byte> toList(byte[] bytes) {
    List<Byte> byteList = new ArrayList<>();
    for (int i = 0; i < bytes.length; i++) {
      byteList.add(bytes[i]);
    }

    return byteList;
  }
}

package io.github.thenovaworks.samples.secretsmanager.spring;


import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.source.ConfigurationProperty;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
// ort org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;


class MapConfigurationPropertySourceTests {

    private Object getValue(ConfigurationPropertySource source, String name) {
        ConfigurationProperty property = source.getConfigurationProperty(ConfigurationPropertyName.of(name));
        return (property != null) ? property.getValue() : null;
    }

    @Test
    void createWhenMapIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> new MapConfigurationPropertySource(null))
                .withMessageContaining("Map must not be null");
    }

    @Test
    void createWhenMapHasEntriesShouldAdaptMap() {
        Map<Object, Object> map = new LinkedHashMap<>();
        map.put("foo.BAR", "spring");
        map.put(ConfigurationPropertyName.of("foo.baz"), "boot");
        MapConfigurationPropertySource source = new MapConfigurationPropertySource(map);
        assertThat(getValue(source, "foo.bar")).isEqualTo("spring");
        assertThat(getValue(source, "foo.baz")).isEqualTo("boot");
    }

    @Test
    void putAllWhenMapIsNullShouldThrowException() {
        MapConfigurationPropertySource source = new MapConfigurationPropertySource();
        assertThatIllegalArgumentException().isThrownBy(() -> source.putAll(null))
                .withMessageContaining("Map must not be null");
    }

    @Test
    void putAllShouldPutEntries() {
        Map<Object, Object> map = new LinkedHashMap<>();
        map.put("foo.BAR", "spring");
        map.put("foo.baz", "boot");
        MapConfigurationPropertySource source = new MapConfigurationPropertySource();
        source.putAll(map);
        assertThat(getValue(source, "foo.bar")).isEqualTo("spring");
        assertThat(getValue(source, "foo.baz")).isEqualTo("boot");
    }

    @Test
    void putShouldPutEntry() {
        MapConfigurationPropertySource source = new MapConfigurationPropertySource();
        source.put("foo.bar", "baz");
        assertThat(getValue(source, "foo.bar")).isEqualTo("baz");
    }

    @Test
    void getConfigurationPropertyShouldGetFromMemory() {
        MapConfigurationPropertySource source = new MapConfigurationPropertySource();
        source.put("foo.bar", "baz");
        assertThat(getValue(source, "foo.bar")).isEqualTo("baz");
        source.put("foo.bar", "big");
        assertThat(getValue(source, "foo.bar")).isEqualTo("big");
    }

    @Test
    void iteratorShouldGetFromMemory() {
        MapConfigurationPropertySource source = new MapConfigurationPropertySource();
        source.put("foo.BAR", "spring");
        source.put("foo.baz", "boot");
        assertThat(source.iterator()).toIterable()
                .containsExactly(ConfigurationPropertyName.of("foo.bar"), ConfigurationPropertyName.of("foo.baz"));
    }

    @Test
    void streamShouldGetFromMemory() {
        MapConfigurationPropertySource source = new MapConfigurationPropertySource();
        source.put("foo.BAR", "spring");
        source.put("foo.baz", "boot");
        assertThat(source.stream()).containsExactly(ConfigurationPropertyName.of("foo.bar"),
                ConfigurationPropertyName.of("foo.baz"));

    }


}

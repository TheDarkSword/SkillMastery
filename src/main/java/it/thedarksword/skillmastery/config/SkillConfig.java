package it.thedarksword.skillmastery.config;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.Property;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SkillConfig implements SettingsHolder {

    public static final Property<String> MARIA_HOST = newProperty("mysql.host", "localhost");
    public static final Property<String> MARIA_USERNAME = newProperty("mysql.username", "maria");
    public static final Property<String> MARIA_PASSWORD = newProperty("mysql.password", "password");
}
